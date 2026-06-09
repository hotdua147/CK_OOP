package com.library.model;

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
        super(userId, fullName, phoneNumber);
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