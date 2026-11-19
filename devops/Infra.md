# Infrastructure & Deployment — Decoded Platform

> **Status**: **ACTIVE** — Updated November 2026  
> **Supersedes**: Original inspection guide (archived below)  
> **Current Implementation**: Terraform modules + GitHub Actions CI/CD

---

## Current Architecture

### Deployment Targets
| Environment | Branch | Database | Backend | Frontend | Trigger |
|-------------|--------|----------|---------|----------|---------|
| **Local** | Any | Docker Postgres (5 DBs) | Docker Compose (6 services) | Vite dev servers (7 apps) | Manual |
| **Staging** | `develop` | Neon (1 project, 5 DBs) | Render Web Services (6) | Render Static Sites (7) | Auto (after CI) |
| **Production** | `main` | Neon (1 project, 5 DBs) | Render Web Services (6) | Render Static Sites (7) | Manual approval |

**Family Tree**: Staging only (private beta), excluded from production.

---

## Terraform Structure

```
devops/terraform/
├── modules/platform/              # Reusable module (single source of truth)
│   ├── versions.tf                # Provider versions (neon ~>0.6, render ~>1.3)
│   ├── variables.tf               # All configurable inputs
│   ├── main.tf                    # Resources: Neon project + 5 DBs/roles + 6 Render WS + 7 Render Static
│   └── outputs.tf                 # URLs, DB connections
├── environments/
│   ├── staging/                   # stage_name=staging, git_branch=develop, plan=starter
│   │   ├── main.tf                # Calls module
│   │   └── variables.tf           # Secrets: jwt_secret, internal_shared_secret
│   └── prod/                      # stage_name=prod, git_branch=main, plan=standard
│       ├── main.tf
│       └── variables.tf           # Secrets: jwt_secret, internal_shared_secret
```

### Module Resources Created
- **Neon**: 1 project (`platform-{stage}`), 5 databases (`authdb`, `restaurantdb`, `guesthousedb`, `marketplacedb`, `familytreedb`), 5 roles (`*_app`)
- **Render Web Services**: 6 (api-gateway, auth, restaurant, guesthouse, marketplace, family-tree)
- **Render Static Sites**: 7 (dashboard, butcher-shop, guesthouse-client, family-tree, zozos-shop, vuyolwethu, portfolio)
- **Per-service env vars**: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `INTERNAL_SHARED_SECRET`

---

## CI/CD Pipeline

### CI (`.github/workflows/ci.yml`)
**Triggers**: PR to `develop`/`main`, push to `develop`/`main`

**Jobs**:
1. **policy** — Enforces branch model (feature→develop→main)
2. **backend** — `mvn package` for 6 services (skip tests)
3. **backend-tests** — Family Tree only (18 tests, real Postgres)
4. **frontend** — `npm run build` for 7 apps (type-checks)
5. **ci-gate** — Aggregates all; **must pass** for branch protection

### CD (`.github/workflows/cd.yml`)
**Trigger**: `workflow_run` on CI completion (only if `ci-gate` succeeded)

**Flow**:
```
CI succeeds on develop
    │
    ▼
CD: select-environment → staging
    │
    ├── terraform-plan (artifact uploaded)
    └── terraform-apply (auto, GitHub Env: staging)
    │
CI succeeds on main (after develop→main PR merge)
    │
    ▼
CD: select-environment → prod
    │
    ├── terraform-plan
    └── terraform-apply (MANUAL APPROVAL, GitHub Env: prod)
```

### Required GitHub Environments
| Environment | Protection | Secrets |
|-------------|------------|---------|
| `staging` | None (auto) | `NEON_API_KEY`, `RENDER_API_KEY`, `RENDER_OWNER_ID`, `JWT_SECRET`, `INTERNAL_SHARED_SECRET` |
| `prod` | **Required reviewers** | Same as staging (DIFFERENT values) |

---

## Local Development (docker-compose)

**File**: `devops/docker-compose.yml`

**Services** (8 containers):
- `db` — Postgres 16 (port 5433)
- `redis` — Redis 7 (port 6379)
- `database-provisioner` — Creates 5 DBs + 5 roles (`*_app`) on startup
- `api-gateway` (8080), `auth-service` (8081), `restaurant-service` (8082), `guesthouse-service` (8083), `marketplace-service` (8084), `familytree-service` (8085)

