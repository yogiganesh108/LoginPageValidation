-- Create database for login testing
CREATE DATABASE IF NOT EXISTS LoginData;
USE LoginData;

-- Create user table
CREATE TABLE IF NOT EXISTS user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert test user
INSERT IGNORE INTO user (email, password) VALUES ('test@example.com', 'Password123!');

-- Display confirmation
SELECT 'Database setup complete!' AS status;
SELECT COUNT(*) as user_count FROM user;
SELECT * FROM user;
