package com.logintest;

import java.sql.*;

/**
 * Database initialization utility
 * Creates the database schema and test data
 */
public class DatabaseInitializer {
    
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "21030-Cm-108";
    private static final String DB_NAME = "LoginData";
    
    public static void main(String[] args) {
        System.out.println("=== Database Initialization ===\n");
        
        try {
            // Load MySQL driver
            Class.forName(MYSQL_DRIVER);
            System.out.println("✓ MySQL JDBC Driver loaded");
            
            // Connect to MySQL server (without specifying a database)
            Connection conn = DriverManager.getConnection(MYSQL_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Connected to MySQL Server\n");
            
            Statement stmt = conn.createStatement();
            
            // Create database
            System.out.println("Creating database '" + DB_NAME + "'...");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✓ Database created/verified\n");
            
            // Use the database
            stmt.executeUpdate("USE " + DB_NAME);
            System.out.println("✓ Using database '" + DB_NAME + "'\n");
            
            // Create user table
            System.out.println("Creating user table...");
            String createTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
                    "email VARCHAR(255) PRIMARY KEY," +
                    "password VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createTableSQL);
            System.out.println("✓ User table created/verified\n");
            
            // Check if test user exists
            System.out.println("Checking for test user...");
            ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE email = 'test@example.com'");
            
            if (!rs.next()) {
                System.out.println("  Test user not found, creating...");
                stmt.executeUpdate("INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!')");
                System.out.println("✓ Test user created");
            } else {
                System.out.println("✓ Test user already exists");
            }
            
            System.out.println("\n=== Current Users ===");
            rs = stmt.executeQuery("SELECT * FROM user");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("email"));
            }
            
            // Cleanup
            stmt.close();
            conn.close();
            
            System.out.println("\n✓ Database initialization completed successfully!");
            System.exit(0);
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            System.err.println("  Error: " + e.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("✗ Database error!");
            System.err.println("  Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("  1. Ensure MySQL is running: brew services start mysql");
            System.err.println("  2. Verify credentials in DatabaseInitializer.java");
            System.err.println("  3. Check MySQL logs for detailed errors");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("✗ Unexpected error!");
            System.err.println("  Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
