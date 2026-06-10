package com.library.model;

import com.library.service.policy.LecturerFinePolicy; // ÉP BUỘC: Import chính xác chiến lược tính phạt của Giảng viên

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
        // ĐÃ ĐỒNG BỘ: Truyền đủ 4 tham số sang lớp cha Reader, tự động tiêm chính sách tính phạt riêng của Giảng viên
        super(userId, fullName, phoneNumber, new LecturerFinePolicy());
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