package com.library.service.policy;

/**
 * Chiến lược tính tiền phạt trễ hạn áp dụng riêng cho đối tượng Giảng viên (Strategy Pattern).
 * Đã loại bỏ import thừa để đồng bộ đồng nhất package toàn hệ thống.
 */
public class LecturerFinePolicy implements FinePolicy {

    // Phí phạt mặc định của Giảng viên: 2.000đ / ngày quá hạn
    private static final double FINE_PER_DAY = 2000.0;

    /**
     * Tính toán số tiền phạt trễ hạn cho đối tượng Giảng viên.
     * Quy định: 2.000đ nhân với số ngày quá hạn thực tế.
     *
     * @param overdueDays Số ngày bạn đọc quá hạn trả sách
     * @return Số tiền phạt tương ứng (VND)
     */
    @Override
    public double calculateFine(int overdueDays) {
        // Nếu trả sách đúng hạn hoặc sớm hạn (số ngày trễ <= 0) thì không phạt tiền
        if (overdueDays <= 0) {
            return 0.0;
        }

        // Trả về số tiền phạt tương ứng theo kiểu số thực double
        return (double) overdueDays * FINE_PER_DAY;
    }
}