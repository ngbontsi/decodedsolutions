# Starts everything the family-tree UI needs, in the background:
#   1. the Postgres container
#   2. the API jar on :8085
#   3. the Vite dev server on :5174
#
# Usage:  .\start-local.ps1
#         .\start-local.ps1 -SkipUi
#
# Safe to re-run: it skips whatever is already listening.

param(
    [switch]$SkipUi
)

$ErrorActionPreference = "Stop"
$service = Split-Path -Parent $MyInvocation.MyCommand.Path
$repo = Split-Path -Parent (Split-Path -Parent $service)
$frontend = Join-Path $repo "frontend\family-tree"
$jar = Join-Path $service "target\family-tree-service-1.0.0.jar"

if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Users\admin\.jdks\ms-21.0.11"
}

function Test-Port([int]$port) {
    return [bool](Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
}

# 1. Database
$db = docker ps --filter "name=familytreedb" --filter "status=running" --format "{{.Names}}"
if ($db -notcontains "familytreedb") {
    Write-Host "Starting familytreedb..."
    docker start familytree-db | Out-Null
} else {
    Write-Host "Database already running."
}

# 2. API
if (Test-Port 8085) {
    Write-Host "API already listening on :8085."
} else {
    if (-not (Test-Path $jar)) {
        Write-Host "Jar missing, building (offline)..."
        Push-Location $service
        & .\mvnw.cmd -o -B -q package -DskipTests
        $buildExit = $LASTEXITCODE
        Pop-Location
        if ($buildExit -ne 0) { throw "Build failed. Run '.\mvnw.cmd -o -B verify' for details." }
    }

    Write-Host "Starting API on :8085..."
    $env:DB_URL = "jdbc:postgresql://localhost:5432/familytreedb"
    $env:DB_USERNAME = "platformuser"
    $env:DB_PASSWORD = "platformpass"
    $env:SERVER_PORT = "8085"
    Start-Process -FilePath (Join-Path $env:JAVA_HOME "bin\java.exe") `
        -ArgumentList @("-jar", $jar) -WorkingDirectory $service -WindowStyle Hidden

    for ($i = 0; $i -lt 45; $i++) {
        try {
            $health = Invoke-RestMethod "http://localhost:8085/actuator/health" -TimeoutSec 3
            if ($health.status -eq "UP") { break }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    if ($health.status -ne "UP") { throw "API did not become healthy on :8085." }
    Write-Host "API is healthy."
}

# 3. UI
if ($SkipUi) {
    Write-Host "Skipping UI (-SkipUi)."
} elseif (Test-Port 5174) {
    Write-Host "UI already listening on :5174."
} else {
    Write-Host "Starting UI on :5174..."
    Start-Process -FilePath "npm.cmd" -ArgumentList @("run", "dev") -WorkingDirectory $frontend -WindowStyle Hidden
    Start-Sleep -Seconds 3
}

Write-Host ""
Write-Host "API  http://localhost:8085/swagger-ui.html"
Write-Host "UI   http://localhost:5174"
