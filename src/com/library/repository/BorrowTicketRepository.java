package com.library.repository;

import com.library.model.BorrowTicket;
import com.library.model.BorrowTicketDetail;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tầng quản lý lưu trữ dữ liệu Phiếu mượn - Đã được chuẩn hóa chống crash và tối ưu đa nền tảng.
 */
public class BorrowTicketRepository {

    // Đồng bộ quy ước đặt tên hằng số toàn hệ thống
    private static final String FILE_PATH = "data/tickets.txt";
    private final List<BorrowTicket> tickets;

    public BorrowTicketRepository() {
        this.tickets = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Trả về một bản sao danh sách Chỉ đọc (Defensive Copy) để bảo vệ toàn vẹn dữ liệu RAM.
     */
    public List<BorrowTicket> getAll() {
        return Collections.unmodifiableList(tickets);
    }

    /**
     * Tìm kiếm phiếu mượn theo ID (Không phân biệt hoa thường)
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
     * Thêm mới phiếu mượn và tự động đồng bộ xuống file text
     */
    public void add(BorrowTicket ticket) {
        if (ticket == null) {
            System.err.println("[BorrowTicketRepository] Lỗi: Không thể thêm phiếu mượn rỗng (null).");
            return;
        }
        tickets.add(ticket);
        saveToFile(); // Tự động đồng bộ ra file cứng
    }

    /**
     * Cập nhật trạng thái phiếu mượn (Khi trả sách, tính tiền phạt)
     */
    public void update(BorrowTicket updatedTicket) {
        if (updatedTicket == null) return;

        boolean isUpdated = false;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(updatedTicket.getTicketId())) {
                tickets.set(i, updatedTicket); // Cập nhật trạng thái mới trên RAM
                isUpdated = true;
                break;
            }
        }

        // Chỉ tốn tài nguyên ghi file khi dữ liệu thực sự có biến động
        if (isUpdated) {
            saveToFile();
        } else {
            System.err.println("[BorrowTicketRepository] Lỗi: Không tìm thấy mã phiếu mượn '" + updatedTicket.getTicketId() + "' để cập nhật.");
        }
    }

    /**
     * Đọc dữ liệu từ file tickets.txt - Bọc phòng thủ lỗi bóc tách sâu
     */
    private void loadFromFile() {
        tickets.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // Nếu chưa có file dữ liệu thì bỏ qua, trả về danh sách rỗng an toàn
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Bỏ qua dòng trống hoặc dòng chú thích cấu trúc file
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Khối try-catch bảo vệ vòng lặp: Một dòng lỗi không làm sập toàn bộ bộ đọc
                try {
                    String[] parts = line.split("\\|");

                    if (parts.length < 7) {
                        System.err.println("[BorrowTicketRepository] Bỏ qua dòng lỗi cấu trúc (Thiếu cột): " + line);
                        continue;
                    }

                    String ticketId = parts[0].trim();
                    String readerId = parts[1].trim();
                    LocalDate borrowDate = LocalDate.parse(parts[2].trim());
                    LocalDate dueDate = LocalDate.parse(parts[3].trim());

                    // ĐÃ KHẮC PHỤC: Xử lý chuỗi "null" an toàn, ngăn nổ ngoại lệ DateTimeParseException
                    LocalDate returnDate = parts[4].trim().equalsIgnoreCase("null") ? null : LocalDate.parse(parts[4].trim());
                    String statusStr = parts[5].trim();
                    double fineAmount = Double.parseDouble(parts[6].trim());

                    // Bóc tách chuỗi phức hợp Chi Tiết Sách Mượn (Nested Split)
                    List<BorrowTicketDetail> details = new ArrayList<>();
                    if (parts.length > 7 && !parts[7].trim().equalsIgnoreCase("null") && !parts[7].trim().isEmpty()) {
                        String[] detailsPart = parts[7].trim().split(",");
                        for (String d : detailsPart) {
                            String[] subParts = d.split(":");
                            if (subParts.length == 2) {
                                String bookId = subParts[0].trim();
                                int quantity = Integer.parseInt(subParts[1].trim());
                                details.add(new BorrowTicketDetail(bookId, quantity));
                            }
                        }
                    }

                    // Khởi tạo đối tượng thông qua Constructor phục dựng dữ liệu an toàn
                    BorrowTicket ticket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate, returnDate, statusStr, fineAmount, details);
                    tickets.add(ticket);

                } catch (Exception e) {
                    System.err.println("[BorrowTicketRepository] Bỏ qua dòng sai định dạng: [" + line + "] -> Chi tiết: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi nghiêm trọng không thể đọc file tickets.txt: " + e.getMessage());
        }
    }

    /**
     * Ghi ngược dữ liệu từ RAM xuống file cứng - Đã tối ưu hóa tạo thư mục tự động và xuống dòng đa nền tảng
     */
    private void saveToFile() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();

        // ĐÃ KHẮC PHỤC: Tự động tạo folder data/ nếu hệ thống chạy lần đầu tiên
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            // Ghi phần tiêu đề hướng dẫn format trong file text
            bw.write("# ===================================================================================="); bw.newLine();
            bw.write("# CẤU TRÚC DỮ LIỆU FILE TICKETS.TXT (QUẢN LÝ PHIẾU MƯỢN / TRẢ SÁCH)"); bw.newLine();
            bw.write("# ===================================================================================="); bw.newLine();
            bw.write("# Định dạng các cột phân tách bằng dấu gạch đứng (|):"); bw.newLine();
            bw.write("# Quy luật: MãPhiếu|MãBạnĐọc|NgàyMượn|NgàyHẹnTrả|NgàyTrảThựcTế|TrạngThái|TiềnPhạt|ChiTiếtMượn"); bw.newLine();
            bw.write("#"); bw.newLine();
            bw.write("# Trong đó:"); bw.newLine();
            bw.write("#   - NgàyTrảThựcTế: Ghi 'null' nếu sách chưa được trả."); bw.newLine();
            bw.write("#   - TrạngThái: 'Đang mượn' hoặc 'Đã trả'."); bw.newLine();
            bw.write("#   - ChiTiếtMượn: Định dạng MãSách:SốLượng. Nếu mượn nhiều loại sách thì phân tách bằng dấu phẩy (,)."); bw.newLine();
            bw.write("# ===================================================================================="); bw.newLine();
            bw.newLine();

            // Duyệt danh sách và tiến hành ghi dữ liệu
            for (BorrowTicket t : tickets) {
                // ĐÃ KHẮC PHỤC: Dùng bw.newLine() thay vì cộng chuỗi "\n" để chạy mượt cả trên Windows lẫn Linux
                bw.write(t.toFileFormat());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[BorrowTicketRepository] Lỗi nghiêm trọng khi ghi dữ liệu vào file tickets.txt: " + e.getMessage());
        }
    }
}