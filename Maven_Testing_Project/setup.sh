#!/bin/bash

# Quick Start Script for Login System with MySQL Database
# This script helps you set up and run the complete system

echo "=========================================="
echo "Login System - Database Setup & Start"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if MySQL is running
echo "Checking MySQL status..."
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}MySQL is not installed. Please install MySQL first.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ MySQL found${NC}"
echo ""

# Prompt for database credentials
echo "Please provide your MySQL database information:"
read -p "Enter MySQL username (default: root): " db_user
db_user=${db_user:-root}

read -sp "Enter MySQL password: " db_password
echo ""

read -p "Enter database schema name (default: skillpalaver): " db_schema
db_schema=${db_schema:-skillpalaver}

echo ""
echo "Using configuration:"
echo "  Username: $db_user"
echo "  Schema: $db_schema"
echo ""

# Create database and table
echo "Setting up database..."
mysql -u "$db_user" -p"$db_password" << EOF
CREATE DATABASE IF NOT EXISTS $db_schema;
USE $db_schema;

CREATE TABLE IF NOT EXISTS user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!')
ON DUPLICATE KEY UPDATE password = 'Password123!';

SELECT 'Database setup completed!' as status;
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database created successfully${NC}"
else
    echo -e "${RED}✗ Database setup failed${NC}"
    exit 1
fi

echo ""
echo "=========================================="
echo "Next Steps:"
echo "=========================================="
echo ""
echo "1. Update DatabaseHelper.java with your credentials:"
echo "   - DB_SCHEMA = \"$db_schema\""
echo "   - DB_USER = \"$db_user\""
echo "   - DB_PASSWORD = \"****\""
echo ""
echo "2. Start the Backend API:"
echo "   cd Maven_Testing_Project"
echo "   mvn exec:java -Dexec.mainClass=\"com.logintest.LoginAPI\""
echo ""
echo "3. In a new terminal, start the React frontend:"
echo "   cd Frontend_React_App"
echo "   npm start"
echo ""
echo "4. Run tests in another terminal:"
echo "   cd Maven_Testing_Project"
echo "   mvn test"
echo ""
echo -e "${GREEN}Setup complete! Follow the steps above to start the application.${NC}"
