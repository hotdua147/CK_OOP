package com.library.model;

import java.util.Objects;

/**
 * Lớp thực thể phụ thuộc đại diện cho chi tiết từng dòng sách trong phiếu mượn.
 * Thiết kế theo dạng Immutable Object (Đối tượng bất biến) nhằm bảo vệ an toàn toàn vẹn dữ liệu.
 */
public final class BorrowTicketDetail {

    // Sử dụng từ khóa final: Chặn việc sửa đổi giá trị sau khi đã khởi tạo đối tượng
    private final String bookId;
    private final int quantity;

    /**
     * Constructor khởi tạo dòng chi tiết mượn sách.
     * Đã tích hợp cơ chế phòng vệ chặn dữ liệu lỗi hoặc dữ liệu rác.
     *
     * @param bookId   Mã cuốn sách đăng ký mượn
     * @param quantity Số lượng cuốn sách muốn mượn
     */
    public BorrowTicketDetail(String bookId, int quantity) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Mã sách không được để trống hoặc nhận giá trị null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Số lượng sách đăng ký mượn phải lớn hơn 0 (Nhận được: " + quantity + ").");
        }

        // CẢI TIẾN: Tự động chuẩn hóa viết hoa mã sách (b001 -> B001) để khớp đồng bộ với file books.txt
        this.bookId = bookId.trim().toUpperCase();
        this.quantity = quantity;
    }

    // ─── Getters (Không viết hàm Setters để giữ tính chất Bất biến) ───────────

    public String getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    // ─── Đồng bộ hóa hiển thị & So sánh đối tượng ────────────────────────────

    /**
     * Định dạng chuỗi khớp hoàn toàn với cấu trúc lưu trữ của cột ChiTiếtMượn trong file tickets.txt (MãSách:SốLượng)
     */
    @Override
    public String toString() {
        return bookId + ":" + quantity;
    }

    /**
     * Thiết lập lại tiêu chuẩn so sánh bằng nội dung dữ liệu thay vì so sánh địa chỉ ô nhớ trên RAM.
     * Phục vụ đắc lực cho các hàm kiểm tra của List như chứa phần tử (.contains) hay xóa phần tử (.remove).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowTicketDetail that = (BorrowTicketDetail) o;
        return quantity == that.quantity && Objects.equals(bookId, that.bookId);
    }

    /**
     * Luôn luôn override hashCode đi kèm với equals để tuân thủ quy chuẩn Java Beans.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bookId, quantity);
    }
}