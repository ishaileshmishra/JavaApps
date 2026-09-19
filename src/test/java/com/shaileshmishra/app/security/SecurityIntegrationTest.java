package com.shaileshmishra.app.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shaileshmishra.app.employee.event.EmployeeEventProducer;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.employee.service.EmployeeService;
import com.shaileshmishra.app.user.repository.UserRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
    "spring.kafka.listener.auto-startup=false"
})
@AutoConfigureMockMvc
@DisplayName("SecurityIntegrationTest")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private EmployeeEventProducer employeeEventProducer;

    @Test
    @DisplayName("should return 401 Unauthorized when accessing protected /employees without JWT token")
    void shouldReturn401_whenAccessingEmployeesWithoutToken() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value(401))
                .andExpect(jsonPath("$.error_message").value("Unauthorized access. Valid JWT token is required."));
    }

    @Test
    @DisplayName("should allow public access to /auth/register")
    void shouldAllowPublicAccessToRegister() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to missing fields, NOT 401 Unauthorized
    }

    @Test
    @DisplayName("should allow public access to /auth/login")
    void shouldAllowPublicAccessToLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to missing fields, NOT 401 Unauthorized
    }
}
