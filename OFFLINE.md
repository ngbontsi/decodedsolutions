# Offline Development Reference

## System Overview

| Layer | Tech | Location |
|-------|------|----------|
| Backend | Spring Boot 3.4 + Java 21 | `backend/` (6 microservices) |
| Frontend | React 19 + Vite 6 + TypeScript | `frontend/` (7 apps) |
| Database | PostgreSQL 16 (Neon) + Redis 7 | Docker containers (local) / Neon (staging/prod) |
| Build | Maven 3.9 (WSL) / npm 11 (Windows) | WSL Ubuntu 24.04 |
| IDE | IntelliJ IDEA 2024+ | Windows |
| CI/CD | GitHub Actions + Terraform | `.github/workflows/` + `devops/terraform/` |

---

## 1. Starting Everything (No Network Required)

### 1.1 Start Containers
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform\devops
docker compose up -d
```

All 8 containers start in ~30 seconds:

| Container | Port | Purpose |
|-----------|------|---------|
| `decoded-platform-db-1` | 5433 (->5432) | PostgreSQL (5 databases) |
| `decoded-platform-redis-1` | 6379 | Redis cache |
| `decoded-platform-api-gateway-1` | 8080 | API gateway |
| `decoded-platform-auth-service-1` | 8081 | Auth + users + roles + audit |
| `decoded-platform-restaurant-service-1` | 8082 | Restaurant API |
| `decoded-platform-guesthouse-service-1` | 8083 | Guesthouse API |
| `decoded-platform-marketplace-service-1` | 8084 | Marketplace API |
| `decoded-platform-familytree-service-1` | 8085 | Family Tree API (beta) |

### 1.2 Verify All Running
```powershell
docker ps --format "table {{.Names}}\t{{.Status}}"
```

### 1.3 Check Logs (if something fails)
```powershell
docker logs decoded-platform-auth-service-1 --tail 30
docker logs decoded-platform-db-1 --tail 10
```

### 1.4 Stop Everything
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform\devops
docker compose down
```
Add `-v` to also delete database data (starts fresh):
```powershell
docker compose down -v
```

---

## 2. API Gateway (localhost:8080)

### 2.1 Public Endpoints (No Token)
- `POST /api/auth/login` — Login, returns JWT
- `POST /api/auth/register` — Register new user
- `POST /api/auth/refresh` — Refresh token

### 2.2 Quick Login (PowerShell)
```powershell
$token = (Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@decoded.com","password":"admin123"}').token
```

### 2.3 Authenticated Requests
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/roles" -Headers @{Authorization = "Bearer $token"}
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/users" -Headers @{Authorization = "Bearer $token"}
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/audit?page=0&size=10" -Headers @{Authorization = "Bearer $token"}
```

---

## 3. Database (PostgreSQL)

### 3.1 Connection Details
- **Host port**: 5433 (localhost)
- **Superuser**: `platformuser` (from `.env` DB_USERNAME)
- **Superuser password**: from `.env` DB_PASSWORD
- **Service roles**: `auth_app`, `restaurant_app`, `guesthouse_app`, `marketplace_app`, `familytree_app` (created by database-provisioner)

### 3.2 View Databases
```powershell
docker exec decoded-platform-db-1 psql -U platformuser -d postgres -c "\l"
```

### 3.3 Connect to a Database
```powershell
# As superuser
docker exec -it decoded-platform-db-1 psql -U platformuser -d authdb

# As service role (for RLS testing)
docker exec -it decoded-platform-db-1 psql -U restaurant_app -d restaurantdb
```

### 3.4 Useful psql Commands (inside psql)
```sql
\d     -- list tables
\d+ table_name  -- describe table
SELECT * FROM users;
SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 10;
SELECT * FROM roles;
```

### 3.5 Databases & Tables (6 services, 5 databases + family tree)

| Database | Service Role | Key Tables |
|----------|--------------|------------|
| `authdb` | `auth_app` | `users`, `roles`, `audit_logs`, `flyway_schema_history` |
| `restaurantdb` | `restaurant_app` | `restaurants`, `menu_items`, `categories` |
| `guesthousedb` | `guesthouse_app` | `guesthouses`, `rooms`, `reservations` |
| `marketplacedb` | `marketplace_app` | `products`, `categories`, `carts`, `cart_items`, `orders`, `order_items` |
| `familytreedb` | `familytree_app` | 23 tables + 1 view (persons, families, events, sources, etc.) |

### 3.6 List Tables for All DBs
```powershell
foreach ($db in @("authdb","restaurantdb","guesthousedb","marketplacedb","familytreedb")) {
    Write-Output "=== $db ==="
    docker exec decoded-platform-db-1 psql -U platformuser -d $db -t -A -c "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE' ORDER BY table_name;"
}
```

### 3.7 Count All Tables
```powershell
$total=0; foreach ($db in @("authdb","restaurantdb","guesthousedb","marketplacedb","familytreedb")) { $c=docker exec decoded-platform-db-1 psql -U platformuser -d $db -t -A -c "SELECT count(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';"; $total+=[int]$c; Write-Output "$db : $c" }; Write-Output "TOTAL : $total"
```

### 3.8 Reset Database (Fresh Start)
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform\devops
docker compose down -v
docker compose up -d
```
This deletes all data and volumes. Flyway re-runs migrations in each service. Database-provisioner creates 5 databases with per-service roles.

