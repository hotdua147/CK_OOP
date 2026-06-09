package com.library.service;

import com.library.model.Reader;

public class BorrowReturnService {
    public double calculateFine(Reader reader, int overdueDays) {
        if (overdueDays <= 0) return 0.0;
        return reader.getFinePolicy().calculateFine(overdueDays);
    }
}