package com.library.repository;

import com.library.model.Reader;
import com.library.model.StudentReader;
import com.library.model.PriorityStudentReader;
import com.library.model.LecturerReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tầng quản lý lưu trữ dữ liệu Độc giả (Bạn đọc) - Đọc ghi file văn bản 'data/readers.txt'
 * ĐÃ CẬP NHẬT: Tích hợp thêm hàm add() và update() để sửa dứt điểm lỗi báo đỏ ở tầng Service.
 */
public class ReaderRepository {
    private static final String FILE_PATH = "data/readers.txt";
    private final List<Reader> readers = new ArrayList<>();

    /**
     * Khởi tạo tầng lưu trữ độc giả và tự động tải dữ liệu từ file văn bản lên bộ nhớ RAM.
     */
    public ReaderRepository() {
        loadFromFile();
    }

    /**
     * Lấy danh sách toàn bộ độc giả hiện có trên bộ nhớ RAM dưới dạng chỉ đọc.
     * Áp dụng tính năng Defensive Copy bảo vệ cấu trúc mảng hệ thống.
     *
     * @return Danh sách Độc giả không thể sửa đổi cấu trúc từ bên ngoài.
     */
    public List<Reader> getAll() {
        return Collections.unmodifiableList(readers);
    }

    /**
     * Tìm kiếm thông tin một độc giả cụ thể dựa trên Mã bạn đọc (Không phân biệt chữ hoa/thường).
     *
     * @param readerId Mã độc giả cần tìm kiếm (Ví dụ: BD001)
     * @return Đối tượng Reader con tương ứng nếu tìm thấy, ngược lại trả về null
     */
    public Reader findById(String readerId) {
        if (readerId == null) return null;
        String trimmedId = readerId.trim();
        for (Reader r : readers) {
            if (r.getUserId().equalsIgnoreCase(trimmedId)) {
                return r;
            }
        }
        return null;
    }

    /**
     * 🌟 THÊM MỚI: Đăng ký thêm một độc giả mới vào bộ nhớ RAM và tự động đồng bộ xuống file text.
     * Phương thức này giải quyết lỗi báo đỏ hàm .add() trong ReaderService.
     *
     * @param reader Đối tượng độc giả mới cần thêm vào hệ thống
     */
    public void add(Reader reader) {
        if (reader != null) {
            readers.add(reader);
            saveToFile(); // Tự động ghi cập nhật lại file text data/readers.txt ngay lập tức
        }
    }

    /**
     * 🌟 THÊM MỚI: Cập nhật thông tin chỉnh sửa của độc giả trên RAM và tự động lưu file text cứng.
     * Phương thức này giải quyết lỗi báo đỏ hàm .update() trong ReaderService.
     *
     * @param updatedReader Đối tượng độc giả mang thông tin mới cần cập nhật
     */
    public void update(Reader updatedReader) {
        if (updatedReader == null) return;
        for (int i = 0; i < readers.size(); i++) {
            if (readers.get(i).getUserId().equalsIgnoreCase(updatedReader.getUserId())) {
                readers.set(i, updatedReader);
                saveToFile(); // Tự động ghi đè dữ liệu mới xuống file text cứng
                return;
            }
        }
    }

    /**
     * Đọc dữ liệu từ file text 'data/readers.txt' và nạp vào danh sách quản lý trên RAM.
     */
    private void loadFromFile() {
        readers.clear();
        File file = new File(FILE_PATH);

        // Nếu file dữ liệu chưa tồn tại, tự động sinh bộ dữ liệu mẫu chuẩn cho lần đầu khởi chạy
        if (!file.exists()) {
            initMockData();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Bỏ qua dòng trống hoặc dòng ghi chú có tiền tố bắt đầu bằng dấu #
                if (line.isEmpty() || line.startsWith("#")) continue;

                Reader r = parseLine(line);
                if (r != null) {
                    readers.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("[ReaderRepository] Lỗi nghiêm trọng xảy ra khi đọc file bạn đọc: " + e.getMessage());
        }
    }

    /**
     * Phân tích một dòng text từ file và khởi tạo chính xác đối tượng lớp con của Reader thông qua tính đa hình.
     */
    private Reader parseLine(String line) {
        try {
            String[] p = line.split("\\|");
            if (p.length < 4) return null;

            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            String type = p[3].trim().toUpperCase(); // CẢI TIẾN: Chuyển sang viết hoa để chống lỗi lệch ký tự

            switch (type) {
                case "STUDENT":
                    return new StudentReader(id, name, phone);
                case "PRIORITY_STUDENT":
                    return new PriorityStudentReader(id, name, phone);
                case "LECTURER":
                    return new LecturerReader(id, name, phone);
                default:
                    System.err.println("[ReaderRepository] Không nhận diện được phân loại bạn đọc: " + type);
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ghi đè toàn bộ danh sách độc giả từ bộ nhớ RAM xuống file văn bản 'data/readers.txt'.
     */
    private void saveToFile() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Ghi dòng tiêu đề cấu trúc hướng dẫn cho file văn bản độc giả
            bw.write("# Format: readerId|fullName|phoneNumber|readerType");
            bw.newLine();
            bw.write("# readerType: STUDENT | PRIORITY_STUDENT | LECTURER");
            bw.newLine();

            for (Reader r : readers) {
                String type = "STUDENT";
                if (r instanceof LecturerReader) {
                    type = "LECTURER";
                } else if (r instanceof PriorityStudentReader) {
                    type = "PRIORITY_STUDENT";
                }

                bw.write(String.format("%s|%s|%s|%s", r.getUserId(), r.getFullName(), r.getPhoneNumber(), type));
                bw.newLine(); // CẢI TIẾN: Thay đổi cộng chuỗi bằng hàm ngắt dòng hệ thống an toàn đa nền tảng
            }
        } catch (IOException e) {
            System.err.println("[ReaderRepository] Lỗi nghiêm trọng xảy ra khi ghi file bạn đọc: " + e.getMessage());
        }
    }

    /**
     * Thiết lập bộ dữ liệu mẫu độc giả chuẩn mã BDxxx đồng bộ với cơ sở dữ liệu thực tế.
     */
    private void initMockData() {
        readers.add(new StudentReader("BD001", "Nguyen Van An", "0901234567"));
        readers.add(new PriorityStudentReader("BD002", "Tran Thi Bich", "0912345678"));
        readers.add(new LecturerReader("BD003", "Le Van Cuong", "0923456789"));
        readers.add(new StudentReader("BD004", "Pham Thi Dung", "0934567890"));
        readers.add(new PriorityStudentReader("BD005", "Hoang Van Em", "0945678901"));
        saveToFile(); // Đồng bộ lưu file text cứng
    }
}