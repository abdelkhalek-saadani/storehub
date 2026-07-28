package com.abdelkhalek.storehub.order.order.dto.product;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RetainResponse {
    List<UUID> retainIds;
}
