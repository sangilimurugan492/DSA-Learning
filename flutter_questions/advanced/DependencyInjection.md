# Dependency Injection

## Q1: What is Dependency Injection and why use it?

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

// Test — inject mock
final service = UserService(MockApiClient(), MockDatabase());
```

### Benefits
- **Testability** — inject mocks in tests
- **Loose coupling** — classes don't create their dependencies
- **Single responsibility** — class doesn't manage dependency lifecycle
- **Reusability** — swap implementations easily

---

## Q2: How do you use `get_it` for DI?

```dart
// pubspec.yaml: get_it: ^7.6.0

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

// Initialize in main()
void main() {
  setupDI();
  runApp(const MyApp());
}

// Usage — anywhere in the app
final api = getIt<ApiClient>();
final userService = getIt<UserService>();
```

### Registration Types
| Type | Instance | Use Case |
|------|----------|----------|
| `registerSingleton` | Created immediately | Eager init (config) |
| `registerLazySingleton` | Created on first access | Most services |
| `registerFactory` | New instance each call | ViewModels, BLoCs |

---

## Q3: How do you use `injectable` for compile-time DI?

```dart
// pubspec.yaml: injectable: ^2.3.0, get_it: ^7.6.0
// dev_dependencies: injectable_generator, build_runner

// 1. Annotate classes
@injectable
class ApiClient {
  Future<User> getUser(int id) async => /* ... */;
}

@injectable
class Database {
  Future<void> save(User user) async => /* ... */;
}

@injectable
class UserService {
  final ApiClient api;
  final Database db;

  UserService(this.api, this.db);  // Auto-resolved by injectable
}

// 2. Register modules (for third-party or external deps)
@module
abstract class RegisterModule {
  @lazySingleton
  Dio dio() => Dio(BaseOptions(baseUrl: 'https://api.example.com'));

  @lazySingleton
  SharedPreferences sharedPreferences() => SharedPreferences.getInstance() as SharedPreferences;
}

// 3. Generate code
// dart run build_runner build

// 4. Initialize in main()
import 'injection.config.dart';

void main() async {
  await configureDependencies();  // Generated function
  runApp(const MyApp());
}

// Usage — same as get_it
final userService = getIt<UserService>();
```

### Injectable Annotations
| Annotation | Equivalent | Behavior |
|-----------|-----------|----------|
| `@injectable` | `registerFactory` | New instance each call |
| `@singleton` | `registerSingleton` | One instance, eager |
| `@lazySingleton` | `registerLazySingleton` | One instance, lazy |
| `@module` | — | Register external deps |

---

## Q4: How do you use Riverpod for DI?

```dart
// Riverpod is both state management AND DI
// No need for get_it — providers ARE the DI

// Service providers
final apiClientProvider = Provider<ApiClient>((ref) {
  return ApiClient();
});

final databaseProvider = Provider<Database>((ref) {
  return Database();
});

// Repository depends on other providers
final userRepositoryProvider = Provider<UserRepository>((ref) {
  return UserRepositoryImpl(
    api: ref.watch(apiClientProvider),
    db: ref.watch(databaseProvider),
  );
});

// Use case depends on repository
final getUserUseCaseProvider = Provider<GetUserUseCase>((ref) {
  return GetUserUseCase(ref.watch(userRepositoryProvider));
});

// BLoC/Notifier depends on use case
final userNotifierProvider = AsyncNotifierProvider<UserNotifier, User?>(UserNotifier.new);

class UserNotifier extends AsyncNotifier<User?> {
  late final getUser = ref.read(getUserUseCaseProvider);

  @override
  Future<User?> build() async {
    return getUser(1);
  }
}

// Testing — override providers
final container = ProviderContainer(overrides: [
  apiClientProvider.overrideWithValue(MockApiClient()),
  databaseProvider.overrideWithValue(MockDatabase()),
]);
```

### Riverpod vs get_it for DI
| Feature | Riverpod | get_it |
|---------|----------|--------|
| Compile-safe | ✅ | ❌ |
| Testable | ✅ (override) | ✅ (register) |
| Lifecycle | Auto-dispose | Manual |
| Scoped | ✅ | ❌ (global) |
| Learning curve | Medium | Low |

---

## Q5: How do you inject dependencies into BLoC?

```dart
// With get_it
class UserBloc extends Bloc<UserEvent, UserState> {
  final UserRepository repository;

  UserBloc(this.repository) : super(UserInitial()) {
    on<FetchUser>(_onFetch);
  }

  Future<void> _onFetch(FetchUser event, Emitter<UserState> emit) async {
    emit(UserLoading());
    try {
      final user = await repository.getUser(event.id);
      emit(UserLoaded(user));
    } catch (e) {
      emit(UserError(e.toString()));
    }
  }
}

// Provide BLoC with dependencies
BlocProvider(
  create: (context) => UserBloc(getIt<UserRepository>()),
  child: UserScreen(),
)

// With injectable
@injectable
class UserBloc extends Bloc<UserEvent, UserState> {
  final UserRepository repository;

  UserBloc(this.repository) : super(UserInitial()) { ... }
}

// In widget
BlocProvider(
  create: (context) => getIt<UserBloc>(),
  child: UserScreen(),
)
```

---

## Q6: How do you scope dependencies?

```dart
// get_it — scoped registration
final getIt = GetIt.instance;

// Register with scope
getIt.registerSingleton<ApiClient>(ApiClient());

// Push scope (e.g., for a feature)
getIt.pushNewScope(scopeName: 'auth');
getIt.registerSingleton<AuthSession>(AuthSession(token: 'xxx'));

// Use in auth scope
final session = getIt<AuthSession>();

// Pop scope when done (disposes scoped deps)
getIt.dropScope('auth');
// AuthSession no longer available

// Riverpod — auto-scoped with ProviderScope
ProviderScope(
  overrides: [
    userRepositoryProvider.overrideWithValue(MockUserRepository()),
  ],
  child: TestApp(),
)

// Nested ProviderScope for feature-level scoping
ProviderScope(
  overrides: [cartProvider.overrideWithValue(CartModel())],
  child: CartScreen(),
)
```

---

## Q7: How do you set up a complete DI container?

```dart
// injection_container.dart
final getIt = GetIt.instance;

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

  // Data sources
  getIt.registerLazySingleton<UserRemoteDataSource>(
    () => UserRemoteDataSource(getIt<ApiClient>()),
  );
  getIt.registerLazySingleton<UserLocalDataSource>(
    () => UserLocalDataSource(getIt<SharedPreferences>()),
  );

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
  getIt.registerFactory<SaveUserUseCase>(
    () => SaveUserUseCase(getIt<UserRepository>()),
  );

  // BLoCs (factory — new instance per screen)
  getIt.registerFactory<UserBloc>(
    () => UserBloc(
      getUser: getIt<GetUserUseCase>(),
      saveUser: getIt<SaveUserUseCase>(),
    ),
  );
}

// main.dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await setupDependencies();
  runApp(const MyApp());
}

// Any widget
final userBloc = getIt<UserBloc>();
```

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Testing](../intermediate/Testing.md)
