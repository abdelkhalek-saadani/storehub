package com.abdelkhalek.storehub.order.store.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStoreRequest {

    @NotBlank
    private String name;

    private String description;

    private String address;

}
