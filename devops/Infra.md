# Decoded Platform — Infrastructure & Deployment Preparation

**Working branch:** `feature/family-tree-beta-deploy`  
**Target integration branch:** `develop`  
**Production branch:** `main`  
**Date:** 13 November 2026

## 1. Objective

Prepare Decoded Platform for a controlled CI/CD deployment lifecycle using GitHub Actions.

Our intended branching and deployment model:

```text
feature/*
    |
    | Pull Request
    v
 develop  -------> STAGING
    |
    | Pull Request
    v
  main   --------> PRODUCTION
```

### Deployment requirements

**Staging (`develop`)**
- API Gateway
- Authentication
- Restaurant
- Guesthouse
- Marketplace
- Family Tree (private family beta)

**Production (`main`)**
- API Gateway
- Authentication
- Restaurant
- Guesthouse
- Marketplace
- Family Tree excluded from production deployment for now.

Family Tree remains part of the same monorepo and development workflow. It does not require another Git repository or another staging environment.

Its database remains isolated from the other services.

---

## 2. Important safety rules

Before starting:

- Do not run `git reset --hard`.
- Do not run `git clean -fd`.
- Do not delete or recreate branches.
- Do not run `docker compose down -v`.
- Do not delete Docker volumes.
- Do not run Terraform `apply` or `destroy`.
- Do not commit or push until the changes have been reviewed.
- Do not overwrite existing configuration files.

Our current branch contains staged and unstaged work. Both are important.

---

## 3. Step 1 — Confirm the project location

**Execution**

Open PowerShell and run:

```powershell
cd C:\Users\admin\Desktop\Projects\decoded-platform

Get-Location
```

**Intention**

Ensure all subsequent commands execute from the correct project root.

**Expected result**

```text
C:\Users\admin\Desktop\Projects\decoded-platform
```

If the directory differs, stop and navigate to the correct project directory.

---

## 4. Step 2 — Confirm GitHub Actions configuration

**Execution**

```powershell
Get-ChildItem .github -Force

Get-ChildItem .github\workflows -Force
```

**Intention**

Determine whether GitHub Actions workflows already exist.

**Expected result**

The `.github` directory contains a `workflows` directory, but the workflows directory is empty.

**Interpretation**

We currently have no project-owned GitHub Actions workflows. We will need to design the CI/CD pipeline.

Do not create workflow files yet.

---

## 5. Step 3 — Confirm Git status and branches

**Execution**

```powershell
git status --short --branch

git branch -vv

git branch -a

git remote -v
```

**Intention**

Identify:
- Current working branch.
- Existing local and remote branches.
- Uncommitted changes.
- GitHub repository connection.
- Whether branches track their remote counterparts.

**Previously confirmed results**

Current branch:

```text
feature/family-tree-beta-deploy
```

Existing branches include:

```text
develop
main
feature/family-tree-beta-deploy
create/family_tree
fix/60-gateway-trust-boundaries
```

Remote:

```text
origin
https://github.com/ngbontsi/decodedsolutions.git
```

**Important**

The feature branch currently has staged and unstaged changes. Preserve both.

---

## 6. Step 4 — Understand staged and unstaged changes

**Execution A — Staged changes**

```powershell
git diff --cached --stat
```

**Intention**

Identify files already added to Git's staging area.

**Previously observed**

- Database provisioning script.
- Family Tree SQL schema files.
- Terraform environment variable files.

There are 14 staged files.

**Execution B — Unstaged changes**

```powershell
git diff --stat
```

**Intention**

Identify modifications that have not yet been staged.

**Previously observed**

- Backend Dockerfiles.
- Family Tree ContributorController.
- Database initialization script.
- Docker Compose configuration.
- Database provisioning script.

There are 10 unstaged files.

**Important observation**

The provisioning script appears in both staged and unstaged changes. Its staged version is empty, while the working copy contains additional content.

We need to inspect this carefully before committing.

---

## 7. Step 5 — Inspect incomplete files

**Execution**

```powershell
Get-Item devops\db\provision-databases.sh,
          devops\terraform\environments\dev\terraform.tfvars,
          devops\terraform\environments\staging\terraform.tfvars,
          devops\terraform\environments\prod\terraform.tfvars |
    Select-Object FullName, Length

Get-Content devops\db\provision-databases.sh
```

**Intention**

Determine whether the files are genuinely empty or simply have not been staged correctly.

**Expected observations**

The provisioning script should contain the database provisioning logic.

The three Terraform `.tfvars` files may currently be empty.

**What to do**

- If the provisioning script contains code, preserve it.
- If Terraform files are empty, do not invent values or credentials.
- We will decide what belongs in each environment configuration later.

---

## 8. Step 6 — Inspect committed feature branch changes

**Execution A**

```powershell
git log --oneline origin/develop..HEAD
```

**Intention**

Identify commits on the current feature branch that are not yet present in remote `develop`.

**Expected result**

A list of commits, or no output if there are no additional commits.

**Execution B**

```powershell
git diff --name-status origin/develop...HEAD
```

**Intention**

Identify the committed files that differ between the feature branch and `develop`.

**Expected result**

A list of added, modified or deleted files.

**Important distinction**

These commands inspect committed branch differences. They do not include all your uncommitted working changes.

---

## 9. Step 7 — Inspect Docker Compose and database provisioning

**Execution**

```powershell
git diff -- devops/docker-compose.yml devops/db/init.sql devops/db/provision-databases.sh
```

**Intention**

Review the infrastructure changes before merging.

We need to verify:

