package com.shaileshmishra.app.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    /**
     * Configures a DefaultErrorHandler that:
     * 1. Retries a failed message up to 3 times with a 1s fixed backoff.
     * 2. After all retries are exhausted, routes the message to the Dead Letter Topic
     *    (suffix ".DLT") so it can be inspected and replayed without blocking the partition.
     *
     * <p>Without this, any deserialization failure or unhandled exception in a @KafkaListener
     * causes infinite retries that permanently stall the partition.
     */
    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @SuppressWarnings("rawtypes")
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate kafkaTemplate) {
        // Route failed messages to "<original-topic>.DLT", preserving the original partition
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception ex) -> {
                    log.error("Publishing to DLT after retries exhausted — topic: {}, partition: {}, offset: {}, error: {}",
                            record.topic(), record.partition(), record.offset(), ex.getMessage());
                    return new TopicPartition(record.topic() + ".DLT", record.partition());
                });

        // Retry 3 times, 1 second apart — then hand off to the recoverer
        FixedBackOff backOff = new FixedBackOff(1_000L, 3L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
