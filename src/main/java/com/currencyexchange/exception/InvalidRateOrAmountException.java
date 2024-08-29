package com.currencyexchange.exception;

public class InvalidRateOrAmountException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Invalid value in field rate";
    public InvalidRateOrAmountException() {
        super(DEFAULT_MESSAGE);
    }
    public InvalidRateOrAmountException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
