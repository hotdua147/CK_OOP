package com.library.model;

/**
 * Lớp trừu tượng đại diện cho Bạn đọc thư viện, kế thừa từ User.
 *
 * Áp dụng:
 *  - Inheritance : kế thừa từ User
 *  - Abstraction : định nghĩa các phương thức trừu tượng
 *  - Polymorphism: lớp con override getMaxBorrowLimit(), getReaderType(), getFinePerDay()
 */
public abstract class Reader extends User {

    // ─── Constructor ───────────────────────────────────────────────────────────

    public Reader(String userId, String fullName, String phoneNumber) {
        super(userId, fullName, phoneNumber);
    }

    // ─── Abstract methods (các lớp con bắt buộc override) ─────────────────────

    /**
     * Trả về số sách tối đa bạn đọc được phép mượn cùng lúc.
     */
    public abstract int getMaxBorrowLimit();

    /**
     * Trả về tên loại bạn đọc (vd: "Sinh viên thường").
     */
    public abstract String getReaderType();

    /**
     * Trả về phí phạt mỗi ngày trễ (đơn vị: VND).
     * Dùng chung với module tính phạt của Thành viên 5.
     */
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
