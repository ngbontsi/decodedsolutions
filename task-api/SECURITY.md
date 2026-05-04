# Security Setup Guide

## Overview

The Task API now uses **Spring Security OAuth2 Resource Server** for JWT-based authentication.

## Components

1. **Backend (Spring Boot)** - Protected REST API
2. **Flutter App** - Mobile client with secure token storage
3. **Keycloak** - Identity Provider (OAuth2/OIDC)

## Quick Start

### 1. Start Docker Compose (includes Keycloak)
```bash
docker-compose up -d
```

### 2. Import Keycloak Realm
The realm is auto-imported via `/keycloak/task-api-realm.json`

### 3. Test Credentials
- **Admin:** `admin` / `admin123`
- **User:** `user` / `user123`

### 4. Access Keycloak Admin Console
- URL: `http://localhost:8180`
- Login: `admin` / `admin`

## API Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/auth/login` | POST | No | Login (returns JWT) |
| `/api/tasks` | GET | Yes | Get all tasks |
| `/api/tasks/{id}` | GET | Yes | Get task by ID |
| `/api/tasks` | POST | Yes | Create task |
| `/api/tasks/{id}` | PUT | Yes | Update task |
| `/api/tasks/{id}` | DELETE | Yes | Delete task |
| `/swagger-ui.html` | GET | No | API documentation |

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `KEYCLOAK_ISSUER_URI` | Keycloak issuer URL | `http://keycloak:8080/realms/task-api` |

## JWT Token Flow

1. Client logs in via `/api/auth/login`
2. Server validates credentials with Keycloak
3. JWT token returned to client
4. Client includes token in `Authorization: Bearer <token>` header
5. Backend validates JWT on each request

## Flutter App Setup

```bash
cd task-app
flutter pub get
flutter run
```

Configure API URL in `lib/config/api_config.dart`
