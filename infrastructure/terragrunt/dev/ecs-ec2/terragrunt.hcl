# ─── Terragrunt DEV — ECS EC2 ────────────────────────────────────────────────
# Despliega el cluster ECS con una instancia EC2 t2.micro
# Se levanta solo para evaluaciones/demos (no 24/7)

include "root" {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../terraform/modules/ecs-ec2"
}

inputs = {
  # EC2 t2.micro: Free Tier eligible (750 h/mes en primeros 12 meses)
  instance_type = "t2.micro"

  # Servicios a desplegar (Task Definitions por separado en ECR)
  services = [
    {
      name           = "eureka-server"
      ecr_image_tag  = "latest"
      container_port = 8761
      host_port      = 8761
      cpu            = 256  # 0.25 vCPU
      memory         = 256  # 256 MB
    },
    {
      name           = "config-server"
      ecr_image_tag  = "latest"
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
      ecr_image_tag  = "latest"
      container_port = 8090
      host_port      = 80  # NGINX-like: el gateway es el 80 del host
      cpu            = 256
      memory         = 256
    },
  ]
}
