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

    public CurrencyServiceImpl(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
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
        return convertToDTO(currency);
    }

    @Override
    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        Currency existingCurrency = currencyDAO.getCurrencyByCode(currencyDTO.getCode());
        if (existingCurrency != null) {
            return null;
        }

        Currency newCurrency = convertToEntity(currencyDTO);
        currencyDAO.addCurrency(newCurrency);
        currencyDTO.setId(newCurrency.getId());
        return currencyDTO;
    }

    @Override
    public void updateCurrency(CurrencyDTO currencyDTO) {
        Currency currency = convertToEntity(currencyDTO);
        currencyDAO.updateCurrency(currency);
    }

    @Override
    public void deleteCurrency(int id) {
        currencyDAO.deleteCurrency(id);
    }

    private CurrencyDTO convertToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setFullName(currency.getFullName());
        dto.setCode(currency.getCode());
        dto.setSign(currency.getSign());
        return dto;
    }

    private Currency convertToEntity(CurrencyDTO dto) {
        Currency currency = new Currency();
        currency.setId(dto.getId());
        currency.setFullName(dto.getFullName());
        currency.setCode(dto.getCode());
        currency.setSign(dto.getSign());
        return currency;
    }
}
