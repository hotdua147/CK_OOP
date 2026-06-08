package com.library.service;

import com.library.model.*;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.utils.IdGenerator;

import java.time.LocalDate;
import java.util.List;

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
    public void borrowBook(String readerId, String bookId, int quantity) throws Exception {
        // 1. Kiểm tra số lượng mượn hợp lệ (> 0)
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sách muốn mượn phải lớn hơn 0!");
        }

        // 2. Kiểm tra sự tồn tại của bạn đọc trong hệ thống
        Reader reader = readerRepository.findById(readerId);
        if (reader == null) {
            throw new Exception("Lỗi: Bạn đọc không tồn tại trên hệ thống!");
        }

        // 3. Kiểm tra sự tồn tại của sách
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new Exception("Lỗi: Sách yêu cầu mượn không tồn tại!");
        }

        // 4. Kiểm tra sách còn đủ số lượng trong kho không
        if (book.getQuantity() < quantity || book.getQuantity() == 0) {
            throw new Exception("Lỗi: Sách '" + book.getTitle() + "' đã hết hoặc không đủ số lượng trong kho!");
        }

        // 5. Kiểm tra hạn mức mượn tối đa dựa trên Polymorphism / Loại bạn đọc
        int maxLimit = 3; // Định mức mặc định của Sinh viên thường
        if (reader instanceof PriorityStudentReader) {
            maxLimit = 5; // Sinh viên ưu tiên được mượn nhiều hơn
        } else if (reader instanceof LecturerReader) {
            maxLimit = 10; // Giảng viên được mượn tối đa 10 quyển
        }

        // Tính tổng số sách đang mượn thực tế mà chưa trả (Trạng thái "Đang mượn")
        int currentBorrowingCount = 0;
        List<BorrowTicket> allTickets = borrowTicketRepository.getAll();
        for (BorrowTicket ticket : allTickets) {
            if (ticket.getReaderId().equals(readerId) && "Đang mượn".equalsIgnoreCase(ticket.getStatus())) {
                for (BorrowTicketDetail detail : ticket.getDetails()) {
                    currentBorrowingCount += detail.getQuantity();
                }
            }
        }

        // Kiểm tra xem lượt mượn mới này có vượt hạn mức tối đa hay không
        if (currentBorrowingCount + quantity > maxLimit) {
            throw new Exception("Lỗi: Không cho phép mượn! Bạn đọc hiện đang mượn "
                    + currentBorrowingCount + " quyển. Hạn mức tối đa được phép là " + maxLimit + " quyển.");
        }

        // 6. Nếu hợp lệ: Tiến hành tạo phiếu mượn
        String ticketId = IdGenerator.generateTicketId();
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // Mặc định thời gian hẹn trả là 14 ngày

        BorrowTicket newTicket = new BorrowTicket(ticketId, readerId, borrowDate, dueDate);
        BorrowTicketDetail detail = new BorrowTicketDetail(bookId, quantity);
        newTicket.addDetail(detail);

        // Giảm số lượng sách trong kho thực tế và cập nhật lại file dữ liệu sách
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.update(book);

        // Lưu phiếu mượn mới vào bộ nhớ & ghi tự động ra file tickets.txt
        borrowTicketRepository.add(newTicket);

        System.out.println(">>> THÀNH CÔNG: Đã tạo phiếu mượn sách mang mã số: " + ticketId);
    }
}