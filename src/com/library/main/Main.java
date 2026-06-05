package com.library.main;

import com.library.model.Book;
import com.library.model.BorrowTicket;
import com.library.model.BorrowTicketDetail;
import com.library.model.LecturerReader;
import com.library.model.PriorityStudentReader;
import com.library.model.Reader;
import com.library.model.ReturnRecord;
import com.library.model.StudentReader;
import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.service.BookService;
import com.library.service.BorrowReturnService;
import com.library.service.ReaderService;
import com.library.service.policy.FinePolicy;
import com.library.service.policy.LecturerFinePolicy;
import com.library.service.policy.NormalStudentFinePolicy;
import com.library.service.policy.PriorityStudentFinePolicy;
import com.library.utils.IdGenerator;
import com.library.utils.ValidationUtils;

public class Main {
    public static void main(String[] args) {
        Object[] instances = {
                new Book(),
                new BorrowTicket(),
                new BorrowTicketDetail(),
                new ReturnRecord(),
                new Reader(),
                new StudentReader(),
                new PriorityStudentReader(),
                new LecturerReader(),
                new BookService(),
                new ReaderService(),
                new BorrowReturnService(),
                new BookRepository(),
                new ReaderRepository(),
                new BorrowTicketRepository(),
                new IdGenerator(),
                new ValidationUtils(),
                new LecturerFinePolicy(),
                new NormalStudentFinePolicy(),
                new PriorityStudentFinePolicy(),
                new FinePolicy() {
                }
        };

        System.out.println("Smoke test: created " + instances.length + " instances.");
        for (Object instance : instances) {
            System.out.println("- " + instance.getClass().getName());
        }
    }
}
