# System Flow Diagrams

## Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER BROWSER                            │
│                     http://localhost:3000                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ HTTP Request
                            │ (Login/Register)
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                     REACT FRONTEND (App.js)                     │
│  - Input validation                                             │
│  - Form handling                                                │
│  - API communication                                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ fetch() POST Request
                            │ JSON: {email, password}
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                   BACKEND API (LoginAPI.java)                   │
│                      Port: 8080                                 │
│  Endpoints:                                                     │
│  - POST /api/login                                              │
│  - POST /api/register                                           │
│  - GET  /api/health                                             │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ Validate credentials
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                  LoginService.java (Business Logic)             │
│  - Input validation                                             │
│  - Credential validation                                        │
│  - User registration                                            │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ Database queries
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│               DatabaseHelper.java (Data Access Layer)           │
│  - Connection management                                        │
│  - Prepared statements                                          │
│  - CRUD operations                                              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ JDBC Connection
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                      MYSQL DATABASE                             │
│                       Port: 3306                                │
│  Schema: your_schema_name                                       │
│  Table: user (email, password)                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Login Flow Sequence

```
User          Frontend        Backend API     LoginService    DatabaseHelper    MySQL DB
 │               │                 │               │                │              │
 │──Enter────────│                 │               │                │              │
 │  credentials  │                 │               │                │              │
 │               │                 │               │                │              │
 │──Click Login──│                 │               │                │              │
 │               │                 │               │                │              │
 │               │──POST /api/login│               │                │              │
 │               │  {email,pass}   │               │                │              │
 │               │                 │               │                │              │
 │               │                 │──validateLogin│                │              │
 │               │                 │  (email,pass) │                │              │
 │               │                 │               │                │              │
 │               │                 │               │──validate──────│              │
 │               │                 │               │  Credentials   │              │
 │               │                 │               │  (email,pass)  │              │
 │               │                 │               │                │              │
 │               │                 │               │                │──SELECT────→ │
 │               │                 │               │                │  FROM user   │
 │               │                 │               │                │  WHERE email │
 │               │                 │               │                │              │
 │               │                 │               │                │←─Result──────│
 │               │                 │               │                │  (password)  │
 │               │                 │               │                │              │
 │               │                 │               │←──Compare──────│              │
 │               │                 │               │   password     │              │
 │               │                 │               │                │              │
 │               │                 │←──LoginResult─│                │              │
 │               │                 │  (success/msg)│                │              │
 │               │                 │               │                │              │
 │               │←─JSON Response──│               │                │              │
 │               │  {success,msg}  │               │                │              │
 │               │                 │               │                │              │
 │←─Display──────│                 │               │                │              │
 │  Result       │                 │               │                │              │
```

## Testing Flow

```
TestNG Test       Selenium        React Frontend    Backend API     MySQL DB
    │                │                  │                │              │
    │──@BeforeClass──│                  │                │              │
    │  setupDatabase │                  │                │              │
    │                │                  │                │              │
    │                │                  │                │──INSERT───→ │
    │                │                  │                │  test user   │
    │                │                  │                │              │
    │──@BeforeMethod─│                  │                │              │
    │  setup()       │                  │                │              │
    │                │                  │                │              │
    │                │──Open Browser────│                │              │
    │                │  Navigate to     │                │              │
    │                │  localhost:3000  │                │              │
    │                │                  │                │              │
    │──@Test─────────│                  │                │              │
    │  testValidLogin│                  │                │              │
    │                │                  │                │              │
    │                │──Find Elements───│                │              │
    │                │  Input Credentials                │              │
    │                │                  │                │              │
    │                │──Click Submit────│                │              │
    │                │                  │                │              │
    │                │                  │──POST /login───│              │
    │                │                  │                │              │
    │                │                  │                │──Query DB──→ │
    │                │                  │                │              │
    │                │                  │                │←─Result──────│
    │                │                  │                │              │
    │                │                  │←─Response──────│              │
    │                │                  │                │              │
    │                │──Verify Success──│                │              │
    │                │  Message         │                │              │
    │                │                  │                │              │
    │←─Assert Pass───│                  │                │              │
    │                │                  │                │              │
    │──@AfterClass───│                  │                │              │
    │  cleanup       │                  │                │              │
    │                │                  │                │──CLEANUP───→ │
    │                │                  │                │  (optional)  │
```

