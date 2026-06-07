package com.library.service.policy;

public class LecturerFinePolicy implements FinePolicy {

    private static final double FINE_PER_DAY = 2000;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * FINE_PER_DAY;
    }
}