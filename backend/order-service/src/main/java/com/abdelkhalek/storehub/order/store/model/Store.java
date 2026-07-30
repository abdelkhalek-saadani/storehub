package com.abdelkhalek.storehub.order.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("stores")
@NoArgsConstructor
@Data
@With
@AllArgsConstructor
public class Store {

    @Id
    private UUID id;
    private String name;
    private String description;
    private String address;

    private String slug;

    @Column("created_at")
    private Instant createdAt;

    }
