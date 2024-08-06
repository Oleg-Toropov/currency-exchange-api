package com.currencyexchange.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurrencyDTO {
    private int id;
    private String code;
    private String fullName;
    private String sign;
}
