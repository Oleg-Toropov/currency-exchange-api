package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.InvalidCurrencyCodeException;
import com.currencyexchange.exception.InvalidCurrencyCodePairException;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet{
    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
    private static final String ERROR_EXCHANGE_RATE_NOT_FOUND = "Exchange rate for the pair not found";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        try {
            String[] codePair = Validator.validateCurrencyCodePair(pathInfo);
            String baseCode = codePair[0];
            String targetCode = codePair[1];

            ExchangeRateDTO exchangeRateDTO = exchangeRateService.getExchangeRateByCurrencyCodePair(baseCode, targetCode);

            if (exchangeRateDTO == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_EXCHANGE_RATE_NOT_FOUND));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(printWriter, exchangeRateDTO);
            }
        } catch (InvalidCurrencyCodePairException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
