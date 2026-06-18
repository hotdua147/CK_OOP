package com.library.model;

/**
 * Lớp đại diện cho thực thể Sách trong thư viện.
 * ĐÃ CẬP NHẬT ĐẦY ĐỦ 6 THUỘC TÍNH - Chuẩn hóa kết nối đồng bộ Spring Boot Web API & Jackson.
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private int quantity; // Số lượng sách còn lại trong kho
    private double price; // Giá trị sách (dùng đối chiếu tính phạt nếu làm mất)

    /**
     *  1. CONSTRUCTOR MẶC ĐỊNH KHÔNG THAM SỐ (BẮT BUỘC PHẢI CÓ)
     * Giúp Spring Boot (Jackson) tự động ánh xạ dữ liệu JSON từ giao diện Web thành đối tượng Java mượt mà.
     */
    public Book() {
    }

    /**
     * 2. Constructor đầy đủ 6 thuộc tính dùng để nạp dữ liệu từ file text mới.
     */
    public Book(String bookId, String title, String author, String category, int quantity, double price) {
        this.bookId = bookId != null ? bookId.trim() : "";
        this.title = title != null ? title.trim() : "";
        this.author = author != null ? author.trim() : "Chưa rõ";
        this.category = category != null ? category.trim() : "Giáo trình";
        setQuantity(quantity);
        setPrice(price);
    }

    /**
     * 3. Constructor rút gọn 3 thuộc tính (Đảm bảo tương thích ngược với dữ liệu cũ).
     */
    public Book(String bookId, String title, int quantity) {
        this(bookId, title, "Chưa rõ", "Giáo trình", quantity, 50000.0);
    }

    // ─── HỆ THỐNG GETTER & SETTER ĐỒNG BỘ ───────────────────────────────────

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId != null ? bookId.trim() : "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author.trim() : "Chưa rõ";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "Giáo trình";
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        // Chặn giá trị âm để đảm bảo tính toàn vẹn dữ liệu kho lưu trữ
        this.quantity = Math.max(0, quantity);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        // Giá trị tài sản không được phép nhỏ hơn 0
        this.price = Math.max(0.0, price);
    }

    // ─── Cơ cơ chế hiển thị dữ liệu dạng chuỗi ────────────────────────────────
    @Override
    public String toString() {
        return String.format("Mã sách: %s | Tên sách: %s | Tác giả: %s | Thể loại: %s | Tồn kho: %d | Giá: %.0fđ",
                bookId, title, author, category, quantity, price);
    }
}