package com.shaileshmishra.app.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shaileshmishra.app.employee.models.Employee;

import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmpIdAndDeletedAtIsNull(String empId);

    List<Employee> findByDeletedAtIsNull(Pageable pageable);

    boolean existsByNameAndDeletedAtIsNull(String name);

    // Case-insensitive duplicate check — guards against "john doe" vs "John Doe"
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}
