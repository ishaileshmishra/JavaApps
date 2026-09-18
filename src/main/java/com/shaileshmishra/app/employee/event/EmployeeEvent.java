package com.shaileshmishra.app.employee.event;
import java.math.BigDecimal;

public class EmployeeEvent {
    private String eventType;   // "CREATED" or "DELETED"
    private String empId;
    private String name;
    private String designation;
    private BigDecimal salary;
    private String timestamp;

    public EmployeeEvent() {
    }

    public EmployeeEvent(String eventType, String empId, String name, String designation, BigDecimal salary, String timestamp) {
        this.eventType = eventType;
        this.empId = empId;
        this.name = name;
        this.designation = designation;
        this.salary = salary;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
