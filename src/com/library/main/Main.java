package com.library.main;

import com.library.model.*;
import com.library.repository.*;
import com.library.service.BorrowReturnService;
import com.library.exception.LibraryException;
import com.library.utils.InputUtils;
import java.util.List;

/**
 * Lớp khởi chạy trung tâm của toàn bộ hệ thống (Giao diện CLI Console UI).
 * Đã tích hợp bộ điều khiển menu thông minh, chống crash tuyệt đối và bẫy lỗi tập trung.
 */
public class Main {
    // Khởi tạo tập trung các tầng quản lý dữ liệu lưu trữ (Nạp dữ liệu từ file cứng lên RAM khi bật ứng dụng)
    private static final BorrowTicketRepository borrowTicketRepository = new BorrowTicketRepository();
    private static final BookRepository bookRepository = new BookRepository();
    private static final ReaderRepository readerRepository = new ReaderRepository();

    // Tiêm (Inject) các repository vào tầng service xử lý nghiệp vụ lõi
    private static final BorrowReturnService borrowReturnService = new BorrowReturnService(
            borrowTicketRepository, bookRepository, readerRepository
    );

    public static void main(String[] args) {
        boolean isRunning = true;

        System.out.println("==================================================================");
        System.out.println("       CHƯƠNG TRÌNH QUẢN LÝ MƯỢN TRẢ SÁCH THƯ VIỆN (NHÓM 4)      ");
        System.out.println("==================================================================");

        while (isRunning) {
            System.out.println("\n╔══════════════════════ MENU CHỨC NĂNG ══════════════════════╗");
            System.out.println("║ 1. Hiển thị danh sách sách có trong thư viện               ║");
            System.out.println("║ 2. Tìm kiếm thông tin sách theo mã                         ║");
            System.out.println("║ 3. Đăng ký mượn sách (Tạo phiếu mượn mới)                  ║");
            System.out.println("║ 4. Xử lý trả sách & Tính phí phạt quá hạn (Đa hình)         ║");
            System.out.println("║ 5. Hiển thị danh sách độc giả (Bạn đọc)                     ║");
            System.out.println("║ 6. Hiển thị danh sách toàn bộ phiếu mượn                   ║");
            System.out.println("║ 7. Thoát chương trình                                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

            // Sử dụng tiện ích InputUtils chặn đứng hoàn toàn nguy cơ gõ chữ gây crash phần mềm
            int choice = InputUtils.readInt("👉 Mời thủ thư chọn chức năng (1-7): ", 1, 7);

            switch (choice) {
                case 1:
                    showAllBooks();
                    break;
                case 2:
                    searchBookById();
                    break;
                case 3:
                    handleBorrowBook();
                    break;
                case 4:
                    handleReturnBook();
                    break;
                case 5:
                    showAllReaders();
                    break;
                case 6:
                    showAllTickets();
                    break;
                case 7:
                    System.out.println("\n👋 Hệ thống đang đóng kết nối và lưu dữ liệu an toàn. Tạm biệt!");
                    isRunning = false;
                    break;
            }
        }
    }

    /**
     * Chức năng 1: Hiển thị danh sách sách (Đã bổ sung đủ 6 cột dữ liệu)
     */
    private static void showAllBooks() {
        System.out.println("\n📚 >>> DANH SÁCH SÁCH TRONG THƯ VIỆN <<<");
        List<Book> books = bookRepository.getAll();
        if (books.isEmpty()) {
            System.out.println("❌ Kho sách hiện tại trống.");
            return;
        }
        System.out.printf("%-8s | %-30s | %-18s | %-12s | %-8s | %-10s\n",
                "Mã Sách", "Tên Cuốn Sách", "Tác Giả", "Thể Loại", "Tồn Kho", "Giá Trị");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf("%-8s | %-30s | %-18s | %-12s | %-8d | %,.0fđ\n",
                    b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getQuantity(), b.getPrice());
        }
    }

