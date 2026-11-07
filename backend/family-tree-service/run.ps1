# Runs the service locally against the Postgres container.
# Usage:  .\run.ps1
$ErrorActionPreference = "Stop"

if (-not $env:JAVA_HOME) {
    $candidate = Get-ChildItem "$env:USERPROFILE\.jdks" -Directory -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -match '^(ms-)?21' } |
        Select-Object -First 1
    if ($candidate) {
        $env:JAVA_HOME = $candidate.FullName
    } else {
        throw "Set JAVA_HOME to a JDK 21 installation, then run this script again."
    }
}

& "$PSScriptRoot\mvnw.cmd" -o -B -q package -DskipTests
if ($LASTEXITCODE -ne 0) { throw "Build failed." }

docker compose -f "$PSScriptRoot\compose.yaml" up -d familytree-db

Write-Host "Waiting for Postgres..."
for ($i = 0; $i -lt 40; $i++) {
    docker exec familytree-db pg_isready -U familytree -d familytree 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) { break }
    Start-Sleep -Seconds 2
}

$env:DB_URL = "jdbc:postgresql://localhost:5432/familytree"
$env:DB_USERNAME = "familytree"
$env:DB_PASSWORD = "familytree"
$env:SERVER_PORT = "8085"

Write-Host "API:      http://localhost:8085/api/v1"
Write-Host "Swagger:  http://localhost:8085/swagger-ui.html"
& "$env:JAVA_HOME\bin\java.exe" -jar "$PSScriptRoot\target\family-tree-service-1.0.0.jar"
