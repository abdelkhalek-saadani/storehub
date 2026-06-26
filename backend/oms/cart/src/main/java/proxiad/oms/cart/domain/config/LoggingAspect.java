package proxiad.oms.cart.domain.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;

// TODO: check best practices for debug logging
@Slf4j
@Aspect
@Component
public class LoggingAspect {


    // TODO configure lombok to log to a file with roll
    @Before("@within(Loggable)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Executing method: {} with arguments: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "@within(Loggable)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method executed: {} - Result: {}", joinPoint.getSignature().toShortString(), result);
    }
}