    /**
     * Chức năng 2: Tìm kiếm sách theo mã
     */
    private static void searchBookById() {
        System.out.println("\n🔎 >>> TÌM KIẾM SÁCH THEO MÃ <<<");
        String bookId = InputUtils.readString("Nhập mã sách cần tìm (Ví dụ: B001): ");
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            System.out.println("❌ Không tìm thấy cuốn sách nào ứng với mã '" + bookId + "'.");
        } else {
            System.out.println("✅ Đã tìm thấy thông tin cuốn sách:");
            System.out.println(" - Mã cuốn sách: " + book.getBookId());
            System.out.println(" - Tên cuốn sách: " + book.getTitle());
            System.out.println(" - Tác giả      : " + book.getAuthor());
            System.out.println(" - Thể loại     : " + book.getCategory());
            System.out.println(" - Số lượng kho : " + book.getQuantity() + " cuốn còn lại.");
            System.out.printf(" - Giá trị sách : %,.0f VND\n", book.getPrice());
        }
    }

    /**
     * Chức năng 3: Xử lý đăng ký mượn sách mới
     */
    private static void handleBorrowBook() {
        System.out.println("\n📝 >>> ĐĂNG KÝ MƯỢN SÁCH MỚI <<<");
        String readerId = InputUtils.readString("Nhập mã bạn đọc (Mã BD): ");
        String bookId = InputUtils.readString("Nhập mã cuốn sách mượn: ");
        int quantity = InputUtils.readPositiveInt("Nhập số lượng sách mượn: ");

        try {
            // Thực hiện gọi hàm xử lý logic và trừ kho tại tầng nghiệp vụ Service
            borrowReturnService.borrowBook(readerId, bookId, quantity);
        } catch (LibraryException e) {
            // Bắt lỗi nghiệp vụ tùy biến (hết sách, mượn quá hạn mức...) và in thông báo an toàn
            System.out.println("\n🛑 LỖI NGHIỆP VỤ: " + e.getMessage());
        }
    }

    /**
     * Chức năng 4: Xử lý trả sách quá hạn và tính phạt đa hình
     */
    private static void handleReturnBook() {
        System.out.println("\n🔄 >>> XỬ LÝ TRẢ SÁCH & TÍNH PHẠT <<<");
        String ticketId = InputUtils.readString("Nhập mã số phiếu mượn cần xử lý (Ví dụ: PT001): ");

        try {
            // Thực hiện gọi hàm hoàn kho và chạy thuật toán tính phạt dựa trên Strategy Pattern
            borrowReturnService.returnBook(ticketId);
        } catch (LibraryException e) {
            System.out.println("\n🛑 LỖI NGHIỆP VỤ: " + e.getMessage());
        }
    }

    /**
     * Chức năng 5: Hiển thị danh sách độc giả
     */
    private static void showAllReaders() {
        System.out.println("\n👥 >>> DANH SÁCH BẠN ĐỌC ĐÃ ĐĂNG KÝ <<<");
        List<Reader> readers = readerRepository.getAll();
        if (readers.isEmpty()) {
            System.out.println("❌ Hệ thống chưa ghi nhận bạn đọc nào.");
            return;
        }
        System.out.printf("%-10s | %-22s | %-13s | %-20s | %s\n", "Mã BD", "Họ Tên", "Số Điện Thoại", "Phân Loại Bạn Đọc", "Hạn Mức");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Reader r : readers) {
            System.out.printf("%-10s | %-22s | %-13s | %-20s | %d cuốn\n",
                    r.getUserId(), r.getFullName(), r.getPhoneNumber(), r.getReaderType(), r.getMaxBorrowLimit());
        }
    }

    /**
     * Chức năng 6: Xem toàn bộ danh sách phiếu mượn/trả trong hệ thống
     */
    private static void showAllTickets() {
        System.out.println("\n📊 >>> DANH SÁCH TOÀN BỘ PHIẾU MƯỢN TRONG HỆ THỐNG <<<");
        List<BorrowTicket> tickets = borrowTicketRepository.getAll();

        if (tickets.isEmpty()) {
            System.out.println("ℹ️ Hiện tại chưa có phiếu mượn nào được ghi nhận trên hệ thống.");
            return;
        }

        // Khung tiêu đề hiển thị chuẩn hóa dữ liệu bảng biểu
        System.out.printf("%-10s | %-10s | %-11s | %-11s | %-12s | %-11s | %-11s | %s\n",
                "Mã Phiếu", "Mã Độc Giả", "Ngày Mượn", "Hạn Trả", "Ngày Trả TT", "Trạng Thái", "Tiền Phạt", "Chi Tiết Sách");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        for (BorrowTicket t : tickets) {
            StringBuilder booksStr = new StringBuilder();
            for (int i = 0; i < t.getDetails().size(); i++) {
                booksStr.append(t.getDetails().get(i).toString());
                if (i < t.getDetails().size() - 1) booksStr.append(", ");
            }

            // ĐỒNG BỘ: Thay chữ text "null" thô kệch thành chữ "Chưa trả" để tối ưu trải nghiệm người dùng
            String returnDateStr = (t.getReturnDate() == null) ? "Chưa trả" : t.getReturnDate().toString();

            System.out.printf("%-10s | %-10s | %-11s | %-11s | %-12s | %-11s | %-11.0f | %s\n",
                    t.getTicketId(),
                    t.getReaderId(),
                    t.getBorrowDate(),
                    t.getDueDate(),
                    returnDateStr,
                    t.getStatusValue(),
                    t.getFineAmount(),
                    booksStr.toString()
            );
        }
    }
}