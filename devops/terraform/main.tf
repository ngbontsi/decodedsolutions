# 1. Create a Neon Postgres Project & Database for this stage
resource "neon_project" "platform_db" {
  name                      = "platform-${var.stage_name}"
  region_id                 = "aws-eu-central-1" # Closest AWS region to South Africa for Neon
  pg_version                = 16
}

# 2. Deploy Spring Boot Backend from the 'Backend' folder
resource "render_web_service" "spring_backend" {
  name   = "backend-api-${var.stage_name}"
  plan   = "starter" # Low-cost tier ($7/mo) or "free" for dev/testing
  region = "frankfurt"

  runtime_source = {
    native_runtime = {
      repo_url   = var.github_repo_url
      branch     = var.git_branch
      root_dir   = "Backend" # Tells Render to only watch and build the Backend folder
      runtime    = "docker"
    }
  }

  # Automatically inject Neon DB credentials into Spring Boot
  env_vars = {
    SPRING_DATASOURCE_URL      = { value = "jdbc:postgresql://${neon_project.platform_db.database_host}/${neon_project.platform_db.database_name}?sslmode=require" }
    SPRING_DATASOURCE_USERNAME = { value = neon_project.platform_db.database_user }
    SPRING_DATASOURCE_PASSWORD = { value = neon_project.platform_db.database_password }
    SPRING_PROFILES_ACTIVE     = { value = var.stage_name }
  }
}

# 3. Deploy React Frontend App 1 (Free Static Site on Render)
resource "render_static_site" "react_portal" {
  name   = "portal-ui-${var.stage_name}"

  repo_url      = var.github_repo_url
  branch        = var.git_branch
  root_dir      = "frontend/client-portal"
  build_command = "npm install && npm run build"
  publish_path  = "dist" # Change to "build" if using Create React App instead of Vite

  # Automatically pass the live Spring Boot API URL to React
  env_vars = {
    VITE_API_URL = { value = render_web_service.spring_backend.url }
  }
}