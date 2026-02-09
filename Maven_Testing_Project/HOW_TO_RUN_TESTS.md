# SOLUTION: How to Run Tests Successfully

## The Problem

You're getting test failures when running `mvn test` because the system architecture has changed:

**OLD WAY (before):**
```
Selenium Tests → React Frontend (with hardcoded credentials)
```

**NEW WAY (now):**
```
Selenium Tests → React Frontend → Backend API → MySQL Database
```

The tests require **all components to be running** simultaneously!

## The Solution: 3-Step Process

### Step 1: Start Backend API (Terminal 1)

```bash
cd Maven_Testing_Project
./start-api.sh
```

**Wait for these messages:**
```
Database connected successfully!
Login API started successfully!
```

**Leave this terminal running!** The API server must stay active.

---

### Step 2: Start React Frontend (Terminal 2)

Open a **new terminal** and run:

```bash
cd Frontend_React_App
npm start
```

**Wait for:**
```
Compiled successfully!
```

Browser will open at http://localhost:3000

**Leave this terminal running too!**

---

### Step 3: Run Tests (Terminal 3)

Open a **third terminal** and run:

```bash
cd Maven_Testing_Project

# First, check if everything is ready
./check-prerequisites.sh

# If all checks pass, run tests
mvn test
```

---

## Quick Verification

Before running tests, verify manually:

```bash
# Check API is running
curl http://localhost:8080/api/health
# Should return: {"status":"ok","message":"API is running"}

# Check Frontend is running
curl http://localhost:3000
# Should return HTML

# Manual test in browser
# 1. Open: http://localhost:3000
# 2. Enter: test@example.com / Password123!
# 3. Click "Sign In"
# 4. Should see: "Login Successful! Redirecting..."
```

If manual test works, automated tests will work!

---

## Visual Guide

```
┌─────────────────────────────────────────────────────────────┐
│ Terminal 1: Backend API                                     │
├─────────────────────────────────────────────────────────────┤
│ $ cd Maven_Testing_Project                                  │
│ $ ./start-api.sh                                            │
│ Database connected successfully!                            │
│ Login API started successfully!                             │
│ [Keep running...]                                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Terminal 2: React Frontend                                  │
├─────────────────────────────────────────────────────────────┤
│ $ cd Frontend_React_App                                     │
│ $ npm start                                                 │
│ Compiled successfully!                                      │
│ [Browser opens at localhost:3000]                          │
│ [Keep running...]                                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Terminal 3: Run Tests                                       │
├─────────────────────────────────────────────────────────────┤
│ $ cd Maven_Testing_Project                                  │
│ $ ./check-prerequisites.sh                                  │
│ ✅ All prerequisites met!                                   │
│                                                             │
│ $ mvn test                                                  │
│ [Tests run: 7, Failures: 0, Success!]                      │
└─────────────────────────────────────────────────────────────┘
```

---

## Common Errors & Solutions

### Error: "Connection refused (port 8080)"
**Reason:** Backend API is not running  
**Solution:** Start API in Terminal 1 first

### Error: "Connection refused (port 3000)"
**Reason:** React frontend is not running  
**Solution:** Start frontend in Terminal 2

### Error: "Unable to locate element"
**Reason:** Page didn't load correctly  
**Solution:** Ensure both API and Frontend are running

### Error: "Success message mismatch"
**Reason:** Backend API couldn't validate credentials  
**Solution:** 
- Check API is connected to database
- Verify test user exists: `test@example.com` / `Password123!`

---

## Alternative: Run Everything with One Script

Create a file `run-all.sh`:

```bash
#!/bin/bash

# Start API in background
cd Maven_Testing_Project
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI" > api.log 2>&1 &
API_PID=$!

# Start Frontend in background
cd ../Frontend_React_App
npm start > frontend.log 2>&1 &
FRONTEND_PID=$!

# Wait for services to start
echo "Waiting for services to start..."
sleep 10

# Run tests
cd ../Maven_Testing_Project
mvn test

# Cleanup
kill $API_PID $FRONTEND_PID
```

---

## Test Execution Checklist

Before running `mvn test`, verify:

- [ ] MySQL is running
- [ ] Database `LoginData` exists with `user` table
- [ ] Backend API is running on port 8080
- [ ] React frontend is running on port 3000
- [ ] Test user exists: `test@example.com` / `Password123!`
- [ ] Manual login works in browser

Then run:
```bash
./check-prerequisites.sh && mvn test
```

---

## Why This Architecture?

The system now uses a **realistic production-like architecture**:

- **Frontend:** User interface (React)
- **Backend API:** Business logic and authentication (Java/Spark)
- **Database:** Data persistence (MySQL)

This is how real-world applications work, making the tests more valuable!

---

## Summary

✅ **DO THIS:**
1. Start API server (Terminal 1)
2. Start React frontend (Terminal 2)  
3. Run `./check-prerequisites.sh` (Terminal 3)
4. Run `mvn test` (Terminal 3)

❌ **DON'T DO THIS:**
- Run `mvn test` without starting API and Frontend first
- Expect tests to pass without all services running

---

**Need help?** Check [TEST_EXECUTION_GUIDE.md](TEST_EXECUTION_GUIDE.md) for more details.
