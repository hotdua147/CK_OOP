package com.library.model;

/**
 * Chi tiết từng sách trong phiếu mượn
 */
public class BorrowTicketDetail {
    private String bookId;
    private int quantity;

    public BorrowTicketDetail(String bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getter và Setter
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return bookId + ":" + quantity;
    }
}