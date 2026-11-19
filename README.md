# Decoded Solutions Platform

A full-stack SaaS platform built with Spring Boot microservices and React frontends, designed for South African small businesses.

## Architecture

```
decoded-platform/
  backend/                          # Spring Boot 3.4 microservices (Java 21)
    api-gateway/                    # Spring Cloud Gateway (port 8080)
    auth-service/                   # JWT auth, RBAC, audit logs (port 8081)
    restaurant-service/             # Restaurant and menu management (port 8082)
    guesthouse-service/             # Guesthouse, rooms and reservations (port 8083)
    marketplace-service/            # Products, cart, orders (port 8084)
    family-tree-service/            # Genealogy, evidence, heritage (port 8085, beta)
  frontend/                         # React 19 + Vite 6 + TypeScript
    decoded_dashboard/              # Admin dashboard
    butcher-shop/                   # E-commerce demo (cart, checkout)
    guesthouse-client/              # Booking demo (rooms, reservations)
    zozos-shop/                     # Restaurant menu app
    vuyolwethu/                     # Client product gallery
    portfolio/                      # Freelance portfolio site
    family-tree/                    # Family tree UI (live API)
  mobile/
    decoded-tasks/                  # PWA task manager
  devops/
    docker-compose.yml              # Local development stack (8 containers)
    db/                             # SQL init, RLS policies, provisioning
    terraform/                      # IaC: Neon + Render (modules + environments)
  deploy.ps1                        # Unified deploy to GitHub Pages
  start-containers.ps1              # Start Docker containers
```

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.4, Spring Cloud Gateway
- **Database**: PostgreSQL 16 (Neon), Redis 7
- **Auth**: JWT tokens, role-based access control (7 roles)
- **Security**: Application-layer ownership scoping (enforced); PostgreSQL RLS policies (installed, not enforced)
- **Frontend**: React 19, TypeScript, Vite 6
- **Deployment**: Terraform → Render (backend) + Neon (DB); GitHub Pages (frontend demos)
- **CI/CD**: GitHub Actions (CI: build/test/policy; CD: Terraform apply)

## Quick Start

### Local Development

```powershell
# 1. Configure secrets (one-time)
cd C:\Users\admin\Desktop\Projects\decoded-platform
cp .env.example .env
# Edit .env: generate JWT_SECRET, INTERNAL_SHARED_SECRET, set DB_PASSWORD

# 2. Start backend services (8 containers: 6 services + Postgres + Redis)
.\start-containers.ps1

# 3. Start a frontend (e.g., dashboard)
cd frontend\decoded_dashboard
npm install
npm run dev
```

### Production Deployment

```powershell
# Deploy all frontends to GitHub Pages
.\deploy.ps1
```

**Backend**: Deploys automatically via Terraform/CD pipeline:
- **Staging**: Auto on merge to `develop` (after CI passes)
- **Production**: Manual approval on merge to `main`

## Services (6 Backend Microservices)

| Service | Local | Staging | Production | Database |
|---------|-------|---------|------------|----------|
| **API Gateway** | :8080 | `*-staging.onrender.com` | `*.onrender.com` | — |
| **Auth Service** | :8081 | `decoded-auth-staging` | `decoded-auth` | `authdb` |
| **Restaurant** | :8082 | `decoded-restaurant-staging` | `decoded-restaurant` | `restaurantdb` |
| **Guesthouse** | :8083 | `decoded-guesthouse-staging` | `decoded-guesthouse` | `guesthousedb` |
| **Marketplace** | :8084 | `decoded-marketplace-staging` | `decoded-marketplace` | `marketplacedb` |
| **Family Tree** | :8085 | `decoded-family-tree-staging` | *excluded* | `familytreedb` |

## Frontend Demos (7 Apps)

| App | Local | GitHub Pages | Render Static (Staging/Prod) |
|-----|-------|--------------|------------------------------|
| **Dashboard** | :5173 | `/dashboard/` | `decoded-dashboard-*` |
| **Butcher Shop** | :5174 | `/butcher-shop/` | `butcher-shop-*` |
| **Guesthouse Client** | :5175 | `/guesthouse-client/` | `guesthouse-client-*` |
| **Zozo's Kitchen** | :5176 | `/zozos-shop/` | `zozos-shop-*` |
| **Vuyolwethu** | :5177 | `/vuyolwethu/` | `vuyolwethu-*` |
| **Portfolio** | :5178 | `/freelance-work/` | `portfolio-*` |
| **Family Tree** | :5179 | `/family-tree/` | `family-tree-*` |

> Only **Family Tree** connects to live backend API. Others use mock data for demo purposes.

## Project Structure

- `backend/` — **6** Spring Boot microservices with Dockerfiles
- `frontend/` — **7** React/Vite/TypeScript applications
- `mobile/` — PWA task manager (Decoded Tasks)
- `devops/` — Docker Compose, database init, **Terraform** (IaC)
- `deploy.ps1` / `deploy.sh` — Single-command rebuild + deploy for all frontends
- `.github/workflows/` — **CI** (`ci.yml`) + **CD** (`cd.yml`) pipelines

## Key Documentation

| Document | Purpose |
|----------|---------|
| `AGENTS.md` | Platform operating manual (tech team) |
| `business/sprints/SPRINTS.md` | Sprint board (source of truth) |
| `business/docs/developer-docs/environment-setup/README.md` | Complete environment setup guide |
| `business/docs/developer-docs/architecture.md` | System architecture |
| `business/docs/developer-docs/api-reference.md` | API endpoints |
| `devops/Infra.md` | Infrastructure & deployment reference |
| `OFFLINE.md` | Offline development reference |
| `.github/workflows/ci.yml` | CI pipeline (build, test, policy) |
| `.github/workflows/cd.yml` | CD pipeline (Terraform deploy) |

## Security Status (Verified Nov 2026)

| Layer | Status |
|-------|--------|
| Application-layer ownership (`ownerId`) | **Enforced** |
| PostgreSQL RLS policies | **Installed, NOT enforced** (0 `FORCE ROW LEVEL SECURITY`) |
| Gateway trust boundary | **Trusts inbound `X-User-*`** (needs hardening) |
| Secrets externalized | **Yes** (`.env` local, GitHub Environments staging/prod) |

Tracked in `business/sprints/SPRINTS.md` (S-1 through S-6).