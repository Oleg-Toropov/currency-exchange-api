package com.currencyexchange.exception;

public class InvalidFieldsException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Missing required field(s)";
    public InvalidFieldsException() {
        super(DEFAULT_MESSAGE);
    }
    public InvalidFieldsException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
