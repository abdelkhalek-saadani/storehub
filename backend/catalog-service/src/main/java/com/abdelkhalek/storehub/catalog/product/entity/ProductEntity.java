package com.abdelkhalek.storehub.catalog.product.entity;

import com.abdelkhalek.storehub.catalog.pricing.entity.DiscountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private String name;

    private String description;

    // Cached current price. Source of truth for *history* is UnitPriceHistory;
    // this column exists so reads (e.g. /catalog/prices) don't need a join.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;


    @ManyToMany(mappedBy = "products")
    private Set<DiscountEntity> discounts = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = true)
    private SubCategory subCategory;

    private Boolean isBestSeller;

    @ManyToOne
    @JoinColumn(name= "sale_event_id")
    private SaleEvent saleEvent;


}