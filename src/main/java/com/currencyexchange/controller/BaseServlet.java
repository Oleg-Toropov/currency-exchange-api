package com.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;

import java.io.PrintWriter;

public class BaseServlet extends HttpServlet {
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected PrintWriter printWriter;
}
