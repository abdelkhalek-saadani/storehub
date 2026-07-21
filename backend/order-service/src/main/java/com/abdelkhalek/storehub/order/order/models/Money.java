package com.abdelkhalek.storehub.order.order.models;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Money {

    @Min(value=0)
    private BigDecimal value;
    private String currency;

    public Money(BigDecimal value) {
        this.value = value;
        this.currency = "EUR";
    }

    public Money() {
        this.currency = "EUR";
        this.value = BigDecimal.ZERO;
    }

    public Money(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

}