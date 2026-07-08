# Architecture Patterns

## Q1: What is Clean Architecture in Flutter?

```
┌─────────────────────────────────────────┐
│              Presentation                │  ← UI (Widgets, BLoC/Provider)
│   ┌─────────────────────────────────┐   │
│   │            Domain                │   │  ← Business Logic (Entities, Use Cases)
│   │   ┌─────────────────────────┐    │   │
│   │   │        Data              │    │   │  ← Data Source (API, DB, Repositories)
│   │   └─────────────────────────┘    │   │
│   └─────────────────────────────────┘   │
└─────────────────────────────────────────┘

Dependency Rule: outer layers depend on inner layers (never reverse)
```

```
lib/
├── core/                    ← Shared utilities
│   ├── error/
│   ├── theme/
│   └── utils/
├── features/                ← Feature-first structure
│   ├── auth/
│   │   ├── data/
│   │   │   ├── datasources/    ← API, local DB
│   │   │   ├── models/         ← DTOs, JSON mapping
│   │   │   └── repositories/   ← Repository impl
│   │   ├── domain/
│   │   │   ├── entities/       ← Pure business objects
│   │   │   ├── repositories/   ← Abstract interfaces
│   │   │   └── usecases/       ← Business operations
│   │   └── presentation/
│   │       ├── blocs/          ← State management
│   │       ├── pages/          ← Screens
│   │       └── widgets/        ← Feature-specific widgets
│   └── product/
│       └── ...
└── main.dart
```

```dart
// Domain layer — pure Dart, no Flutter dependencies

// Entity
class User {
  final int id;
  final String name;
  final String email;
  const User({required this.id, required this.name, required this.email});
}

// Repository interface (abstract)
abstract class UserRepository {
  Future<User> getUser(int id);
  Future<List<User>> getUsers();
  Future<void> saveUser(User user);
}

// Use case
class GetUserUseCase {
  final UserRepository repository;
  GetUserUseCase(this.repository);

  Future<User> call(int id) => repository.getUser(id);
}

// Data layer — implements domain interfaces

// Model (extends entity, adds JSON)
class UserModel extends User {
  const UserModel({
    required super.id,
    required super.name,
    required super.email,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] as int,
      name: json['name'] as String,
      email: json['email'] as String,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id, 'name': name, 'email': email,
  };
}

// Repository implementation
class UserRepositoryImpl implements UserRepository {
  final UserRemoteDataSource remoteDataSource;
  final UserLocalDataSource localDataSource;

  UserRepositoryImpl(this.remoteDataSource, this.localDataSource);

  @override
  Future<User> getUser(int id) async {
    // Try local first
    final local = await localDataSource.getUser(id);
    if (local != null) return local;

    // Fetch from remote
    final remote = await remoteDataSource.getUser(id);
    await localDataSource.cacheUser(remote);
    return remote;
  }
}

// Presentation layer
class UserBloc extends Bloc<UserEvent, UserState> {
  final GetUserUseCase getUser;

  UserBloc(this.getUser) : super(UserInitial()) {
    on<FetchUser>(_onFetchUser);
  }

  Future<void> _onFetchUser(FetchUser event, Emitter<UserState> emit) async {
    emit(UserLoading());
    try {
      final user = await getUser(event.id);
      emit(UserLoaded(user));
    } catch (e) {
      emit(UserError(e.toString()));
    }
  }
}
```

---

## Q2: What is MVVM in Flutter?

```dart
// MVVM — Model, View, ViewModel
// View (Widget) → ViewModel (ChangeNotifier/Bloc) → Model (Data)

// Model
class Product {
  final int id;
  final String name;
  final double price;
  const Product({required this.id, required this.name, required this.price});
}

// ViewModel
class ProductViewModel extends ChangeNotifier {
  final ProductRepository _repository;

  ProductViewModel(this._repository);

  List<Product> _products = [];
  List<Product> get products => _products;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _error;
  String? get error => _error;

  Future<void> loadProducts() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _products = await _repository.getProducts();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}

// View (Widget)
class ProductScreen extends StatelessWidget {
  const ProductScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => ProductViewModel(repo)..loadProducts(),
      child: Consumer<ProductViewModel>(
        builder: (context, vm, _) {
          if (vm.isLoading) return const CircularProgressIndicator();
          if (vm.error != null) return Text('Error: ${vm.error}');
          return ListView.builder(
            itemCount: vm.products.length,
            itemBuilder: (_, i) => ListTile(
              title: Text(vm.products[i].name),
              subtitle: Text('\$${vm.products[i].price}'),
            ),
          );
        },
      ),
    );
  }
}
```

---

## Q3: What is the Repository pattern?

