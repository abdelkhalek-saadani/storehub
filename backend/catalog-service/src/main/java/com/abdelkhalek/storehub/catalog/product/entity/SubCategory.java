package com.abdelkhalek.storehub.catalog.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "sub_categories")
@Data
@NoArgsConstructor
public class SubCategory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", nullable = true)
    private ParentCategory parentCategory;

    @Column(nullable = false)
    private UUID storeId;

    private String imageUrl;

}
