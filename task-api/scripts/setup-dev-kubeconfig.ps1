# Encode kubeconfig for GitHub secrets
# Run this in PowerShell

param(
    [string]$KubeconfigPath = "$env:USERPROFILE\.kube\docker-desktop-kubeconfig",
    [string]$Repo = "ngbontsi/task-api"
)

$ghPath = "C:\Program Files\GitHub CLI\gh.exe"

if (-not (Test-Path $KubeconfigPath)) {
    Write-Error "Kubeconfig not found at: $KubeconfigPath"
    Write-Host "`nFirst run: .\scripts\export-docker-desktop-kubeconfig.ps1" -ForegroundColor Yellow
    exit 1
}

# Encode to base64
$encoded = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content $KubeconfigPath -Raw)))

# Set the secret
Write-Host "Setting KUBE_CONFIG_DEV secret..." -ForegroundColor Cyan
& $ghPath secret set KUBE_CONFIG_DEV --body $encoded --repo $Repo

Write-Host "`nDev kubeconfig secret set successfully!" -ForegroundColor Green