---

## 4. IntelliJ IDEA

### 4.1 Opening the Project
1. Open IntelliJ IDEA
2. `File > Open` → select `C:\Users\admin\Desktop\Projects\decoded-platform\`
3. IntelliJ detects the Maven `pom.xml` files and configures automatically

### 4.2 JDK Setup
- **Project SDK**: Java 21 (Temurin) — `File > Project Structure > Project SDK`
- **Each backend module**: `File > Project Structure > Modules` → ensure Java 21 target (matches Dockerfiles)
- **WSL build**: Uses Java 21 via `./mvnw.cmd` (handles JDK version)

### 4.3 Running Backend Services in IntelliJ
Each service has a `main` class in `backend/<service>/src/main/java/com/platform/<service>/`.
- Click the green play icon next to `main()` to run
- Or create Run Configurations with:
  - **Working directory**: `backend/<service>`
  - **Active profiles**: `local` (or leave blank)
  - **Environment variables** (from `.env`): `JWT_SECRET`, `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/authdb`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `INTERNAL_SHARED_SECRET`

### 4.4 Maven Build in IntelliJ
- **Maven tool window** (right sidebar) → `Lifecycle > package`
- Or: `Ctrl+Shift+F10` on `pom.xml`
- **WARNING**: IntelliJ uses Project SDK. For Docker deployment, build with WSL `mvnw` instead (see section 6).

### 4.5 Database Tool Window (IntelliJ Ultimate)
- `View > Tool Windows > Database`
- `+ > PostgreSQL`
- Host: `localhost`, Port: `5433`, User: `platformuser`, Password: (from `.env`), Database: `authdb`
- Click "Test Connection" — no driver download needed offline (IntelliJ bundles it)

### 4.6 Useful Plugins (Pre-installed)
- Spring Boot — detects `@RestController`, `@Service`, `application.yml`
- JPA Buddy — visualises `@Entity` schemas
- Database Tools and SQL — built-in pgAdmin replacement

---

## 5. Building & Injecting JARs (Backend Changes)

### 5.1 The Problem
- JARs must be built with Java 21 (matching the Docker containers' JDK)
- Windows has Java 26, so we use WSL (Java 21 + Maven 3.9) via `mvnw.cmd`

### 5.2 Build a Service
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform

# Build all services:
.\mvnw.cmd clean package -DskipTests

# Build a single service:
.\mvnw.cmd clean package -DskipTests -pl backend/auth-service -am
```

### 5.3 Inject JAR into Running Container
```powershell
# Copy built JAR into container
docker cp backend/auth-service/target/auth-service-*.jar decoded-platform-auth-service-1:/app/app.jar

# Inject into guesthouse:
docker cp backend/guesthouse-service/target/guesthouse-service-*.jar decoded-platform-guesthouse-service-1:/app/app.jar

# Restart the service to pick up changes
docker compose -f devops/docker-compose.yml restart auth-service
```

### 5.4 Verify the New JAR is Running
```powershell
curl -s http://localhost:8080/api/auth/roles -H "Authorization: Bearer $token"
```
Or check the startup log:
```powershell
docker logs decoded-platform-auth-service-1 --tail 5
```

### 5.5 Full Rebuild & Restart (All Services)
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform
.\mvnw.cmd clean package -DskipTests

$services = @{
    "api-gateway" = "decoded-platform-api-gateway-1"
    "auth-service" = "decoded-platform-auth-service-1"
    "restaurant-service" = "decoded-platform-restaurant-service-1"
    "guesthouse-service" = "decoded-platform-guesthouse-service-1"
    "marketplace-service" = "decoded-platform-marketplace-service-1"
    "family-tree-service" = "decoded-platform-familytree-service-1"
}

