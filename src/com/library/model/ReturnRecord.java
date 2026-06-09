package com.library.model;

import java.time.LocalDate;

public class ReturnRecord {
    private String ticketId;
    private String bookId;
    private LocalDate actualReturnDate;
    private double fineAmount;

    public ReturnRecord() {}

    public ReturnRecord(String ticketId, String bookId, LocalDate actualReturnDate, double fineAmount) {
        this.ticketId = ticketId;
        this.bookId = bookId;
        this.actualReturnDate = actualReturnDate;
        this.fineAmount = fineAmount;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    @Override
    public String toString() {
        return "[Ma phieu: " + ticketId +
                " | Ma sach: " + bookId +
                " | Ngay tra: " + actualReturnDate +
                " | Tien phat: " + fineAmount + "d]";
    }
}