package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.InvalidCurrencyCodeException;
import com.currencyexchange.exception.InvalidFieldsException;
import com.currencyexchange.exception.InvalidRateException;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private static final String ERROR_CURRENCY_DOES_NOT_EXIST = "One (or both) currency from the currency pair does not " +
            "exist in the database";
    private static final String ERROR_CURRENCY_PAIR_EXISTS = "Currency pair with this code already exists";
    private static final int ERROR_CODE_SQLITE_CONSTRAINT = 19;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<ExchangeRateDTO> exchangeRates = exchangeRateService.getAllExchangeRates();
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(printWriter, exchangeRates);
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");
        List<String> fields = Arrays.asList(baseCurrencyCode, targetCurrencyCode, rate);

        try {
            Validator.validateFields(fields);
            Validator.validateRate(rate);
        } catch (InvalidFieldsException | InvalidRateException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
            return;
        }

        try {
            CurrencyDTO baseCurrency = currencyService.getCurrencyByCode(baseCurrencyCode);
            CurrencyDTO targetCurrency = currencyService.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null || targetCurrency == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_DOES_NOT_EXIST));
            }

            ExchangeRateDTO newExchangeRateDTO =
                    new ExchangeRateDTO(null, baseCurrency, targetCurrency, new BigDecimal(rate));

            ExchangeRateDTO addedExchangeRate = exchangeRateService.addExchangeRate(newExchangeRateDTO);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(printWriter, addedExchangeRate);

        } catch (DatabaseUnavailableException e) {
            SQLException t = (SQLException) e.getCause();

            if (t.getErrorCode() == ERROR_CODE_SQLITE_CONSTRAINT) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_PAIR_EXISTS));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
            }
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
