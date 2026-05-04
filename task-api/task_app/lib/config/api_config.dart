class ApiConfig {
  static const String baseUrl = 'http://localhost:8080'; // API Gateway
  static const String apiPrefix = '/api';
  
  // Endpoints
  static const String tasks = '$apiPrefix/tasks';
  static const String auth = '$apiPrefix/auth';
  static const String login = '$auth/login';
  
  // Timeouts
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}
