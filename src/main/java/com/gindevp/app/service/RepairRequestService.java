package com.gindevp.app.service;

import com.gindevp.app.domain.ConsumableAssignment;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.domain.RepairRequestLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.domain.enumeration.RepairResolution;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.repository.ConsumableAssignmentRepository;
import com.gindevp.app.repository.ConsumableStockRepository;
import com.gindevp.app.repository.EmployeeRepository;
import com.gindevp.app.repository.EquipmentAssignmentRepository;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.repository.RepairRequestRepository;
import com.gindevp.app.service.dto.RepairRequestDTO;
import com.gindevp.app.service.dto.RepairRequestLineDTO;
import com.gindevp.app.service.mapper.RepairRequestLineMapper;
import com.gindevp.app.service.mapper.RepairRequestMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.RepairRequest}.
 */
@Service
@Transactional
public class RepairRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(RepairRequestService.class);

    private static final String ENTITY_NAME = "repairRequest";

    private final RepairRequestRepository repairRequestRepository;

    private final RepairRequestMapper repairRequestMapper;

    private final RepairRequestLineMapper repairRequestLineMapper;

    private final EquipmentRepository equipmentRepository;

    private final AssetItemRepository assetItemRepository;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final ConsumableStockRepository consumableStockRepository;

    private final EmployeeRepository employeeRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final AppAuditLogService appAuditLogService;

    private final EquipmentRepairReturnEligibilityService equipmentRepairReturnEligibilityService;

    public RepairRequestService(
        RepairRequestRepository repairRequestRepository,
        RepairRequestMapper repairRequestMapper,
        RepairRequestLineMapper repairRequestLineMapper,
        EquipmentRepository equipmentRepository,
        AssetItemRepository assetItemRepository,
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        ConsumableAssignmentRepository consumableAssignmentRepository,
        ConsumableStockRepository consumableStockRepository,
        EmployeeRepository employeeRepository,
        CurrentEmployeeService currentEmployeeService,
        AppAuditLogService appAuditLogService,
        EquipmentRepairReturnEligibilityService equipmentRepairReturnEligibilityService
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.repairRequestMapper = repairRequestMapper;
        this.repairRequestLineMapper = repairRequestLineMapper;
        this.equipmentRepository = equipmentRepository;
        this.assetItemRepository = assetItemRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.consumableStockRepository = consumableStockRepository;
        this.employeeRepository = employeeRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.appAuditLogService = appAuditLogService;
        this.equipmentRepairReturnEligibilityService = equipmentRepairReturnEligibilityService;
    }

    public RepairRequestDTO save(RepairRequestDTO repairRequestDTO) {
        LOG.debug("Request to save RepairRequest : {}", repairRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
            if (
                repairRequestDTO.getRequester() == null ||
                repairRequestDTO.getRequester().getId() == null ||
                !repairRequestDTO.getRequester().getId().equals(eid)
            ) {
                throw new AccessDeniedException("Chỉ được tạo yêu cầu sửa chữa thay mặt nhân viên đã đăng nhập");
            }
        }
        List<RepairRequestLineDTO> lineDtos = resolveLineDtos(repairRequestDTO, null);
        validateAndAssertEligibility(lineDtos, null);
        RepairRequest repairRequest = repairRequestMapper.toEntity(repairRequestDTO);
        syncRepairLinesFromDtos(repairRequest, lineDtos);
        RepairRequestStatus old = RepairRequestStatus.NEW;
        repairRequest = repairRequestRepository.save(repairRequest);
        RepairRequest forLifecycle = repairRequestRepository
            .findOneWithEagerRelationships(repairRequest.getId())
            .orElse(repairRequest);
        applyRepairLifecycle(old, forLifecycle);
        return repairRequestMapper.toDto(
            repairRequestRepository.findOneWithEagerRelationships(repairRequest.getId()).orElse(repairRequest)
        );
    }

    public RepairRequestDTO update(RepairRequestDTO repairRequestDTO) {
        LOG.debug("Request to update RepairRequest : {}", repairRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật đầy đủ yêu cầu sửa chữa");
        }
        RepairRequest entity = repairRequestRepository
            .findOneWithEagerRelationships(repairRequestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu sửa chữa", ENTITY_NAME, "notfound"));
        RepairRequestStatus old = entity.getStatus();
        List<RepairRequestLineDTO> lineDtos = resolveLineDtos(repairRequestDTO, entity);
        validateAndAssertEligibility(lineDtos, repairRequestDTO.getId());
        repairRequestMapper.partialUpdate(entity, repairRequestDTO);
        syncRepairLinesFromDtos(entity, lineDtos);
        assertRepairStatusTransition(old, entity.getStatus());
        assertRejectionReasonWhenRejected(old, entity.getStatus(), entity.getRejectionReason());
        RepairRequest saved = repairRequestRepository.save(entity);
        RepairRequest forLifecycle = repairRequestRepository.findOneWithEagerRelationships(saved.getId()).orElse(saved);
        applyRepairLifecycle(old, forLifecycle);
        return repairRequestMapper.toDto(repairRequestRepository.findOneWithEagerRelationships(saved.getId()).orElse(saved));
    }

    public Optional<RepairRequestDTO> partialUpdate(RepairRequestDTO repairRequestDTO) {
        LOG.debug("Request to partially update RepairRequest : {}", repairRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            return repairRequestRepository
                .findById(repairRequestDTO.getId())
                .map(existingRepairRequest -> {
                    Long eid = currentEmployeeService
                        .currentEmployeeId()
                        .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
                    if (
                        existingRepairRequest.getRequester() == null ||
                        !eid.equals(existingRepairRequest.getRequester().getId())
                    ) {
                        throw new AccessDeniedException("Không phải yêu cầu của bạn");
                    }
                    if (existingRepairRequest.getStatus() != RepairRequestStatus.NEW) {
                        throw new BadRequestAlertException("Chỉ sửa khi trạng thái Mới", ENTITY_NAME, "notnew");
                    }
                    if (repairRequestDTO.getStatus() != null) {
                        throw new AccessDeniedException("Không đổi trạng thái từ form sửa của nhân viên");
                    }
                    if (employeeRepairContentPatchHasForbiddenFields(repairRequestDTO)) {
                        throw new AccessDeniedException("Chỉ được sửa vấn đề, mô tả, ghi chú đính kèm");
                    }
                    RepairRequestStatus old = existingRepairRequest.getStatus();
                    repairRequestMapper.partialUpdate(existingRepairRequest, repairRequestDTO);
                    RepairRequest saved = repairRequestRepository.save(existingRepairRequest);
                    RepairRequest forLifecycle = repairRequestRepository.findOneWithEagerRelationships(saved.getId()).orElse(saved);
                    applyRepairLifecycle(old, forLifecycle);
                    return saved;
                })
                .map(repairRequestMapper::toDto);
        }

        return repairRequestRepository
            .findOneWithEagerRelationships(repairRequestDTO.getId())
            .map(existingRepairRequest -> {
                RepairRequestStatus old = existingRepairRequest.getStatus();
                List<RepairRequestLineDTO> lineDtos = resolveLineDtos(repairRequestDTO, existingRepairRequest);
                validateAndAssertEligibility(lineDtos, repairRequestDTO.getId());
                repairRequestMapper.partialUpdate(existingRepairRequest, repairRequestDTO);
                syncRepairLinesFromDtos(existingRepairRequest, lineDtos);
                assertRepairStatusTransition(old, existingRepairRequest.getStatus());
                assertRejectionReasonWhenRejected(old, existingRepairRequest.getStatus(), existingRepairRequest.getRejectionReason());
                RepairRequest saved = repairRequestRepository.save(existingRepairRequest);
                RepairRequest forLifecycle = repairRequestRepository.findOneWithEagerRelationships(saved.getId()).orElse(saved);
                applyRepairLifecycle(old, forLifecycle);
                return saved;
            })
            .map(repairRequestMapper::toDto);
    }

    /**
     * Luồng: NEW → (ACCEPTED | REJECTED) → IN_PROGRESS (chỉ từ ACCEPTED) → COMPLETED (chỉ từ IN_PROGRESS).
     */
    private static void assertRepairStatusTransition(RepairRequestStatus oldStatus, RepairRequestStatus newStatus) {
        if (newStatus == null || newStatus == oldStatus) {
            return;
        }
        boolean allowed =
            (oldStatus == RepairRequestStatus.NEW && (newStatus == RepairRequestStatus.ACCEPTED || newStatus == RepairRequestStatus.REJECTED)) ||
            (oldStatus == RepairRequestStatus.ACCEPTED && newStatus == RepairRequestStatus.IN_PROGRESS) ||
            (oldStatus == RepairRequestStatus.IN_PROGRESS && newStatus == RepairRequestStatus.COMPLETED);
        if (!allowed) {
            throw new BadRequestAlertException(
                "Chuyển trạng thái không hợp lệ: "
                    + oldStatus
                    + " → "
                    + newStatus
                    + ". Hoàn tất chỉ sau khi đã chuyển sang Đang sửa.",
                ENTITY_NAME,
                "badtransition"
            );
        }
    }

    private void applyRepairLifecycle(RepairRequestStatus oldStatus, RepairRequest saved) {
        if (oldStatus == RepairRequestStatus.NEW &&
            (saved.getStatus() == RepairRequestStatus.ACCEPTED || saved.getStatus() == RepairRequestStatus.IN_PROGRESS)
        ) {
            applyRepairStarted(saved);
        }
        if (saved.getStatus() == RepairRequestStatus.COMPLETED && oldStatus != RepairRequestStatus.COMPLETED) {
            applyRepairCompleted(saved);
        }
    }

    private void applyRepairStarted(RepairRequest saved) {
        List<Long> ids = equipmentIdsForRequest(saved);
        if (ids.isEmpty()) {
            return;
        }
        for (Long eqId : ids) {
            equipmentRepository.findById(eqId).ifPresent(eq -> {
                eq.setStatus(EquipmentOperationalStatus.UNDER_REPAIR);
                equipmentRepository.save(eq);
            });
        }
        LOG.debug("Repair started for {} equipment(s) on request {}", ids.size(), saved.getId());
    }

    private void applyRepairCompleted(RepairRequest saved) {
        RepairResolution out = saved.getRepairOutcome() != null ? saved.getRepairOutcome() : RepairResolution.RETURN_USER;
        for (Long eqId : equipmentIdsForRequest(saved)) {
            Equipment eq = equipmentRepository.findById(eqId).orElse(null);
            if (eq == null) {
                continue;
            }
            switch (out) {
                case RETURN_USER -> eq.setStatus(EquipmentOperationalStatus.IN_USE);
                case RETURN_STOCK -> {
                    eq.setStatus(EquipmentOperationalStatus.IN_STOCK);
                    closeOpenAssignment(eqId);
                }
                case MARK_BROKEN -> {
                    eq.setStatus(EquipmentOperationalStatus.BROKEN);
                    closeOpenAssignment(eqId);
                }
            }
            equipmentRepository.save(eq);
            appAuditLogService.recordBusiness(
                "REPAIR_COMPLETED",
                "requestId=" + saved.getId() + " equipmentId=" + eqId + " outcome=" + out
            );
            LOG.debug("Repair completed for equipment {} outcome {}", eqId, out);
        }
        applyConsumableRepairCompletion(saved, out);
    }

    /**
     * Kết quả «Trả về kho» / «Hỏng»: trừ SL bàn giao vật tư (FIFO) và cập nhật tồn — giống hoàn thành thu hồi.
     * «Trả lại người dùng»: không đụng bàn giao vật tư.
     */
    private void applyConsumableRepairCompletion(RepairRequest saved, RepairResolution out) {
        if (out == RepairResolution.RETURN_USER) {
            return;
        }
        Employee requester = saved.getRequester();
        Long requesterId = requester != null ? requester.getId() : null;
        if (requesterId == null) {
            return;
        }
        if (saved.getLines() == null || saved.getLines().isEmpty()) {
            return;
        }
        for (RepairRequestLine line : saved.getLines()) {
            if (line.getLineType() != AssetManagementType.CONSUMABLE || line.getAssetItem() == null) {
                continue;
            }
            Long itemId = line.getAssetItem().getId();
            if (itemId == null) {
                continue;
            }
            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            if (qty <= 0) {
                continue;
            }
            int actuallyReturned = drainConsumableReturnedQuantity(requesterId, itemId, qty);
            consumableStockRepository.findFirstByAssetItem_Id(itemId).ifPresent(stock -> {
                int onHand = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
                int issued = stock.getQuantityIssued() != null ? stock.getQuantityIssued() : 0;
                stock.setQuantityIssued(Math.max(0, issued - actuallyReturned));
                if (out == RepairResolution.RETURN_STOCK) {
                    stock.setQuantityOnHand(onHand + actuallyReturned);
                }
                consumableStockRepository.save(stock);
            });
            appAuditLogService.recordBusiness(
                "REPAIR_CONSUMABLE_COMPLETED",
                "requestId="
                    + saved.getId()
                    + " assetItemId="
                    + itemId
                    + " qty="
                    + qty
                    + " drained="
                    + actuallyReturned
                    + " outcome="
                    + out
            );
            LOG.debug("Repair consumable line completed assetItemId={} drained={} outcome={}", itemId, actuallyReturned, out);
        }
    }

    /** Cùng logic {@link ReturnRequestService} — trả vật tư từ bàn giao NV → PB → vị trí → pool. */
    private int drainConsumableReturnedQuantity(Long requesterId, Long assetItemId, int qtyToReturn) {
        if (qtyToReturn <= 0) {
            return 0;
        }
        int remaining = qtyToReturn;
        int totalDrained = 0;
        Optional<Employee> requesterEmp = employeeRepository.findOneWithToOneRelationships(requesterId);
        Long deptId = requesterEmp.map(e -> e.getDepartment() != null ? e.getDepartment().getId() : null).orElse(null);
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

    private static List<Long> equipmentIdsForRequest(RepairRequest r) {
        if (r.getLines() != null && !r.getLines().isEmpty()) {
            return r
                .getLines()
                .stream()
                .filter(l -> l.getLineType() == null || l.getLineType() == AssetManagementType.DEVICE)
                .map(RepairRequestLine::getEquipment)
                .filter(Objects::nonNull)
                .map(Equipment::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        }
        if (r.getEquipment() != null && r.getEquipment().getId() != null) {
            return List.of(r.getEquipment().getId());
        }
        return List.of();
    }

    private List<RepairRequestLineDTO> resolveLineDtos(RepairRequestDTO dto, RepairRequest existing) {
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            return dto.getLines();
        }
        if (dto.getEquipment() != null && dto.getEquipment().getId() != null) {
            RepairRequestLineDTO one = new RepairRequestLineDTO();
            one.setLineNo(1);
            one.setLineType(AssetManagementType.DEVICE);
            one.setEquipment(dto.getEquipment());
            return List.of(one);
        }
        if (existing != null && existing.getLines() != null && !existing.getLines().isEmpty()) {
            return existing.getLines().stream().map(repairRequestLineMapper::toDto).toList();
        }
        return List.of();
    }

    private void validateAndAssertEligibility(List<RepairRequestLineDTO> lines, Long excludeRepairRequestId) {
        if (lines.isEmpty()) {
            throw new BadRequestAlertException("Cần ít nhất một thiết bị hoặc vật tư", ENTITY_NAME, "nolines");
        }
        List<Long> equipmentIds = new ArrayList<>();
        List<Long> consumableIds = new ArrayList<>();
        for (RepairRequestLineDTO line : lines) {
            AssetManagementType lt = line.getLineType() != null ? line.getLineType() : AssetManagementType.DEVICE;
            if (lt == AssetManagementType.DEVICE) {
                if (line.getEquipment() == null || line.getEquipment().getId() == null) {
                    throw new BadRequestAlertException("Dòng thiết bị cần chọn thiết bị", ENTITY_NAME, "lineequipment");
                }
                equipmentIds.add(line.getEquipment().getId());
            } else if (lt == AssetManagementType.CONSUMABLE) {
                if (line.getAssetItem() == null || line.getAssetItem().getId() == null) {
                    throw new BadRequestAlertException("Dòng vật tư cần chọn vật tư", ENTITY_NAME, "lineassetitem");
                }
                if (line.getQuantity() == null || line.getQuantity() < 1) {
                    throw new BadRequestAlertException("Dòng vật tư cần số lượng ≥ 1", ENTITY_NAME, "lineqty");
                }
                consumableIds.add(line.getAssetItem().getId());
            } else {
                throw new BadRequestAlertException("lineType không hợp lệ", ENTITY_NAME, "badlinetype");
            }
        }
        assertDistinctEquipmentLines(equipmentIds);
        assertDistinctConsumableLines(consumableIds);
        equipmentRepairReturnEligibilityService.assertEquipmentEligibleForRepair(equipmentIds, excludeRepairRequestId);
        equipmentRepairReturnEligibilityService.assertConsumableAssetItemsEligibleForRepair(consumableIds, excludeRepairRequestId);
    }

    private void assertDistinctEquipmentLines(List<Long> equipmentIds) {
        Set<Long> seen = new HashSet<>();
        for (Long id : equipmentIds) {
            if (!seen.add(id)) {
                throw new BadRequestAlertException(
                    "Trùng thiết bị trên cùng phiếu: "
                        + equipmentRepairReturnEligibilityService.formatEquipmentRef(id),
                    ENTITY_NAME,
                    "duplicateequipment"
                );
            }
        }
    }

    private void assertDistinctConsumableLines(List<Long> consumableIds) {
        Set<Long> seen = new HashSet<>();
        for (Long id : consumableIds) {
            if (!seen.add(id)) {
                throw new BadRequestAlertException(
                    "Trùng vật tư trên cùng phiếu: " + equipmentRepairReturnEligibilityService.formatAssetItemRef(id),
                    ENTITY_NAME,
                    "duplicateconsumable"
                );
            }
        }
    }

    private void syncRepairLinesFromDtos(RepairRequest entity, List<RepairRequestLineDTO> lineDtos) {
        if (entity.getLines() == null) {
            entity.setLines(new ArrayList<>());
        }
        entity.getLines().clear();
        int n = 1;
        for (RepairRequestLineDTO lineDto : lineDtos) {
            AssetManagementType lt = lineDto.getLineType() != null ? lineDto.getLineType() : AssetManagementType.DEVICE;
            RepairRequestLine line = new RepairRequestLine();
            line.setLineNo(lineDto.getLineNo() != null ? lineDto.getLineNo() : n);
            if (lineDto.getLineNo() == null) {
                n++;
            } else {
                n = lineDto.getLineNo() + 1;
            }
            line.setLineType(lt);
            line.setRepairRequest(entity);
            if (lt == AssetManagementType.DEVICE) {
                line.setEquipment(equipmentRepository.getReferenceById(lineDto.getEquipment().getId()));
                line.setAssetItem(null);
                line.setQuantity(null);
            } else {
                line.setEquipment(null);
                line.setAssetItem(assetItemRepository.getReferenceById(lineDto.getAssetItem().getId()));
                line.setQuantity(lineDto.getQuantity());
            }
            entity.getLines().add(line);
        }
        Equipment firstEq = entity
            .getLines()
            .stream()
            .filter(l -> l.getLineType() == AssetManagementType.DEVICE && l.getEquipment() != null)
            .map(RepairRequestLine::getEquipment)
            .findFirst()
            .orElse(null);
        entity.setEquipment(firstEq);
    }

    private void closeOpenAssignment(Long equipmentId) {
        equipmentAssignmentRepository
            .findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(equipmentId)
            .ifPresent(a -> {
                a.setReturnedDate(LocalDate.now());
                equipmentAssignmentRepository.save(a);
            });
    }

    public Page<RepairRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return repairRequestRepository.findAllWithEagerRelationships(pageable).map(repairRequestMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<RepairRequestDTO> findOne(Long id) {
        LOG.debug("Request to get RepairRequest : {}", id);
        return repairRequestRepository
            .findOneWithEagerRelationships(id)
            .map(repairRequestMapper::toDto)
            .filter(this::visibleRepairToCurrentUser);
    }

    private boolean visibleRepairToCurrentUser(RepairRequestDTO dto) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return true;
        }
        Long eid = currentEmployeeService.currentEmployeeId().orElse(null);
        return dto.getRequester() != null && dto.getRequester().getId() != null && dto.getRequester().getId().equals(eid);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete RepairRequest : {}", id);
        RepairRequest r = repairRequestRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu sửa chữa", ENTITY_NAME, "notfound"));
        RepairRequestStatus s = r.getStatus();
        if (s != RepairRequestStatus.NEW && s != RepairRequestStatus.REJECTED) {
            throw new BadRequestAlertException(
                "Chỉ xóa yêu cầu ở trạng thái Mới hoặc Từ chối",
                ENTITY_NAME,
                "baddeletestatus"
            );
        }

        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            repairRequestRepository.deleteById(id);
            return;
        }

        Long eid = currentEmployeeService.currentEmployeeId().orElse(null);
        if (eid == null || r.getRequester() == null || !Objects.equals(r.getRequester().getId(), eid)) {
            throw new AccessDeniedException("Chỉ QLTS/Admin hoặc người gửi phiếu được xóa yêu cầu sửa chữa");
        }
        if (s != RepairRequestStatus.NEW) {
            throw new AccessDeniedException("Người gửi chỉ được hủy (xóa) phiếu ở trạng thái Mới tạo");
        }
        repairRequestRepository.deleteById(id);
    }

    private static boolean employeeRepairContentPatchHasForbiddenFields(RepairRequestDTO d) {
        return (
            d.getCode() != null ||
            d.getRequestDate() != null ||
            d.getRequester() != null ||
            d.getEquipment() != null ||
            d.getLines() != null ||
            d.getStatus() != null ||
            d.getResolutionNote() != null ||
            d.getRepairOutcome() != null ||
            d.getRejectionReason() != null
        );
    }

    private static void assertRejectionReasonWhenRejected(
        RepairRequestStatus oldStatus,
        RepairRequestStatus newStatus,
        String rejectionReason
    ) {
        if (oldStatus == RepairRequestStatus.NEW && newStatus == RepairRequestStatus.REJECTED) {
            if (rejectionReason == null || rejectionReason.isBlank()) {
                throw new BadRequestAlertException("Cần nhập lý do từ chối", ENTITY_NAME, "rejectionreason");
            }
        }
    }
}
