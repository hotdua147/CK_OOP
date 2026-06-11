package com.library.service.policy;

/**
 * Chiến lược tính tiền phạt trễ hạn áp dụng riêng cho Sinh viên thường (Strategy Pattern).
 * Đã loại bỏ import thừa do interface FinePolicy đã được chuyển về chung package.
 */
public class NormalStudentFinePolicy implements FinePolicy {

    // Phí phạt mặc định cho sinh viên thường: 5.000đ / ngày quá hạn
    private static final double FINE_PER_DAY = 5000.0;

    /**
     * Tính toán số tiền phạt trễ hạn cho Sinh viên thường.
     * Quy định: 5.000đ nhân với số ngày quá hạn thực tế.
     *
     * @param overdueDays Số ngày bạn đọc quá hạn trả sách
     * @return Số tiền phạt tương ứng kiểu số thực (VND)
     */
    @Override
    public double calculateFine(int overdueDays) {
        // Nếu trả đúng hạn hoặc sớm hơn hạn quy định (số ngày trễ <= 0) thì không tính phạt
        if (overdueDays <= 0) {
            return 0.0;
        }

        // Trả về số tiền phạt tương ứng theo đúng kiểu double đồng bộ toàn hệ thống
        return (double) overdueDays * FINE_PER_DAY;
    }
}