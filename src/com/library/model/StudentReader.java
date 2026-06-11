package com.library.model;

import com.library.service.policy.NormalStudentFinePolicy; // ÉP BUỘC: Import chiến lược tính phạt tương ứng của sinh viên

/**
 * Sinh viên thường.
 * - Mượn tối đa : 3 cuốn
 * - Phí phạt     : 5.000đ / ngày trễ
 */
public class StudentReader extends Reader {

    private static final int    MAX_BORROW_LIMIT = 3;
    private static final int    FINE_PER_DAY     = 5_000;   // 5.000 VND
    private static final String READER_TYPE      = "Sinh viên thường";

    // ─── Constructor ───────────────────────────────────────────────────────────

    public StudentReader(String userId, String fullName, String phoneNumber) {
        // ĐÃ ĐỒNG BỘ: Truyền đủ 4 tham số sang lớp cha Reader, tự động tiêm chính sách tính phạt trễ hạn
        super(userId, fullName, phoneNumber, new NormalStudentFinePolicy());
    }

    // ─── Override abstract methods ─────────────────────────────────────────────

    @Override
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }

    @Override
    public String getReaderType() {
        return READER_TYPE;
    }

    @Override
    public int getFinePerDay() {
        return FINE_PER_DAY;
    }

    // ─── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "StudentReader{" + super.toString() + "}";
    }
}