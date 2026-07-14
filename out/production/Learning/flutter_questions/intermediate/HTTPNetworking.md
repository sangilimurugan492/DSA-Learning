# HTTP & Networking

## Q1: How do you make HTTP requests with the `http` package?

```dart
// pubspec.yaml: http: ^1.1.0
import 'package:http/http.dart' as http;

// GET request
Future<User> fetchUser(int id) async {
  final response = await http.get(
    Uri.parse('https://api.example.com/users/$id'),
    headers: {'Authorization': 'Bearer $token'},
  );

  if (response.statusCode == 200) {
    return User.fromJson(jsonDecode(response.body));
  } else {
    throw Exception('Failed: ${response.statusCode}');
  }
}

// POST request
Future<User> createUser(User user) async {
  final response = await http.post(
    Uri.parse('https://api.example.com/users'),
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode(user.toJson()),
  );

  if (response.statusCode == 201) {
    return User.fromJson(jsonDecode(response.body));
  }
  throw Exception('Failed: ${response.statusCode}');
}

// PUT, PATCH, DELETE
await http.put(url, body: jsonEncode(data));
await http.patch(url, body: jsonEncode(data));
await http.delete(url);
```

---

## Q2: How do you use `dio` for advanced networking?

```dart
// pubspec.yaml: dio: ^5.4.0
import 'package:dio/dio.dart';

final dio = Dio(BaseOptions(
  baseUrl: 'https://api.example.com',
  connectTimeout: const Duration(seconds: 10),
  receiveTimeout: const Duration(seconds: 15),
  headers: {'Accept': 'application/json'},
));

// Simple GET
final response = await dio.get('/users/1');
print(response.data);

// POST with form data
final formData = FormData.fromMap({
  'name': 'Alice',
  'file': await MultipartFile.fromFile('path/to/file.jpg'),
});
await dio.post('/upload', data: formData);

// Download with progress
await dio.download(
  'https://example.com/file.zip',
  '/tmp/file.zip',
  onReceiveProgress: (received, total) {
    final progress = (received / total * 100).toStringAsFixed(0);
    print('Downloaded: $progress%');
  },
);

// Cancel request with CancelToken
final cancelToken = CancelToken();
dio.get('/data', cancelToken: cancelToken);
cancelToken.cancel('User cancelled');
```

---

## Q3: How do you set up interceptors in Dio?

```dart
// Interceptors — middleware for requests and responses
dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) {
    // Add auth token to every request
    final token = getToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    print('→ ${options.method} ${options.path}');
    handler.next(options);  // Continue
  },
  onResponse: (response, handler) {
    print('← ${response.statusCode} ${response.requestOptions.path}');
    handler.next(response);
  },
  onError: (error, handler) {
    print('✕ ${error.message}');

    // Auto-refresh token on 401
    if (error.response?.statusCode == 401) {
      final newToken = await refreshToken();
      setToken(newToken);
      // Retry the request
      final options = error.requestOptions;
      options.headers['Authorization'] = 'Bearer $newToken';
      final response = await dio.fetch(options);
      return handler.resolve(response);
    }

    handler.next(error);
  },
));

// Logging interceptor
dio.interceptors.add(LogInterceptor(
  request: true,
  requestHeader: true,
  requestBody: true,
  responseHeader: true,
  responseBody: true,
  error: true,
));

// Retry interceptor
dio.interceptors.add(RetryInterceptor(
  dio: dio,
  retries: 3,
  retryDelays: const [
    Duration(seconds: 1),
    Duration(seconds: 2),
    Duration(seconds: 4),
  ],
));
```

---

## Q4: How do you handle errors in networking?

```dart
// Custom API exception
class ApiException implements Exception {
  final int statusCode;
  final String message;
  final dynamic data;

  ApiException(this.statusCode, this.message, [this.data]);

  @override
  String toString() => 'ApiException($statusCode): $message';
}

// Repository with error handling
class UserRepository {
  final Dio _dio;

  UserRepository(this._dio);

  Future<User> getUser(int id) async {
    try {
      final response = await _dio.get('/users/$id');
      return User.fromJson(response.data);
    } on DioException catch (e) {
      switch (e.type) {
        case DioExceptionType.connectionTimeout:
          throw ApiException(-1, 'Connection timeout');
        case DioExceptionType.receiveTimeout:
          throw ApiException(-1, 'Receive timeout');
        case DioExceptionType.badResponse:
          final statusCode = e.response?.statusCode ?? 0;
          final message = e.response?.data?['message'] ?? 'Unknown error';
          throw ApiException(statusCode, message);
        case DioExceptionType.connectionError:
          throw ApiException(-1, 'No internet connection');
        default:
          throw ApiException(-1, e.message ?? 'Unknown error');
      }
    }
  }
}

// UI error handling
Future<void> _loadUser() async {
  setState(() => _isLoading = true);
  try {
    final user = await _repository.getUser(1);
    setState(() => _user = user);
  } on ApiException catch (e) {
    setState(() => _error = e.message);
  } catch (e) {
    setState(() => _error = 'Unexpected error: $e');
  } finally {
    setState(() => _isLoading = false);
  }
}
```

