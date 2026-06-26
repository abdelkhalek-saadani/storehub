package proxiad.oms.pricing.domain.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class Money {
        private BigDecimal value;
        private Currency currency;

        public Money(BigDecimal value) {
            this.value = value;
            this.currency = Currency.getInstance("EUR");
        }

}