**Key Features**:
- Fail-fast env vars (`${VAR:?error}`) — no silent defaults
- Per-service `INTERNAL_SHARED_SECRET` for gateway→service auth
- `database-provisioner` creates roles/databases with proper ownership
- Healthchecks on Postgres before services start

**Start**: `cd devops && docker compose up -d`
**Stop**: `docker compose down`
**Reset**: `docker compose down -v && docker compose up -d`

---

## Secrets Management

### Local (`.env` — never committed)
```bash
JWT_SECRET=<openssl rand -base64 48>
INTERNAL_SHARED_SECRET=<openssl rand -base64 32>
DB_USERNAME=platformuser
DB_PASSWORD=<strong password>
BETA_PASSCODE=<optional>
```

### Staging/Production (GitHub Environments)
| Secret | Purpose |
|--------|---------|
| `NEON_API_KEY` | Terraform Neon provider auth |
| `RENDER_API_KEY` | Terraform Render provider auth |
| `RENDER_OWNER_ID` | Render account owner ID |
| `JWT_SECRET` | JWT signing (32+ chars) |
| `INTERNAL_SHARED_SECRET` | Gateway→service auth |

**Rotation**: Quarterly (90 days). See `business/docs/developer-docs/environment-setup/README.md#secrets-management`.

---

## Database Architecture

### Per-Service Isolation
| Service | Database | Role | Owner |
|---------|----------|------|-------|
| Auth | `authdb` | `auth_app` | Auth service |
| Restaurant | `restaurantdb` | `restaurant_app` | Restaurant service |
| Guesthouse | `guesthousedb` | `guesthouse_app` | Guesthouse service |
| Marketplace | `marketplacedb` | `marketplace_app` | Marketplace service |
| Family Tree | `familytreedb` | `familytree_app` | Family Tree service |

### RLS Policies
- **File**: `devops/db/rls/setup_rls.sql` (30 policies)
- **Status**: **Installed but NOT enforced**
- **Blocker**: App connects as `platformuser` (superuser + table owner)
- **Needed**: Non-owner app role + `ALTER TABLE ... FORCE ROW LEVEL SECURITY` + 2-user verification
- **Tracked**: S-2/S-3 in `business/sprints/SPRINTS.md`

---

## Branching & Deployment Rules

```
feature/* | fix/* | hotfix/*
    │
    ▼ PR (ci-gate must pass)
develop (integration)
    │
    ▼ Auto-deploy via CD
STAGING (Render + Neon)
    │
    ▼ PR (ci-gate must pass, manual approval)
main (production)
    │
    ▼ Manual approval in CD
PRODUCTION (Render + Neon)
```

**Branch Protection**: `develop` and `main` require `ci-gate` status check.

**Family Tree**: Deploys to staging only (`BetaGateFilter` enforces passcode).

---

## Quick Commands

```powershell
# Local
cd devops && docker compose up -d
cd devops && docker compose down -v && docker compose up -d  # full reset

# Terraform (local validation only)
cd devops/terraform/environments/staging && terraform init -backend=false && terraform validate

# Frontend deploy
.\deploy.ps1

# Check status
.\check-status.ps1
.\check-status.ps1 -Detailed
```

---

## Archived: Original Inspection Guide (Nov 13, 2026)

<details>
<summary>Original step-by-step inspection guide (historical)</summary>

The original `Infra.md` was written as a step-by-step inspection guide for a specific session on the `feature/family-tree-beta-deploy` branch. It contained:

- Steps 1-3: Project location, GitHub Actions config, Git status
- Steps 4-6: Staged/unstaged changes, empty files inspection
- Steps 7-9: Docker Compose, Dockerfiles, Family Tree migration duplicates
- Steps 10-13: Deployment architecture design, future checklist

**Status**: All inspection steps completed. CI/CD workflows created. Terraform restructured into module + environments. No further inspection needed.

</details>

---

## Related Documents

| Document | Purpose |
|----------|---------|
| `AGENTS.md` | Platform operating manual |
| `business/sprints/SPRINTS.md` | Sprint board |
| `business/docs/developer-docs/environment-setup/README.md` | Full environment setup |
| `business/docs/developer-docs/architecture.md` | System architecture |
| `business/docs/developer-docs/api-reference.md` | API endpoints |
| `devops/db/rls/setup_rls.sql` | RLS policies |
| `.github/workflows/ci.yml` | CI pipeline |
| `.github/workflows/cd.yml` | CD pipeline |