package io.bitnomio.shared.infra.io.messaging.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.myinvestor.shared.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

/**
 * Abstract base class for RabbitMQ-based domain event listeners.
 * This class handles the boilerplate of setting up queues, exchanges, and bindings,
 * as well as deserializing incoming messages to domain events.
 *
 * @param <T> the type of domain event this listener handles
 */
public abstract class RabbitMQDomainEventListener<T extends DomainEvent> implements DomainEventListener<T>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQDomainEventListener.class);

    private final ObjectMapper objectMapper;
    private final ConnectionFactory connectionFactory;
    private final String queueName;

    /**
     * Creates a new RabbitMQDomainEventListener.
     *
     * @param objectMapper the object mapper to use for deserializing events
     * @param connectionFactory the RabbitMQ connection factory
     */
    protected RabbitMQDomainEventListener(ObjectMapper objectMapper, ConnectionFactory connectionFactory) {
        this.objectMapper = objectMapper;
        this.connectionFactory = connectionFactory;
        this.queueName = generateQueueName();
    }

    /**
     * Generates a queue name for this listener.
     * The default implementation uses the simple name of the implementing class.
     *
     * @return the queue name
     */
    protected String generateQueueName() {
        return this.getClass().getSimpleName() + "-queue";
    }

    /**
     * Gets the queue name for this listener.
     *
     * @return the queue name
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Sets up the RabbitMQ infrastructure (exchange, queue, binding) when the bean is initialized.
     */
    @Override
    public void afterPropertiesSet() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);

        // Declare the exchange
        TopicExchange exchange = new TopicExchange(getExchange());
        admin.declareExchange(exchange);

        // Declare the queue
        Queue queue = new Queue(queueName, true);
        admin.declareQueue(queue);

        // Bind the queue to the exchange with the routing key pattern
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(getRoutingKeyPattern());
        admin.declareBinding(binding);

        logger.info("Set up RabbitMQ listener for {} on exchange {} with routing key {}",
                getEventClass().getSimpleName(), getExchange(), getRoutingKeyPattern());
    }

    /**
     * Handles incoming messages from RabbitMQ.
     * This method deserializes the message to a domain event and calls the handle method.
     *
     * @param message the message from RabbitMQ
     */
    @RabbitListener(queues = "#{@getQueueName}")
    public void receiveMessage(String message) {
        try {
            logger.debug("Received message: {}", message);

            // Deserialize the message to a domain event
            T event = objectMapper.readValue(message, getEventClass());

            // Handle the event
            handle(event);

            logger.debug("Successfully handled event {}", event.getEventId());
        } catch (IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to handle event: {}", e.getMessage(), e);
        }
    }
}
