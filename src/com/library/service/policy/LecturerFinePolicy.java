package com.library.service.policy; // Giữ nguyên package của file này nếu bạn muốn đặt các class Policy ở đây

import com.library.policy.FinePolicy; // ÉP BUỘC: Import bản chuẩn từ package model/policy sang

public class LecturerFinePolicy implements FinePolicy {
    private static final double FINE_PER_DAY = 2000.0;

    @Override
    public double calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0.0;
        return (double) overdueDays * FINE_PER_DAY;
    }
}