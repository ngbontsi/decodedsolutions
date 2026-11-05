## Goal
- Maintain a multi-service platform (Spring Boot microservices + React frontends) with live client demos and marketing website, all deployed via GitHub Pages.

## Constraints & Preferences
- Develop on `develop` or feature branches; never edit `gh-pages` directly
- All React apps use `HashRouter` for GitHub Pages compatibility
- npm/WSL via Ubuntu for builds; Windows PowerShell for file operations
- Client demos must remain standalone apps, not dashboard modules
- Workflow: `develop` → `npm run build` → `./deploy.ps1` (never commit dist/)
- Sprint board sync: keep `business/sprints/SPRINTS.md` and the live GitHub Projects board aligned. Security and release-readiness work have equal priority to feature work.
- Never commit secrets, PATs, or credentials to source control. Rotate any exposed tokens immediately.

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
- Removed guesthouse monitoring pages from dashboard
- Fixed Vite base paths for all apps to match GitHub Pages URL structure
- Cleared merge conflicts, synced `develop` and `main`, deleted `master` branch
- Created `deploy.ps1` and `deploy.sh` — single-command rebuild + deploy for all apps
- Identified GitHub Pages for `decodedsolutionsite` was pointing at `master` branch instead of `gh-pages` (this caused the blank page)
- Fixed all 5 backend Dockerfiles to use Java 21 (was Java 17)
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
- Updated all controllers and services to read `X-User-Id` and `X-User-Role` headers for RLS enforcement
- Added admin bypass: ADMIN role sees all data, others scoped to their own `ownerId`
- Built and injected all JARs into running containers (api-gateway, restaurant, guesthouse, marketplace)
- Verified RLS end-to-end: create scoped to caller, list filtered by ownerId, writes enforce ownership
- Updated OFFLINE.md with task-api removal and RLS documentation

## Mock Data Status
| App | Data Source | Real Backend? |
|-----|------------|---------------|
| **butcher-shop** | `src/data/products.ts` (static import) | No — 100% mock |
| **guesthouse-client** | `src/data/properties.ts` (static import) | No — 100% mock |
| **decoded_dashboard** | Inline mock data per page | No — 100% mock |


### In Progress
- Butcher shop needs real product content populated from user's hard-copy documents
- User to provide product data via voice-to-text or spreadsheet template

### Done (PostgreSQL Native RLS)
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

### Blocked
- (none)

## Key Decisions
- `HashRouter` across all SPAs to avoid 404 on GitHub Pages static hosting
- Client demos (butcher, guesthouse) kept as standalone apps, not embedded in dashboard
- Website project lives in its own repo (`decodedsolutionsite`) with its own deployment pipeline
- Build artifacts (dist/) never committed to source branches — only deployed via gh-pages
- All deployment automated via `deploy.ps1`/`deploy.sh` to avoid manual branch switching
- Dockerfiles updated to Java 21 to match pom.xml target
- Secrets externalized to env vars with sensible defaults in application.yml
- **Dual-layer RLS**: PostgreSQL native RLS (database-level via `set_config` + policies) + application-level `ownerId` checks (redundant defence). PostgreSQL RLS is primary; app-level is fallback.
- **RLS session propagation**: AOP aspect calls `set_config()` before every `repository.*()` call. `SET` is session-scoped and transaction-safe via `true` (local) parameter.
- **Admin bypass**: Separate PostgreSQL policy `admin_all` checks `app.current_user_role = 'ADMIN'` — admins bypass all ownership restrictions at the database level.
- **Frontend role gating**: Three-tier: (1) Route-level guard → (2) Nav filter → (3) Button-level hide. Backend RLS is the real authority; frontend gating is UX only.
- **JAR inject workflow**: Build with WSL `mvn` → `docker cp` → restart, avoids full image rebuild for fast iteration.

## Next Steps
- Provide butcher shop product data (voice-to-text or CSV template)
- Update products.ts with real products, prices, categories, images
- Rebuild and deploy via `.\deploy.ps1`
- Change `decodedsolutionsite` Pages source from `master` to `gh-pages` in repo settings
- Run RLS SQL against production databases (`psql -f devops/db/rls/setup_rls.sql`)
- (Future) Wire frontend demos to real Spring Boot backend APIs when ready

## Critical Context
- Backend: 5 Spring Boot microservices (api-gateway, auth, guesthouse, marketplace, restaurant) with Docker configs — **backend running on localhost:8080-8084**
- Auth-service has Keycloak dependency but no Keycloak docker-compose or config found
- Guesthouse-service missing `application.yml` (was deleted/recreated)
- WSL mount: `/mnt/c/Users/admin/Desktop/decoded\ solution\ platform/platform/`
- `.git/index.lock` may stall git operations; delete with `Remove-Item -Force .git/index.lock`
- gh-pages currently serves: static landing page (root), butcher-shop/, guesthouse-client/, dashboard/
- **RLS Dual Layer**: PostgreSQL native (policy + set_config) + Application-level (ownerId checks in services/controllers). Apply RLS SQL with `psql -f devops/db/rls/setup_rls.sql`.
- **Backend AOP flow**: HTTP request → `UserContextFilter` reads headers → sets ThreadLocal → `RlsAspect` calls `set_config()` before each repository call → PostgreSQL RLS policies evaluate → data returned

## Relevant Files
- `devops/docker-compose.yml`: Updated build paths & env vars for secrets
- `devops/db/rls/setup_rls.sql`: PostgreSQL native RLS for all 4 databases (admin bypass + ownership scoping)
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
- `business Processes/website/`: Marketing site (separate repo `ngbontsi/decodedsolutionsite`)
- `deploy.ps1` / `deploy.sh`: Unified deployment scripts in platform root
- `backend/*/Dockerfile`: All using `eclipse-temurin:21-jdk-alpine` / `21-jre-alpine`
- `backend/*/src/main/resources/application.yml`: Secrets use `${ENV_VAR:default}` pattern
- `backend/*/src/main/java/**/config/UserContext.java`: ThreadLocal holder (userId, userRole, userEmail)
- `backend/*/src/main/java/**/config/UserContextFilter.java`: Servlet Filter reading X-User-* headers
- `backend/*/src/main/java/**/config/RlsAspect.java`: AOP aspect setting PostgreSQL session params
- `backend/*/pom.xml`: Now includes `spring-boot-starter-aop`
- `backend/*/src/main/java/**/service/*.java`: All services enforce RLS via ownerId checks
- `backend/*/src/main/java/**/controller/*.java`: Controllers read X-User-Id/X-User-Role headers; all 11 now annotated with Swagger `@Tag`/`@Operation`/`@ApiResponses`
- `OFFLINE.md`: Complete offline reference including RLS documentation