$services.GetEnumerator() | ForEach-Object {
    Write-Output "Copying $($_.Key)..."
    docker cp "backend/$($_.Key)/target/$($_.Key)-*.jar" "$($_.Value):/app/app.jar"
}

docker compose -f devops/docker-compose.yml restart
```

---

## 6. Frontend Development

### 6.1 Start Dev Server (Dashboard)
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform\frontend\decoded_dashboard
npm install
npm run dev
```
Opens at: `http://localhost:5173/` (or next available port). Proxies `/api` requests to `localhost:8080` (the API gateway).

### 6.2 Build for Production
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform\frontend\decoded_dashboard
npm run build
```
Output goes to `dist/` directory (not committed to git).

### 6.3 All Frontend Apps (7 total)

| App | Path | Dev Command | Deployed URL |
|-----|------|-------------|--------------|
| Decoded Dashboard | `frontend/decoded_dashboard/` | `npm run dev` | `/dashboard/` |
| Butcher Shop | `frontend/butcher-shop/` | `npm run dev` | `/butcher-shop/` |
| Guesthouse Client | `frontend/guesthouse-client/` | `npm run dev` | `/guesthouse-client/` |
| Zozo's Shop | `frontend/zozos-shop/` | `npm run dev` | `/zozos-shop/` |
| Vuyolwethu | `frontend/vuyolwethu/` | `npm run dev` | `/vuyolwethu/` |
| Portfolio | `frontend/portfolio/` | `npm run dev` | `/freelance-work/` |
| Family Tree | `frontend/family-tree/` | `npm run dev` | `/family-tree/` |

**Note**: Only Family Tree connects to live backend API. Others use mock data from `src/data/` files.

---

## 7. Deployment

### 7.1 Local → GitHub Pages (Frontend Demos)
```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform
.\deploy.ps1
```
This builds all 7 frontends and publishes to `gh-pages` branch. Never commit `dist/` folders.

### 7.2 Staging/Production (Render + Neon via Terraform)
- **Staging**: Auto-deploys on merge to `develop` (after CI passes)
- **Production**: Manual approval required on merge to `main`
- **Infrastructure**: `devops/terraform/modules/platform/` + `environments/{staging,prod}/`
- **CD Workflow**: `.github/workflows/cd.yml` (runs after CI `ci-gate` succeeds)

### 7.3 Required GitHub Secrets (for CD)
| Environment | Secrets |
|-------------|---------|
| `staging` | `NEON_API_KEY`, `RENDER_API_KEY`, `RENDER_OWNER_ID`, `JWT_SECRET`, `INTERNAL_SHARED_SECRET` |
| `prod` | Same as staging (DIFFERENT values — never reuse) |

---

## 8. Sample Users (Pre-Seeded)

| Name | Email | Password | Role |
|------|-------|----------|------|
| Admin User | admin@decoded.com | admin123 | ADMIN |
| Sipho Mokoena | sipho@restaurant.co.za | pass123 | RESTAURANT_OWNER |
| Thandi Nkosi | thandi@guesthouse.co.za | pass123 | GUESTHOUSE_OWNER |
| Lindiwe Zulu | lindiwe@marketplace.co.za | pass123 | MARKETPLACE_VENDOR |
| James Petersen | james@customer.co.za | pass123 | USER |

Admin can change roles and enable/disable users at: `http://localhost:5173/app/users`

---

## 9. Quick Troubleshooting

### Containers won't start
```powershell
docker compose -f devops/docker-compose.yml logs --tail 50
```
Common cause: port conflict (5433, 8080-8085, 6379 already in use). Check with `netstat -ano | findstr :5433`.

### DB connection refused
```powershell
docker exec -it decoded-platform-db-1 psql -U platformuser -d authdb -c "SELECT 1"
```
If this fails, the DB isn't ready yet. Wait 10s and retry.

### Flyway migration errors
```powershell
docker exec decoded-platform-db-1 psql -U platformuser -d authdb -c "SELECT version, success FROM flyway_schema_history ORDER BY installed_rank"
```
If a migration failed, you may need to delete and recreate the DB volume:
```powershell
docker compose -f devops/docker-compose.yml down -v && docker compose -f devops/docker-compose.yml up -d
```

### Vite dev server fails
If `npm run dev` gives binding errors, build via WSL:
```powershell
wsl -d Ubuntu bash -c 'cd /mnt/c/Users/admin/Desktop/Projects/decoded-platform/frontend/decoded_dashboard && npm install && npm run build'
```

