package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phiếu mượn sách của thư viện - Đã được chuẩn hóa và nâng cấp bảo mật dữ liệu
 */
public class BorrowTicket {

    /**
     * Định nghĩa Enum cho Trạng thái phiếu mượn để tránh lỗi gõ sai chính tả String
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
         * Chuyển đổi từ chuỗi tiếng Việt trong file txt sang Enum tương ứng
         */
        public static TicketStatus fromValue(String value) {
            for (TicketStatus status : TicketStatus.values()) {
                if (status.getValue().equalsIgnoreCase(value.trim())) {
                    return status;
                }
            }
            // Mặc định trả về Đang mượn nếu chuỗi không khớp
            return BORROWING;
        }
    }

    private String ticketId;
    private String readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private TicketStatus status; // Thay thế String bằng Enum
    private double fineAmount;
    private final List<BorrowTicketDetail> details; // Sử dụng final để cố định tham chiếu danh sách

    /**
     * Constructor 1: Dùng khi tạo mới phiếu mượn thực tế ở tầng Service
     */
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate) {
        if (ticketId == null || ticketId.trim().isEmpty()) throw new IllegalArgumentException("Mã phiếu không được trống.");
        if (readerId == null || readerId.trim().isEmpty()) throw new IllegalArgumentException("Mã bạn đọc không được trống.");
        if (borrowDate == null || dueDate == null) throw new IllegalArgumentException("Ngày mượn và ngày hẹn trả không được null.");

        this.ticketId = ticketId;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = TicketStatus.BORROWING; // Mặc định khi tạo mới
        this.fineAmount = 0.0; //
        this.details = new ArrayList<>(); //
    }

    /**
     * Constructor 2: Dùng khi khôi phục dữ liệu từ file dữ liệu txt lên hệ thống
     * Đã tích hợp phòng thủ lỗi Null chống Sập RAM nếu file dữ liệu thiếu sót.
     */
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate,
                        LocalDate returnDate, String statusStr, double fineAmount, List<BorrowTicketDetail> details) {
        this.ticketId = ticketId;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate; //
        this.returnDate = returnDate; //
        this.status = TicketStatus.fromValue(statusStr); // Ép kiểu an toàn từ String sang Enum
        this.fineAmount = fineAmount < 0 ? 0.0 : fineAmount; // Ngăn chặn tiền phạt âm vô lý

        // Phòng thủ lỗi Null: Nếu danh sách truyền vào bị null thì chủ động tạo mới array trống
        this.details = (details != null) ? details : new ArrayList<>();
    }

    /**
     * Thêm một dòng sách chi tiết vào phiếu
     */
    public void addDetail(BorrowTicketDetail detail) {
        if (detail == null) {
            throw new IllegalArgumentException("Chi tiết sách mượn không được để trống (null).");
        }
        this.details.add(detail); //
    }

    /**
     * Trả về danh sách chi tiết dưới dạng CHỈ ĐỌC (Unmodifiable List)
     * Ngăn chặn tuyệt đối việc các tầng khác tự ý can thiệp .clear() hoặc .remove() danh sách gốc.
     */
    public List<BorrowTicketDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    /**
     * Chuyển đổi dữ liệu Object thành chuỗi định dạng chuẩn để ghi trực tiếp vào file tickets.txt
     * Đồng bộ tuyệt đối cấu trúc với tầng Repository.
     */
    public String toFileFormat() {
        StringBuilder detailsStr = new StringBuilder();
        if (details.isEmpty()) {
            detailsStr.append("null");
        } else {
            for (int i = 0; i < details.size(); i++) {
                detailsStr.append(details.get(i).toString());
                if (i < details.size() - 1) {
                    detailsStr.append(",");
                }
            }
        }
        return String.format("%s|%s|%s|%s|%s|%s|%.1f|%s",
                ticketId,
                readerId,
                borrowDate,
                dueDate,
                returnDate == null ? "null" : returnDate,
                status.getValue(), // Lấy chuỗi tiếng Việt của Enum để ghi vào file
                fineAmount,
                detailsStr.toString()
        );
    }

    // ====================================================================================================
    // GETTER VÀ SETTER (Tuân thủ tính Encapsulation)
    // ====================================================================================================

    public String getTicketId() { return ticketId; } //
    public void setTicketId(String ticketId) { this.ticketId = ticketId; } //

    public String getReaderId() { return readerId; } //
    public void setReaderId(String readerId) { this.readerId = readerId; } //

    public LocalDate getBorrowDate() { return borrowDate; } //
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; } //

    public LocalDate getDueDate() { return dueDate; } //
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; } //

    public LocalDate getReturnDate() { return returnDate; } //
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; } //

    // Chuyển đổi linh hoạt Getter/Setter trạng thái để phục vụ so sánh logic nội bộ dạng Enum lẫn String
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public String getStatusValue() { return status.getValue(); }
    public void setStatusByValue(String statusStr) { this.status = TicketStatus.fromValue(statusStr); }

    public double getFineAmount() { return fineAmount; } //
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; } //
}