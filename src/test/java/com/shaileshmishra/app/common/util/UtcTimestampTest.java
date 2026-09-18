package com.shaileshmishra.app.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UtcTimestamp")
class UtcTimestampTest {

    @Test
    @DisplayName("should return a non-null timestamp")
    void shouldReturnNonNullTimestamp() {
        String timestamp = UtcTimestamp.now();
        assertNotNull(timestamp);
        assertFalse(timestamp.isBlank());
    }

    @Test
    @DisplayName("should return timestamp ending with 'Z' (UTC)")
    void shouldEndWithZ() {
        String timestamp = UtcTimestamp.now();
        assertTrue(timestamp.endsWith("Z"), "Timestamp should end with 'Z' but was: " + timestamp);
    }

    @Test
    @DisplayName("should match the expected ISO-like format yyyy-MM-ddTHH:mm:ss.SSSZ")
    void shouldMatchExpectedFormat() {
        String timestamp = UtcTimestamp.now();
        // Pattern: 2026-09-18T12:00:00.000Z
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"),
                "Timestamp format mismatch: " + timestamp);
    }

    @Test
    @DisplayName("should return a timestamp close to the current time")
    void shouldBeCloseToCurrentTime() {
        Instant before = Instant.now();
        String timestamp = UtcTimestamp.now();
        Instant after = Instant.now();

        // Parse the timestamp back to Instant
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);
        Instant parsed = Instant.from(formatter.parse(timestamp));

        assertFalse(parsed.isBefore(before.minusSeconds(1)),
                "Timestamp is too far in the past");
        assertFalse(parsed.isAfter(after.plusSeconds(1)),
                "Timestamp is too far in the future");
    }
}

