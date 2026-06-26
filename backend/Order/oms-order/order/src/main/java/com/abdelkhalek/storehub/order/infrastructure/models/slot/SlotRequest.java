package com.abdelkhalek.storehub.order.infrastructure.models.slot;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotRequest {
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
}
