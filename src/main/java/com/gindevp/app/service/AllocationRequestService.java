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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private final StockDocumentEventService stockDocumentEventService;

    private final AssetItemRepository assetItemRepository;

    private final AssetLineRepository assetLineRepository;

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
        AppAuditLogService appAuditLogService,
        StockDocumentEventService stockDocumentEventService,
        AssetItemRepository assetItemRepository,
        AssetLineRepository assetLineRepository
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
        this.stockDocumentEventService = stockDocumentEventService;
        this.assetItemRepository = assetItemRepository;
        this.assetLineRepository = assetLineRepository;
    }

    private static final String ENTITY_NAME = "allocationRequest";

    private static String nvl(String s) {
        return s != null ? s : "";
    }

    /** Mã + tên mặt hàng (vật tư) để hiển thị trong thông báo lỗi. */
    private String labelConsumableAssetItem(Long assetItemId) {
        if (assetItemId == null) {
            return "mã vật tư (chưa xác định)";
        }
        return assetItemRepository
            .findById(assetItemId)
            .map(ai -> String.format("%s — %s [id mã=%d]", nvl(ai.getCode()), nvl(ai.getName()), assetItemId))
            .orElse("id mã vật tư = " + assetItemId);
    }

    /** Dòng thiết bị trên phiếu YC (STT + dòng tài sản nếu có). */
    private String labelDeviceRequestLine(AllocationRequestLine line) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dòng ").append(line.getLineNo() != null ? line.getLineNo() : "?");
        if (line.getAssetLine() != null && line.getAssetLine().getId() != null) {
            assetLineRepository.findById(line.getAssetLine().getId()).ifPresent(al -> {
                String nm = al.getName() != null && !al.getName().isBlank() ? al.getName() : nvl(al.getCode());
                sb.append(" – dòng TS: ").append(nm);
            });
        }
        sb.append(" (id dòng YC=").append(line.getId()).append(")");
        return sb.toString();
    }

    private String labelEquipmentBrief(Equipment eq) {
        if (eq == null) {
            return "thiết bị (không rõ)";
        }
        String code = eq.getEquipmentCode() != null ? eq.getEquipmentCode() : "?";
        return String.format("%s [id TB=%d]", code, eq.getId());
    }

    /**
     * Bản ghi tồn kho vật tư theo master — nếu chưa có thì tạo 0/0 để trừ tồn nhất quán (tránh lỗi nostock khi chưa nhập kho đúng luồng).
     */
    private ConsumableStock getOrCreateConsumableStockForItem(Long assetItemId, AssetItem lineAssetItem) {
        return consumableStockRepository.findFirstByAssetItem_Id(assetItemId).orElseGet(() -> {
            AssetItem itemRef =
                lineAssetItem != null && lineAssetItem.getId() != null ? lineAssetItem : new AssetItem().id(assetItemId);
            ConsumableStock row = new ConsumableStock();
            row.setQuantityOnHand(0);
            row.setQuantityIssued(0);
            row.setAssetItem(itemRef);
            row.setNote("Khởi tạo khi hoàn thành cấp phát — trước đó chưa có bản ghi tồn kho");
            LOG.info("Created consumable_stock for assetItemId {} (missing before allocation completion)", assetItemId);
            return consumableStockRepository.save(row);
        });
    }

    /**
     * Khi duyệt / ghi nhận phiếu xuất: bắt buộc đã chọn thiết bị tồn kho (DEVICE) và mã vật tư (CONSUMABLE) cho từng dòng.
     * <p>
     * Đủ tồn vật tư kiểm tra khi chuyển sang {@link AllocationRequestStatus#COMPLETED}.
     */
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
        validateUniqueDeviceEquipmentPerRequest(id);
        validateConsumableLinesHaveAssetItem(id);
    }

    /** Dòng vật tư có SL &gt; 0 phải đã chọn mã mặt hàng (QLTS PATCH assetItem) trước khi duyệt. */
    private void validateConsumableLinesHaveAssetItem(Long allocationRequestId) {
        for (AllocationRequestLine line : allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId)) {
            if (line.getLineType() != AssetManagementType.CONSUMABLE) {
                continue;
            }
            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            if (qty <= 0) {
                continue;
            }
            if (line.getAssetItem() == null || line.getAssetItem().getId() == null) {
                throw new BadRequestAlertException(
                    "Dòng vật tư STT "
                        + (line.getLineNo() != null ? line.getLineNo() : "?")
                        + " (id dòng YC="
                        + line.getId()
                        + "): chưa chọn mã hàng — vào duyệt cấp phát và chọn «Chọn tài sản» (mã vật tư) trước khi duyệt.",
                    ENTITY_NAME,
                    "consumableitemrequired"
                );
            }
        }
    }

    /**
     * Trước khi hoàn thành: đủ tồn kho cho từng mã vật tư (cộng dồn SL các dòng cùng mã trên một phiếu).
     */
    private void validateConsumableLinesSufficientStock(Long allocationRequestId) {
        Map<Long, Integer> demandByAssetItemId = new HashMap<>();
        List<AllocationRequestLine> lines = allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId);
        for (AllocationRequestLine line : lines) {
            if (line.getLineType() != AssetManagementType.CONSUMABLE) {
                continue;
            }
            int qty = line.getQuantity() != null ? line.getQuantity() : 0;
            if (qty <= 0) {
                continue;
            }
            if (line.getAssetItem() == null || line.getAssetItem().getId() == null) {
                throw new BadRequestAlertException(
                    "Dòng vật tư STT "
                        + (line.getLineNo() != null ? line.getLineNo() : "?")
                        + " (id dòng YC="
                        + line.getId()
                        + "): chưa có mã hàng — không thể kiểm tra tồn kho khi hoàn thành.",
                    ENTITY_NAME,
                    "consumableitemrequired"
                );
            }
            Long itemId = line.getAssetItem().getId();
            demandByAssetItemId.merge(itemId, qty, Integer::sum);
        }
        for (Map.Entry<Long, Integer> entry : demandByAssetItemId.entrySet()) {
            Long itemId = entry.getKey();
            int need = entry.getValue();
            String itemLabel = labelConsumableAssetItem(itemId);
            List<ConsumableStock> stocks = consumableStockRepository.findAllByAssetItem_Id(itemId);
            if (stocks.isEmpty()) {
                throw new BadRequestAlertException(
                    "Chưa có bản ghi tồn kho cho vật tư: "
                        + itemLabel
                        + ". Vào Tồn kho vật tư / nhập kho cho mã này trước khi hoàn thành cấp phát.",
                    ENTITY_NAME,
                    "nostock"
                );
            }
            int onHand = stocks.stream().mapToInt(s -> s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0).sum();
            if (onHand < need) {
                throw new BadRequestAlertException(
                    "Không đủ tồn kho vật tư — "
                        + itemLabel
                        + ". Cần xuất: "
                        + need
                        + ", hiện tồn kho: "
                        + onHand
                        + ". Nhập thêm hoặc giảm số lượng trên phiếu.",
                    ENTITY_NAME,
                    "insufficientstock"
                );
            }
        }
    }

    /**
     * Trừ tồn kho vật tư trên một hoặc nhiều bản ghi consumable_stock cùng mã (FIFO theo id).
     */
    private void deductConsumableStockForLine(Long assetItemId, int qty, AssetItem lineAssetItem) {
        List<ConsumableStock> rows = new ArrayList<>(consumableStockRepository.findAllByAssetItem_Id(assetItemId));
        if (rows.isEmpty()) {
            getOrCreateConsumableStockForItem(assetItemId, lineAssetItem);
            rows = new ArrayList<>(consumableStockRepository.findAllByAssetItem_Id(assetItemId));
        }
        int totalOnHand = rows.stream().mapToInt(s -> s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0).sum();
        if (totalOnHand < qty) {
            throw new BadRequestAlertException(
                "Không đủ tồn kho vật tư — "
                    + labelConsumableAssetItem(assetItemId)
                    + ". Cần xuất: "
                    + qty
                    + ", hiện tồn kho: "
                    + totalOnHand
                    + ".",
                ENTITY_NAME,
                "insufficientstock"
            );
        }
        int remaining = qty;
        rows.sort(Comparator.comparing(ConsumableStock::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        for (ConsumableStock st : rows) {
            if (remaining <= 0) {
                break;
            }
            int oh = st.getQuantityOnHand() != null ? st.getQuantityOnHand() : 0;
            if (oh == 0) {
                continue;
            }
            int take = Math.min(oh, remaining);
            st.setQuantityOnHand(oh - take);
            int issued = st.getQuantityIssued() != null ? st.getQuantityIssued() : 0;
            st.setQuantityIssued(issued + take);
            consumableStockRepository.save(st);
            remaining -= take;
        }
    }

    private void validateAllDeviceLinesHaveEquipment(Long allocationRequestId) {
        for (AllocationRequestLine line : allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId)) {
            if (line.getLineType() == AssetManagementType.DEVICE && line.getEquipment() == null) {
                throw new BadRequestAlertException(
                    "Thiếu thiết bị tồn kho — "
                        + labelDeviceRequestLine(line)
                        + ". Vào chi tiết yêu cầu cấp phát, cột «Chọn tài sản», chọn thiết bị còn tồn kho cho từng dòng trước khi hoàn thành.",
                    ENTITY_NAME,
                    "equipmentrequired"
                );
            }
        }
    }

    /**
     * Cùng một thiết bị không được gán cho nhiều dòng DEVICE trong một YC — nếu không, dòng đầu tạo bàn giao
     * rồi dòng sau sẽ gặp {@code equipmentbusy} khó hiểu.
     */
    private void validateUniqueDeviceEquipmentPerRequest(Long allocationRequestId) {
        Set<Long> seen = new HashSet<>();
        for (AllocationRequestLine line : allocationRequestLineRepository.findAllByRequest_Id(allocationRequestId)) {
            if (line.getLineType() != AssetManagementType.DEVICE) {
                continue;
            }
            if (line.getEquipment() == null || line.getEquipment().getId() == null) {
                continue;
            }
            Long eqId = line.getEquipment().getId();
            if (!seen.add(eqId)) {
                String eqPart = equipmentRepository
                    .findById(eqId)
                    .map(this::labelEquipmentBrief)
                    .orElse("id TB=" + eqId);
                throw new BadRequestAlertException(
                    "Trùng thiết bị trên hai dòng trong cùng phiếu — "
                        + eqPart
                        + ". Hãy chọn hai thiết bị (serial/mã) khác nhau.",
                    ENTITY_NAME,
                    "duplicateequipmentline"
                );
            }
        }
    }

    /**
     * Chuyển sang EXPORT_SLIP_CREATED: chỉ từ APPROVED; kiểm tra đủ TB/mã VT và tồn kho (bàn giao + trừ tồn thực hiện ngay sau bước này).
     */
    private void assertExportSlipTransitionAllowed(
        AllocationRequestStatus oldStatus,
        AllocationRequestStatus newStatus,
        Long requestId
    ) {
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
        if (requestId != null) {
            validateAllDeviceLinesHaveEquipment(requestId);
            validateUniqueDeviceEquipmentPerRequest(requestId);
            validateConsumableLinesSufficientStock(requestId);
        }
    }

    /** Hoàn thành: chỉ sau khi đã xuất cấp phát (bàn giao/trừ tồn đã chạy ở bước phiếu xuất). */
    private void assertAllocationCompletionAllowed(AllocationRequestStatus oldStatus, AllocationRequestStatus newStatus) {
        if (newStatus != AllocationRequestStatus.COMPLETED || oldStatus == AllocationRequestStatus.COMPLETED) {
            return;
        }
        if (oldStatus != AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            throw new BadRequestAlertException(
                "Chỉ được hoàn thành sau khi đã ghi nhận xuất cấp phát (Đã tạo phiếu xuất). Không hoàn thành trực tiếp từ Đã duyệt.",
                ENTITY_NAME,
                "notready"
            );
        }
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
     * Ghi nhận bàn giao thiết bị / trừ tồn vật tư khi chuyển sang EXPORT_SLIP_CREATED (cùng lúc với phiếu xuất).
     */
    private void applyAllocationPhysicalIssue(Long allocationRequestId) {
        AllocationRequest ar = allocationRequestRepository.findOneWithEagerRelationships(allocationRequestId).orElse(null);
        if (ar == null || ar.getStatus() != AllocationRequestStatus.EXPORT_SLIP_CREATED) {
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
        validateUniqueDeviceEquipmentPerRequest(allocationRequestId);
        for (AllocationRequestLine line : lines) {
            if (line.getLineType() == AssetManagementType.DEVICE) {
                if (line.getEquipment() == null || line.getEquipment().getId() == null) {
                    continue;
                }
                Long eqId = line.getEquipment().getId();
                Equipment eq = equipmentRepository
                    .findById(eqId)
                    .orElseThrow(() ->
                        new BadRequestAlertException(
                            "Không tìm thấy thiết bị id TB="
                                + eqId
                                + " trên "
                                + labelDeviceRequestLine(line)
                                + " — dữ liệu có thể đã bị xóa hoặc sai id.",
                            ENTITY_NAME,
                            "eqnotfound"
                        )
                    );
                Optional<EquipmentAssignment> existingOpen = equipmentAssignmentRepository.findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(
                    eqId
                );
                EquipmentAssignment ea = existingOpen.orElse(null);
                if (ea != null) {
                    String existingNote = ea.getNote();
                    if (existingNote != null && existingNote.contains(noteSuffix)) {
                        LOG.debug(
                            "Equipment {} already has open assignment for this allocation (note contains {}) — idempotent skip",
                            eqId,
                            noteSuffix
                        );
                        continue;
                    }
                    // Đóng mọi bàn giao mở (kể cả IN_USE: thường là bàn giao cũ chưa đóng trả / lệch với trạng thái TB)
                    while (true) {
                        Optional<EquipmentAssignment> open = equipmentAssignmentRepository.findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(
                            eqId
                        );
                        EquipmentAssignment a = open.orElse(null);
                        if (a == null) {
                            break;
                        }
                        a.setReturnedDate(today);
                        equipmentAssignmentRepository.save(a);
                        equipmentAssignmentRepository.flush();
                        LOG.warn(
                            "Closed open equipment_assignment id={} for equipment id={} during allocation completion",
                            a.getId(),
                            eqId
                        );
                    }
                    eq = equipmentRepository
                        .findById(eqId)
                        .orElseThrow(() ->
                            new BadRequestAlertException(
                                "Không tìm thấy thiết bị id TB="
                                    + eqId
                                    + " (sau khi xử lý bàn giao) — "
                                    + labelDeviceRequestLine(line),
                                ENTITY_NAME,
                                "eqnotfound"
                            )
                        );
                    if (eq.getStatus() == EquipmentOperationalStatus.IN_USE) {
                        eq.setStatus(EquipmentOperationalStatus.IN_STOCK);
                        equipmentRepository.save(eq);
                        LOG.warn(
                            "Normalized equipment id={} from IN_USE to IN_STOCK after closing open assignments (allocation completion)",
                            eqId
                        );
                    }
                }
                if (eq.getStatus() != EquipmentOperationalStatus.IN_STOCK) {
                    throw new BadRequestAlertException(
                        labelDeviceRequestLine(line)
                            + ": thiết bị "
                            + labelEquipmentBrief(eq)
                            + " không ở trạng thái tồn kho (IN_STOCK); hiện tại: "
                            + eq.getStatus()
                            + ". Kiểm tra bàn giao mở / trạng thái thiết bị trước khi hoàn thành.",
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
                deductConsumableStockForLine(itemId, qty, line.getAssetItem());

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
            "ALLOCATION_EXPORT_PHYSICAL",
            "requestId=" + allocationRequestId + " code=" + code
        );
        LOG.debug("Applied allocation physical issue (export slip) for request {}", allocationRequestId);
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
        if (issue.getId() != null) {
            stockDocumentEventService.record(
                StockDocumentEventService.DOC_ISSUE,
                issue.getId(),
                "CREATE",
                "Tạo phiếu xuất " + issue.getCode() + " (theo YC cấp phát)",
                "allocationRequestId=" + allocationRequestId + ", status=" + issue.getStatus()
            );
        }

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
        assertExportSlipTransitionAllowed(oldStatus, allocationRequest.getStatus(), id);
        assertAllocationCompletionAllowed(oldStatus, allocationRequest.getStatus());
        validateDeviceLinesPickedIfApproving(allocationRequest);
        assertRejectionReasonWhenRejected(oldStatus, allocationRequest.getStatus(), allocationRequest.getRejectionReason());
        final AllocationRequest savedAr = allocationRequestRepository.save(allocationRequest);
        if (oldStatus == AllocationRequestStatus.APPROVED && savedAr.getStatus() == AllocationRequestStatus.EXPORT_SLIP_CREATED) {
            ensureStockIssueForExportSlip(savedAr.getId());
            applyAllocationPhysicalIssue(savedAr.getId());
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
                        if (employeeAllocationCancelHasExtraFields(allocationRequestDTO)) {
                            throw new AccessDeniedException("Khi hủy chỉ gửi id và status CANCELLED");
                        }
                        if (existingAllocationRequest.getStatus() != AllocationRequestStatus.PENDING) {
                            throw new BadRequestAlertException("Chỉ hủy khi đang chờ duyệt", ENTITY_NAME, "notpending");
                        }
                    } else {
                        if (existingAllocationRequest.getStatus() != AllocationRequestStatus.PENDING) {
                            throw new BadRequestAlertException("Chỉ sửa khi đang chờ duyệt", ENTITY_NAME, "notpending");
                        }
                        if (employeeAllocationContentPatchHasForbiddenFields(allocationRequestDTO)) {
                            throw new AccessDeniedException(
                                "Chỉ được sửa lý do, ghi chú đính kèm, ghi chú thêm (người nhận)"
                            );
                        }
                    }
                }
                AllocationRequestStatus oldStatus = existingAllocationRequest.getStatus();
                allocationRequestMapper.partialUpdate(existingAllocationRequest, allocationRequestDTO);
                defaultAssigneeFields(existingAllocationRequest);
                assertBeneficiaryStructure(existingAllocationRequest);
                assertExportSlipTransitionAllowed(oldStatus, existingAllocationRequest.getStatus(), existingAllocationRequest.getId());
                assertAllocationCompletionAllowed(oldStatus, existingAllocationRequest.getStatus());
                validateDeviceLinesPickedIfApproving(existingAllocationRequest);
                assertRejectionReasonWhenRejected(
                    oldStatus,
                    existingAllocationRequest.getStatus(),
                    existingAllocationRequest.getRejectionReason()
                );
                AllocationRequest saved = allocationRequestRepository.save(existingAllocationRequest);
                if (oldStatus == AllocationRequestStatus.APPROVED && saved.getStatus() == AllocationRequestStatus.EXPORT_SLIP_CREATED) {
                    ensureStockIssueForExportSlip(saved.getId());
                    applyAllocationPhysicalIssue(saved.getId());
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
        AllocationRequest ar = allocationRequestRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu cấp phát", ENTITY_NAME, "notfound"));
        AllocationRequestStatus s = ar.getStatus();
        if (
            s != AllocationRequestStatus.PENDING &&
            s != AllocationRequestStatus.REJECTED &&
            s != AllocationRequestStatus.CANCELLED
        ) {
            throw new BadRequestAlertException(
                "Chỉ xóa yêu cầu ở trạng thái chờ duyệt / từ chối / đã hủy",
                ENTITY_NAME,
                "baddeletestatus"
            );
        }
        if (ar.getStockIssue() != null) {
            throw new BadRequestAlertException("Đã có phiếu xuất kho — không xóa được", ENTITY_NAME, "hasstockissue");
        }
        allocationRequestLineRepository.deleteAll(allocationRequestLineRepository.findAllByRequest_Id(id));
        allocationRequestRepository.deleteById(id);
    }

    private static boolean employeeAllocationCancelHasExtraFields(AllocationRequestDTO d) {
        return (
            d.getReason() != null ||
            d.getAttachmentNote() != null ||
            d.getBeneficiaryNote() != null ||
            d.getAssigneeType() != null ||
            d.getBeneficiaryEmployee() != null ||
            d.getBeneficiaryDepartment() != null ||
            d.getBeneficiaryLocation() != null ||
            d.getRequestDate() != null ||
            d.getCode() != null ||
            d.getRequester() != null ||
            d.getStockIssueId() != null ||
            d.getStockIssueCode() != null ||
            d.getRejectionReason() != null
        );
    }

    private static boolean employeeAllocationContentPatchHasForbiddenFields(AllocationRequestDTO d) {
        return (
            d.getCode() != null ||
            d.getRequestDate() != null ||
            d.getRequester() != null ||
            d.getAssigneeType() != null ||
            d.getBeneficiaryEmployee() != null ||
            d.getBeneficiaryDepartment() != null ||
            d.getBeneficiaryLocation() != null ||
            d.getStockIssueId() != null ||
            d.getStockIssueCode() != null ||
            d.getStatus() != null ||
            d.getRejectionReason() != null
        );
    }

    private static void assertRejectionReasonWhenRejected(
        AllocationRequestStatus oldStatus,
        AllocationRequestStatus newStatus,
        String rejectionReason
    ) {
        if (oldStatus == AllocationRequestStatus.PENDING && newStatus == AllocationRequestStatus.REJECTED) {
            if (rejectionReason == null || rejectionReason.isBlank()) {
                throw new BadRequestAlertException("Cần nhập lý do từ chối", ENTITY_NAME, "rejectionreason");
            }
        }
    }
}
