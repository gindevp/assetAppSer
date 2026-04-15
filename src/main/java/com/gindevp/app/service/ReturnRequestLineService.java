package com.gindevp.app.service;

import com.gindevp.app.domain.ConsumableAssignment;
import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.domain.ReturnRequestLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import com.gindevp.app.repository.ConsumableAssignmentRepository;
import com.gindevp.app.repository.EmployeeRepository;
import com.gindevp.app.repository.ReturnRequestLineRepository;
import com.gindevp.app.repository.ReturnRequestRepository;
import com.gindevp.app.service.dto.ReturnRequestLineDTO;
import com.gindevp.app.service.mapper.ReturnRequestLineMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.ReturnRequestLine}.
 */
@Service
@Transactional
public class ReturnRequestLineService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestLineService.class);

    private static final String ENTITY_NAME = "returnRequestLine";

    private final ReturnRequestLineRepository returnRequestLineRepository;

    private final ReturnRequestLineMapper returnRequestLineMapper;

    private final ReturnRequestRepository returnRequestRepository;

    private final CurrentEmployeeService currentEmployeeService;

    private final EquipmentRepairReturnEligibilityService equipmentRepairReturnEligibilityService;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final EmployeeRepository employeeRepository;

    public ReturnRequestLineService(
        ReturnRequestLineRepository returnRequestLineRepository,
        ReturnRequestLineMapper returnRequestLineMapper,
        ReturnRequestRepository returnRequestRepository,
        CurrentEmployeeService currentEmployeeService,
        EquipmentRepairReturnEligibilityService equipmentRepairReturnEligibilityService,
        ConsumableAssignmentRepository consumableAssignmentRepository,
        EmployeeRepository employeeRepository
    ) {
        this.returnRequestLineRepository = returnRequestLineRepository;
        this.returnRequestLineMapper = returnRequestLineMapper;
        this.returnRequestRepository = returnRequestRepository;
        this.currentEmployeeService = currentEmployeeService;
        this.equipmentRepairReturnEligibilityService = equipmentRepairReturnEligibilityService;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.employeeRepository = employeeRepository;
    }

    private void assertReturnRequestOpen(ReturnRequest rr) {
        ReturnRequestStatus st = rr.getStatus();
        if (
            st == ReturnRequestStatus.COMPLETED || st == ReturnRequestStatus.REJECTED || st == ReturnRequestStatus.CANCELLED
        ) {
            throw new BadRequestAlertException("Yêu cầu thu hồi đã kết thúc — không thao tác dòng", ENTITY_NAME, "requestclosed");
        }
    }

    private ReturnRequest assertCanAddLine(Long returnRequestId) {
        ReturnRequest rr = returnRequestRepository
            .findOneWithEagerRelationships(returnRequestId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
        assertReturnRequestOpen(rr);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
            if (rr.getRequester() == null || !eid.equals(rr.getRequester().getId())) {
                throw new AccessDeniedException("Không phải yêu cầu của bạn");
            }
            if (rr.getStatus() != ReturnRequestStatus.PENDING) {
                throw new BadRequestAlertException("Chỉ thêm dòng khi yêu cầu đang chờ duyệt", ENTITY_NAME, "notpending");
            }
        }
        return rr;
    }

    public ReturnRequestLineDTO save(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to save ReturnRequestLine : {}", returnRequestLineDTO);
        if (returnRequestLineDTO.getRequest() == null || returnRequestLineDTO.getRequest().getId() == null) {
            throw new BadRequestAlertException("Thiếu yêu cầu thu hồi", ENTITY_NAME, "norequest");
        }
        ReturnRequest rr = assertCanAddLine(returnRequestLineDTO.getRequest().getId());
        AssetManagementType lt = returnRequestLineDTO.getLineType() != null
            ? returnRequestLineDTO.getLineType()
            : AssetManagementType.DEVICE;
        if (lt == AssetManagementType.DEVICE) {
            if (returnRequestLineDTO.getEquipment() == null || returnRequestLineDTO.getEquipment().getId() == null) {
                throw new BadRequestAlertException("Thiếu thiết bị", ENTITY_NAME, "noequipment");
            }
            equipmentRepairReturnEligibilityService.assertEquipmentEligibleForReturnLine(
                returnRequestLineDTO.getEquipment().getId(),
                returnRequestLineDTO.getRequest().getId(),
                null
            );
        } else if (lt == AssetManagementType.CONSUMABLE) {
            if (returnRequestLineDTO.getAssetItem() == null || returnRequestLineDTO.getAssetItem().getId() == null) {
                throw new BadRequestAlertException("Thiếu vật tư", ENTITY_NAME, "noassetitem");
            }
            if (returnRequestLineDTO.getQuantity() == null || returnRequestLineDTO.getQuantity() < 1) {
                throw new BadRequestAlertException("Nhập số lượng thu hồi (≥ 1)", ENTITY_NAME, "badqty");
            }
            equipmentRepairReturnEligibilityService.assertConsumableAssetItemEligibleForReturnLine(
                returnRequestLineDTO.getAssetItem().getId(),
                returnRequestLineDTO.getRequest().getId(),
                null
            );
            Long requesterId = rr.getRequester() != null ? rr.getRequester().getId() : null;
            if (requesterId == null) {
                throw new BadRequestAlertException("Thiếu người yêu cầu thu hồi", ENTITY_NAME, "norequester");
            }
            int max = consumableHeldForRequester(requesterId, returnRequestLineDTO.getAssetItem().getId());
            if (returnRequestLineDTO.getQuantity() > max) {
                throw new BadRequestAlertException("Số lượng vượt SL còn giữ (" + max + ")", ENTITY_NAME, "qtyexceed");
            }
        } else {
            throw new BadRequestAlertException("lineType không hợp lệ", ENTITY_NAME, "badlinetype");
        }
        ReturnRequestLine returnRequestLine = returnRequestLineMapper.toEntity(returnRequestLineDTO);
        returnRequestLine = returnRequestLineRepository.save(returnRequestLine);
        return returnRequestLineMapper.toDto(returnRequestLine);
    }

    public ReturnRequestLineDTO update(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to update ReturnRequestLine : {}", returnRequestLineDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được sửa dòng thu hồi");
        }
        ReturnRequestLine existing = returnRequestLineRepository
            .findOneWithEagerRelationships(returnRequestLineDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy dòng thu hồi", ENTITY_NAME, "idnotfound"));
        assertParentReturnRequestOpen(existing);
        AssetManagementType lt = returnRequestLineDTO.getLineType() != null
            ? returnRequestLineDTO.getLineType()
            : (existing.getLineType() != null ? existing.getLineType() : AssetManagementType.DEVICE);
        if (lt == AssetManagementType.DEVICE) {
            if (returnRequestLineDTO.getEquipment() == null || returnRequestLineDTO.getEquipment().getId() == null) {
                throw new BadRequestAlertException("Thiếu thiết bị", ENTITY_NAME, "noequipment");
            }
            equipmentRepairReturnEligibilityService.assertEquipmentEligibleForReturnLine(
                returnRequestLineDTO.getEquipment().getId(),
                existing.getRequest().getId(),
                existing.getId()
            );
        } else if (lt == AssetManagementType.CONSUMABLE) {
            if (returnRequestLineDTO.getAssetItem() == null || returnRequestLineDTO.getAssetItem().getId() == null) {
                throw new BadRequestAlertException("Thiếu vật tư", ENTITY_NAME, "noassetitem");
            }
            if (returnRequestLineDTO.getQuantity() == null || returnRequestLineDTO.getQuantity() < 1) {
                throw new BadRequestAlertException("Nhập số lượng thu hồi (≥ 1)", ENTITY_NAME, "badqty");
            }
            equipmentRepairReturnEligibilityService.assertConsumableAssetItemEligibleForReturnLine(
                returnRequestLineDTO.getAssetItem().getId(),
                existing.getRequest().getId(),
                existing.getId()
            );
            ReturnRequest rr = returnRequestRepository
                .findOneWithEagerRelationships(existing.getRequest().getId())
                .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
            Long requesterId = rr.getRequester() != null ? rr.getRequester().getId() : null;
            if (requesterId == null) {
                throw new BadRequestAlertException("Thiếu người yêu cầu thu hồi", ENTITY_NAME, "norequester");
            }
            int max = consumableHeldForRequester(requesterId, returnRequestLineDTO.getAssetItem().getId());
            if (returnRequestLineDTO.getQuantity() > max) {
                throw new BadRequestAlertException("Số lượng vượt SL còn giữ (" + max + ")", ENTITY_NAME, "qtyexceed");
            }
        } else {
            throw new BadRequestAlertException("lineType không hợp lệ", ENTITY_NAME, "badlinetype");
        }
        ReturnRequestLine returnRequestLine = returnRequestLineMapper.toEntity(returnRequestLineDTO);
        returnRequestLine = returnRequestLineRepository.save(returnRequestLine);
        return returnRequestLineMapper.toDto(returnRequestLine);
    }

    public Optional<ReturnRequestLineDTO> partialUpdate(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to partially update ReturnRequestLine : {}", returnRequestLineDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật dòng thu hồi");
        }

        return returnRequestLineRepository
            .findOneWithEagerRelationships(returnRequestLineDTO.getId())
            .map(existingReturnRequestLine -> {
                assertParentReturnRequestOpen(existingReturnRequestLine);
                returnRequestLineMapper.partialUpdate(existingReturnRequestLine, returnRequestLineDTO);
                AssetManagementType lt =
                    existingReturnRequestLine.getLineType() != null
                        ? existingReturnRequestLine.getLineType()
                        : AssetManagementType.DEVICE;
                if (
                    lt == AssetManagementType.DEVICE &&
                    existingReturnRequestLine.getEquipment() != null &&
                    existingReturnRequestLine.getEquipment().getId() != null &&
                    existingReturnRequestLine.getRequest() != null &&
                    existingReturnRequestLine.getRequest().getId() != null
                ) {
                    equipmentRepairReturnEligibilityService.assertEquipmentEligibleForReturnLine(
                        existingReturnRequestLine.getEquipment().getId(),
                        existingReturnRequestLine.getRequest().getId(),
                        existingReturnRequestLine.getId()
                    );
                } else if (
                    lt == AssetManagementType.CONSUMABLE &&
                    existingReturnRequestLine.getAssetItem() != null &&
                    existingReturnRequestLine.getAssetItem().getId() != null &&
                    existingReturnRequestLine.getRequest() != null &&
                    existingReturnRequestLine.getRequest().getId() != null
                ) {
                    equipmentRepairReturnEligibilityService.assertConsumableAssetItemEligibleForReturnLine(
                        existingReturnRequestLine.getAssetItem().getId(),
                        existingReturnRequestLine.getRequest().getId(),
                        existingReturnRequestLine.getId()
                    );
                    ReturnRequest rr = returnRequestRepository
                        .findOneWithEagerRelationships(existingReturnRequestLine.getRequest().getId())
                        .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
                    Long requesterId = rr.getRequester() != null ? rr.getRequester().getId() : null;
                    if (requesterId == null) {
                        throw new BadRequestAlertException("Thiếu người yêu cầu thu hồi", ENTITY_NAME, "norequester");
                    }
                    int qty = existingReturnRequestLine.getQuantity() != null ? existingReturnRequestLine.getQuantity() : 0;
                    if (qty < 1) {
                        throw new BadRequestAlertException("Nhập số lượng thu hồi (≥ 1)", ENTITY_NAME, "badqty");
                    }
                    int max = consumableHeldForRequester(requesterId, existingReturnRequestLine.getAssetItem().getId());
                    if (qty > max) {
                        throw new BadRequestAlertException("Số lượng vượt SL còn giữ (" + max + ")", ENTITY_NAME, "qtyexceed");
                    }
                }
                return existingReturnRequestLine;
            })
            .map(returnRequestLineRepository::save)
            .map(returnRequestLineMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReturnRequestLineDTO> findAll() {
        LOG.debug("Request to get all ReturnRequestLines");
        return returnRequestLineRepository
            .findAll()
            .stream()
            .map(returnRequestLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ReturnRequestLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return returnRequestLineRepository.findAllWithEagerRelationships(pageable).map(returnRequestLineMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReturnRequestLineDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnRequestLine : {}", id);
        return returnRequestLineRepository.findOneWithEagerRelationships(id).map(returnRequestLineMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ReturnRequestLine : {}", id);
        ReturnRequestLine existing = returnRequestLineRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy dòng thu hồi", ENTITY_NAME, "idnotfound"));
        assertParentReturnRequestOpen(existing);

        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
            if (existing.getRequest() == null || existing.getRequest().getId() == null) {
                throw new BadRequestAlertException("Dòng không gắn phiếu thu hồi", ENTITY_NAME, "norequest");
            }
            ReturnRequest rr = returnRequestRepository
                .findOneWithEagerRelationships(existing.getRequest().getId())
                .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
            if (rr.getRequester() == null || !eid.equals(rr.getRequester().getId())) {
                throw new AccessDeniedException("Không phải yêu cầu của bạn");
            }
            if (rr.getStatus() != ReturnRequestStatus.PENDING) {
                throw new BadRequestAlertException("Chỉ xóa dòng khi yêu cầu đang chờ duyệt", ENTITY_NAME, "notpending");
            }
        }
        returnRequestLineRepository.deleteById(id);
    }

    private void assertParentReturnRequestOpen(ReturnRequestLine line) {
        if (line.getRequest() == null || line.getRequest().getId() == null) {
            return;
        }
        ReturnRequest rr = returnRequestRepository
            .findOneWithEagerRelationships(line.getRequest().getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
        assertReturnRequestOpen(rr);
    }

    /** Tổng SL còn giữ (NV → PB → vị trí → toàn công ty), cùng thứ tự với khi hoàn tất thu hồi. */
    private int consumableHeldForRequester(Long requesterId, Long assetItemId) {
        var emp = employeeRepository.findOneWithToOneRelationships(requesterId);
        Long deptId = emp.map(e -> e.getDepartment() != null ? e.getDepartment().getId() : null).orElse(null);
        Long locId = emp.map(e -> e.getLocation() != null ? e.getLocation().getId() : null).orElse(null);
        int sum = 0;
        sum += sumHeld(consumableAssignmentRepository.findByEmployee_IdAndAssetItem_IdOrderByIdAsc(requesterId, assetItemId));
        if (deptId != null) {
            sum +=
                sumHeld(
                    consumableAssignmentRepository.findByDepartment_IdAndAssetItem_IdAndEmployeeIsNullOrderByIdAsc(deptId, assetItemId)
                );
        }
        if (locId != null) {
            sum +=
                sumHeld(
                    consumableAssignmentRepository.findByLocation_IdAndAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullOrderByIdAsc(
                        locId,
                        assetItemId
                    )
                );
        }
        sum +=
            sumHeld(
                consumableAssignmentRepository.findByAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullAndLocationIsNullOrderByIdAsc(
                    assetItemId
                )
            );
        return sum;
    }

    private static int sumHeld(List<ConsumableAssignment> list) {
        int s = 0;
        for (ConsumableAssignment ca : list) {
            int q = ca.getQuantity() != null ? ca.getQuantity() : 0;
            int r = ca.getReturnedQuantity() != null ? ca.getReturnedQuantity() : 0;
            s += Math.max(0, q - r);
        }
        return s;
    }
}
