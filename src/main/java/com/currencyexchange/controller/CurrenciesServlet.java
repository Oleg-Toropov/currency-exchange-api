package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
import com.currencyexchange.exception.DatabaseUnavailableException;
import com.currencyexchange.exception.InvalidParametersException;
import com.currencyexchange.service.CurrencyService;
import com.currencyexchange.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private static final String ERROR_CURRENCY_EXISTS = "Currency with this code already exists";
    private static final int ERROR_CODE_SQLITE_CONSTRAINT = 19;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setStatus(HttpServletResponse.SC_OK);
            printWriter.write(objectMapper.writeValueAsString(currencies));
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
        List<String> parameters = Arrays.asList(name, code, sign);

        try {
            Validator.validateParameters(parameters);
        } catch (InvalidParametersException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
            return;
        }

        try {
            CurrencyDTO newCurrency = new CurrencyDTO(null, code, name, sign);
            CurrencyDTO addedCurrency = currencyService.addCurrency(newCurrency);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(printWriter, addedCurrency);
        } catch (DatabaseUnavailableException e) {
            SQLException t = (SQLException) e.getCause();

            if (t.getErrorCode() == ERROR_CODE_SQLITE_CONSTRAINT) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_EXISTS));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(e.getMessage()));
            }
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
