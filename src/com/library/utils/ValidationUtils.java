package com.library.utils;

import java.util.regex.Pattern;

/**
 * Các hàm tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào.
 * NÂNG CẤP: Sử dụng Compiled Pattern để tối ưu hiệu năng và bổ sung các ràng buộc định dạng ID hệ thống.
 */
public final class ValidationUtils {

    // Biên dịch sẵn các biểu thức chính quy (Regex) dưới dạng hằng số để tối ưu hiệu năng bộ nhớ
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(03|05|07|08|09)\\d{8}$");
    private static final Pattern READER_ID_PATTERN = Pattern.compile("^BD\\d{3}$");
    private static final Pattern BOOK_ID_PATTERN = Pattern.compile("^B\\d{3}$");
    private static final Pattern TICKET_ID_PATTERN = Pattern.compile("^PT\\d{3}$");

    // Không cho phép khởi tạo (Utility Class Pattern)
    private ValidationUtils() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích kiểm tra dữ liệu, không được phép khởi tạo đối tượng!");
    }

    // ─── Chuỗi ────────────────────────────────────────────────────────────────

    /**
     * Kiểm tra chuỗi có rỗng hoặc null không.
     *
     * @param s chuỗi cần kiểm tra
     * @return true nếu null hoặc chỉ gồm khoảng trắng
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ─── Số điện thoại ────────────────────────────────────────────────────────

    /**
     * Kiểm tra số điện thoại Việt Nam hợp lệ (10 chữ số).
     * Hỗ trợ các đầu số hiện tại: 03x, 05x, 07x, 08x, 09x
     *
     * @param phone số điện thoại cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // ─── Định dạng Mã Hệ thống (Mở rộng cải tiến) ─────────────────────────────

    /**
     * Kiểm tra mã bạn đọc có tuân thủ định dạng chuẩn BDxxx hay không (Ví dụ: BD001)
     * * @param readerId mã bạn đọc cần kiểm tra
     * @return true nếu đúng định dạng
     */
    public static boolean isValidReaderId(String readerId) {
        if (isEmpty(readerId)) return false;
        return READER_ID_PATTERN.matcher(readerId.trim().toUpperCase()).matches();
    }

    /**
     * Kiểm tra mã sách có tuân thủ định dạng chuẩn Bxxx hay không (Ví dụ: B001)
     * * @param bookId mã sách cần kiểm tra
     * @return true nếu đúng định dạng
     */
    public static boolean isValidBookId(String bookId) {
        if (isEmpty(bookId)) return false;
        return BOOK_ID_PATTERN.matcher(bookId.trim().toUpperCase()).matches();
    }

    /**
     * Kiểm tra mã phiếu mượn có tuân thủ định dạng chuẩn PTxxx hay không (Ví dụ: PT001)
     * * @param ticketId mã phiếu mượn cần kiểm tra
     * @return true nếu đúng định dạng
     */
    public static boolean isValidTicketId(String ticketId) {
        if (isEmpty(ticketId)) return false;
        return TICKET_ID_PATTERN.matcher(ticketId.trim().toUpperCase()).matches();
    }
}