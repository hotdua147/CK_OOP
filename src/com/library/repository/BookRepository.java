package com.library.repository;

import com.library.model.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     *
     * @return Danh sách Sách không thể sửa đổi cấu trúc từ bên ngoài.
     */
    public List<Book> getAll() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Tìm kiếm một cuốn sách cụ thể dựa trên Mã sách (Không phân biệt chữ hoa, chữ thường).
     *
     * @param bookId Mã cuốn sách cần tìm
     * @return Đối tượng Book nếu tìm thấy, ngược lại trả về null
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
     * Cập nhật thông tin một cuốn sách trên bộ nhớ RAM và tự động đồng bộ xuống file text cứng.
     *
     * @param updatedBook Đối tượng sách mang thông tin mới cần cập nhật
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

        // Nếu file không tồn tại, tiến hành sinh dữ liệu mẫu ban đầu để chạy ứng dụng
        if (!file.exists()) {
            initMockData();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Bỏ qua dòng trống hoặc dòng ghi chú (comment) bắt đầu bằng ký tự #
                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    String[] parts = line.split("\\|");
                    // Trường hợp 1: Dữ liệu chuẩn hóa mới đầy đủ 6 cột
                    if (parts.length >= 6) {
                        String id = parts[0].trim();
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        String category = parts[3].trim();
                        int quantity = Integer.parseInt(parts[4].trim());
                        double price = Double.parseDouble(parts[5].trim());

                        books.add(new Book(id, title, author, category, quantity, price));
                    }
                    // Trường hợp 2: Tương thích ngược với file mẫu cũ chỉ chứa 3 cột
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
            // Ghi dòng tiêu đề cấu trúc hướng dẫn cho file văn bản
            bw.write("# MãSách|TênSách|TácGiả|ThểLoại|SốLượng|GiáTrị");
            bw.newLine();

            for (Book b : books) {
                // Sử dụng định dạng %.0f để hiển thị giá tiền gọn gàng không dính đuôi thập phân .0
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
        saveToFile(); // Lưu ngay xuống file
    }
}