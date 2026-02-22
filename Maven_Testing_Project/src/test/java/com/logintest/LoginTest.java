package com.logintest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.sql.SQLException;
import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static LoginService loginService;

    private final String BASE_URL = "http://localhost:3000";

    private final String VALID_EMAIL = "test@example.com";
    private final String VALID_PASSWORD = "Password123!";
    private final String INVALID_EMAIL = "wrong@user.com";
    private final String INVALID_PASSWORD = "WrongPass123";

    // ---------- DATABASE SETUP ----------

    @BeforeClass
    public static void setupDatabase() throws SQLException {
        loginService = new LoginService();
        loginService.initialize();
       
        loginService.removeUser("test@example.com");
        loginService.removeUser("newuser@example.com");
        loginService.removeUser("user2@example.com");
        loginService.removeUser("admin@example.com");
        
        loginService.registerUser("test@example.com", "Password123!");
    }

    @AfterClass
    public static void cleanupDatabase() {
        if (loginService != null) loginService.cleanup();
    }

    // ---------- DRIVER SETUP ----------

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

   
    // FUNCTIONAL TESTING
    

    @Test
    public void testValidLogin() {
        login(VALID_EMAIL, VALID_PASSWORD);
        assertStatusContains("Login Successful");
    }

    @Test
    public void testInvalidLogin() {
        login(INVALID_EMAIL, INVALID_PASSWORD);
        assertStatusContains("Invalid");
    }

    @Test
    public void testCorrectEmailWrongPassword() {
        login(VALID_EMAIL, INVALID_PASSWORD);
        assertStatusContains("Invalid");
    }

    @Test
    public void testEmptyFields() {
        clickSubmit();
        assertStatusContains("fix validation");
    }

    @Test
    public void testInvalidEmailFormat() {
        typeEmail("invalid-email");
        clickSubmit();
        assertEmailError("invalid");
    }

  
    // BOUNDARY VALUE ANALYSIS
    

    @Test
    public void testEmailMaxLengthBoundary() {
        String longEmail = "a".repeat(51) + "@test.com";
        typeEmail(longEmail);
        assertEmailError("maximum length");
    }

    @Test
    public void testPasswordMinBoundary() {
        typePassword("12345");
        assertPasswordError("at least 6");
    }

    @Test
    public void testPasswordMaxBoundary() {
        String longPass = "A".repeat(31);
        typePassword(longPass);
        assertPasswordError("maximum length");
    }

   
    // NEGATIVE TESTING
    

    @Test
    public void testSpacesOnlyInput() {
        login("   ", "   ");
        assertStatusContains("validation");
    }

    @Test
    public void testLeadingTrailingSpaces() {
        login("  test@example.com  ", "  Password123!  ");
        assertStatusContains("Invalid");
    }

   

    
    // SECURITY TESTING
    

    @Test
    public void testSQLInjection() {
        login("' OR '1'='1", "password");
        assertStatusContains("Invalid");
    }

    

    
    // REGISTRATION TESTING
    

    @Test
    public void testRegistrationSuccess() {
        switchToRegister();

        typeEmail("newuser@example.com");
        typePassword("StrongPass1!");
        typeConfirmPassword("StrongPass1!");

        clickSubmit();

        assertStatusContains("Registration Successful");
    }

    @Test
    public void testPasswordMismatch() {
        switchToRegister();

        typeEmail("user2@example.com");
        typePassword("Password123!");
        typeConfirmPassword("Different123!");

        clickSubmit();

        assertStatusContains("validation");
    }

   
    // PASSWORD STRENGTH
    

    @Test
    public void testPasswordStrengthMeter() {
        switchToRegister();

        typePassword("weak");
        Assert.assertEquals(getStrengthText(), "Weak");

        clearPassword();
        typePassword("StrongPass1!");
        Assert.assertEquals(getStrengthText(), "Strong");
    }

   
    // USABILITY TESTING
    

    @Test
    public void testEnterKeySubmission() {
        typeEmail(VALID_EMAIL);
        WebElement pass = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        pass.sendKeys(VALID_PASSWORD + Keys.ENTER);
        assertStatusContains("Login Successful");
    }

    @Test
    public void testPasswordVisibilityToggle() {
        WebElement pass = driver.findElement(By.cssSelector("[data-testid='password-input']"));
        pass.sendKeys("Password123!");

        WebElement toggle = driver.findElement(By.cssSelector("button[type='button']"));
        toggle.click();

        Assert.assertEquals(pass.getAttribute("type"), "text");
    }

    
    // HELPER METHODS
   

    private void login(String email, String pass) {
        typeEmail(email);
        typePassword(pass);
        clickSubmit();
    }

    private void typeEmail(String email) {
        driver.findElement(By.cssSelector("[data-testid='email-input']")).sendKeys(email);
    }

    private void typePassword(String pass) {
        driver.findElement(By.cssSelector("[data-testid='password-input']")).sendKeys(pass);
    }

    private void clearPassword() {
        driver.findElement(By.cssSelector("[data-testid='password-input']")).clear();
    }

    private void typeConfirmPassword(String pass) {
        driver.findElement(By.cssSelector("[data-testid='confirm-password-input']")).sendKeys(pass);
    }

    private void clickSubmit() {
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();
    }

    private void switchToRegister() {
        driver.findElement(By.cssSelector("[data-testid='toggle-auth-mode']")).click();
    }

    private String getStrengthText() {
        return driver.findElement(By.cssSelector("[data-testid='password-strength'] p")).getText();
    }

    private void assertStatusContains(String text) {
        // Add a small delay to ensure response has arrived
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WebElement status = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("[data-testid='status-message']")));
        try {
            Thread.sleep(200); // Give a bit more time for visibility
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(status.getText().toLowerCase().contains(text.toLowerCase()));
    }

    private void assertEmailError(String text) {
        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='email-error']")));
        Assert.assertTrue(error.getText().toLowerCase().contains(text.toLowerCase()));
    }

    private void assertPasswordError(String text) {
        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-testid='password-error']")));
        Assert.assertTrue(error.getText().toLowerCase().contains(text.toLowerCase()));
    }
}