# environments/prod/variables.tf

variable "jwt_secret" {
  description = "JWT secret for token signing"
  type        = string
  sensitive   = true
}

variable "internal_shared_secret" {
  description = "Shared secret for internal service-to-service calls"
  type        = string
  sensitive   = true
}