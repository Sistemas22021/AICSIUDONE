# ─────────────────────────────────────────────────────────────────────────────
# Módulo Terraform: ECR (Elastic Container Registry)
#
# Crea un repositorio ECR por cada microservicio del ecosistema.
# Se llama UNA SOLA VEZ para crear los repos; las imágenes las sube el pipeline.
# ─────────────────────────────────────────────────────────────────────────────

variable "env_name" {
  description = "Ambiente (dev/prod)"
  type        = string
}

variable "services" {
  description = "Lista de nombres de servicios para los que se crearán repos ECR"
  type        = list(string)
  default = [
    "sso/eureka-server",
    "sso/config-server",
    "sso/auth-service",
    "sso/api-gateway",
  ]
}

# ── Repositorios ECR ──────────────────────────────────────────────────────────

resource "aws_ecr_repository" "service" {
  for_each = toset(var.services)

  name                 = each.value
  image_tag_mutability = "MUTABLE"

  # Escanea las imágenes en busca de vulnerabilidades automáticamente
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Service = each.value
  }
}

# ── Política de ciclo de vida: mantener solo las últimas 5 imágenes ───────────
# Ahorra espacio en el Free Tier

resource "aws_ecr_lifecycle_policy" "keep_last_5" {
  for_each   = aws_ecr_repository.service
  repository = each.value.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Mantener solo las últimas 5 imágenes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 5
      }
      action = { type = "expire" }
    }]
  })
}

# ── Outputs ───────────────────────────────────────────────────────────────────

output "repository_urls" {
  description = "URLs de los repositorios ECR creados"
  value       = { for k, v in aws_ecr_repository.service : k => v.repository_url }
}
