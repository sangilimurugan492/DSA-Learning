# BLoC

## Q1: What is BLoC and how does it work?

BLoC (Business Logic Component) uses streams to manage state: **Events → BLoC → States**.

```
User Action → Event → BLoC (process) → State → UI Rebuild

    ┌──────────┐     ┌──────────┐     ┌──────────┐
    │   Event   │ ──→ │   BLoC   │ ──→ │   State   │
    │ (intent)  │     │ (logic)  │     │  (result) │
    └──────────┘     └──────────┘     └──────────┘
                         ↓
                    UI rebuilds
```

```dart
// pubspec.yaml: flutter_bloc: ^8.1.0

// Cubit — simpler version (function → state)
class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);  // Initial state

  void increment() => emit(state + 1);  // Function → new state
  void decrement() => emit(state - 1);
  void reset() => emit(0);
}

// BLoC — event-driven (event → state)
abstract class CounterEvent {}
class Increment extends CounterEvent {}
class Decrement extends CounterEvent {}

class CounterBloc extends Bloc<CounterEvent, int> {
  CounterBloc() : super(0) {
    on<Increment>((event, emit) => emit(state + 1));
    on<Decrement>((event, emit) => emit(state - 1));
  }
}
```

### Cubit vs BLoC
| Feature | Cubit | BLoC |
|---------|-------|------|
| Trigger | Function call | Event object |
| Boilerplate | Less | More |
| Testing | Simpler | More structured |
| Traceability | Medium | High (events logged) |
| Use case | Simple state | Complex flows |

---

## Q2: How do you provide and consume BLoC?

```dart
// Provide BLoC
BlocProvider(
  create: (context) => CounterCubit(),
  child: const CounterScreen(),
)

// MultiBlocProvider — multiple BLoCs
MultiBlocProvider(
  providers: [
    BlocProvider(create: (_) => AuthBloc(authRepository)),
    BlocProvider(create: (_) => CartBloc()),
    BlocProvider(create: (_) => ThemeBloc()),
  ],
  child: const MyApp(),
)

// Consume with BlocBuilder
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return BlocBuilder<CounterCubit, int>(
      builder: (context, count) {
        return Text('$count');  // Rebuilds on state change
      },
    );
  }
}

// BlocBuilder with buildWhen — optimize rebuilds
BlocBuilder<CounterCubit, int>(
  buildWhen: (previous, current) => previous != current,  // Only rebuild if changed
  builder: (context, count) => Text('$count'),
)

// BlocSelector — select specific value
BlocSelector<CartBloc, CartState, int>(
  selector: (state) => state.itemCount,  // Only rebuild when itemCount changes
  builder: (context, itemCount) => Text('$itemCount items'),
)

// context.read — trigger events (no rebuild)
ElevatedButton(
  onPressed: () => context.read<CounterCubit>().increment(),  // Cubit
  child: const Text('+'),
)

ElevatedButton(
  onPressed: () => context.read<CounterBloc>().add(Increment()),  // BLoC
  child: const Text('+'),
)
```

---

## Q3: How do you structure a BLoC for a feature?

```dart
// Feature: Authentication

// 1. Events
abstract class AuthEvent {}
class LoginRequested extends AuthEvent {
  final String email;
  final String password;
  LoginRequested(this.email, this.password);
}
class LogoutRequested extends AuthEvent {}
class CheckAuthStatus extends AuthEvent {}

// 2. States
abstract class AuthState {}
class AuthInitial extends AuthState {}
class AuthLoading extends AuthState {}
class Authenticated extends AuthState {
  final User user;
  Authenticated(this.user);
}
class Unauthenticated extends AuthState {}
class AuthError extends AuthState {
  final String message;
  AuthError(this.message);
}

// 3. BLoC
class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final AuthRepository repository;

  AuthBloc(this.repository) : super(AuthInitial()) {
    on<LoginRequested>(_onLogin);
    on<LogoutRequested>(_onLogout);
    on<CheckAuthStatus>(_onCheckStatus);
  }

  Future<void> _onLogin(LoginRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      final user = await repository.login(event.email, event.password);
      emit(Authenticated(user));
    } catch (e) {
      emit(AuthError(e.toString()));
    }
  }

  Future<void> _onLogout(LogoutRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    await repository.logout();
    emit(Unauthenticated());
  }

  Future<void> _onCheckStatus(CheckAuthStatus event, Emitter<AuthState> emit) async {
    final user = await repository.getCurrentUser();
    if (user != null) {
      emit(Authenticated(user));
    } else {
      emit(Unauthenticated());
    }
  }
}

// 4. UI
class AuthScreen extends StatelessWidget {
  const AuthScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return BlocConsumer<AuthBloc, AuthState>(
      listener: (context, state) {
        // Side effects (navigation, snackbar)
        if (state is AuthError) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(state.message)),
          );
        }
        if (state is Authenticated) {
          context.go('/home');
        }
      },
      builder: (context, state) {
        return switch (state) {
          AuthInitial() => const LoginForm(),
          AuthLoading() => const CircularProgressIndicator(),
          Authenticated(user: final user) => HomeScreen(user: user),
          Unauthenticated() => const LoginForm(),
          AuthError(message: final msg) => LoginForm(errorMessage: msg),
        };
      },
    );
  }
}
```

