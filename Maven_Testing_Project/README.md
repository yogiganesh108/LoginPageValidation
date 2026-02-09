# Login System with MySQL Database Integration

## Overview
This project has been updated to validate user credentials against a MySQL database instead of using hardcoded values.

## Project Structure

```
Maven_Testing_Project/
├── pom.xml (Updated with MySQL and Spark dependencies)
├── database_setup.sql (MySQL schema and table creation script)
└── src/test/java/com/logintest/
    ├── DatabaseHelper.java (Database connection and queries)
    ├── LoginService.java (Business logic for authentication)
    ├── LoginAPI.java (REST API backend server)
    └── LoginTest.java (Updated Selenium tests with database integration)

Frontend_React_App/
└── src/App.js (Updated to call backend API)
```

## Database Setup

### Step 1: Install MySQL
Make sure MySQL is installed and running on your system.

### Step 2: Create Database Schema and Table
Run the SQL script to set up your database:

```bash
mysql -u root -p < database_setup.sql
```

Or manually execute the SQL commands:

```sql
CREATE DATABASE IF NOT EXISTS your_schema_name;
USE your_schema_name;

CREATE TABLE IF NOT EXISTS user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO user (email, password) VALUES ('test@example.com', 'Password123!');
```

### Step 3: Update Database Configuration
Edit the database connection parameters in `DatabaseHelper.java`:

```java
private static final String DB_SCHEMA = "your_schema_name"; // Replace with your actual schema name
private static final String DB_USER = "root"; // Replace with your MySQL username
private static final String DB_PASSWORD = ""; // Replace with your MySQL password
```

## Running the Application

### Step 1: Install Maven Dependencies
```bash
cd "Maven_Testing_Project"
mvn clean install
```

### Step 2: Start the Backend API Server
Run the LoginAPI class to start the backend server on port 8080:

```bash
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

Or run directly from your IDE (Run LoginAPI.java main method).

You should see:
```
Database connected successfully!
Login API started successfully!
```

### Step 3: Start the React Frontend
In a new terminal:

```bash
cd "Frontend_React_App"
npm start
```

The React app will start on http://localhost:3000

### Step 4: Run Selenium Tests
In another terminal:

```bash
cd "Maven_Testing_Project"
mvn test
```

## API Endpoints

### 1. Health Check
- **URL**: `http://localhost:8080/api/health`
- **Method**: GET
- **Response**: 
```json
{
  "status": "ok",
  "message": "API is running"
}
```

### 2. Login
- **URL**: `http://localhost:8080/api/login`
- **Method**: POST
- **Body**:
```json
{
  "email": "test@example.com",
  "password": "Password123!"
}
```
- **Success Response** (200):
```json
{
  "success": true,
  "message": "Login Successful"
}
```
- **Error Response** (401):
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

### 3. Register
- **URL**: `http://localhost:8080/api/register`
- **Method**: POST
- **Body**:
```json
{
  "email": "newuser@example.com",
  "password": "SecurePass123!"
}
```
- **Success Response** (201):
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

## Key Components

### 1. DatabaseHelper.java
- Manages MySQL database connections
- Provides methods for:
  - Validating credentials
  - Checking user existence
  - Adding/removing users
  
### 2. LoginService.java
- Business logic layer
- Handles:
  - Input validation
  - Authentication
  - User registration

### 3. LoginAPI.java
- REST API backend using Spark Java framework
- Exposes endpoints for login and registration
- Handles CORS for React frontend
- Runs on port 8080

### 4. LoginTest.java
- Updated Selenium tests
- Uses @BeforeClass to setup test data in database
- Uses @AfterClass to cleanup database connection
- Tests validate against actual database credentials

### 5. App.js (React Frontend)
- Updated to make API calls to backend
- Uses fetch() to communicate with http://localhost:8080
- Handles success and error responses

## Testing Flow

1. **Database Setup**: Test data is inserted via @BeforeClass
2. **API Server**: Must be running on port 8080
3. **React Frontend**: Must be running on port 3000
4. **Selenium Tests**: Access frontend and verify database-backed authentication

## Important Notes

### Security Considerations (Production)
⚠️ **Current implementation is for testing only!**

For production use:
1. **Hash passwords** using bcrypt or Argon2
2. **Use environment variables** for database credentials
3. **Implement proper session management** with JWT tokens
4. **Add HTTPS** support
5. **Implement rate limiting** to prevent brute force attacks
6. **Add SQL injection protection** (already using PreparedStatements)

### Database Configuration
Make sure to update these in `DatabaseHelper.java`:
- `DB_SCHEMA`: Your MySQL database name
- `DB_USER`: Your MySQL username
- `DB_PASSWORD`: Your MySQL password

### Troubleshooting

**Issue**: "Unable to connect to server"
- **Solution**: Make sure the backend API is running on port 8080

**Issue**: "Database connection failed"
- **Solution**: Check MySQL is running and credentials in DatabaseHelper.java are correct

**Issue**: Tests failing with "Invalid credentials"
- **Solution**: Ensure test data exists in database (check @BeforeClass setup)

**Issue**: Port 8080 already in use
- **Solution**: Stop any other processes using port 8080 or change the port in LoginAPI.java

## Sample Test Credentials
- Email: `test@example.com`
- Password: `Password123!`

These credentials should be present in your database for tests to pass.
