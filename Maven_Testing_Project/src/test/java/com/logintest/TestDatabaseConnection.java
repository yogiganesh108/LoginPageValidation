package com.logintest;

import java.sql.SQLException;

/**
 * Quick test utility to verify database connection
 * Run this to ensure your database configuration is correct before starting the full application
 */
public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Testing Database Connection");
        System.out.println("========================================\n");
        
        DatabaseHelper dbHelper = new DatabaseHelper();
        
        try {
            // Test connection
            System.out.println("Attempting to connect to database...");
            dbHelper.connect();
            System.out.println("✓ Connection successful!\n");
            
            // Test if user table exists and has data
            System.out.println("Checking if test user exists...");
            boolean userExists = dbHelper.userExists("test@example.com");
            
            if (userExists) {
                System.out.println("✓ Test user found: test@example.com\n");
                
                // Test credential validation
                System.out.println("Testing credential validation...");
                boolean validCredentials = dbHelper.validateCredentials("test@example.com", "Password123!");
                
                if (validCredentials) {
                    System.out.println("✓ Credentials validated successfully!\n");
                } else {
                    System.out.println("✗ Credential validation failed!");
                    System.out.println("  Expected password: Password123!");
                    System.out.println("  Please check the password in your database.\n");
                }
            } else {
                System.out.println("✗ Test user not found!");
                System.out.println("  Run the following SQL to insert test user:");
                System.out.println("  INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!');\n");
            }
            
            System.out.println("========================================");
            System.out.println("Database Configuration Test Complete");
            System.out.println("========================================");
            
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("\nError Details:");
            System.err.println("  Message: " + e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  1. MySQL is running");
            System.err.println("  2. Database schema exists");
            System.err.println("  3. Credentials in DatabaseHelper.java are correct");
            System.err.println("     - DB_SCHEMA");
            System.err.println("     - DB_USER");
            System.err.println("     - DB_PASSWORD");
        } finally {
            dbHelper.disconnect();
        }
    }
}
