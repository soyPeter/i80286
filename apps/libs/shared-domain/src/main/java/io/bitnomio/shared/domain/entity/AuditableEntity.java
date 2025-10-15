package io.bitnomio.shared.domain.entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Base class for all entities that require auditing information.
 * This class provides common auditing fields like creation and modification timestamps.
 * All domain entities across bounded contexts should extend this class to ensure
 * consistent auditing capabilities.
 */
public abstract class AuditableEntity {
    private final Instant createdAt;
    private Instant updatedAt;

    protected AuditableEntity() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    protected AuditableEntity(Instant createdAt, Instant updatedAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /**
     * Gets the timestamp when this entity was created.
     *
     * @return the creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the timestamp when this entity was last updated.
     *
     * @return the last update timestamp
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the last modification timestamp to the current time.
     */
    protected void markAsModified() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditableEntity that = (AuditableEntity) o;
        return Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt);
    }
}