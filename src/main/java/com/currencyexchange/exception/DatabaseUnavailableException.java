package com.currencyexchange.exception;

public class DatabaseUnavailableException extends RuntimeException{
    public DatabaseUnavailableException(Throwable cause) {
        super(cause);
    }
}
