package com.gindevp.app.service;

import com.gindevp.app.domain.*;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.domain.enumeration.ReturnDisposition;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import com.gindevp.app.repository.*;
import com.gindevp.app.service.dto.ReturnRequestDTO;
import com.gindevp.app.service.mapper.ReturnRequestMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.ReturnRequest}.
 */
@Service
@Transactional
public class ReturnRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestService.class);

    private static final String ENTITY_NAME = "returnRequest";

    private final ReturnRequestRepository returnRequestRepository;

    private final ReturnRequestMapper returnRequestMapper;

    private final ReturnRequestLineRepository returnRequestLineRepository;

    private final EquipmentRepository equipmentRepository;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final ConsumableStockRepository consumableStockRepository;

    private final EmployeeRepository employeeRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final AppAuditLogService appAuditLogService;

    public ReturnRequestService(
        ReturnRequestRepository returnRequestRepository,
        ReturnRequestMapper returnRequestMapper,
        ReturnRequestLineRepository returnRequestLineRepository,
        EquipmentRepository equipmentRepository,
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        ConsumableAssignmentRepository consumableAssignmentRepository,
        ConsumableStockRepository consumableStockRepository,
        EmployeeRepository employeeRepository,
        CurrentEmployeeService currentEmployeeService,
        AppAuditLogService appAuditLogService
    ) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnRequestMapper = returnRequestMapper;
        this.returnRequestLineRepository = returnRequestLineRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.consumableStockRepository = consumableStockRepository;
        this.employeeRepository = employeeRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.appAuditLogService = appAuditLogService;
    }

    public ReturnRequestDTO save(ReturnRequestDTO returnRequestDTO) {
        LOG.debug("Request to save ReturnRequest : {}", returnRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() ->
                    new BadRequestAlertException("Tài khoản chưa liên kết nhân viên", ENTITY_NAME, "noemployee")
                );
            if (
                returnRequestDTO.getRequester() == null ||
                returnRequestDTO.getRequester().getId() == null ||
                !returnRequestDTO.getRequester().getId().equals(eid)
            ) {
                throw new AccessDeniedException("Chỉ được tạo yêu cầu thu hồi thay mặt nhân viên đã đăng nhập");
            }
        }
        if (returnRequestDTO.getStatus() == ReturnRequestStatus.COMPLETED) {
            throw new BadRequestAlertException(
                "Không tạo yêu cầu thu hồi ở trạng thái hoàn thành",
                ENTITY_NAME,
                "invalidcreatestatus"
            );
        }
        ReturnRequest returnRequest = returnRequestMapper.toEntity(returnRequestDTO);
        returnRequest = returnRequestRepository.save(returnRequest);
        return returnRequestMapper.toDto(returnRequest);
    }

    public ReturnRequestDTO update(ReturnRequestDTO returnRequestDTO) {
        LOG.debug("Request to update ReturnRequest : {}", returnRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật đầy đủ yêu cầu thu hồi");
        }
        Optional<ReturnRequest> before = returnRequestRepository.findById(returnRequestDTO.getId());
        ReturnRequestStatus oldStatus = before.map(ReturnRequest::getStatus).orElse(null);
        ReturnRequest returnRequest = returnRequestMapper.toEntity(returnRequestDTO);
        assertReturnCompletionAllowed(oldStatus, returnRequest.getStatus(), returnRequestDTO.getId());
        returnRequest = returnRequestRepository.save(returnRequest);
        if (returnRequest.getStatus() == ReturnRequestStatus.COMPLETED && oldStatus != ReturnRequestStatus.COMPLETED) {
            applyReturnCompletion(returnRequest.getId());
        }
        return returnRequestMapper.toDto(returnRequest);
    }

    public Optional<ReturnRequestDTO> partialUpdate(ReturnRequestDTO returnRequestDTO) {
        LOG.debug("Request to partially update ReturnRequest : {}", returnRequestDTO);

        return returnRequestRepository
            .findOneWithEagerRelationships(returnRequestDTO.getId())
            .map(existingReturnRequest -> {
                if (!currentEmployeeService.isAssetManagerOrAdmin()) {
                    Long eid = currentEmployeeService
                        .currentEmployeeId()
                        .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
                    if (
                        existingReturnRequest.getRequester() == null ||
                        !eid.equals(existingReturnRequest.getRequester().getId())
                    ) {
                        throw new AccessDeniedException("Không phải yêu cầu của bạn");
                    }
                    if (returnRequestDTO.getStatus() != null) {
                        if (returnRequestDTO.getStatus() != ReturnRequestStatus.CANCELLED) {
                            throw new AccessDeniedException("Chỉ được hủy yêu cầu (CANCELLED)");
                        }
                        if (
                            returnRequestDTO.getNote() != null ||
                            returnRequestDTO.getRequestDate() != null ||
                            returnRequestDTO.getCode() != null ||
                            returnRequestDTO.getRequester() != null
                        ) {
                            throw new AccessDeniedException("Khi hủy chỉ gửi id và status CANCELLED");
                        }
                        if (existingReturnRequest.getStatus() != ReturnRequestStatus.PENDING) {
                            throw new BadRequestAlertException("Chỉ hủy khi đang chờ duyệt", ENTITY_NAME, "notpending");
                        }
                    } else {
                        if (existingReturnRequest.getStatus() != ReturnRequestStatus.PENDING) {
                            throw new BadRequestAlertException("Chỉ sửa ghi chú khi đang chờ duyệt", ENTITY_NAME, "notpending");
                        }
                        if (returnRequestDTO.getNote() == null) {
                            throw new AccessDeniedException("Gửi ghi chú (note) cần cập nhật");
                        }
                        if (
                            returnRequestDTO.getCode() != null ||
                            returnRequestDTO.getRequestDate() != null ||
                            returnRequestDTO.getRequester() != null
                        ) {
                            throw new AccessDeniedException("Chỉ được sửa ghi chú (note)");
                        }
                    }
                }
                ReturnRequestStatus oldStatus = existingReturnRequest.getStatus();
                returnRequestMapper.partialUpdate(existingReturnRequest, returnRequestDTO);
                assertReturnCompletionAllowed(oldStatus, existingReturnRequest.getStatus(), existingReturnRequest.getId());
                ReturnRequest saved = returnRequestRepository.save(existingReturnRequest);
                if (saved.getStatus() == ReturnRequestStatus.COMPLETED && oldStatus != ReturnRequestStatus.COMPLETED) {
                    applyReturnCompletion(saved.getId());
                }
                return saved;
            })
            .map(returnRequestMapper::toDto);
    }

    private void assertReturnCompletionAllowed(ReturnRequestStatus oldStatus, ReturnRequestStatus newStatus, Long requestId) {
        if (newStatus != ReturnRequestStatus.COMPLETED || oldStatus == ReturnRequestStatus.COMPLETED) {
            return;
        }
        if (oldStatus != ReturnRequestStatus.APPROVED) {
            throw new BadRequestAlertException(
                "Chỉ hoàn thành thu hồi khi yêu cầu đã duyệt (APPROVED)",
                ENTITY_NAME,
                "notapproved"
            );
        }
        boolean anySelected = returnRequestLineRepository
            .findAllByRequest_Id(requestId)
            .stream()
            .anyMatch(line -> Boolean.TRUE.equals(line.getSelected()));
        if (!anySelected) {
            throw new BadRequestAlertException(
                "Chọn ít nhất một dòng thu hồi thực tế (selected) trước khi hoàn thành",
                ENTITY_NAME,
                "noselection"
            );
        }
    }

    private static ReturnDisposition resolveDisposition(ReturnRequestLine line) {
        return line.getDisposition() != null ? line.getDisposition() : ReturnDisposition.TO_STOCK;
    }

    /**
     * Trả vật tư từ các bàn giao: NV → phòng ban (pool) → toàn công ty (pool). Không đủ tồn bàn giao thì chỉ trừ phần khớp được.
     */
    private int drainConsumableReturnedQuantity(Long requesterId, Long assetItemId, int qtyToReturn) {
        if (qtyToReturn <= 0) {
            return 0;
        }
        int remaining = qtyToReturn;
        int totalDrained = 0;
        Optional<Employee> requesterEmp = employeeRepository.findOneWithToOneRelationships(requesterId);
        Long deptId = requesterEmp
            .map(e -> e.getDepartment() != null ? e.getDepartment().getId() : null)
            .orElse(null);
        Long locId = requesterEmp.map(e -> e.getLocation() != null ? e.getLocation().getId() : null).orElse(null);

        totalDrained += drainConsumableFromList(
            consumableAssignmentRepository.findByEmployee_IdAndAssetItem_IdOrderByIdAsc(requesterId, assetItemId),
            remaining
        );
        remaining = qtyToReturn - totalDrained;
        if (remaining > 0 && deptId != null) {
            int d = drainConsumableFromList(
                consumableAssignmentRepository.findByDepartment_IdAndAssetItem_IdAndEmployeeIsNullOrderByIdAsc(deptId, assetItemId),
                remaining
            );
            totalDrained += d;
            remaining = qtyToReturn - totalDrained;
        }
        if (remaining > 0 && locId != null) {
            int d = drainConsumableFromList(
                consumableAssignmentRepository.findByLocation_IdAndAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullOrderByIdAsc(locId, assetItemId),
                remaining
            );
            totalDrained += d;
            remaining = qtyToReturn - totalDrained;
        }
        if (remaining > 0) {
            totalDrained += drainConsumableFromList(
                consumableAssignmentRepository.findByAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullAndLocationIsNullOrderByIdAsc(assetItemId),
                remaining
            );
        }
        return totalDrained;
    }

    private int drainConsumableFromList(List<ConsumableAssignment> assignments, int maxQty) {
        int remaining = maxQty;
        int taken = 0;
        for (ConsumableAssignment ca : assignments) {
            if (remaining <= 0) {
                break;
            }
            int already = ca.getReturnedQuantity() != null ? ca.getReturnedQuantity() : 0;
            int canReturn = ca.getQuantity() - already;
            if (canReturn <= 0) {
                continue;
            }
            int portion = Math.min(canReturn, remaining);
            ca.setReturnedQuantity(already + portion);
            consumableAssignmentRepository.save(ca);
            taken += portion;
            remaining -= portion;
        }
        return taken;
    }

    /**
     * Hoàn thành thu hồi: đóng bàn giao; thiết bị theo hướng xử lý; vật tư cập nhật returned_quantity và tồn (chỉ cộng tồn khi về kho).
     */
    private void applyReturnCompletion(Long returnRequestId) {
        ReturnRequest rr = returnRequestRepository.findOneWithEagerRelationships(returnRequestId).orElse(null);
        if (rr == null || rr.getStatus() != ReturnRequestStatus.COMPLETED) {
            return;
        }
        Employee requester = rr.getRequester();
        Long requesterId = requester != null ? requester.getId() : null;
        List<ReturnRequestLine> lines = returnRequestLineRepository.findAllByRequest_Id(returnRequestId);
        LocalDate today = LocalDate.now();

        for (ReturnRequestLine line : lines) {
            if (!Boolean.TRUE.equals(line.getSelected())) {
                continue;
            }
            if (line.getLineType() == AssetManagementType.DEVICE && line.getEquipment() != null && line.getEquipment().getId() != null) {
                ReturnDisposition disp = resolveDisposition(line);
                Long eqId = line.getEquipment().getId();
                equipmentAssignmentRepository
                    .findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(eqId)
                    .ifPresent(a -> {
                        a.setReturnedDate(today);
                        equipmentAssignmentRepository.save(a);
                    });
                equipmentRepository.findById(eqId).ifPresent(eq -> {
                    EquipmentOperationalStatus next = switch (disp) {
                        case TO_STOCK -> EquipmentOperationalStatus.IN_STOCK;
                        case TO_REPAIR -> EquipmentOperationalStatus.UNDER_REPAIR;
                        case BROKEN -> EquipmentOperationalStatus.BROKEN;
                        case LOST -> EquipmentOperationalStatus.LOST;
                    };
                    eq.setStatus(next);
                    equipmentRepository.save(eq);
                });
            } else if (line.getLineType() == AssetManagementType.CONSUMABLE && line.getAssetItem() != null && requesterId != null) {
                ReturnDisposition disp = resolveDisposition(line);
                int qty = line.getQuantity() != null ? line.getQuantity() : 0;
                if (qty <= 0) {
                    continue;
                }
                Long itemId = line.getAssetItem().getId();
                int actuallyReturned = drainConsumableReturnedQuantity(requesterId, itemId, qty);
                consumableStockRepository.findFirstByAssetItem_Id(itemId).ifPresent(stock -> {
                    int onHand = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
                    int issued = stock.getQuantityIssued() != null ? stock.getQuantityIssued() : 0;
                    stock.setQuantityIssued(Math.max(0, issued - actuallyReturned));
                    if (disp == ReturnDisposition.TO_STOCK) {
                        stock.setQuantityOnHand(onHand + actuallyReturned);
                    }
                    consumableStockRepository.save(stock);
                });
            }
        }
        String reqCode = rr.getCode() != null ? rr.getCode() : String.valueOf(returnRequestId);
        appAuditLogService.recordBusiness("RETURN_COMPLETED", "requestId=" + returnRequestId + " code=" + reqCode);
        LOG.debug("Applied return completion for request {}", returnRequestId);
    }

    public Page<ReturnRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return returnRequestRepository.findAllWithEagerRelationships(pageable).map(returnRequestMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReturnRequestDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnRequest : {}", id);
        return returnRequestRepository
            .findOneWithEagerRelationships(id)
            .map(returnRequestMapper::toDto)
            .filter(dto -> visibleToCurrentUser(dto));
    }

    private boolean visibleToCurrentUser(ReturnRequestDTO dto) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return true;
        }
        Long eid = currentEmployeeService.currentEmployeeId().orElse(null);
        return dto.getRequester() != null && dto.getRequester().getId() != null && dto.getRequester().getId().equals(eid);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ReturnRequest : {}", id);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được xóa yêu cầu thu hồi");
        }
        ReturnRequest rr = returnRequestRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "notfound"));
        ReturnRequestStatus s = rr.getStatus();
        if (
            s != ReturnRequestStatus.PENDING &&
            s != ReturnRequestStatus.REJECTED &&
            s != ReturnRequestStatus.CANCELLED
        ) {
            throw new BadRequestAlertException(
                "Chỉ xóa yêu cầu ở trạng thái chờ duyệt / từ chối / đã hủy",
                ENTITY_NAME,
                "baddeletestatus"
            );
        }
        returnRequestLineRepository.deleteAll(returnRequestLineRepository.findAllByRequest_Id(id));
        returnRequestRepository.deleteById(id);
    }
}
