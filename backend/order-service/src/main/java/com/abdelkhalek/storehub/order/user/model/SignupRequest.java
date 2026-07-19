package com.abdelkhalek.storehub.order.user.model;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String address;
    private String phoneNumber;


}