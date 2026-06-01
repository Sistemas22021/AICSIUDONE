# ─── Terragrunt PROD — ECS EC2 ────────────────────────────────────────────────
# Igual que dev pero con instance_type t2.micro (mismo en prod para Free Tier)
# Para producción real se recomendaría migrar a t3.small o t3.medium

include "root" {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../terraform/modules/ecs-ec2"
}

inputs = {
  instance_type = "t2.micro"  # Free Tier durante 12 meses

  services = [
    {
      name           = "eureka-server"
      ecr_image_tag  = var.eureka_server_image_tag
      container_port = 8761
      host_port      = 8761
      cpu            = 256
      memory         = 256
    },
    {
      name           = "config-server"
      ecr_image_tag  = var.config_server_image_tag
      container_port = 8888
      host_port      = 8888
      cpu            = 256
      memory         = 256
    },
    {
      name           = "auth-service"
      ecr_image_tag  = var.auth_service_image_tag
      container_port = 8080
      host_port      = 8080
      cpu            = 512
      memory         = 512
    },
    {
      name           = "api-gateway"
      ecr_image_tag  = var.api_gateway_image_tag
      container_port = 8090
      host_port      = 80
      cpu            = 256
      memory         = 256
    },
  ]
}
