package com.gindevp.app.service;

import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.domain.enumeration.RepairResolution;
import com.gindevp.app.repository.EquipmentAssignmentRepository;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.repository.RepairRequestRepository;
import com.gindevp.app.service.dto.RepairRequestDTO;
import com.gindevp.app.service.mapper.RepairRequestMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.util.Optional;
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

    private final EquipmentRepository equipmentRepository;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final AppAuditLogService appAuditLogService;

    public RepairRequestService(
        RepairRequestRepository repairRequestRepository,
        RepairRequestMapper repairRequestMapper,
        EquipmentRepository equipmentRepository,
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        CurrentEmployeeService currentEmployeeService,
        AppAuditLogService appAuditLogService
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.repairRequestMapper = repairRequestMapper;
        this.equipmentRepository = equipmentRepository;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.appAuditLogService = appAuditLogService;
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
        RepairRequest repairRequest = repairRequestMapper.toEntity(repairRequestDTO);
        RepairRequestStatus old = RepairRequestStatus.NEW;
        repairRequest = repairRequestRepository.save(repairRequest);
        applyRepairLifecycle(old, repairRequest);
        return repairRequestMapper.toDto(repairRequest);
    }

    public RepairRequestDTO update(RepairRequestDTO repairRequestDTO) {
        LOG.debug("Request to update RepairRequest : {}", repairRequestDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật đầy đủ yêu cầu sửa chữa");
        }
        RepairRequestStatus old = repairRequestRepository
            .findById(repairRequestDTO.getId())
            .map(RepairRequest::getStatus)
            .orElse(RepairRequestStatus.NEW);
        RepairRequest repairRequest = repairRequestMapper.toEntity(repairRequestDTO);
        assertRepairStatusTransition(old, repairRequest.getStatus());
        assertRejectionReasonWhenRejected(old, repairRequest.getStatus(), repairRequest.getRejectionReason());
        repairRequest = repairRequestRepository.save(repairRequest);
        applyRepairLifecycle(old, repairRequest);
        return repairRequestMapper.toDto(repairRequest);
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
                    applyRepairLifecycle(old, saved);
                    return saved;
                })
                .map(repairRequestMapper::toDto);
        }

        return repairRequestRepository
            .findById(repairRequestDTO.getId())
            .map(existingRepairRequest -> {
                RepairRequestStatus old = existingRepairRequest.getStatus();
                repairRequestMapper.partialUpdate(existingRepairRequest, repairRequestDTO);
                assertRepairStatusTransition(old, existingRepairRequest.getStatus());
                assertRejectionReasonWhenRejected(old, existingRepairRequest.getStatus(), existingRepairRequest.getRejectionReason());
                RepairRequest saved = repairRequestRepository.save(existingRepairRequest);
                applyRepairLifecycle(old, saved);
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
        if (saved.getEquipment() == null || saved.getEquipment().getId() == null) {
            return;
        }
        equipmentRepository.findById(saved.getEquipment().getId()).ifPresent(eq -> {
            eq.setStatus(EquipmentOperationalStatus.UNDER_REPAIR);
            equipmentRepository.save(eq);
        });
        LOG.debug("Repair started for equipment on request {}", saved.getId());
    }

    private void applyRepairCompleted(RepairRequest saved) {
        if (saved.getEquipment() == null || saved.getEquipment().getId() == null) {
            return;
        }
        Long eqId = saved.getEquipment().getId();
        Equipment eq = equipmentRepository.findById(eqId).orElse(null);
        if (eq == null) {
            return;
        }
        RepairResolution out = saved.getRepairOutcome() != null ? saved.getRepairOutcome() : RepairResolution.RETURN_USER;
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
            "requestId="
                + saved.getId()
                + " equipmentId="
                + eqId
                + " outcome="
                + out
        );
        LOG.debug("Repair completed for equipment {} outcome {}", eqId, out);
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
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được xóa yêu cầu sửa chữa");
        }
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
        repairRequestRepository.deleteById(id);
    }

    private static boolean employeeRepairContentPatchHasForbiddenFields(RepairRequestDTO d) {
        return (
            d.getCode() != null ||
            d.getRequestDate() != null ||
            d.getRequester() != null ||
            d.getEquipment() != null ||
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
