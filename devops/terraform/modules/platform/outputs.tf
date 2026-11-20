# modules/platform/outputs.tf

output "neon_project_id" {
  value       = neon_project.platform.id
  description = "Neon project ID"
}

output "neon_database_host" {
  value       = neon_project.platform.database_host
  description = "Neon database host"
}

output "neon_database_user" {
  value       = neon_project.platform.database_user
  description = "Neon default database user"
}

output "database_urls" {
  description = "Map of service names to their DATABASE_URL"
  value = {
    for k, v in var.backend_services :
    k => "jdbc:postgresql://${neon_project.platform.database_host}/${neon_database.app[k].name}?sslmode=require"
  }
}

output "backend_service_urls" {
  description = "Map of backend service names to their Render URLs"
  value = {
    for k, v in render_web_service.backend :
    k => v.url
  }
}

output "frontend_site_urls" {
  description = "Map of frontend app names to their Render URLs"
  value = {
    for k, v in render_static_site.frontend :
    k => v.url
  }
}

output "api_gateway_url" {
  value       = render_web_service.backend["api-gateway"].url
  description = "API Gateway URL"
}