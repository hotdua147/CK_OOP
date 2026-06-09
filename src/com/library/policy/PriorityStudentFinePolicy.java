package com.library.policy;

public class PriorityStudentFinePolicy implements FinePolicy {
    @Override
    public double calculateFine(int lateDays) {
        return lateDays > 0 ? lateDays * 3000.0 : 0.0;
    }
}
