package proxiad.oms.pricing.infrastructure.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class MoneyEvent {
    private BigDecimal value;
    private Currency currency;

    public MoneyEvent(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public MoneyEvent(BigDecimal value) {
        this.value = value;
        this.currency = Currency.getInstance("EUR");
    }

    public MoneyEvent(){
        this.value = new BigDecimal(0);
        this.currency = Currency.getInstance("EUR");
    }


}