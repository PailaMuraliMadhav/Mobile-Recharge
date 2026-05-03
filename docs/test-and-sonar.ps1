# Test and SonarQube Analysis Script
# Usage: .\test-and-sonar.ps1 <service-name>
# Example: .\test-and-sonar.ps1 user-service

param(
    [Parameter(Mandatory=$false)]
    [string]$ServiceName = "all"
)

$services = @(
    "user-service",
    "PaymentService",
    "RechargeService",
    "OperatorService",
    "NotificationService"
)

function Test-Service {
    param([string]$service)
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Testing: $service" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
    
    if (!(Test-Path $service)) {
        Write-Host "✗ Service directory not found: $service" -ForegroundColor Red
        return $false
    }
    
    Push-Location $service
    
    try {
        # Run tests with coverage
        Write-Host "Running tests with coverage..." -ForegroundColor Yellow
        ./mvnw clean test
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Tests passed for $service" -ForegroundColor Green
            
            # Run SonarQube analysis
            Write-Host "`nRunning SonarQube analysis..." -ForegroundColor Yellow
            ./mvnw sonar:sonar `
                -Dsonar.projectKey=$service `
                -Dsonar.projectName=$service `
                -Dsonar.host.url=http://localhost:9000
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✓ SonarQube analysis completed for $service" -ForegroundColor Green
                return $true
            } else {
                Write-Host "✗ SonarQube analysis failed for $service" -ForegroundColor Red
                return $false
            }
        } else {
            Write-Host "✗ Tests failed for $service" -ForegroundColor Red
            return $false
        }
    }
    finally {
        Pop-Location
    }
}

# Main execution
Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Test & SonarQube Analysis Runner     ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝`n" -ForegroundColor Cyan

if ($ServiceName -eq "all") {
    $results = @{}
    foreach ($service in $services) {
        $results[$service] = Test-Service $service
    }
    
    # Summary
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "SUMMARY" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    foreach ($service in $services) {
        $status = if ($results[$service]) { "✓ PASSED" } else { "✗ FAILED" }
        $color = if ($results[$service]) { "Green" } else { "Red" }
        Write-Host "$service : $status" -ForegroundColor $color
    }
} else {
    Test-Service $ServiceName
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "View SonarQube results at:" -ForegroundColor Cyan
Write-Host "http://localhost:9000" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan
