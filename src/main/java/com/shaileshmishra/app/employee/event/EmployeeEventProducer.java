package com.shaileshmishra.app.employee.event;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEventProducer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventProducer.class);
    private static final String TOPIC = "employee-events";
    private static final long SEND_TIMEOUT_SECONDS = 5;

    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    public EmployeeEventProducer(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an EmployeeEvent to Kafka and blocks until the broker acknowledges
     * receipt (up to {@code SEND_TIMEOUT_SECONDS}). Throws on failure so callers
     * are aware the event was not delivered — preventing silent data-loss.
     *
     * @param event the event to publish
     * @throws RuntimeException if the send fails or times out
     */
    public void publishEvent(EmployeeEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.getEmpId(), event)
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("Published {} event for empId: {}", event.getEventType(), event.getEmpId());
        } catch (TimeoutException e) {
            log.error("Timed out publishing {} event for empId: {}", event.getEventType(), event.getEmpId(), e);
            throw new RuntimeException("Kafka send timed out for empId: " + event.getEmpId(), e);
        } catch (ExecutionException e) {
            log.error("Failed to publish {} event for empId: {}", event.getEventType(), event.getEmpId(), e.getCause());
            throw new RuntimeException("Kafka send failed for empId: " + event.getEmpId(), e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing {} event for empId: {}", event.getEventType(), event.getEmpId(), e);
            throw new RuntimeException("Kafka send interrupted for empId: " + event.getEmpId(), e);
        }
    }
}
