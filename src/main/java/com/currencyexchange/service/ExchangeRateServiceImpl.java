package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.dao.ExchangeRateDAOImpl;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;
    private final CurrencyServiceImpl currencyService;

    public ExchangeRateServiceImpl() {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
        this.currencyDAO = new CurrencyDAOImpl();
        this.currencyService = new CurrencyServiceImpl();
    }

    public ExchangeRateServiceImpl(ExchangeRateDAO exchangeRateDAO, CurrencyDAO currencyDAO, CurrencyServiceImpl currencyService) {
        this.exchangeRateDAO = exchangeRateDAO;
        this.currencyDAO = currencyDAO;
        this.currencyService = currencyService;
    }

    @Override
    public List<ExchangeRateDTO> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.getAllExchangeRates();
        List<ExchangeRateDTO> exchangeRateDTOs = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRates) {
            exchangeRateDTOs.add(convertExchangeRateToDTO(exchangeRate));
        }

        return exchangeRateDTOs;
    }


    @Override
    public ExchangeRateDTO getExchangeRateByCurrencyCode(String currencyCodePair) {
        ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRateByCurrencyCode(currencyCodePair);
        return convertExchangeRateToDTO(exchangeRate);
    }

    @Override
    public void addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = convertExchangeRateDTOToEntity(exchangeRateDTO);
        exchangeRateDAO.addExchangeRate(exchangeRate);
    }

    @Override
    public void updateExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = convertExchangeRateDTOToEntity(exchangeRateDTO);
        exchangeRateDAO.updateExchangeRate(exchangeRate);
    }

    @Override
    public void deleteExchangeRate(int id) {
        exchangeRateDAO.deleteExchangeRate(id);
    }

    private ExchangeRateDTO convertExchangeRateToDTO(ExchangeRate exchangeRate) {
        Currency baseCurrency = currencyDAO.getCurrencyById(exchangeRate.getBaseCurrencyId());
        Currency targetCurrency = currencyDAO.getCurrencyById(exchangeRate.getTargetCurrencyId());

        return new ExchangeRateDTO(exchangeRate.getId(), currencyService.convertCurrencyToDTO(baseCurrency),
                currencyService.convertCurrencyToDTO(targetCurrency), exchangeRate.getRate());
    }

    private ExchangeRate convertExchangeRateDTOToEntity(ExchangeRateDTO dto) {
        return new ExchangeRate(dto.getId(), dto.getBaseCurrency().getId(),
                dto.getTargetCurrency().getId(), dto.getRate());
    }
}
