package com.library.model;

/**
 * Giảng viên.
 * - Mượn tối đa : 10 cuốn
 * - Phí phạt     : 2.000đ / ngày trễ
 */
public class LecturerReader extends Reader {

    private static final int    MAX_BORROW_LIMIT = 10;
    private static final int    FINE_PER_DAY     = 2_000;   // 2.000 VND
    private static final String READER_TYPE      = "Giảng viên";

    // ─── Constructor ───────────────────────────────────────────────────────────

    public LecturerReader(String userId, String fullName, String phoneNumber) {
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
        return "LecturerReader{" + super.toString() + "}";
    }
}