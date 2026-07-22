package com.abdelkhalek.storehub.order.shared.model;

import lombok.Data;

import java.util.UUID;

@Data
public class AppliedOffer {
    UUID offerId;
    String label;
    String type;
}
