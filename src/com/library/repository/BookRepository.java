package com.library.repository;

import org.springframework.stereotype.Repository;
import com.library.model.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
/**
 * Tầng quản lý lưu trữ dữ liệu Sách - Đọc ghi file văn bản 'data/books.txt'
 * NÂNG CẤP: Quản lý đầy đủ cấu trúc dữ liệu 6 thuộc tính theo yêu cầu đề bài lớn.
 */
public class BookRepository {
    private static final String FILE_PATH = "data/books.txt";
    private final List<Book> books = new ArrayList<>();

    /**
     * Khởi tạo tầng lưu trữ và tự động nạp dữ liệu từ file văn bản lên bộ nhớ RAM.
     */
    public BookRepository() {
        loadFromFile();
    }

    /**
     * Lấy danh sách toàn bộ sách hiện có trên bộ nhớ RAM dưới dạng chỉ đọc.
     * Áp dụng tính năng Defensive Copy để bảo vệ cấu trúc danh sách gốc.
     */
    public List<Book> getAll() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Tìm kiếm một cuốn sách cụ thể dựa trên Mã sách (Không phân biệt chữ hoa, chữ thường).
     */
    public Book findById(String bookId) {
        if (bookId == null) return null;
        String trimmedId = bookId.trim();
        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(trimmedId)) {
                return book;
            }
        }
        return null;
    }

    /**
     * THÊM MỚI: Thêm đầu sách mới vào danh sách RAM và đồng bộ xuống file text cứng
     */
    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
            saveToFile(); // Tự động đồng bộ ghi đè xuống file books.txt
        }
    }

    /**
     * THÊM MỚI: Xóa vĩnh viễn đầu sách khỏi bộ nhớ RAM và đồng bộ lại file text
     */
    public void deleteBook(String bookId) {
        if (bookId != null) {
            books.removeIf(b -> b.getBookId().equalsIgnoreCase(bookId.trim()));
            saveToFile(); // Ghi lại file văn bản mới sau khi đã xóa phần tử
        }
    }

    /**
     * Cập nhật thông tin một cuốn sách trên bộ nhớ RAM và tự động đồng bộ xuống file text cứng.
     */
    public void update(Book updatedBook) {
        if (updatedBook == null) return;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId().equalsIgnoreCase(updatedBook.getBookId())) {
                books.set(i, updatedBook);
                saveToFile(); // Tự động đồng bộ thời gian thực xuống file text
                return;
            }
        }
    }

    /**
     * Đọc và bóc tách dữ liệu từ file text 'data/books.txt' đưa lên RAM.
     */
    private void loadFromFile() {
        books.clear();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            initMockData();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        String id = parts[0].trim();
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        String category = parts[3].trim();
                        int quantity = Integer.parseInt(parts[4].trim());
                        double price = Double.parseDouble(parts[5].trim());

                        books.add(new Book(id, title, author, category, quantity, price));
                    }
                    else if (parts.length == 3) {
                        String id = parts[0].trim();
                        String title = parts[1].trim();
                        int quantity = Integer.parseInt(parts[2].trim());

                        books.add(new Book(id, title, quantity));
                    }
                } catch (Exception e) {
                    System.err.println("[BookRepository] Bỏ qua dòng dữ liệu lỗi định dạng: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi nghiêm trọng xảy ra khi đọc file sách: " + e.getMessage());
        }
    }

    /**
     * Ghi đè toàn bộ danh sách sách từ bộ nhớ RAM xuống file văn bản 'data/books.txt'.
     */
    private void saveToFile() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("# MãSách|TênSách|TácGiả|ThểLoại|SốLượng|GiáTrị");
            bw.newLine();

            for (Book b : books) {
                bw.write(String.format("%s|%s|%s|%s|%d|%.0f",
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getQuantity(), b.getPrice()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi nghiêm trọng xảy ra khi ghi file sách: " + e.getMessage());
        }
    }

    /**
     * Khởi tạo bộ dữ liệu mẫu chuẩn 6 thuộc tính cho lần chạy đầu tiên.
     */
    private void initMockData() {
        books.add(new Book("B001", "Lap trinh Java can ban", "Nguyen Van A", "Giao trinh", 14, 50000.0));
        books.add(new Book("B002", "Cau truc du lieu va Giai thuat", "Tran Van B", "Giao trinh", 8, 60000.0));
        books.add(new Book("B003", "Thiet ke huong doi tuong OOP", "Le Thi C", "Tham khao", 12, 75000.0));
        books.add(new Book("B004", "Co so du lieu va SQL", "Phan Van D", "Giao trinh", 5, 55000.0));
        books.add(new Book("B005", "Phan tich va Thiet ke he thong", "Hoang Ngoc E", "Tham khao", 20, 90000.0));
        saveToFile();
    }
}