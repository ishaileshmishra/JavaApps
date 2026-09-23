package com.shaileshmishra.app.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shaileshmishra.app.employee.event.EmployeeEventProducer;
import com.shaileshmishra.app.employee.repository.EmployeeRepository;
import com.shaileshmishra.app.user.repository.UserRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,org.springframework.boot.mongodb.autoconfigure.health.MongoHealthContributorAutoConfiguration,org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
    "spring.kafka.listener.auto-startup=false",
    "management.health.mongodb.enabled=false"
})
@AutoConfigureMockMvc
@DisplayName("HealthEndpointIntegrationTest")
class HealthEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private EmployeeEventProducer employeeEventProducer;

    @Test
    @DisplayName("should return UP and application details on /actuator/health without authentication")
    void shouldReturnHealthStatus_onActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.application.status").value("UP"))
                .andExpect(jsonPath("$.components.application.details.application").value("JavaApps"))
                .andExpect(jsonPath("$.components.application.details.status").value("OPERATIONAL"));
    }

    @Test
    @DisplayName("should return 200 OK on GET /health without authentication")
    void shouldReturnHealthStatus_onCustomHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("JavaApps"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
