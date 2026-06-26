package proxiad.oms.cart.infrastructure.models;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxiad.oms.cart.domain.models.Money;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Embeddable
public class MoneyEntity {
    private BigDecimal value;
    private Currency currency;

    public MoneyEntity(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public MoneyEntity(BigDecimal value) {
        this.value = value;
        this.currency = Currency.getInstance("EUR");
    }

    public MoneyEntity(){
        this.value = new BigDecimal(0);
        this.currency = Currency.getInstance("EUR");
    }


}