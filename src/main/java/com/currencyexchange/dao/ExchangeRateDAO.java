package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    List<ExchangeRate> getAllExchangeRates();

    Optional<ExchangeRate> getExchangeRateByCurrencyPairId(int baseCurrencyId, int targetCurrencyId);

    ExchangeRate addExchangeRate(ExchangeRate exchangeRate);

    void updateExchangeRate(ExchangeRate exchangeRate);

    void deleteExchangeRate(int id);
}