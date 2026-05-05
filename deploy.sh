#!/usr/bin/env bash
# Deploy all live demos and website to GitHub Pages
# Run with: ./deploy.sh  (from platform/ directory)

set -e

PLATFORM_ROOT="$(cd "$(dirname "$0")" && pwd)"
WEBSITE_ROOT="$PLATFORM_ROOT/../business Processes/website"

# Color codes
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo ""
echo "${CYAN}========== Deploying to GitHub Pages ==========${NC}"

# ── 1. Deploy client apps to decodedsolutions gh-pages ──
echo ""
echo "${YELLOW}[1/2] Deploying client apps...${NC}"

deploy_app() {
    local name="$1"
    local path="$2"
    local dest="$3"

    echo ""
    echo -n "  Building $name..."
    cd "$path"
    if ! npx --yes vite build > /dev/null 2>&1; then
        echo -e "${RED} ✗ Build failed${NC}"
        return 1
    fi
    echo " done"

    echo -n "  Deploying $name..."
    if ! npx --yes gh-pages -d dist --dest "$dest" 2>&1 > /dev/null; then
        echo -e "${RED} ✗ Deploy failed${NC}"
        return 1
    fi
    echo -e "${GREEN} ✓ $name → https://ngbontsi.github.io/decodedsolutions/$dest/${NC}"
}

deploy_app "butcher-shop"       "$PLATFORM_ROOT/frontend/butcher-shop"          "butcher-shop"
deploy_app "guesthouse-client"  "$PLATFORM_ROOT/frontend/guesthouse-client"     "guesthouse-client"
deploy_app "dashboard"          "$PLATFORM_ROOT/frontend/decoded_dashboard"     "dashboard"

# ── 2. Deploy website to decodedsolutionsite gh-pages ──
echo ""
echo "${YELLOW}[2/2] Deploying marketing website...${NC}"

echo -n "  Building website..."
cd "$WEBSITE_ROOT"
if ! npx --yes vite build > /dev/null 2>&1; then
    echo -e "${RED} ✗ Build failed${NC}"
    exit 1
fi
echo " done"

echo -n "  Deploying website..."
if ! npx --yes gh-pages -d dist 2>&1 > /dev/null; then
    echo -e "${RED} ✗ Deploy failed${NC}"
    exit 1
fi
echo -e "${GREEN} ✓ Website → https://ngbontsi.github.io/decodedsolutionsite/${NC}"

echo ""
echo "${CYAN}========== Deploy Complete ==========${NC}"
