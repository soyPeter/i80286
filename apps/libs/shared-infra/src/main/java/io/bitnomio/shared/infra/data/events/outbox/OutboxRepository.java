package io.bitnomio.shared.infra.data.events.outbox;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing the outbox table.
 * This is used to implement the transactional outbox pattern for reliable event publishing.
 */
public interface OutboxRepository {
    
    /**
     * Saves an outbox message to the database.
     *
     * @param message the message to save
     * @return the saved message
     */
    OutboxMessage save(OutboxMessage message);
    
    /**
     * Finds an outbox message by its ID.
     *
     * @param id the message ID
     * @return the message, or empty if not found
     */
    Optional<OutboxMessage> findById(String id);
    
    /**
     * Finds all unprocessed outbox messages.
     *
     * @param limit the maximum number of messages to return
     * @return the unprocessed messages
     */
    List<OutboxMessage> findUnprocessed(int limit);
    
    /**
     * Marks an outbox message as processed.
     *
     * @param id the message ID
     * @return true if the message was marked as processed, false if it was not found
     */
    boolean markAsProcessed(String id);
    
    /**
     * Deletes processed outbox messages that are older than the specified number of days.
     *
     * @param days the number of days
     * @return the number of messages deleted
     */
    int deleteProcessedOlderThan(int days);
}