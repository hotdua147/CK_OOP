package com.library.service;

import com.library.model.*;
import com.library.repository.ReaderRepository;
import com.library.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý toàn bộ logic nghiệp vụ liên quan đến Bạn đọc.
 *
 * Chức năng:
 *  - Thêm / sửa / xóa bạn đọc
 *  - Tìm kiếm theo mã, theo tên
 *  - Hiển thị danh sách
 *  - Kiểm tra giới hạn mượn sách (dùng bởi BorrowReturnService của Trưởng nhóm)
 */
public class ReaderService {

    private final ReaderRepository readerRepository;
    private List<Reader> readerList;

    // ─── Constructor ───────────────────────────────────────────────────────────

    /**
     * Khởi tạo service và nạp dữ liệu từ file.
     *
     * @throws IOException nếu có lỗi đọc file
     */
    public ReaderService() throws IOException {
        this.readerRepository = new ReaderRepository();
        this.readerList       = readerRepository.readAll();
    }

    // ─── Thêm bạn đọc ─────────────────────────────────────────────────────────

    /**
     * Thêm một bạn đọc mới vào hệ thống.
     *
     * @param reader đối tượng Reader cần thêm
     * @throws IOException          nếu ghi file thất bại
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ
     */
    public void addReader(Reader reader) throws IOException {
        // Kiểm tra dữ liệu đầu vào
        if (ValidationUtils.isEmpty(reader.getUserId()))
            throw new IllegalArgumentException("Mã bạn đọc không được để trống.");
        if (ValidationUtils.isEmpty(reader.getFullName()))
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (!ValidationUtils.isValidPhone(reader.getPhoneNumber()))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ: " + reader.getPhoneNumber());
        if (findById(reader.getUserId()) != null)
            throw new IllegalArgumentException("Mã bạn đọc đã tồn tại: " + reader.getUserId());

        readerList.add(reader);
        readerRepository.writeAll(readerList);
        System.out.println("[OK] Đã thêm bạn đọc: " + reader.getFullName() + " (" + reader.getUserId() + ")");
    }

    // ─── Cập nhật thông tin ───────────────────────────────────────────────────

    /**
     * Cập nhật họ tên hoặc số điện thoại của bạn đọc.
     *
     * @param readerId mã bạn đọc cần cập nhật
     * @param newName  họ tên mới (truyền null để giữ nguyên)
     * @param newPhone số điện thoại mới (truyền null để giữ nguyên)
     */
    public void updateReader(String readerId, String newName, String newPhone) throws IOException {
        Reader r = findById(readerId);
        if (r == null)
            throw new IllegalArgumentException("Không tìm thấy bạn đọc với mã: " + readerId);

        if (!ValidationUtils.isEmpty(newName)) {
            r.setFullName(newName);
        }
        if (!ValidationUtils.isEmpty(newPhone)) {
            if (!ValidationUtils.isValidPhone(newPhone))
                throw new IllegalArgumentException("Số điện thoại không hợp lệ: " + newPhone);
            r.setPhoneNumber(newPhone);
        }

        readerRepository.writeAll(readerList);
        System.out.println("[OK] Đã cập nhật thông tin bạn đọc: " + readerId);
    }

    // ─── Xóa bạn đọc ─────────────────────────────────────────────────────────

    /**
     * Xóa bạn đọc khỏi hệ thống.
     *
     * @param readerId mã bạn đọc cần xóa
     */
    public void deleteReader(String readerId) throws IOException {
        Reader r = findById(readerId);
        if (r == null)
            throw new IllegalArgumentException("Không tìm thấy bạn đọc với mã: " + readerId);

        readerList.remove(r);
        readerRepository.writeAll(readerList);
        System.out.println("[OK] Đã xóa bạn đọc: " + readerId);
    }

    // ─── Tìm kiếm ─────────────────────────────────────────────────────────────

    /**
     * Tìm bạn đọc theo mã (chính xác, không phân biệt hoa thường).
     *
     * @return Reader nếu tìm thấy, null nếu không
     */
    public Reader findById(String readerId) {
        if (ValidationUtils.isEmpty(readerId)) return null;
        for (Reader r : readerList) {
            if (r.getUserId().equalsIgnoreCase(readerId.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Tìm bạn đọc theo họ tên (tìm kiếm gần đúng, không phân biệt hoa thường).
     *
     * @return danh sách Reader khớp với từ khóa
     */
    public List<Reader> findByName(String keyword) {
        List<Reader> result = new ArrayList<>();
        if (ValidationUtils.isEmpty(keyword)) return result;

        String lowerKeyword = keyword.trim().toLowerCase();
        for (Reader r : readerList) {
            if (r.getFullName().toLowerCase().contains(lowerKeyword)) {
                result.add(r);
            }
        }
        return result;
    }

    // ─── Kiểm tra giới hạn mượn ───────────────────────────────────────────────

    /**
     * Kiểm tra bạn đọc có thể mượn thêm sách không.
     * Gọi bởi BorrowReturnService (Trưởng nhóm).
     *
     * @param readerId       mã bạn đọc
     * @param currentBorrows số sách đang mượn hiện tại
     * @param requestAmount  số sách muốn mượn thêm
     * @return true nếu được phép, false nếu vượt giới hạn
     */
    public boolean canBorrow(String readerId, int currentBorrows, int requestAmount) {
        Reader r = findById(readerId);
        if (r == null) return false;
        return (currentBorrows + requestAmount) <= r.getMaxBorrowLimit();
    }

    // ─── Lấy danh sách ────────────────────────────────────────────────────────

    /**
     * Trả về toàn bộ danh sách bạn đọc.
     */
    public List<Reader> getAllReaders() {
        return new ArrayList<>(readerList);
    }

    // ─── Hiển thị ─────────────────────────────────────────────────────────────

    /**
     * In danh sách tất cả bạn đọc ra console (dạng bảng).
     */
    public void displayAll() {
        if (readerList.isEmpty()) {
            System.out.println("Danh sách bạn đọc trống.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       DANH SÁCH BẠN ĐỌC                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
        System.out.printf("%-10s %-25s %-13s %-22s %-12s%n",
                "Mã BD", "Họ tên", "SĐT", "Loại bạn đọc", "Mượn tối đa");
        System.out.println("─".repeat(86));
        for (Reader r : readerList) {
            System.out.printf("%-10s %-25s %-13s %-22s %-12d%n",
                    r.getUserId(),
                    r.getFullName(),
                    r.getPhoneNumber(),
                    r.getReaderType(),
                    r.getMaxBorrowLimit());
        }
        System.out.println("─".repeat(86));
        System.out.println("Tổng số bạn đọc: " + readerList.size());
    }

    /**
     * In kết quả tìm kiếm ra console.
     */
    public void displayReaders(List<Reader> list, String searchInfo) {
        if (list.isEmpty()) {
            System.out.println("Không tìm thấy bạn đọc với: " + searchInfo);
            return;
        }
        System.out.println("\n=== Kết quả tìm kiếm: " + searchInfo + " ===");
        System.out.printf("%-10s %-25s %-13s %-22s%n", "Mã BD", "Họ tên", "SĐT", "Loại bạn đọc");
        System.out.println("─".repeat(72));
        for (Reader r : list) {
            System.out.printf("%-10s %-25s %-13s %-22s%n",
                    r.getUserId(), r.getFullName(), r.getPhoneNumber(), r.getReaderType());
        }
    }
}
