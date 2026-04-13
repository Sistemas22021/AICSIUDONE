# ─────────────────────────────────────────────────────────────────────────────
# Módulo Terraform: S3 Static Hosting
#
# Crea un bucket S3 configurado para hosting de sitios estáticos.
# Usado para desplegar el Login MFE y la Consumer App como SPAs.
#
# NOTA: Sin CloudFront (Free Tier). Las URLs serán del tipo:
#   http://<bucket>.s3-website-us-east-1.amazonaws.com
# ─────────────────────────────────────────────────────────────────────────────

variable "env_name" { type = string }

variable "frontends" {
  description = "Lista de nombres de frontends a desplegar"
  type        = list(string)
  default     = ["login-mfe", "consumer-app"]
}

# ── Buckets S3 ────────────────────────────────────────────────────────────────

resource "aws_s3_bucket" "frontend" {
  for_each = toset(var.frontends)

  bucket = "sso-${each.value}-${var.env_name}"

  tags = {
    Frontend    = each.value
    Environment = var.env_name
  }
}

# ── Configurar acceso público (necesario para static hosting) ─────────────────

resource "aws_s3_bucket_public_access_block" "frontend" {
  for_each = aws_s3_bucket.frontend

  bucket = each.value.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

# ── Habilitar static website hosting ─────────────────────────────────────────

resource "aws_s3_bucket_website_configuration" "frontend" {
  for_each = aws_s3_bucket.frontend

  bucket = each.value.id

  index_document { suffix = "index.html" }
  error_document { key = "index.html" }  # Redirige 404 al index (SPA routing)
}

# ── Política de bucket: acceso público de lectura ─────────────────────────────

resource "aws_s3_bucket_policy" "public_read" {
  for_each = aws_s3_bucket.frontend

  bucket = each.value.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "PublicReadGetObject"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:GetObject"
      Resource  = "${each.value.arn}/*"
    }]
  })

  depends_on = [aws_s3_bucket_public_access_block.frontend]
}

# ── Outputs ───────────────────────────────────────────────────────────────────

output "website_endpoints" {
  description = "URLs del static hosting de cada frontend"
  value = {
    for k, v in aws_s3_bucket_website_configuration.frontend :
    k => v.website_endpoint
  }
}

output "bucket_names" {
  value = { for k, v in aws_s3_bucket.frontend : k => v.id }
}
