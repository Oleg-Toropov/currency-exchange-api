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

@WebServlet(name = "AddCurrencyServlet", urlPatterns = "/currencies")
public class AddCurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullName = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        if (fullName == null || code == null || sign == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"Missing required field(s)\"}");
            return;
        }

        CurrencyDTO newCurrency = new CurrencyDTO();
        newCurrency.setFullName(fullName);
        newCurrency.setCode(code);
        newCurrency.setSign(sign);

        CurrencyDTO addedCurrency = currencyService.addCurrency(newCurrency);

        if (addedCurrency == null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"message\": \"Currency with this code already exists\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), addedCurrency);
        }
    }
}
