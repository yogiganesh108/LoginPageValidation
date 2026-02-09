import os
import zipfile

# --- 1. DEFINE FILE CONTENTS ---

# React App.js Content
react_app_js = r"""import React, { useState, useEffect } from 'react';
import { User, Lock, Mail, AlertCircle, CheckCircle, Shield, Eye, EyeOff } from 'lucide-react';

export default function App() {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState({ type: '', message: '' });
  const [loading, setLoading] = useState(false);

  // Password strength checking
  const checkStrength = (pass) => {
    let score = 0;
    if (pass.length >= 8) score++;
    if (/[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;
    return score;
  };

  const strength = checkStrength(formData.password);

  // Input validation
  const validate = (field, value) => {
    let newErrors = { ...errors };

    // SQL Injection basic pattern detection for demonstration
    const sqlInjectionPattern = /('|"|;|--|\/\*|\*\/)/;

    if (field === 'email') {
      if (!value) newErrors.email = 'Email is required';
      else if (!/\S+@\S+\.\S+/.test(value)) newErrors.email = 'Email is invalid';
      else if (value.length > 50) newErrors.email = 'Email exceeds maximum length (50)';
      else if (sqlInjectionPattern.test(value)) newErrors.email = 'Invalid characters detected (Security)';
      else delete newErrors.email;
    }

    if (field === 'password') {
      if (!value) newErrors.password = 'Password is required';
      else if (value.length < 6) newErrors.password = 'Password must be at least 6 characters';
      else if (value.length > 30) newErrors.password = 'Password exceeds maximum length';
      else delete newErrors.password;
    }

    if (field === 'confirmPassword' && !isLogin) {
      if (value !== formData.password) newErrors.confirmPassword = 'Passwords do not match';
      else delete newErrors.confirmPassword;
    }

    setErrors(newErrors);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    validate(name, value);
    setStatus({ type: '', message: '' }); // Clear status on type
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setStatus({ type: '', message: '' });

    // Simulate Network Delay
    setTimeout(() => {
      const hasErrors = Object.keys(errors).length > 0;
      const emptyFields = !formData.email || !formData.password || (!isLogin && !formData.confirmPassword);

      if (hasErrors || emptyFields) {
        setStatus({ type: 'error', message: 'Please fix validation errors before submitting.' });
        setLoading(false);
        return;
      }

      // Simulation of Backend Logic
      if (isLogin) {
        // Hardcoded credential check for testing
        if (formData.email === 'test@example.com' && formData.password === 'Password123!') {
          setStatus({ type: 'success', message: 'Login Successful! Redirecting...' });
        } else {
          setStatus({ type: 'error', message: 'Invalid credentials.' });
        }
      } else {
        setStatus({ type: 'success', message: 'Registration Successful! Please login.' });
      }
      setLoading(false);
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 to-purple-100 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden">
        {/* Header */}
        <div className="bg-indigo-600 p-8 text-center">
          <div className="mx-auto bg-white/20 w-16 h-16 rounded-full flex items-center justify-center mb-4 backdrop-blur-sm">
            <Shield className="text-white w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-white mb-2" data-testid="page-title">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
          <p className="text-indigo-100 text-sm">
            {isLogin ? 'Secure access to your dashboard' : 'Join our secure platform today'}
          </p>
        </div>

        {/* Form */}
        <div className="p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Email Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
              <div className="relative">
                <Mail className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  data-testid="email-input"
                  className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 transition-colors ${
                    errors.email 
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-200' 
                      : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                  }`}
                  placeholder="name@company.com"
                />
              </div>
              {errors.email && (
                <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="email-error">
                  <AlertCircle className="w-3 h-3 mr-1" /> {errors.email}
                </p>
              )}
            </div>

            {/* Password Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  data-testid="password-input"
                  className={`w-full pl-10 pr-10 py-2 border rounded-lg focus:ring-2 transition-colors ${
                    errors.password
                      ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
                      : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                  }`}
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-4 h-4"/> : <Eye className="w-4 h-4"/>}
                </button>
              </div>
              {errors.password && (
                <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="password-error">
                  <AlertCircle className="w-3 h-3 mr-1" /> {errors.password}
                </p>
              )}
              
              {/* Password Strength Meter (Bonus Requirement) */}
              {!isLogin && formData.password && (
                <div className="mt-2" data-testid="password-strength">
                  <div className="flex gap-1 h-1 mb-1">
                    {[1, 2, 3, 4].map((i) => (
                      <div 
                        key={i} 
                        className={`flex-1 rounded-full transition-all duration-300 ${
                          strength >= i 
                            ? (strength <= 2 ? 'bg-red-400' : strength === 3 ? 'bg-yellow-400' : 'bg-green-400')
                            : 'bg-gray-200'
                        }`}
                      />
                    ))}
                  </div>
                  <p className="text-xs text-gray-500 text-right">
                    {strength <= 2 ? 'Weak' : strength === 3 ? 'Medium' : 'Strong'}
                  </p>
                </div>
              )}
            </div>

            {/* Confirm Password (Registration Only) */}
            {!isLogin && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                <div className="relative">
                  <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                  <input
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    data-testid="confirm-password-input"
                    className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 transition-colors ${
                      errors.confirmPassword
                        ? 'border-red-300 focus:border-red-500 focus:ring-red-200'
                        : 'border-gray-300 focus:border-indigo-500 focus:ring-indigo-200'
                    }`}
                    placeholder="••••••••"
                  />
                </div>
                {errors.confirmPassword && (
                  <p className="text-red-500 text-xs mt-1 flex items-center" data-testid="confirm-error">
                    <AlertCircle className="w-3 h-3 mr-1" /> {errors.confirmPassword}
                  </p>
                )}
              </div>
            )}

            {/* Status Message */}
            {status.message && (
              <div 
                data-testid="status-message"
                className={`p-3 rounded-lg text-sm flex items-start ${
                  status.type === 'error' ? 'bg-red-50 text-red-700' : 'bg-green-50 text-green-700'
                }`}
              >
                {status.type === 'error' ? <AlertCircle className="w-4 h-4 mr-2 mt-0.5" /> : <CheckCircle className="w-4 h-4 mr-2 mt-0.5" />}
                {status.message}
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              data-testid="submit-button"
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 rounded-lg transition-colors focus:ring-4 focus:ring-indigo-200 disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                isLogin ? 'Sign In' : 'Create Account'
              )}
            </button>
          </form>
        </div>

        {/* Footer */}
        <div className="bg-gray-50 p-4 text-center border-t border-gray-100">
          <p className="text-sm text-gray-600">
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <button
              onClick={() => {
                setIsLogin(!isLogin);
                setFormData({ email: '', password: '', confirmPassword: '' });
                setErrors({});
                setStatus({ type: '', message: '' });
              }}
              data-testid="toggle-auth-mode"
              className="text-indigo-600 font-semibold hover:text-indigo-700 transition-colors"
            >
              {isLogin ? 'Sign up' : 'Log in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
"""

