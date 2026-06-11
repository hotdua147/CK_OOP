package com.library.repository;

import com.library.model.BorrowTicket;
import com.library.model.BorrowTicketDetail;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tầng quản lý lưu trữ dữ liệu Phiếu mượn - Đọc ghi dữ liệu file text 'data/tickets.txt'
 * Đã được chuẩn hóa chống crash lỗi ép kiểu ngày tháng và hỗ trợ đồng bộ thời gian thực.
 */
public class BorrowTicketRepository {
    private static final String FILE_PATH = "data/tickets.txt";
    private final List<BorrowTicket> tickets = new ArrayList<>();

    /**
     * Khởi tạo tầng quản lý phiếu và tự động tải dữ liệu lịch sử từ file text lên RAM.
     */
    public BorrowTicketRepository() {
        loadFromFile();
    }

    /**
     * Lấy danh sách toàn bộ phiếu mượn/trả sách đang quản lý trên bộ nhớ RAM.
     * Áp dụng tính năng Defensive Copy bảo vệ cấu trúc mảng gốc hệ thống.
     *
     * @return Danh sách phiếu mượn dưới dạng Chỉ đọc (Unmodifiable List)
     */
    public List<BorrowTicket> getAll() {
        return Collections.unmodifiableList(tickets);
    }

    /**
     * Tìm kiếm phiếu mượn cụ thể dựa theo mã số số phiếu (Không phân biệt chữ hoa/thường).
     *
     * @param ticketId Mã phiếu mượn cần tìm (Ví dụ: PT001)
     * @return Đối tượng BorrowTicket nếu tìm thấy, ngược lại trả về null
     */
    public BorrowTicket findById(String ticketId) {
        if (ticketId == null) return null;
        String trimmedId = ticketId.trim();
        for (BorrowTicket ticket : tickets) {
            if (ticket.getTicketId().equalsIgnoreCase(trimmedId)) {
                return ticket;
            }
        }
        return null;
    }

    /**
     * Đăng ký thêm một phiếu mượn sách mới vào hệ thống và đồng bộ ngay xuống file text.
     *
     * @param ticket Đối tượng phiếu mượn mới khởi tạo thành công ở tầng Service
     */
    public void add(BorrowTicket ticket) {
        if (ticket != null) {
            tickets.add(ticket);
            saveToFile(); // Tự động cập nhật file tickets.txt ngay lập tức
        }
    }

    /**
     * Cập nhật trạng thái phiếu mượn (Ví dụ: Chuyển sang Đã trả, cập nhật ngày trả thực tế, tiền phạt).
     *
     * @param updatedTicket Đối tượng phiếu mượn mang dữ liệu mới cần ghi đè
     */
    public void update(BorrowTicket updatedTicket) {
        if (updatedTicket == null) return;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equalsIgnoreCase(updatedTicket.getTicketId())) {
                tickets.set(i, updatedTicket);
                saveToFile(); // Đồng bộ trạng thái mới xuống file text cứng
                return;
            }
        }
    }

    /**
     * Đọc và phân tích dữ liệu dòng text từ file 'data/tickets.txt' đưa lên RAM.
     */
    private void loadFromFile() {
        tickets.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return; // Nếu chạy lần đầu chưa có phiếu, bỏ qua nạp file

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Bỏ qua các dòng trống hoặc các dòng chú thích định dạng cột bằng dấu #
                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        String ticketId = parts[0].trim();
                        String readerId = parts[1].trim();
                        LocalDate borrowDate = LocalDate.parse(parts[2].trim());
                        LocalDate dueDate = LocalDate.parse(parts[3].trim());

                        // Khởi tạo đối tượng phiếu mượn cơ sở
                        BorrowTicket ticket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate);

                        // Xử lý an toàn cột Ngày trả thực tế: Tránh lỗi parse chuỗi "null"
                        String returnDateStr = parts[4].trim();
                        if (!returnDateStr.equalsIgnoreCase("null")) {
                            ticket.setReturnDate(LocalDate.parse(returnDateStr));
                        }

                        // Phục hồi các trạng thái nghiệp vụ và số tiền phạt đi kèm
                        ticket.setStatusByValue(parts[5].trim());
                        ticket.setFineAmount(Double.parseDouble(parts[6].trim()));

                        // Bóc tách danh sách sách mượn phức hợp dạng: MãSách:SốLượng,MãSách:SốLượng
                        String detailsStr = parts[7].trim();
                        String[] detailsArr = detailsStr.split(",");
                        for (String d : detailsArr) {
                            String[] kv = d.split(":");
                            if (kv.length == 2) {
                                String bookId = kv[0].trim();
                                int quantity = Integer.parseInt(kv[1].trim());
                                ticket.addDetail(new BorrowTicketDetail(bookId, quantity));
                            }
                        }
                        tickets.add(ticket);
                    }
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi cục bộ của 1 phiếu hỏng định dạng, đảm bảo các phiếu mượn khác vẫn nạp thành công
                    System.err.println("[BorrowTicketRepository] Bỏ qua dòng phiếu lỗi định dạng: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi nghiêm trọng khi đọc dữ liệu phiếu: " + e.getMessage());
        }
    }

    /**
     * Ghi đè toàn bộ danh sách phiếu mượn đang quản lý trên bộ nhớ RAM xuống file text 'data/tickets.txt'.
     */
    private void saveToFile() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Ghi cấu trúc văn bản hướng dẫn cột dữ liệu ở đầu file text
            bw.write("# MãPhiếu|MãBạnĐọc|NgàyMượn|NgàyHẹnTrả|NgàyTrảThựcTế|TrạngThái|TiềnPhạt|ChiTiếtMượn");
            bw.newLine();

            for (BorrowTicket t : tickets) {
                bw.write(t.toFileFormat());
                bw.newLine(); // Xuống dòng an toàn đa nền tảng
            }
        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi nghiêm trọng khi ghi dữ liệu phiếu: " + e.getMessage());
        }
    }
}