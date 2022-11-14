package datalogic.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CorrelationId {
    public static final String HTTP_HEADER = "X-Correlation-ID";
    public static final String MDC_KEY = "CorrelationId";

    public static String create() {
        final String correlationId = UUID.randomUUID().toString();
        MDC.put(MDC_KEY, correlationId);
        return correlationId;
    }

    public static Optional<String> value(){
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    public static void set(final String correlationId) {
        MDC.put(MDC_KEY, correlationId);
    }
    public static void remove() {
        MDC.remove(MDC_KEY);
    }
}
