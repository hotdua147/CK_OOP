package com.library.utils;

/**
 * Các hàm tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào.
 *
 * Chức năng:
 * - Kiểm tra chuỗi rỗng / null
 * - Kiểm tra số điện thoại Việt Nam hợp lệ
 */
public class ValidationUtils {

    // Không cho phép khởi tạo (utility class)
    private ValidationUtils() {}

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
     * Kiểm tra số điện thoại Việt Nam hợp lệ.
     *
     * Hỗ trợ các đầu số hiện tại (10 chữ số):
     *   03x (Viettel), 05x (Vietnamobile/Gmobile),
     *   07x (Mobifone), 08x (Vinaphone/Viettel), 09x (tất cả nhà mạng)
     *
     * @param phone số điện thoại cần kiểm tra
     * @return true nếu hợp lệ
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        // Regex: bắt đầu bằng 03/05/07/08/09, theo sau là 8 chữ số
        return phone.trim().matches("^(03|05|07|08|09)\\d{8}$");
    }
}