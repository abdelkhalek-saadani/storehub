package com.abdelkhalek.storehub.order.domain.models;

import lombok.Data;

@Data
public class Address {
    String city;
    String street;
    String number;

    public static Address getDefaultAddress() {
        Address address = new Address();
        address.city = "some city";
        address.street = "some street";
        address.number = "some number";
        return address;
    }
}
