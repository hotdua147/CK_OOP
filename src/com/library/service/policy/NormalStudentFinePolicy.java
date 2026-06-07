package com.library.service.policy;

public class NormalStudentFinePolicy implements FinePolicy {

    private static final double FINE_PER_DAY = 5000;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * FINE_PER_DAY;
    }
}