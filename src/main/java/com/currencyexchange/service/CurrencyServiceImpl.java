package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.exception.CurrencyNotFoundException;
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

        if (currency.isEmpty()) {
            throw new CurrencyNotFoundException();
        }

        return convertCurrencyToDTO(currency.get());
    }

    @Override
    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        Currency newCurrency = convertCurrencyDTOToEntity(currencyDTO);
        Currency addedCurrency = currencyDAO.addCurrency(newCurrency);

        return convertCurrencyToDTO(addedCurrency);
    }

    @Override
    public void deleteCurrency(String code) {
        Optional<Currency> currency = currencyDAO.getCurrencyByCode(code);

        if (currency.isEmpty()) {
            throw new CurrencyNotFoundException();
        }

        currencyDAO.deleteCurrency(code);
    }

    public CurrencyDTO convertCurrencyToDTO(Currency currency) {
        return new CurrencyDTO(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
    }

    public Currency convertCurrencyDTOToEntity(CurrencyDTO dto) {
        return new Currency(dto.getId(), dto.getCode(), dto.getName(), dto.getSign());
    }
}