---

## Q4: What is BlocBuilder vs BlocListener vs BlocConsumer?

| Widget | Rebuilds? | Side Effects? | Use Case |
|--------|-----------|---------------|----------|
| `BlocBuilder` | ✅ Yes | ❌ No | Rebuild UI on state change |
| `BlocListener` | ❌ No | ✅ Yes | Navigation, snackbar, dialog |
| `BlocConsumer` | ✅ Yes | ✅ Yes | Both rebuild + side effects |

```dart
// BlocBuilder — rebuild UI
BlocBuilder<AuthBloc, AuthState>(
  builder: (context, state) {
    if (state is AuthLoading) return const CircularProgressIndicator();
    return const LoginForm();
  },
)

// BlocListener — side effects only (no rebuild)
BlocListener<AuthBloc, AuthState>(
  listenWhen: (previous, current) => current is Authenticated,
  listener: (context, state) {
    if (state is Authenticated) {
      context.go('/home');  // Navigate
    }
  },
  child: const LoginForm(),  // Static UI
)

// BlocConsumer — both rebuild + side effects
BlocConsumer<AuthBloc, AuthState>(
  listenWhen: (previous, current) => current is AuthError || current is Authenticated,
  listener: (context, state) {
    if (state is AuthError) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(state.message)),
      );
    }
    if (state is Authenticated) {
      context.go('/home');
    }
  },
  buildWhen: (previous, current) => current is! AuthError,  // Don't rebuild on error
  builder: (context, state) {
    if (state is AuthLoading) return const CircularProgressIndicator();
    return const LoginForm();
  },
)
```

---

## Q5: How do you handle complex state with BLoC?

```dart
// Immutable state class (use equatable or freezed)
class CartState {
  final List<CartItem> items;
  final bool isLoading;
  final String? error;
  final double? total;

  const CartState({
    this.items = const [],
    this.isLoading = false,
    this.error,
    this.total,
  });

  CartState copyWith({
    List<CartItem>? items,
    bool? isLoading,
    String? error,
    double? total,
  }) {
    return CartState(
      items: items ?? this.items,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      total: total ?? this.total,
    );
  }
}

// Events
abstract class CartEvent {}
class LoadCart extends CartEvent {}
class AddToCart extends CartEvent {
  final Product product;
  AddToCart(this.product);
}
class RemoveFromCart extends CartEvent {
  final String productId;
  RemoveFromCart(this.productId);
}
class ClearCart extends CartEvent {}

// BLoC with complex state
class CartBloc extends Bloc<CartEvent, CartState> {
  final CartRepository repository;

  CartBloc(this.repository) : super(const CartState()) {
    on<LoadCart>(_onLoadCart);
    on<AddToCart>(_onAddToCart);
    on<RemoveFromCart>(_onRemoveFromCart);
    on<ClearCart>(_onClearCart);
  }

  Future<void> _onLoadCart(LoadCart event, Emitter<CartState> emit) async {
    emit(state.copyWith(isLoading: true, error: null));
    try {
      final items = await repository.getCart();
      final total = _calculateTotal(items);
      emit(state.copyWith(items: items, total: total, isLoading: false));
    } catch (e) {
      emit(state.copyWith(isLoading: false, error: e.toString()));
    }
  }

  Future<void> _onAddToCart(AddToCart event, Emitter<CartState> emit) async {
    emit(state.copyWith(isLoading: true));
    try {
      await repository.addItem(event.product);
      final items = [...state.items, CartItem(product: event.product)];
      emit(state.copyWith(
        items: items,
        total: _calculateTotal(items),
        isLoading: false,
      ));
    } catch (e) {
      emit(state.copyWith(isLoading: false, error: e.toString()));
    }
  }

  double _calculateTotal(List<CartItem> items) {
    return items.fold(0, (sum, item) => sum + item.price * item.quantity);
  }
}
```

