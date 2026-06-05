package com.library.repository;

import com.library.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý đọc / ghi danh sách bạn đọc từ file (File IO).
 *
 * Định dạng mỗi dòng trong readers.txt:
 *   readerId|fullName|phoneNumber|readerType
 *
 * Ví dụ:
 *   BD001|Nguyen Van An|0901234567|STUDENT
 *   BD002|Tran Thi Bich|0912345678|PRIORITY_STUDENT
 *   BD003|Le Van Cuong|0923456789|LECTURER
 */
public class ReaderRepository {

    private static final String FILE_PATH = "data/readers.txt";

    // Hằng số loại bạn đọc lưu trong file
    public static final String TYPE_STUDENT          = "STUDENT";
    public static final String TYPE_PRIORITY_STUDENT = "PRIORITY_STUDENT";
    public static final String TYPE_LECTURER         = "LECTURER";

    // ─── Đọc file ──────────────────────────────────────────────────────────────

    /**
     * Đọc tất cả bạn đọc từ file readers.txt.
     * Nếu file chưa tồn tại, trả về danh sách rỗng.
     *
     * @return danh sách Reader
     * @throws IOException nếu có lỗi đọc file
     */
    public List<Reader> readAll() throws IOException {
        List<Reader> readers = new ArrayList<>();
        File file = new File(FILE_PATH);

        // Nếu file chưa tồn tại, trả về danh sách rỗng
        if (!file.exists()) {
            return readers;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Bỏ qua dòng trống và dòng comment (#)
                if (line.isEmpty() || line.startsWith("#")) continue;

                Reader reader = parseLine(line);
                if (reader != null) {
                    readers.add(reader);
                }
            }
        }
        return readers;
    }

    // ─── Ghi file ──────────────────────────────────────────────────────────────

    /**
     * Ghi toàn bộ danh sách bạn đọc ra file readers.txt.
     *
     * @param readers danh sách cần ghi
     * @throws IOException nếu có lỗi ghi file
     */
    public void writeAll(List<Reader> readers) throws IOException {
        // Tạo thư mục data/ nếu chưa có
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write("# Format: readerId|fullName|phoneNumber|readerType");
            bw.newLine();
            bw.write("# readerType: STUDENT | PRIORITY_STUDENT | LECTURER");
            bw.newLine();
            for (Reader r : readers) {
                bw.write(toLine(r));
                bw.newLine();
            }
        }
    }

    // ─── Helper: parse dòng → Reader ───────────────────────────────────────────

    /**
     * Phân tích một dòng text từ file thành đối tượng Reader.
     *
     * @param line một dòng trong file
     * @return Reader tương ứng, hoặc null nếu dòng không hợp lệ
     */
    private Reader parseLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) return null;

        String id    = parts[0].trim();
        String name  = parts[1].trim();
        String phone = parts[2].trim();
        String type  = parts[3].trim();

        switch (type) {
            case TYPE_STUDENT:
                return new StudentReader(id, name, phone);
            case TYPE_PRIORITY_STUDENT:
                return new PriorityStudentReader(id, name, phone);
            case TYPE_LECTURER:
                return new LecturerReader(id, name, phone);
            default:
                System.err.println("[ReaderRepository] Loại bạn đọc không hợp lệ: " + type);
                return null;
        }
    }

    // ─── Helper: Reader → dòng text ────────────────────────────────────────────

    /**
     * Chuyển đối tượng Reader thành chuỗi để lưu vào file.
     */
    private String toLine(Reader r) {
        String type;
        if (r instanceof LecturerReader)         type = TYPE_LECTURER;
        else if (r instanceof PriorityStudentReader) type = TYPE_PRIORITY_STUDENT;
        else                                     type = TYPE_STUDENT;

        return r.getUserId() + "|" + r.getFullName() + "|" + r.getPhoneNumber() + "|" + type;
    }
}
