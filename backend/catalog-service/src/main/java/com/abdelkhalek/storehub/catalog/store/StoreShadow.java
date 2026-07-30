package com.abdelkhalek.storehub.catalog.store;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "store_shadow")
@NoArgsConstructor
@Data
public class StoreShadow {

    @Id
    private UUID id;
    private String slug;
    private UUID ownerId;
    private String status;
    private Instant syncedAt;

}
