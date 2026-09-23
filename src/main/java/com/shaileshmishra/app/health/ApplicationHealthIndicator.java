package com.shaileshmishra.app.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import com.shaileshmishra.app.common.util.UtcTimestamp;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();

        return Health.up()
                .withDetail("application", "JavaApps")
                .withDetail("status", "OPERATIONAL")
                .withDetail("timestamp", UtcTimestamp.now())
                .withDetail("freeMemoryBytes", freeMemory)
                .withDetail("totalMemoryBytes", totalMemory)
                .build();
    }
}
