package com.library.model;

import com.library.service.policy.PriorityStudentFinePolicy; // ĐÃ SỬA: Import chính xác chiến lược phạt riêng của SV ưu tiên

/**
 * Lớp thực thể đại diện cho loại bạn đọc là Sinh viên ưu tiên, kế thừa từ lớp Reader.
 * Áp dụng cấu hình quy định đặc thù hệ thống:
 * - Số lượng sách được mượn tối đa cùng lúc: 5 cuốn.
 * - Phí phạt trễ hạn mặc định: 3.000đ / ngày quá hạn.
 */
public class PriorityStudentReader extends Reader {

    // Định nghĩa các hằng số cấu hình nghiệp vụ riêng của Sinh viên ưu tiên
    private static final int    MAX_BORROW_LIMIT = 5;
    private static final int    FINE_PER_DAY     = 3000;   // 3.000 VND / ngày trễ
    private static final String READER_TYPE      = "Sinh viên ưu tiên";

    /**
     * Constructor khởi tạo tài khoản bạn đọc cho Sinh viên ưu tiên.
     * ĐÃ SỬA: Tiêm (Inject) chính xác đối tượng chiến lược PriorityStudentFinePolicy vào lớp cha.
     *
     * @param userId      Mã độc giả sinh viên ưu tiên (Định dạng chuẩn: BDxxx)
     * @param fullName    Họ và tên sinh viên
     * @param phoneNumber Số điện thoại liên lạc
     */
    public PriorityStudentReader(String userId, String fullName, String phoneNumber) {
        // Chuẩn hóa loại bỏ khoảng trắng thừa đầu/cuối chuỗi và truyền sang lớp cha Reader
        super(userId != null ? userId.trim() : "",
                fullName != null ? fullName.trim() : "",
                phoneNumber != null ? phoneNumber.trim() : "",
                new PriorityStudentFinePolicy());
    }

    // ─── Override các phương thức trừu tượng từ lớp cha Reader ────────────────

    /**
     * Lấy giới hạn số lượng sách mượn tối đa cùng lúc của Sinh viên ưu tiên.
     * @return 5 cuốn
     */
    @Override
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }

    /**
     * Lấy tên phân loại hiển thị của độc giả.
     * @return Chuỗi "Sinh viên ưu tiên"
     */
    @Override
    public String getReaderType() {
        return READER_TYPE;
    }

    /**
     * Lấy định mức số tiền phạt quá hạn cơ sở mỗi ngày để phục vụ hiển thị tra cứu.
     * @return 3000
     */
    @Override
    public int getFinePerDay() {
        return FINE_PER_DAY;
    }
}