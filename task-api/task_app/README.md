# Task Manager Flutter App

A Flutter mobile application for managing tasks with secure authentication.

## Features

- User authentication with JWT tokens
- Create, read, update, delete tasks
- Mark tasks as completed
- Pull-to-refresh
- Secure token storage

## Requirements

- Flutter SDK 3.0+
- Android Studio / Xcode
- Running backend API

## Setup

1. **Install dependencies:**
```bash
cd task-app
flutter pub get
```

2. **Configure API:**
Edit `lib/config/api_config.dart` to set your backend URL.

3. **Run the app:**
```bash
flutter run
```

## Project Structure

```
task-app/
├── lib/
│   ├── config/          # API configuration
│   ├── models/          # Data models
│   ├── screens/         # UI screens
│   ├── services/        # API services
│   └── main.dart        # App entry point
├── pubspec.yaml
└── README.md
```

## Screenshots

[Add screenshots here]

## Backend Requirements

The app requires the Task API running at the configured URL with:
- JWT authentication
- REST endpoints: `/api/tasks`, `/api/auth/login`
