package io.bitnomio.shared.infra.io.messaging.event.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bitnomio.shared.infra.io.messaging.event.DomainEvent;
import io.bitnomio.shared.infra.io.messaging.event.EventPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for processing outbox messages and publishing them to the message broker.
 * This implements the transactional outbox pattern for reliable event publishing.
 */
@Service
public class OutboxService {
    private static final Logger logger = LoggerFactory.getLogger(OutboxService.class);

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new OutboxService.
     *
     * @param outboxRepository the outbox repository
     * @param rabbitTemplate the RabbitMQ template
     * @param objectMapper the object mapper
     */
    public OutboxService(OutboxRepository outboxRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Stores a domain event in the outbox table.
     * This should be called as part of the same transaction that updates the domain model.
     *
     * @param event the domain event to store
     * @param exchangeName the exchange to publish to
     * @param routingKey the routing key to use
     */
    @Transactional
    public void storeEvent(DomainEvent event, String exchangeName, String routingKey) {
        try {
            logger.debug("Storing event {} in outbox", event.getEventId());

            // Serialize the event to JSON
            String payload = objectMapper.writeValueAsString(event);

            // Create an outbox message
            OutboxMessage message = OutboxMessage.create(
                    event.getAggregateId(),
                    event.getClass().getSimpleName().replace("Event", ""),
                    event.getEventType(),
                    payload,
                    event.getOccurredOn(),
                    exchangeName,
                    routingKey
            );

            // Save the message to the outbox
            outboxRepository.save(message);

            logger.debug("Successfully stored event {} in outbox", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to store event {} in outbox: {}", event.getEventId(), e.getMessage(), e);
            throw new EventPublishingException("Failed to store event in outbox: " + e.getMessage(), e);
        }
    }

    /**
     * Processes unprocessed outbox messages and publishes them to the message broker.
     * This is scheduled to run periodically.
     */
    @Scheduled(fixedDelayString = "${outbox.processing.interval:5000}")
    @Transactional
    public void processOutbox() {
        try {
            logger.debug("Processing outbox messages");

            // Find unprocessed messages
            List<OutboxMessage> messages = outboxRepository.findUnprocessed(100);

            if (messages.isEmpty()) {
                logger.debug("No unprocessed outbox messages found");
                return;
            }

            logger.debug("Found {} unprocessed outbox messages", messages.size());

            // Process each message
            for (OutboxMessage message : messages) {
                try {
                    // Publish the message to RabbitMQ
                    rabbitTemplate.convertAndSend(
                            message.getExchangeName(),
                            message.getRoutingKey(),
                            message.getPayload()
                    );

                    // Mark the message as processed
                    outboxRepository.markAsProcessed(message.getId());

                    logger.debug("Successfully processed outbox message {}", message.getId());
                } catch (Exception e) {
                    logger.error("Failed to process outbox message {}: {}", message.getId(), e.getMessage(), e);
                    // Don't rethrow, continue with the next message
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process outbox: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleans up old processed outbox messages.
     * This is scheduled to run daily.
     */
    @Scheduled(cron = "${outbox.cleanup.cron:0 0 0 * * ?}")
    @Transactional
    public void cleanupOutbox() {
        try {
            logger.debug("Cleaning up old processed outbox messages");

            // Delete processed messages older than 7 days
            int deleted = outboxRepository.deleteProcessedOlderThan(7);

            logger.debug("Deleted {} old processed outbox messages", deleted);
        } catch (Exception e) {
            logger.error("Failed to clean up outbox: {}", e.getMessage(), e);
        }
    }
}
