package com.currencyexchange.exception;

public class DatabaseUnavailableException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "The database is unavailable";

    public DatabaseUnavailableException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
