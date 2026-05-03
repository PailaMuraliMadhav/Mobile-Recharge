# ============================================================
# OmniRecharge — Run Tests + SonarQube for ALL microservices
#
# Usage:
#   .\docs\run-all-tests-sonar.ps1
#   .\docs\run-all-tests-sonar.ps1 -Token "your_sonar_token"
#
# Prerequisites:
#   - SonarQube running on http://localhost:9000
#   - MySQL running locally
#   - RabbitMQ running locally
# ============================================================

param(
    [string]$Token   = "squ_60a20cf2f2e8e9a9ecf48a28b737ff742e16bccf",
    [string]$SonarUrl = "http://localhost:9000"
)

$ErrorActionPreference = "Continue"

$services = @(
    @{ name = "user-service";        dir = "user-service";        key = "UserService" },
    @{ name = "PaymentService";      dir = "PaymentService";      key = "PaymentService" },
    @{ name = "RechargeService";     dir = "RechargeService";     key = "RechargeService" },
    @{ name = "OperatorService";     dir = "OperatorService";     key = "OperatorService" },
    @{ name = "NotificationService"; dir = "NotificationService"; key = "NotificationService" }
)

$results = @()
$root = Split-Path -Parent $PSScriptRoot   # workspace root (parent of docs/)

foreach ($svc in $services) {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  $($svc.name)" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan

    $svcPath     = Join-Path $root $svc.dir
    $jacocoXml   = Join-Path $svcPath "target\site\jacoco\jacoco.xml"
    $javaBinaries = Join-Path $svcPath "target\classes"

    Push-Location $svcPath

    # ── Step 1: tests + Jacoco report ──────────────────────────────
    Write-Host "  Running tests..." -ForegroundColor Yellow
    & .\mvnw.cmd verify "-Dspring.profiles.active=test" --no-transfer-progress -q
    $testExit = $LASTEXITCODE

    if ($testExit -ne 0) {
        Write-Host "  ❌ Tests FAILED" -ForegroundColor Red
        $results += [PSCustomObject]@{ Service = $svc.name; Tests = "FAIL"; Sonar = "SKIPPED"; Coverage = "—" }
        Pop-Location
        continue
    }

    Write-Host "  ✅ Tests PASSED" -ForegroundColor Green

    # Confirm the Jacoco XML was actually generated
    if (-not (Test-Path $jacocoXml)) {
        Write-Host "  ⚠️  jacoco.xml not found at: $jacocoXml" -ForegroundColor Yellow
    } else {
        Write-Host "  📊 Jacoco report: $jacocoXml" -ForegroundColor DarkGray
    }

    # ── Step 2: Sonar analysis ─────────────────────────────────────
    Write-Host "  Running Sonar analysis..." -ForegroundColor Yellow
    & .\mvnw.cmd sonar:sonar `
        "-Dsonar.projectKey=$($svc.key)" `
        "-Dsonar.host.url=$SonarUrl" `
        "-Dsonar.token=$Token" `
        "-Dsonar.coverage.jacoco.xmlReportPaths=$jacocoXml" `
        "-Dsonar.java.binaries=$javaBinaries" `
        --no-transfer-progress -q
    $sonarExit = $LASTEXITCODE

    if ($sonarExit -eq 0) {
        Write-Host "  ✅ Sonar analysis sent" -ForegroundColor Green
        $results += [PSCustomObject]@{ Service = $svc.name; Tests = "PASS"; Sonar = "DONE"; Coverage = "see dashboard" }
    } else {
        Write-Host "  ⚠️  Sonar analysis failed (exit $sonarExit)" -ForegroundColor Yellow
        $results += [PSCustomObject]@{ Service = $svc.name; Tests = "PASS"; Sonar = "FAIL"; Coverage = "—" }
    }

    Pop-Location
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  SUMMARY" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
$results | Format-Table -AutoSize

Write-Host ""
Write-Host "SonarQube dashboard → $SonarUrl/projects" -ForegroundColor Cyan
