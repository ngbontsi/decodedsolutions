# modules/platform/versions.tf

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