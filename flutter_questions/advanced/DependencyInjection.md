# Dependency Injection

## 📖 Explanation

Dependency Injection (DI) is a design pattern where objects receive their dependencies from an external source rather than creating them internally. This makes code loosely coupled, testable, and reusable.

### Why Use DI?
- **Testability** — Inject mock dependencies in tests
- **Loose coupling** — Classes don't create their own dependencies
- **Single responsibility** — Class doesn't manage dependency lifecycle
- **Reusability** — Swap implementations easily

### DI Approaches in Flutter
| Approach | Package | Compile-Safe | Use Case |
|----------|---------|-------------|----------|
| `get_it` | get_it | ❌ | Service locator, simple setup |
| `injectable` | injectable + get_it | ✅ | Compile-time DI with code generation |
| Riverpod | flutter_riverpod | ✅ | DI + state management combined |
| Constructor injection | None | ✅ | Manual, no package needed |

### get_it Registration Types
| Type | Instance | Use Case |
|------|----------|----------|
| `registerSingleton` | Created immediately | Eager init (config) |
| `registerLazySingleton` | Created on first access | Most services |
| `registerFactory` | New instance each call | ViewModels, BLoCs |

### Injectable Annotations
| Annotation | Equivalent | Behavior |
|-----------|-----------|----------|
| `@injectable` | `registerFactory` | New instance each call |
| `@singleton` | `registerSingleton` | One instance, eager |
| `@lazySingleton` | `registerLazySingleton` | One instance, lazy |
| `@module` | — | Register external deps |

### Riverpod vs get_it for DI
| Feature | Riverpod | get_it |
|---------|----------|--------|
| Compile-safe | ✅ | ❌ |
| Testable | ✅ (override) | ✅ (register) |
| Lifecycle | Auto-dispose | Manual |
| Scoped | ✅ | ❌ (global) |
| Learning curve | Medium | Low |

---

## 🧪 Code Example

```dart
// ❌ Without DI — tight coupling, hard to test
class UserService {
  final _api = ApiClient();  // Created inside — can't mock
  final _db = Database();     // Hard dependency
  Future<User> getUser(int id) => _api.getUser(id);
}

// ✅ With DI — inject dependencies, easy to test
class UserService {
  final ApiClient api;
  final Database db;
  UserService(this.api, this.db);  // Injected via constructor
  Future<User> getUser(int id) => api.getUser(id);
}

// ── get_it setup ──
import 'package:get_it/get_it.dart';

final getIt = GetIt.instance;

void setupDI() {
  // Singleton — one instance for entire app
  getIt.registerSingleton<ApiClient>(ApiClient());

  // Lazy singleton — created on first access
  getIt.registerLazySingleton<Database>(() => Database());

  // Factory — new instance every time
  getIt.registerFactory<UserService>(() => UserService(
    getIt<ApiClient>(),
    getIt<Database>(),
  ));
}

void main() {
  setupDI();
  runApp(const MyApp());
}

// Usage — anywhere in the app
final api = getIt<ApiClient>();
final userService = getIt<UserService>();

// ── Complete DI container ──
Future<void> setupDependencies() async {
  // External
  final sharedPreferences = await SharedPreferences.getInstance();
  getIt.registerSingleton<SharedPreferences>(sharedPreferences);

  // Core
  getIt.registerLazySingleton<Dio>(() => Dio(BaseOptions(
    baseUrl: 'https://api.example.com',
    connectTimeout: const Duration(seconds: 10),
  )));

  getIt.registerLazySingleton<ApiClient>(() => ApiClient(getIt<Dio>()));

  // Repositories
  getIt.registerLazySingleton<UserRepository>(
    () => UserRepositoryImpl(
      remote: getIt<UserRemoteDataSource>(),
      local: getIt<UserLocalDataSource>(),
    ),
  );

  // Use cases
  getIt.registerFactory<GetUserUseCase>(
    () => GetUserUseCase(getIt<UserRepository>()),
  );

  // BLoCs (factory — new instance per screen)
  getIt.registerFactory<UserBloc>(
    () => UserBloc(getUser: getIt<GetUserUseCase>()),
  );
}

// ── Scoping ──
getIt.pushNewScope(scopeName: 'auth');
getIt.registerSingleton<AuthSession>(AuthSession(token: 'xxx'));
// Use in auth scope
final session = getIt<AuthSession>();
// Pop scope when done (disposes scoped deps)
getIt.dropScope('auth');  // AuthSession no longer available
```

### Output
```
A Flutter app with dependency injection:
- get_it service locator registered with singletons, lazy singletons, and factories
- ApiClient, Database as lazy singletons
- UserRepository with injected data sources
- UserBloc as factory (new instance per screen)
- Scoped dependencies for feature-level isolation
```

---

## ❓ Interview Questions

