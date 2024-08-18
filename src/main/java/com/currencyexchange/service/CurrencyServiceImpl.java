package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.model.Currency;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyDAO currencyDAO;

    public CurrencyServiceImpl() {
        this.currencyDAO = new CurrencyDAOImpl();
    }

    @Override
    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyDAO.getAllCurrencies();
        return currencies.stream()
                .map(this::convertCurrencyToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyDTO getCurrencyByCode(String code) {
        Optional<Currency> currency = currencyDAO.getCurrencyByCode(code);
        return currency.map(this::convertCurrencyToDTO).orElse(null);
    }

    @Override
    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        Currency newCurrency = convertCurrencyDTOToEntity(currencyDTO);
        Currency addedCurrency = currencyDAO.addCurrency(newCurrency);
        return convertCurrencyToDTO(addedCurrency);
    }

    @Override
    public boolean deleteCurrency(String code) {
        Optional <Currency> currency = currencyDAO.getCurrencyByCode(code);
        if (currency.isPresent()) {
            currencyDAO.deleteCurrency(code);
            return true;
        } else {
            return false;
        }
    }

    public CurrencyDTO convertCurrencyToDTO(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

    public Currency convertCurrencyDTOToEntity(CurrencyDTO dto) {
        return new Currency(dto.getId(), dto.getCode(), dto.getName(), dto.getSign());
    }
}
