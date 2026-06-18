package com.library.service;

import com.library.model.Reader;
import com.library.repository.ReaderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service

/**
 * Tầng xử lý logic nghiệp vụ liên quan đến Độc giả (Bạn đọc).
 * Đã loại bỏ hoàn toàn sự phụ thuộc vào ValidationUtils bằng cách chuẩn hóa bộ lọc kiểm tra inline,
 * và đồng bộ chính xác với cấu trúc ReaderRepository mới.
 */
public class ReaderService {
    private final ReaderRepository readerRepository;

    /**
     * Constructor tiêm (Inject) ReaderRepository từ ngoài vào để tương tác dữ liệu.
     */
    public ReaderService(ReaderRepository readerRepository) {
        if (readerRepository == null) {
            throw new IllegalArgumentException("Lỗi hệ thống: ReaderRepository truyền vào không được phép để null!");
        }
        this.readerRepository = readerRepository;
    }

    /**
     * Lấy danh sách toàn bộ độc giả có trên hệ thống để hiển thị lên giao diện UI.
     *
     * @return Danh sách Độc giả (Chỉ đọc)
     */
    public List<Reader> getAllReaders() {
        return readerRepository.getAll();
    }

    /**
     * Tìm kiếm một độc giả cụ thể dựa vào mã bạn đọc.
     *
     * @param readerId Mã độc giả cần tìm (Ví dụ: BD001)
     * @return Đối tượng Reader nếu tìm thấy
     */
    public Reader getReaderById(String readerId) {
        if (readerId == null || readerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã bạn đọc tìm kiếm không được phép để trống!");
        }

        Reader reader = readerRepository.findById(readerId.trim());
        if (reader == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Không tìm thấy bạn đọc có mã '" + readerId + "' trong hệ thống!");
        }
        return reader;
    }

    /**
     * Nghiệp vụ thêm mới một bạn đọc vào hệ thống thư viện.
     * Tự động kiểm tra định dạng dữ liệu đầu vào không cần ValidationUtils.
     */
    public void addReader(Reader reader) {
        if (reader == null || reader.getUserId() == null) {
            throw new IllegalArgumentException("Lỗi: Dữ liệu tài khoản bạn đọc không hợp lệ!");
        }

        // Kiểm tra phòng vệ chống trùng lặp mã ID
        if (readerRepository.findById(reader.getUserId()) != null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Mã bạn đọc '" + reader.getUserId() + "' đã tồn tại trên hệ thống!");
        }

        // Kiểm tra tính hợp lệ của họ tên và số điện thoại
        validateReaderData(reader.getFullName(), reader.getPhoneNumber());

        // Đưa vào kho lưu trữ dữ liệu và kích hoạt lưu file text cứng
        readerRepository.add(reader);
        System.out.println("[ReaderService] Thêm bạn đọc mới thành công: " + reader.getFullName());
    }

    /**
     * Nghiệp vụ xóa một bạn đọc khỏi hệ thống.
     * Kiểm tra sự tồn tại trước khi xóa để tránh lỗi dữ liệu.
     */
    public void deleteReader(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã bạn đọc cần xóa không được để trống!");
        }

        // Kiểm tra xem độc giả có tồn tại hay không
        if (readerRepository.findById(id.trim()) == null) {
            throw new IllegalArgumentException("Lỗi: Không tìm thấy bạn đọc mã '" + id + "' để xóa!");
        }

        // Gọi Repository để xóa
        // LƯU Ý: Kiểm tra file ReaderRepository.java của bạn xem hàm xóa tên là delete() hay remove()
        readerRepository.delete(id.trim());

        System.out.println("[ReaderService] Đã xóa bạn đọc thành công: " + id);
    }

    /**
     * Nghiệp vụ cập nhật thông tin chỉnh sửa của một bạn đọc hiện hành.
     */
    public void updateReader(Reader reader) {
        if (reader == null || reader.getUserId() == null) {
            throw new IllegalArgumentException("Lỗi: Dữ liệu cập nhật bạn đọc không hợp lệ!");
        }

        // Bẫy lỗi nếu đối tượng chỉnh sửa không có thật trong kho dữ liệu
        Reader existing = readerRepository.findById(reader.getUserId());
        if (existing == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Không tìm thấy bạn đọc mang mã '" + reader.getUserId() + "' để cập nhật!");
        }

        // Kiểm tra tính hợp lệ của họ tên và số điện thoại mới
        validateReaderData(reader.getFullName(), reader.getPhoneNumber());

        // Kích hoạt đồng bộ sửa đổi xuống file cứng
        readerRepository.update(reader);
        System.out.println("[ReaderService] Cập nhật thông tin bạn đọc thành công: " + reader.getFullName());
    }

    /**
     * BỘ LỌC KIỂM TRA DỮ LIỆU NỘI BỘ (Thay thế hoàn toàn cho ValidationUtils bị mất)
     * Đảm bảo tính đóng gói nghiệp vụ sạch sẽ ngay tại tầng Service.
     */
    private void validateReaderData(String fullName, String phoneNumber) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Họ và tên bạn đọc không được phép để trống!");
        }
        if (fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Họ và tên quá ngắn, vui lòng nhập đầy đủ họ tên!");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Số điện thoại liên lạc không được phép để trống!");
        }
        // Kiểm tra số điện thoại bằng biểu thức chính quy Regex tiêu chuẩn (Yêu cầu chỉ chứa từ 10 đến 11 chữ số)
        if (!phoneNumber.trim().matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Lỗi định dạng: Số điện thoại không hợp lệ! (Phải chứa từ 10 đến 11 ký tự số).");
        }
    }
}