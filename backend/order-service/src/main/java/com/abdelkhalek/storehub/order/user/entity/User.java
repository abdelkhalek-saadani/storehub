package com.abdelkhalek.storehub.order.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("users")
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @Column("keycloak_id")
    private String keycloakId;

    private String email;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String address;

    @Column("phone_number")
    private String phoneNumber;

    @Column("preferred_store_id")
    private UUID preferredStoreId;

    private LocalDate dateOfBirth;

    private String gender;

    private String country;

    private String city;

    private String postalCode;

    }
