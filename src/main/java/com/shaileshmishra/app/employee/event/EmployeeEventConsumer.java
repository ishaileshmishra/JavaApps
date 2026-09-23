package com.shaileshmishra.app.employee.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@Service
public class EmployeeEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventConsumer.class);

    /**
     * Main consumer. Errors are handled by the {@code DefaultErrorHandler} bean
     * in KafkaConfig — retries 3×, then routes to employee-events.DLT.
     */
    @KafkaListener(topics = "employee-events", groupId = "employee-service-group")
    public void handleEvent(EmployeeEvent event) {
        switch (event.getEventType()) {
            case "CREATED" -> log.info("📥 Consumer New employee created: {} ({})",
                    event.getName(), event.getEmpId());
            case "DELETED" -> log.info("🗑️ Consumer Employee deleted: {} ({})",
                    event.getName(), event.getEmpId());
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
        // Here you'd call another service, send notifications,
        // update a search index, trigger an audit log, etc.
    }

    /**
     * Dead Letter Topic listener — receives messages that failed all retries.
     * Logs full context so ops can inspect and decide whether to replay or discard.
     */
    @KafkaListener(topics = "employee-events.DLT", groupId = "employee-service-dlt-group")
    public void handleDltEvent(
            @Payload(required = false) byte[] payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(name = "kafka_dlt-exception-message", required = false) String exceptionMessage) {
        log.error("⚠️  DLT message received — topic: {}, partition: {}, offset: {}, error: {}",
                topic, partition, offset, exceptionMessage);
        // Extend here: persist to a failure store, trigger an alert, or push to a replay queue
    }
}
