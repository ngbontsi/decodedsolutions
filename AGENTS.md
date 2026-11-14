## Goal
- Maintain a multi-service platform (six Spring Boot microservices + React frontends) with live client demos on GitHub Pages, and a backend deployment path moving to Render + Neon via Terraform.

## Constraints & Preferences
- Develop on `develop` or feature branches; never edit `gh-pages` directly
- All React apps use `HashRouter` for GitHub Pages compatibility
- npm/WSL via Ubuntu for builds; Windows PowerShell for file operations
- Client demos must remain standalone apps, not dashboard modules
- Frontend workflow: `develop` → `npm run build` → `.\deploy.ps1` (never commit dist/)
- Never claim a security control is enforced without verifying it at the layer that actually enforces it (see **Security State**)

## Branching Strategy (enforced via GitHub branch protection)
Both `main` and `develop` require pull requests — no direct pushes allowed.

### Branch hierarchy
```
main (production) ←── PR ←── develop (integration) ←── PR ←── feature/fix branches
```

### Branch types
| Branch | Purpose | Branches from | Merges to |
|--------|---------|---------------|-----------|
| `main` | Production/live code only | — | — |
| `develop` | Integration branch, all features land here | `main` | `main` via PR |
| `feature/*` | New features | `develop` | `develop` via PR |
| `fix/*` | Bug fixes | `develop` | `develop` via PR |
| `hotfix/*` | Urgent production fixes | `main` | `main` + `develop` via PRs |

### Workflow for new work
```bash
# 1. Start from latest develop
git checkout develop
git pull origin develop

# 2. Create feature/fix branch
git checkout -b feature/my-feature

# 3. Work, commit, push
git add .
git commit -m "feat: description"
git push origin feature/my-feature

# 4. Create PR: feature/my-feature → develop
# 5. After PR merges, delete the feature branch

# 6. When ready to release to production:
# Create PR: develop → main
```

### Commit message conventions
- `feat:` — new feature
- `fix:` — bug fix
- `refactor:` — code change that neither fixes a bug nor adds a feature
- `docs:` — documentation only
- `chore:` — build, CI, or tooling changes
- `style:` — formatting, no code change
- `test:` — adding or correcting tests

## Deployment Topology

| Environment | Branch | Services | Frontend |
|-------------|--------|----------|----------|
| Local | any | docker-compose, :8080-8085 | Vite dev servers |
| GitHub Pages | `gh-pages` | none | butcher-shop, guesthouse-client, dashboard, zozos-shop, vuyolwethu |
| Staging | `develop` | api-gateway, auth, restaurant, guesthouse, marketplace, **family tree** | Render static site |
| Production | `main` | api-gateway, auth, restaurant, guesthouse, marketplace (**family tree excluded**) | Render static site |

- Planned infra: `devops/terraform` — `neon_project` (aws-eu-central-1, pg16) with five databases (`authdb`, `restaurantdb`, `guesthousedb`, `marketplacedb`, `familytreedb`) + Render web service / static site.
- Terraform is **scaffolded, never applied**. `devops/terraform/main.tf` still has path and DB-wiring drift — see `business/sprints/SPRINTS.md` T-39.
- No CI/CD workflows are tracked in `.github/workflows` yet.
- Reference: `devops/Infra.md` (13 Nov 2026).

## Security State (verified 14 Nov 2026)

This section exists because the previous version of this file overclaimed. Verify before trusting.

