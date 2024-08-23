package com.currencyexchange.service;

import com.currencyexchange.dto.ExchangeRateDTO;

import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDTO> getAllExchangeRates();

    ExchangeRateDTO getExchangeRateByCurrencyCodePair(String baseCode, String targetCode);

    ExchangeRateDTO addExchangeRate(String baseCode, String targetCode, String rate);

    ExchangeRateDTO updateExchangeRate(String baseCode, String targetCode, String rate);

    void deleteExchangeRate(int id);
}