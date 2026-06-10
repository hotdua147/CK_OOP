package com.library.service.policy;
import com.library.policy.FinePolicy;
public class PriorityStudentFinePolicy implements FinePolicy {
    private static final double FINE_PER_DAY = 3000.0;

    @Override
    public double calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0.0;
        return (double) overdueDays * FINE_PER_DAY;
    }
}