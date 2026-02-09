package com.logintest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static LoginService loginService;
    
    // URL where the React App is running locally
    private final String BASE_URL = "http://localhost:3000";
    
    // Test credentials - these should exist in the database
    private final String VALID_EMAIL = "test@example.com";
    private final String VALID_PASSWORD = "Password123!";
    private final String INVALID_EMAIL = "wrong@user.com";
    private final String INVALID_PASSWORD = "WrongPass123";

    @BeforeClass
    public static void setupDatabase() throws SQLException {
        // Initialize database connection
        loginService = new LoginService();
        loginService.initialize();
        
        // Setup test data in database
        // Remove if exists, then add fresh test user
        loginService.removeUser("test@example.com");
        loginService.registerUser("test@example.com", "Password123!");
        
        System.out.println("Test database setup completed");
    }
    
    @AfterClass
    public static void cleanupDatabase() {
        // Optional: Clean up test data
        // loginService.removeUser("test@example.com");
        
        // Close database connection
        if (loginService != null) {
            loginService.cleanup();
        }
        
        System.out.println("Test database cleanup completed");
    }

    @BeforeMethod
    public void setup() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless"); // Uncomment to run without UI
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- FUNCTIONAL TESTING ---

    @Test(priority = 1, description = "Test login with valid credentials")
    public void testValidLogin() {
        WebElement emailInput = driver.findElement(By.cssSelector("[data-testid='email-input']"));
        WebElement passInput = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        WebElement submitBtn = driver.findElement(By.cssSelector("[data-testid='submit-button']"));

        // Valid credentials from database
        emailInput.sendKeys(VALID_EMAIL);
        passInput.sendKeys(VALID_PASSWORD);
        submitBtn.click();

        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        Assert.assertTrue(statusMsg.getText().contains("Login Successful"), "Success message mismatch!");
    }

    @Test(priority = 2, description = "Test login with invalid credentials")
    public void testInvalidLogin() {
        driver.findElement(By.cssSelector("[data-testid='email-input']")).sendKeys(INVALID_EMAIL);
        driver.findElement(By.cssSelector("[data-testid='password-input']")).sendKeys(INVALID_PASSWORD);
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();

        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        Assert.assertTrue(statusMsg.getText().contains("Invalid credentials"), "Error message for invalid login mismatch!");
    }

    @Test(priority = 3, description = "Test empty fields validation")
    public void testEmptyFields() {
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();

        // Wait for error message to appear (backend validation)
        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        
        // Backend returns validation error for empty fields
        Assert.assertTrue(statusMsg.getText().contains("Please fix validation errors") || 
                         statusMsg.getText().contains("Email is required") ||
                         statusMsg.getText().contains("required"), 
                         "Empty fields validation message not found");
    }

    // --- BOUNDARY TESTING ---

    @Test(priority = 4, description = "Test field behavior with extremely long input")
    public void testBoundaryLongInput() {
        // Create a string with 51 chars (Limit is 50 in App.jsx)
        String longEmail = "a".repeat(51) + "@test.com";
        
        WebElement emailInput = driver.findElement(By.cssSelector("[data-testid='email-input']"));
        emailInput.sendKeys(longEmail);

        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='email-error']")));
        Assert.assertTrue(emailError.getText().contains("maximum length"), "Boundary check for max length failed");
    }

    @Test(priority = 5, description = "Test password minimum length boundary")
    public void testBoundaryMinPassword() {
        driver.findElement(By.cssSelector("[data-testid='password-input']")).sendKeys("12345"); // 5 chars
        
        WebElement passError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='password-error']")));
        Assert.assertTrue(passError.getText().contains("at least 6 characters"), "Min length boundary check failed");
    }

    // --- SECURITY TESTING (BONUS) ---

    @Test(priority = 6, description = "Test SQL Injection protection via PreparedStatements")
    public void testSQLInjectionAttempt() {
        WebElement emailInput = driver.findElement(By.cssSelector("[data-testid='email-input']"));
        WebElement passInput = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        WebElement submitBtn = driver.findElement(By.cssSelector("[data-testid='submit-button']"));
        
        // Common SQLi pattern - should be safely handled by PreparedStatement
        String sqlAttack = "' OR '1'='1"; 
        emailInput.sendKeys(sqlAttack);
        passInput.sendKeys("password");
        submitBtn.click();

        // Backend safely handles SQL injection attempts via PreparedStatements
        // The system should either:
        // 1. Show "Invalid credentials" (SQL injection failed - good!)
        // 2. Show validation error for invalid email format
        // Both outcomes prove SQL injection is prevented
        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        String messageText = statusMsg.getText();
        
        // Test passes if we get any error message, proving SQL injection didn't succeed
        Assert.assertTrue(messageText.contains("Invalid") || 
                         messageText.contains("credentials") ||
                         messageText.contains("error") ||
                         messageText.contains("validation") ||
                         messageText.contains("fix"),
            "SQL injection test failed. Got message: " + messageText);
    }

    @Test(priority = 7, description = "Test Password Strength Validation (Registration Mode)")
    public void testPasswordStrength() {
        // Switch to Register mode
        driver.findElement(By.cssSelector("[data-testid='toggle-auth-mode']")).click();
        
        WebElement passInput = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        
        // Weak Password
        passInput.sendKeys("weak");
        WebElement strengthMeter = driver.findElement(By.cssSelector("[data-testid='password-strength'] p"));
        Assert.assertEquals(strengthMeter.getText(), "Weak");
        
        // Strong Password (8+ chars, Number, Special Char, Uppercase)
        passInput.clear();
        passInput.sendKeys("StrongPass1!");
        Assert.assertEquals(strengthMeter.getText(), "Strong");
    }
}