package com.shaileshmishra.app.employee.util;

import java.util.UUID;

public final class EmployeeIdGenerator {

    private EmployeeIdGenerator() {
    }

    public static String generate() {
        String[] uuidParts = UUID.randomUUID().toString().split("-");
        return "sh" + uuidParts[3] + uuidParts[4];
    }
}
