package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp thực thể đại diện cho Phiếu mượn/trả sách của thư viện.
 * Quản lý trạng thái mượn và danh sách chi tiết các cuốn sách được mượn cùng lúc.
 */
public class BorrowTicket {

    /**
     * Định nghĩa Enum quản lý trạng thái phiếu mượn chống sai sót dữ liệu chuỗi.
     */
    public enum TicketStatus {
        BORROWING("Đang mượn"),
        RETURNED("Đã trả");

        private final String value;

        TicketStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Chuyển đổi linh hoạt từ văn bản tiếng Việt của file .txt sang Enum tương ứng.
         */
        public static TicketStatus fromValue(String value) {
            if (value == null) return BORROWING;
            for (TicketStatus status : TicketStatus.values()) {
                if (status.getValue().equalsIgnoreCase(value.trim())) {
                    return status;
                }
            }
            return BORROWING; // Mặc định là đang mượn nếu dữ liệu không khớp
        }
    }

    private String ticketId;
    private String readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // Sẽ là null nếu độc giả chưa trả sách
    private TicketStatus status;
    private double fineAmount;
    private final List<BorrowTicketDetail> details = new ArrayList<>();

    /**
     * Constructor khởi tạo một phiếu mượn mới.
     */
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate) {
        this.ticketId = ticketId != null ? ticketId.trim() : "";
        this.readerId = readerId != null ? readerId.trim() : "";
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = TicketStatus.BORROWING; // Phiếu mới tạo luôn ở trạng thái Đang mượn
        this.fineAmount = 0.0;
    }

    // ─── Getters & Setters (Tuân thủ tính Encapsulation) ───────────

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

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public String getStatusValue() { return status.getValue(); }
    public void setStatusByValue(String statusStr) { this.status = TicketStatus.fromValue(statusStr); }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) {
        this.fineAmount = Math.max(0.0, fineAmount); // Chặn giá trị tiền phạt âm
    }

    /**
     * Lấy danh sách chi tiết các cuốn sách mượn.
     * @return Một bản sao danh sách Chỉ đọc (Defensive Copy) để bảo vệ an toàn dữ liệu RAM.
     */
    public List<BorrowTicketDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    /**
     * Thêm dòng chi tiết cuốn sách được đăng ký mượn vào phiếu.
     */
    public void addDetail(BorrowTicketDetail detail) {
        if (detail != null) {
            this.details.add(detail);
        }
    }

    // ─── Xử lý định dạng File text IO ─────────────────────────────────────────

    /**
     * Chuyển đổi toàn bộ thông tin phiếu mượn thành một dòng text chuẩn hóa
     * phân tách bằng dấu gạch đứng để ghi xuống file tickets.txt.
     */
    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < details.size(); i++) {
            sb.append(details.get(i).toString());
            if (i < details.size() - 1) {
                sb.append(",");
            }
        }

        // Trả về chuỗi map khớp hoàn toàn 8 cột dữ liệu lưu trữ
        return String.format("%s|%s|%s|%s|%s|%s|%.1f|%s",
                ticketId,
                readerId,
                borrowDate,
                dueDate,
                (returnDate == null ? "null" : returnDate.toString()), // Đồng bộ giá trị null dạng text
                status.getValue(),
                fineAmount,
                sb.toString());
    }
}