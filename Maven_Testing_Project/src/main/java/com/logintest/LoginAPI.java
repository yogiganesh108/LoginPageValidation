package com.logintest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class LoginAPI {
    
    private static LoginService loginService;
    private static Gson gson = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();
    
    public static void main(String[] args) {
        // Initialize login service
        loginService = new LoginService();
        try {
            loginService.initialize();
            System.out.println("Login API started successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.exit(1);
        }
        
        // Configure port
        port(8080);
        
        // Enable CORS for React frontend
        enableCORS();
        
        // API Routes
        
        // Health check
        get("/api/health", (req, res) -> {
            res.type("application/json");
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("message", "API is running");
            return gson.toJson(response);
        });
        
        // Login endpoint
        post("/api/login", LoginAPI::handleLogin);
        
        // Register endpoint (for testing)
        post("/api/register", LoginAPI::handleRegister);
        
        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            loginService.cleanup();
            stop();
        }));
    }
    
    /**
     * Handles login requests
     */
    private static String handleLogin(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Parse request body
            Map<String, String> requestData = gson.fromJson(req.body(), MAP_TYPE);
            String email = requestData.get("email");
            String password = requestData.get("password");
            
            // Validate credentials
            LoginService.LoginResult result = loginService.validateLogin(email, password);
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            
            if (result.isSuccess()) {
                res.status(200);
            } else {
                res.status(401);
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            res.status(500);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
    
    /**
     * Handles registration requests (for testing purposes)
     */
    private static String handleRegister(Request req, Response res) {
        res.type("application/json");
        
        try {
            // Parse request body
            Map<String, String> requestData = gson.fromJson(req.body(), MAP_TYPE);
            String email = requestData.get("email");
            String password = requestData.get("password");
            
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                res.status(400);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email is required");
                return gson.toJson(errorResponse);
            }
            
            if (password == null || password.trim().isEmpty()) {
                res.status(400);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Password is required");
                return gson.toJson(errorResponse);
            }
            
            // Check if user already exists
            if (loginService.doesUserExist(email)) {
                res.status(409);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User already exists");
                return gson.toJson(errorResponse);
            }
            
            // Register user
            boolean registered = loginService.registerUser(email, password);
            
            Map<String, Object> response = new HashMap<>();
            if (registered) {
                res.status(201);
                response.put("success", true);
                response.put("message", "Registration Successful");
            } else {
                res.status(500);
                response.put("success", false);
                response.put("message", "Failed to register user");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            res.status(500);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
    
    /**
     * Enables CORS for cross-origin requests
     */
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            
            return "OK";
        });
        
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        });
    }
}
