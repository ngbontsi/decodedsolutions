# environments/staging/main.tf

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    neon = {
      source  = "kislerdm/neon"
      version = "~> 0.6.0"
    }
    render = {
      source  = "render-oss/render"
      version = "~> 1.3.0"
    }
  }
}

provider "neon" {
  # NEON_API_KEY from environment
}

provider "render" {
  # RENDER_API_KEY and RENDER_OWNER_ID from environment
}

module "platform" {
  source = "../../modules/platform"

  stage_name      = "staging"
  github_repo_url = "https://github.com/ngbontsi/decodedsolutions.git"
  git_branch      = "develop"
  region          = "aws-eu-central-1"
  render_region   = "frankfurt"
  render_plan     = "starter"

  jwt_secret             = var.jwt_secret
  internal_shared_secret = var.internal_shared_secret
}