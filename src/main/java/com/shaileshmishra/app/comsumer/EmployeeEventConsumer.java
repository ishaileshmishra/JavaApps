package com.shaileshmishra.app.comsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.shaileshmishra.app.employee.event.EmployeeEvent;


@Service
public class EmployeeEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmployeeEventConsumer.class);
    @KafkaListener(topics = "employee-events", groupId = "employee-service-group")
    public void handleEvent(EmployeeEvent event) {
        switch (event.getEventType()) {
            case "CREATED" -> log.info("📥 New employee created: {} ({})",
                    event.getName(), event.getEmpId());
            case "DELETED" -> log.info("🗑️ Employee deleted: {} ({})",
                    event.getName(), event.getEmpId());
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
        // Here you'd call another service, send notifications,
        // update a search index, trigger an audit log, etc.
    }
}
