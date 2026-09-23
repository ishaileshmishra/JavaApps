package com.shaileshmishra.app.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class UtcTimestamp {

    static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(PATTERN)
            .withZone(ZoneOffset.UTC);

    private UtcTimestamp() {
    }

    /**
     * Returns current UTC time as an ISO-8601 formatted String.
     * Use for display, logging, event payloads, and health responses.
     */
    public static String now() {
        return FORMATTER.format(Instant.now());
    }

    /**
     * Returns current UTC time as an {@link Instant}.
     * Use for domain model timestamp fields (stored as BSON Date in MongoDB)
     * so they support range queries, sorting, and TTL indexes.
     */
    public static Instant nowAsInstant() {
        return Instant.now();
    }
}
