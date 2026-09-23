package com.shaileshmishra.app.employee.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private String name;
    private String designation;
    private String empId;
    private BigDecimal salary;
}
