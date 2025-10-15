-- V0__init_test_db.sql
CREATE TABLE IF NOT EXISTS dummy_table
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255)             NOT NULL DEFAULT 'TEST_SYSTEM',
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(255)             NOT NULL DEFAULT 'TEST_SYSTEM',
    deleted    BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);
