package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import java.util.List;

/**
 * Tầng xử lý logic nghiệp vụ liên quan đến Sách
 * Đã được đồng bộ hóa chính xác với các phương thức thực tế của BookRepository
 */
public class BookService {
    private final BookRepository bookRepository;

    /**
     * Constructor tiêm (Inject) BookRepository từ ngoài vào
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Lấy toàn bộ danh sách sách có trong thư viện để hiển thị lên Menu
     */
    public List<Book> getAllBooks() {
        // Gọi đúng phương thức lấy danh sách của BookRepository
        return bookRepository.getAll();
    }

    /**
     * Tìm kiếm một cuốn sách cụ thể dựa vào mã sách
     */
    public Book getBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách tìm kiếm không được để trống!");
        }

        // Đã đồng bộ theo phương thức tìm kiếm thực tế trong BookRepository của bạn
        Book book = bookRepository.findById(bookId.trim());
        if (book == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Không tìm thấy sách có mã '" + bookId + "' trong hệ thống!");
        }
        return book;
    }

    /**
     * Cập nhật số lượng sách trong kho
     */
    public void updateBookQuantity(String bookId, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Lỗi: Số lượng sách trong kho không được là số âm!");
        }

        Book book = getBookById(bookId);
        book.setQuantity(newQuantity);

        // Đồng bộ dữ liệu xuống file cứng thông qua Repository của bạn
        bookRepository.update(book);
        System.out.println("[BookService] Cập nhật số lượng sách '" + book.getTitle() + "' thành công. (Kho mới: " + newQuantity + ").");
    }
}