package com.abdelkhalek.storehub.order.order.dto;

public record AddressDto(
        AddressTypeDto type,
        String street,
        String city,
        String apartmentNumber,
        String zipCode,
        String deliveryInstructions) {

    public enum AddressTypeDto {
        HOME,
        OFFICE,
        APARTMENT
    }
}
