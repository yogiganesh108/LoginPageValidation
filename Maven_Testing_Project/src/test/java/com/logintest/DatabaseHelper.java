package com.logintest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/"; 
    private static final String DB_SCHEMA = "LoginData"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "21030-Cm-108"; 
    
    private Connection connection;
    
    
    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL + DB_SCHEMA, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Validates user credentials against the database
     * @param email - User's email (primary key)
     * @param password - User's password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateCredentials(String email, String password) {
        String query = "SELECT password FROM user WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    // In production, use hashed password comparison
                    return password.equals(dbPassword);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Checks if a user exists in the database
     * @param email - User's email
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String email) {
        String query = "SELECT email FROM user WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Adds a new user to the database (for testing purposes)
     * @param email - User's email
     * @param password - User's password
     * @return true if user was added successfully, false otherwise
     */
    public boolean addUser(String email, String password) {
        String query = "INSERT INTO user (email, password) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Removes a user from the database (for cleanup after testing)
     * @param email - User's email
     * @return true if user was removed successfully, false otherwise
     */
    public boolean removeUser(String email) {
        String query = "DELETE FROM user WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Gets the connection object (for advanced operations)
     * @return Connection object
     */
    public Connection getConnection() {
        return connection;
    }
}
