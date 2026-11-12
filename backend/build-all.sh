#!/bin/bash
set -e
cd "/mnt/c/Users/admin/Desktop/decoded solution platform/platform/backend"
for svc in auth-service restaurant-service guesthouse-service marketplace-service family-tree-service; do
  echo "=== Building $svc ==="
  cd "$svc"
  mvn clean install -q
  echo "SUCCESS: $svc"
  cd ..
done
echo "=== All builds complete ==="
