package com.currencyexchange.dao;

import com.currencyexchange.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    List<Currency> getAllCurrencies();

    Optional<Currency> getCurrencyByCode(String code);

    Currency addCurrency(Currency currency);

    void deleteCurrency(String code);
}