1. PostgreSQL healthcheck.
2. Database provisioner service.
3. Database creation logic.
4. Backend service dependencies.
5. Family Tree database isolation.
6. Existing persistent database volumes.

**Expected architecture**

The PostgreSQL instance should provide separate databases:

```text
authdb
restaurantdb
guesthousedb
marketplacedb
familytreedb
```

The database provisioner should create missing databases without deleting or recreating existing ones.

The provisioner should finish successfully before backend services start.

**Expected provisioner behaviour**

```text
Checking Decoded Platform databases...
authdb already exists. Leaving it untouched.
restaurantdb already exists. Leaving it untouched.
guesthousedb already exists. Leaving it untouched.
marketplacedb already exists. Leaving it untouched.
familytreedb already exists. Leaving it untouched.
Database provisioning completed successfully.
```

Do not delete existing databases or Docker volumes.

---

## 10. Step 8 — Inspect backend Dockerfiles and Family Tree controller

**Execution**

```powershell
git diff -- backend
```

**Intention**

Understand the code and Docker build changes.

We need to establish:

- Whether Dockerfiles have been updated consistently.
- Whether Maven dependencies are cached correctly.
- Whether builds can work with unreliable internet access.
- Whether the Family Tree controller change is intentional.
- Whether the changes affect the other backend services.

**Expected result**

A readable list of code differences.

If the output is too large, inspect individual files:

```powershell
git diff -- backend/api-gateway/Dockerfile

git diff -- backend/auth-service/Dockerfile

git diff -- backend/family-tree-service/Dockerfile

git diff -- backend/family-tree-service/src/main/java/com/platform/familytree/community/controller/ContributorController.java

git diff -- backend/restaurant-service/Dockerfile

git diff -- backend/guesthouse-service/Dockerfile

git diff -- backend/marketplace-service/Dockerfile
```

Do not modify the files during this inspection.

---

## 11. Step 9 — Check duplicate Family Tree migrations

**Execution**

```powershell
Get-ChildItem backend\family-tree-service\src\main\resources\db\migration -File |
    ForEach-Object {
        $other = Join-Path "devops\db\schema" $_.Name

        if (Test-Path $other) {
            $a = (Get-FileHash $_.FullName -Algorithm SHA256).Hash
            $b = (Get-FileHash $other -Algorithm SHA256).Hash

            [PSCustomObject]@{
                Migration = $_.Name
                Identical = ($a -eq $b)
            }
        }
    } | Format-Table -AutoSize
```

**Intention**

Establish whether the SQL migration files under `devops/db/schema` are identical to those under the Family Tree service.

**Expected result**

A table containing migration filenames and whether the files are identical.

Example:

```text
Migration                                  Identical
----------                                 ---------
V1__extensions_and_archive.sql             True
V2__reference_places_and_sources.sql        True
V3__identity.sql                           True
```

**Architectural decision**

The Family Tree service's own Flyway migration directory should remain authoritative unless we deliberately introduce a central migration runner.

We should avoid maintaining duplicate migration sources unnecessarily.

Do not delete either directory until we have inspected references and confirmed the intended approach.

---

## 12. Step 10 — Review the existing deployment architecture

Once the preceding inspections are complete, we will design the GitHub Actions pipeline.

### Required workflow behaviour

**For pull requests into `develop`:**

- Compile affected services.
- Run automated tests.
- Validate Docker build configurations.
- Prevent broken code from entering staging.

**After merging into `develop`:**

- Deploy staging services.
- Deploy Family Tree to staging.
- Apply Family Tree migrations to the staging `familytreedb`.
- Keep Family Tree private and restrict access to invited family members.

**For pull requests into `main`:**

- Run production validation.
- Review deployment impact.
- Require approval before production promotion.

**After merging into `main`:**

- Deploy production services.
- Do not deploy Family Tree.
- Do not run Family Tree production migrations.

### Important deployment distinction

A service can exist in the monorepo without being deployed to every environment.

Family Tree is part of development and staging, but it is not yet part of the commercial production deployment.

---

## 13. Step 11 — Future implementation checklist

Do not execute this section yet. It is our planned work after reviewing the inspection results.

- [ ] Review and reconcile staged and unstaged changes.
- [ ] Confirm the authoritative Family Tree migration location.
- [ ] Review database provisioning logic.
- [ ] Review Dockerfile build changes.
- [ ] Review Terraform configuration and provider compatibility.
- [ ] Confirm `develop` and `main` deployment requirements.
- [ ] Create GitHub Actions CI workflow.
- [ ] Create staging deployment workflow for `develop`.
- [ ] Include Family Tree in staging deployment.
- [ ] Create production deployment workflow for `main`.
- [ ] Exclude Family Tree from production deployment.
- [ ] Configure GitHub repository secrets and environment protection.
- [ ] Test staging deployment.
- [ ] Verify production deployment rules without deploying unfinished changes.

---

## 14. Feedback template

When connectivity is stable, send the command results in this format.

| Step | Result |
|---|---|
| 1. Project location | |
| 2. GitHub workflows | |
| 3. Git branches | |
| 4. Staged/unstaged changes | |
| 5. Empty files | |
| 6. Branch differences | |
| 7. Docker Compose | |
| 8. Dockerfiles | |
| 9. Migration comparison | |

If a command returns an error, record the complete error message.

If a result differs from the expected output, do not attempt to fix it independently. Share the result so we can decide the safest next action.

---

## Final objective

We are building a controlled development and release process for Decoded Platform.

Our principle:

**Develop, integrate, validate, stage, approve, and promote to production.**

We want a deployment architecture that supports future services without compromising existing data, private family information, or production stability.