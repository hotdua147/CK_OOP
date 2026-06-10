package com.library.model;

/**
 * Lớp đại diện cho thực thể Sách trong thư viện
 */
public class Book {
    private String bookId;
    private String title;
    private int quantity; // Số lượng sách còn lại trong kho

    public Book(String bookId, String title, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
    }

    // Getter và Setter tuân thủ tính đóng gói Encapsulation
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }
}