package com.currencyexchange.exception;

public class InvalidRateException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Invalid value in field rate";
    public InvalidRateException() {
        super(DEFAULT_MESSAGE);
    }
    public InvalidRateException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
