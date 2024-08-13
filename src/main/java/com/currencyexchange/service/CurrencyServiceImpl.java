package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.model.Currency;

import java.util.List;
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyDTO getCurrencyByCode(String code) {
        Currency currency = currencyDAO.getCurrencyByCode(code);
        return (currency == null)? null : convertToDTO(currency);
    }

    @Override
    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        Currency newCurrency = convertToEntity(currencyDTO);
        Currency addedCurrency = currencyDAO.addCurrency(newCurrency);
        return convertToDTO(addedCurrency);
    }

    @Override
    public boolean deleteCurrency(int id) {
        currencyDAO.deleteCurrency(id);
        Currency currency = currencyDAO.getCurrencyById(id);
        return currency == null;
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setName(currency.getFullName());
        dto.setCode(currency.getCode());
        dto.setSign(currency.getSign());
        return dto;
    }

    private Currency convertToEntity(CurrencyDTO dto) {
        Currency currency = new Currency();
        currency.setId(dto.getId());
        currency.setFullName(dto.getName());
        currency.setCode(dto.getCode());
        currency.setSign(dto.getSign());
        return currency;
    }
}
