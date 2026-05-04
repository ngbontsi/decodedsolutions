#!/bin/bash
set -e

echo "Deploying Task API to Kubernetes..."

# Create namespace
kubectl apply -f namespace.yaml

# Apply configs and secrets
kubectl apply -f config.yaml -n task-api

# Apply PostgreSQL
kubectl apply -f postgres.yaml -n task-api

# Wait for postgres
echo "Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=available --timeout=120s deployment/postgres -n task-api

# Apply Task API
kubectl apply -f deployment.yaml -n task-api
kubectl apply -f service.yaml -n task-api
kubectl apply -f ingress.yaml -n task-api

echo "Deployment complete!"
echo "API available at: http://task-api.local"
