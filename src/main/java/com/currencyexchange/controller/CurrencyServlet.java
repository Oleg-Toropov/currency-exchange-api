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

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyServiceImpl();
    private static final String ERROR_MISSING_CODE = "Currency code is missing in the request URL";
    private static final String ERROR_MISSING_ID = "Currency id is missing in the request URL or write wrong";
    private static final String ERROR_CURRENCY_NOT_FOUND = "Currency not found";
    private static final String ERROR_DATABASE_UNAVAILABLE = "The database is unavailable";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_MISSING_CODE));
                return;
            }

            String currencyCode = pathInfo.substring(1).toUpperCase();
            CurrencyDTO currency = currencyService.getCurrencyByCode(currencyCode);

            if (currency == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_NOT_FOUND));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(printWriter, currency);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_DATABASE_UNAVAILABLE));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.length() <= 1 || !pathInfo.substring(1).matches("^\\d+$")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_MISSING_ID));
                return;
            }

            int currentId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = currencyService.deleteCurrency(currentId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_CURRENCY_NOT_FOUND));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(printWriter, new ErrorResponseDTO(ERROR_DATABASE_UNAVAILABLE));
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printWriter = response.getWriter();
        super.service(request, response);
    }
}
