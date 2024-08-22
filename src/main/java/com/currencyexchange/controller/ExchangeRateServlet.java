package com.currencyexchange.controller;

import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeRateDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.ExchangeRateNotFoundException;
import com.currencyexchange.exception.InvalidCurrencyCodePairException;
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

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
