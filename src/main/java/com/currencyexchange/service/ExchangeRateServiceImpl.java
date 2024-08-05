package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.dao.CurrencyDAOImpl;
import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.dao.ExchangeRateDAOImpl;
import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateServiceImpl implements ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl();
    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();

    @Override
    public List<ExchangeRateDTO> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.getAllExchangeRates();
        return exchangeRates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExchangeRateDTO getExchangeRateByCurrencyCode(String currencyCodePair) {
        ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRateByCurrencyCode(currencyCodePair);
        return convertToDTO(exchangeRate);
    }

    @Override
    public void addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = convertToEntity(exchangeRateDTO);
        exchangeRateDAO.addExchangeRate(exchangeRate);
    }

    @Override
    public void updateExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = convertToEntity(exchangeRateDTO);
        exchangeRateDAO.updateExchangeRate(exchangeRate);
    }

    @Override
    public void deleteExchangeRate(int id) {
        exchangeRateDAO.deleteExchangeRate(id);
    }

    private ExchangeRateDTO convertToDTO(ExchangeRate exchangeRate) {
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setId(exchangeRate.getId());
        dto.setBaseCurrencyId(exchangeRate.getBaseCurrencyId());
        dto.setTargetCurrencyId(exchangeRate.getTargetCurrencyId());
        dto.setRate(exchangeRate.getRate());
        return dto;
    }

    private ExchangeRate convertToEntity(ExchangeRateDTO dto) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(dto.getId());
        exchangeRate.setBaseCurrencyId(dto.getBaseCurrencyId());
        exchangeRate.setTargetCurrencyId(dto.getTargetCurrencyId());
        exchangeRate.setRate(dto.getRate());
        return exchangeRate;
    }

    private CurrencyDTO convertCurrencyToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setName(currency.getFullName());
        dto.setCode(currency.getCode());
        dto.setSign(currency.getSign());
        return dto;
    }
}
