package com.library.repository;

import com.library.model.BorrowTicket;
import com.library.model.BorrowTicketDetail;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowTicketRepository {
    private final String filePath = "data/tickets.txt";
    private List<BorrowTicket> tickets;

    public BorrowTicketRepository() {
        this.tickets = new ArrayList<>();
        loadFromFile();
    }

    public List<BorrowTicket> getAll() {
        return tickets;
    }

    public void add(BorrowTicket ticket) {
        tickets.add(ticket);
        saveToFile(); // Tự động đồng bộ ra file sau khi thêm mới
    }

    public void update(BorrowTicket updatedTicket) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(updatedTicket.getTicketId())) {
                tickets.set(i, updatedTicket);
                break;
            }
        }
        saveToFile(); // Tự động đồng bộ sau khi cập nhật dữ liệu trả sách
    }

    // Đọc dữ liệu từ file tickets.txt
    private void loadFromFile() {
        tickets.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return; // Nếu chưa có file dữ liệu thì bỏ qua
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // SỬA ĐỔI: Bỏ qua dòng trống HOẶC dòng chú thích bắt đầu bằng dấu #
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|");

                String ticketId = parts[0];
                String readerId = parts[1];
                LocalDate borrowDate = LocalDate.parse(parts[2]);
                LocalDate dueDate = LocalDate.parse(parts[3]);
                LocalDate returnDate = parts[4].equals("null") ? null : LocalDate.parse(parts[4]);
                String status = parts[5];
                double fineAmount = Double.parseDouble(parts[6]);

                List<BorrowTicketDetail> details = new ArrayList<>();
                if (parts.length > 7 && !parts[7].equals("null") && !parts[7].isEmpty()) {
                    String[] detailsPart = parts[7].split(",");
                    for (String d : detailsPart) {
                        String[] subParts = d.split(":");
                        details.add(new BorrowTicketDetail(subParts[0], Integer.parseInt(subParts[1])));
                    }
                }
                tickets.add(new BorrowTicket(ticketId, readerId, borrowDate, dueDate, returnDate, status, fineAmount, details));
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file tickets.txt: " + e.getMessage());
        }
    }

    // Ghi toàn bộ danh sách ra file tickets.txt bằng kỹ thuật File IO
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // SỬA ĐỔI: Tự động ghi tiêu đề và cấu trúc dữ liệu vào đầu file để không bị mất comment
            bw.write("# ====================================================================================================\n");
            bw.write("# CẤU TRÚC DỮ LIỆU FILE TICKETS.TXT (QUẢN LÝ PHIẾU MƯỢN / TRẢ SÁCH)\n");
            bw.write("# ====================================================================================================\n");
            bw.write("# Định dạng các cột phân tách bằng dấu gạch đứng (|):\n");
            bw.write("# Quy luật: MãPhiếu|MãBạnĐọc|NgàyMượn|NgàyHẹnTrả|NgàyTrảThựcTế|TrạngThái|TiềnPhạt|ChiTiếtMượn\n");
            bw.write("#\n");
            bw.write("# Trong đó:\n");
            bw.write("#   - NgàyTrảThựcTế: Ghi 'null' nếu sách chưa được trả.\n");
            bw.write("#   - TrạngThái: 'Đang mượn' hoặc 'Đã trả'.\n");
            bw.write("#   - ChiTiếtMượn: Định dạng MãSách:SốLượng. Nếu mượn nhiều loại sách thì phân tách bằng dấu phẩy (,).\n");
            bw.write("# ====================================================================================================\n\n");

            // Tiến hành ghi danh sách phiếu mượn thực tế xuống dưới file
            for (BorrowTicket t : tickets) {
                StringBuilder detailsStr = new StringBuilder();
                if (t.getDetails().isEmpty()) {
                    detailsStr.append("null");
                } else {
                    for (int i = 0; i < t.getDetails().size(); i++) {
                        detailsStr.append(t.getDetails().get(i).toString());
                        if (i < t.getDetails().size() - 1) detailsStr.append(",");
                    }
                }
                bw.write(String.format("%s|%s|%s|%s|%s|%s|%.1f|%s\n",
                        t.getTicketId(),
                        t.getReaderId(),
                        t.getBorrowDate(),
                        t.getDueDate(),
                        t.getReturnDate() == null ? "null" : t.getReturnDate(),
                        t.getStatus(),
                        t.getFineAmount(),
                        detailsStr.toString()
                ));
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi dữ liệu vào file tickets.txt: " + e.getMessage());
        }
    }
}