package com.currencyexchange.service;

import com.currencyexchange.dto.CurrencyDTO;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDTO> getAllCurrencies();
    CurrencyDTO getCurrencyByCode(String code);
    void addCurrency(CurrencyDTO currencyDTO);
    void updateCurrency(CurrencyDTO currencyDTO);
    void deleteCurrency(int id);
}