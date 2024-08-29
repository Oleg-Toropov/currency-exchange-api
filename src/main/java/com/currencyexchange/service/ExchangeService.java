package com.currencyexchange.service;

import com.currencyexchange.dto.ExchangeDTO;

public interface ExchangeService {
    ExchangeDTO makeExchange(String from, String to, String amount);
}
