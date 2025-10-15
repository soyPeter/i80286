/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * shared-domain - Created by pedro.almendro@MyInvestor
 * Date: 9/8/25 Time: 20:00
 *
 */
package io.bitnomio.shared.domain.exceptions;

/**
 * Exception thrown when a domain event cannot be published.
 * This is a domain exception that encapsulates event publishing failures.
 */
public class EventPublishingException extends RuntimeException {
    
    private final String eventType;
    private final Object event;

    public EventPublishingException(String message) {
        super(message);
        this.eventType = null;
        this.event = null;
    }

    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
        this.eventType = null;
        this.event = null;
    }

    public EventPublishingException(String message, String eventType, Object event) {
        super(message);
        this.eventType = eventType;
        this.event = event;
    }

    public EventPublishingException(String message, String eventType, Object event, Throwable cause) {
        super(message, cause);
        this.eventType = eventType;
        this.event = event;
    }

    public String getEventType() {
        return eventType;
    }

    public Object getEvent() {
        return event;
    }
}
