package com.currencyexchange.model;

import com.currencyexchange.dto.CurrencyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private Integer id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
