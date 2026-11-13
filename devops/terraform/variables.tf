variable "stage_name" {
  description = "Deployment stage or client identifier (e.g., staging, prod, acme-corp)"
  type        = string
}

variable "github_repo_url" {
  description = "Full HTTPS URL of your monorepo"
  type        = string
  default     = "https://github.com"
}

variable "git_branch" {
  description = "Branch to deploy from (e.g., develop for staging, main for prod)"
  type        = string
  default     = "main"
}
variable "frontend_apps" {
  description = "Map of React apps inside the frontend folder"
  type        = map(string)
  default = {
    # Format: short_service_name = "exact/folder/path"
    butcher   = "frontend/butcher-shop"
    family    = "frontend/family-tree"
    client    = "frontend/guesthouse-client"
    admin     = "frontend/decoded_dashboard"
    zozos     = "frontend/zozos-shop"
    portfolio = "frontend/portfolio"
    vuyo      = "frontend/vuyolwethu"

  }
}