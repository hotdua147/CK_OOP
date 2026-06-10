package com.library.main;

import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.service.BorrowReturnService;

import java.util.Scanner;

/**
 * Lớp khởi chạy hệ thống (Giao diện CLI Console)
 * Đã tích hợp hoàn chỉnh luồng nghiệp vụ Mượn sách, Trả sách và cơ chế bắt lỗi tập trung an toàn.
 */
public class Main {
    // Khởi tạo tập trung các tầng quản lý dữ liệu (Nạp dữ liệu từ file text lên RAM)
    private static final BorrowTicketRepository borrowTicketRepository = new BorrowTicketRepository();
    private static final BookRepository bookRepository = new BookRepository();
    private static final ReaderRepository readerRepository = new ReaderRepository();

    // Tiêm (Inject) các repository vào tầng service xử lý nghiệp vụ lõi
    private static final BorrowReturnService borrowReturnService = new BorrowReturnService(
            borrowTicketRepository, bookRepository, readerRepository
    );


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("==========================================================");
        System.out.println("    CHƯƠNG TRÌNH QUẢN LÝ THƯ VIỆN ĐẠI HỌC (NHÓM 4)       ");
        System.out.println("==========================================================");

        while (isRunning) {
            printMenu();
            System.out.print("Chọn chức năng (1-4): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleBorrowBook(scanner);
                    break;
                case "2":
                    // ĐÃ KÍCH HOẠT: Khớp nối luồng xử lý Trả sách & Tính tiền phạt từ nhánh manh_dat
                    handleReturnBook(scanner);
                    break;
                case "3":
                    handleShowAllTickets();
                    break;
                case "4":
                    System.out.println("\nCảm ơn bạn đã sử dụng phần mềm! Hẹn gặp lại.");
                    isRunning = false;
                    break;
                default:
                    System.err.println("\nLựa chọn không hợp lệ! Vui lòng nhập số từ 1 đến 4.");
                    break;
            }

            // Thêm một khoảng nghỉ nhỏ giữa các lượt chọn menu để giao diện Console dễ nhìn hơn
            if (isRunning) {
                System.out.println("\nNhấn Enter để quay lại Menu chính...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /**
     * Hiển thị danh sách Menu chức năng chính
     */
    private static void printMenu() {
        System.out.println("\n--------- MENU QUẢN LÝ THƯ VIỆN ---------");
        System.out.println("1. Thực hiện Đăng ký Mượn sách");
        System.out.println("2. Thực hiện Trả sách & Tính phí phạt");
        System.out.println("3. Hiển thị danh sách tất cả Phiếu mượn");
        System.out.println("4. Thoát chương trình");
        System.out.println("-----------------------------------------");
    }

    /**
     * Điều khiển luồng nhập liệu cho nghiệp vụ Mượn sách (Tối ưu từ hotdua147)
     */
    private static void handleBorrowBook(Scanner scanner) {
        System.out.println("\n>>> THỰC HIỆN NGHIỆP VỤ MƯỢN SÁCH <<<");

        System.out.print("Nhập mã bạn đọc (ví dụ: SV001, GV001): ");
        String readerId = scanner.nextLine().trim();

        System.out.print("Nhập mã sách muốn mượn (ví dụ: B001): ");
        String bookId = scanner.nextLine().trim();

        System.out.print("Nhập số lượng mượn: ");
        String quantityStr = scanner.nextLine().trim();

        // Khối xử lý ngoại lệ tập trung: Đảm bảo dữ liệu nhập sai không làm sập phần mềm
        try {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Số lượng sách mượn bắt buộc phải là một số nguyên dương hợp lệ!");
            }

            // Gọi xuống tầng Service để thực thi xử lý nghiệp vụ cốt lõi
            borrowReturnService.borrowBook(readerId, bookId, quantity);

        } catch (IllegalArgumentException e) {
            System.err.println("\n[LỖI NHẬP LIỆU] " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("\n[VI PHẠM QUY ĐỊNH] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n[LỖI HỆ THỐNG NGOÀI Ý MUỐN] " + e.getMessage());
        }
    }

    /**
     * Điều khiển luồng nhập liệu cho nghiệp vụ Trả sách & Tính phạt (Leader tích hợp đồng bộ)
     */
    private static void handleReturnBook(Scanner scanner) {
        System.out.println("\n>>> THỰC HIỆN NGHIỆP VỤ TRẢ SÁCH & TÍNH PHẠT <<<");

        System.out.print("Nhập mã phiếu mượn cần trả (ví dụ: PT001): ");
        String ticketId = scanner.nextLine().trim();

        if (ticketId.isEmpty()) {
            System.err.println("\n[LỖI NHẬP LIỆU] Mã phiếu mượn không được để trống!");
            return;
        }

        // Áp dụng cơ chế bắt lỗi tập trung đồng bộ với luồng Mượn sách
        try {
            // Gọi xuống tầng Service tìm phiếu mượn cũ, cập nhật ngày trả thực tế, tính phạt Strategy và hoàn kho
            borrowReturnService.returnBook(ticketId);

        } catch (IllegalArgumentException e) {
            System.err.println("\n[LỖI NHẬP LIỆU] " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("\n[VI PHẠM QUY ĐỊNH] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n[LỖI HỆ THỐNG NGOÀI Ý MUỐN] " + e.getMessage());
        }
    }

    /**
     * Hiển thị toàn bộ dữ liệu phiếu mượn hiện có trên hệ thống RAM (được nạp từ file)
     */
    private static void handleShowAllTickets() {
        System.out.println("\n>>> DANH SÁCH TOÀN BỘ PHIẾU MƯỢN TRONG HỆ THỐNG <<<");
        var tickets = borrowTicketRepository.getAll();

        if (tickets.isEmpty()) {
            System.out.println("Hiện tại chưa có phiếu mượn nào được ghi nhận trên hệ thống.");
            return;
        }

        System.out.printf("%-10s | %-10s | %-11s | %-11s | %-11s | %-10s | %-10s | %s\n",
                "Mã Phiếu", "Mã Độc Giả", "Ngày Mượn", "Hạn Trả", "Ngày Trả TT", "Trạng Thái", "Tiền Phạt", "Chi Tiết Sách");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        for (var t : tickets) {
            StringBuilder booksStr = new StringBuilder();
            for (int i = 0; i < t.getDetails().size(); i++) {
                booksStr.append(t.getDetails().get(i).toString());
                if (i < t.getDetails().size() - 1) booksStr.append(", ");
            }

            System.out.printf("%-10s | %-10s | %-11s | %-11s | %-11s | %-10s | %-10.1f | %s\n",
                    t.getTicketId(),
                    t.getReaderId(),
                    t.getBorrowDate(),
                    t.getDueDate(),
                    t.getReturnDate() == null ? "Chưa trả" : t.getReturnDate(),
                    t.getStatusValue(),
                    t.getFineAmount(),
                    booksStr.toString()
            );
        }
    }
}