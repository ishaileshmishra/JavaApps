package com.shaileshmishra.app.employee.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.shaileshmishra.app.employee.dto.EmployeeRequestDTO;
import com.shaileshmishra.app.employee.dto.EmployeeResponseDTO;
import com.shaileshmishra.app.employee.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponseDTO> getEmployees() {
        return this.employeeService.getEmployees();
    }

    @GetMapping("/{empId}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(
            @PathVariable String empId) {
        var currentEmp = this.employeeService.getEmployeeById(empId);
        return ResponseEntity.ok(new EmployeeResponseDTO(
                currentEmp.getName(),
                currentEmp.getDesignation(),
                currentEmp.getEmpId(),
                currentEmp.getSalary()));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.employeeService.createEmployee(request));
    }

    @PutMapping("/{empId}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable String empId,
            @Valid @RequestBody EmployeeRequestDTO request) {
        return ResponseEntity.ok(
                this.employeeService.updateEmployee(empId, request));
    }

    @DeleteMapping("/{empId}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(
            @PathVariable String empId) {
        var response = this.employeeService.deleteEmployee(empId);
        return ResponseEntity.ok(response);
    }
}
