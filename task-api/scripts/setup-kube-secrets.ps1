# Kubernetes Secrets Setup Script
# Run this AFTER setup-secrets.ps1

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubToken,

    [Parameter(Mandatory=$true)]
    [string]$DevKubeconfigPath,

    [Parameter(Mandatory=$true)]
    [string]$StagingKubeconfigPath,

    [Parameter(Mandatory=$true)]
    [string]$ProdKubeconfigPath,

    [string]$Repo = "ngbontsi/task-api"
)

$ghPath = "C:\Program Files\GitHub CLI\gh.exe"

Write-Host "Setting up Kubernetes secrets..." -ForegroundColor Cyan

# Encode and set kubeconfigs
$devConfig = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content $DevKubeconfigPath -Raw)))
$stagingConfig = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content $StagingKubeconfigPath -Raw)))
$prodConfig = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content $ProdKubeconfigPath -Raw)))

& $ghPath secret set KUBE_CONFIG_DEV --body $devConfig --repo $Repo
& $ghPath secret set KUBE_CONFIG_STAGING --body $stagingConfig --repo $Repo
& $ghPath secret set KUBE_CONFIG_PROD --body $prodConfig --repo $Repo

Write-Host "`nKubernetes secrets added successfully!" -ForegroundColor Green
