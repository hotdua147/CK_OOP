package com.library.utils;

import java.util.List;

/**
 * Tự động sinh mã ID tăng dần cho Bạn đọc, Sách, Phiếu mượn.
 *
 * Ví dụ:
 *   generateReaderId(...)       → BD001, BD002, BD003 ...
 *   generateBookId(...)         → S001, S002, S003 ...
 *   generateBorrowTicketId(...) → PM001, PM002, PM003 ...
 */
public class IdGenerator {

    // Prefix cho từng loại đối tượng
    private static final String PREFIX_READER  = "BD";
    private static final String PREFIX_BOOK    = "S";
    private static final String PREFIX_TICKET  = "PM";

    // ─── Public factory methods ────────────────────────────────────────────────

    /**
     * Sinh mã bạn đọc mới (BD001, BD002, ...).
     *
     * @param existingIds danh sách mã đã có trong hệ thống
     * @return mã mới chưa bị trùng
     */
    public static String generateReaderId(List<String> existingIds) {
        return generateId(PREFIX_READER, existingIds);
    }

    /**
     * Sinh mã sách mới (S001, S002, ...).
     *
     * @param existingIds danh sách mã đã có trong hệ thống
     * @return mã mới chưa bị trùng
     */
    public static String generateBookId(List<String> existingIds) {
        return generateId(PREFIX_BOOK, existingIds);
    }

    /**
     * Sinh mã phiếu mượn mới (PM001, PM002, ...).
     *
     * @param existingIds danh sách mã đã có trong hệ thống
     * @return mã mới chưa bị trùng
     */
    public static String generateBorrowTicketId(List<String> existingIds) {
        return generateId(PREFIX_TICKET, existingIds);
    }

    // ─── Core logic ───────────────────────────────────────────────────────────

    /**
     * Sinh ID tăng dần theo prefix.
     * Tìm số lớn nhất trong danh sách hiện có, rồi cộng thêm 1.
     *
     * @param prefix      tiền tố (BD, S, PM, ...)
     * @param existingIds danh sách mã hiện có
     * @return ID mới dạng PREFIX + số 3 chữ số (vd: BD003)
     */
    private static String generateId(String prefix, List<String> existingIds) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.toUpperCase().startsWith(prefix.toUpperCase())) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length()));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                    // Bỏ qua mã không theo format chuẩn
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
