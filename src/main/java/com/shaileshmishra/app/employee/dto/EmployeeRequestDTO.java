package com.shaileshmishra.app.employee.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class EmployeeRequestDTO {

    @NotBlank 
    private String name;

    @NotBlank
    private String designation;

    @Positive
    private BigDecimal salary;

    public EmployeeRequestDTO() {
    }

    public EmployeeRequestDTO(String name, String designation, BigDecimal salary) {
        this.name = name;
        this.designation = designation;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
