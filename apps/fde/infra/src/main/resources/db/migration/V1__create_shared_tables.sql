-- V1__create_shared_tables.sql
-- Initial schema for shared database used across bounded contexts

-- Customers table - shared reference across all bounded contexts
CREATE TABLE IF NOT EXISTS customers
(
    id            VARCHAR(36) PRIMARY KEY,
    first_name    VARCHAR(100)             NOT NULL,
    last_name     VARCHAR(100)             NOT NULL,
    email         VARCHAR(255)             NOT NULL UNIQUE,
    phone_number  VARCHAR(50),
    date_of_birth DATE,
    national_id   VARCHAR(50),
    status        VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255)             NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by    VARCHAR(255)             NOT NULL DEFAULT 'SYSTEM',
    deleted       BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at    TIMESTAMP WITH TIME ZONE,
    deleted_by    VARCHAR(255)
);

-- Accounts table - shared reference across all bounded contexts
CREATE TABLE IF NOT EXISTS accounts
(
    id             VARCHAR(36) PRIMARY KEY,
    customer_id    VARCHAR(36)              NOT NULL REFERENCES customers (id),
    account_number VARCHAR(50)              NOT NULL UNIQUE,
    type           VARCHAR(20)              NOT NULL,
    status         VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    balance        DECIMAL(19, 2)           NOT NULL DEFAULT 0.00,
    currency       VARCHAR(3)               NOT NULL DEFAULT 'EUR',
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255)             NOT NULL DEFAULT 'SYSTEM',
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by     VARCHAR(255)             NOT NULL DEFAULT 'SYSTEM',
    deleted        BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMP WITH TIME ZONE,
    deleted_by     VARCHAR(255)
);

-- Domain events table - for event sourcing and cross-context communication
CREATE TABLE IF NOT EXISTS domain_events
(
    id            VARCHAR(36) PRIMARY KEY,
    aggregate_id  VARCHAR(36)              NOT NULL,
    event_type    VARCHAR(255)             NOT NULL,
    event_data    JSONB                    NOT NULL,
    occurred_on   TIMESTAMP WITH TIME ZONE NOT NULL,
    published     BOOLEAN                  NOT NULL DEFAULT FALSE,
    published_at  TIMESTAMP WITH TIME ZONE,
    exchange_name VARCHAR(100)             NOT NULL,
    routing_key   VARCHAR(255)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create index on domain_events for efficient querying
CREATE INDEX IF NOT EXISTS idx_domain_events_aggregate_id ON domain_events (aggregate_id);
CREATE INDEX IF NOT EXISTS idx_domain_events_event_type ON domain_events (event_type);
CREATE INDEX IF NOT EXISTS idx_domain_events_published ON domain_events (published);
CREATE INDEX IF NOT EXISTS idx_domain_events_occurred_on ON domain_events (occurred_on);

-- Outbox table - for reliable message publishing (transactional outbox pattern)
CREATE TABLE IF NOT EXISTS outbox_messages
(
    id            VARCHAR(36) PRIMARY KEY,
    aggregate_id  VARCHAR(36)              NOT NULL,
    aggregate_type VARCHAR(255)            NOT NULL,
    event_type    VARCHAR(255)             NOT NULL,
    payload       JSONB                    NOT NULL,
    occurred_on   TIMESTAMP WITH TIME ZONE NOT NULL,
    processed     BOOLEAN                  NOT NULL DEFAULT FALSE,
    processed_at  TIMESTAMP WITH TIME ZONE,
    exchange_name VARCHAR(100)             NOT NULL,
    routing_key   VARCHAR(255)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create index on outbox_messages for efficient processing
CREATE INDEX IF NOT EXISTS idx_outbox_messages_processed ON outbox_messages (processed);
CREATE INDEX IF NOT EXISTS idx_outbox_messages_aggregate_id ON outbox_messages (aggregate_id);
CREATE INDEX IF NOT EXISTS idx_outbox_messages_event_type ON outbox_messages (event_type);