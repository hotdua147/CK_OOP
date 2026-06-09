package com.library.service;

import com.library.model.*;
import com.library.policy.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowReturnService {
    private List<BorrowTicket> borrowTickets;
    private List<Book> books;
    private List<ReturnRecord> returnRecords = new ArrayList<>();

    public BorrowReturnService(List<BorrowTicket> borrowTickets, List<Book> books) {
        this.borrowTickets = borrowTickets;
        this.books = books;
    }

    public void returnBook(String ticketId, String bookId, LocalDate actualReturnDate, String readerType) {
        System.out.println("\n=== DANG XU LY TRA SACH CHO PHIEU: " + ticketId + " ===");

        BorrowTicket targetTicket = null;
        for (BorrowTicket ticket : borrowTickets) {
            if (ticket.getTicketId().equalsIgnoreCase(ticketId)) {
                targetTicket = ticket;
                break;
            }
        }

        if (targetTicket == null) {
            System.out.println("LOI NGHIEP VU: Phieu muon '" + ticketId + "' khong ton tai tren he thong!");
            return;
        }

        if ("Da tra".equalsIgnoreCase(targetTicket.getStatus())) {
            System.out.println("LOI NGHIEP VU: Phieu muon nay da duoc xu ly 'Da tra' truoc do. Khong the tra lai!");
            return;
        }

        long lateDays = ChronoUnit.DAYS.between(targetTicket.getDueDate(), actualReturnDate);
        double fineAmount = 0.0;

        if (lateDays > 0) {
            FinePolicy policy;

            switch (readerType.trim().toLowerCase()) {
                case "sinh vien thuong":
                    policy = new NormalStudentFinePolicy();
                    break;

                case "sinh vien uu tien":
                    policy = new PriorityStudentFinePolicy();
                    break;

                case "giang vien":
                    policy = new LecturerFinePolicy();
                    break;

                default:
                    policy = new NormalStudentFinePolicy();
                    break;
            }

            fineAmount = policy.calculateFine((int) lateDays);

            System.out.println("CANH BAO: Tra sach tre " + lateDays + " ngay. Bat buoc tinh phi phat!");
        } else {
            System.out.println("Thong ke: Tra sach dung han hoac som han.");
        }

        boolean bookFound = false;

        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(bookId)) {
                book.setQuantity(book.getQuantity() + 1);

                System.out.println("Kho sach: Da tra 1 cuon. So luong hien tai cua ma sach '" +
                        bookId + "' tang len thanh: " + book.getQuantity());

                bookFound = true;
                break;
            }
        }

        if (!bookFound) {
            System.out.println("Luu y: Ma sach nay khong khop trong kho hien tai, nhung he thong van tiep tuc xu ly phieu.");
        }

        targetTicket.setStatus("Da tra");
        targetTicket.setActualReturnDate(actualReturnDate);

        ReturnRecord record = new ReturnRecord(
                ticketId,
                bookId,
                actualReturnDate,
                fineAmount
        );

        returnRecords.add(record);

        System.out.println("THANH CONG: Da bien nhan thong tin tra sach!");
        System.out.println("Chi tiet bien nhan: " + record);
    }

    public List<ReturnRecord> getReturnRecords() {
        return returnRecords;
    }
}