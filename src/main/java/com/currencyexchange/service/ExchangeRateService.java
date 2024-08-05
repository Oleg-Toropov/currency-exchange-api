package com.currencyexchange.service;

import com.currencyexchange.dto.ExchangeRateDTO;

import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDTO> getAllExchangeRates();
    ExchangeRateDTO getExchangeRateByCurrencyCode(String currencyCodePair);
    void addExchangeRate(ExchangeRateDTO exchangeRateDTO);
    void updateExchangeRate(ExchangeRateDTO exchangeRateDTO);
    void deleteExchangeRate(int id);
}