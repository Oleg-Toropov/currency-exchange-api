package com.currencyexchange.service;

import com.currencyexchange.dto.ExchangeRateDTO;

import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDTO> getAllExchangeRates();

    ExchangeRateDTO getExchangeRateByCurrencyCodePair(String baseCode, String targetCode);

    ExchangeRateDTO addExchangeRate(ExchangeRateDTO exchangeRateDTO);

    void updateExchangeRate(ExchangeRateDTO exchangeRateDTO);

    void deleteExchangeRate(int id);
}