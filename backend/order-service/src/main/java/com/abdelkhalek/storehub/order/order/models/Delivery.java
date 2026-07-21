package com.abdelkhalek.storehub.order.order.models;

import lombok.Data;

@Data
public class Delivery {
    //Livraison { mode livraison Enum Retrait ou A DOMICILLE, adresse (@Client pour deuxieme mode ,@ Magasin pour premiere mode)
    DeliveryMode mode;
    Address address;
}
