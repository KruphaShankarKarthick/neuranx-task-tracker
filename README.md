# Task Tracker — Setup & Run Guide

A full-stack Task Tracker built with **Spring Boot 3 + React 18 + PostgreSQL**.

---

## Method 1: Without Docker (Manual Setup)

### Step 1 — Install Prerequisites

You need four things installed. Check each one first:

```powershell
java -version     # Need Java 17 or higher
mvn -version      # Need Maven 3.x
node -v           # Need Node 18 or higher
psql -U postgres  # Need PostgreSQL 16
```

If any of these are missing, install them:

| Tool | Download |
|------|----------|
| Java 17 | https://adoptium.net — click Temurin 17 |
| Maven | https://maven.apache.org/download.cgi |
| Node | https://nodejs.org — click LTS |
| PostgreSQL | https://www.postgresql.org/download/windows — set password to `postgres` during install |

> **Maven PATH tip (Windows):** After installing Maven, add `C:\Program Files\apache-maven-x.x.x\bin` to your System PATH manually via System Properties → Environment Variables.

> **PostgreSQL PATH tip (Windows):** Add `C:\Program Files\PostgreSQL\16\bin` to your System PATH so `psql` works in PowerShell.

---

### Step 2 — Start PostgreSQL

**Windows (PowerShell as Administrator):**
```powershell
net start postgresql-16
```

If the service name is different, find it with:
```powershell
Get-Service | Where-Object {$_.Name -like "*postgres*"}
```
Then use the name it shows.

**Mac/Linux:**
```bash
brew services start postgresql@16    # Mac
sudo service postgresql start        # Linux
```

---

### Step 3 — Create the Database

```powershell
psql -U postgres -c "CREATE DATABASE tasktracker;"
```

Enter password `postgres` when prompted.

If the database already exists, that's fine — skip this step.

---

### Step 4 — Run the Database Migration

The app uses Flyway to create tables automatically on first run. But if Flyway fails (you'll see `missing table [projects]` in the error), run the migration manually:

```powershell
psql -U postgres -d tasktracker -f "path\to\neuranx-task-tracker\backend\src\main\resources\db\migration\V1__init_schema.sql"
```

Replace `path\to` with the actual folder where you unzipped the project.

> **This is the most common issue.** If the backend crashes on startup, run the SQL file above and try again.

---

### Step 5 — Start the Backend

Open a **new PowerShell window**:

```powershell
cd path\to\neuranx-task-tracker\backend
mvn spring-boot:run
```

Wait until you see:
```
Started TaskTrackerApplication in X.X seconds
```

The API is now running at `http://localhost:8080`.

---

### Step 6 — Start the Frontend

Open **another new PowerShell window**:

```powershell
cd path\to\neuranx-task-tracker\frontend
npm install
npm start
```

The app opens automatically at `http://localhost:3000`.

> **Disk space error during npm install?** Run `npm cache clean --force` first, then try again. You need at least 500MB free.

---

### What Should Be Running

You need all three active at the same time:

| Service | How to start | URL |
|---------|-------------|-----|
| PostgreSQL | `net start postgresql-16` | — |
| Backend | `mvn spring-boot:run` in `/backend` | http://localhost:8080 |
| Frontend | `npm start` in `/frontend` | http://localhost:3000 |

---

## Method 2: Docker Compose (Easier — one command)

### Step 1 — Install Docker Desktop

Download from https://www.docker.com/products/docker-desktop and run the installer.

After install, **restart your PC**.

> **If the installer fails with error 1603:** Try restarting first and running the installer again. If it still fails, download the `.exe` directly from the Docker website instead of using Chocolatey.

---

### Step 2 — Start Docker Desktop

Open Docker Desktop from the Start menu. Wait until the bottom-left shows:

```
Engine running
```

This can take 1–2 minutes. Don't run the next command until you see this.

> **WSL 2 error?** Docker Desktop may prompt you to install WSL 2. Click the link it provides and follow the steps — it's a one-time setup.

---

### Step 3 — Run the Project

```powershell
cd path\to\neuranx-task-tracker
docker compose up --build
```

This automatically:
- Starts a PostgreSQL container
- Runs the database migration
- Builds and starts the Spring Boot API
- Builds and starts the React frontend

First build takes 3–5 minutes. Subsequent runs are faster.

| Service | URL |
|---------|-----|
| App | http://localhost:3000 |
| API | http://localhost:8080/api |
| Swagger | http://localhost:8080/swagger-ui.html |

To stop everything: `Ctrl+C` then `docker compose down`

---

## Running the Tests

No database needed — tests use H2 in-memory automatically.

```powershell
cd path\to\neuranx-task-tracker\backend
mvn test
```

You should see `BUILD SUCCESS` with all tests passing.

---

## Troubleshooting

### `psql` is not recognized
PostgreSQL bin folder is not in your PATH.
Fix: Add `C:\Program Files\PostgreSQL\16\bin` to System PATH → restart PowerShell.

### `mvn` is not recognized
Maven bin folder is not in your PATH.
Fix: Add `C:\Program Files\apache-maven-X.X.X\bin` to System PATH → restart PowerShell.

### Backend crashes: `missing table [projects]`
Flyway migration didn't run. Fix:
```powershell
psql -U postgres -d tasktracker -f "backend\src\main\resources\db\migration\V1__init_schema.sql"
```
Then restart the backend.

### Backend crashes: `Connection refused` on port 5432
PostgreSQL is not running. Fix:
```powershell
net start postgresql-16
```

### `npm install` fails with `ENOSPC: no space left on device`
Your disk is full. Free up space:
```powershell
npm cache clean --force
cleanmgr /d C
```
Then retry `npm install`.

### Docker: `500 Internal Server Error` on compose up
Docker Desktop engine isn't fully started yet. Open Docker Desktop, wait for "Engine running", then retry.

### Frontend shows "Could not save task"
The backend is not running. Make sure `mvn spring-boot:run` is active in a separate PowerShell window.

---

## Project Structure

```
neuranx-task-tracker/
├── backend/                  Spring Boot 3 API (Java 17)
│   ├── src/main/java/        Entities, repositories, services, controllers
│   ├── src/main/resources/
│   │   └── db/migration/     V1__init_schema.sql  ← run this if Flyway fails
│   └── src/test/java/        Unit + integration tests
├── frontend/                 React 18 SPA
│   └── src/
├── docker-compose.yml        One-command full stack run
└── README.md                 This file
```

---

## API Quick Reference

| Method | Endpoint | What it does |
|--------|----------|-------------|
| GET | `/api/tasks` | List tasks (supports `?status=TODO&priority=HIGH&page=0&size=20&sortBy=dueDate`) |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/projects` | List projects |
| POST | `/api/projects` | Create project |

Full interactive docs at `http://localhost:8080/swagger-ui.html` when the backend is running.
