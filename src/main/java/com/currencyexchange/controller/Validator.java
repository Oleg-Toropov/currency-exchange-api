package com.currencyexchange.controller;

import com.currencyexchange.exception.InvalidCurrencyCodeException;
import com.currencyexchange.exception.InvalidParametersException;

import java.util.List;

public class Validator {
    public static void validateParameters(List<String> parameters) {
        for (String parameter : parameters) {
            if (parameter == null || parameter.isEmpty()) {
                throw new InvalidParametersException();
            }
        }
    }

    public static void validateCurrencyCode(String codeCurrency) {
        if (codeCurrency == null || codeCurrency.length() <= 1) {
            throw new InvalidCurrencyCodeException();
        }
    }
}
