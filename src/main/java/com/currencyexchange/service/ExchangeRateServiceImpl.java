package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.dao.ExchangeRateDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

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
            return null;
        }

        Optional<ExchangeRate> exchangeRate =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(baseCurrency.get().getId(), targetCurrency.get().getId());

        return exchangeRate.map(this::convertExchangeRateToDTO).orElse(null);
    }

    @Override
    public ExchangeRateDTO addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate newExchangeRate = convertExchangeRateDTOToEntity(exchangeRateDTO);
        ExchangeRate addedExchangeRate = exchangeRateDAO.addExchangeRate(newExchangeRate);

        return convertExchangeRateToDTO(addedExchangeRate);
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
