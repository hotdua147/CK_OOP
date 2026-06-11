package com.library.service.policy;

// ÉP BUỘC: Import chính xác Interface FinePolicy bản chuẩn mà bạn vừa tạo riêng
import com.library.policy.FinePolicy;

/**
 * Chiến lược tính tiền phạt cho Sinh viên thường
 */
public class NormalStudentFinePolicy implements FinePolicy {
    // Phí phạt mặc định mỗi ngày trễ (Ví dụ: 5000đ/ngày)
    private static final double FINE_PER_DAY = 5000.0;

    @Override
    public double calculateFine(int overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        // Trả về số tiền kiểu double đồng bộ toàn hệ thống
        return (double) overdueDays * FINE_PER_DAY;
    }
}