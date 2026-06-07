package com.library.service.policy;

public class PriorityStudentFinePolicy implements FinePolicy {

    private static final double FINE_PER_DAY = 3000;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * FINE_PER_DAY;
    }
}