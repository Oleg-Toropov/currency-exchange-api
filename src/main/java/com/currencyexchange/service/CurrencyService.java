package com.currencyexchange.service;

import com.currencyexchange.dto.CurrencyDTO;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDTO> getAllCurrencies();
    CurrencyDTO getCurrencyByCode(String code);
    CurrencyDTO addCurrency(CurrencyDTO currencyDTO);
    void deleteCurrency(String code);
}