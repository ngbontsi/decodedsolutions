# modules/platform/main.tf

# 1. Neon Postgres Project with 5 databases
resource "neon_project" "platform" {
  name       = "${var.neon_project_name_prefix}-${var.stage_name}"
  region_id  = var.region
  pg_version = 16
}

# Neon roles (one per database)
resource "neon_role" "app" {
  for_each = var.database_names

  project_id = neon_project.platform.id
  branch_id  = neon_project.platform.default_branch_id
  name       = "${each.key}_app"
}

# Neon databases (owned by corresponding roles)
resource "neon_database" "app" {
  for_each = var.database_names

  project_id = neon_project.platform.id
  branch_id  = neon_project.platform.default_branch_id
  name       = each.value
  owner_name = neon_role.app[each.key].name
}

# 2. Render Web Services (one per backend service)
resource "render_web_service" "backend" {
  for_each = var.backend_services

  name   = "${each.key}-${var.stage_name}"
  plan   = var.render_plan
  region = var.render_region

  runtime_source = {
    docker = {
      repo_url        = var.github_repo_url
      branch          = var.git_branch
      root_directory  = each.value
      dockerfile_path = "${each.value}/Dockerfile"
      auto_deploy     = true
    }
  }

  env_vars = {
    SPRING_PROFILES_ACTIVE = { value = var.stage_name }
    # Database URL will be constructed per-service using Neon connection info
    # Each service gets its own DATABASE_URL via env var override below
  }

  # Build filter: only trigger on changes to this service's directory
  build_filter = {
    paths         = ["${each.value}/**"]
    ignored_paths = ["${each.value}/**/*.md", "${each.value}/**/*.txt"]
  }
}

# Per-service DATABASE_URL env var injection
resource "render_env_var" "backend_db" {
  for_each = var.backend_services

  service_id = render_web_service.backend[each.key].id
  key        = "SPRING_DATASOURCE_URL"
  value      = "jdbc:postgresql://${neon_project.platform.database_host}/${neon_database.app[each.key].name}?sslmode=require"
}

resource "render_env_var" "backend_db_user" {
  for_each = var.backend_services

  service_id = render_web_service.backend[each.key].id
  key        = "SPRING_DATASOURCE_USERNAME"
  value      = neon_role.app[each.key].name
}

resource "render_env_var" "backend_db_pass" {
  for_each = var.backend_services

  service_id = render_web_service.backend[each.key].id
  key        = "SPRING_DATASOURCE_PASSWORD"
  value      = neon_role.app[each.key].password
}

# Shared secrets (injected via GitHub secrets at deploy time)
resource "render_env_var" "jwt_secret" {
  for_each = var.backend_services

  service_id = render_web_service.backend[each.key].id
  key        = "JWT_SECRET"
  value      = var.jwt_secret
}

resource "render_env_var" "internal_shared_secret" {
  for_each = var.backend_services

  service_id = render_web_service.backend[each.key].id
  key        = "INTERNAL_SHARED_SECRET"
  value      = var.internal_shared_secret
}

# 3. Render Static Sites (one per frontend app)
resource "render_static_site" "frontend" {
  for_each = var.frontend_apps

  name           = "${each.key}-${var.stage_name}"
  repo_url       = var.github_repo_url
  branch         = var.git_branch
  root_directory = each.value
  build_command  = "npm ci && npm run build"
  publish_path   = "dist"

  # Build filter: only trigger on changes to this app's directory
  build_filter = {
    paths         = ["${each.value}/**"]
    ignored_paths = ["${each.value}/**/*.md", "${each.value}/**/*.txt"]
  }

  env_vars = {
    VITE_API_URL = { value = "https://${replace(render_web_service.backend["api-gateway"].url, "https://", "")}" }
  }
}