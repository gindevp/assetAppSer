package com.gindevp.app.service;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.repository.RepairRequestRepository;
import com.gindevp.app.repository.ReturnRequestLineRepository;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Đảm bảo trên một thiết bị không đồng thời có yêu cầu sửa chữa đang mở và thu hồi đang mở.
 */
@Service
public class EquipmentRepairReturnEligibilityService {

    private static final String ENTITY_REPAIR = "repairRequest";
    private static final String ENTITY_RETURN_LINE = "returnRequestLine";

    private static final Collection<RepairRequestStatus> OPEN_REPAIR_STATUSES = List.of(
        RepairRequestStatus.NEW,
        RepairRequestStatus.ACCEPTED,
        RepairRequestStatus.IN_PROGRESS
    );

    private static final Collection<ReturnRequestStatus> OPEN_RETURN_STATUSES = List.of(
        ReturnRequestStatus.PENDING,
        ReturnRequestStatus.APPROVED
    );

    private final RepairRequestRepository repairRequestRepository;
    private final ReturnRequestLineRepository returnRequestLineRepository;
    private final EquipmentRepository equipmentRepository;
    private final AssetItemRepository assetItemRepository;

    public EquipmentRepairReturnEligibilityService(
        RepairRequestRepository repairRequestRepository,
        ReturnRequestLineRepository returnRequestLineRepository,
        EquipmentRepository equipmentRepository,
        AssetItemRepository assetItemRepository
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.returnRequestLineRepository = returnRequestLineRepository;
        this.equipmentRepository = equipmentRepository;
        this.assetItemRepository = assetItemRepository;
    }

    /** Hiển thị trong thông báo lỗi: mã TB + danh mục (nếu có). */
    public String formatEquipmentRef(Long equipmentId) {
        if (equipmentId == null) {
            return "thiết bị (chưa rõ mã)";
        }
        return equipmentRepository
            .findOneWithEagerRelationships(equipmentId)
            .map(this::describeEquipment)
            .orElseGet(() -> "thiết bị id=" + equipmentId);
    }

    /** Hiển thị trong thông báo lỗi: mã vật tư + tên. */
    public String formatAssetItemRef(Long assetItemId) {
        if (assetItemId == null) {
            return "vật tư (chưa rõ mã)";
        }
        return assetItemRepository
            .findById(assetItemId)
            .map(this::describeAssetItem)
            .orElseGet(() -> "vật tư id=" + assetItemId);
    }

    private String describeEquipment(Equipment e) {
        String code = e.getEquipmentCode() != null ? e.getEquipmentCode().trim() : "?";
        AssetItem ai = e.getAssetItem();
        if (ai != null) {
            String nm = ai.getName() != null ? ai.getName().trim() : "";
            if (!nm.isEmpty()) {
                return "mã TB " + code + " — " + nm;
            }
            String ic = ai.getCode() != null ? ai.getCode().trim() : "";
            if (!ic.isEmpty()) {
                return "mã TB " + code + " — danh mục " + ic;
            }
        }
        return "mã TB " + code;
    }

    private String describeAssetItem(AssetItem ai) {
        String code = ai.getCode() != null ? ai.getCode().trim() : "?";
        String nm = ai.getName() != null ? ai.getName().trim() : "";
        if (!nm.isEmpty()) {
            return "mã " + code + " — " + nm;
        }
        return "mã " + code;
    }

    public void assertEquipmentEligibleForRepair(Collection<Long> equipmentIds, Long excludeRepairRequestId) {
        for (Long eqId : equipmentIds) {
            if (
                repairRequestRepository.existsActiveRepairForEquipmentExcluding(eqId, excludeRepairRequestId, OPEN_REPAIR_STATUSES)
            ) {
                throw new BadRequestAlertException(
                    "Thiết bị ("
                        + formatEquipmentRef(eqId)
                        + ") đã có yêu cầu sửa chữa chưa hoàn tất (Mới / Đã nhận / Đang sửa) — không tạo thêm phiếu cho cùng thiết bị.",
                    ENTITY_REPAIR,
                    "repairinprogress"
                );
            }
            if (
                returnRequestLineRepository.existsOpenReturnLineForEquipmentExcluding(
                    eqId,
                    null,
                    OPEN_RETURN_STATUSES,
                    AssetManagementType.DEVICE
                )
            ) {
                throw new BadRequestAlertException(
                    "Thiết bị ("
                        + formatEquipmentRef(eqId)
                        + ") đang có phiếu thu hồi chưa kết thúc (chờ duyệt hoặc đã duyệt) — không tạo thêm yêu cầu sửa chữa cho cùng thiết bị. Hoàn tất / từ chối / hủy phiếu thu hồi hoặc bỏ chọn thiết bị này.",
                    ENTITY_REPAIR,
                    "returnblocksrepair"
                );
            }
        }
    }

