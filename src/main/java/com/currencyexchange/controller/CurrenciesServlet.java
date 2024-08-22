package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.exception.CurrencyExistsException;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.InvalidFieldsException;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(printWriter, currencies);
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");
        List<String> fields = List.of(name, code, sign);

        try {
            Validator.validateFields(fields);

            CurrencyDTO newCurrency = new CurrencyDTO(null, name, code, sign);
            CurrencyDTO addedCurrency = currencyService.addCurrency(newCurrency);
            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(printWriter, addedCurrency);

        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
        } catch (CurrencyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
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
