package com.abdelkhalek.storehub.order.infrastructure.models.order;

import com.abdelkhalek.storehub.order.order.models.DeliveryMode;
import com.abdelkhalek.storehub.order.order.models.PaymentMode;
import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import io.github.joselion.springr2dbcrelationships.annotations.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@NoArgsConstructor
@Table(
        name = "customer_order"
)
public class OrderEntity {

    @Id
    UUID id;

    LocalDateTime date;

    UUID deliveryAddressId;
    @OneToOne
    AddressEntity deliveryAddress;


//    UUID storeId;

    @OneToOne
    AddressEntity invoiceAddress;
    UUID invoiceAddressId;

    DeliveryMode deliveryMode;


    @OneToOne
    SlotEntity slot;
    UUID slotId;


    PaymentMode paymentMode;


    @OneToOne
    MoneyEntity originalSubtotal;
    UUID originalSubtotalId;

    // subtotal
    @OneToOne
    MoneyEntity subtotal;
    UUID subtotalId;



    // livraison frais (si applicable)
    @OneToOne
    MoneyEntity deliveryFee;
    UUID deliveryFeeId;


    // total a payer (inclut subtotal + livraison )
    @OneToOne
    MoneyEntity total;
   UUID totalId;


    // items list (with their prices)
    @OneToMany(mappedBy = "order_id")
    List<CartItemEntity> cartItems;

    UUID slotRetainId;

    UUID inventoryRetainId;

}
