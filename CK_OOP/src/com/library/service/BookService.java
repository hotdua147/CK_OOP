package com.library.service;

import com.library.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class BookService {

    private BookRepository repository;
    private List<Book> books;

    public BookService() {
        repository = new BookRepository();
        books = repository.loadBooks();
    }

    public void displayBooks() {

        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book findById(String id) {

        for (Book book : books) {

            if (book.getBookId().equalsIgnoreCase(id)) {
                return book;
            }
        }

        return null;
    }

    public List<Book> findByName(String keyword) {

        List<Book> result = new ArrayList<>();

        for (Book book : books) {

            if (book.getTitle()
                    .toLowerCase()
                    .contains(keyword.toLowerCase())) {

                result.add(book);
            }
        }

        return result;
    }

    public boolean isAvailable(String bookId, int quantity) {

        Book book = findById(bookId);

        if (book == null) {
            return false;
        }

        return book.getQuantity() >= quantity;
    }

    public List<Book> getAllBooks() {
        return books;
    }
}