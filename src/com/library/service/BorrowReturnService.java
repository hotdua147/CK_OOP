package com.library.service;

import com.library.model.*;
import com.library.repository.*;
import com.library.utils.IdGenerator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowReturnService {
    private final BorrowTicketRepository borrowTicketRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    public BorrowReturnService(BorrowTicketRepository btr, BookRepository br, ReaderRepository rr) {
        this.borrowTicketRepository = btr; this.bookRepository = br; this.readerRepository = rr;
    }

    public void borrowBook(String readerId, String bookId, int quantity) {
        Reader reader = readerRepository.findById(readerId);
        Book book = bookRepository.findById(bookId);
        if (reader == null || book == null) throw new IllegalArgumentException("Dữ liệu không tồn tại!");

        String ticketId = IdGenerator.generateTicketId(borrowTicketRepository.getAll());
        BorrowTicket ticket = new BorrowTicket(ticketId, readerId, LocalDate.now(), LocalDate.now().plusDays(14));
        ticket.addDetail(new BorrowTicketDetail(bookId, quantity));

        borrowTicketRepository.add(ticket);
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.update(book);
    }

    public void returnBook(String ticketId) {
        BorrowTicket ticket = borrowTicketRepository.findById(ticketId);
        if (ticket == null) throw new IllegalArgumentException("Không tìm thấy phiếu!");

        Reader reader = readerRepository.findById(ticket.getReaderId());
        long overdueDays = ChronoUnit.DAYS.between(ticket.getDueDate(), LocalDate.now());
        double fine = (overdueDays > 0) ? (double) overdueDays * reader.getFinePerDay() : 0;

        ticket.setFineAmount(fine);
        ticket.setStatus(BorrowTicket.TicketStatus.RETURNED);
        borrowTicketRepository.update(ticket);
    }
}