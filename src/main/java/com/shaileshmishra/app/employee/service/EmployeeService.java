package com.shaileshmishra.app.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.shaileshmishra.app.common.util.UtcTimestamp;
import com.shaileshmishra.app.employee.dto.EmployeeRequestDTO;
import com.shaileshmishra.app.employee.dto.EmployeeResponseDTO;
import com.shaileshmishra.app.employee.models.Employee;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.employee.util.EmployeeIdGenerator;
import com.shaileshmishra.app.exception.EmployeeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService(EmployeeRepository employeeRepository) {
        logger.info("EmployeeService is working");
        this.employeeRepository = employeeRepository;
    }

    public String ping() {
        return "All okay";
    }

    public Employee getEmployeeById(String empId) {
        return employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                    "Employee not found with empId: " + empId
                ));
    }

    public List<EmployeeResponseDTO> getEmployees() {
        return employeeRepository.findAll().stream().map(
                emp -> new EmployeeResponseDTO(emp.getName(), emp.getDesignation(), emp.getEmpId(), emp.getSalary()))
                .toList();
    }

    public EmployeeResponseDTO createEmployee(@RequestBody EmployeeRequestDTO request) {
        var currentTimestamp = UtcTimestamp.now();
        var employee = new Employee(request.getName(), request.getDesignation(), EmployeeIdGenerator.generate(),
                request.getSalary(), currentTimestamp, currentTimestamp);
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            logger.error("Error occurred while storing employee: " + request.getName(), e.getMessage());
            throw new EmployeeNotFoundException(
                    "Error occurred while saving employee: " + request.getName()
                );
        }
        return new EmployeeResponseDTO(employee.getName(), employee.getDesignation(),
                employee.getEmpId(), employee.getSalary());
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

    public String deleteEmployee(String empId) {
        Employee employee = getEmployeeById(empId);
        employee.setDeletedAt(UtcTimestamp.now());
        employeeRepository.save(employee);
        return "Employee with empId: " + empId + " has been deleted successfully.";
    }

}
