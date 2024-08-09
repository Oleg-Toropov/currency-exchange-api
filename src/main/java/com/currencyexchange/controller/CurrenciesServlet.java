package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final String ERROR_MISSING_FIELDS = "Missing required field(s)";
    private static final String ERROR_CURRENCY_EXISTS = "Currency with this code already exists";
    private static final String ERROR_DATABASE_UNAVAILABLE = "The database is unavailable";
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(currencies));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE_UNAVAILABLE);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String fullName = request.getParameter("name");
            String code = request.getParameter("code");
            String sign = request.getParameter("sign");

            if (fullName == null || code == null || sign == null) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(ERROR_MISSING_FIELDS);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), errorResponse);
                return;
            }

            CurrencyDTO newCurrency = new CurrencyDTO();
            newCurrency.setName(fullName);
            newCurrency.setCode(code);
            newCurrency.setSign(sign);

            CurrencyDTO addedCurrency = currencyService.addCurrency(newCurrency);

            if (addedCurrency == null) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(ERROR_CURRENCY_EXISTS);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), errorResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), addedCurrency);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_DATABASE_UNAVAILABLE);
        }
    }
}
