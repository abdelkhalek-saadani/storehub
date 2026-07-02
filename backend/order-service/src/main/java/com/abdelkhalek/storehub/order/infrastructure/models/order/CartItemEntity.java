package com.abdelkhalek.storehub.order.infrastructure.models.order;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@NoArgsConstructor

@Table(
        name = "cart_item"
)
public class CartItemEntity {

    @Id
    UUID id;
    UUID productId;
    int quantity;


    BigDecimal subtotal;

    BigDecimal unitPrice;

    BigDecimal originalUnitPrice;

    @ManyToOne( foreignKey = "cart_id")
    OrderEntity order;
    UUID orderId;


//    public MoneyEntity getSubtotal() {
//        return subtotal;
//    }
}
