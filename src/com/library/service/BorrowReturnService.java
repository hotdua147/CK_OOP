package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.utils.IdGenerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Tầng xử lý nghiệp vụ trung tâm (Business Logic Layer) quản lý mượn và trả sách.
 * Kết nối đồng bộ dữ liệu giữa các kho lưu trữ và áp dụng Strategy Pattern tính phí phạt.
 */
public class BorrowReturnService {
    private final BorrowTicketRepository borrowTicketRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    /**
     * Constructor tiêm (Inject) các repository phụ thuộc vào để quản lý tập trung.
     */
    public BorrowReturnService(BorrowTicketRepository borrowTicketRepository,
                               BookRepository bookRepository,
                               ReaderRepository readerRepository) {
        this.borrowTicketRepository = borrowTicketRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    /**
     * NGHIỆP VỤ 1: XỬ LÝ ĐĂNG KÝ MƯỢN SÁCH mới
     * Kiểm tra điều kiện phòng vệ nghiêm ngặt trước khi cấp phiếu mượn.
     */
    public void borrowBook(String readerId, String bookId, int quantity) throws LibraryException {
        // 1. Kiểm tra sự tồn tại của Độc giả
        Reader reader = readerRepository.findById(readerId);
        if (reader == null) {
            throw new LibraryException("Mã bạn đọc '" + readerId + "' không tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra sự tồn tại của Sách trong kho
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new LibraryException("Mã sách '" + bookId + "' không tồn tại trong thư viện!");
        }

        // 3. Kiểm tra số lượng tồn kho của cuốn sách
        if (book.getQuantity() < quantity) {
            throw new LibraryException("Mượn thất bại! Sách '" + book.getTitle()
                    + "' hiện chỉ còn tồn " + book.getQuantity() + " cuốn (Yêu cầu mượn: " + quantity + ").");
        }

        // 4. Kiểm tra tổng hạn mức mượn sách hiện tại của Độc giả
        int currentlyBorrowedCount = countCurrentlyBorrowedBooks(readerId);
        if (currentlyBorrowedCount + quantity > reader.getMaxBorrowLimit()) {
            throw new LibraryException(String.format(
                    "Từ chối cấp phiếu! Độc giả [%s] đang mượn %d cuốn. "
                            + "Nếu mượn thêm %d cuốn sẽ vượt quá hạn mức tối đa của phân loại %s (%d cuốn).",
                    reader.getFullName(), currentlyBorrowedCount, quantity, reader.getReaderType(), reader.getMaxBorrowLimit()
            ));
        }

        // 5. Đủ điều kiện -> Tiến hành trừ kho sách vật lý
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.update(book);

        // 6. Tự động sinh mã phiếu tăng tiến PTxxx bằng IdGenerator
        String nextTicketId = IdGenerator.generateTicketId(borrowTicketRepository.getAll());

        // 7. Tạo phiếu mượn mới (Quy định mặc định thời gian mượn tối đa là 14 ngày)
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);
        BorrowTicket newTicket = new BorrowTicket(nextTicketId, readerId, borrowDate, dueDate);

        // Thêm chi tiết dòng sách đăng ký mượn vào phiếu
        newTicket.addDetail(new BorrowTicketDetail(bookId, quantity));

        // 8. Lưu phiếu mượn vào cơ sở dữ liệu cứng file tickets.txt
        borrowTicketRepository.add(newTicket);

        System.out.printf("\n🎉 ĐĂNG KÝ MƯỢN THÀNH CÔNG! Phiếu mượn [%s] đã được tạo.\n", nextTicketId);
        System.out.printf("   - Độc giả: %s (%s)\n", reader.getFullName(), reader.getReaderType());
        System.out.printf("   - Sách mượn: %s x %d cuốn\n", book.getTitle(), quantity);
        System.out.printf("   - Ngày mượn: %s | Hạn hẹn trả sách: %s\n", borrowDate, dueDate);
    }

    /**
     * NGHIỆP VỤ 2: XỬ LÝ TRẢ SÁCH VÀ TÍNH PHẠT ĐA HÌNH (Strategy Pattern)
     */
    public void returnBook(String ticketId) throws LibraryException {
        // 1. Kiểm tra sự tồn tại của phiếu mượn
        BorrowTicket ticket = borrowTicketRepository.findById(ticketId);
        if (ticket == null) {
            throw new LibraryException("Mã phiếu mượn '" + ticketId + "' không tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra xem phiếu này đã xử lý trả trước đó chưa
        if (ticket.getStatus() == BorrowTicket.TicketStatus.RETURNED) {
            throw new LibraryException("Phiếu mượn '" + ticketId + "' này đã được hoàn tất trả sách từ trước!");
        }

        // 3. Tìm thông tin độc giả sở hữu phiếu mượn này để lấy chiến lược phạt
        Reader reader = readerRepository.findById(ticket.getReaderId());
        if (reader == null) {
            throw new LibraryException("Lỗi hệ thống: Không tìm thấy chủ sở hữu của phiếu mượn này!");
        }

        // 4. Thiết lập ngày trả thực tế là ngày hôm nay
        LocalDate returnDate = LocalDate.now();
        ticket.setReturnDate(returnDate);

        // 5. Tính toán số ngày trễ hạn thực tế
        // Hàm ChronoUnit.DAYS.between sẽ tính khoảng cách ngày: (Ngày thực tế - Ngày hẹn trả)
        int overdueDays = (int) ChronoUnit.DAYS.between(ticket.getDueDate(), returnDate);

        // 6. ÁP DỤNG ĐA HÌNH STRATEGY PATTERN: Tự động tính tiền phạt dựa trên phân loại độc giả cụ thể
        double fineAmount = reader.getFinePolicy().calculateFine(overdueDays);
        ticket.setFineAmount(fineAmount);

        // 7. Thực hiện hoàn kho số lượng sách vật lý trở lại thư viện
        for (BorrowTicketDetail detail : ticket.getDetails()) {
            Book book = bookRepository.findById(detail.getBookId());
            if (book != null) {
                book.setQuantity(book.getQuantity() + detail.getQuantity());
                bookRepository.update(book); // Cập nhật lại kho sách cứng
            }
        }

        // 8. Chuyển đổi trạng thái phiếu mượn sang ĐÃ TRẢ và lưu đè file cứng
        ticket.setStatus(BorrowTicket.TicketStatus.RETURNED);
        borrowTicketRepository.update(ticket);

        // 9. In kết quả tổng biên bảm trả sách ra màn hình Console
        System.out.printf("\n✅ XỬ LÝ TRẢ SÁCH THÀNH CÔNG! Phiếu mượn [%s] đã đóng.\n", ticketId);
        System.out.println("   - Người trả: " + reader.getFullName());
        System.out.println("   - Số ngày quá hạn: " + (overdueDays > 0 ? overdueDays + " ngày" : "0 ngày (Đúng hạn)"));
        if (overdueDays > 0) {
            System.out.printf("   - Độc giả thuộc diện: %s (Định mức phạt: %,dđ/ngày)\n", reader.getReaderType(), reader.getFinePerDay());
            System.out.printf("   - 💸 SỐ TIỀN PHẠT THỰC THU: %,.0f VND\n", fineAmount);
        } else {
            System.out.println("   - ✨ Độc giả trả đúng hạn quy định, không phát sinh phí phạt.");
        }
    }

    /**
     * Hàm phụ trợ tính tổng số lượng sách một độc giả đang mượn thực tế trên RAM
     * (Chỉ tính các phiếu mượn đang có trạng thái nghiệp vụ là BORROWING - Đang mượn).
     */
    private int countCurrentlyBorrowedBooks(String readerId) {
        int total = 0;
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();
        for (BorrowTicket t : allTickets) {
            if (t.getReaderId().equalsIgnoreCase(readerId) && t.getStatus() == BorrowTicket.TicketStatus.BORROWING) {
                // Duyệt qua tất cả các dòng chi tiết của phiếu đó để cộng dồn số lượng
                for (BorrowTicketDetail detail : t.getDetails()) {
                    total += detail.getQuantity();
                }
            }
        }
        return total;
    }
}