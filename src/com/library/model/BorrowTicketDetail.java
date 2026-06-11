package com.library.model;

import java.util.Objects;

/**
 * Chi tiết từng dòng sách trong phiếu mượn (Entity phụ thuộc)
 * Đã được thiết kế lại theo dạng Immutable để đảm bảo tính toàn vẹn dữ liệu.
 */
public final class BorrowTicketDetail {
    // Sử dụng từ khóa final để ngăn chặn việc thay đổi giá trị thuộc tính sau khi khởi tạo
    private final String bookId;
    private final int quantity;

    /**
     * Constructor khởi tạo dòng chi tiết sách mượn
     * Đã tích hợp kiểm tra ràng buộc dữ liệu (Validation) để chặn dữ liệu rác
     */
    public BorrowTicketDetail(String bookId, int quantity) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Mã sách không được để trống hoặc null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Số lượng mượn phải lớn hơn 0 (Nhận được: " + quantity + ").");
        }
        this.bookId = bookId.trim();
        this.quantity = quantity;
    }

    // ====================================================================================================
    // GETTER (Không sử dụng SETTER để đảm bảo tính Immutable - Bất biến)
    // ====================================================================================================

    public String getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Định dạng chuỗi khớp hoàn toàn với quy ước lưu trữ file tickets.txt (MãSách:SốLượng)
     */
    @Override
    public String toString() {
        return bookId + ":" + quantity;
    }

    /**
     * Override equals để hỗ trợ việc so sánh nội dung Object (phục vụ các hàm contains(), remove() của List)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowTicketDetail integrity = (BorrowTicketDetail) o;
        return quantity == integrity.quantity && bookId.equals(integrity.bookId);
    }

    /**
     * Override hashCode đi kèm với equals theo đúng tiêu chuẩn Java Beans
     */
    @Override
    public int hashCode() {
        return Objects.hash(bookId, quantity);
    }
}