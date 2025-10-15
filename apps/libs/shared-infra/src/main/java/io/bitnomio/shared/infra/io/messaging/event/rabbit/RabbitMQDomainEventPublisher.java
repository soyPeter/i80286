package io.bitnomio.shared.infra.io.messaging.event.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bitnomio.shared.infra.io.messaging.event.DomainEvent;
import io.bitnomio.shared.infra.io.messaging.event.DomainEventPublisher;
import io.bitnomio.shared.infra.io.messaging.event.EventPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ implementation of the DomainEventPublisher interface.
 * This class is responsible for publishing domain events to RabbitMQ exchanges.
 */
@Component
public class RabbitMQDomainEventPublisher implements DomainEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQDomainEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String defaultExchange;

    /**
     * Creates a new RabbitMQDomainEventPublisher.
     *
     * @param rabbitTemplate the RabbitMQ template to use for publishing
     * @param objectMapper the object mapper to use for serializing events
     * @param defaultExchange the default exchange to publish to if not specified
     */
    public RabbitMQDomainEventPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            String defaultExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.defaultExchange = defaultExchange;
    }

    @Override
    public void publish(DomainEvent event) {
        // Derive routing key from event type
        String routingKey = deriveRoutingKey(event);
        publish(event, defaultExchange, routingKey);
    }

    @Override
    public void publish(DomainEvent event, String exchange, String routingKey) {
        try {
            logger.debug("Publishing event {} to exchange {} with routing key {}",
                    event.getEventType(), exchange, routingKey);

            // Serialize the event to JSON
            String eventJson = objectMapper.writeValueAsString(event);

            // Publish to RabbitMQ
            rabbitTemplate.convertAndSend(exchange, routingKey, eventJson);

            logger.debug("Successfully published event {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish event {}: {}", event.getEventId(), e.getMessage(), e);
            throw new EventPublishingException("Failed to publish event: " + e.getMessage(), e);
        }
    }

    /**
     * Derives a routing key from the event type.
     * For example, "CustomerCreated" becomes "customer.created".
     *
     * @param event the domain event
     * @return the derived routing key
     */
    private String deriveRoutingKey(DomainEvent event) {
        String eventType = event.getEventType();

        // Convert camel case to dot notation
        // e.g., "CustomerCreated" -> "customer.created"
        StringBuilder routingKey = new StringBuilder();
        for (int i = 0; i < eventType.length(); i++) {
            char c = eventType.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                routingKey.append('.');
                routingKey.append(Character.toLowerCase(c));
            } else {
                routingKey.append(Character.toLowerCase(c));
            }
        }

        return routingKey.toString();
    }
}
