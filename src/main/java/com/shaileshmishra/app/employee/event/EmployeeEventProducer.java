package com.shaileshmishra.app.employee.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEventProducer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventProducer.class);
    private static final String TOPIC = "employee-events";
    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    
    public EmployeeEventProducer(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreateEvent(EmployeeEvent event) {
        kafkaTemplate.send(TOPIC, event.getEmpId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", event.getEmpId(), ex);
                    } else {
                        log.info("Published {} createevent for empId: {}", 
                                event.getEventType(), event.getEmpId());
                    }
                });
    }


      public void publishDeleteEvent(EmployeeEvent event) {
        kafkaTemplate.send(TOPIC, event.getEmpId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", event.getEmpId(), ex);
                    } else {
                        log.info("Published {} delete event for empId: {}", 
                                event.getEventType(), event.getEmpId());
                    }
                });
    }
}
