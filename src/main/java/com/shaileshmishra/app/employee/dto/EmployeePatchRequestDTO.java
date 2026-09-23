package com.shaileshmishra.app.employee.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePatchRequestDTO {

    private String name;

    private String designation;

    @Positive(message = "Salary must be positive")
    private BigDecimal salary;
}
