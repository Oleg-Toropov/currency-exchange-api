package com.currencyexchange.controller;

import com.currencyexchange.exception.InvalidCurrencyCodePairException;
import com.currencyexchange.exception.InvalidCurrencyCodeException;
import com.currencyexchange.exception.InvalidParametersException;

import java.util.List;

public class Validator {
    private static final int POSITION_BASE_CODE = 3;
    private static final int POSITION_TARGET_CODE = 6;
    private static final int LENGTH_RATE_CODE = 6;

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

    public static String[] validateCurrencyCodePair(String currencyCodePair) {
        validateCurrencyCode(currencyCodePair);
        String codePair = currencyCodePair.substring(1).toUpperCase();

        if (codePair.length() != LENGTH_RATE_CODE) {
            throw new InvalidCurrencyCodePairException();
        }

        String baseCode = codePair.substring(0, POSITION_BASE_CODE);
        String targetCode = codePair.substring(POSITION_BASE_CODE, POSITION_TARGET_CODE);

        if (baseCode.equals(targetCode)) {
            throw new InvalidCurrencyCodePairException();
        }

        return new String[]{baseCode, targetCode};
    }
}
