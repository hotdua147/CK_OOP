package com.library.service;

import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.utils.IdGenerator;

import java.time.LocalDate;
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
     * Nghiệp vụ 2.3: Thực hiện mượn sách
     */
    public void borrowBook(String readerId, String bookId, int quantity) {
        // 1. Kiểm tra số lượng mượn hợp lệ (> 0)
        if (quantity <= 0) {
            throw new IllegalArgumentException("Lỗi nhập liệu: Số lượng sách muốn mượn phải lớn hơn 0!"); //
        }

        // 2. Kiểm tra sự tồn tại của bạn đọc trong hệ thống
        Reader reader = readerRepository.findById(readerId); //
        if (reader == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Bạn đọc có mã '" + readerId + "' không tồn tại trên hệ thống!"); //
        }

        // 3. Kiểm tra sự tồn tại của sách trong hệ thống
        Book book = bookRepository.findById(bookId); //
        if (book == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Sách có mã '" + bookId + "' không tồn tại trên hệ thống!"); //
        }

        // 4. Kiểm tra số lượng sách trong kho có đủ đáp ứng hay không
        // Rút gọn biểu thức logic dư thừa
        if (book.getQuantity() < quantity) {
            throw new IllegalStateException("Lỗi kho hàng: Sách '" + book.getTitle() + "' không đủ số lượng trong kho! (Hiện còn: " + book.getQuantity() + " cuốn)."); //
        }

        // 5. Kiểm tra hạn mức mượn sách của bạn đọc
        // Sử dụng ĐA HÌNH (Polymorphism) để lấy hạn mức thay vì lạm dụng instanceof
        int maxLimit = reader.getMaxBorrowLimit();

        // Tính toán động tổng số sách mà độc giả này đang mượn thực tế
        int currentBorrowingCount = 0; //
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll(); //

        for (BorrowTicket ticket : allTickets) { //
            // Kiểm tra khớp mã bạn đọc và phiếu phải ở trạng thái đang mượn (sử dụng Enum an toàn)
            if (ticket.getReaderId().equalsIgnoreCase(readerId) && ticket.getStatus() == BorrowTicket.TicketStatus.BORROWING) {
                for (BorrowTicketDetail detail : ticket.getDetails()) { //
                    currentBorrowingCount += detail.getQuantity(); //
                }
            }
        }

        // Kiểm tra xem lượt mượn mới này có vượt hạn mức tối đa hay không
        if (currentBorrowingCount + quantity > maxLimit) {
            throw new IllegalStateException("Lỗi hạn mức: Không cho phép mượn! Bạn đọc hiện đang mượn " //
                    + currentBorrowingCount + " quyển. Hạn mức tối đa của loại độc giả này là " + maxLimit + " quyển."); //
        }

        // 6. Nếu hợp lệ: Tiến hành quy trình tạo phiếu mượn và đồng bộ dữ liệu
        String ticketId = IdGenerator.generateTicketId(); //
        LocalDate borrowDate = LocalDate.now(); //

        // Tận dụng tính đa hình để lấy số ngày mượn quy định động tùy loại độc giả (Sinh viên: 14 ngày, Giảng viên: 30 ngày,...)
        int borrowDaysLimit = reader.getBorrowDaysLimit();
        LocalDate dueDate = borrowDate.plusDays(borrowDaysLimit);

        // Khởi tạo đối tượng phiếu mượn mới
        BorrowTicket newTicket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate); //
        BorrowTicketDetail detail = new BorrowTicketDetail(bookId, quantity); //
        newTicket.addDetail(detail); //

        try {
            // ĐƯỢC CẢI TIẾN: Thực hiện ghi phiếu mượn vào file tickets.txt trước
            borrowTicketRepository.add(newTicket); //

            // Nếu tạo phiếu thành công tốt đẹp, mới tiến hành trừ kho sách và ghi file dữ liệu sách
            book.setQuantity(book.getQuantity() - quantity); //
            bookRepository.update(book); //

            System.out.println("Chúc mừng: Đã lập phiếu mượn thành công! Mã phiếu: " + ticketId);

        } catch (Exception e) {
            // Đảm bảo thông báo lỗi hệ thống I/O nếu việc ghi file cứng gặp sự cố ngầm
            throw new IllegalStateException("Lỗi hệ thống cứng: Không thể hoàn tất giao dịch mượn sách. Chi tiết: " + e.getMessage());
        }
    }
}