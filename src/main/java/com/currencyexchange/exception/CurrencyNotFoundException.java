package com.currencyexchange.exception;

public class CurrencyNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Currency not found";
    public CurrencyNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
