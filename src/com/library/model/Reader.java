package com.library.model;

import com.library.policy.FinePolicy;

/**
 * Lớp trừu tượng đại diện cho Bạn đọc thư viện, kế thừa từ User.
 */
public abstract class Reader extends User {
    // TÍCH HỢP: Khớp nối với Strategy Pattern tính tiền phạt của Thành viên 5
    protected FinePolicy finePolicy;

    // ─── Constructor ───────────────────────────────────────────────────────────
    public Reader(String userId, String fullName, String phoneNumber, FinePolicy finePolicy) {
        super(userId, fullName, phoneNumber);
        this.finePolicy = finePolicy;
    }

    // Getter để tầng Service có thể lấy chiến lược tính phạt ra chạy
    public FinePolicy getFinePolicy() {
        return finePolicy;
    }

    // ─── Abstract methods (các lớp con bắt buộc override) ─────────────────────
    public abstract int getMaxBorrowLimit();
    public abstract String getReaderType();

    // Tận dụng hàm cũ của bạn: Vẫn có thể lấy số tiền phạt mặc định để hiển thị
    public abstract int getFinePerDay();

    // ─── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return super.toString()
                + ", readerType='" + getReaderType() + "'"
                + ", maxBorrowLimit=" + getMaxBorrowLimit()
                + ", finePerDay=" + getFinePerDay() + "đ";
    }
}