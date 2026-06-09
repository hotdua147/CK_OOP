package com.library.service.policy;

public class NormalStudentFinePolicy implements FinePolicy {
    private static final double FINE_PER_DAY = 5000.0;

    @Override
    public double calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0.0;
        return (double) overdueDays * FINE_PER_DAY;
    }
}