package com.shaileshmishra.app.employee.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shaileshmishra.app.employee.dto.EmployeeRequestDTO;
import com.shaileshmishra.app.employee.dto.EmployeeResponseDTO;
import com.shaileshmishra.app.employee.models.Employee;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.exception.EmployeeAlreadyExistsException;
import com.shaileshmishra.app.employee.event.EmployeeEventProducer;
import com.shaileshmishra.app.exception.EmployeeNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeEventProducer eventProducer;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private EmployeeRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee(
                "John Doe", "Software Engineer", "sh1234abcd5678ef",
                new BigDecimal("85000.00"), "2026-09-18T12:00:00.000Z", "2026-09-18T12:00:00.000Z");

        sampleRequest = new EmployeeRequestDTO(
                "John Doe", "Software Engineer", new BigDecimal("85000.00"));
    }

    @Test
    @DisplayName("ping() should return 'All okay'")
    void ping_shouldReturnAllOkay() {
        assertEquals("All okay", employeeService.ping());
    }

    @Nested
    @DisplayName("getEmployeeById")
    class GetEmployeeById {

        @Test
        @DisplayName("should return employee when found")
        void shouldReturnEmployee_whenFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("sh1234abcd5678ef"))
                    .thenReturn(Optional.of(sampleEmployee));

            Employee result = employeeService.getEmployeeById("sh1234abcd5678ef");

            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("Software Engineer", result.getDesignation());
            assertEquals("sh1234abcd5678ef", result.getEmpId());
            verify(employeeRepository).findByEmpIdAndDeletedAtIsNull("sh1234abcd5678ef");
        }

        @Test
        @DisplayName("should throw EmployeeNotFoundException when not found")
        void shouldThrowException_whenNotFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("invalid-id"))
                    .thenReturn(Optional.empty());

            EmployeeNotFoundException exception = assertThrows(
                    EmployeeNotFoundException.class,
                    () -> employeeService.getEmployeeById("invalid-id"));

            assertEquals("Employee not found with empId: invalid-id", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getEmployees")
    class GetEmployees {

        @Test
        @DisplayName("should return list of employee response DTOs")
        void shouldReturnEmployeeList() {
            Employee employee2 = new Employee(
                    "Jane Smith", "Backend Developer", "sh5678efgh1234ab",
                    new BigDecimal("95000.00"), "2026-09-18T12:00:00.000Z", "2026-09-18T12:00:00.000Z");

            when(employeeRepository.findByDeletedAtIsNull())
                    .thenReturn(List.of(sampleEmployee, employee2));

            List<EmployeeResponseDTO> result = employeeService.getEmployees();

            assertEquals(2, result.size());
            assertEquals("John Doe", result.get(0).getName());
            assertEquals("Jane Smith", result.get(1).getName());
            verify(employeeRepository).findByDeletedAtIsNull();
        }

        @Test
        @DisplayName("should return empty list when no employees exist")
        void shouldReturnEmptyList_whenNoEmployees() {
            when(employeeRepository.findByDeletedAtIsNull())
                    .thenReturn(List.of());

            List<EmployeeResponseDTO> result = employeeService.getEmployees();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("createEmployee")
    class CreateEmployee {

        @Test
        @DisplayName("should create employee when name does not exist")
        void shouldCreateEmployee_whenNameIsUnique() {
            when(employeeRepository.existsByNameAndDeletedAtIsNull("John Doe"))
                    .thenReturn(false);
            when(employeeRepository.save(any(Employee.class)))
                    .thenReturn(sampleEmployee);

            EmployeeResponseDTO result = employeeService.createEmployee(sampleRequest);

            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("Software Engineer", result.getDesignation());
            assertEquals(new BigDecimal("85000.00"), result.getSalary());
            verify(employeeRepository).existsByNameAndDeletedAtIsNull("John Doe");
            verify(employeeRepository).save(any(Employee.class));
        }

        @Test
        @DisplayName("should publish CREATED event only after save() succeeds — correct ordering")
        void shouldPublishEvent_onlyAfterSave() {
            when(employeeRepository.existsByNameAndDeletedAtIsNull("John Doe"))
                    .thenReturn(false);
            when(employeeRepository.save(any(Employee.class)))
                    .thenReturn(sampleEmployee);

            employeeService.createEmployee(sampleRequest);

            // Assert strict ordering: save must happen before the event is published
            InOrder inOrder = inOrder(employeeRepository, eventProducer);
            inOrder.verify(employeeRepository).save(any(Employee.class));
            inOrder.verify(eventProducer).publishCreateEvent(any());
        }

        @Test
        @DisplayName("should NOT publish event when save() throws")
        void shouldNotPublishEvent_whenSaveFails() {
            when(employeeRepository.existsByNameAndDeletedAtIsNull("John Doe"))
                    .thenReturn(false);
            when(employeeRepository.save(any(Employee.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> employeeService.createEmployee(sampleRequest));

            // Event must never be published if persistence failed
            verify(eventProducer, never()).publishCreateEvent(any());
        }

        @Test
        @DisplayName("should throw EmployeeAlreadyExistsException when name exists")
        void shouldThrowException_whenNameAlreadyExists() {
            when(employeeRepository.existsByNameAndDeletedAtIsNull("John Doe"))
                    .thenReturn(true);

            EmployeeAlreadyExistsException exception = assertThrows(
                    EmployeeAlreadyExistsException.class,
                    () -> employeeService.createEmployee(sampleRequest));

            assertEquals("Employee with name 'John Doe' already exists.", exception.getMessage());
            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("updateEmployee")
    class UpdateEmployee {

        @Test
        @DisplayName("should update and return employee when found")
        void shouldUpdateEmployee_whenFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("sh1234abcd5678ef"))
                    .thenReturn(Optional.of(sampleEmployee));
            when(employeeRepository.save(any(Employee.class)))
                    .thenReturn(sampleEmployee);

            EmployeeRequestDTO updateRequest = new EmployeeRequestDTO(
                    "John Updated", "Senior Engineer", new BigDecimal("120000.00"));

            EmployeeResponseDTO result = employeeService.updateEmployee("sh1234abcd5678ef", updateRequest);

            assertNotNull(result);
            verify(employeeRepository).save(any(Employee.class));
        }

        @Test
        @DisplayName("should throw EmployeeNotFoundException when employee not found")
        void shouldThrowException_whenEmployeeNotFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("invalid-id"))
                    .thenReturn(Optional.empty());

            assertThrows(EmployeeNotFoundException.class,
                    () -> employeeService.updateEmployee("invalid-id", sampleRequest));

            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("deleteEmployee")
    class DeleteEmployee {

        @Test
        @DisplayName("should soft-delete employee and return success message")
        void shouldSoftDeleteEmployee_whenFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("sh1234abcd5678ef"))
                    .thenReturn(Optional.of(sampleEmployee));
            when(employeeRepository.save(any(Employee.class)))
                    .thenReturn(sampleEmployee);

            var result = employeeService.deleteEmployee("sh1234abcd5678ef");

            assertEquals("Employee with empId: sh1234abcd5678ef has been deleted successfully.",
                    result.get("message"));
            assertNotNull(sampleEmployee.getDeletedAt());
            verify(employeeRepository, times(1)).save(sampleEmployee);  // exactly one save — double-save bug is fixed
        }

        @Test
        @DisplayName("should throw EmployeeNotFoundException when employee not found")
        void shouldThrowException_whenEmployeeNotFound() {
            when(employeeRepository.findByEmpIdAndDeletedAtIsNull("invalid-id"))
                    .thenReturn(Optional.empty());

            assertThrows(EmployeeNotFoundException.class,
                    () -> employeeService.deleteEmployee("invalid-id"));

            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }
}

