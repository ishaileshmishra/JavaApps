package com.shaileshmishra.app.employee.dto;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class EmployeeRequestDTO {

    @NotBlank 
    private String name;

    @NotBlank
    private String designation;

    @Positive 
    private double salary;

    @DateTimeFormat 
    private String createdAt;

    public EmployeeRequestDTO(String name, String designation, double salary) {
        this.name = name;
        this.designation = designation;
        this.salary = salary;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public double getSalary() {
        return salary;
    }


}
