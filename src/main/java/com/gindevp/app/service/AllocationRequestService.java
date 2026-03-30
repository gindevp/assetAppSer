package com.gindevp.app.service;

import com.gindevp.app.domain.*;
import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import com.gindevp.app.domain.enumeration.AssigneeType;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.repository.*;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.security.SecurityUtils;
import com.gindevp.app.service.dto.AllocationRequestDTO;
import com.gindevp.app.service.mapper.AllocationRequestMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.AllocationRequest}.
 */
@Service
@Transactional
public class AllocationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestService.class);

    private final AllocationRequestRepository allocationRequestRepository;

    private final AllocationRequestMapper allocationRequestMapper;

    private final AllocationRequestLineRepository allocationRequestLineRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final EquipmentRepository equipmentRepository;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final ConsumableStockRepository consumableStockRepository;

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final LocationRepository locationRepository;

    private final StockIssueRepository stockIssueRepository;

    private final StockIssueLineRepository stockIssueLineRepository;

    private final AppAuditLogService appAuditLogService;

    public AllocationRequestService(
        AllocationRequestRepository allocationRequestRepository,
        AllocationRequestMapper allocationRequestMapper,
        AllocationRequestLineRepository allocationRequestLineRepository,
        CurrentEmployeeService currentEmployeeService,
        EquipmentRepository equipmentRepository,
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        ConsumableAssignmentRepository consumableAssignmentRepository,
        ConsumableStockRepository consumableStockRepository,
        EmployeeRepository employeeRepository,
        DepartmentRepository departmentRepository,
        LocationRepository locationRepository,
        StockIssueRepository stockIssueRepository,
        StockIssueLineRepository stockIssueLineRepository,
        AppAuditLogService appAuditLogService
    ) {
        this.allocationRequestRepository = allocationRequestRepository;
        this.allocationRequestMapper = allocationRequestMapper;
        this.allocationRequestLineRepository = allocationRequestLineRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.equipmentRepository = equipmentRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.consumableStockRepository = consumableStockRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.locationRepository = locationRepository;
        this.stockIssueRepository = stockIssueRepository;
        this.stockIssueLineRepository = stockIssueLineRepository;
        this.appAuditLogService = appAuditLogService;
    }

    private static final String ENTITY_NAME = "allocationRequest";

    private void validateDeviceLinesPickedIfApproving(AllocationRequest allocationRequest) {
        Long id = allocationRequest.getId();
        if (id == null) {
            return;
        }
        AllocationRequestStatus st = allocationRequest.getStatus();
        if (st != AllocationRequestStatus.APPROVED && st != AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            return;
        }
        validateAllDeviceLinesHaveEquipment(id);
    }

    private void validateAllDeviceLinesHaveEquipment(Long allocationRequestId) {
        for (AllocationRequestLine line : allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId)) {
            if (line.getLineType() == AssetManagementType.DEVICE && line.getEquipment() == null) {
                throw new BadRequestAlertException(
                    "Mỗi dòng thiết bị phải chọn thiết bị tồn kho trước khi duyệt / hoàn thành",
                    ENTITY_NAME,
                    "equipmentrequired"
                );
            }
        }
    }

    private void assertExportSlipTransitionAllowed(AllocationRequestStatus oldStatus, AllocationRequestStatus newStatus) {
        if (newStatus != AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            return;
        }
        if (oldStatus == AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            return;
        }
        if (oldStatus != AllocationRequestStatus.APPROVED) {
            throw new BadRequestAlertException(
                "Chỉ ghi nhận phiếu xuất khi yêu cầu đã duyệt (APPROVED)",
                ENTITY_NAME,
                "exportslipnotapproved"
            );
        }
    }

    private void assertAllocationCompletionAllowed(
        AllocationRequestStatus oldStatus,
        AllocationRequestStatus newStatus,
        Long requestId
    ) {
        if (newStatus != AllocationRequestStatus.COMPLETED || oldStatus == AllocationRequestStatus.COMPLETED) {
            return;
        }
        if (
            oldStatus != AllocationRequestStatus.APPROVED && oldStatus != AllocationRequestStatus.EXPORT_SLIP_CREATED
        ) {
            throw new BadRequestAlertException(
                "Chỉ hoàn thành cấp phát khi đã duyệt (APPROVED) hoặc đã ghi nhận phiếu xuất (EXPORT_SLIP_CREATED)",
                ENTITY_NAME,
                "notready"
            );
        }
        validateAllDeviceLinesHaveEquipment(requestId);
    }

    private void defaultAssigneeFields(AllocationRequest ar) {
        if (ar.getAssigneeType() == null) {
            ar.setAssigneeType(AssigneeType.EMPLOYEE);
        }
        if (
            ar.getAssigneeType() == AssigneeType.EMPLOYEE &&
            (ar.getBeneficiaryEmployee() == null || ar.getBeneficiaryEmployee().getId() == null) &&
            ar.getRequester() != null &&
            ar.getRequester().getId() != null
        ) {
            Employee ref = new Employee();
            ref.setId(ar.getRequester().getId());
            ar.setBeneficiaryEmployee(ref);
        }
    }

    private void assertBeneficiaryStructure(AllocationRequest ar) {
        AssigneeType at = ar.getAssigneeType();
        if (at == null) {
            throw new BadRequestAlertException("Thiếu loại đối tượng nhận (assigneeType)", ENTITY_NAME, "noassigneetype");
        }
        switch (at) {
            case EMPLOYEE -> {
                if (ar.getBeneficiaryEmployee() == null || ar.getBeneficiaryEmployee().getId() == null) {
                    throw new BadRequestAlertException(
                        "Cấp cho nhân viên: cần beneficiaryEmployee (id)",
                        ENTITY_NAME,
                        "nobeneficiaryemployee"
                    );
                }
            }
            case DEPARTMENT -> {
                if (ar.getBeneficiaryDepartment() == null || ar.getBeneficiaryDepartment().getId() == null) {
                    throw new BadRequestAlertException(
                        "Cấp cho phòng ban: cần beneficiaryDepartment (id)",
                        ENTITY_NAME,
                        "nobeneficiarydept"
                    );
                }
            }
            case LOCATION -> {
                if (ar.getBeneficiaryLocation() == null || ar.getBeneficiaryLocation().getId() == null) {
                    throw new BadRequestAlertException(
                        "Cấp cho vị trí/khu vực: cần beneficiaryLocation (id)",
                        ENTITY_NAME,
                        "nobeneficiaryloc"
                    );
                }
            }
            case COMPANY -> {
                // không bắt buộc FK
            }
        }
    }

    private void assertBeneficiaryAllowedForCurrentUser(AllocationRequest ar) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return;
        }
        boolean coordinator = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.DEPARTMENT_COORDINATOR);
        AssigneeType at = ar.getAssigneeType();
        Long eid = currentEmployeeService
            .currentEmployeeId()
            .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));

        if (at == AssigneeType.EMPLOYEE) {
            Long bid = ar.getBeneficiaryEmployee().getId();
            if (Objects.equals(bid, eid)) {
                return;
            }
            if (coordinator) {
                return;
            }
            throw new AccessDeniedException("Nhân viên chỉ được xin cấp phát cho chính mình");
        }

        if (!coordinator) {
            throw new AccessDeniedException(
                "Chỉ điều phối phòng ban hoặc QLTS được xin cấp cho phòng ban / vị trí / toàn công ty"
            );
        }
    }

    private record AssignmentTargets(Employee employee, Department department, Location location) {}

    private AssignmentTargets resolveAssignmentTargets(AllocationRequest ar) {
        AssigneeType at = ar.getAssigneeType() != null ? ar.getAssigneeType() : AssigneeType.EMPLOYEE;
        return switch (at) {
            case EMPLOYEE -> {
                Long id = ar.getBeneficiaryEmployee().getId();
                Employee be = employeeRepository
                    .findById(id)
                    .orElseThrow(() ->
                        new BadRequestAlertException("Không tìm thấy nhân viên nhận", ENTITY_NAME, "beneficiarynotfound")
                    );
                yield new AssignmentTargets(be, be.getDepartment(), null);
            }
            case DEPARTMENT -> {
                Long id = ar.getBeneficiaryDepartment().getId();
                Department d = departmentRepository
                    .findById(id)
                    .orElseThrow(() ->
                        new BadRequestAlertException("Không tìm thấy phòng ban nhận", ENTITY_NAME, "deptnotfound")
                    );
                yield new AssignmentTargets(null, d, null);
            }
            case LOCATION -> {
                Long id = ar.getBeneficiaryLocation().getId();
                Location loc = locationRepository
                    .findById(id)
                    .orElseThrow(() ->
                        new BadRequestAlertException("Không tìm thấy vị trí nhận", ENTITY_NAME, "locnotfound")
                    );
                yield new AssignmentTargets(null, null, loc);
            }
            case COMPANY -> new AssignmentTargets(null, null, null);
        };
    }

    /**
     * Ghi nhận bàn giao thiết bị / xuất vật tư theo dòng YC khi chuyển sang COMPLETED.
     */
    private void applyAllocationCompletion(Long allocationRequestId) {
        AllocationRequest ar = allocationRequestRepository.findOneWithEagerRelationships(allocationRequestId).orElse(null);
        if (ar == null || ar.getStatus() != AllocationRequestStatus.COMPLETED) {
            return;
        }
        if (ar.getRequester() == null || ar.getRequester().getId() == null) {
            throw new BadRequestAlertException("Thiếu người yêu cầu trên yêu cầu cấp phát", ENTITY_NAME, "norequester");
        }
        employeeRepository
            .findById(ar.getRequester().getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy nhân viên yêu cầu", ENTITY_NAME, "requesternotfound"));
        assertBeneficiaryStructure(ar);
        AssignmentTargets targets = resolveAssignmentTargets(ar);

        LocalDate today = LocalDate.now();
        String code = ar.getCode() != null ? ar.getCode() : String.valueOf(allocationRequestId);
        String noteSuffix = "YC " + code;
        String equipNote =
            ar.getAssigneeType() == AssigneeType.COMPANY
                ? "Cấp phát toàn công ty — " + noteSuffix
                : "Cấp phát từ " + noteSuffix;

        List<AllocationRequestLine> lines = allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId);
        for (AllocationRequestLine line : lines) {
            if (line.getLineType() == AssetManagementType.DEVICE) {
                if (line.getEquipment() == null || line.getEquipment().getId() == null) {
                    continue;
                }
                Long eqId = line.getEquipment().getId();
                if (
                    equipmentAssignmentRepository.findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(eqId).isPresent()
                ) {
                    throw new BadRequestAlertException(
                        "Thiết bị đã đang được bàn giao — không cấp trùng",
                        ENTITY_NAME,
                        "equipmentbusy"
                    );
                }
                Equipment eq = equipmentRepository.findById(eqId).orElseThrow(() ->
                    new BadRequestAlertException("Không tìm thấy thiết bị", ENTITY_NAME, "eqnotfound")
                );
                if (eq.getStatus() != EquipmentOperationalStatus.IN_STOCK) {
                    throw new BadRequestAlertException(
                        "Thiết bị không ở trạng thái tồn kho (IN_STOCK)",
                        ENTITY_NAME,
                        "notinstock"
                    );
                }
                EquipmentAssignment assignment = new EquipmentAssignment();
                assignment.setAssignedDate(today);
                assignment.setEquipment(eq);
                assignment.setEmployee(targets.employee());
                assignment.setDepartment(targets.department());
                assignment.setLocation(targets.location());
                assignment.setNote(equipNote);
                equipmentAssignmentRepository.save(assignment);
                eq.setStatus(EquipmentOperationalStatus.IN_USE);
                equipmentRepository.save(eq);
            } else if (line.getLineType() == AssetManagementType.CONSUMABLE) {
                if (line.getAssetItem() == null || line.getAssetItem().getId() == null) {
                    continue;
                }
                int qty = line.getQuantity() != null ? line.getQuantity() : 0;
                if (qty <= 0) {
                    continue;
                }
                Long itemId = line.getAssetItem().getId();
                ConsumableStock stock = consumableStockRepository
                    .findFirstByAssetItem_Id(itemId)
                    .orElseThrow(() ->
                        new BadRequestAlertException("Chưa có bản ghi tồn kho cho vật tư này", ENTITY_NAME, "nostock")
                    );
                int onHand = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
                if (onHand < qty) {
                    throw new BadRequestAlertException(
                        "Không đủ tồn kho (cần " + qty + ", hiện " + onHand + ")",
                        ENTITY_NAME,
                        "insufficientstock"
                    );
                }
                stock.setQuantityOnHand(onHand - qty);
                int issued = stock.getQuantityIssued() != null ? stock.getQuantityIssued() : 0;
                stock.setQuantityIssued(issued + qty);
                consumableStockRepository.save(stock);

                ConsumableAssignment ca = new ConsumableAssignment();
                ca.setQuantity(qty);
                ca.setAssignedDate(today);
                ca.setReturnedQuantity(0);
                ca.setAssetItem(line.getAssetItem());
                ca.setEmployee(targets.employee());
                ca.setDepartment(targets.department());
                ca.setLocation(targets.location());
                ca.setNote(noteSuffix);
                consumableAssignmentRepository.save(ca);
            }
        }
        appAuditLogService.recordBusiness(
            "ALLOCATION_COMPLETED",
            "requestId=" + allocationRequestId + " code=" + code
        );
        LOG.debug("Applied allocation completion for request {}", allocationRequestId);
    }

    private String buildUniqueStockIssueCode(Long allocationRequestId) {
        String base = "PX" + LocalDate.now().getYear() + "-" + allocationRequestId;
        if (base.length() > 20) {
            base = base.substring(0, 20);
        }
        String code = base;
        int n = 0;
        while (stockIssueRepository.existsByCode(code)) {
            n++;
            String suffix = "x" + n;
            int maxBase = 20 - suffix.length();
            code = base.length() <= maxBase ? base + suffix : base.substring(0, Math.max(1, maxBase)) + suffix;
        }
        return code;
    }

    /**
     * Tạo phiếu xuất + dòng chi tiết khi YC chuyển sang EXPORT_SLIP_CREATED (idempotent).
     */
    private void ensureStockIssueForExportSlip(Long allocationRequestId) {
        if (stockIssueRepository.findByAllocationRequest_Id(allocationRequestId).isPresent()) {
            return;
        }
        AllocationRequest ar = allocationRequestRepository
            .findOneWithEagerRelationships(allocationRequestId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu cấp phát", ENTITY_NAME, "notfound"));
        if (ar.getStatus() != AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            return;
        }
        defaultAssigneeFields(ar);
        assertBeneficiaryStructure(ar);
        AssignmentTargets targets = resolveAssignmentTargets(ar);
        LocalDate today = LocalDate.now();
        String code = buildUniqueStockIssueCode(allocationRequestId);
        StockIssue issue = new StockIssue();
        issue.setCode(code);
        issue.setIssueDate(today);
        issue.setStatus(DocumentStatus.CONFIRMED);
        issue.setAssigneeType(ar.getAssigneeType());
        issue.setNote("Phiếu xuất theo YC " + (ar.getCode() != null ? ar.getCode() : String.valueOf(allocationRequestId)));
        issue.setAllocationRequest(ar);
        issue.setEmployee(targets.employee());
        issue.setDepartment(targets.department());
        issue.setLocation(targets.location());
        issue = stockIssueRepository.save(issue);

        List<AllocationRequestLine> lines = allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId);
        for (AllocationRequestLine line : lines) {
            if (line.getLineType() == AssetManagementType.DEVICE) {
                if (line.getEquipment() == null || line.getEquipment().getId() == null) {
                    continue;
                }
                StockIssueLine sil = new StockIssueLine();
                sil.setLineNo(line.getLineNo());
                sil.setQuantity(1);
                sil.setIssue(issue);
                sil.setEquipment(line.getEquipment());
                AssetItem ai =
                    line.getEquipment().getAssetItem() != null ? line.getEquipment().getAssetItem() : line.getAssetItem();
                sil.setAssetItem(ai);
                sil.setNote(line.getNote());
                stockIssueLineRepository.save(sil);
            } else if (line.getLineType() == AssetManagementType.CONSUMABLE) {
                if (line.getAssetItem() == null || line.getAssetItem().getId() == null) {
                    continue;
                }
                int qty = line.getQuantity() != null ? line.getQuantity() : 0;
                if (qty <= 0) {
                    continue;
                }
                StockIssueLine sil = new StockIssueLine();
                sil.setLineNo(line.getLineNo());
                sil.setQuantity(qty);
                sil.setIssue(issue);
                sil.setAssetItem(line.getAssetItem());
                sil.setNote(line.getNote());
                stockIssueLineRepository.save(sil);
            }
        }
        LOG.debug("Created stock issue {} for allocation request {}", code, allocationRequestId);
    }

    /**
     * Save a allocationRequest.
     *
     * @param allocationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public AllocationRequestDTO save(AllocationRequestDTO allocationRequestDTO) {
        LOG.debug("Request to save AllocationRequest : {}", allocationRequestDTO);
        if (allocationRequestDTO.getStatus() == AllocationRequestStatus.COMPLETED) {
            throw new BadRequestAlertException(
                "Không tạo yêu cầu cấp phát ở trạng thái hoàn thành",
                ENTITY_NAME,
                "invalidcreatestatus"
            );
        }
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() ->
                    new BadRequestAlertException(
                        "Tài khoản chưa liên kết nhân viên. Admin: gán nhân viên trong Quản lý user.",
                        ENTITY_NAME,
                        "noemployee"
                    )
                );
            if (
                allocationRequestDTO.getRequester() == null ||
                allocationRequestDTO.getRequester().getId() == null ||
                !allocationRequestDTO.getRequester().getId().equals(eid)
            ) {
                throw new AccessDeniedException("Chỉ được tạo yêu cầu thay mặt nhân viên đã đăng nhập");
            }
        }
        AllocationRequest allocationRequest = allocationRequestMapper.toEntity(allocationRequestDTO);
        defaultAssigneeFields(allocationRequest);
        assertBeneficiaryStructure(allocationRequest);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            assertBeneficiaryAllowedForCurrentUser(allocationRequest);
        }
        allocationRequest = allocationRequestRepository.save(allocationRequest);
        validateDeviceLinesPickedIfApproving(allocationRequest);
        return allocationRequestMapper.toDto(allocationRequest);
    }

    /**
     * Update a allocationRequest.
     *
     * @param allocationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public AllocationRequestDTO update(AllocationRequestDTO allocationRequestDTO) {
        LOG.debug("Request to update AllocationRequest : {}", allocationRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật đầy đủ yêu cầu cấp phát");
        }
        Long id = allocationRequestDTO.getId();
        AllocationRequestStatus oldStatus = allocationRequestRepository.findById(id).map(AllocationRequest::getStatus).orElse(null);
        AllocationRequest allocationRequest = allocationRequestMapper.toEntity(allocationRequestDTO);
        defaultAssigneeFields(allocationRequest);
        assertBeneficiaryStructure(allocationRequest);
        assertExportSlipTransitionAllowed(oldStatus, allocationRequest.getStatus());
        assertAllocationCompletionAllowed(oldStatus, allocationRequest.getStatus(), id);
        validateDeviceLinesPickedIfApproving(allocationRequest);
        final AllocationRequest savedAr = allocationRequestRepository.save(allocationRequest);
        if (oldStatus == AllocationRequestStatus.APPROVED && savedAr.getStatus() == AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            ensureStockIssueForExportSlip(savedAr.getId());
        }
        if (
            savedAr.getStatus() == AllocationRequestStatus.COMPLETED &&
            oldStatus != AllocationRequestStatus.COMPLETED
        ) {
            applyAllocationCompletion(savedAr.getId());
        }
        return allocationRequestRepository
            .findOneWithEagerRelationships(savedAr.getId())
            .map(allocationRequestMapper::toDto)
            .orElseGet(() -> allocationRequestMapper.toDto(savedAr));
    }

    /**
     * Partially update a allocationRequest.
     *
     * @param allocationRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AllocationRequestDTO> partialUpdate(AllocationRequestDTO allocationRequestDTO) {
        LOG.debug("Request to partially update AllocationRequest : {}", allocationRequestDTO);

        return allocationRequestRepository
            .findOneWithEagerRelationships(allocationRequestDTO.getId())
            .map(existingAllocationRequest -> {
                if (!currentEmployeeService.isAssetManagerOrAdmin()) {
                    Long eid = currentEmployeeService
                        .currentEmployeeId()
                        .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
                    if (
                        existingAllocationRequest.getRequester() == null ||
                        !eid.equals(existingAllocationRequest.getRequester().getId())
                    ) {
                        throw new AccessDeniedException("Không phải yêu cầu của bạn");
                    }
                    if (allocationRequestDTO.getStatus() != null) {
                        if (allocationRequestDTO.getStatus() != AllocationRequestStatus.CANCELLED) {
                            throw new AccessDeniedException("Chỉ được hủy yêu cầu (CANCELLED)");
                        }
                    }
                    if (
                        allocationRequestDTO.getReason() != null ||
                        allocationRequestDTO.getBeneficiaryNote() != null ||
                        allocationRequestDTO.getAssigneeType() != null ||
                        allocationRequestDTO.getBeneficiaryEmployee() != null ||
                        allocationRequestDTO.getBeneficiaryDepartment() != null ||
                        allocationRequestDTO.getBeneficiaryLocation() != null ||
                        allocationRequestDTO.getRequestDate() != null ||
                        allocationRequestDTO.getCode() != null ||
                        allocationRequestDTO.getRequester() != null
                    ) {
                        throw new AccessDeniedException("Chỉ được gửi id và status CANCELLED");
                    }
                    if (existingAllocationRequest.getStatus() != AllocationRequestStatus.PENDING) {
                        throw new BadRequestAlertException("Chỉ hủy khi đang chờ duyệt", ENTITY_NAME, "notpending");
                    }
                }
                AllocationRequestStatus oldStatus = existingAllocationRequest.getStatus();
                allocationRequestMapper.partialUpdate(existingAllocationRequest, allocationRequestDTO);
                defaultAssigneeFields(existingAllocationRequest);
                assertBeneficiaryStructure(existingAllocationRequest);
                assertExportSlipTransitionAllowed(oldStatus, existingAllocationRequest.getStatus());
                assertAllocationCompletionAllowed(oldStatus, existingAllocationRequest.getStatus(), existingAllocationRequest.getId());
                validateDeviceLinesPickedIfApproving(existingAllocationRequest);
                AllocationRequest saved = allocationRequestRepository.save(existingAllocationRequest);
                if (oldStatus == AllocationRequestStatus.APPROVED && saved.getStatus() == AllocationRequestStatus.EXPORT_SLIP_CREATED) {
                    ensureStockIssueForExportSlip(saved.getId());
                }
                if (
                    saved.getStatus() == AllocationRequestStatus.COMPLETED &&
                    oldStatus != AllocationRequestStatus.COMPLETED
                ) {
                    applyAllocationCompletion(saved.getId());
                }
                return saved;
            })
            .map(ar ->
                allocationRequestRepository.findOneWithEagerRelationships(ar.getId()).map(allocationRequestMapper::toDto).orElseGet(() -> allocationRequestMapper.toDto(ar))
            );
    }

    /**
     * Get all the allocationRequests with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AllocationRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return allocationRequestRepository.findAllWithEagerRelationships(pageable).map(allocationRequestMapper::toDto);
    }

    /**
     * Get one allocationRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AllocationRequestDTO> findOne(Long id) {
        LOG.debug("Request to get AllocationRequest : {}", id);
        return allocationRequestRepository
            .findOneWithEagerRelationships(id)
            .map(allocationRequestMapper::toDto)
            .filter(this::visibleAllocationToCurrentUser);
    }

    private boolean visibleAllocationToCurrentUser(AllocationRequestDTO dto) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return true;
        }
        Long eid = currentEmployeeService.currentEmployeeId().orElse(null);
        return dto.getRequester() != null && dto.getRequester().getId() != null && dto.getRequester().getId().equals(eid);
    }

    /**
     * Delete the allocationRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AllocationRequest : {}", id);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được xóa yêu cầu cấp phát");
        }
        allocationRequestRepository.deleteById(id);
    }
}
