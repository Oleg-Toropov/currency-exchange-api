package com.currencyexchange.exception;

public class ExchangeRateExistsException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Currency pair with this code already exists";
    public ExchangeRateExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
