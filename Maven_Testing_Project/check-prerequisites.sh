#!/bin/bash

# Script to check if all prerequisites are running before executing tests

echo "=========================================="
echo "Checking Test Prerequisites"
echo "=========================================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

all_ok=true

# Check 1: Backend API on port 8080
echo -n "1. Checking Backend API (port 8080)... "
if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Running${NC}"
else
    echo -e "${RED}❌ NOT running${NC}"
    echo "   Solution: cd Maven_Testing_Project && ./start-api.sh"
    all_ok=false
fi

# Check 2: React Frontend on port 3000
echo -n "2. Checking React Frontend (port 3000)... "
if curl -s http://localhost:3000 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Running${NC}"
else
    echo -e "${RED}❌ NOT running${NC}"
    echo "   Solution: cd Frontend_React_App && npm start"
    all_ok=false
fi

# Check 3: MySQL Database
echo -n "3. Checking MySQL Database... "
if mysql -u root -p"21030-Cm-108" -e "USE LoginData; SELECT 1;" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Accessible${NC}"
else
    echo -e "${RED}❌ NOT accessible${NC}"
    echo "   Solution: Check MySQL is running and credentials are correct"
    all_ok=false
fi

# Check 4: Test user exists
echo -n "4. Checking test user in database... "
test_user_count=$(mysql -u root -p"21030-Cm-108" -D LoginData -se "SELECT COUNT(*) FROM user WHERE email='test@example.com';" 2>/dev/null)
if [ "$test_user_count" = "1" ]; then
    echo -e "${GREEN}✅ Exists${NC}"
else
    echo -e "${YELLOW}⚠️  Not found${NC}"
    echo "   Note: Tests will create it automatically"
fi

echo ""
echo "=========================================="

if [ "$all_ok" = true ]; then
    echo -e "${GREEN}✅ All prerequisites met!${NC}"
    echo ""
    echo "You can now run:"
    echo "  mvn test"
    exit 0
else
    echo -e "${RED}❌ Prerequisites NOT met!${NC}"
    echo ""
    echo "Please start the required services first:"
    echo ""
    echo "Terminal 1:"
    echo "  cd Maven_Testing_Project"
    echo "  ./start-api.sh"
    echo ""
    echo "Terminal 2:"
    echo "  cd Frontend_React_App"
    echo "  npm start"
    echo ""
    echo "Then run this script again to verify."
    exit 1
fi
