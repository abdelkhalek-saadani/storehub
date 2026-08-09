package com.abdelkhalek.storehub.order.order;

import java.util.UUID;

public record TrackOrderRequest (UUID orderId, String email) {

}
