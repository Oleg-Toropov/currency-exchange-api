package com.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class ErrorResponseDTO {
    private final String message;

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
