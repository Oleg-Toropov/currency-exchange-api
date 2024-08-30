package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    List<ExchangeRate> getAllExchangeRates();

    Optional<ExchangeRate> getExchangeRateById(int id);
    Optional<ExchangeRate> getExchangeRateByCurrencyPairId(int baseCurrencyId, int targetCurrencyId);

    Optional<ExchangeRate> getExchangeRateByCurrencyPairCode(String baseCode, String targetCode);

    ExchangeRate addExchangeRate(ExchangeRate exchangeRate);

    void updateExchangeRate(ExchangeRate exchangeRate);
    void deleteExchangeRate(int id);

    List<ExchangeRate> getExchangeRatesByGeneralBaseCurrency(int baseCurrencyId, int targetCurrencyId);

    List<ExchangeRate> getExchangeRatesByGeneralTargetCurrency(int baseCurrencyId, int targetCurrencyId);

    List<ExchangeRate> getExchangeRatesByGeneralBaseTargetCurrency(int baseCurrencyId, int targetCurrencyId);
}