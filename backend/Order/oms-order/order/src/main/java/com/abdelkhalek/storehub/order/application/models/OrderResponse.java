package com.abdelkhalek.storehub.order.application.models;


/***
 * think about adding discriminator field, so the response at the front level knows which class it received
 * /@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
 * /@JsonSubTypes({
 *     /@JsonSubTypes.Type(value = ClassA.class, name = "A"),
 *     /@JsonSubTypes.Type(value = ClassB.class, name = "B")
 * })
 * sealed interface OrderResponse permits ClassA, ClassB {}
 */

public sealed interface OrderResponse permits CashOrderResponse, OnlineOrderResponse {
}
