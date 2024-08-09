package com.currencyexchange.controller;

import com.currencyexchange.dto.CurrencyDTO;
import com.currencyexchange.dto.ErrorResponseDTO;
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
    private static final String ERROR_MISSING_FIELDS = "Missing required field(s)";
    private static final String ERROR_CURRENCY_EXISTS = "Currency with this code already exists";
    private static final String ERROR_DATABASE_UNAVAILABLE = "The database is unavailable";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            printWriter.write(objectMapper.writeValueAsString(currencies));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_DATABASE_UNAVAILABLE));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String fullName = request.getParameter("name");
            String code = request.getParameter("code");
            String sign = request.getParameter("sign");

            if (fullName == null || code == null || sign == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_MISSING_FIELDS));
                return;
            }

            CurrencyDTO newCurrency = new CurrencyDTO();
            newCurrency.setName(fullName);
            newCurrency.setCode(code);
            newCurrency.setSign(sign);

            CurrencyDTO addedCurrency = currencyService.addCurrency(newCurrency);

            if (addedCurrency == null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_EXISTS));
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                objectMapper.writeValue(printWriter, addedCurrency);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_DATABASE_UNAVAILABLE));
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
