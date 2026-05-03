# ============================================================
# OmniRecharge — Start All Microservices Locally
# Usage: .\docs\start-all-services.ps1
# Requires: MySQL, RabbitMQ running locally
# ============================================================

# Load .env variables
$envFile = Join-Path (Split-Path $PSScriptRoot -Parent) ".env"
Get-Content $envFile | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        $key   = $Matches[1].Trim()
        $value = $Matches[2].Trim().Trim("'")
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  Loaded: $key" -ForegroundColor DarkGray
    }
}

$root = Split-Path $PSScriptRoot -Parent

$services = @(
    @{ name = "eureka-server";     dir = "eureka-server";     port = 8761; waitSec = 15 },
    @{ name = "config-server";     dir = "config-server";     port = 8888; waitSec = 15 },
    @{ name = "api-gateway";       dir = "api-gateway";       port = 8080; waitSec = 20 },
    @{ name = "user-service";      dir = "user-service";      port = 8081; waitSec = 20 },
    @{ name = "operator-service";  dir = "OperatorService";   port = 8082; waitSec = 20 },
    @{ name = "recharge-service";  dir = "RechargeService";   port = 8083; waitSec = 20 },
    @{ name = "payment-service";   dir = "PaymentService";    port = 8084; waitSec = 20 },
    @{ name = "notification-service"; dir = "NotificationService"; port = 8085; waitSec = 15 }
)

$pids = @()

foreach ($svc in $services) {
    $svcPath = Join-Path $root $svc.dir
    Write-Host ""
    Write-Host "Starting $($svc.name) on port $($svc.port)..." -ForegroundColor Cyan

    $proc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c", "cd /d `"$svcPath`" && mvnw.cmd spring-boot:run" `
        -PassThru -WindowStyle Minimized
    $pids += $proc.Id

    Write-Host "  PID: $($proc.Id) — waiting $($svc.waitSec)s..." -ForegroundColor DarkGray
    Start-Sleep -Seconds $svc.waitSec

    # Quick health check
    try {
        $null = Invoke-WebRequest -Uri "http://localhost:$($svc.port)/actuator/health" -TimeoutSec 3 -ErrorAction Stop
        Write-Host "  ✅ $($svc.name) is UP" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠️  $($svc.name) health check pending (may still be starting)" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  All services started" -ForegroundColor Cyan
Write-Host "  Eureka:   http://localhost:8761" -ForegroundColor White
Write-Host "  Gateway:  http://localhost:8080" -ForegroundColor White
Write-Host "  Swagger (via gateway):" -ForegroundColor White
Write-Host "    User:     http://localhost:8081/swagger-ui.html" -ForegroundColor White
Write-Host "    Operator: http://localhost:8082/swagger-ui.html" -ForegroundColor White
Write-Host "    Recharge: http://localhost:8083/swagger-ui.html" -ForegroundColor White
Write-Host "    Payment:  http://localhost:8084/swagger-ui.html" -ForegroundColor White
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "PIDs: $($pids -join ', ')" -ForegroundColor DarkGray
Write-Host "To stop all: Stop-Process -Id $($pids -join ',') -Force" -ForegroundColor DarkGray
