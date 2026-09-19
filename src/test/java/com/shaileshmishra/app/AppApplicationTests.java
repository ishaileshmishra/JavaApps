package com.shaileshmishra.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.shaileshmishra.app.employee.event.EmployeeEventProducer;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.user.repository.UserRepository;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration")
class AppApplicationTests {

	@MockitoBean
	private EmployeeRepository employeeRepository;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private EmployeeEventProducer employeeEventProducer;

	@Test
	void contextLoads() {
	}

}
