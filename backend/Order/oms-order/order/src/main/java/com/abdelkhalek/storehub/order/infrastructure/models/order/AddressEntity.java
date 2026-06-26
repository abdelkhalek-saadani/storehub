package com.abdelkhalek.storehub.order.infrastructure.models.order;

import io.github.joselion.springr2dbcrelationships.annotations.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@With
@Table(
        name="address"
)
public class AddressEntity {
    @Id
    UUID id;

    String city;
    String street;
    int number;

}
