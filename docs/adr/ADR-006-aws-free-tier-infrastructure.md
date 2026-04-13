# ADR-006: Infraestructura AWS — Free Tier con ECS EC2 y S3

## Status: Aceptado | Date: 2026-04

## Contexto

El ecosistema necesita infraestructura en AWS para las evaluaciones y demos.
El requisito es que el costo sea **cero o mínimo**, usando el Free Tier de AWS.

## Decisión

### Compute: ECS con EC2 t2.micro (NO Fargate)
- **Fargate**: $0.04048/vCPU-hora + $0.004445/GB-hora → costoso para 4 servicios
- **EC2 t2.micro**: 750 horas/mes **gratuitas** durante los primeros 12 meses
- **Estrategia**: Encender la instancia SOLO para evaluaciones, apagarla después

```bash
# Encender para evaluación
aws ec2 start-instances --instance-ids <id>

# Apagar después de la evaluación
aws ec2 stop-instances --instance-ids <id>
```

### Frontends: S3 Static Hosting (NO CloudFront)
- **CloudFront**: $0.0085/10k requests → pequeño pero innecesario para demos
- **S3 Static Hosting**: 5GB de almacenamiento gratuito + 20k requests/mes gratuitas
- **Tradeoff**: URLs menos amigables pero totalmente funcionales para evaluaciones

### Load Balancer: NGINX en el EC2 (NO ALB)
- **ALB**: ~$16/mes → demasiado costoso
- **NGINX**: Configurado como proxy reverso directamente en el EC2, $0

### Base de Datos: Neon/Supabase → RDS (Fase 2)
- Ver ADR-005

## Estimación de costos para evaluaciones

| Servicio | Costo por evaluación (2h) | Free Tier |
|---|---|---|
| EC2 t2.micro | $0 (primeros 12 meses) | ✅ |
| S3 Static | $0 | ✅ |
| ECR Storage (500MB) | ~$0.05/mes | ❌ Mínimo |
| CloudWatch Logs (7 días) | ~$0 | ✅ |
| **Total** | **≈ $0** | |

## Prerequisitos manuales (una sola vez)

Crear el bucket de estado de Terraform y la tabla DynamoDB para el lock:
```bash
aws s3 mb s3://sso-terraform-state-dev --region us-east-1
aws dynamodb create-table \
  --table-name sso-terraform-locks-dev \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1
```
