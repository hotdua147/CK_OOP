package com.library.repository;

import com.library.model.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tầng quản lý lưu trữ dữ liệu Sách - Đọc ghi file cứng data/books.txt
 * Đã được chuẩn hóa tương thích đa nền tảng và bọc phòng thủ lỗi bóc tách dữ liệu.
 */
public class BookRepository {
    private static final String FILE_PATH = "data/books.txt";
    private final List<Book> books;

    public BookRepository() {
        this.books = new ArrayList<>();
        loadFromFile(); // Tự động nạp dữ liệu từ file lên RAM khi khởi tạo
    }

    /**
     * Lấy toàn bộ danh sách sách dưới dạng Chỉ đọc (Bảo vệ dữ liệu RAM)
     */
    public List<Book> getAll() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Tìm kiếm sách theo mã ID (Không phân biệt hoa thường)
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
     * Cập nhật thông tin hoặc số lượng sách trên RAM và đồng bộ xuống file
     */
    public void update(Book updatedBook) {
        if (updatedBook == null) return;

        boolean isUpdated = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId().equalsIgnoreCase(updatedBook.getBookId())) {
                books.set(i, updatedBook); // Cập nhật trên RAM
                isUpdated = true;
                break;
            }
        }

        if (isUpdated) {
            saveToFile(); // Đồng bộ ngay xuống file text
        }
    }

    /**
     * Đọc dữ liệu từ file books.txt lên bộ nhớ RAM
     */
    private void loadFromFile() {
        books.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // Tạo dữ liệu giả định ban đầu nếu file chưa tồn tại để hệ thống có thể chạy ngay
            initMockData();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Bỏ qua dòng trống và dòng chú thích
                }

                try {
                    // Định dạng file quy ước: MãSách|TênSách|SốLượng
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        String bookId = parts[0].trim();
                        String title = parts[1].trim();
                        int quantity = Integer.parseInt(parts[2].trim());

                        books.add(new Book(bookId, title, quantity));
                    }
                } catch (Exception e) {
                    System.err.println("[BookRepository] Bỏ qua dòng lỗi định dạng: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi nghiêm trọng khi đọc file dữ liệu sách: " + e.getMessage());
        }
    }

    /**
     * Ghi ngược dữ liệu từ RAM xuống file cứng đa nền tảng
     */
    private void saveToFile() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Tự động tạo thư mục data/ nếu chưa có
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("# =================================================="); bw.newLine();
            bw.write("# CẤU TRÚC DỮ LIỆU FILE BOOKS.TXT (QUẢN LÝ KHO SÁCH)"); bw.newLine();
            bw.write("# Quy luật định dạng: MãSách|TênSách|SốLượng Kho"); bw.newLine();
            bw.write("# =================================================="); bw.newLine();
            bw.newLine();

            for (Book b : books) {
                bw.write(String.format("%s|%s|%d", b.getBookId(), b.getTitle(), b.getQuantity()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[BookRepository] Lỗi nghiêm trọng khi ghi file dữ liệu sách: " + e.getMessage());
        }
    }

    /**
     * Khởi tạo kho dữ liệu sách mẫu ban đầu nếu máy chạy lần đầu chưa có file
     */
    private void initMockData() {
        books.add(new Book("B001", "Lap trinh Java can ban", 15));
        books.add(new Book("B002", "Cau truc du lieu va Giai thuat", 8));
        books.add(new Book("B003", "Thiet ke huong doi tuong OOP", 12));
        books.add(new Book("B004", "Co so du lieu va SQL", 5));
        books.add(new Book("B005", "Phan tich va Thiet ke he thong", 20));
        saveToFile(); // Lưu ngay ra file text
    }
}