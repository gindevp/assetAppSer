package com.gindevp.app.domain.enumeration;

/**
 * Hướng xử lý khi thu hồi (thiết bị / vật tư).
 */
public enum ReturnDisposition {
    /** Về kho sử dụng được */
    TO_STOCK,
    /** Chuyển sửa chữa */
    TO_REPAIR,
    /** Hỏng */
    BROKEN,
    /** Mất */
    LOST,
}
