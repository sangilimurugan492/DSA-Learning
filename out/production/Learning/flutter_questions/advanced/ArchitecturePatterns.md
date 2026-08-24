# Architecture Patterns

## 📖 Explanation

Architecture patterns in Flutter define how you structure your code — separating UI, business logic, and data layers. The right architecture makes your app testable, maintainable, and scalable.

### Clean Architecture
Clean Architecture divides the app into three layers with a strict dependency rule: **outer layers depend on inner layers, never the reverse**.

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

- **Presentation** — Widgets, BLoC/Provider/ViewModel. Depends on Domain.
- **Domain** — Entities, Use Cases, Repository interfaces. Pure Dart, no Flutter. Innermost layer.
- **Data** — Repository implementations, Data Sources (API, DB), Models (DTOs). Depends on Domain.

### MVVM (Model-View-ViewModel)
MVVM separates UI from business logic using a ViewModel (ChangeNotifier or Bloc). The View calls ViewModel methods, the ViewModel handles logic and exposes state, and the View rebuilds on state change.

### Repository Pattern
The Repository abstracts data sources. The UI doesn't know whether data comes from an API, local DB, or cache. This makes swapping data sources easy and centralizes caching/fallback logic.

### Architecture Comparison
| Pattern | Layers | Testability | Boilerplate | Best For |
|---------|--------|-------------|-------------|----------|
| Clean Architecture | 3 (Presentation, Domain, Data) | High | High | Large/Enterprise apps |
| MVVM | 3 (Model, View, ViewModel) | Good | Medium | Medium apps |
| Repository Pattern | 2 (UI, Data) | Medium | Low | Small/Medium apps |
| Simple (setState) | 1 | Low | None | Prototypes |

### BLoC vs MVVM
| Feature | BLoC | MVVM |
|---------|------|------|
| State | Events → States | Properties + notifyListeners |
| Pattern | Event-driven | Data binding |
| Boilerplate | More | Less |
| Testability | Excellent | Good |
| Reactive | Streams | ChangeNotifier |
| Best for | Complex apps | Medium apps |

### Error Handling Across Layers
Use sealed `Failure` classes and a `Result<T>` wrapper (Success/Failure) to propagate errors from Data → Domain → Presentation without exceptions leaking into UI.

### Architecture Selection Guide
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

## 🧪 Code Example

