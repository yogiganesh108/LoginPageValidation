package com.logintest;

import java.sql.SQLException;

public class LoginService {
    
    private DatabaseHelper dbHelper;
    
    public LoginService() {
        this.dbHelper = new DatabaseHelper();
    }
    
    /**
     * Initializes the database connection
     * @throws SQLException if connection fails
     */
    public void initialize() throws SQLException {
        dbHelper.connect();
    }
    
    /**
     * Closes the database connection
     */
    public void cleanup() {
        dbHelper.disconnect();
    }
    
    /**
     * Validates user login credentials
     * @param email - User's email
     * @param password - User's password
     * @return LoginResult object containing success status and message
     */
    public LoginResult validateLogin(String email, String password) {
        // Input validation
        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password is required");
        }
        
        // Check credentials against database
        boolean isValid = dbHelper.validateCredentials(email, password);
        
        if (isValid) {
            return new LoginResult(true, "Login Successful");
        } else {
            return new LoginResult(false, "Invalid credentials");
        }
    }
    
    /**
     * Checks if a user exists in the system
     * @param email - User's email
     * @return true if user exists, false otherwise
     */
    public boolean doesUserExist(String email) {
        return dbHelper.userExists(email);
    }
    
    /**
     * Registers a new user (for testing purposes)
     * @param email - User's email
     * @param password - User's password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String email, String password) {
        // Check if user already exists
        if (dbHelper.userExists(email)) {
            return false;
        }
        
        return dbHelper.addUser(email, password);
    }
    
    /**
     * Removes a user from the system (for test cleanup)
     * @param email - User's email
     * @return true if removal successful, false otherwise
     */
    public boolean removeUser(String email) {
        return dbHelper.removeUser(email);
    }
    
    /**
     * Inner class to represent login result
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        
        public LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
