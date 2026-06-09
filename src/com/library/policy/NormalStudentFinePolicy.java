package com.library.policy;

public class NormalStudentFinePolicy implements FinePolicy {
    @Override
    public double calculateFine(int lateDays) {
        return lateDays > 0 ? lateDays * 5000.0 : 0.0;
    }
}
