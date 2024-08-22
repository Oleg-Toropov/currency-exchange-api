package com.currencyexchange.exception;

public class CurrencyExistsException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Currency with this code already exists";
    public CurrencyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
