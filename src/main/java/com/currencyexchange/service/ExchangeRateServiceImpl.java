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
        List<ExchangeRateDTO> exchangeRateDTOs = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRates) {
            exchangeRateDTOs.add(convertExchangeRateToDTO(exchangeRate));
        }

        return exchangeRateDTOs;
    }

    @Override
    public ExchangeRateDTO getExchangeRateByCurrencyCodePair(String baseCode, String targetCode) {
        Optional<Currency> baseCurrency = currencyDAO.getCurrencyByCode(baseCode);
        Optional<Currency> targetCurrency = currencyDAO.getCurrencyByCode(targetCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        Optional<ExchangeRate> exchangeRate =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(baseCurrency.get().getId(), targetCurrency.get().getId());

        if (exchangeRate.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        return convertExchangeRateToDTO(exchangeRate.get());
    }

    @Override
    public ExchangeRateDTO addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        List<Integer> currencyId = checkExistsCurrencies(baseCurrencyCode, targetCurrencyCode);

        ExchangeRate newExchangeRate =
                new ExchangeRate(null, currencyId.get(0), currencyId.get(1), new BigDecimal(rate));

        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.getExchangeRateByCurrencyPairId(
                newExchangeRate.getBaseCurrencyId(), newExchangeRate.getTargetCurrencyId());

        if (exchangeRate.isPresent()) {
            throw new ExchangeRateExistsException();
        }

        ExchangeRate addedExchangeRate = exchangeRateDAO.addExchangeRate(newExchangeRate);

        return convertExchangeRateToDTO(addedExchangeRate);
    }

    @Override
    public ExchangeRateDTO updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        List<Integer> currencyId = checkExistsCurrencies(baseCurrencyCode, targetCurrencyCode);

        Optional<ExchangeRate> exchangeRate =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(currencyId.get(0), currencyId.get(1));

        if (exchangeRate.isEmpty()) {
            throw new ExchangeRateNotFoundException();
        }

        ExchangeRate exchangeRateForUpdate = exchangeRate.get();
        exchangeRateForUpdate.setRate(new BigDecimal(rate));
        exchangeRateDAO.updateExchangeRate(exchangeRateForUpdate);

        return convertExchangeRateToDTO(exchangeRateForUpdate);
    }

    private List<Integer> checkExistsCurrencies(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<Currency> baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new CurrencyNotFoundException();
        }

        return List.of(baseCurrency.get().getId(), targetCurrency.get().getId());
    }

    @Override
    public void deleteExchangeRate(int id) {
        exchangeRateDAO.deleteExchangeRate(id);
    }

    private ExchangeRateDTO convertExchangeRateToDTO(ExchangeRate exchangeRate) {
        Optional<Currency> baseCurrency = currencyDAO.getCurrencyById(exchangeRate.getBaseCurrencyId());
        Optional<Currency> targetCurrency = currencyDAO.getCurrencyById(exchangeRate.getTargetCurrencyId());
        CurrencyDTO baseCurrencyDTO = baseCurrency.map(currencyService::convertCurrencyToDTO).orElse(null);
        CurrencyDTO targetCurrencyDTO = targetCurrency.map(currencyService::convertCurrencyToDTO).orElse(null);

        return new ExchangeRateDTO(exchangeRate.getId(), baseCurrencyDTO, targetCurrencyDTO, exchangeRate.getRate());
    }

    private ExchangeRate convertExchangeRateDTOToEntity(ExchangeRateDTO dto) {
        return new ExchangeRate(dto.getId(), dto.getBaseCurrency().getId(),
                dto.getTargetCurrency().getId(), dto.getRate());
    }
}
