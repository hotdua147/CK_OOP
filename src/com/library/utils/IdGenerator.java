package com.library.utils;

import com.library.model.BorrowTicket;
import java.util.List;

/**
 * Lớp tiện ích tự động sinh mã tăng tiến cho hệ thống phiếu mượn thư viện.
 * Thiết kế theo chuẩn Utility Class (final class, private constructor) để tối ưu vùng nhớ.
 */
public final class IdGenerator {

    // Private constructor nhằm chặn việc khởi tạo đối tượng từ bên ngoài
    private IdGenerator() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không được phép khởi tạo đối tượng!");
    }

    /**
     * Tự động sinh mã phiếu mượn tiếp theo dưới dạng PTxxx (Ví dụ: PT001, PT002...)
     * dựa trên số thứ tự số lớn nhất hiện có trong danh sách phiếu trên RAM.
     *
     * @param existingTickets Danh sách tất cả các phiếu mượn hiện có trên hệ thống
     * @return Mã phiếu mượn mới duy nhất không trùng lặp (PTxxx)
     */
    public static String generateTicketId(List<BorrowTicket> existingTickets) {
        int max = 0;

        // Phòng vệ nếu danh sách trống hoặc chưa được khởi tạo, mặc định trả về mã đầu tiên
        if (existingTickets == null || existingTickets.isEmpty()) {
            return "PT001";
        }

        for (BorrowTicket t : existingTickets) {
            if (t == null || t.getTicketId() == null) {
                continue;
            }

            try {
                String ticketId = t.getTicketId().trim();

                // Trường hợp 1: Mã chuẩn hóa theo file tickets.txt mẫu là "PTxxx" (Ví dụ: PT001)
                if (ticketId.toUpperCase().startsWith("PT") && ticketId.length() >= 3) {
                    int num = Integer.parseInt(ticketId.substring(2));
                    if (num > max) {
                        max = num;
                    }
                }
                // Trường hợp 2: Cơ chế tương thích ngược phòng khi hệ thống chứa mã cũ dạng "PM-xxx" (Ví dụ: PM-001)
                else if (ticketId.toUpperCase().startsWith("PM-") && ticketId.length() >= 4) {
                    int num = Integer.parseInt(ticketId.substring(3));
                    if (num > max) {
                        max = num;
                    }
                }
            } catch (NumberFormatException ignored) {
                // Chủ động bỏ qua các dòng mã bị lỗi định dạng từ file text để hệ thống không bị crash
            }
        }

        // Trả về chuỗi định dạng PT kèm số thứ tự tăng dần 3 chữ số (Ví dụ: max = 7 -> trả về PT008)
        return String.format("PT%03d", max + 1);
    }
}