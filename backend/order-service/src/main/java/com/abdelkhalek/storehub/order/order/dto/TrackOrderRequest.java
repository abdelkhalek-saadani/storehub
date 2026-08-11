package com.abdelkhalek.storehub.order.order.dto;

import java.util.UUID;

public record TrackOrderRequest (UUID orderId, String email) {

}
