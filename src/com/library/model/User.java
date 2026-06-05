package com.library.model;

/**
 * Lớp cơ sở (abstract) đại diện cho người dùng chung trong hệ thống.
 * Áp dụng Encapsulation: tất cả thuộc tính để private, truy cập qua getter/setter.
 */
public abstract class User {

    private String userId;
    private String fullName;
    private String phoneNumber;

    // ─── Constructor ───────────────────────────────────────────────────────────

    public User(String userId, String fullName, String phoneNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // ─── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "userId='" + userId + "', fullName='" + fullName + "', phoneNumber='" + phoneNumber + "'";
    }
}
