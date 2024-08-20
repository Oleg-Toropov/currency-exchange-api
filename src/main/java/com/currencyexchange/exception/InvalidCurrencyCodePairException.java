package com.currencyexchange.exception;

public class InvalidCurrencyCodePairException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Codes of currencies is missing or write wrong in the request URL";
    public InvalidCurrencyCodePairException() {
        super(DEFAULT_MESSAGE);
    }
}
