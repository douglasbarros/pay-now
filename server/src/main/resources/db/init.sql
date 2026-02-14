-- EzyCollect Database Initialization Script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS ezycollect;
USE ezycollect;

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id CHAR(36) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    encrypted_card_number VARCHAR(500) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create webhooks table
CREATE TABLE IF NOT EXISTS webhooks (
    id CHAR(36) PRIMARY KEY,
    endpoint_url VARCHAR(500) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_active (active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data for testing (optional)
-- INSERT INTO webhooks (id, endpoint_url, active, created_at, updated_at) 
-- VALUES (UUID(), 'https://webhook.site/your-unique-url', TRUE, NOW(), NOW());
