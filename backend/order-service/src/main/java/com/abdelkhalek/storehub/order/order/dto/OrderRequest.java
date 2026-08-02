package com.abdelkhalek.storehub.order.order.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record OrderRequest (
        @NotBlank  UUID slotId,
        @NotBlank  UUID storeId,
        @NotBlank  UUID cartId,
        // These fields are optional
        String billingAddress,
        String deliveryAddress,
        String firstName,
        String lastName,
        String phoneNumber
){

}