# React package.json
react_package_json = """{
  "name": "login-test-app",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "cra-template": "1.2.0",
    "lucide-react": "^0.309.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-scripts": "5.0.1"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}"""

# React index.js
react_index_js = """import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
"""

# React index.css (Tailwind directive)
react_index_css = """
/* Simple Tailwind CDN simulation for local running without full build chain setup in text */
@tailwind base;
@tailwind components;
@tailwind utilities;
"""

# React public/index.html
react_index_html = """<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Login Test App</title>
    <script src="https://cdn.tailwindcss.com"></script>
  </head>
  <body>
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <div id="root"></div>
  </body>
</html>
"""

# Maven pom.xml
maven_pom_xml = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.logintest</groupId>
    <artifactId>login-form-testing</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <selenium.version>4.16.1</selenium.version>
        <testng.version>7.9.0</testng.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.github.bonigarcia</groupId>
            <artifactId>webdrivermanager</artifactId>
            <version>5.6.3</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.3</version>
            </plugin>
        </plugins>
    </build>
</project>
"""

# Java Test File
java_test_file = r"""package com.logintest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    
    // URL where the React App is running locally
    private final String BASE_URL = "http://localhost:3000";

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

        // Valid credentials defined in App.jsx
        emailInput.sendKeys("test@example.com");
        passInput.sendKeys("Password123!");
        submitBtn.click();

        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        Assert.assertTrue(statusMsg.getText().contains("Login Successful"), "Success message mismatch!");
    }

    @Test(priority = 2, description = "Test login with invalid credentials")
    public void testInvalidLogin() {
        driver.findElement(By.cssSelector("[data-testid='email-input']")).sendKeys("wrong@user.com");
        driver.findElement(By.cssSelector("[data-testid='password-input']")).sendKeys("WrongPass123");
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();

        WebElement statusMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='status-message']")));
        Assert.assertTrue(statusMsg.getText().contains("Invalid credentials"), "Error message for invalid login mismatch!");
    }

    @Test(priority = 3, description = "Test empty fields validation")
    public void testEmptyFields() {
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();

        WebElement emailError = driver.findElement(By.cssSelector("[data-testid='email-error']"));
        WebElement passError = driver.findElement(By.cssSelector("[data-testid='password-error']"));

        Assert.assertTrue(emailError.isDisplayed(), "Email error not displayed");
        Assert.assertEquals(emailError.getText(), "Email is required");
        Assert.assertEquals(passError.getText(), "Password is required");
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

    @Test(priority = 6, description = "Test basic SQL Injection Attempt prevention")
    public void testSQLInjectionAttempt() {
        WebElement emailInput = driver.findElement(By.cssSelector("[data-testid='email-input']"));
        
        // Common SQLi pattern
        String sqlAttack = "' OR '1'='1"; 
        emailInput.sendKeys(sqlAttack);

        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='email-error']")));
        
        // The App.jsx is programmed to detect this specific pattern and show a security error
        Assert.assertTrue(emailError.getText().contains("Invalid characters") || emailError.getText().contains("Security"), 
            "Application did not flag potential SQL Injection characters");
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
}"""

# --- 2. SETUP DIRECTORY STRUCTURE ---

# Directories to create
frontend_dir = "Frontend_React_App"
maven_dir = "Maven_Testing_Project"

# File maps (path relative to root : content)
frontend_files = {
    f"{frontend_dir}/package.json": react_package_json,
    f"{frontend_dir}/src/App.js": react_app_js,
    f"{frontend_dir}/src/index.js": react_index_js,
    f"{frontend_dir}/src/index.css": react_index_css,
    f"{frontend_dir}/public/index.html": react_index_html,
}

maven_files = {
    f"{maven_dir}/pom.xml": maven_pom_xml,
    f"{maven_dir}/src/test/java/com/logintest/LoginTest.java": java_test_file,
}

# --- 3. GENERATION FUNCTIONS ---

def create_files(file_map):
    for path, content in file_map.items():
        # Ensure directory exists
        os.makedirs(os.path.dirname(path), exist_ok=True)
        # Write file
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)

def create_zip(source_dir, zip_name):
    with zipfile.ZipFile(zip_name, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(source_dir):
            for file in files:
                file_path = os.path.join(root, file)
                # Arcname removes the full path so zip structure is clean
                arcname = os.path.relpath(file_path, start=os.path.dirname(source_dir))
                zipf.write(file_path, arcname)

# --- 4. EXECUTE ---

print("Generating Frontend Project...")
create_files(frontend_files)
create_zip(frontend_dir, "Frontend_Project.zip")

print("Generating Maven Testing Project...")
create_files(maven_files)
create_zip(maven_dir, "Maven_Testing_Project.zip")

print("\nDONE!")
print(f"1. Frontend_Project.zip created.")
print(f"2. Maven_Testing_Project.zip created.")
