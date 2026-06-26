package proxiad.oms.cart.domain.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Loggable {
    String value() default ""; // Optional custom log message
}