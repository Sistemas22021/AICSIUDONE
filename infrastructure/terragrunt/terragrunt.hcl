# ─────────────────────────────────────────────────────────────────────────────
# Terragrunt raíz — Configuración compartida por todos los ambientes
#
# Este archivo define:
# - El backend remoto de Terraform (estado en S3)
# - El lock de estado (DynamoDB)
# - El provider de AWS con las variables de entorno
# - Los inputs comunes a todos los módulos
# ─────────────────────────────────────────────────────────────────────────────

locals {
  # Leer variables del ambiente (dev/prod) desde el terragrunt.hcl del subdirectorio
  env_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))
  env_name = local.env_vars.locals.env_name
  aws_region = local.env_vars.locals.aws_region
}

# ─── Backend remoto: estado de Terraform en S3 ───────────────────────────────
# El bucket y la tabla DynamoDB deben crearse manualmente ANTES del primer apply.
# Ver docs/adr/ADR-006 para instrucciones.

remote_state {
  backend = "s3"

  config = {
    bucket         = "sso-terraform-state-${local.env_name}"
    key            = "${path_relative_to_include()}/terraform.tfstate"
    region         = local.aws_region
    encrypt        = true
    dynamodb_table = "sso-terraform-locks-${local.env_name}"
  }

  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}

# ─── Provider AWS ─────────────────────────────────────────────────────────────
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite_terragrunt"

  contents = <<EOF
provider "aws" {
  region = "${local.aws_region}"

  default_tags {
    tags = {
      Project     = "sso-boilerplate"
      Environment = "${local.env_name}"
      ManagedBy   = "terragrunt"
      Team        = "main"
    }
  }
}
EOF
}

# ─── Inputs comunes ──────────────────────────────────────────────────────────
inputs = {
  env_name   = local.env_name
  aws_region = local.aws_region
  project    = "sso-boilerplate"
  team       = "main"
}
