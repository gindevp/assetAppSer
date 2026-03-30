package com.gindevp.app.service.util;

/** Định dạng hiển thị mã theo tài liệu (PREFIX + gạch + 6 số). */
public final class CodeDisplayUtils {

    private CodeDisplayUtils() {}

    /** EQ000001 → EQ-000001 */
    public static String formatEquipmentCode(String code) {
        if (code == null || code.isBlank()) {
            return "—";
        }
        String s = code.trim();
        if (s.matches("(?i)[A-Za-z]{2,8}\\d{6}")) {
            int split = s.length() - 6;
            return s.substring(0, split).toUpperCase() + "-" + s.substring(split);
        }
        return s;
    }
}
