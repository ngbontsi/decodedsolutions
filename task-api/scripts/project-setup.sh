#!/bin/bash
# Project Setup Script
# Run after ubuntu-setup.sh

set -e

PROJECT_DIR="/path/to/project"  # CHANGE THIS

echo "Setting up project..."

cd "$PROJECT_DIR"

# Build the application
echo "Building Spring Boot application..."
mvn clean package -DskipTests

# Start Docker services
echo "Starting Docker services..."
docker-compose up -d

# Wait for services
echo "Waiting for services..."
sleep 10

# Show status
echo ""
echo "=========================================="
echo " Services Status:"
echo "=========================================="
docker-compose ps

echo ""
echo "Access points:"
echo "  App:        http://localhost:8080"
echo "  Swagger:    http://localhost:8080/swagger-ui.html"
echo "  Keycloak:   http://localhost:8180"
echo ""