## Database Schema Diagram

```
┌────────────────────────────────────┐
│            USER TABLE              │
├────────────────────────────────────┤
│  Column        │ Type      │ Key   │
├────────────────┼───────────┼───────┤
│  email         │ VARCHAR   │ PK    │
│  password      │ VARCHAR   │       │
│  created_at    │ TIMESTAMP │       │
│  updated_at    │ TIMESTAMP │       │
└────────────────────────────────────┘

Constraints:
- email: PRIMARY KEY (unique identifier)
- password: NOT NULL
- created_at: DEFAULT CURRENT_TIMESTAMP
- updated_at: AUTO UPDATE ON CHANGE
```

## Component Responsibilities

```
┌─────────────────────────────────────────────────────────────┐
│                     FRONTEND LAYER                          │
│  React App (App.js)                                         │
│  --------------------------------------------------------   │
│  Responsibilities:                                          │
│  • User interface rendering                                 │
│  • Form validation (client-side)                            │
│  • API request handling                                     │
│  • Error/Success message display                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   API/CONTROLLER LAYER                      │
│  LoginAPI.java (Spark Framework)                            │
│  --------------------------------------------------------   │
│  Responsibilities:                                          │
│  • HTTP request routing                                     │
│  • Request parsing (JSON)                                   │
│  • Response formatting (JSON)                               │
│  • CORS handling                                            │
│  • HTTP status codes                                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                      │
│  LoginService.java                                          │
│  --------------------------------------------------------   │
│  Responsibilities:                                          │
│  • Input validation (server-side)                           │
│  • Business rules enforcement                               │
│  • Credential verification logic                            │
│  • User registration logic                                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATA ACCESS LAYER                         │
│  DatabaseHelper.java                                        │
│  --------------------------------------------------------   │
│  Responsibilities:                                          │
│  • Database connection management                           │
│  • SQL query execution (with PreparedStatements)            │
│  • Result set processing                                    │
│  • Transaction handling                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                     PERSISTENCE LAYER                       │
│  MySQL Database                                             │
│  --------------------------------------------------------   │
│  Responsibilities:                                          │
│  • Data storage                                             │
│  • Data integrity                                           │
│  • Concurrent access handling                               │
│  • ACID transactions                                        │
└─────────────────────────────────────────────────────────────┘
```

## File Structure Tree

```
Skill Palaver Project/
│
├── Frontend_React_App/
│   ├── package.json
│   ├── public/
│   └── src/
│       └── App.js ────────────────► Updated to call API
│
└── Maven_Testing_Project/
    ├── pom.xml ───────────────────► Updated dependencies
    ├── database_setup.sql ────────► DB setup script
    ├── setup.sh ──────────────────► Automated setup
    ├── README.md ─────────────────► Full documentation
    ├── CONFIGURATION.md ──────────► Setup guide
    ├── PROJECT_SUMMARY.md ────────► Quick overview
    │
    └── src/test/java/com/logintest/
        ├── DatabaseHelper.java ───► DB connection & queries
        ├── LoginService.java ─────► Business logic
        ├── LoginAPI.java ─────────► REST API server
        ├── LoginTest.java ────────► Updated Selenium tests
        └── TestDatabaseConnection.java ─► DB test utility
```

---

## Color Legend (for printed documentation)

- **Frontend Layer**: User interface components
- **API Layer**: HTTP request handling
- **Business Layer**: Application logic
- **Data Layer**: Database operations
- **Persistence Layer**: Data storage

## Key Concepts

### Separation of Concerns
Each layer has a specific responsibility and doesn't directly interact with layers beyond its immediate neighbors.

### Security Through Layers
- Frontend: Client-side validation (UX)
- API: Request validation and authorization
- Business: Server-side validation and business rules
- Data: Prepared statements (SQL injection prevention)

### Testability
- Each layer can be tested independently
- Mock objects can replace database for unit tests
- Integration tests verify end-to-end functionality

---

For more details, see README.md and CONFIGURATION.md
