package com.abdelkhalek.storehub.order.store.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteEmployeeRequest {

    @NotBlank @Email
    private String email;

}