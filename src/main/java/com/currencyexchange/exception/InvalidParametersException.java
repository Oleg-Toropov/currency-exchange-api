package com.currencyexchange.exception;

public class InvalidParametersException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Missing required field(s)";
    public InvalidParametersException() {
        super(DEFAULT_MESSAGE);
    }
    public InvalidParametersException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
