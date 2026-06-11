package com.library.model;

import com.library.service.policy.NormalStudentFinePolicy; // Import chính xác chiến lược tính phạt của Sinh viên thường

/**
 * Lớp thực thể đại diện cho đối tượng độc giả là Sinh viên thường.
 * Kế thừa từ lớp Reader và áp dụng các cấu hình quy định đặc thù:
 * - Số lượng sách mượn tối đa cùng lúc: 3 cuốn.
 * - Phí phạt trễ hạn mặc định: 5.000đ / ngày quá hạn.
 */
public class StudentReader extends Reader {

    // Định nghĩa các hằng số cấu hình nghiệp vụ riêng của Sinh viên thường
    private static final int    MAX_BORROW_LIMIT = 3;
    private static final int    FINE_PER_DAY     = 5000;   // 5.000 VND / ngày trễ
    private static final String READER_TYPE      = "Sinh viên thường";

    /**
     * Constructor khởi tạo tài khoản bạn đọc cho Sinh viên thường.
     * Tự động liên kết (tiêm) đối tượng chiến lược NormalStudentFinePolicy vào lớp cha.
     *
     * @param userId      Mã độc giả sinh viên (Định dạng: BDxxx)
     * @param fullName    Họ và tên sinh viên
     * @param phoneNumber Số điện thoại liên lạc
     */
    public StudentReader(String userId, String fullName, String phoneNumber) {
        // Chuẩn hóa loại bỏ khoảng trắng thừa đầu/cuối chuỗi và truyền sang lớp cha Reader
        super(userId != null ? userId.trim() : "",
                fullName != null ? fullName.trim() : "",
                phoneNumber != null ? phoneNumber.trim() : "",
                new NormalStudentFinePolicy());
    }

    // ─── Override các phương thức trừu tượng từ lớp cha Reader ────────────────

    /**
     * Lấy giới hạn số lượng sách mượn tối đa cùng lúc của Sinh viên thường.
     * @return 3 cuốn
     */
    @Override
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }

    /**
     * Lấy tên phân loại hiển thị của độc giả.
     * @return Chuỗi "Sinh viên thường"
     */
    @Override
    public String getReaderType() {
        return READER_TYPE;
    }

    /**
     * Lấy định mức số tiền phạt quá hạn cơ sở mỗi ngày.
     * Phục vụ cho mục đích hiển thị tra cứu nhanh trên giao diện.
     * @return 5000
     */
    @Override
    public int getFinePerDay() {
        return FINE_PER_DAY;
    }
}