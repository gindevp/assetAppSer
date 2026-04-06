package com.gindevp.app.domain.enumeration;

/**
 * Loại báo mất: thiết bị từng chiếc hoặc vật tư theo số lượng trên bàn giao.
 */
public enum LossReportKind {
    EQUIPMENT,
    CONSUMABLE,
    /** Một phiếu gồm nhiều dòng thiết bị / vật tư (JSON trong loss_entries_json). */
    COMBINED,
}