```dart
// Repository — abstraction over data sources
// UI doesn't know if data comes from API, DB, or cache

// Abstract repository
abstract class ProductRepository {
  Future<List<Product>> getProducts();
  Future<Product> getProduct(int id);
  Future<void> saveProduct(Product product);
}

// Implementation with multiple sources
class ProductRepositoryImpl implements ProductRepository {
  final ProductRemoteDataSource remote;
  final ProductLocalDataSource local;

  ProductRepositoryImpl({required this.remote, required this.local});

  @override
  Future<List<Product>> getProducts() async {
    // 1. Try local cache
    final cached = await local.getProducts();
    if (cached.isNotEmpty) return cached;

    // 2. Fetch from API
    final remoteProducts = await remote.getProducts();

    // 3. Cache locally
    await local.saveProducts(remoteProducts);

    return remoteProducts;
  }

  @override
  Future<Product> getProduct(int id) async {
    try {
      return await remote.getProduct(id);
    } catch (e) {
      // Fallback to local
      return await local.getProduct(id);
    }
  }
}

// Data sources
class ProductRemoteDataSource {
  final ApiClient api;
  ProductRemoteDataSource(this.api);

  Future<List<Product>> getProducts() async {
    final response = await api.get('/products');
    return (response.data as List)
        .map((e) => Product.fromJson(e))
        .toList();
  }
}

class ProductLocalDataSource {
  final SharedPreferences prefs;
  ProductLocalDataSource(this.prefs);

  Future<List<Product>> getProducts() async {
    final json = prefs.getString('products');
    if (json == null) return [];
    return (jsonDecode(json) as List)
        .map((e) => Product.fromJson(e))
        .toList();
  }
}
```

---

## Q4: What is the difference between BLoC and MVVM?

| Feature | BLoC | MVVM |
|---------|------|------|
| State | Events → States | Properties + notifyListeners |
| Pattern | Event-driven | Data binding |
| Boilerplate | More | Less |
| Testability | Excellent | Good |
| Reactive | Streams | ChangeNotifier |
| Best for | Complex apps | Medium apps |

```dart
// BLoC — explicit events and states
// Event: FetchUser → State: Loading → Loaded/Error

// MVVM — properties + notifyListeners
// ViewModel.isLoading = true → UI rebuilds
```

---

## Q5: How do you structure a feature-first app?

```
lib/
├── core/
│   ├── constants/
│   ├── theme/
│   ├── utils/
│   └── widgets/           ← Shared widgets
├── features/
│   ├── auth/
│   │   ├── data/
│   │   │   ├── datasources/
│   │   │   │   ├── auth_remote_datasource.dart
│   │   │   │   └── auth_local_datasource.dart
│   │   │   ├── models/
│   │   │   │   └── user_model.dart
│   │   │   └── repositories/
│   │   │       └── auth_repository_impl.dart
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   └── user.dart
│   │   │   ├── repositories/
│   │   │   │   └── auth_repository.dart
│   │   │   └── usecases/
│   │   │       ├── login_usecase.dart
│   │   │       └── logout_usecase.dart
│   │   └── presentation/
│   │       ├── bloc/
│   │       │   ├── auth_bloc.dart
│   │       │   ├── auth_event.dart
│   │       │   └── auth_state.dart
│   │       ├── pages/
│   │       │   ├── login_page.dart
│   │       │   └── register_page.dart
│   │       └── widgets/
│   │           └── auth_button.dart
│   ├── product/
│   │   └── ... (same structure)
│   └── profile/
│       └── ... (same structure)
├── injection_container.dart  ← DI setup
└── main.dart
```

---

## Q6: How do you handle error handling across layers?

```dart
// Core error types
abstract class Failure {
  final String message;
  Failure(this.message);
}

class ServerFailure extends Failure {
  final int statusCode;
  ServerFailure(this.statusCode, super.message);
}

class CacheFailure extends Failure {
  CacheFailure(super.message);
}

class NetworkFailure extends Failure {
  NetworkFailure() : super('No internet connection');
}

// Result wrapper (Either<Failure, T>)
sealed class Result<T> {
  const Result();
}

class Success<T> extends Result<T> {
  final T data;
  const Success(this.data);
}

class FailureResult<T> extends Result<T> {
  final Failure failure;
  const FailureResult(this.failure);
}

// Repository returns Result
class UserRepositoryImpl implements UserRepository {
  @override
  Future<Result<User>> getUser(int id) async {
    try {
      final user = await remoteDataSource.getUser(id);
      return Success(user);
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionError) {
        return FailureResult(NetworkFailure());
      }
      return FailureResult(ServerFailure(
        e.response?.statusCode ?? 0,
        e.message ?? 'Server error',
      ));
    } catch (e) {
      return FailureResult(CacheFailure(e.toString()));
    }
  }
}

// Use case
class GetUserUseCase {
  final UserRepository repo;
  GetUserUseCase(this.repo);

  Future<Result<User>> call(int id) => repo.getUser(id);
}

// BLoC handles Result
Future<void> _onFetch(FetchUser event, Emitter<UserState> emit) async {
  emit(UserLoading());
  final result = await getUserUseCase(event.id);
  switch (result) {
    case Success(:final data):
      emit(UserLoaded(data));
    case FailureResult(:final failure):
      emit(UserError(failure.message));
  }
}
```

---

## Q7: How do you choose an architecture for your app?

```
App Size         → Architecture
────────────────────────────────────
Small/Prototype  → setState + simple Provider
Medium           → MVVM + Repository
Large            → Clean Architecture + BLoC
Enterprise       → Clean Architecture + BLoC + DDD
```

| Criteria | Simple | Clean Architecture |
|----------|--------|-------------------|
| App size | Small | Large |
| Team size | 1-2 | 5+ |
| Testability | Low | High |
| Boilerplate | Low | High |
| Learning curve | Low | High |
| Maintainability | Low | High |

> **Rule:** Don't over-engineer. Start simple, add layers as complexity grows. Clean Architecture is worth it for apps with 10+ features and multiple data sources.

---

## 🔗 Related Topics
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Dependency Injection](DependencyInjection.md)
- [Testing](../intermediate/Testing.md)
