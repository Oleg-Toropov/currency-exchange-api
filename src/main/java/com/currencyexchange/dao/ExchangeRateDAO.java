package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;
import java.util.List;

public interface ExchangeRateDAO {
    List<ExchangeRate> getAllExchangeRates();
    ExchangeRate getExchangeRateByCurrencyCode(String currencyCodePair);
    void addExchangeRate(ExchangeRate exchangeRate);
    void updateExchangeRate(ExchangeRate exchangeRate);
    void deleteExchangeRate(int id);
}