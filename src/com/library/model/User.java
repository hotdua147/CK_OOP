package com.library.model;

/**
 * Lớp cơ sở trừu tượng (Abstract class) đại diện cho một người dùng chung trong hệ thống.
 * Áp dụng nghiêm ngặt tính Đóng gói (Encapsulation): Tất cả thuộc tính để private, tương tác qua getter/setter.
 */
public abstract class User {

    private String userId;      // Mã định danh duy nhất (Ví dụ: BD001)
    private String fullName;    // Họ và tên toàn bộ
    private String phoneNumber; // Số điện thoại liên lạc

    /**
     * Constructor khởi tạo thông tin người dùng cơ sở.
     * Tích hợp cơ chế phòng vệ dữ liệu và chuẩn hóa chuỗi tự động.
     *
     * @param userId      Mã người dùng công khai
     * @param fullName    Họ và tên người dùng
     * @param phoneNumber Số điện thoại liên lạc
     */
    public User(String userId, String fullName, String phoneNumber) {
        setUserId(userId);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
    }

    // ─── Getters & Setters (Tuân thủ nghiêm ngặt tính Encapsulation) ───────────

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Mã người dùng (User ID) không được phép để trống hoặc null.");
        }
        // CẢI TIẾN: Tự động cắt khoảng trắng và viết hoa toàn bộ mã (ví dụ: bd001 -> BD001) để đồng bộ tìm kiếm
        this.userId = userId.trim().toUpperCase();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Họ và tên người dùng không được phép để trống hoặc null.");
        }
        this.fullName = fullName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Hỗ trợ lưu vết số điện thoại gọn gàng sau khi cắt khoảng trắng thừa
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : "";
    }

    // ─── Chuẩn hóa hiển thị thông tin ──────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("userId='%s', fullName='%s', phoneNumber='%s'", userId, fullName, phoneNumber);
    }
}