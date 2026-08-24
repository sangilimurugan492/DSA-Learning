# HTTP Networking

## 📖 Explanation

HTTP networking in Flutter involves making API calls, parsing JSON responses, handling errors, and managing authentication. The `http` package is the standard starting point, with `dio` offering advanced features.

### HTTP Packages
| Package | Features | Use Case |
|---------|----------|----------|
| `http` | Basic GET/POST, simple | Simple apps, learning |
| `dio` | Interceptors, cancellation,FormData, retry | Production apps |
| `retrofit` | Type-safe API (code gen) | Clean architecture |

### HTTP Methods
| Method | Purpose | Idempotent |
|--------|---------|------------|
| GET | Fetch data | ✅ |
| POST | Create resource | ❌ |
| PUT | Update (full) | ✅ |
| PATCH | Update (partial) | ❌ |
| DELETE | Remove resource | ✅ |

### JSON Parsing
- `jsonDecode(response.body)` → `Map<String, dynamic>`
- Use `fromJson` factory constructor for model classes
- `jsonEncode(model.toJson())` for request bodies
- Use `json_serializable` or `freezed` for code generation

### Error Handling
| Exception | Cause | Handling |
|-----------|-------|----------|
| `SocketException` | No internet | Show offline message |
| `TimeoutException` | Server slow | Retry or cancel |
| `HttpException` | 4xx/5xx | Show error message |
| `FormatException` | Bad JSON | Parse safely |

### Status Codes
| Range | Meaning |
|-------|---------|
| 200-299 | Success |
| 300-399 | Redirect |
| 400-499 | Client error (auth, validation) |
| 500-599 | Server error |

### Best Practices
- Always wrap network calls in try-catch
- Use timeouts to avoid hanging
- Show loading/error states in UI
- Cache responses for offline support
- Use interceptors for auth tokens and logging
- Cancel in-flight requests when widget disposes

---

## 🧪 Code Example

```dart
// ── Basic HTTP with `http` package ──
import 'package:http/http.dart' as http;

class ApiService {
  static const _baseUrl = 'https://api.example.com';

  Future<List<User>> getUsers() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/users'),
        headers: {'Authorization': 'Bearer $token'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List data = jsonDecode(response.body);
        return data.map((json) => User.fromJson(json)).toList();
      } else {
        throw ApiException(response.statusCode, response.body);
      }
    } on SocketException {
      throw NetworkException('No internet connection');
    } on TimeoutException {
      throw NetworkException('Request timed out');
    }
  }

  Future<User> createUser(String name, String email) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/users'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'name': name, 'email': email}),
    );

    if (response.statusCode == 201) {
      return User.fromJson(jsonDecode(response.body));
    }
    throw ApiException(response.statusCode, 'Failed to create user');
  }
}

// ── Model with fromJson / toJson ──
class User {
  final int id;
  final String name;
  final String email;

  const User({required this.id, required this.name, required this.email});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as int,
      name: json['name'] as String,
      email: json['email'] as String,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id, 'name': name, 'email': email,
  };
}

// ── Dio with interceptors ──
class DioClient {
  late final Dio _dio;

  DioClient() {
    _dio = Dio(BaseOptions(
      baseUrl: 'https://api.example.com',
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 15),
      headers: {'Accept': 'application/json'},
    ));

    _dio.interceptors.addAll([
      _AuthInterceptor(),
      _LogInterceptor(),
      _RetryInterceptor(),
    ]);
  }

  Future<Response<T>> get<T>(String path, {Map<String, dynamic>? query}) {
    return _dio.get<T>(path, queryParameters: query);
  }
}

class _AuthInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final token = AuthStorage.token;
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    if (err.response?.statusCode == 401) {
      // Token expired — refresh and retry
      _refreshTokenAndRetry(err, handler);
    } else {
      handler.next(err);
    }
  }
}

// ── Cancel in-flight requests ──
class UserScreen extends StatefulWidget {
  const UserScreen({super.key});
  @override
  State<UserScreen> createState() => _UserScreenState();
}

class _UserScreenState extends State<UserScreen> {
  final _cancelToken = CancelToken();
  List<User>? _users;

  @override
  void initState() {
    super.initState();
    _loadUsers();
  }

  @override
  void dispose() {
    _cancelToken.cancel();  // Cancel on dispose
    super.dispose();
  }

  Future<void> _loadUsers() async {
    try {
      _users = await api.getUsers(cancelToken: _cancelToken);
      if (mounted) setState(() {});
    } on DioException catch (e) {
      if (CancelToken.isCancel(e)) return;  // Ignore cancel
      if (mounted) setState(() => _error = e.message);
    }
  }
}

// ── Retrofit (type-safe API) ──
@RestApi(baseUrl: 'https://api.example.com')
abstract class UserApi {
  factory UserApi(Dio dio) = _UserApi;

  @GET('/users')
  Future<List<User>> getUsers();

  @GET('/users/{id}')
  Future<User> getUser(@Path('id') int id);

  @POST('/users')
  Future<User> createUser(@Body() Map<String, dynamic> body);
}
```

### Output
```
A Flutter app with HTTP networking:
- ApiService with GET/POST, error handling, and timeouts
- User model with fromJson/toJson
- Dio client with auth interceptor (token refresh on 401)
- CancelToken to cancel requests on widget dispose
- Retrofit for type-safe API definitions
```

---

## ❓ Interview Questions

1. **How do you make HTTP requests in Flutter?**
   - Use the `http` package: `http.get(Uri.parse(url))` for GET, `http.post(Uri.parse(url), headers: {...}, body: jsonEncode({...}))` for POST. Parse the response: `jsonDecode(response.body)` gives a `Map<String, dynamic>`, pass it to `Model.fromJson()`. Check `response.statusCode` — 200/201 for success, 4xx/5xx for errors. Always wrap in try-catch for `SocketException` (no internet), `TimeoutException` (slow server), and `FormatException` (bad JSON). For production apps, use `dio` for interceptors, cancellation, and retries.

