# run_maintainer.ps1
# Script para levantar los módulos de Users Maintainer en terminales separadas

$RootPath = Get-Location
$EnvFile = "$RootPath\.env"

function Start-ServiceInNewWindow {
    param (
        [string]$Name,
        [string]$Path,
        [string]$Command
    )
    Write-Host "Iniciando $Name en $Path..." -ForegroundColor Cyan
    
    # Script para cargar .env (si existe) y ejecutar comando
    $StartupScript = @"
        if (Test-Path '$EnvFile') {
            Get-Content '$EnvFile' | Where-Object { `$_ -notmatch '^#' -and `$_ -match '=' } | ForEach-Object {
                `$name, `$value = `$_ -split '=', 2
                [Environment]::SetEnvironmentVariable(`$name.Trim(), `$value.Trim(), 'Process')
            }
        }
        cd '$Path'
        $Command
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $StartupScript
}

# 1. Backend: Users Maintainer API (Port 8085)
Start-ServiceInNewWindow "Maintainer API" "$RootPath\teams\main\backend\users-maintainer" ".\mvnw.cmd spring-boot:run"

# 2. Frontend: Users Maintainer Web (Port 3005)
Start-ServiceInNewWindow "Maintainer Web" "$RootPath\teams\main\frontend\users-maintainer-web" "npm run dev"

Write-Host "`nMódulos de Maintainer iniciados." -ForegroundColor Green
Write-Host "API: http://localhost:8085/swagger-ui.html"
Write-Host "Web: http://localhost:3005/users"
