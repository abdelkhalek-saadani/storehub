package com.abdelkhalek.storehub.catalog.slot.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "slot_config")
@Getter
@Setter
public class SlotConfig {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    // 0 = Sunday ... 6 = Saturday (matches java.time.DayOfWeek.getValue() % 7)
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_min", nullable = false)
    private Integer slotDurationMin;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "cutoff_minutes", nullable = false)
    private Integer cutoffMinutes;

    @Column(name = "extra_fee")
    private BigDecimal extraFee;

    @Column(nullable = false)
    private boolean active = true;
}
