@echo off
setlocal

echo Deploying Task API to Kubernetes...

kubectl apply -f namespace.yaml

kubectl apply -f config.yaml -n task-api

kubectl apply -f postgres.yaml -n task-api

echo Waiting for PostgreSQL...
kubectl wait --for=condition=available --timeout=120s deployment/postgres -n task-api

kubectl apply -f deployment.yaml -n task-api
kubectl apply -f service.yaml -n task-api
kubectl apply -f ingress.yaml -n task-api

echo Deployment complete!
