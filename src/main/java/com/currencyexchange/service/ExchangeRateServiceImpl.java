package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.dao.ExchangeRateDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.CurrencyNotFoundException;
import com.currencyexchange.exception.ExchangeRateExistsException;
import com.currencyexchange.exception.ExchangeRateNotFoundException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;
    private final CurrencyServiceImpl currencyService;

    public ExchangeRateServiceImpl() {
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
        this.currencyDAO = new CurrencyDAOImpl();
        this.currencyService = new CurrencyServiceImpl();
    }

    @Override
    public List<ExchangeRateDTO> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.getAllExchangeRates();
        List<ExchangeRateDTO> exchangeRatesDTO = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRates) {
            exchangeRatesDTO.add(convertExchangeRateToDTO(exchangeRate));
        }

        return exchangeRatesDTO;
    }

    @Override
    public ExchangeRateDTO getExchangeRateByCurrencyPairCode(String baseCode, String targetCode) {
        Optional<ExchangeRate> exchangeRate =
                exchangeRateDAO.getExchangeRateByCurrencyPairCode(baseCode, targetCode);

        if (exchangeRate.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        return convertExchangeRateToDTO(exchangeRate.get());
    }

    @Override
    public ExchangeRateDTO addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        List<Currency> currencies = checkExistsCurrencies(baseCurrencyCode, targetCurrencyCode);

        ExchangeRate newExchangeRate =
                new ExchangeRate(null, currencies.get(0), currencies.get(1), new BigDecimal(rate));

        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.getExchangeRateByCurrencyPairId(
                newExchangeRate.getBaseCurrency().getId(), newExchangeRate.getTargetCurrency().getId());

        if (exchangeRate.isPresent()) {
            throw new ExchangeRateExistsException();
        }

        ExchangeRate addedExchangeRate = exchangeRateDAO.addExchangeRate(newExchangeRate);

        return convertExchangeRateToDTO(addedExchangeRate);
    }

    @Override
    public ExchangeRateDTO updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        List<Currency> currencies = checkExistsCurrencies(baseCurrencyCode, targetCurrencyCode);

        Optional<ExchangeRate> exchangeRate =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(currencies.get(0).getId(), currencies.get(1).getId());

        if (exchangeRate.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        ExchangeRate exchangeRateForUpdate = exchangeRate.get();
        exchangeRateForUpdate.setRate(new BigDecimal(rate));
        exchangeRateDAO.updateExchangeRate(exchangeRateForUpdate);

        return convertExchangeRateToDTO(exchangeRateForUpdate);
    }

    @Override
    public void deleteExchangeRate(int id) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.getExchangeRateById(id);

        if (exchangeRate.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        exchangeRateDAO.deleteExchangeRate(id);
    }

    private List<Currency> checkExistsCurrencies(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<Currency> baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new CurrencyNotFoundException();
        }

        return List.of(baseCurrency.get(), targetCurrency.get());
    }

    private ExchangeRateDTO convertExchangeRateToDTO(ExchangeRate exchangeRate) {
        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setId(exchangeRate.getId());
        exchangeRateDTO.setBaseCurrency(currencyService.convertCurrencyToDTO(exchangeRate.getBaseCurrency()));
        exchangeRateDTO.setTargetCurrency(currencyService.convertCurrencyToDTO(exchangeRate.getTargetCurrency()));
        exchangeRateDTO.setRate(exchangeRate.getRate());

        return exchangeRateDTO;
    }
}
