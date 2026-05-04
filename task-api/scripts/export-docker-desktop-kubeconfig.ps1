# Get Docker Desktop Kubernetes kubeconfig
# Run this in PowerShell

# Method 1: Merge into default kubeconfig
kubectl config view --raw > "$env:USERPROFILE\.kube\config"

# Method 2: Export to separate file (recommended)
kubectl config view --raw > "$env:USERPROFILE\.kube\docker-desktop-kubeconfig"

# Verify connection
kubectl get nodes --kubeconfig "$env:USERPROFILE\.kube\docker-desktop-kubeconfig"

Write-Host "`nKubeconfig saved to: $env:USERPROFILE\.kube\docker-desktop-kubeconfig" -ForegroundColor Green