---

## Q6: How do you test BLoC?

```dart
// pubspec.yaml: bloc_test: ^9.1.0

void main() {
  group('AuthBloc', () {
    late AuthBloc bloc;
    late MockAuthRepository repository;

    setUp(() {
      repository = MockAuthRepository();
      bloc = AuthBloc(repository);
    });

    tearDown(() => bloc.close());

    blocTest<AuthBloc, AuthState>(
      'emits [loading, authenticated] on successful login',
      build: () {
        when(repository.login('email', 'pass'))
            .thenAnswer((_) async => User('Alice'));
        return bloc;
      },
      act: (bloc) => bloc.add(LoginRequested('email', 'pass')),
      wait: const Duration(milliseconds: 100),
      expect: () => [
        AuthLoading(),
        Authenticated(User('Alice')),
      ],
    );

    blocTest<AuthBloc, AuthState>(
      'emits [loading, error] on failed login',
      build: () {
        when(repository.login('email', 'wrong'))
            .thenThrow(Exception('Invalid credentials'));
        return bloc;
      },
      act: (bloc) => bloc.add(LoginRequested('email', 'wrong')),
      expect: () => [
        isA<AuthLoading>(),
        isA<AuthError>(),
      ],
    );

    blocTest<AuthBloc, AuthState>(
      'emits [unauthenticated] on logout',
      build: () => bloc,
      act: (bloc) => bloc.add(LogoutRequested()),
      expect: () => [
        isA<AuthLoading>(),
        isA<Unauthenticated>(),
      ],
    );
  });

  group('Widget tests', () {
    testWidgets('Counter increments', (tester) async {
      await tester.pumpWidget(
        BlocProvider(
          create: (_) => CounterCubit(),
          child: const MaterialApp(home: CounterScreen()),
        ),
      );

      expect(find.text('0'), findsOneWidget);

      await tester.tap(find.text('+'));
      await tester.pump();

      expect(find.text('1'), findsOneWidget);
    });
  });
}
```

---

## Q7: How do you use BLoC with streams and transformers?

```dart
// Event transformers — debounce, throttle, etc.
class SearchBloc extends Bloc<SearchEvent, SearchState> {
  final SearchRepository repository;

  SearchBloc(this.repository) : super(const SearchState()) {
    // Debounce search events
    on<SearchQueryChanged>(
      _onSearch,
      transformer: debounce(const Duration(milliseconds: 300)),
    );
  }

  Future<void> _onSearch(SearchQueryChanged event, Emitter<SearchState> emit) async {
    emit(state.copyWith(isLoading: true));
    try {
      final results = await repository.search(event.query);
      emit(state.copyWith(results: results, isLoading: false));
    } catch (e) {
      emit(state.copyWith(error: e.toString(), isLoading: false));
    }
  }
}

// Custom transformer
EventTransformer<T> debounce<T>(Duration duration) {
  return (events, mapper) => events.debounceTime(duration).switchMap(mapper);
}

// Restartable — cancel previous event processing
on<FetchData>(
  _onFetchData,
  transformer: restartable(),
)

// Concurrent — process events in parallel
on<FetchData>(
  _onFetchData,
  transformer: concurrent(),
)

// Sequential — process events one at a time
on<FetchData>(
  _onFetchData,
  transformer: sequential(),
)
```

---

## 🔗 Related Topics
- [Riverpod](Riverpod.md)
- [Provider](Provider.md)
- [Best Practices](BestPractices.md)
