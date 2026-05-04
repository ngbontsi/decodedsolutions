class User {
  final String username;
  final String token;
  final DateTime? expiresAt;

  User({
    required this.username,
    required this.token,
    this.expiresAt,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['username'] as String,
      token: json['accessToken'] as String,
      expiresAt: json['expiresIn'] != null
          ? DateTime.now().add(Duration(seconds: json['expiresIn'] as int))
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'accessToken': token,
      if (expiresAt != null)
        'expiresIn': expiresAt!.difference(DateTime.now()).inSeconds,
    };
  }

  bool get isExpired {
    if (expiresAt == null) return false;
    return DateTime.now().isAfter(expiresAt!);
  }
}
