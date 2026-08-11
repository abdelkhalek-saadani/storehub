package com.abdelkhalek.storehub.order.store.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <b>Note: </b>This will be used when employee feature is added
 */
@Data
public class InviteEmployeeRequest {

    @NotBlank @Email
    private String email;

}