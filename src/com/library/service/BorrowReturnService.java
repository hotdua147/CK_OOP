package com.library.service;

import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.utils.IdGenerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Tầng xử lý logic nghiệp vụ Mượn - Trả sách
 * Đã được tối ưu hóa đa hình, xử lý ngoại lệ chặt chẽ và bảo vệ toàn vẹn dữ liệu
 */
public class BorrowReturnService {
    private final BorrowTicketRepository borrowTicketRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    public BorrowReturnService(BorrowTicketRepository borrowTicketRepository,
                               BookRepository bookRepository,
                               ReaderRepository readerRepository) {
        this.borrowTicketRepository = borrowTicketRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    /**
     * Nghiệp vụ 2.3: Thực hiện mượn sách (Nhánh hotdua147)
     */
    public void borrowBook(String readerId, String bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Lỗi nhập liệu: Số lượng sách muốn mượn phải lớn hơn 0!");
        }

        Reader reader = readerRepository.findById(readerId);
        if (reader == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Bạn đọc có mã '" + readerId + "' không tồn tại trên hệ thống!");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Sách có mã '" + bookId + "' không tồn tại trên hệ thống!");
        }

        if (book.getQuantity() < quantity) {
            throw new IllegalStateException("Lỗi kho hàng: Sách '" + book.getTitle() + "' không đủ số lượng trong kho! (Hiện còn: " + book.getQuantity() + " cuốn).");
        }

        int maxLimit = reader.getMaxBorrowLimit();
        int currentBorrowingCount = 0;
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();

        for (BorrowTicket ticket : allTickets) {
            if (ticket.getReaderId().equalsIgnoreCase(readerId) && ticket.getStatus() == BorrowTicket.TicketStatus.BORROWING) {
                for (BorrowTicketDetail detail : ticket.getDetails()) {
                    currentBorrowingCount += detail.getQuantity();
                }
            }
        }

        if (currentBorrowingCount + quantity > maxLimit) {
            throw new IllegalStateException("Lỗi hạn mức: Không cho phép mượn! Bạn đọc hiện đang mượn "
                    + currentBorrowingCount + " quyển. Hạn mức tối đa của loại độc giả này là " + maxLimit + " quyển.");
        }

        String ticketId = IdGenerator.generateTicketId();
        LocalDate borrowDate = LocalDate.now();
        int borrowDaysLimit = reader.getBorrowDaysLimit();
        LocalDate dueDate = borrowDate.plusDays(borrowDaysLimit);

        BorrowTicket newTicket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate);
        BorrowTicketDetail detail = new BorrowTicketDetail(bookId, quantity);
        newTicket.addDetail(detail);

        try {
            borrowTicketRepository.add(newTicket);
            book.setQuantity(book.getQuantity() - quantity);
            bookRepository.update(book);
            System.out.println("Chúc mừng: Đã lập phiếu mượn thành công! Mã phiếu: " + ticketId);
        } catch (Exception e) {
            throw new IllegalStateException("Lỗi hệ thống cứng: Không thể hoàn tất giao dịch mượn sách. Chi tiết: " + e.getMessage());
        }
    }

    /**
     * Nghiệp vụ 2.4: Thực hiện trả sách và tính phạt trễ hạn tự động (Nhánh manh_dat)
     */
    public void returnBook(String ticketId) {
        // 1. Kiểm tra sự tồn tại của phiếu mượn
        BorrowTicket ticket = borrowTicketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Không tìm thấy phiếu mượn có mã '" + ticketId + "' trên hệ thống!");
        }

        // 2. Chặn lỗi logic nếu phiếu đã trả rồi
        if (ticket.getStatus() == BorrowTicket.TicketStatus.RETURNED) {
            throw new IllegalStateException("Lỗi logic: Phiếu mượn này đã được hoàn thành (trả sách) từ trước!");
        }

        // 3. Tìm thông tin Bạn đọc tương ứng để làm căn cứ tính phạt
        Reader reader = readerRepository.findById(ticket.getReaderId());
        if (reader == null) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Bạn đọc '" + ticket.getReaderId() + "' ghi trên phiếu không tồn tại!");
        }

        // 4. Thiết lập ngày trả thực tế và tính toán số ngày quá hạn
        LocalDate returnDate = LocalDate.now();
        ticket.setReturnDate(returnDate);

        long daysBetween = ChronoUnit.DAYS.between(ticket.getDueDate(), returnDate);
        int overdueDays = daysBetween > 0 ? (int) daysBetween : 0;

        // 5. Khớp nối Strategy Pattern: Gọi hàm tính tiền phạt động theo loại Bạn đọc
        double fineAmount = calculateFine(reader, overdueDays);
        ticket.setFineAmount(fineAmount);

        // 6. Chuyển đổi trạng thái phiếu mượn
        ticket.setStatus(BorrowTicket.TicketStatus.RETURNED);

        try {
            // 7. Quy trình hoàn kho sách (Cộng lại số lượng vào kho)
            for (BorrowTicketDetail detail : ticket.getDetails()) {
                Book book = bookRepository.findById(detail.getBookId());
                if (book != null) {
                    book.setQuantity(book.getQuantity() + detail.getQuantity());
                    bookRepository.update(book); // Đồng bộ file books.txt
                }
            }

            // 8. Đồng bộ trạng thái phiếu mượn mới nhất xuống file tickets.txt
            borrowTicketRepository.update(ticket);

            System.out.println("\n=== HỆ THỐNG XỬ LÝ TRẢ SÁCH THÀNH CÔNG ===");
            System.out.println(" Mã phiếu mượn : " + ticketId);
            System.out.println(" Số ngày trễ hạn: " + overdueDays + " ngày");
            System.out.println(" Tiền phạt trễ  : " + String.format("%,.0f", fineAmount) + " VND");
            System.out.println("===========================================");

        } catch (Exception e) {
            throw new IllegalStateException("Lỗi hệ thống cứng: Không thể ghi nhận trả sách. Chi tiết: " + e.getMessage());
        }
    }

    /**
     * Nghiệp vụ tính tiền phạt trễ hạn sử dụng Strategy Pattern.
     */
    public double calculateFine(Reader reader, int overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return reader.getFinePolicy().calculateFine(overdueDays);
    }
}