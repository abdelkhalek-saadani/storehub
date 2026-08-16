package com.abdelkhalek.storehub.order.order.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record OrderRequest (
        @NotBlank  UUID storeId,
        @NotBlank  UUID cartId,
        @NotBlank  UUID slotId,
        String email,
        // These fields are optional
        // Wire real address object from the frontend
        String billingAddress,
        String deliveryAddress,
        String firstName,
        String lastName,
        String phoneNumber
){

}
