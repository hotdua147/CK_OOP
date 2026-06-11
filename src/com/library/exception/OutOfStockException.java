package com.library.exception;

public class OutOfStockException extends LibraryException {
    public OutOfStockException(String message) {
        super(message);
    }
}