| Claim | Verified reality |
|-------|------------------|
| PostgreSQL RLS enforced | **No.** `setup_rls.sql` has 30 `CREATE POLICY` statements and **0 `FORCE ROW LEVEL SECURITY`**. The app connects as `platformuser`, the image-created superuser and table owner, so policies are bypassed. |
| Services enforce auth | **No.** `restaurant-service`, `guesthouse-service`, `marketplace-service` all use `anyRequest().permitAll()` and trust `X-User-Id` / `X-User-Role` from the gateway. |
| Gateway is the trust boundary | Yes, by design — but it trusts inbound `X-User-*` headers and publishes 8081-8085, 5433, 6379 on the host. |
| Secrets are externalized | Partly. Env vars are used, but defaults still resolve: `${DB_USERNAME:-platformuser}` / `${DB_PASSWORD:-platformpass}`. |
| App-level ownership scoping works | Yes — `ownerId` filtering + ADMIN bypass in services/controllers. This is the only enforced layer today. |

Tracked as S-1…S-6 on the board; see `business/sprints/SPRINTS.md`.

## Progress
### Done
- Added Swagger/OpenAPI annotations (`@Tag`, `@Operation`, `@ApiResponses`) to all 11 controllers across 4 services (auth: AuthController, AuditController, RoleController; restaurant: RestaurantController; guesthouse: GuesthouseController, RoomController, ReservationController; marketplace: ProductController, CartController, OrderController, CategoryController)
- Each controller has `@Tag` at class level, `@Operation(summary=..., description=...)` on each method, and `@ApiResponses` documenting response codes with `@Schema` references to DTOs
- All 4 services return valid OpenAPI JSON at `/v3/api-docs` (verified inside Docker containers)
- Swagger UI available at `http://localhost:8080/swagger-ui.html` (via gateway) — each service's springdoc auto-configures without explicit SecurityConfig overrides
- Replaced hardcoded JWT/DB secrets with `${ENV_VAR}` references, created `.env.example`
- Restructured monorepo into `backend/`, `frontend/`, `devops/`
- Built internal admin dashboard (`decoded_dashboard`) with React/Vite, deployed to `https://ngbontsi.github.io/decodedsolutions/dashboard/`
- Converted static marketing site to Vite + React + TS (separate repo: `ngbontsi/decodedsolutionsite`)
- Created portfolio page on website with 3 live demo cards (butcher shop, guesthouse, dashboard)
- Built and deployed butcher shop client (`butcher-shop/`) with catalog, cart, wishlist, 2-step checkout → `https://ngbontsi.github.io/decodedsolutions/butcher-shop/`
- Built and deployed guesthouse client (`guesthouse-client/`) with property browsing, room selection, date/guest picker, booking confirmation, "My Bookings" tracker → `https://ngbontsi.github.io/decodedsolutions/guesthouse-client/`
- Built and deployed `zozos-shop/` and `vuyolwethu/` → `https://ngbontsi.github.io/decodedsolutions/zozos-shop/` and `/vuyolwethu/`
- Removed guesthouse monitoring pages from dashboard
- Fixed Vite base paths for all apps to match GitHub Pages URL structure
- Cleared merge conflicts, synced `develop` and `main`, deleted `master` branch
- Created `deploy.ps1` and `deploy.sh` — single-command rebuild + deploy for all apps
- Identified GitHub Pages for `decodedsolutionsite` was pointing at `master` branch instead of `gh-pages` (this caused the blank page)
- Fixed all backend Dockerfiles to use Java 21 (was Java 17) — now six services
- Externalized secrets from all application.yml files (JWT, DB credentials) into env vars
- Deleted stale `feature/decoded_dashboard` branch locally
- Cleaned gh-pages: removed leaked source files (workflows, Flutter configs, backend source, .env.example)
- Added register page to dashboard (`RegisterPage.tsx`) with name, email, password, role fields
- Added `register` function to `AuthContext` — calls `POST /api/auth/register`, auto-logs in after registration
- Updated `LoginPage.tsx` with "Don't have an account?" link to `/register`
- Updated `App.tsx` with `/register` route
- Added CSS styles for form-row, select, and register-link
- Deleted `task-api` from source, docker-compose, gateway routes, and DB init (taskdb removed)
- Added `ownerId` columns to Restaurant, MenuItem, Guesthouse, Room, Product entities
- Updated all controllers and services to read `X-User-Id` and `X-User-Role` headers for ownership scoping
- Added admin bypass: ADMIN role sees all data, others scoped to their own `ownerId`
- Built and injected all JARs into running containers (api-gateway, restaurant, guesthouse, marketplace)
- Verified **application-level** ownership scoping end-to-end: create scoped to caller, list filtered by ownerId, writes enforce ownership. (Database-level RLS remains inert — see Security State)
- Updated OFFLINE.md with task-api removal and RLS documentation
- **Family tree service Phase 1** — `backend/family-tree-service` on :8085, isolated DB, 9 Flyway migrations in `devops/db/schema/` (23 tables + 1 view), 45 endpoints, 18 tests green against real Postgres, `BetaGateFilter` for the private beta
- **Family tree frontend** — `frontend/family-tree` (Vite + React, :5174) reads and writes the API
- **Terraform scaffolding** — `devops/terraform` provisions Neon Postgres (5 databases) + Render web/static services; `environments/{dev,staging,prod}` scaffolded but empty; never applied
- **Deployment standards** — all six Dockerfiles on Java 21, per-service `.dockerignore`, `.gitignore` excludes Terraform providers/state, removed committed provider binaries
- **Board reconciliation (14 Nov 2026)** — board and `business/sprints/SPRINTS.md` realigned; added T-35…T-39; corrected RLS, demo-deployment, and service-count claims

