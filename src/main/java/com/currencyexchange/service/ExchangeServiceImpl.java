package com.currencyexchange.service;

import com.currencyexchange.dao.*;
import com.currencyexchange.dto.ExchangeDTO;
import com.currencyexchange.exception.CurrencyNotFoundException;
import com.currencyexchange.exception.ExchangeRateNotFoundException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService {
    private final CurrencyDAO currencyDAO;
    private final CurrencyServiceImpl currencyService;
    private final ExchangeRateDAO exchangeRateDAO;
    private static final int SCALE_RATE = 6;
    private static final int SCALE_CONVERT_AMOUNT = 2;

    public ExchangeServiceImpl() {
        this.currencyDAO = new CurrencyDAOImpl();
        this.currencyService = new CurrencyServiceImpl();
        this.exchangeRateDAO = new ExchangeRateDAOImpl();
    }

    @Override
    public ExchangeDTO makeExchange(String from, String to, String amount) {
        Optional<Currency> fromCurrency = currencyDAO.getCurrencyByCode(from);
        Optional<Currency> toCurrency = currencyDAO.getCurrencyByCode(to);

        if (fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            throw new CurrencyNotFoundException();
        }

        Currency baseCurrency = fromCurrency.get();
        Currency targetCurrency = toCurrency.get();
        BigDecimal amountForExchange = new BigDecimal(amount);

        BigDecimal rate = getRate(baseCurrency.getId(), targetCurrency.getId());

        if (rate != null) {
            BigDecimal convertedAmount = rate.multiply(amountForExchange).setScale(SCALE_CONVERT_AMOUNT, RoundingMode.HALF_UP);
            return convertExchangeToDTO(baseCurrency, targetCurrency, rate, amountForExchange, convertedAmount);
        } else {
            throw new ExchangeRateNotFoundException();
        }
    }

    private BigDecimal getRate(int baseCurrencyId, int targetCurrencyId) {
        Optional<ExchangeRate> exchangeRateBaseToTarget =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(baseCurrencyId, targetCurrencyId);
        if (exchangeRateBaseToTarget.isPresent()) {

            return exchangeRateBaseToTarget.get().getRate();
        }

        Optional<ExchangeRate> exchangeRateTargetToBase =
                exchangeRateDAO.getExchangeRateByCurrencyPairId(targetCurrencyId, baseCurrencyId);
        if (exchangeRateTargetToBase.isPresent()) {
            BigDecimal temp = new BigDecimal(1);

            return temp.divide(exchangeRateTargetToBase.get().getRate(), SCALE_RATE, RoundingMode.HALF_UP);
        }

        List<ExchangeRate> exchangeRatesByGeneralBaseCurrency =
                exchangeRateDAO.getExchangeRatesByGeneralBaseCurrency(baseCurrencyId, targetCurrencyId);
        if (!exchangeRatesByGeneralBaseCurrency.isEmpty()) {
            BigDecimal rate1 = exchangeRatesByGeneralBaseCurrency.get(0).getRate();
            BigDecimal rate2 = exchangeRatesByGeneralBaseCurrency.get(1).getRate();

            return rate2.divide(rate1, SCALE_RATE, RoundingMode.HALF_UP);
        }

        List<ExchangeRate> exchangeRatesByGeneralTargetCurrency =
                exchangeRateDAO.getExchangeRatesByGeneralTargetCurrency(baseCurrencyId, targetCurrencyId);
        if (!exchangeRatesByGeneralTargetCurrency.isEmpty()) {
            BigDecimal rate1 = exchangeRatesByGeneralTargetCurrency.get(0).getRate();
            BigDecimal rate2 = exchangeRatesByGeneralTargetCurrency.get(1).getRate();

            return rate1.divide(rate2, SCALE_RATE, RoundingMode.HALF_UP);
        }

        List<ExchangeRate> exchangeRatesByGeneralBaseTargetCurrency1 =
                exchangeRateDAO.getExchangeRatesByGeneralBaseTargetCurrency(baseCurrencyId, targetCurrencyId);
        if (!exchangeRatesByGeneralBaseTargetCurrency1.isEmpty()) {
            BigDecimal rate1 = exchangeRatesByGeneralBaseTargetCurrency1.get(0).getRate();
            BigDecimal rate2 = exchangeRatesByGeneralBaseTargetCurrency1.get(1).getRate();

            return rate1.multiply(rate2);
        }

        List<ExchangeRate> exchangeRatesByGeneralBaseTargetCurrency2 =
                exchangeRateDAO.getExchangeRatesByGeneralBaseTargetCurrency(targetCurrencyId, baseCurrencyId);
        if (!exchangeRatesByGeneralBaseTargetCurrency2.isEmpty()) {
            BigDecimal rate1 = exchangeRatesByGeneralBaseTargetCurrency2.get(0).getRate();
            BigDecimal rate2 = exchangeRatesByGeneralBaseTargetCurrency2.get(1).getRate();
            BigDecimal rateTargetToBase = rate1.multiply(rate2);
            BigDecimal temp = new BigDecimal(1);

            return temp.divide(rateTargetToBase, SCALE_RATE, RoundingMode.HALF_UP);
        }

        return null;
    }

    private ExchangeDTO convertExchangeToDTO(Currency baseCurrency, Currency targetCurrency, BigDecimal rate,
                                             BigDecimal amountForExchange, BigDecimal convertedAmount) {

        return new ExchangeDTO(currencyService.convertCurrencyToDTO(baseCurrency),
                currencyService.convertCurrencyToDTO(targetCurrency), rate, amountForExchange, convertedAmount);
    }
}
