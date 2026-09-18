package com.shaileshmishra.app.employee.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@DisplayName("EmployeeIdGenerator")
class EmployeeIdGeneratorTest {

    @Test
    @DisplayName("should generate a non-null ID")
    void shouldGenerateNonNullId() {
        String id = EmployeeIdGenerator.generate();
        assertNotNull(id);
        assertFalse(id.isBlank());
    }

    @Test
    @DisplayName("should generate an ID starting with 'sh' prefix")
    void shouldStartWithShPrefix() {
        String id = EmployeeIdGenerator.generate();
        assertTrue(id.startsWith("sh"), "ID should start with 'sh' but was: " + id);
    }

    @RepeatedTest(50)
    @DisplayName("should generate unique IDs across multiple invocations")
    void shouldGenerateUniqueIds() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            assertTrue(ids.add(EmployeeIdGenerator.generate()),
                    "Duplicate ID generated");
        }
    }
}

