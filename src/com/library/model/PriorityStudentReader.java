package com.library.model;

import com.library.service.policy.NormalStudentFinePolicy; // Import chiến lược tính phạt từ package tương ứng

/**
 * Sinh viên ưu tiên.
 * - Mượn tối đa : 5 cuốn
 * - Phí phạt     : 3.000đ / ngày trễ
 */
public class PriorityStudentReader extends Reader {

    private static final int    MAX_BORROW_LIMIT = 5;
    private static final int    FINE_PER_DAY     = 3_000;   // 3.000 VND
    private static final String READER_TYPE      = "Sinh viên ưu tiên";

    // ─── Constructor ───────────────────────────────────────────────────────────

    public PriorityStudentReader(String userId, String fullName, String phoneNumber) {
        // ĐÃ ĐỒNG BỘ: Truyền đầy đủ 4 tham số sang lớp cha Reader
        // (Nếu nhóm bạn có lớp PriorityStudentFinePolicy riêng, hãy thay thế 'new NormalStudentFinePolicy()' bằng lớp đó nhé)
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
        return "PriorityStudentReader{" + super.toString() + "}";
    }
}