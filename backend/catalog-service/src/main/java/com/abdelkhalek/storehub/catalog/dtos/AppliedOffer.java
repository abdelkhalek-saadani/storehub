package com.abdelkhalek.storehub.catalog.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AppliedOffer {
    UUID offerId;
    String label;
    String type;
}
