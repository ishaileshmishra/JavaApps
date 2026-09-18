package com.shaileshmishra.app.employee.dto;

import java.math.BigDecimal;

public class EmployeeResponseDTO {

    private String name;
    private String designation;
    private String empId;
    private BigDecimal salary;

    public EmployeeResponseDTO(String name, String designation, String empId, BigDecimal salary) {
        this.name = name;
        this.designation = designation;
        this.empId = empId;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
