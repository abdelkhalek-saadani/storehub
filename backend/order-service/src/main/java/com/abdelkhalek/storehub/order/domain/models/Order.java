package com.abdelkhalek.storehub.order.domain.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Order {
    UUID id;

    LocalDateTime date;

    Store store;

    Address deliveryAddress;
    // adresse facturation
    Address invoiceAddress;
    // methode de livraison
    DeliveryMode deliveryMode;

    // details de creneaux
    Slot slot;

    // methode de paiement
    PaymentMode paymentMode;

    // originalSubtotal
    Money originalSubtotal;

    // subtotal
    Money subtotal;
    // livraison frais (si applicable)
    Money deliveryFee;

    // total a payer (inclut subtotal + livraison )
    Money total;

    // items list (with their prices)
    List<CartItem> cartItems;

    UUID slotRetainId;

    UUID inventoryRetainId;

    public Order(UUID id) {
        this.id = id;
    }

    public Order(){}

}
