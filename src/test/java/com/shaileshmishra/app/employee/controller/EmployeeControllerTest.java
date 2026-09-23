package com.shaileshmishra.app.employee.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.shaileshmishra.app.employee.dto.EmployeeRequestDTO;
import com.shaileshmishra.app.employee.dto.EmployeeResponseDTO;
import com.shaileshmishra.app.employee.service.EmployeeService;
import com.shaileshmishra.app.exception.EmployeeAlreadyExistsException;
import com.shaileshmishra.app.exception.EmployeeNotFoundException;

import com.shaileshmishra.app.security.CustomUserDetailsService;
import com.shaileshmishra.app.security.JwtTokenProvider;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EmployeeController")
class EmployeeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EmployeeService employeeService;

        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;

        @MockitoBean
        private CustomUserDetailsService userDetailsService;

        private static final String BASE_URL = "/employees";

        @Nested
        @DisplayName("GET /employees")
        class GetAllEmployees {

                @Test
                @DisplayName("should return 200 with list of employees")
                void shouldReturnEmployeeList() throws Exception {
                        List<EmployeeResponseDTO> employees = List.of(
                                        new EmployeeResponseDTO("John Doe", "Engineer", "sh1234abcd",
                                                        new BigDecimal("85000")),
                                        new EmployeeResponseDTO("Jane Smith", "Developer", "sh5678efgh",
                                                        new BigDecimal("95000")));

                        when(employeeService.getEmployees(0, 20)).thenReturn(employees);

                        mockMvc.perform(get(BASE_URL))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$", hasSize(2)))
                                        .andExpect(jsonPath("$[0].name", is("John Doe")))
                                        .andExpect(jsonPath("$[0].empId", is("sh1234abcd")))
                                        .andExpect(jsonPath("$[1].name", is("Jane Smith")));
                }

                @Test
                @DisplayName("should return 200 with empty list when no employees")
                void shouldReturnEmptyList() throws Exception {
                        when(employeeService.getEmployees(0, 20)).thenReturn(List.of());

                        mockMvc.perform(get(BASE_URL))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$", hasSize(0)));
                }
        }

        @Nested
        @DisplayName("GET /employees/{empId}")
        class GetEmployeeById {

                @Test
                @DisplayName("should return 200 with employee when found")
                void shouldReturnEmployee() throws Exception {
                        EmployeeResponseDTO responseDto = new EmployeeResponseDTO(
                                        "John Doe", "Engineer", "sh1234abcd",
                                        new BigDecimal("85000"));

                        when(employeeService.getEmployeeById("sh1234abcd")).thenReturn(responseDto);

                        mockMvc.perform(get(BASE_URL + "/sh1234abcd"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.name", is("John Doe")))
                                        .andExpect(jsonPath("$.designation", is("Engineer")))
                                        .andExpect(jsonPath("$.empId", is("sh1234abcd")))
                                        .andExpect(jsonPath("$.salary", is(85000)));
                }

                @Test
                @DisplayName("should return 404 when employee not found")
                void shouldReturn404_whenNotFound() throws Exception {
                        when(employeeService.getEmployeeById("invalid-id"))
                                        .thenThrow(new EmployeeNotFoundException(
                                                        "Employee not found with empId: invalid-id"));

                        mockMvc.perform(get(BASE_URL + "/invalid-id"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.error_message", is("Employee With UID Not Found.")))
                                        .andExpect(jsonPath("$.error_code", is(404)));
                }
        }

        @Nested
        @DisplayName("POST /employees")
        class CreateEmployee {

                @Test
                @DisplayName("should return 201 when employee created successfully")
                void shouldCreateEmployee() throws Exception {
                        EmployeeResponseDTO response = new EmployeeResponseDTO(
                                        "John Doe", "Engineer", "sh1234abcd", new BigDecimal("85000"));

                        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                                        .thenReturn(response);

                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "John Doe",
                                                            "designation": "Engineer",
                                                            "salary": 85000
                                                        }
                                                        """))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.name", is("John Doe")))
                                        .andExpect(jsonPath("$.empId", is("sh1234abcd")));
                }

                @Test
                @DisplayName("should return 409 when employee name already exists")
                void shouldReturn409_whenDuplicateName() throws Exception {
                        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                                        .thenThrow(new EmployeeAlreadyExistsException(
                                                        "Employee with name 'John Doe' already exists."));

                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "John Doe",
                                                            "designation": "Engineer",
                                                            "salary": 85000
                                                        }
                                                        """))
                                        .andExpect(status().isConflict())
                                        .andExpect(jsonPath("$.error_message", is("Employee already exists.")))
                                        .andExpect(jsonPath("$.error_code", is(409)));
                }

                @Test
                @DisplayName("should return 400 when name is blank")
                void shouldReturn400_whenNameIsBlank() throws Exception {
                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "",
                                                            "designation": "Engineer",
                                                            "salary": 85000
                                                        }
                                                        """))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("should return 400 when salary is negative")
                void shouldReturn400_whenSalaryIsNegative() throws Exception {
                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "John Doe",
                                                            "designation": "Engineer",
                                                            "salary": -5000
                                                        }
                                                        """))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("should return 400 when request body is missing required fields")
                void shouldReturn400_whenFieldsMissing() throws Exception {
                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("PUT /employees/{empId}")
        class UpdateEmployee {

                @Test
                @DisplayName("should return 200 when employee updated successfully")
                void shouldUpdateEmployee() throws Exception {
                        EmployeeResponseDTO response = new EmployeeResponseDTO(
                                        "John Updated", "Senior Engineer", "sh1234abcd", new BigDecimal("120000"));

                        when(employeeService.updateEmployee(eq("sh1234abcd"), any(EmployeeRequestDTO.class)))
                                        .thenReturn(response);

                        mockMvc.perform(put(BASE_URL + "/sh1234abcd")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "John Updated",
                                                            "designation": "Senior Engineer",
                                                            "salary": 120000
                                                        }
                                                        """))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.name", is("John Updated")))
                                        .andExpect(jsonPath("$.designation", is("Senior Engineer")));
                }

                @Test
                @DisplayName("should return 404 when updating non-existent employee")
                void shouldReturn404_whenEmployeeNotFound() throws Exception {
                        when(employeeService.updateEmployee(eq("invalid-id"), any(EmployeeRequestDTO.class)))
                                        .thenThrow(new EmployeeNotFoundException(
                                                        "Employee not found with empId: invalid-id"));

                        mockMvc.perform(put(BASE_URL + "/invalid-id")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("""
                                                        {
                                                            "name": "John Updated",
                                                            "designation": "Senior Engineer",
                                                            "salary": 120000
                                                        }
                                                        """))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.error_code", is(404)));
                }
        }

        @Nested
        @DisplayName("DELETE /employees/{empId}")
        class DeleteEmployee {

                @Test
                @DisplayName("should return 200 with success message")
                void shouldDeleteEmployee() throws Exception {
                        when(employeeService.deleteEmployee("sh1234abcd"))
                                        .thenReturn(Map.of("message",
                                                        "Employee with empId: sh1234abcd has been deleted successfully."));

                        mockMvc.perform(delete(BASE_URL + "/sh1234abcd"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.message",
                                                        containsString("deleted successfully")));
                }

                @Test
                @DisplayName("should return 404 when deleting non-existent employee")
                void shouldReturn404_whenEmployeeNotFound() throws Exception {
                        when(employeeService.deleteEmployee("invalid-id"))
                                        .thenThrow(new EmployeeNotFoundException(
                                                        "Employee not found with empId: invalid-id"));

                        mockMvc.perform(delete(BASE_URL + "/invalid-id"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.error_code", is(404)));
                }
        }
}
