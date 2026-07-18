package com.abdelkhalek.storehub.order.store.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("stores")
@NoArgsConstructor
@Data
public class Store {

    @Id
    private UUID id;
    private String name;
    private String description;
    private String address;

    @Column("created_at")
    private Instant createdAt;

    }
