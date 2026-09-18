package com.shaileshmishra.app.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class UtcTimestamp {

    final static String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern(PATTERN)
            .withZone(ZoneOffset.UTC);

    private UtcTimestamp() {
    }

    public static String now() {
        return FORMATTER.format(Instant.now());
    }
}