## Mock Data Status
| App | Data Source | Real Backend? |
|-----|------------|---------------|
| **butcher-shop** | `src/data/products.ts` (static import) | No — 100% mock |
| **guesthouse-client** | `src/data/properties.ts` (static import) | No — 100% mock |
| **decoded_dashboard** | Inline mock data per page | No — 100% mock |
| **zozos-shop** | Static content | No — 100% mock |
| **vuyolwethu** | Static content | No — 100% mock |
| **family-tree** | Live API (`family-tree-service` :8085) | **Yes** |

### In Progress
- Family tree beta deployment (staging only, excluded from prod) — board T-36
- Terraform → Render + Neon rollout — board T-37, T-39
- Fixing `gh-pages` root: it still boots the butcher-shop bundle instead of a demo hub — board T-34 (#88)
- Butcher shop needs real product content populated from user's hard-copy documents
- User to provide product data via voice-to-text or spreadsheet template

### Done (PostgreSQL Native RLS — **installed, not enforced**)
- Created `devops/db/rls/setup_rls.sql` — PostgreSQL Row-Level Security policies for all 4 databases
  - Every table gets: `admin_all` (ADMIN bypass) + `user_own` (scoped to `owner_id` or `user_id`)
  - Categories table: `public_read` for shared reference data
  - Cart items / Order items: subquery-based RLS via parent cart/order
  - Child entities (cart_items, order_items): RLS via `EXISTS` subquery against parent
- Created `UserContext.java` (ThreadLocal) + `UserContextFilter.java` (reads `X-User-Id`/`X-User-Role` headers) in all 4 services
- Created `RlsAspect.java` — AOP `@Before` advice on all repository methods, calls `SELECT set_config('app.current_user_id', ...)` to set PostgreSQL session parameters before every query
- Added `spring-boot-starter-aop` to all 4 service pom.xml files
- Frontend: Added role-based route guards (`App.tsx` — `ProtectedRoute` with `allowedRoles`)
- Frontend: Sidebar now filters nav items by user role (Admin sees all; others see subset)
- Frontend: `EntityDataPage.tsx` hides Add/Edit/Delete buttons for non-privileged roles
- Frontend: Created `hooks/useAuthorization.ts` — reusable role-checking hook
- **Missing for enforcement:** a non-owner application role, `ALTER TABLE ... FORCE ROW LEVEL SECURITY`, and a two-user verification pass

### Blocked
- RLS enforcement — blocked on DB role separation + `FORCE ROW LEVEL SECURITY` (S-2/S-3)
- Family tree beta + prod deployment — blocked on the Terraform/Render pipeline (T-36/T-37)
- Butcher Shop mobile work — blocked on client deposit (board #70)

## Key Decisions
- `HashRouter` across all SPAs to avoid 404 on GitHub Pages static hosting
- Client demos (butcher, guesthouse) kept as standalone apps, not embedded in dashboard
- Website project lives in its own repo (`decodedsolutionsite`) with its own deployment pipeline
- Build artifacts (dist/) never committed to source branches — only deployed via gh-pages
- All frontend deployment automated via `deploy.ps1`/`deploy.sh` to avoid manual branch switching
- Dockerfiles standardized on Java 21 to match pom.xml target
- Secrets externalized to env vars — but defaults must be removed before production
- **Ownership scoping is dual-approach**: application-level `ownerId` checks (enforced today) + PostgreSQL native policies (installed but inert). Do not describe the database layer as authoritative until FORCE RLS is on and the app role is a non-owner.
- **RLS session propagation**: AOP aspect calls `set_config()` before every `repository.*()` call. `SET` is session-scoped and transaction-safe via the `true` (local) parameter.
- **Admin bypass**: Separate PostgreSQL policy `admin_all` checks `app.current_user_role = 'ADMIN'`.
- **Frontend role gating**: Three-tier: (1) Route-level guard → (2) Nav filter → (3) Button-level hide. Frontend gating is UX only.
- **JAR inject workflow**: Build with WSL `mvn` → `docker cp` → restart, avoids full image rebuild for fast iteration.
- **Terraform is the deployment path going forward** (commit 2ff3984, 14 Nov 2026): Render + Neon, one `stage_name` per environment, `develop` → staging, `main` → production.
- **Family tree stays in the monorepo** but deploys to staging only for now, with its own isolated database.
- **Six services, not five** — any doc saying five is stale (`family-tree-service` on :8085).

## Next Steps
1. Turn RLS on for real: create a non-owner app role, `ALTER TABLE ... FORCE ROW LEVEL SECURITY`, re-run `setup_rls.sql`, verify with two different users
2. Stop trusting `X-User-*` at the edge: strip inbound headers at the gateway, add an internal shared token, look up roles from the DB
3. Fix Terraform drift (paths `Backend/` → `backend/`, nonexistent `frontend/client-portal`, five databases on one datasource)
4. Add CI/CD workflows for staging (`develop`) and production (`main`)
5. Fix the `gh-pages` root so it serves a demo hub, not the butcher-shop bundle; decide whether `portfolio` stays in `deploy.ps1`
6. Provide butcher shop product data (voice-to-text or CSV template), update `products.ts`, rebuild via `.\deploy.ps1`
7. Change `decodedsolutionsite` Pages source from `master` to `gh-pages` in repo settings
8. (Future) Wire remaining frontend demos to real Spring Boot APIs

## Critical Context
- Backend: **six** Spring Boot microservices — api-gateway (:8080), auth (:8081), restaurant (:8082), guesthouse (:8083), marketplace (:8084), family-tree (:8085); docker-compose also publishes Postgres :5433 and Redis :6379
- Family tree service is self-contained: own DB, `BetaGateFilter`, runs locally via `backend/family-tree-service/run.ps1`
- Auth-service has a Keycloak dependency but no Keycloak docker-compose or config found
- WSL mount used previously: `/mnt/c/Users/admin/Desktop/decoded\ solution\ platform/platform/` (path has since moved to `Projects/decoded-platform`)
- `.git/index.lock` may stall git operations; delete with `Remove-Item -Force .git/index.lock`
- gh-pages serves: `index.html` (root — currently the butcher-shop app, **not** a hub), `butcher-shop/`, `guesthouse-client/`, `dashboard/`, `zozos-shop/`, `vuyolwethu/`
- `deploy.ps1` app list still includes `portfolio` → `freelance-work`, even though the linked portfolio is the standalone `ngbontsi.github.io/freelance-work/`
- **Ownership flow**: HTTP request → gateway JWT filter → `UserContextFilter` reads headers → ThreadLocal → `RlsAspect` calls `set_config()` before each repository call → app-level `ownerId` filters (enforced) → PostgreSQL policies (currently bypassed)

## Relevant Files
- `devops/docker-compose.yml`: 6 services, Postgres + Redis, env-var secrets with defaults
- `devops/Infra.md`: deployment lifecycle plan (branch → environment mapping, safety rules)
- `devops/terraform/main.tf`: Neon project + 5 databases + Render web/static services (drifted — T-39)
- `devops/terraform/environments/{dev,staging,prod}/`: empty scaffolds
- `devops/db/rls/setup_rls.sql`: 30 policies, no `FORCE ROW LEVEL SECURITY`
- `devops/db/schema/`: family tree Flyway migrations V1–V9 + `R__views.sql`
- `.env.example`: Template for `JWT_SECRET`, `DB_USERNAME`, `DB_PASSWORD`
- `frontend/decoded_dashboard/`: Admin dashboard (React/Vite/HashRouter, deployed `/dashboard/`)
- `frontend/decoded_dashboard/src/App.tsx`: Role-based route guards with `ProtectedRoute`
- `frontend/decoded_dashboard/src/hooks/useAuthorization.ts`: Reusable role-checking hook
- `frontend/decoded_dashboard/src/components/layout/DashboardLayout.tsx`: Role-filtered navigation
- `frontend/decoded_dashboard/src/pages/data/EntityDataPage.tsx`: Role-based CRUD button visibility
- `frontend/butcher-shop/`: Butcher e-commerce demo (cart/wishlist/checkout, deployed `/butcher-shop/`)
- `frontend/butcher-shop/src/data/products.ts`: Mock product data — needs real content
- `frontend/butcher-shop/src/types/index.ts`: `Product` and `CartItem` types
- `frontend/guesthouse-client/`: Guesthouse booking demo (rooms/dates/bookings, deployed `/guesthouse-client/`)
- `frontend/zozos-shop/`, `frontend/vuyolwethu/`: Client demos (deployed)
- `frontend/family-tree/`: Family tree UI (Vite + React, :5174) — talks to the live API
- `backend/family-tree-service/`: Family tree API (:8085), 45 endpoints, `run.ps1`
- `deploy.ps1` / `deploy.sh`: Unified frontend deployment scripts
- `backend/*/Dockerfile`: `eclipse-temurin:21-jdk-alpine` / `21-jre-alpine`
- `backend/*/src/main/resources/application.yml`: Secrets use `${ENV_VAR:default}` pattern
- `backend/*/src/main/java/**/config/UserContext.java`: ThreadLocal holder (userId, userRole, userEmail)
- `backend/*/src/main/java/**/config/UserContextFilter.java`: Servlet Filter reading X-User-* headers
- `backend/*/src/main/java/**/config/RlsAspect.java`: AOP aspect setting PostgreSQL session params
- `backend/*/src/main/java/**/config/SecurityConfig.java`: `anyRequest().permitAll()` in restaurant/guesthouse/marketplace
- `backend/api-gateway/src/main/java/com/platform/gateway/config/JwtAuthenticationFilter.java`: public paths + public GET prefixes
- `backend/*/pom.xml`: Includes `spring-boot-starter-aop`
- `backend/*/src/main/java/**/controller/*.java`: Controllers read X-User-Id/X-User-Role; 11 annotated with Swagger
- `OFFLINE.md`: Offline reference (partly stale — see SPRINTS.md documentation debt)
- `../business/sprints/SPRINTS.md`: Shared sprint board, reconciled 14 Nov 2026
