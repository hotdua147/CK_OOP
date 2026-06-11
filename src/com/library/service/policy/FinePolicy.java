package com.library.service.policy; // ĐÃ SỬA: Khớp chính xác với thư mục vật lý com.library.service.policy

/**
 * Interface định nghĩa chiến lược tính tiền phạt trễ hạn (Strategy Pattern).
 * Đóng vai trò là lớp trừu tượng gốc, cho phép hệ thống linh hoạt thay đổi
 * thuật toán tính toán tiền phạt trễ hạn theo từng loại bạn đọc khác nhau
 * (Sinh viên thường, Sinh viên ưu tiên, Giảng viên) mà không làm ảnh hưởng đến tầng Service.
 */
public interface FinePolicy {

    /**
     * Tính toán số tiền phạt dựa trên số ngày trễ hạn thực tế của phiếu mượn.
     * * @param overdueDays Số ngày bạn đọc quá hạn trả sách (phải lớn hơn hoặc bằng 0)
     * @return Số tiền phí phạt trễ hạn tương ứng kiểu số thực (VND)
     */
    double calculateFine(int overdueDays);
}