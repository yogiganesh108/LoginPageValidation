-- MySQL Database Setup Script
-- This script creates the schema and table for the login system

-- Step 1: Create database schema (if not exists)
CREATE DATABASE IF NOT EXISTS your_schema_name;

-- Step 2: Use the schema
USE your_schema_name;

-- Step 3: Create user table
CREATE TABLE IF NOT EXISTS user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Step 4: Insert sample test data
INSERT INTO user (email, password) VALUES 
    ('test@example.com', 'Password123!')
ON DUPLICATE KEY UPDATE password = 'Password123!';

-- Optional: Insert additional test users
INSERT INTO user (email, password) VALUES 
    ('admin@example.com', 'Admin123!'),
    ('user@example.com', 'User123!')
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- Verify data
SELECT * FROM user;

-- Note: In production, passwords should be hashed using bcrypt or similar algorithm
-- This is plain text for testing purposes only
