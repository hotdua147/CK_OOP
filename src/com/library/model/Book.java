package com.library.model;

public class Book {
    private String bookId;
    private String title;
    private int quantity;

    public Book() {}

    public Book(String bookId, String title, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
