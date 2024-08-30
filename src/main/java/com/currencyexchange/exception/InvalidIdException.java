package com.currencyexchange.exception;

public class InvalidIdException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "ID is missing or write wrong in the request URL";
    public InvalidIdException() {
        super(DEFAULT_MESSAGE);
    }
}
