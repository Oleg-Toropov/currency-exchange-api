package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.InvalidCurrencyCodeException;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private static final String ERROR_CURRENCY_NOT_FOUND = "Currency not found";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        try {
            Validator.validateCurrencyCode(pathInfo);

            String currencyCode = pathInfo.substring(1).toUpperCase();
            CurrencyDTO currency = currencyService.getCurrencyByCode(currencyCode);

            if (currency == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_NOT_FOUND));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(printWriter, currency);
            }
        } catch (InvalidCurrencyCodeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        try {
            Validator.validateCurrencyCode(pathInfo);

            String currencyCode = pathInfo.substring(1).toUpperCase();
            boolean wasItDeleted = currencyService.deleteCurrency(currencyCode);

            if (wasItDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_NOT_FOUND));
            }
        } catch (InvalidCurrencyCodeException e) {
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
