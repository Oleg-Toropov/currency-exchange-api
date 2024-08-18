package com.currencyexchange.controller;

import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.service.ExchangeRateServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet{
    private final ExchangeRateService exchangeRateService = new ExchangeRateServiceImpl();

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
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
