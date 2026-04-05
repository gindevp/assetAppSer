package com.gindevp.app.service;

import com.gindevp.app.domain.*;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.domain.enumeration.LossReportKind;
import com.gindevp.app.domain.enumeration.LossReportRequestStatus;
import com.gindevp.app.repository.*;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.security.SecurityUtils;
import com.gindevp.app.service.dto.*;
import com.gindevp.app.service.mapper.AssetItemMapper;
import com.gindevp.app.service.mapper.EmployeeMapper;
import com.gindevp.app.service.mapper.EquipmentMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LossReportRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(LossReportRequestService.class);

    private static final String ENTITY_NAME = "lossReportRequest";

    private final LossReportRequestRepository lossReportRequestRepository;

    private final EmployeeRepository employeeRepository;

    private final EquipmentRepository equipmentRepository;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final ConsumableStockRepository consumableStockRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final AppAuditLogService appAuditLogService;

    private final EmployeeMapper employeeMapper;

    private final EquipmentMapper equipmentMapper;

    private final AssetItemMapper assetItemMapper;

    public LossReportRequestService(
        LossReportRequestRepository lossReportRequestRepository,
        EmployeeRepository employeeRepository,
        EquipmentRepository equipmentRepository,
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        ConsumableAssignmentRepository consumableAssignmentRepository,
        ConsumableStockRepository consumableStockRepository,
        CurrentEmployeeService currentEmployeeService,
        AppAuditLogService appAuditLogService,
        EmployeeMapper employeeMapper,
        EquipmentMapper equipmentMapper,
        AssetItemMapper assetItemMapper
    ) {
        this.lossReportRequestRepository = lossReportRequestRepository;
        this.employeeRepository = employeeRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.consumableStockRepository = consumableStockRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.appAuditLogService = appAuditLogService;
        this.employeeMapper = employeeMapper;
        this.equipmentMapper = equipmentMapper;
        this.assetItemMapper = assetItemMapper;
    }

    /** Chỉ cổng NV (không phải QLTS/Admin/GĐ) tạo YC. */
    private void assertEmployeePortalCanCreate() {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new BadRequestAlertException(
                "YC báo mất do nhân viên tạo từ «Tài sản của tôi» — QLTS/Admin duyệt tại đây",
                ENTITY_NAME,
                "portalonly"
            );
        }
    }

    private boolean isDeptCoordinator() {
        return SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.DEPARTMENT_COORDINATOR);
    }

    private boolean employeesInSameDepartment(Long employeeId1, Long employeeId2) {
        Optional<Employee> emp1 = employeeRepository.findOneWithToOneRelationships(employeeId1);
        Optional<Employee> emp2 = employeeRepository.findOneWithToOneRelationships(employeeId2);
        return emp1
            .flatMap(e1 -> emp2.map(e2 -> {
                Long d1 = e1.getDepartment() != null ? e1.getDepartment().getId() : null;
                Long d2 = e2.getDepartment() != null ? e2.getDepartment().getId() : null;
                return d1 != null && d1.equals(d2);
            }))
            .orElse(false);
    }

    private boolean assignmentMatchesRequester(EquipmentAssignment a, Employee requester) {
        Long rid = requester.getId();
        if (a.getEmployee() != null) {
            if (a.getEmployee().getId().equals(rid)) return true;
            if (isDeptCoordinator()) {
                return employeesInSameDepartment(rid, a.getEmployee().getId());
            }
            return false;
        }
        if (a.getDepartment() != null && requester.getDepartment() != null && a.getDepartment().getId().equals(requester.getDepartment().getId())) {
            return true;
        }
        return (
            a.getLocation() != null &&
            requester.getLocation() != null &&
            a.getLocation().getId().equals(requester.getLocation().getId())
        );
    }

    private boolean consumableAssignmentMatchesRequester(ConsumableAssignment ca, Employee requester) {
        Long rid = requester.getId();
        if (ca.getEmployee() != null) {
            if (ca.getEmployee().getId().equals(rid)) return true;
            if (isDeptCoordinator()) {
                return employeesInSameDepartment(rid, ca.getEmployee().getId());
            }
            return false;
        }
        if (ca.getDepartment() != null && requester.getDepartment() != null && ca.getDepartment().getId().equals(requester.getDepartment().getId())) {
            return true;
        }
        return (
            ca.getLocation() != null &&
            requester.getLocation() != null &&
            ca.getLocation().getId().equals(requester.getLocation().getId())
        );
    }

    private int consumableHeld(ConsumableAssignment ca) {
        int q = ca.getQuantity() != null ? ca.getQuantity() : 0;
        int r = ca.getReturnedQuantity() != null ? ca.getReturnedQuantity() : 0;
        return Math.max(0, q - r);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public LossReportRequestDTO save(LossReportRequestDTO dto) {
        assertEmployeePortalCanCreate();
        Long eid = currentEmployeeService
            .currentEmployeeId()
            .orElseThrow(() -> new BadRequestAlertException("Tài khoản chưa liên kết nhân viên", ENTITY_NAME, "noemployee"));
        if (dto.getRequester() == null || dto.getRequester().getId() == null || !dto.getRequester().getId().equals(eid)) {
            throw new AccessDeniedException("Chỉ được tạo YC báo mất cho chính nhân viên đăng nhập");
        }
        if (dto.getStatus() != null && dto.getStatus() != LossReportRequestStatus.PENDING) {
            throw new BadRequestAlertException("Tạo YC với trạng thái chờ duyệt", ENTITY_NAME, "badcreatestatus");
        }
        Employee requester = employeeRepository
            .findOneWithToOneRelationships(eid)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy nhân viên", ENTITY_NAME, "noemployee"));

        String lossOccurredAt = trimToNull(dto.getLossOccurredAt());
        String lossLocation = trimToNull(dto.getLossLocation());
        String reasonText = trimToNull(dto.getReason());
        String lossDescription = trimToNull(dto.getLossDescription());
        if (lossOccurredAt == null) {
            throw new BadRequestAlertException("Nhập thời gian (xảy ra / phát hiện mất)", ENTITY_NAME, "nolosstime");
        }
        if (lossLocation == null) {
            throw new BadRequestAlertException("Nhập địa điểm", ENTITY_NAME, "nolossplace");
        }
        if (reasonText == null) {
            throw new BadRequestAlertException("Nhập lý do", ENTITY_NAME, "noreason");
        }
        if (lossDescription == null) {
            throw new BadRequestAlertException("Nhập mô tả mất", ENTITY_NAME, "nolossdesc");
        }

        LossReportRequest e = new LossReportRequest();
        e.setCode(dto.getCode());
        e.setRequestDate(dto.getRequestDate() != null ? dto.getRequestDate() : Instant.now());
        e.setStatus(LossReportRequestStatus.PENDING);
        e.setLossKind(dto.getLossKind());
        e.setLossOccurredAt(lossOccurredAt);
        e.setLossLocation(lossLocation);
        e.setReason(reasonText);
        e.setLossDescription(lossDescription);
        e.setRequester(requester);

        if (dto.getLossKind() == LossReportKind.EQUIPMENT) {
            if (dto.getEquipment() == null || dto.getEquipment().getId() == null) {
                throw new BadRequestAlertException("Chọn thiết bị", ENTITY_NAME, "noequipment");
            }
            Long eqId = dto.getEquipment().getId();
            if (lossReportRequestRepository.existsByEquipment_IdAndStatus(eqId, LossReportRequestStatus.PENDING)) {
                throw new BadRequestAlertException("Đã có YC báo mất chờ duyệt cho thiết bị này", ENTITY_NAME, "duplicatepending");
            }
            Equipment eq = equipmentRepository.findById(eqId).orElseThrow(() -> new BadRequestAlertException("Không tìm thấy thiết bị", ENTITY_NAME, "noequipment"));
            if (eq.getStatus() != EquipmentOperationalStatus.IN_USE) {
                throw new BadRequestAlertException("Chỉ báo mất thiết bị đang sử dụng", ENTITY_NAME, "badequipmentstatus");
            }
            EquipmentAssignment assign = equipmentAssignmentRepository
                .findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(eqId)
                .orElseThrow(() -> new BadRequestAlertException("Thiết bị không có bàn giao hiệu lực", ENTITY_NAME, "noassignment"));
            if (!assignmentMatchesRequester(assign, requester)) {
                throw new AccessDeniedException("Thiết bị không thuộc phạm vi «Tài sản của tôi»");
            }
            e.setEquipment(eq);
            e.setQuantity(1);
        } else {
            if (dto.getConsumableAssignment() == null || dto.getConsumableAssignment().getId() == null) {
                throw new BadRequestAlertException("Chọn dòng vật tư (bàn giao)", ENTITY_NAME, "noca");
            }
            if (dto.getQuantity() == null || dto.getQuantity() < 1) {
                throw new BadRequestAlertException("Nhập số lượng báo mất (≥ 1)", ENTITY_NAME, "badqty");
            }
            Long caId = dto.getConsumableAssignment().getId();
            if (lossReportRequestRepository.existsByConsumableAssignment_IdAndStatus(caId, LossReportRequestStatus.PENDING)) {
                throw new BadRequestAlertException("Đã có YC báo mất chờ duyệt cho dòng vật tư này", ENTITY_NAME, "duplicatepending");
            }
            ConsumableAssignment ca = consumableAssignmentRepository
                .findById(caId)
                .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy bàn giao vật tư", ENTITY_NAME, "noca"));
            if (!consumableAssignmentMatchesRequester(ca, requester)) {
                throw new AccessDeniedException("Vật tư không thuộc phạm vi «Tài sản của tôi»");
            }
            int held = consumableHeld(ca);
            if (dto.getQuantity() > held) {
                throw new BadRequestAlertException("Số lượng vượt SL còn giữ (" + held + ")", ENTITY_NAME, "qtyexceed");
            }
            e.setConsumableAssignment(ca);
            e.setQuantity(dto.getQuantity());
        }

        e = lossReportRequestRepository.save(e);
        LOG.debug("Created loss report request {}", e.getId());
        return toDto(e);
    }

    public Optional<LossReportRequestDTO> partialUpdate(LossReportRequestDTO dto) {
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin/GĐ xác nhận hoặc từ chối YC báo mất");
        }
        if (dto.getId() == null || dto.getStatus() == null) {
            throw new BadRequestAlertException("Cần id và trạng thái", ENTITY_NAME, "badpatch");
        }
        return lossReportRequestRepository
            .findById(dto.getId())
            .map(existing -> {
                if (existing.getStatus() != LossReportRequestStatus.PENDING) {
                    throw new BadRequestAlertException("Chỉ xử lý khi đang chờ duyệt", ENTITY_NAME, "notpending");
                }
                if (dto.getStatus() != LossReportRequestStatus.APPROVED && dto.getStatus() != LossReportRequestStatus.REJECTED) {
                    throw new BadRequestAlertException("Chỉ duyệt hoặc từ chối", ENTITY_NAME, "badstatus");
                }
                existing.setStatus(dto.getStatus());
                LossReportRequest saved = lossReportRequestRepository.save(existing);
                if (saved.getStatus() == LossReportRequestStatus.APPROVED) {
                    LossReportRequest full = lossReportRequestRepository.findOneWithRelationships(saved.getId()).orElse(saved);
                    applyApprovedLoss(full);
                }
                String code = saved.getCode() != null ? saved.getCode() : String.valueOf(saved.getId());
                appAuditLogService.recordBusiness(
                    "LOSS_REPORT_" + saved.getStatus().name(),
                    "requestId=" + saved.getId() + " code=" + code
                );
                return toDto(lossReportRequestRepository.findOneWithRelationships(saved.getId()).orElse(saved));
            });
    }

    private void applyApprovedLoss(LossReportRequest lr) {
        if (lr.getLossKind() == LossReportKind.EQUIPMENT && lr.getEquipment() != null) {
            Long eqId = lr.getEquipment().getId();
            Equipment eq = equipmentRepository.findById(eqId).orElseThrow();
            eq.setStatus(EquipmentOperationalStatus.LOST);
            equipmentRepository.save(eq);
            equipmentAssignmentRepository
                .findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(eqId)
                .ifPresent(a -> {
                    a.setReturnedDate(LocalDate.now());
                    equipmentAssignmentRepository.save(a);
                });
        } else if (lr.getLossKind() == LossReportKind.CONSUMABLE && lr.getConsumableAssignment() != null && lr.getQuantity() != null) {
            ConsumableAssignment ca = consumableAssignmentRepository
                .findById(lr.getConsumableAssignment().getId())
                .orElseThrow();
            int qty = lr.getQuantity();
            int already = ca.getReturnedQuantity() != null ? ca.getReturnedQuantity() : 0;
            int can = ca.getQuantity() - already;
            if (qty > can || qty < 1) {
                throw new BadRequestAlertException("Số lượng báo mất không hợp lệ khi áp dụng", ENTITY_NAME, "badapplyqty");
            }
            ca.setReturnedQuantity(already + qty);
            consumableAssignmentRepository.save(ca);
            Long itemId = ca.getAssetItem().getId();
            consumableStockRepository.findFirstByAssetItem_Id(itemId).ifPresent(stock -> {
                int issued = stock.getQuantityIssued() != null ? stock.getQuantityIssued() : 0;
                stock.setQuantityIssued(Math.max(0, issued - qty));
                consumableStockRepository.save(stock);
            });
        }
    }

    @Transactional(readOnly = true)
    public Page<LossReportRequestDTO> findAll(Pageable pageable) {
        Page<LossReportRequest> page;
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            page = lossReportRequestRepository.findAll(pageable);
        } else {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Chưa liên kết nhân viên"));
            page = lossReportRequestRepository.findByRequester_Id(eid, pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<LossReportRequestDTO> findOne(Long id) {
        return lossReportRequestRepository
            .findOneWithRelationships(id)
            .map(this::toDto)
            .filter(this::visibleToCurrentUser);
    }

    private boolean visibleToCurrentUser(LossReportRequestDTO dto) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return true;
        }
        Long eid = currentEmployeeService.currentEmployeeId().orElse(null);
        return dto.getRequester() != null && dto.getRequester().getId() != null && dto.getRequester().getId().equals(eid);
    }

    private LossReportRequestDTO toDto(LossReportRequest e) {
        LossReportRequestDTO d = new LossReportRequestDTO();
        d.setId(e.getId());
        d.setCode(e.getCode());
        d.setRequestDate(e.getRequestDate());
        d.setStatus(e.getStatus());
        d.setLossKind(e.getLossKind());
        d.setQuantity(e.getQuantity());
        d.setLossOccurredAt(e.getLossOccurredAt());
        d.setLossLocation(e.getLossLocation());
        d.setReason(e.getReason());
        d.setLossDescription(e.getLossDescription());
        if (e.getRequester() != null) {
            d.setRequester(employeeMapper.toDto(e.getRequester()));
        }
        if (e.getEquipment() != null) {
            d.setEquipment(equipmentMapper.toDto(e.getEquipment()));
        }
        if (e.getConsumableAssignment() != null) {
            ConsumableAssignmentRefDTO ref = new ConsumableAssignmentRefDTO();
            ref.setId(e.getConsumableAssignment().getId());
            if (e.getConsumableAssignment().getAssetItem() != null) {
                ref.setAssetItem(assetItemMapper.toDto(e.getConsumableAssignment().getAssetItem()));
            }
            d.setConsumableAssignment(ref);
        }
        return d;
    }
}
