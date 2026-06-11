package com.library.exception;

/**
 * Ngoại lệ tùy biến phục vụ riêng cho các vi phạm quy định nghiệp vụ của Thư viện.
 * Giúp tách biệt lỗi hệ thống (Crash) với lỗi vi phạm quy tắc mượn/trả sách.
 */
public class LibraryException extends Exception {

    /**
     * Khởi tạo một thông báo lỗi nghiệp vụ cụ thể.
     *
     * @param message Nội dung thông báo lỗi hiển thị cho thủ thư
     */
    public LibraryException(String message) {
        super(message);
    }
}