```dart
// ── Domain Layer (pure Dart, no Flutter) ──

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

// ── Data Layer (implements domain interfaces) ──

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

// Repository implementation with caching
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

// ── Presentation Layer ──

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

### Feature-First Project Structure
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

### Output
```
A Clean Architecture Flutter app with:
- Domain layer: pure Dart entities, repository interfaces, use cases
- Data layer: models with JSON mapping, repository implementations with caching
- Presentation layer: BLoC handling events → states
- Feature-first folder structure for scalability
```

---

## ❓ Interview Questions

1. **What is Clean Architecture in Flutter?**
   - Clean Architecture divides the app into three layers: Presentation (UI, BLoC/Provider), Domain (entities, use cases, repository interfaces), and Data (repository implementations, data sources, models). The dependency rule states that outer layers depend on inner layers, never the reverse. Domain is pure Dart with no Flutter dependencies. This separation makes the app highly testable (you can test business logic without UI or network), maintainable (changes in one layer don't affect others), and scalable (easy to add features). The tradeoff is more boilerplate — use it for large apps with 10+ features and multiple data sources.

2. **What is MVVM in Flutter and how does it differ from Clean Architecture?**
   - MVVM (Model-View-ViewModel) separates UI from business logic using a ViewModel (extends ChangeNotifier). The View calls ViewModel methods, the ViewModel handles logic and exposes state via properties + `notifyListeners()`, and the View rebuilds using Consumer or context.watch. MVVM has three layers (Model, View, ViewModel) vs Clean Architecture's three (Presentation, Domain, Data). MVVM is simpler with less boilerplate — good for medium apps. Clean Architecture is more structured with use cases and repository interfaces — good for large/enterprise apps. MVVM doesn't enforce the dependency rule as strictly as Clean Architecture.

3. **What is the Repository pattern and why use it?**
   - The Repository pattern abstracts data sources behind a common interface. The UI calls repository methods (e.g., `getUsers()`) without knowing if data comes from an API, local DB, or cache. Benefits: (1) Swappable data sources — change API to DB without touching UI. (2) Centralized caching — try local cache first, fetch from API if missing. (3) Testable — mock the repository in tests. (4) Single source of truth for data. Implementation: define abstract repository interface in Domain layer, implement it in Data layer with multiple data sources (remote + local).

4. **What is the difference between BLoC and MVVM?**
   - BLoC uses an event-driven pattern: Events → BLoC → States. Events are explicit classes, states are explicit classes, and the BLoC transforms events into states using streams. This gives excellent traceability (every state change is logged) and testability. MVVM uses properties + `notifyListeners()` — the ViewModel exposes state via getters and calls `notifyListeners()` when state changes. MVVM has less boilerplate but less structure. BLoC is better for complex apps with many state transitions. MVVM is better for medium apps where simplicity matters. BLoC is harder to learn but enforces stricter separation.

5. **How do you structure a feature-first Flutter app?**
   - Feature-first structure groups code by feature (auth, product, cart) rather than by layer (models, screens, services). Each feature has its own data/ (datasources, models, repositories), domain/ (entities, repository interfaces, usecases), and presentation/ (blocs, pages, widgets) folders. Shared code goes in core/ (theme, utils, error handling). DI setup goes in injection_container.dart. Benefits: features are self-contained (easy to add/remove), team members can work on different features without conflicts, and the structure scales naturally as the app grows. This is the recommended structure for medium-to-large Flutter apps.

6. **How do you handle error handling across architecture layers?**
   - Define sealed `Failure` classes (ServerFailure, CacheFailure, NetworkFailure) in the Domain layer. Create a `Result<T>` wrapper using `sealed class Result<T>` with `Success<T>` and `FailureResult<T>` subtypes. The Data layer catches exceptions and returns `Result` — `Success(data)` on success, `FailureResult(failure)` on error. The Domain layer passes `Result` through use cases. The Presentation layer (BLoC) pattern-matches on `Result` using switch expressions: `case Success(:final data) → emit(Loaded(data))`, `case FailureResult(:final failure) → emit(Error(failure.message))`. This prevents exceptions from leaking into UI and makes error handling explicit and testable.

7. **How do you choose an architecture for your app?**
   - Choose based on app size, team size, and complexity. Small/Prototype → setState + simple Provider (minimal boilerplate, fast development). Medium → MVVM + Repository (good separation, manageable complexity). Large → Clean Architecture + BLoC (strict separation, high testability, scalable). Enterprise → Clean Architecture + BLoC + DDD (domain-driven design for complex business rules). Criteria: app size (small vs large), team size (1-2 vs 5+), testability needs (low vs high), boilerplate tolerance (low vs high), learning curve (low vs high). Rule: don't over-engineer. Start simple and add layers as complexity grows.

8. **What is a Use Case and when do you need it?**
   - A Use Case (or Interactor) encapsulates a single business operation — e.g., `GetUserUseCase`, `AddToCartUseCase`. It depends on a repository interface and exposes a `call()` method. Use cases sit in the Domain layer and are called by the Presentation layer (BLoC/ViewModel). Benefits: (1) Single responsibility — each use case does one thing. (2) Reusable — multiple BLoCs can use the same use case. (3) Testable — test business logic without UI. (4) Orchestrates multiple repositories if needed. Use cases are most valuable in large apps. For small apps, the BLoC can call the repository directly — use cases add unnecessary indirection.

9. **How do you implement dependency injection in Clean Architecture?**
   - Use `get_it` for service location: register singletons (ApiClient, Database), lazy singletons (repositories), and factories (BLoCs, use cases). Set up in `injection_container.dart` with `setupDependencies()`, called in `main()`. Alternatively, use `injectable` for compile-time DI with annotations (@injectable, @lazySingleton, @module). Or use Riverpod as both state management and DI — providers are the DI mechanism. Inject repository interfaces into use cases, and use cases into BLoCs. This makes everything testable — inject mock repositories in tests. The Presentation layer never creates data sources directly — it goes through use cases → repositories.

10. **How do you migrate from a simple architecture to Clean Architecture?**
    - Migrate incrementally, not all at once. Step 1: Extract repository interfaces from direct API calls in BLoCs. Step 2: Move models to data/models/ and create entities in domain/entities/. Step 3: Create use cases for complex business operations. Step 4: Move BLoCs to presentation/blocs/. Step 5: Set up DI with get_it. Don't refactor everything at once — migrate one feature at a time. Start with the most complex feature. Keep the old code working while gradually moving to the new structure. The migration is worth it when the app has 10+ features, multiple data sources, or a team of 5+ developers.

---

## 🔗 Related Topics
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Dependency Injection](DependencyInjection.md)
- [Testing](../intermediate/Testing.md)
