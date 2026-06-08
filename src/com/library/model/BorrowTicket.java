package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Phiếu mượn sách của thư viện
 */
public class BorrowTicket {
    private String ticketId;
    private String readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // "Đang mượn" hoặc "Đã trả"
    private double fineAmount;
    private List<BorrowTicketDetail> details;

    // Constructor dùng khi tạo mới phiếu mượn ngoài thực tế
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate) {
        this.ticketId = ticketId;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "Đang mượn"; // Trạng thái mặc định khi tạo mới
        this.fineAmount = 0.0;
        this.details = new ArrayList<>();
    }

    // Constructor đầy đủ dùng khi load dữ liệu từ file txt lên hệ thống
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate,
                        LocalDate returnDate, String status, double fineAmount, List<BorrowTicketDetail> details) {
        this.ticketId = ticketId;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount;
        this.details = details;
    }

    public void addDetail(BorrowTicketDetail detail) {
        this.details.add(detail);
    }

    // Getter và Setter tuân thủ tính Encapsulation
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public List<BorrowTicketDetail> getDetails() { return details; }
    public void setDetails(List<BorrowTicketDetail> details) { this.details = details; }
}