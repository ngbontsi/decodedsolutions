#!/bin/bash
# Ubuntu Development Environment Setup Script
# Run: chmod +x ubuntu-setup.sh && ./ubuntu-setup.sh

set -e

echo "=========================================="
echo " Ubuntu Development Environment Setup"
echo "=========================================="

# Update system
echo "[1/8] Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 23
echo "[2/8] Installing Java 23..."
sudo apt install -y openjdk-23-jdk wget

# Install Maven
echo "[3/8] Installing Maven..."
sudo apt install -y maven

# Install Docker
echo "[4/8] Installing Docker..."
sudo apt install -y docker.io docker-compose

# Install kubectl
echo "[5/8] Installing kubectl..."
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
rm kubectl

# Install Flutter
echo "[6/8] Installing Flutter..."
cd /opt
sudo apt install -y clang cmake ninja-build pkg-config libgtk-3-dev
wget -q https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.24.0-stable.tar.xz
sudo tar xf flutter_linux_3.24.0-stable.tar.xz
rm flutter_linux_3.24.0-stable.tar.xz

# Add Flutter to PATH
echo 'export PATH="$PATH:/opt/flutter/bin"' >> ~/.bashrc
export PATH="$PATH:/opt/flutter/bin"

# Install VS Code
echo "[7/8] Installing VS Code..."
wget -q https://code.visualstudio.com/sha/download?build=stable -O code.deb
sudo dpkg -i code.deb || sudo apt-get install -f -y
rm code.deb

# Install GitHub CLI
echo "[8/8] Installing GitHub CLI..."
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
sudo apt update
sudo apt install -y gh

# Final setup
echo ""
echo "=========================================="
echo " Setup Complete!"
echo "=========================================="
echo ""
echo "Run these commands to activate:"
echo "  source ~/.bashrc"
echo "  flutter --version"
echo "  docker --version"
echo "  gh auth login"
echo ""
echo "Then navigate to your project:"
echo "  cd /path/to/your/project"
