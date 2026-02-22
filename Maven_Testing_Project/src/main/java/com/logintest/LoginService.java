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
        
        // Validate input format and security (pass original values to detect leading/trailing spaces)
        String emailValidation = validateEmailFormat(email);
        if (emailValidation != null) {
            return new LoginResult(false, emailValidation);
        }
        
        String passwordValidation = validatePasswordFormat(password);
        if (passwordValidation != null) {
            return new LoginResult(false, passwordValidation);
        }
        
        // Check credentials against database (use trimmed values for lookup)
        boolean isValid = dbHelper.validateCredentials(email.trim(), password.trim());
        
        if (isValid) {
            return new LoginResult(true, "Login Successful");
        } else {
            return new LoginResult(false, "Invalid credentials");
        }
    }
    
    /**
     * Validates email format and detects malicious patterns
     * @param email - Email to validate
     * @return Error message if invalid, null if valid
     */
    private String validateEmailFormat(String email) {
        // Check for leading/trailing spaces (original value)
        if (!email.equals(email.trim())) {
            return "Invalid credentials";
        }
        
        String trimmedEmail = email.trim();
        
        // Block spaces-only input
        if (trimmedEmail.replaceAll("\\s+", "").isEmpty()) {
            return "Invalid credentials";
        }
        
        // SQL Injection patterns
        if (containsSQLInjectionPatterns(trimmedEmail)) {
            return "Invalid credentials";
        }
        
        // XSS Injection patterns
        if (containsXSSPatterns(trimmedEmail)) {
            return "Invalid credentials";
        }
        
        // HTML Injection patterns
        if (containsHTMLPatterns(trimmedEmail)) {
            return "Invalid credentials";
        }
        
        // Unicode characters (allow standard ASCII emails)
        if (!trimmedEmail.matches("[\\x00-\\x7F]+")) {
            return "Invalid credentials";
        }
        
        // Basic email format
        if (!trimmedEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            return "Invalid credentials";
        }
        
        return null;
    }
    
    /**
     * Validates password format and detects malicious patterns
     * @param password - Password to validate
     * @return Error message if invalid, null if valid
     */
    private String validatePasswordFormat(String password) {
        // Check for leading/trailing spaces (original value)
        if (!password.equals(password.trim())) {
            return "Invalid credentials";
        }
        
        String trimmedPassword = password.trim();
        
        // Block spaces-only input
        if (trimmedPassword.replaceAll("\\s+", "").isEmpty()) {
            return "Invalid credentials";
        }
        
        // SQL Injection patterns
        if (containsSQLInjectionPatterns(trimmedPassword)) {
            return "Invalid credentials";
        }
        
        // XSS Injection patterns
        if (containsXSSPatterns(trimmedPassword)) {
            return "Invalid credentials";
        }
        
        // HTML Injection patterns
        if (containsHTMLPatterns(trimmedPassword)) {
            return "Invalid credentials";
        }
        
        return null;
    }
    
    /**
     * Detects SQL injection patterns
     */
    private boolean containsSQLInjectionPatterns(String input) {
        String lowerInput = input.toLowerCase();
        String[] sqlPatterns = {
            "' or '1'='1",
            "\" or \"1\"=\"1",
            "'; drop",
            "' union",
            "\" union",
            "'; delete",
            "' insert",
            "' update",
            "' select",
            "-- ", 
            "/*", 
            "*/"
        };
        
        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Detects XSS patterns
     */
    private boolean containsXSSPatterns(String input) {
        String lowerInput = input.toLowerCase();
        String[] xssPatterns = {
            "<script",
            "</script>",
            "javascript:",
            "onerror=",
            "onload=",
            "onclick=",
            "onmouseover=",
            "eval(",
            "alert(",
            "prompt(",
            "confirm("
        };
        
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Detects HTML injection patterns
     */
    private boolean containsHTMLPatterns(String input) {
        String lowerInput = input.toLowerCase();
        String[] htmlPatterns = {
            "<b>",
            "</b>",
            "<i>",
            "</i>",
            "<p>",
            "</p>",
            "<div>",
            "</div>",
            "<span>",
            "</span>",
            "<font>",
            "</font>",
            "<strong>",
            "</strong>"
        };
        
        for (String pattern : htmlPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }
        
        return false;
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
