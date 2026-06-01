# run_local.ps1
# Script para levantar el ecosistema SSO en terminales separadas

$RootPath = Get-Location
$EnvFile = "$RootPath\.env"

function Start-ServiceInNewWindow {
    param (
        [string]$Name,
        [string]$Path,
        [string]$Command
    )
    Write-Host "Iniciando $Name en $Path..." -ForegroundColor Cyan
    
    # Script para cargar .env y ejecutar comando
    $StartupScript = @"
        Get-Content '$EnvFile' | Where-Object { `$_ -notmatch '^#' -and `$_ -match '=' } | ForEach-Object {
            `$name, `$value = `$_ -split '=', 2
            [Environment]::SetEnvironmentVariable(`$name.Trim(), `$value.Trim(), 'Process')
        }
        cd '$Path'
        $Command
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $StartupScript
}

# 1. Eureka Server
Start-ServiceInNewWindow "Eureka Server" "$RootPath\teams\main\backend\eureka-server" "mvn spring-boot:run"

# Esperar un poco a que Eureka empiece
Start-Sleep -Seconds 15

# 2. Config Server
Start-ServiceInNewWindow "Config Server" "$RootPath\teams\main\backend\config-server" "mvn spring-boot:run"

# Esperar a que Config Server empiece
Start-Sleep -Seconds 15

# 3. Auth Service
Start-ServiceInNewWindow "Auth Service" "$RootPath\teams\main\backend\auth-service" "mvn spring-boot:run"

# 4. API Gateway
Start-ServiceInNewWindow "API Gateway" "$RootPath\teams\main\backend\api-gateway" "mvn spring-boot:run"

# 5. Login MFE
Start-ServiceInNewWindow "Login MFE" "$RootPath\teams\main\frontend\login-mfe" "npm run dev"

# 6. Consumer App
Start-ServiceInNewWindow "Consumer App" "$RootPath\teams\main\frontend\consumer-app" "npm run dev"

Write-Host "`nEcosistema en marcha. Verifica los logs en las ventanas abiertas." -ForegroundColor Green
Write-Host "Eureka: http://localhost:8761"
Write-Host "Config: http://localhost:8888/auth-service/dev"
Write-Host "Auth UI: http://localhost:8080/swagger-ui.html"
Write-Host "Login: http://localhost:3000"
Write-Host "App: http://localhost:3001"
