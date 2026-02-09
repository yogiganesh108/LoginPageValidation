# Configuration Guide

## Required Configuration Before Running

### 1. Update DatabaseHelper.java

**File Location**: `src/test/java/com/logintest/DatabaseHelper.java`

**Lines to Update** (around line 11-14):

```java
private static final String DB_SCHEMA = "your_schema_name"; // ← CHANGE THIS
private static final String DB_USER = "root";               // ← CHANGE THIS
private static final String DB_PASSWORD = "";               // ← CHANGE THIS
```

**Example Configuration**:
```java
private static final String DB_SCHEMA = "skillpalaver";     // Your database name
private static final String DB_USER = "root";               // Your MySQL username
private static final String DB_PASSWORD = "mypassword";     // Your MySQL password
```

---

## MySQL Database Setup

### Option 1: Run the SQL Script
```bash
mysql -u root -p < database_setup.sql
```

### Option 2: Run Commands Manually
```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE skillpalaver;
USE skillpalaver;

-- Create table
CREATE TABLE user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!');

-- Verify
SELECT * FROM user;
```

### Option 3: Use the Setup Script
```bash
cd Maven_Testing_Project
./setup.sh
```
The script will prompt you for credentials and set up everything automatically.

---

## Complete Startup Checklist

- [ ] MySQL is installed and running
- [ ] Database schema created (e.g., `skillpalaver`)
- [ ] Table `user` created with `email` and `password` columns
- [ ] Test user inserted: `test@example.com` / `Password123!`
- [ ] `DatabaseHelper.java` updated with your database credentials
- [ ] Maven dependencies installed: `mvn clean install`

---

## Starting the Application

### Terminal 1: Backend API Server
```bash
cd Maven_Testing_Project
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

**Expected Output**:
```
Database connected successfully!
Login API started successfully!
```

**Test the API**:
```bash
curl http://localhost:8080/api/health
```

### Terminal 2: React Frontend
```bash
cd Frontend_React_App
npm start
```

**Expected Output**:
```
Compiled successfully!
You can now view frontend_react_app in the browser.
  Local:            http://localhost:3000
```

### Terminal 3: Run Tests (Optional)
```bash
cd Maven_Testing_Project
mvn test
```

---

## Verification

### 1. Test Backend API
```bash
# Health check
curl http://localhost:8080/api/health

# Test login
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Password123!"}'
```

### 2. Test Frontend
1. Open browser: http://localhost:3000
2. Enter credentials:
   - Email: `test@example.com`
   - Password: `Password123!`
3. Click "Sign In"
4. Should see "Login Successful! Redirecting..."

### 3. Check Database
```sql
mysql -u root -p
USE skillpalaver;
SELECT * FROM user;
```

---

## Troubleshooting

### Error: "Database connection failed"
**Solution**: Check credentials in `DatabaseHelper.java` and ensure MySQL is running.

```bash
# Check if MySQL is running
mysql -u root -p

# On macOS
brew services list | grep mysql

# On Linux
systemctl status mysql
```

### Error: "Port 8080 already in use"
**Solution**: Kill the process using port 8080 or change the port in `LoginAPI.java`.

```bash
# Find process using port 8080
lsof -ti:8080

# Kill the process
kill -9 $(lsof -ti:8080)
```

### Error: "Unable to connect to server" (Frontend)
**Solution**: Make sure the backend API is running on port 8080.

```bash
# Check if API is running
curl http://localhost:8080/api/health
```

### Tests Fail: "Invalid credentials"
**Solution**: Ensure test user exists in database.

```sql
SELECT * FROM user WHERE email = 'test@example.com';
```

If not found, insert:
```sql
INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!');
```

---

## Port Summary

| Service | Port | URL |
|---------|------|-----|
| Backend API | 8080 | http://localhost:8080 |
| React Frontend | 3000 | http://localhost:3000 |
| MySQL Database | 3306 | localhost:3306 |

---

## Project Architecture

```
┌─────────────────┐
│  React Frontend │ (Port 3000)
│   (App.js)      │
└────────┬────────┘
         │ HTTP Requests
         ↓
┌─────────────────┐
│   Backend API   │ (Port 8080)
│  (LoginAPI)     │
└────────┬────────┘
         │ JDBC
         ↓
┌─────────────────┐
│ MySQL Database  │ (Port 3306)
│  (user table)   │
└─────────────────┘
```

---

## Next Steps

Once everything is running:
1. Try registering a new user through the frontend
2. Login with the new credentials
3. Run the Selenium tests to verify everything works
4. Check the database to see new users added

---

## Security Notes (Important!)

⚠️ **Current Implementation is for Testing Only**

Before deploying to production:
1. Hash passwords using bcrypt or Argon2
2. Use environment variables for credentials
3. Implement JWT token-based authentication
4. Add HTTPS support
5. Implement rate limiting
6. Add input sanitization and validation
7. Use connection pooling for database
8. Add logging and monitoring

---

## Quick Commands Reference

```bash
# Database
mysql -u root -p
CREATE DATABASE skillpalaver;
USE skillpalaver;
SELECT * FROM user;

# Backend
cd Maven_Testing_Project
mvn clean install
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"

# Frontend
cd Frontend_React_App
npm install
npm start

# Tests
cd Maven_Testing_Project
mvn test

# API Testing
curl http://localhost:8080/api/health
```

---

Need help? Check the README.md for more detailed information.
