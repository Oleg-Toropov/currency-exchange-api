package com.currencyexchange.exception;

public class InvalidCurrencyCodeException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Currency code is missing in the request URL";
    public InvalidCurrencyCodeException() {
        super(DEFAULT_MESSAGE);
    }
}
