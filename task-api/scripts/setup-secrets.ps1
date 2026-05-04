# GitHub Secrets Setup Script
# Run this in PowerShell after you've created your GitHub PAT

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubToken,

    [Parameter(Mandatory=$true)]
    [string]$DockerPassword,

    [string]$Repo = "ngbontsi/task-api",
    [string]$Domain = "ngbontsi.com"
)

# Verify gh is installed
$ghPath = "C:\Program Files\GitHub CLI\gh.exe"
if (-not (Test-Path $ghPath)) {
    Write-Error "GitHub CLI not found. Install from: https://cli.github.com/"
    exit 1
}

Write-Host "Setting up secrets for $Repo..." -ForegroundColor Cyan

# Docker Registry
& $ghPath secret set DOCKER_REGISTRY --body "docker.io" --repo $Repo

# Docker Hub Credentials
& $ghPath secret set DOCKER_USERNAME --body "ngbontsi" --repo $Repo
& $ghPath secret set DOCKER_PASSWORD --body $DockerPassword --repo $Repo

# Domain
& $ghPath secret set DOMAIN --body $Domain --repo $Repo

Write-Host "`nSecrets added successfully!" -ForegroundColor Green
Write-Host "`nRemaining secrets to add manually:" -ForegroundColor Yellow
Write-Host "  - KUBE_CONFIG_DEV (base64 encoded kubeconfig for development cluster)"
Write-Host "  - KUBE_CONFIG_STAGING (base64 encoded kubeconfig for staging cluster)"
Write-Host "  - KUBE_CONFIG_PROD (base64 encoded kubeconfig for production cluster)"
Write-Host "`nTo encode kubeconfig: [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content 'path\to\kubeconfig' -Raw)))"