---

## Q5: How do you parse JSON in Dart?

```dart
// Manual parsing
class User {
  final int id;
  final String name;
  final String? email;
  final Address? address;

  User({required this.id, required this.name, this.email, this.address});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as int,
      name: json['name'] as String,
      email: json['email'] as String?,
      address: json['address'] != null
          ? Address.fromJson(json['address'] as Map<String, dynamic>)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'address': address?.toJson(),
    };
  }
}

class Address {
  final String street;
  final String city;

  Address({required this.street, required this.city});

  factory Address.fromJson(Map<String, dynamic> json) {
    return Address(
      street: json['street'] as String,
      city: json['city'] as String,
    );
  }

  Map<String, dynamic> toJson() => {'street': street, 'city': city};
}

// Usage
final json = jsonDecode(response.body) as Map<String, dynamic>;
final user = User.fromJson(json);
```

### Code generation (json_serializable)
```dart
// pubspec.yaml: json_serializable, build_runner
// 1. Annotate model
part 'user.g.dart';

@JsonSerializable()
class User {
  final int id;
  final String name;
  @JsonKey(name: 'email_address')  // Map different JSON key
  final String? email;

  User({required this.id, required this.name, this.email});

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}

// 2. Generate: dart run build_runner build
```

---

## Q6: How do you implement a REST API client?

```dart
// Generic API client
class ApiClient {
  final Dio _dio;

  ApiClient(this._dio);

  Future<T> get<T>(
    String path, {
    required T Function(Map<String, dynamic>) fromJson,
    Map<String, dynamic>? query,
  }) async {
    final response = await _dio.get(path, queryParameters: query);
    return fromJson(response.data as Map<String, dynamic>);
  }

  Future<List<T>> getList<T>(
    String path, {
    required T Function(Map<String, dynamic>) fromJson,
  }) async {
    final response = await _dio.get(path);
    final list = response.data as List;
    return list.map((e) => fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<T> post<T>(
    String path, {
    required Map<String, dynamic> body,
    required T Function(Map<String, dynamic>) fromJson,
  }) async {
    final response = await _dio.post(path, data: body);
    return fromJson(response.data as Map<String, dynamic>);
  }
}

// Repository
class UserRepository {
  final ApiClient _api;

  UserRepository(this._api);

  Future<User> getUser(int id) => _api.get(
    '/users/$id',
    fromJson: User.fromJson,
  );

  Future<List<User>> getUsers() => _api.getList(
    '/users',
    fromJson: User.fromJson,
  );

  Future<User> createUser(User user) => _api.post(
    '/users',
    body: user.toJson(),
    fromJson: User.fromJson,
  );
}
```

---

## Q7: How do you cache network responses?

```dart
// Simple in-memory cache
class CachedApiClient {
  final Dio _dio;
  final Map<String, _CacheEntry> _cache = {};

  CachedApiClient(this._dio);

  Future<Response> get(String path, {Duration cacheDuration = const Duration(minutes: 5)}) async {
    final cached = _cache[path];
    if (cached != null && cached.isValid) {
      return cached.response;
    }

    final response = await _dio.get(path);
    _cache[path] = _CacheEntry(response, DateTime.now().add(cacheDuration));
    return response;
  }

  void clearCache() => _cache.clear();
}

class _CacheEntry {
  final Response response;
  final DateTime expiresAt;
  _CacheEntry(this.response, this.expiresAt);
  bool get isValid => DateTime.now().isBefore(expiresAt);
}

// Using flutter_cache_manager for file caching
// pubspec.yaml: flutter_cache_manager: ^3.3.0
final cacheManager = DefaultCacheManager();
final file = await cacheManager.getSingleFile('https://example.com/image.jpg');
// Returns cached file if available, downloads if not
```

---

## 🔗 Related Topics
- [Dart Basics](../beginner/DartBasics.md)
- [State Management Advanced](StateManagementAdvanced.md)
- [Firebase Integration](FirebaseIntegration.md)
