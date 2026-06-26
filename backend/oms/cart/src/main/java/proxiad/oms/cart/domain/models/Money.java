package proxiad.oms.cart.domain.models;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class Money {

    private BigDecimal value;
    private Currency currency;


    public Money(){
        this.value = new BigDecimal(0);
        this.currency = Currency.getInstance("EUR");
    }

    public Money(BigDecimal value) {
        this.value = value;
        this.currency = Currency.getInstance("EUR");
    }




    public Money(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

}