    /**
     * @param excludeReturnRequestLineId khi sửa dòng có sẵn — bỏ qua trùng với chính dòng đó
     */
    public void assertEquipmentEligibleForReturnLine(Long equipmentId, Long returnRequestId, Long excludeReturnRequestLineId) {
        if (excludeReturnRequestLineId == null) {
            if (returnRequestLineRepository.existsByRequest_IdAndEquipment_Id(returnRequestId, equipmentId)) {
                throw new BadRequestAlertException(
                    "Thiết bị (" + formatEquipmentRef(equipmentId) + ") đã có trên phiếu thu hồi này — không thêm trùng.",
                    ENTITY_RETURN_LINE,
                    "duplicateline"
                );
            }
        } else {
            if (
                returnRequestLineRepository.existsByRequest_IdAndEquipment_IdAndIdNot(
                    returnRequestId,
                    equipmentId,
                    excludeReturnRequestLineId
                )
            ) {
                throw new BadRequestAlertException(
                    "Thiết bị (" + formatEquipmentRef(equipmentId) + ") đã có trên phiếu thu hồi này — không thêm trùng.",
                    ENTITY_RETURN_LINE,
                    "duplicateline"
                );
            }
        }
        if (
            returnRequestLineRepository.existsOpenReturnLineForEquipmentExcluding(
                equipmentId,
                returnRequestId,
                OPEN_RETURN_STATUSES,
                AssetManagementType.DEVICE
            )
        ) {
            throw new BadRequestAlertException(
                "Thiết bị ("
                    + formatEquipmentRef(equipmentId)
                    + ") đã có trên phiếu thu hồi khác chưa kết thúc (chờ duyệt hoặc đã duyệt) — không thêm cùng thiết bị vào phiếu này.",
                ENTITY_RETURN_LINE,
                "otheropenreturn"
            );
        }
        if (repairRequestRepository.existsActiveRepairForEquipmentExcluding(equipmentId, null, OPEN_REPAIR_STATUSES)) {
            throw new BadRequestAlertException(
                "Thiết bị ("
                    + formatEquipmentRef(equipmentId)
                    + ") đang có yêu cầu sửa chữa chưa hoàn tất — không thêm vào thu hồi cho đến khi xử lý xong phiếu sửa chữa.",
                ENTITY_RETURN_LINE,
                "repairblocksreturn"
            );
        }
    }

    public void assertConsumableAssetItemsEligibleForRepair(Collection<Long> assetItemIds, Long excludeRepairRequestId) {
        for (Long itemId : assetItemIds) {
            if (
                repairRequestRepository.existsActiveRepairForAssetItemExcluding(
                    itemId,
                    excludeRepairRequestId,
                    OPEN_REPAIR_STATUSES,
                    AssetManagementType.CONSUMABLE
                )
            ) {
                throw new BadRequestAlertException(
                    "Vật tư ("
                        + formatAssetItemRef(itemId)
                        + ") đã có yêu cầu sửa chữa chưa hoàn tất — không tạo thêm phiếu cho cùng mặt hàng.",
                    ENTITY_REPAIR,
                    "repairinprogressconsumable"
                );
            }
        }
    }

    /**
     * @param excludeReturnRequestLineId khi sửa dòng có sẵn — bỏ qua trùng với chính dòng đó
     */
    public void assertConsumableAssetItemEligibleForReturnLine(
        Long assetItemId,
        Long returnRequestId,
        Long excludeReturnRequestLineId
    ) {
        if (excludeReturnRequestLineId == null) {
            if (returnRequestLineRepository.existsByRequest_IdAndAssetItem_IdAndLineType(returnRequestId, assetItemId, AssetManagementType.CONSUMABLE)) {
                throw new BadRequestAlertException(
                    "Vật tư (" + formatAssetItemRef(assetItemId) + ") đã có trên phiếu thu hồi này — không thêm trùng.",
                    ENTITY_RETURN_LINE,
                    "duplicatelineconsumable"
                );
            }
        } else {
            if (
                returnRequestLineRepository.existsByRequest_IdAndAssetItem_IdAndLineTypeAndIdNot(
                    returnRequestId,
                    assetItemId,
                    AssetManagementType.CONSUMABLE,
                    excludeReturnRequestLineId
                )
            ) {
                throw new BadRequestAlertException(
                    "Vật tư (" + formatAssetItemRef(assetItemId) + ") đã có trên phiếu thu hồi này — không thêm trùng.",
                    ENTITY_RETURN_LINE,
                    "duplicatelineconsumable"
                );
            }
        }
        // Cho phép cùng mặt hàng xuất hiện trên nhiều phiếu thu hồi mở.
        // Giới hạn số lượng còn khả dụng được kiểm soát ở ReturnRequestLineService (consumableHeldForRequester)
        // và FE đã trừ pending theo từng phiếu.
        if (
            repairRequestRepository.existsActiveRepairForAssetItemExcluding(assetItemId, null, OPEN_REPAIR_STATUSES, AssetManagementType.CONSUMABLE)
        ) {
            throw new BadRequestAlertException(
                "Vật tư ("
                    + formatAssetItemRef(assetItemId)
                    + ") đang có yêu cầu sửa chữa chưa hoàn tất — không thêm vào thu hồi cho đến khi xử lý xong phiếu sửa chữa.",
                ENTITY_RETURN_LINE,
                "repairblocksreturnconsumable"
            );
        }
    }
}
