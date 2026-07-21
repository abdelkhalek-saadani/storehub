package com.abdelkhalek.storehub.order.order.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@With
@NoArgsConstructor
public class Slot {
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;

    public static Slot getDefaultSlot() {
        Slot slot = new Slot();
        slot.date = LocalDate.now();
        slot.startTime = LocalTime.now();
        slot.endTime = LocalTime.now().plusHours(1);
        return slot;
    }
}
