package com.library.policy;


/**
 * Chiến lược tính tiền phạt trễ hạn (Strategy Pattern)
 */
public interface FinePolicy {
    /**
     * Tính toán số tiền phạt dựa trên số ngày trễ hạn thực tế
     * @param overdueDays số ngày quá hạn
     * @return số tiền phạt (VND)
     */
    double calculateFine(int overdueDays);
}