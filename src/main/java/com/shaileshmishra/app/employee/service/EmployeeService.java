package com.shaileshmishra.app.employee.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.shaileshmishra.app.common.util.UtcTimestamp;
import com.shaileshmishra.app.employee.dto.EmployeeRequestDTO;
import com.shaileshmishra.app.employee.dto.EmployeeResponseDTO;
import com.shaileshmishra.app.employee.event.EmployeeEvent;
import com.shaileshmishra.app.employee.event.EmployeeEventProducer;
import com.shaileshmishra.app.employee.models.Employee;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.employee.util.EmployeeIdGenerator;
import com.shaileshmishra.app.exception.EmployeeAlreadyExistsException;
import com.shaileshmishra.app.exception.EmployeeNotFoundException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeEventProducer eventProducer; // ← inject

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeEventProducer eventProducer) {
        this.employeeRepository = employeeRepository;
        this.eventProducer = eventProducer;
    }

    public String ping() {
        return "All okay";
    }

    public Employee getEmployeeById(String empId) {
        return employeeRepository.findByEmpIdAndDeletedAtIsNull(empId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with empId: " + empId));
    }

    public List<EmployeeResponseDTO> getEmployees() {
        return employeeRepository.findByDeletedAtIsNull().stream().map(
                this::toResponse)
                .toList();
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        if (employeeRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new EmployeeAlreadyExistsException(
                    "Employee with name '" + request.getName() + "' already exists.");
        }
        var currentTimestamp = UtcTimestamp.now();
        var employee = new Employee(request.getName(), request.getDesignation(), EmployeeIdGenerator.generate(),
                request.getSalary(), currentTimestamp, currentTimestamp);

        // 🔔 Publish event after successful save
        eventProducer.publishCreateEvent(new EmployeeEvent(
                "CREATED", employee.getEmpId(), employee.getName(),
                employee.getDesignation(), employee.getSalary(),
                UtcTimestamp.now()));
        // save the employee and return the response
        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO updateEmployee(String empId, EmployeeRequestDTO request) {
        Employee employee = getEmployeeById(empId);
        employee.setName(request.getName());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setUpdatedAt(UtcTimestamp.now());
        return toResponse(employeeRepository.save(employee));
    }

    private EmployeeResponseDTO toResponse(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getName(),
                employee.getDesignation(),
                employee.getEmpId(),
                employee.getSalary());
    }

    public Map<String, Object> deleteEmployee(String empId) {
        Employee employee = getEmployeeById(empId);
        employee.setDeletedAt(UtcTimestamp.now());
        if (employee.getDeletedAt() == null) {
            throw new RuntimeException("Failed to delete employee with empId: " + empId);
        }
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete employee with empId: " + empId, e);
        }
        employeeRepository.save(employee);
        // 🔔 Publish event after successful delete
        eventProducer.publishDeleteEvent(new EmployeeEvent(
                "DELETED", employee.getEmpId(), employee.getName(),
                employee.getDesignation(), employee.getSalary(),
                UtcTimestamp.now()));
        return Map.of("message", "Employee with empId: " + empId + " has been deleted successfully.");
    }

}
