package com.currencyexchange.controller;

import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.dto.ExchangeDTO;
import com.currencyexchange.exception.*;
import com.currencyexchange.service.ExchangeService;
import com.currencyexchange.service.ExchangeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends BaseServlet {
    private final ExchangeService exchangeService = new ExchangeServiceImpl();
    private static final String MISSING_REQUIRED_FIELDS = "Missing required field(s) or write wrong";
    private static final String ERROR_CURRENCY_DOES_NOT_EXIST = "One (or both) currency from the currency pair does not " +
            "exist in the database";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        try {
            Validator.validateFields(new String[]{from, to, amount});
            Validator.validateRateOrAmount(amount);

            ExchangeDTO exchange =
                    exchangeService.makeExchange(from.toUpperCase(), to.toUpperCase(), amount);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(printWriter, exchange);

        } catch (InvalidFieldsException | InvalidRateOrAmountException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(MISSING_REQUIRED_FIELDS));
        } catch (CurrencyNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_DOES_NOT_EXIST));
        } catch (ExchangeRateNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
