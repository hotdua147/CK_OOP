package com.library.policy;

public class LecturerFinePolicy implements FinePolicy {
    @Override
    public double calculateFine(int lateDays) {
        return lateDays > 0 ? lateDays * 2000.0 : 0.0;
    }
}