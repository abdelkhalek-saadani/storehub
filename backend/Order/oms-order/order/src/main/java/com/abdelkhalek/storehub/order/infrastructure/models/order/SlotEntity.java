package com.abdelkhalek.storehub.order.infrastructure.models.order;

import io.github.joselion.springr2dbcrelationships.annotations.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@Table(
        name = "slot"
)
@NoArgsConstructor
public class SlotEntity {
    @Id
    UUID id;

    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;

}