### Git is stuck (index.lock)
```powershell
Remove-Item -Force .git/index.lock
```

### Maven wrapper not working
Ensure WSL Ubuntu is running:
```powershell
wsl -d Ubuntu -- bash -c "mvn --version"
```

---

## 10. Row-Level Security (RLS) — Current State

All entity `ownerId` fields are enforced at the **application layer** (verified).

### 10.1 Owner Fields Added

| Service | Entity | Field |
|---------|--------|-------|
| `restaurant-service` | `Restaurant` | `ownerId` |
| `restaurant-service` | `MenuItem` | `ownerId` |
| `guesthouse-service` | `Guesthouse` | `ownerId` |
| `guesthouse-service` | `Room` | `ownerId` |
| `guesthouse-service` | `Reservation` | `userId` (customer) |
| `marketplace-service` | `Product` | `ownerId` |
| `marketplace-service` | `Cart` | `userId` (customer) |
| `marketplace-service` | `Order` | `userId` (customer) |

### 10.2 Enforcement Pattern

- **Create**: `ownerId` set from `X-User-Id` header (forwarded by gateway)
- **Read**: Queries filtered by `ownerId`; `ADMIN` role sees all
- **Update/Delete**: Ownership verified before mutation; `ADMIN` bypasses check
- **Headers used**: `X-User-Id`, `X-User-Role`, `X-User-Email` (set by `JwtAuthenticationFilter`)

### 10.3 Database-level RLS: **installed but NOT enforced** (verified 14 Nov 2026)

`devops/db/rls/setup_rls.sql` defines 30 policies, but the database does not apply them:

- No `ALTER TABLE ... FORCE ROW LEVEL SECURITY` anywhere in the file (0 occurrences)
- The app connects as `platformuser`, the Postgres image's superuser and the table owner
- Table owners bypass non-forced RLS; superusers bypass it entirely

**So only the application-layer `ownerId` checks in 10.2 are actually enforced.** To make the database layer real: create a non-owner application role, `ALTER TABLE ... FORCE ROW LEVEL SECURITY` on every table, and verify with two different users. Tracked as S-2/S-3 in `../business/sprints/SPRINTS.md`.

Related: `restaurant-service`, `guesthouse-service` and `marketplace-service` all use `anyRequest().permitAll()` and trust the `X-User-*` headers forwarded by the gateway, and the gateway publishes 8081-8085 plus 5433/6379 on the host.

---

## 11. Commands Cheat Sheet

| Action | Command |
|--------|---------|
| Start everything | `cd devops && docker compose up -d` |
| Stop everything | `cd devops && docker compose down` |
| Reset DB to fresh | `cd devops && docker compose down -v && docker compose up -d` |
| Login as admin | `Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@decoded.com","password":"admin123"}'` |
| Build all JARs | `.\mvnw.cmd clean package -DskipTests` |
| Build one JAR | `.\mvnw.cmd clean package -DskipTests -pl backend/auth-service -am` |
| Inject JAR into container | `docker cp backend/auth-service/target/auth-service-*.jar decoded-platform-auth-service-1:/app/app.jar` |
| Restart a service | `docker compose -f devops/docker-compose.yml restart auth-service` |
| Start dashboard dev | `cd frontend/decoded_dashboard && npm run dev` |
| Deploy all to Pages | `.\deploy.ps1` |
| List all DB tables | See section 3.6 |
| Open IntelliJ | `& "C:\Program Files\JetBrains\IntelliJ IDEA 2024.3\bin\idea64.exe"` |

---

## 12. Key Documentation References

| Document | Purpose |
|----------|---------|
| `AGENTS.md` | Platform operating manual (tech team) |
| `business/sprints/SPRINTS.md` | Sprint board (source of truth) |
| `business/docs/developer-docs/environment-setup/README.md` | Full environment setup guide |
| `business/docs/developer-docs/environment-setup/QUICKSTART.md` | 5-minute setup cheat sheet |
| `devops/Infra.md` | Infrastructure lifecycle plan (partly superseded by Terraform) |
| `devops/db/rls/setup_rls.sql` | RLS policies (installed, not enforced) |
| `.github/workflows/ci.yml` | CI pipeline (build, test, policy) |
| `.github/workflows/cd.yml` | CD pipeline (Terraform deploy) |
| `.env.example` | Local secrets template |
| `OFFLINE.md` | This file |