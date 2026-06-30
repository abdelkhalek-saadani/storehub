package com.abdelkhalek.storehub.order.application.models.delivery;

public class DeliveryDTO {
    //Livraison { mode livraison Enum Retrait ou A DOMICILLE, adresse (@Client pour deuxieme mode ,@ Magasin pour premiere mode)
    DeliveryMode mode;
    AddressDTO address;
}
