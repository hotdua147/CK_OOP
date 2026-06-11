package com.library.model;

import com.library.service.policy.LecturerFinePolicy; // Import chính xác chiến lược tính phạt của Giảng viên

/**
 * Lớp thực thể đại diện cho đối tượng độc giả là Giảng viên.
 * Kế thừa từ lớp Reader và áp dụng cấu hình quy định đặc thù:
 * - Số lượng sách mượn tối đa: 10 cuốn.
 * - Phí phạt trễ hạn mặc định: 2.000đ / ngày quá hạn.
 */
public class LecturerReader extends Reader {

    // Định nghĩa các hằng số cấu hình nghiệp vụ riêng của Giảng viên
    private static final int    MAX_BORROW_LIMIT = 10;
    private static final int    FINE_PER_DAY     = 2000;   // 2.000 VND / ngày trễ
    private static final String READER_TYPE      = "Giảng viên";

    /**
     * Constructor khởi tạo tài khoản bạn đọc cho Giảng viên.
     * Tự động liên kết (tiêm) đối tượng chiến lược LecturerFinePolicy vào lớp cha.
     *
     * @param userId      Mã độc giả giảng viên (Định dạng: BDxxx)
     * @param fullName    Họ và tên giảng viên
     * @param phoneNumber Số điện thoại liên lạc
     */
    public LecturerReader(String userId, String fullName, String phoneNumber) {
        // Chuẩn hóa loại bỏ khoảng trắng thừa đầu/cuối chuỗi và truyền sang lớp cha Reader
        super(userId != null ? userId.trim() : "",
                fullName != null ? fullName.trim() : "",
                phoneNumber != null ? phoneNumber.trim() : "",
                new LecturerFinePolicy());
    }

    // ─── Override các phương thức trừu tượng từ lớp cha Reader ────────────────

    /**
     * Lấy giới hạn số lượng sách mượn tối đa cùng lúc của Giảng viên.
     * @return 10 cuốn
     */
    @Override
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }

    /**
     * Lấy tên phân loại hiển thị của độc giả.
     * @return Chuỗi "Giảng viên"
     */
    @Override
    public String getReaderType() {
        return READER_TYPE;
    }

    /**
     * Lấy định mức số tiền phạt quá hạn cơ sở mỗi ngày.
     * Phục vụ cho mục đích hiển thị thông tin tra cứu nhanh.
     * @return 2000
     */
    @Override
    public int getFinePerDay() {
        return FINE_PER_DAY;
    }
}