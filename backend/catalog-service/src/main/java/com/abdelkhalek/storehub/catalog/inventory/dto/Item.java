package com.abdelkhalek.storehub.catalog.inventory.dto;

import java.util.UUID;

public record Item (UUID productId,
                   int quantity){}
