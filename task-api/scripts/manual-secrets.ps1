# Create secrets manually without GitHub PAT
# Run this in PowerShell with GitHub CLI authenticated

$ghPath = "C:\Program Files\GitHub CLI\gh.exe"
$Repo = "ngbontsi/task-api"

Write-Host "Creating secrets for $Repo..." -ForegroundColor Cyan

# Example commands - replace values manually:
Write-Host "`nRun these commands:"

@"
# Docker Hub
gh secret set DOCKER_REGISTRY --body `"docker.io`" --repo $Repo
gh secret set DOCKER_USERNAME --body `"YOUR_DOCKER_USERNAME`" --repo $Repo
gh secret set DOCKER_PASSWORD --body `"YOUR_DOCKER_PAT`" --repo $Repo

# Domain
gh secret set DOMAIN --body `"yourdomain.com`" --repo $Repo

# Kubernetes (encode kubeconfig to base64 first)
`$devKube = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content 'C:\path\to\dev-kubeconfig' -Raw)))
gh secret set KUBE_CONFIG_DEV --body `$devKube --repo $Repo

# Staging
`$stagingKube = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content 'C:\path\to\staging-kubeconfig' -Raw)))
gh secret set KUBE_CONFIG_STAGING --body `$stagingKube --repo $Repo

# Production
`$prodKube = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content 'C:\path\to\prod-kubeconfig' -Raw)))
gh secret set KUBE_CONFIG_PROD --body `$prodKube --repo $Repo
"@

Write-Host "`nCopy and run the commands above in PowerShell." -ForegroundColor Yellow
