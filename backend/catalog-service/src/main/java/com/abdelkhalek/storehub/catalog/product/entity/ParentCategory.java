package com.abdelkhalek.storehub.catalog.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parent_categories")
@Data
@NoArgsConstructor
public class ParentCategory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategory> subCategories = new ArrayList<>();

    @Column(nullable = false)
    private UUID storeId;
}