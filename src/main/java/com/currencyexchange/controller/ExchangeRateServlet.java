package com.currencyexchange.controller;

import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.*;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();
    private static final String EXCHANGE_RATE_NOT_FOUND = "Exchange rate for the pair not found";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        try {
            String[] codePair = Validator.validateCurrencyCodePair(pathInfo);

            ExchangeRateDTO exchangeRateDTO =
                    exchangeRateService.getExchangeRateByCurrencyCodePair(codePair[0], codePair[1]);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(printWriter, exchangeRateDTO);

        } catch (InvalidCurrencyCodePairException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (ExchangeRateNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String field = request.getReader().readLine();

        try {
            String[] codePair = Validator.validateCurrencyCodePair(pathInfo);
            String rate = Validator.validateRateFromBody(field);
            Validator.validateRateOrAmount(rate);

            ExchangeRateDTO exchangeRateDTO =
                    exchangeRateService.updateExchangeRate(codePair[0], codePair[1], rate);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(printWriter, exchangeRateDTO);

        } catch (InvalidCurrencyCodePairException | InvalidFieldsException | InvalidRateOrAmountException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(EXCHANGE_RATE_NOT_FOUND));
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();

        String method = request.getMethod();
        if (!method.equals("PATCH")) {
            super.service(request, response);
            return;
        }

        this.doPatch(request, response);
    }
}
