package com.shaileshmishra.app.employee.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String designation;
    @Indexed(unique = true)
    private String empId;
    private BigDecimal salary;
    @Builder.Default
    private String internalCode = "INT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Employee(String name, String designation, String empId, BigDecimal salary, Instant createdAt,
            Instant updatedAt) {
        this.name = name;
        this.designation = designation;
        this.empId = empId;
        this.salary = salary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.internalCode = "blt" + java.util.UUID.randomUUID().toString().substring(0, 8).toLowerCase();
        this.deletedAt = null;
    }
}
