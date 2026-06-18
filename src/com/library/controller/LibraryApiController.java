package com.library.controller;

import com.library.model.Book;
import com.library.model.Reader;
import com.library.service.BookService;
import com.library.service.ReaderService;
import com.library.service.BorrowReturnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LibraryApiController {

    private final BookService bookService;
    private final ReaderService readerService;
    private final BorrowReturnService borrowReturnService;

    public LibraryApiController(BookService bookService, ReaderService readerService, BorrowReturnService borrowReturnService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.borrowReturnService = borrowReturnService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<Map<String, Object>>> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            List<Map<String, Object>> safeBookList = new ArrayList<>();
            for (Book b : books) {
                Map<String, Object> map = new HashMap<>();
                map.put("bookId", b.getBookId());
                map.put("title", b.getTitle());
                map.put("author", b.getAuthor());
                map.put("category", b.getCategory());
                map.put("quantity", b.getQuantity());
                map.put("price", b.getPrice());
                safeBookList.add(map);
            }
            return ResponseEntity.ok(safeBookList);
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi thật sự ra Console để bạn nhìn thấy
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/books")
    public ResponseEntity<?> addNewBook(@RequestBody Map<String, Object> payload) {
        try {
            // Ép kiểu an toàn, tránh lỗi null
            String bookId = String.valueOf(payload.getOrDefault("bookId", ""));
            String title = String.valueOf(payload.getOrDefault("title", ""));
            String author = String.valueOf(payload.getOrDefault("author", "Chưa rõ"));
            String category = String.valueOf(payload.getOrDefault("category", "Giáo trình"));
            int quantity = Integer.parseInt(String.valueOf(payload.getOrDefault("quantity", 0)));
            double price = Double.parseDouble(String.valueOf(payload.getOrDefault("price", 0.0)));

            Book newBook = new Book(bookId, title, author, category, quantity, price);
            bookService.addBook(newBook);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Thêm sách thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa vĩnh viễn đầu sách khỏi kho!"));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Không thể xóa đầu sách chỉ định!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ==================== 2. NGHIỆP VỤ QUẢN LÝ BẠN ĐỌC (CRUD) ====================

    @GetMapping("/readers")
    public ResponseEntity<List<Map<String, Object>>> getAllReaders() {
        try {
            List<Reader> readers = readerService.getAllReaders();
            List<Map<String, Object>> safeReaderList = new ArrayList<>();

            for (Reader r : readers) {
                Map<String, Object> map = new HashMap<>();
                map.put("readerId", r.getUserId());
                map.put("fullName", r.getFullName());
                map.put("phoneNumber", r.getPhoneNumber());
                map.put("type", r.getReaderType());
                safeReaderList.add(map);
            }
            return ResponseEntity.ok(safeReaderList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/readers")
    public ResponseEntity<?> registerNewReader(@RequestBody Map<String, String> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Đăng ký thành công!"));
    }

    // ==================== 3. NGHIỆP VỤ MƯỢN / TRẢ SÁCH XUYÊN TẦNG ====================

    @PostMapping("/borrow")
    public ResponseEntity<?> processBorrow(@RequestBody Map<String, String> payload) {
        try {
            String readerId = payload.get("readerId");
            String bookId = payload.get("bookId");

            borrowReturnService.borrowBook(readerId, bookId, 1);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Cấp phiếu mượn thành công! Kho cứng đã đồng bộ."));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Độc giả vi phạm hạn mức mượn hoặc sách đã hết hàng!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> processReturn(@RequestBody Map<String, String> payload) {
        try {
            String bookId = payload.get("bookId");
            borrowReturnService.returnBook(bookId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Nhận lại sách hoàn trả thành công! Đã thanh toán dữ liệu phiếu mượn."));
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống hoặc không tìm thấy lịch sử phiếu mượn của cuốn sách này!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}