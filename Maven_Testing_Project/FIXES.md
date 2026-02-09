# Issue Resolution: ClassNotFoundException for LoginAPI

## Problem
When trying to run the LoginAPI with:
```bash
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

You received the error:
```
java.lang.ClassNotFoundException: com.logintest.LoginAPI
```

## Root Cause
The LoginAPI.java, DatabaseHelper.java, and LoginService.java files were placed in the **test source directory** (`src/test/java`) instead of the **main source directory** (`src/main/java`).

Maven's exec plugin cannot find classes in the test directory when running with `exec:java` because:
- `src/test/java` is only compiled during the test phase
- `src/main/java` is compiled during the compile phase and is on the classpath for execution

## Solution Applied
The following files were **copied** from `src/test/java/com/logintest/` to `src/main/java/com/logintest/`:
1. ✅ LoginAPI.java
2. ✅ DatabaseHelper.java  
3. ✅ LoginService.java

The test versions remain in `src/test/java` for use by LoginTest.java.

## Verification
After the fix:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

Expected output:
```
Database connected successfully!
Login API started successfully!
```

## New Project Structure
```
Maven_Testing_Project/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── logintest/
│   │               ├── LoginAPI.java       ← Main application (ADDED)
│   │               ├── DatabaseHelper.java ← Database access (ADDED)
│   │               └── LoginService.java   ← Business logic (ADDED)
│   └── test/
│       └── java/
│           └── com/
│               └── logintest/
│                   ├── LoginTest.java              ← Selenium tests
│                   ├── TestDatabaseConnection.java ← DB test utility
│                   ├── DatabaseHelper.java         ← (kept for tests)
│                   ├── LoginService.java           ← (kept for tests)
│                   └── LoginAPI.java               ← (kept for reference)
└── ...
```

## Running the Application

### Option 1: Using the startup script (Easiest)
```bash
cd Maven_Testing_Project
./start-api.sh
```

### Option 2: Using Maven directly
```bash
cd Maven_Testing_Project
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

### Option 3: Package and run as JAR (Production-like)
```bash
mvn clean package
java -cp target/login-form-testing-1.0-SNAPSHOT.jar com.logintest.LoginAPI
```

## Additional Notes

### About the SLF4J Warning
You may see this warning when starting the API:
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
```

This is **NOT an error** - it's just a logging configuration notice. The Spark framework uses SLF4J for logging, but we only included slf4j-simple in test scope. You can safely ignore this warning, or add the dependency to main scope if you want proper logging:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.9</version>
</dependency>
```

### Database Configuration
Make sure you've updated the database credentials in **both locations**:
- `src/main/java/com/logintest/DatabaseHelper.java` (for running the API)
- `src/test/java/com/logintest/DatabaseHelper.java` (for running tests)

Currently configured as:
- DB_SCHEMA: "LoginData"
- DB_USER: "root"
- DB_PASSWORD: (already set)

## Testing the Fix

1. **Test API is running:**
   ```bash
   curl http://localhost:8080/api/health
   ```
   Expected: `{"status":"ok","message":"API is running"}`

2. **Test login:**
   ```bash
   curl -X POST http://localhost:8080/api/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"Password123!"}'
   ```

3. **Start React frontend in another terminal:**
   ```bash
   cd Frontend_React_App
   npm start
   ```

4. **Run Selenium tests in another terminal:**
   ```bash
   cd Maven_Testing_Project
   mvn test
   ```

## Summary
✅ **Issue Fixed!** The classes have been moved to the correct location (`src/main/java`), and the API should now start successfully.

---

**Date Fixed:** February 9, 2026  
**Issue:** ClassNotFoundException for LoginAPI  
**Solution:** Moved classes from src/test/java to src/main/java