1. **What is Dependency Injection and why use it?**
   - Dependency Injection is a pattern where objects receive their dependencies from an external source (constructor, service locator, or framework) instead of creating them internally. Without DI, a class creates its own dependencies (`final _api = ApiClient()`), making it tightly coupled and untestable. With DI, dependencies are injected (`UserService(this.api, this.db)`), making the class testable (inject mocks), loosely coupled (swap implementations), and focused on single responsibility (doesn't manage lifecycle). In Flutter, use `get_it` (service locator), `injectable` (compile-time DI), or Riverpod (DI + state management).

2. **How do you use `get_it` for DI?**
   - `get_it` is a service locator for Dart. Register dependencies in a setup function: `getIt.registerSingleton<ApiClient>(ApiClient())` (created immediately), `getIt.registerLazySingleton<Database>(() => Database())` (created on first access), `getIt.registerFactory<UserService>(() => UserService(getIt<ApiClient>(), getIt<Database>()))` (new instance each call). Call `setupDI()` in `main()` before `runApp()`. Access anywhere via `getIt<ApiClient>()`. Use singletons for shared services (ApiClient, Database), lazy singletons for most services (repositories), and factories for per-screen instances (BLoCs, ViewModels).

3. **How do you use `injectable` for compile-time DI?**
   - `injectable` generates DI code using annotations, providing compile-time safety. Annotate classes: `@injectable` (factory), `@singleton` (eager singleton), `@lazySingleton` (lazy singleton). Use `@module` to register external dependencies (Dio, SharedPreferences). Run `dart run build_runner build` to generate `injection.config.dart`. Call `configureDependencies()` in `main()`. Benefits: compile-time safety (no runtime registration errors), less boilerplate (auto-resolves constructor dependencies), and easy to maintain. The generated code uses `get_it` under the hood.

4. **How do you use Riverpod for DI?**
   - Riverpod is both state management and DI. Define providers: `final apiClientProvider = Provider<ApiClient>((ref) => ApiClient())`. Depend on other providers: `final userRepositoryProvider = Provider<UserRepository>((ref) => UserRepositoryImpl(api: ref.watch(apiClientProvider), db: ref.watch(databaseProvider)))`. Use cases and BLoCs depend on repository providers. Testing: override providers with `ProviderContainer(overrides: [apiClientProvider.overrideWithValue(MockApiClient())])`. Benefits: compile-safe (no runtime errors like get_it), auto-dispose (lifecycle managed), scoped (ProviderScope for feature-level isolation). No need for get_it when using Riverpod.

5. **How do you inject dependencies into BLoC?**
   - Inject via constructor: `UserBloc(this.repository) : super(UserInitial())`. Provide with BLoC: `BlocProvider(create: (context) => UserBloc(getIt<UserRepository>()), child: UserScreen())`. With injectable: annotate BLoC with `@injectable`, then `BlocProvider(create: (context) => getIt<UserBloc>())`. With Riverpod: define a provider that creates the BLoC with its dependencies. The BLoC never creates its own dependencies — it receives them through the constructor. This makes the BLoC testable — inject mock repositories in tests.

6. **How do you scope dependencies?**
   - In `get_it`, use `pushNewScope(scopeName: 'auth')` to create a scoped container. Register scoped dependencies after pushing the scope. Use `getIt<AuthSession>()` to access scoped deps. Call `getIt.dropScope('auth')` to dispose scoped dependencies when leaving the feature. In Riverpod, use nested `ProviderScope` with overrides: `ProviderScope(overrides: [cartProvider.overrideWithValue(CartModel())], child: CartScreen())`. Scoped providers are isolated — the nested scope has its own instance while other screens use the root scope. Scoping is useful for feature-level dependencies (auth session, feature-specific config).

7. **How do you set up a complete DI container?**
   - Create `injection_container.dart` with a `setupDependencies()` async function. Register in order: (1) External packages (SharedPreferences, Dio) — eager singletons. (2) Core services (ApiClient) — lazy singletons. (3) Data sources (remote, local) — lazy singletons. (4) Repositories — lazy singletons with injected data sources. (5) Use cases — factories with injected repositories. (6) BLoCs — factories with injected use cases. Call `await setupDependencies()` in `main()` after `WidgetsFlutterBinding.ensureInitialized()`. Access anywhere via `getIt<T>()`. This gives a clean dependency graph where each layer only depends on the layer below it.

8. **What is the difference between Singleton, Lazy Singleton, and Factory?**
   - **Singleton** (`registerSingleton`): Instance is created immediately at registration. Use for critical services that must be ready before app starts (SharedPreferences, Firebase). **Lazy Singleton** (`registerLazySingleton`): Instance is created on first access. Use for most services (ApiClient, Database, repositories) — saves startup time by deferring creation. **Factory** (`registerFactory`): New instance is created every time `getIt<T>()` is called. Use for BLoCs and ViewModels — each screen gets its own instance with its own state. Singletons share state across the app; factories give isolated state per access.

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Testing](../intermediate/Testing.md)
