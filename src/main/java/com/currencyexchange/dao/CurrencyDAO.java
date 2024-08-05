package com.currencyexchange.dao;

import com.currencyexchange.model.Currency;

import java.util.List;

public interface CurrencyDAO {
    List<Currency> getAllCurrencies();
    Currency getCurrencyByCode(String code);
    void addCurrency(Currency currency);
    void updateCurrency(Currency currency);
    void deleteCurrency(int id);
}