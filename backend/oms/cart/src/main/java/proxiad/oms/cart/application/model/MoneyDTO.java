package proxiad.oms.cart.application.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyDTO {

    @Min(value=0)
    private String value;
    private String currency;

    public MoneyDTO(String value) {
        this.value = value;
        this.currency = "EUR";
    }

    public MoneyDTO() {
        this.currency = "EUR";
        this.value = "0.00";
    }

    public MoneyDTO(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

}
