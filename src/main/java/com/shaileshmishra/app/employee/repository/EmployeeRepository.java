package com.shaileshmishra.app.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shaileshmishra.app.employee.models.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmpIdAndDeletedAtIsNull(String empId);

    List<Employee> findByDeletedAtIsNull();

    boolean existsByNameAndDeletedAtIsNull(String name);
}
