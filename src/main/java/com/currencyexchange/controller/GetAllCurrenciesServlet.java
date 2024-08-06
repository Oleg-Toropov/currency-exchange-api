package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetAllCurrenciesServlet", urlPatterns = "/currencies")
public class GetAllCurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(currencies));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching currencies");
        }
    }
}