2. **What is the difference between `http` and `dio`?**
   - `http` is a simple package — basic GET/POST/PUT/DELETE, no interceptors, no cancellation, no FormData. Good for simple apps and learning. `dio` is a powerful HTTP client — interceptors (auth, logging, retry), request cancellation (`CancelToken`), FormData (file uploads), timeouts, global configuration (`BaseOptions`), response types (JSON, bytes, stream), and retry logic. Use `dio` for production apps that need auth token management, file uploads, request cancellation, or centralized error handling. `http` is fine for small apps with simple API calls.

3. **How do you parse JSON in Flutter?**
   - Use `jsonDecode(response.body)` to convert JSON string to `Map<String, dynamic>` or `List<dynamic>`. Create model classes with a `fromJson` factory: `factory User.fromJson(Map<String, dynamic> json) => User(id: json['id'], name: json['name'])`. For nested objects, call the nested model's `fromJson`. For lists: `(json['items'] as List).map((e) => Item.fromJson(e)).toList()`. For encoding: `jsonEncode(user.toJson())`. For type safety and less boilerplate, use `json_serializable` (code generation with `@JsonSerializable()`) or `freezed` (immutable models + JSON).

4. **How do you handle network errors?**
   - Wrap network calls in try-catch. Catch `SocketException` → no internet → show offline message or cached data. Catch `TimeoutException` → server slow → retry or cancel. Check `response.statusCode`: 401 → token expired, refresh token. 403 → forbidden, show access denied. 404 → not found, show empty state. 500 → server error, show retry button. Define custom exceptions: `class ApiException implements Exception { final int statusCode; final String message; }`. Use `Result<T>` pattern (Success/Failure) to propagate errors without exceptions. Always show user-friendly error messages, not raw exception text.

5. **What are interceptors and how do you use them?**
   - Interceptors in `dio` run before requests and after responses — middleware for HTTP calls. `onRequest` — add auth token to headers, log the request. `onResponse` — log response time, transform data. `onError` — handle 401 (refresh token and retry), retry on timeout, convert `DioException` to custom exception. Add with `_dio.interceptors.add(MyInterceptor())`. Common use cases: (1) Auth — add `Authorization: Bearer $token` to every request. (2) Logging — log request/response for debugging. (3) Retry — retry failed requests with exponential backoff. (4) Error mapping — convert DioException to domain exceptions.

6. **How do you handle authentication tokens?**
   - Store token securely (flutter_secure_storage). Add an auth interceptor that injects `Authorization: Bearer $token` into every request header. On 401 response, the interceptor refreshes the token using the refresh token, retries the original request with the new token. If refresh fails, redirect to login. Store both access token (short-lived) and refresh token (long-lived). Use `AuthStorage` class to manage token storage/retrieval. In the interceptor's `onError`, check for 401, call `_refreshToken()`, update stored token, and retry with `handler.resolve(options)`.

7. **How do you cancel in-flight HTTP requests?**
   - In `dio`, use `CancelToken`: create `final cancelToken = CancelToken()`, pass to request `dio.get(url, cancelToken: cancelToken)`. Call `cancelToken.cancel()` to abort — throws `DioException` with type `cancel`. Check with `CancelToken.isCancel(error)`. Use in `dispose()` to cancel when the widget is removed from the tree — prevents `setState` after dispose and saves bandwidth. In `http` package, there's no built-in cancellation — wrap in a `Future` and ignore the result if the widget is disposed. This is a key advantage of `dio` over `http`.

8. **How do you upload files in Flutter?**
   - Use `dio` with `FormData`: `final formData = FormData.fromMap({'file': await MultipartFile.fromFile(filePath, filename: 'photo.jpg'), 'name': 'John'})`, then `dio.post('/upload', data: formData)`. For multiple files: `'files': [MultipartFile.fromFile(file1), MultipartFile.fromFile(file2)]`. Track upload progress with `onSendProgress: (sent, total) { print('${(sent/total*100).round()}%'); }`. For large files, use chunked upload with `Stream<List<int>>`. The `http` package doesn't support file uploads well — always use `dio` for uploads.

9. **What is Retrofit and how does it work?**
   - Retrofit is a type-safe HTTP client using code generation. Define an abstract class with annotations: `@RestApi(baseUrl: ...)`, `@GET('/users')`, `@POST('/users')`, `@Path('id')`, `@Body()`, `@Query('page')`. Run `dart run build_runner build` to generate the implementation (`_UserApi`). The generated code uses `dio` internally. Benefits: compile-time safety (no string URLs), IDE autocomplete, automatic JSON serialization (with `json_serializable`), less boilerplate. Use Retrofit in clean architecture apps where the API interface is defined in the data layer and the implementation is generated.

10. **How do you implement offline support / caching?**
    - (1) Cache GET responses in local DB (sqflite, Hive) with timestamp. (2) On app launch, show cached data first, then fetch fresh data from API. (3) If offline, use cached data with "last updated" indicator. (4) Use a `ConnectivityResult` to detect online/offline status. (5) Queue mutations (POST/PUT/DELETE) when offline, sync when back online (workmanager for background sync). (6) Use `dio_cache_interceptor` for HTTP-level caching. (7) Use `hydrated_bloc` for state persistence. The repository pattern is key — the UI calls repository methods, and the repository decides: try API first, fallback to cache, or vice versa.

---

## 🔗 Related Topics
- [Firebase Integration](FirebaseIntegration.md)
- [State Management Advanced](StateManagementAdvanced.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
