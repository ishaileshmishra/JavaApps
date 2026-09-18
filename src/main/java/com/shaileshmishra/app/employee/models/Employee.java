package com.shaileshmishra.app.employee.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    private String name;
    private String designation;
    @Indexed(unique = true)
    private String empId;
    private BigDecimal salary;
    private String internalCode;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public Employee(String name, String designation, String empId, BigDecimal salary, String createdAt, String updatedAt) {
        this.name = name;
        this.designation = designation;
        this.empId = empId;
        this.salary = salary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.internalCode = "INT-100";
        this.deletedAt = null;
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

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}
