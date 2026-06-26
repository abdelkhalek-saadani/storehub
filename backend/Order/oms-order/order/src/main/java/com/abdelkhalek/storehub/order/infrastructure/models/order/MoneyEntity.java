package com.abdelkhalek.storehub.order.infrastructure.models.order;

import io.github.joselion.springr2dbcrelationships.annotations.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@Table(name ="money")
public class MoneyEntity {
    @Id UUID id;

    BigDecimal value;
    String currency;


}
