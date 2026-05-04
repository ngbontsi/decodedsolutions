import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/task.dart';
import 'auth_service.dart';

class TaskService {
  final AuthService _authService = AuthService();

  Future<Map<String, String>> _getHeaders() async {
    final token = await _authService.getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };
  }

  Future<List<Task>> getAllTasks() async {
    try {
      final headers = await _getHeaders();
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}${ApiConfig.tasks}'),
        headers: headers,
      ).timeout(ApiConfig.connectTimeout);

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        return data.map((json) => Task.fromJson(json)).toList();
      }
      throw Exception('Failed to load tasks: ${response.statusCode}');
    } catch (e) {
      rethrow;
    }
  }

  Future<Task> getTaskById(int id) async {
    try {
      final headers = await _getHeaders();
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}${ApiConfig.tasks}/$id'),
        headers: headers,
      ).timeout(ApiConfig.connectTimeout);

      if (response.statusCode == 200) {
        return Task.fromJson(jsonDecode(response.body));
      }
      throw Exception('Failed to load task: ${response.statusCode}');
    } catch (e) {
      rethrow;
    }
  }

  Future<Task> createTask(Task task) async {
    try {
      final headers = await _getHeaders();
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}${ApiConfig.tasks}'),
        headers: headers,
        body: jsonEncode(task.toJson()),
      ).timeout(ApiConfig.connectTimeout);

      if (response.statusCode == 201) {
        return Task.fromJson(jsonDecode(response.body));
      }
      throw Exception('Failed to create task: ${response.statusCode}');
    } catch (e) {
      rethrow;
    }
  }

  Future<Task> updateTask(int id, Task task) async {
    try {
      final headers = await _getHeaders();
      final response = await http.put(
        Uri.parse('${ApiConfig.baseUrl}${ApiConfig.tasks}/$id'),
        headers: headers,
        body: jsonEncode(task.toJson()),
      ).timeout(ApiConfig.connectTimeout);

      if (response.statusCode == 200) {
        return Task.fromJson(jsonDecode(response.body));
      }
      throw Exception('Failed to update task: ${response.statusCode}');
    } catch (e) {
      rethrow;
    }
  }

  Future<void> deleteTask(int id) async {
    try {
      final headers = await _getHeaders();
      final response = await http.delete(
        Uri.parse('${ApiConfig.baseUrl}${ApiConfig.tasks}/$id'),
        headers: headers,
      ).timeout(ApiConfig.connectTimeout);

      if (response.statusCode != 204) {
        throw Exception('Failed to delete task: ${response.statusCode}');
      }
    } catch (e) {
      rethrow;
    }
  }
}
