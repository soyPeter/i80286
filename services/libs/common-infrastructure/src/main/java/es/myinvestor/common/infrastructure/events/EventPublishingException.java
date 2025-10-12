package es.myinvestor.common.infrastructure.events;

/**
 * Exception thrown when there is an error publishing a domain event.
 * This is a runtime exception as event publishing failures are typically
 * unrecoverable in the current request context and should be handled by
 * the outbox pattern or retry mechanisms.
 */
public class EventPublishingException extends RuntimeException {

    /**
     * Creates a new EventPublishingException with the specified message.
     *
     * @param message the error message
     */
    public EventPublishingException(String message) {
        super(message);
    }

    /**
     * Creates a new EventPublishingException with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
