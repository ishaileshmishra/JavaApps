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

    /**
     * This method is used to create an EmployeeService.
     * 
     * @param employeeRepository The employee repository.
     * @param eventProducer      The event producer.
     */
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeEventProducer eventProducer) {
        this.employeeRepository = employeeRepository;
        this.eventProducer = eventProducer;
    }

    /**
     * This method is used to get an employee entity by empId (internal use).
     */
    Employee getEmployeeEntityById(String empId) {
        return employeeRepository.findByEmpIdAndDeletedAtIsNull(empId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with empId: " + empId));
    }

    /**
     * This method is used to get an employee by empId.
     * 
     * @param empId The employee ID of the employee to get.
     * @return The response object containing the employee details.
     */
    public EmployeeResponseDTO getEmployeeById(String empId) {
        return toResponse(getEmployeeEntityById(empId));
    }

    /**
     * This method is used to get all employees with pagination.
     * 
     * @param skip  The number of elements to skip.
     * @param limit The maximum number of elements to return.
     * @return The response object containing the employee details.
     */
    public List<EmployeeResponseDTO> getEmployees(int skip, int limit) {
        return employeeRepository
                .findByDeletedAtIsNull(new com.shaileshmishra.app.common.util.OffsetPageRequest(skip, limit)).stream()
                .map(
                        this::toResponse)
                .toList();
    }

    /**
     * This method is used to create a new employee.
     * 
     * @param request The request object containing the employee details.
     * @return The response object containing the created employee details.
     */
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        String normalizedName = request.getName().trim();

        if (employeeRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(normalizedName)) {
            throw new EmployeeAlreadyExistsException(
                    "Employee with name '" + normalizedName + "' already exists.");
        }
        // now
        var currentTimestamp = UtcTimestamp.nowAsInstant();
        // Store the trimmed name, not the raw request value
        var employee = new Employee(normalizedName, request.getDesignation(), EmployeeIdGenerator.generate(),
                request.getSalary(), currentTimestamp, currentTimestamp);

        try {
            Employee saved = employeeRepository.save(employee);
            eventProducer.publishEvent(new EmployeeEvent("CREATED", saved.getEmpId(), saved.getName(),
                    saved.getDesignation(), saved.getSalary(),
                    UtcTimestamp.now()));
            return toResponse(saved);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new EmployeeAlreadyExistsException(
                    "Employee with name '" + normalizedName + "' already exists.");
        }
    }

    /**
     * This method is used to update an employee.
     * 
     * @param empId   The employee ID of the employee to update.
     * @param request The request object containing the employee details.
     * @return The response object containing the updated employee details.
     */
    public EmployeeResponseDTO updateEmployee(String empId, EmployeeRequestDTO request) {
        Employee employee = getEmployeeEntityById(empId);
        employee.setName(request.getName());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setUpdatedAt(UtcTimestamp.nowAsInstant());
        return toResponse(employeeRepository.save(employee));
    }

    /**
     * This method is used to partially update an employee.
     * 
     * @param empId   The employee ID of the employee to update.
     * @param request The request object containing the fields to update.
     * @return The response object containing the updated employee details.
     */
    public EmployeeResponseDTO patchEmployee(String empId,
            com.shaileshmishra.app.employee.dto.EmployeePatchRequestDTO request) {
        Employee employee = getEmployeeEntityById(empId);
        boolean updated = false;

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            employee.setName(request.getName().trim());
            updated = true;
        }
        if (request.getDesignation() != null && !request.getDesignation().trim().isEmpty()) {
            employee.setDesignation(request.getDesignation().trim());
            updated = true;
        }
        if (request.getSalary() != null) {
            employee.setSalary(request.getSalary());
            updated = true;
        }

        if (updated) {
            employee.setUpdatedAt(UtcTimestamp.nowAsInstant());
            employee = employeeRepository.save(employee);
        }
        return toResponse(employee);
    }

    /**
     * This method is used to convert an employee to a response object.
     * 
     * @param employee The employee to convert.
     * @return The response object containing the employee details.
     */
    private EmployeeResponseDTO toResponse(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getName(),
                employee.getDesignation(),
                employee.getEmpId(),
                employee.getSalary());
    }

    /**
     * This method is used to delete an employee.
     * 
     * @param empId The employee ID of the employee to delete.
     * @return The response object containing the deleted employee details.
     */
    public Map<String, Object> deleteEmployee(String empId) {
        Employee employee = getEmployeeEntityById(empId);
        employee.setDeletedAt(UtcTimestamp.nowAsInstant());
        employeeRepository.save(employee);
        eventProducer.publishEvent(new EmployeeEvent(
                "DELETED", employee.getEmpId(), employee.getName(),
                employee.getDesignation(), employee.getSalary(),
                UtcTimestamp.now()));
        return Map.of("message", "Employee with empId: " + empId + " has been deleted successfully.");
    }

}
