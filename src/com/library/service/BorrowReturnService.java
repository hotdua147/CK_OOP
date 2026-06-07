package com.library.service;

public double calculateFine(Reader reader, int overdueDays) {

    if (overdueDays <= 0) {
        return 0;
    }

    return reader.getFinePolicy()
            .calculateFine(overdueDays);
}
