package com.library.repository;

import com.library.model.BorrowTicket;
import com.library.model.BorrowTicketDetail;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tầng quản lý lưu trữ dữ liệu Phiếu mượn - Đã được xử lý ngoại lệ và tối ưu bảo mật
 */
public class BorrowTicketRepository {
    private final String filePath = "data/tickets.txt"; //
    private final List<BorrowTicket> tickets; // Sử dụng final để cố định tham chiếu danh sách

    public BorrowTicketRepository() {
        this.tickets = new ArrayList<>(); //
        loadFromFile(); //
    }

    /**
     * Trả về một bản sao danh sách (Defensive Copy) hoặc danh sách Chỉ đọc
     * Ngăn chặn việc tầng ngoài tự ý gọi .clear() hoặc .remove() làm sai lệch RAM với File cứng.
     */
    public List<BorrowTicket> getAll() {
        return Collections.unmodifiableList(tickets);
    }

    /**
     * Tìm kiếm phiếu mượn theo ID
     */
    public BorrowTicket findById(String ticketId) {
        if (ticketId == null) return null;
        for (BorrowTicket ticket : tickets) {
            if (ticket.getTicketId().equalsIgnoreCase(ticketId.trim())) {
                return ticket;
            }
        }
        return null;
    }

    /**
     * Thêm mới phiếu mượn và tự động đồng bộ ra file
     */
    public void add(BorrowTicket ticket) {
        if (ticket == null) {
            System.err.println("Lỗi: Không thể thêm phiếu mượn rỗng (null).");
            return;
        }
        tickets.add(ticket); //
        saveToFile(); // Tự động đồng bộ ra file sau khi thêm mới
    }

    /**
     * Cập nhật thông tin phiếu mượn (Ví dụ: Khi trả sách, tính tiền phạt)
     */
    public void update(BorrowTicket updatedTicket) {
        if (updatedTicket == null) return;

        boolean isUpdated = false;
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(updatedTicket.getTicketId())) { //
                tickets.set(i, updatedTicket); // Cập nhật trên RAM
                isUpdated = true;
                break; //
            }
        }

        // Chỉ ghi file khi dữ liệu thực sự có sự thay đổi
        if (isUpdated) {
            saveToFile(); //
        } else {
            System.err.println("Lỗi: Không tìm thấy mã phiếu mượn '" + updatedTicket.getTicketId() + "' để cập nhật.");
        }
    }

    /**
     * Đọc dữ liệu từ file tickets.txt
     * Đã bọc Try-catch bên trong vòng lặp chống sập hệ thống (Fault-Tolerance).
     */
    private void loadFromFile() {
        tickets.clear(); //
        File file = new File(filePath); //
        if (!file.exists()) {
            return; // Nếu chưa có file dữ liệu thì bỏ qua
        }

        // Sử dụng Try-with-resources để tự động đóng luồng đọc file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { //
            String line; //
            while ((line = br.readLine()) != null) { //
                // Bỏ qua dòng trống hoặc dòng chú thích (Comment)
                if (line.trim().isEmpty() || line.trim().startsWith("#")) { //
                    continue; //
                }

                // Đặt khối try-catch tại đây để nếu một dòng dữ liệu bị lỗi, hệ thống vẫn tiếp tục đọc các dòng tiếp theo
                try {
                    String[] parts = line.split("\\|"); // Sử dụng chuẩn regex thoát ký tự đặc biệt

                    // Một dòng hợp lệ bắt buộc phải có tối thiểu 7 cột dữ liệu gốc ban đầu
                    if (parts.length < 7) {
                        System.err.println("Bỏ qua dòng lỗi cấu trúc (Thiếu cột): " + line);
                        continue;
                    }

                    String ticketId = parts[0].trim(); //
                    String readerId = parts[1].trim(); //
                    LocalDate borrowDate = LocalDate.parse(parts[2].trim()); //
                    LocalDate dueDate = LocalDate.parse(parts[3].trim()); //

                    // Xử lý giá trị ngày trả thực tế nếu đang ở trạng thái chưa trả (chuỗi "null")
                    LocalDate returnDate = parts[4].trim().equalsIgnoreCase("null") ? null : LocalDate.parse(parts[4].trim()); //
                    String statusStr = parts[5].trim(); //
                    double fineAmount = Double.parseDouble(parts[6].trim()); //

                    // Xử lý bóc tách chuỗi phức hợp Chi Tiết Sách Mượn (Nested Split)
                    List<BorrowTicketDetail> details = new ArrayList<>(); //
                    if (parts.length > 7 && !parts[7].trim().equalsIgnoreCase("null") && !parts[7].trim().isEmpty()) { //
                        String[] detailsPart = parts[7].trim().split(","); // Tách theo dấu phẩy để lấy từng loại sách
                        for (String d : detailsPart) { //
                            String[] subParts = d.split(":"); // Tách theo dấu hai chấm lấy MãSách và SốLượng
                            if (subParts.length == 2) {
                                String bookId = subParts[0].trim();
                                int quantity = Integer.parseInt(subParts[1].trim());
                                details.add(new BorrowTicketDetail(bookId, quantity)); //
                            }
                        }
                    }

                    // Khởi tạo đối tượng thông qua Constructor phục dựng dữ liệu an toàn
                    BorrowTicket ticket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate, returnDate, statusStr, fineAmount, details);
                    tickets.add(ticket);

                } catch (Exception e) {
                    System.err.println("Lỗi phân tích cú pháp tại dòng dữ liệu: [" + line + "] -> Chi tiết: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi nghiêm trọng không thể đọc file tickets.txt: " + e.getMessage());
        }
    }

    /**
     * Ghi ngược toàn bộ danh sách phiếu mượn từ bộ nhớ RAM xuống file tickets.txt
     */
    private void saveToFile() {
        // Sử dụng Try-with-resources tự động đóng luồng ghi file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) { //

            // Ghi phần tiêu đề (Header Comment) định nghĩa cấu trúc file dữ liệu để tiện đối chiếu
            bw.write("# ====================================================================================\n"); //
            bw.write("# CẤU TRÚC DỮ LIỆU FILE TICKETS.TXT (QUẢN LÝ PHIẾU MƯỢN / TRẢ SÁCH)\n"); //
            bw.write("# ====================================================================================\n"); //
            bw.write("# Định dạng các cột phân tách bằng dấu gạch đứng (|):\n"); //
            bw.write("# Quy luật: MãPhiếu|MãBạnĐọc|NgàyMượn|NgàyHẹnTrả|NgàyTrảThựcTế|TrạngThái|TiềnPhạt|ChiTiếtMượn\n"); //
            bw.write("#\n"); //
            bw.write("# Trong đó:\n"); //
            bw.write("#   - NgàyTrảThựcTế: Ghi 'null' nếu sách chưa được trả.\n"); //
            bw.write("#   - TrạngThái: 'Đang mượn' hoặc 'Đã trả'.\n"); //
            bw.write("#   - ChiTiếtMượn: Định dạng MãSách:SốLượng. Nếu mượn nhiều loại sách thì phân tách bằng dấu phẩy (,).\n"); //
            bw.write("# ====================================================================================\n\n"); //

            // Duyệt danh sách và tiến hành ghi dữ liệu
            for (BorrowTicket t : tickets) {
                // Tận dụng tối đa tính đóng gói bằng cách gọi trực tiếp hàm toFileFormat() từ chính Model
                bw.write(t.toFileFormat() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi dữ liệu vào file tickets.txt: " + e.getMessage()); //
        }
    }
}