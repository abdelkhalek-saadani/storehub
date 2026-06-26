package com.abdelkhalek.storehub.order.application.models;

import lombok.Data;
import jakarta.validation.constraints.Min;

@Data
public class MoneyDTO {

    @Min(value=0)
    private String value;
    private String currency;

    public MoneyDTO(String value) {
        this.value = value;
        this.currency = "EUR";
    }

    public MoneyDTO() {
        this.currency = "EUR";
        this.value = "0.00";
    }

    public MoneyDTO(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

}