# Test Execution Guide

## Prerequisites for Running Tests

Before running `mvn test`, you MUST have the following running:

### 1. Backend API Server (Port 8080)
**Terminal 1:**
```bash
cd Maven_Testing_Project
./start-api.sh
```

**OR:**
```bash
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
```

**Verify it's running:**
```bash
curl http://localhost:8080/api/health
# Should return: {"status":"ok","message":"API is running"}
```

### 2. React Frontend (Port 3000)
**Terminal 2:**
```bash
cd Frontend_React_App
npm start
```

**Verify it's running:**
Open browser: http://localhost:3000

### 3. MySQL Database
Ensure MySQL is running with:
- Database: `LoginData`
- Table: `user` with columns `email` and `password`
- Test user: `test@example.com` / `Password123!`

### 4. Run Tests
**Terminal 3:**
```bash
cd Maven_Testing_Project
mvn test
```

## Why Tests Are Failing

The system now works with a **full-stack architecture**:

```
Selenium Tests → React Frontend (3000) → Backend API (8080) → MySQL Database (3306)
```

If any component is missing, tests will fail:

### Error: "Success message mismatch" or "Invalid credentials"
**Cause:** Backend API is not running on port 8080
**Solution:** Start the API server first

### Error: "Unable to locate element"
**Cause:** React frontend is not running on port 3000
**Solution:** Start the React app first

### Error: "Database connection failed"
**Cause:** MySQL is not running or credentials are wrong
**Solution:** Check MySQL and DatabaseHelper.java configuration

## Proper Test Execution Order

```bash
# Terminal 1 - Start Backend API
cd Maven_Testing_Project
./start-api.sh
# Wait for: "Database connected successfully!" and "Login API started successfully!"

# Terminal 2 - Start Frontend
cd Frontend_React_App
npm start
# Wait for: "Compiled successfully!" and browser opens

# Terminal 3 - Run Tests
cd Maven_Testing_Project
mvn test
```

## Quick Check Script

Use this to verify all components before running tests:

```bash
# Check if Backend API is running
curl -s http://localhost:8080/api/health && echo "✅ API is running" || echo "❌ API is NOT running"

# Check if Frontend is running
curl -s http://localhost:3000 > /dev/null && echo "✅ Frontend is running" || echo "❌ Frontend is NOT running"

# Check if MySQL is running
mysql -u root -p"21030-Cm-108" -e "USE LoginData; SELECT COUNT(*) FROM user;" && echo "✅ Database is accessible" || echo "❌ Database is NOT accessible"
```

## Understanding Test Flow

Each test works like this:

1. **@BeforeClass** - Sets up test data in database
2. **@BeforeMethod** - Opens Chrome browser and navigates to React app
3. **@Test** - Interacts with frontend UI (fill form, click button)
4. Frontend calls backend API: `POST http://localhost:8080/api/login`
5. Backend queries MySQL database
6. Backend returns response to frontend
7. Frontend displays success/error message
8. **Test verifies** the message on the page
9. **@AfterMethod** - Closes browser

## Common Test Failures & Solutions

| Error | Reason | Solution |
|-------|--------|----------|
| Connection refused (port 8080) | API not running | Start `./start-api.sh` |
| Connection refused (port 3000) | Frontend not running | Run `npm start` |
| NoSuchElementException | Wrong page loaded | Check both API and Frontend are running |
| Success message mismatch | Credentials not validated | Check API is connected to database |
| SQL Exception | Database not accessible | Check MySQL is running |

## Manual Testing Before Automated Tests

Before running automated tests, manually test:

1. Open http://localhost:3000 in browser
2. Enter: `test@example.com` / `Password123!`
3. Click "Sign In"
4. Should see: "Login Successful! Redirecting..."

If manual test works, automated tests should work too.

## Running Individual Tests

```bash
# Run only one test
mvn test -Dtest=LoginTest#testValidLogin

# Run tests in headless mode (no browser UI)
# Edit LoginTest.java and uncomment: options.addArguments("--headless");
```

## Debugging Tips

### Enable Verbose Logging
```bash
mvn test -X
```

### Check Surefire Reports
```bash
cat target/surefire-reports/com.logintest.LoginTest.txt
```

### Take Screenshots on Failure
Add to LoginTest.java in @AfterMethod:
```java
if (driver != null) {
    File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
    Files.copy(screenshot.toPath(), Paths.get("screenshot.png"));
}
```

## Summary

❌ **DON'T:** Run `mvn test` without starting API and Frontend first  
✅ **DO:** Start API → Start Frontend → Then run tests

The tests are **integration tests** that require the full stack to be running!
