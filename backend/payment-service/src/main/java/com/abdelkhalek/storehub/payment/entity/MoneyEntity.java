package com.abdelkhalek.storehub.payment.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
@Getter
@Setter
public class MoneyEntity {
    private BigDecimal value;
    private Currency currency;

    public MoneyEntity(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public MoneyEntity(BigDecimal value) {
        this.value = value;
        this.currency = Currency.getInstance("EUR");
    }

    public MoneyEntity(){
        this.value = new BigDecimal(0);
        this.currency = Currency.getInstance("EUR");
    }


}