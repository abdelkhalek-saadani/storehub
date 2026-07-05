package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppliedDiscount {
    private String description;
    private BigDecimal amountPerUnit;

    public String toString() {
        return "description = " + description + ", this discount subtracts "
                + amountPerUnit + " from each product's   unit price";
    }

}
