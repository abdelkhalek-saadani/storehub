package com.abdelkhalek.storehub.catalog.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_shadow")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class UserShadow {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String keycloakId;
    private Instant syncedAt;

}