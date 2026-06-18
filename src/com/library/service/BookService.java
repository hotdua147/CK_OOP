package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
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
        return bookRepository.getAll();
    }

    /**
     * Tìm kiếm một cuốn sách cụ thể dựa vào mã sách
     */
    public Book getBookById(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách tìm kiếm không được để trống!");
        }

        Book book = bookRepository.findById(bookId.trim());
        if (book == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Không tìm thấy sách có mã '" + bookId + "' trong hệ thống!");
        }
        return book;
    }

    /**
     * THÊM MỚI: Xử lý logic nghiệp vụ thêm sách mới (Kiểm tra trùng lặp mã sách)
     */
    public void addBook(Book book) {
        if (book == null || book.getBookId() == null || book.getBookId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Thông tin sách hoặc mã sách không được để trống!");
        }
        // Kiểm tra xem đầu sách này đã tồn tại trong file text chưa
        if (bookRepository.findById(book.getBookId().trim()) != null) {
            throw new IllegalArgumentException("Lỗi: Mã sách '" + book.getBookId() + "' đã tồn tại trong kho dữ liệu!");
        }
        bookRepository.addBook(book);
    }

    /**
     * THÊM MỚI: Xử lý logic nghiệp vụ xóa bỏ đầu sách khỏi kho dữ liệu thư viện
     */
    public void deleteBook(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã sách cần xóa không hợp lệ!");
        }
        if (bookRepository.findById(bookId.trim()) == null) {
            throw new IllegalArgumentException("Lỗi: Không tìm thấy đầu sách mang mã '" + bookId + "' trong kho để xóa!");
        }
        bookRepository.deleteBook(bookId);
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

        bookRepository.update(book);
        System.out.println("[BookService] Cập nhật số lượng sách '" + book.getTitle() + "' thành công. (Kho mới: " + newQuantity + ").");
    }
}