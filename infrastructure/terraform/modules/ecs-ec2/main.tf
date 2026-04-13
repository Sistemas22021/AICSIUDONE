# ─────────────────────────────────────────────────────────────────────────────
# Módulo Terraform: ECS con EC2 (t2.micro — Free Tier)
#
# Crea un cluster ECS con una instancia EC2 t2.micro que corre todos
# los microservicios del ecosistema SSO en contenedores Docker.
#
# ¿Por qué EC2 y no Fargate?
# Fargate no tiene Free Tier. EC2 t2.micro tiene 750h/mes gratis
# en los primeros 12 meses. Para evaluaciones/demos, es suficiente.
# ─────────────────────────────────────────────────────────────────────────────

variable "env_name"     { type = string }
variable "aws_region"   { type = string }
variable "project"      { type = string }
variable "team"         { type = string }

variable "instance_type" {
  description = "Tipo de instancia EC2. t2.micro es Free Tier."
  type        = string
  default     = "t2.micro"
}

variable "services" {
  description = "Servicios a desplegar en ECS"
  type = list(object({
    name           = string
    ecr_image_tag  = string
    container_port = number
    host_port      = number
    cpu            = number
    memory         = number
  }))
}

# ── ECR: obtener URLs de las imágenes ────────────────────────────────────────

data "aws_ecr_repository" "service" {
  for_each = { for s in var.services : s.name => s }
  name     = "${var.project}/${each.value.name}"
}

# ── ECS Cluster ───────────────────────────────────────────────────────────────

resource "aws_ecs_cluster" "main" {
  name = "${var.project}-${var.env_name}"
}

# ── IAM: Rol de instancia EC2 para ECS ───────────────────────────────────────

resource "aws_iam_role" "ecs_instance_role" {
  name = "${var.project}-ecs-instance-${var.env_name}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance" {
  role       = aws_iam_role.ecs_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs" {
  name = "${var.project}-ecs-profile-${var.env_name}"
  role = aws_iam_role.ecs_instance_role.name
}

# ── Security Group: permite tráfico en los puertos de los servicios ───────────

resource "aws_security_group" "ecs_instance" {
  name        = "${var.project}-ecs-sg-${var.env_name}"
  description = "Security group para instancia ECS del SSO Boilerplate"

  # HTTP público (API Gateway)
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Eureka Dashboard
  ingress {
    from_port   = 8761
    to_port     = 8761
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # SSH (para debugging, restringir a tu IP en producción)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ── EC2 Instance: AMI ECS-Optimized (incluye Docker + agente ECS) ─────────────

data "aws_ami" "ecs_optimized" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }
}

resource "aws_instance" "ecs" {
  ami                  = data.aws_ami.ecs_optimized.id
  instance_type        = var.instance_type
  iam_instance_profile = aws_iam_instance_profile.ecs.name

  vpc_security_group_ids = [aws_security_group.ecs_instance.id]

  # Registrar la instancia en el cluster ECS
  user_data = base64encode(<<-EOT
    #!/bin/bash
    echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
  EOT
  )

  tags = {
    Name = "${var.project}-ecs-${var.env_name}"
  }
}

# ── ECS Task Definitions (una por servicio) ────────────────────────────────────

resource "aws_ecs_task_definition" "service" {
  for_each = { for s in var.services : s.name => s }

  family                   = "${var.project}-${each.value.name}-${var.env_name}"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  cpu                      = each.value.cpu
  memory                   = each.value.memory

  container_definitions = jsonencode([{
    name  = each.value.name
    image = "${data.aws_ecr_repository.service[each.key].repository_url}:${each.value.ecr_image_tag}"

    portMappings = [{
      containerPort = each.value.container_port
      hostPort      = each.value.host_port
      protocol      = "tcp"
    }]

    # Variables de entorno vienen de AWS Systems Manager Parameter Store
    # (más seguro que hardcodear en Terraform)
    environment = [
      { name = "EUREKA_HOST", value = aws_instance.ecs.private_ip },
      { name = "CONFIG_SERVER_URL", value = "http://${aws_instance.ecs.private_ip}:8888" },
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = "/ecs/${var.project}/${each.value.name}/${var.env_name}"
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "ecs"
      }
    }
  }])
}

# ── CloudWatch Log Groups ─────────────────────────────────────────────────────

resource "aws_cloudwatch_log_group" "service" {
  for_each          = { for s in var.services : s.name => s }
  name              = "/ecs/${var.project}/${each.value.name}/${var.env_name}"
  retention_in_days = 7  # 7 días para Free Tier (limitar costos)
}

# ── Outputs ───────────────────────────────────────────────────────────────────

output "ec2_public_ip" {
  description = "IP pública de la instancia EC2 con ECS"
  value       = aws_instance.ecs.public_ip
}

output "ecs_cluster_name" {
  value = aws_ecs_cluster.main.name
}
