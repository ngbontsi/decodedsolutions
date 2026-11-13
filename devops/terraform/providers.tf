terraform {
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
  # Reads NEON_API_KEY from environment variables
}
provider "render" {
  # Reads RENDER_API_KEY and RENDER_OWNER_ID from your environment variables
}