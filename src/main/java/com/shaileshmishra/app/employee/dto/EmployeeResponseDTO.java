package com.shaileshmishra.app.employee.dto;

public class EmployeeResponseDTO {

    private String name;
    private String designation;
    private String empId;
    private double salary;

    public EmployeeResponseDTO(String name, String designation, String empId, double salary) {
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
