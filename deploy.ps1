# Deploy all live demos and website to GitHub Pages
# Run this from the project root: ./deploy.ps1

$ErrorActionPreference = "Stop"
$PlatformRoot = $PSScriptRoot
$WebsiteRoot = "$PlatformRoot\..\business Processes\website"

$apps = @(
    @{ Name = "butcher-shop";       Path = "$PlatformRoot\frontend\butcher-shop";         Dest = "butcher-shop" },
    @{ Name = "guesthouse-client";  Path = "$PlatformRoot\frontend\guesthouse-client";    Dest = "guesthouse-client" },
    @{ Name = "dashboard";          Path = "$PlatformRoot\frontend\decoded_dashboard";    Dest = "dashboard" }
)

Write-Host "`n========== Deploying to GitHub Pages ==========" -ForegroundColor Cyan

# ── 1. Deploy client apps to decodedsolutions gh-pages ──
Write-Host "`n[1/2] Deploying client apps..." -ForegroundColor Yellow

foreach ($app in $apps) {
    Write-Host "`n  Building $($app.Name)..."
    Push-Location $app.Path
    try {
        npx --yes vite build 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) { throw "Build failed for $($app.Name)" }

        npx --yes gh-pages -d dist --dest $app.Dest 2>&1
        if ($LASTEXITCODE -ne 0) { throw "Deploy failed for $($app.Name)" }

        Write-Host "  ✓ $($app.Name) → https://ngbontsi.github.io/decodedsolutions/$($app.Dest)/" -ForegroundColor Green
    }
    catch {
        Write-Host "  ✗ Error: $_" -ForegroundColor Red
    }
    finally {
        Pop-Location
    }
}

# ── 2. Deploy website to decodedsolutionsite gh-pages ──
Write-Host "`n[2/2] Deploying marketing website..." -ForegroundColor Yellow

Push-Location $WebsiteRoot
try {
    Write-Host "`n  Building website..."
    npx --yes vite build 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) { throw "Website build failed" }

    npx --yes gh-pages -d dist 2>&1
    if ($LASTEXITCODE -ne 0) { throw "Website deploy failed" }

    Write-Host "`n  ✓ Website → https://ngbontsi.github.io/decodedsolutionsite/" -ForegroundColor Green
}
catch {
    Write-Host "  ✗ Error: $_" -ForegroundColor Red
}
finally {
    Pop-Location
}

Write-Host "`n========== Deploy Complete ==========" -ForegroundColor Cyan
