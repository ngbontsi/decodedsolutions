# modules/platform/variables.tf

variable "stage_name" {
  description = "Deployment stage name (e.g., staging, prod)"
  type        = string
}

variable "github_repo_url" {
  description = "Full HTTPS URL of the monorepo"
  type        = string
}

variable "git_branch" {
  description = "Branch to deploy from"
  type        = string
}

variable "region" {
  description = "AWS region for Neon/Render"
  type        = string
  default     = "aws-eu-central-1"
}

variable "render_region" {
  description = "Render region (frankfurt for EU)"
  type        = string
  default     = "frankfurt"
}

variable "render_plan" {
  description = "Render plan for web services (starter, standard, etc.)"
  type        = string
  default     = "starter"
}

variable "backend_services" {
  description = "Map of backend service names to their dockerfile paths"
  type        = map(string)
  default = {
    "api-gateway"         = "backend/api-gateway"
    "auth-service"        = "backend/auth-service"
    "restaurant-service"  = "backend/restaurant-service"
    "guesthouse-service"  = "backend/guesthouse-service"
    "marketplace-service" = "backend/marketplace-service"
    "family-tree-service" = "backend/family-tree-service"
  }
}

variable "frontend_apps" {
  description = "Map of frontend app names to their folder paths"
  type        = map(string)
  default = {
    "butcher-shop"      = "frontend/butcher-shop"
    "decoded-dashboard" = "frontend/decoded_dashboard"
    "family-tree"       = "frontend/family-tree"
    "guesthouse-client" = "frontend/guesthouse-client"
    "portfolio"         = "frontend/portfolio"
    "vuyolwethu"        = "frontend/vuyolwethu"
    "zozos-shop"        = "frontend/zozos-shop"
  }
}

variable "neon_project_name_prefix" {
  description = "Prefix for Neon project name"
  type        = string
  default     = "platform"
}

variable "database_names" {
  description = "Map of logical names to database names"
  type        = map(string)
  default = {
    "auth"        = "authdb"
    "restaurant"  = "restaurantdb"
    "guesthouse"  = "guesthousedb"
    "marketplace" = "marketplacedb"
    "familytree"  = "familytreedb"
  }
}

variable "jwt_secret" {
  description = "JWT secret for token signing (injected from GitHub secret)"
  type        = string
  sensitive   = true
}

variable "internal_shared_secret" {
  description = "Shared secret for internal service-to-service calls"
  type        = string
  sensitive   = true
}