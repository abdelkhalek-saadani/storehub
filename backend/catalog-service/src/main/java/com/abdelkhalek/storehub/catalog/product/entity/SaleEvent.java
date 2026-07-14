package com.abdelkhalek.storehub.catalog.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name= "sale_events")
@NoArgsConstructor
@Getter
@Setter
public class SaleEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String imageUrl;

    private String description;

}
