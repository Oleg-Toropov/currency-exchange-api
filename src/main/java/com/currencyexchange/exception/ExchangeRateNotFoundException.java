package com.currencyexchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Exchange rate for the pair not found";
    public ExchangeRateNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
