package com.library.model;

import java.time.LocalDate;

/**
 * Lớp thực thể đại diện cho Biên bản / Nhật ký giao dịch trả sách (Return Transaction Log).
 * Giúp thư viện lưu vết chi tiết lịch sử các ca trả sách thành công và số tiền phạt đã thu.
 */
public class ReturnRecord {
    private String recordId;    // Mã giao dịch trả sách (Ví dụ: RR001, RR002...)
    private String ticketId;    // Mã phiếu mượn gốc liên kết dến ca trả sách này
    private LocalDate returnDate; // Ngày thực hiện giao dịch trả sách
    private double finePaid;    // Số tiền phạt độc giả đã thanh toán tại giao dịch này

    /**
     * Constructor khởi tạo một Biên bản trả sách hoàn chỉnh.
     * Tích hợp kiểm tra phòng vệ dữ liệu đầu vào.
     *
     * @param recordId   Mã giao dịch trả sách
     * @param ticketId   Mã phiếu mượn gốc tương ứng
     * @param returnDate Ngày thực tế trả sách
     * @param finePaid   Số tiền phạt thực thu (VND)
     */
    public ReturnRecord(String recordId, String ticketId, LocalDate returnDate, double finePaid) {
        this.recordId = recordId != null ? recordId.trim() : "";
        this.ticketId = ticketId != null ? ticketId.trim() : "";
        this.returnDate = returnDate != null ? returnDate : LocalDate.now();
        setFinePaid(finePaid);
    }

    // ─── Getters & Setters (Đảm bảo tính Đóng gói Encapsulation) ──────────────

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFinePaid() {
        return finePaid;
    }

    public void setFinePaid(double finePaid) {
        // Chặn giá trị âm, tiền phạt thực tế thu được bắt buộc phải từ 0đ trở lên
        this.finePaid = Math.max(0.0, finePaid);
    }

    // ─── Đồng bộ hóa định dạng hiển thị và lưu file text ─────────────────────

    /**
     * Chuẩn hóa định dạng chuỗi phân tách bằng dấu gạch đứng nếu nhóm có nhu cầu
     * mở rộng ghi lịch sử biên bản trả sách riêng biệt xuống file returns.txt sau này.
     */
    public String toFileFormat() {
        return String.format("%s|%s|%s|%.1f", recordId, ticketId, returnDate, finePaid);
    }

    @Override
    public String toString() {
        return String.format("ReturnRecord[MãGiaoDịch='%s', MãPhiếuMượn='%s', NgàyTrả=%s, TiềnPhạtĐãThu=%,.0fđ]",
                recordId, ticketId, returnDate, finePaid);
    }
}