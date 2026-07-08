# State Management Advanced

## Q1: How does Riverpod work?

```dart
// Riverpod — compile-safe, testable state management
// pubspec.yaml: flutter_riverpod: ^2.4.0

// 1. Provider — read-only value
final greetingProvider = Provider<String>((ref) {
  return 'Hello, World!';
});

// 2. StateProvider — simple mutable state
final counterProvider = StateProvider<int>((ref) => 0);
// Modify: ref.read(counterProvider.notifier).state++

// 3. StateNotifierProvider — complex state with notifier
class TodoNotifier extends StateNotifier<List<Todo>> {
  TodoNotifier() : super([]);

  void add(Todo todo) => state = [...state, todo];
  void remove(String id) => state = state.where((t) => t.id != id).toList();
  void toggle(String id) => state = [
    for (final t in state)
      if (t.id == id) t.copyWith(done: !t.done) else t,
  ];
}

final todoProvider = StateNotifierProvider<TodoNotifier, List<Todo>>((ref) {
  return TodoNotifier();
});

// 4. FutureProvider — async data
final userProvider = FutureProvider<User>((ref) async {
  return api.fetchUser();
});

// 5. StreamProvider — stream data
final authProvider = StreamProvider<User?>((ref) {
  return FirebaseAuth.instance.authStateChanges();
});

// 6. ChangeNotifierProvider — for existing ChangeNotifier models
final modelProvider = ChangeNotifierProvider<MyModel>((ref) => MyModel());
```

### Consuming Providers
```dart
// ConsumerWidget — for StatelessWidget
class CounterScreen extends ConsumerWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);  // Rebuilds on change
    return Text('$count');
  }
}

// ConsumerStatefulWidget — for StatefulWidget
class TodoScreen extends ConsumerStatefulWidget {
  const TodoScreen({super.key});
  @override
  ConsumerState<TodoScreen> createState() => _TodoScreenState();
}

class _TodoScreenState extends ConsumerState<TodoScreen> {
  @override
  Widget build(BuildContext context) {
    final todos = ref.watch(todoProvider);  // List<Todo>
    final notifier = ref.read(todoProvider.notifier);  // TodoNotifier

    return ListView.builder(
      itemCount: todos.length,
      itemBuilder: (context, index) {
        final todo = todos[index];
        return ListTile(
          title: Text(todo.title),
          trailing: Checkbox(
            value: todo.done,
            onChanged: (_) => notifier.toggle(todo.id),
          ),
        );
      },
    );
  }
}

// ref.watch — rebuilds on change (in build only)
// ref.read — read once (in callbacks, initState)
// ref.listen — side effect on change
ref.listen<int>(counterProvider, (previous, next) {
  if (next > 10) showSnackBar('Limit reached!');
});
```

---

## Q2: How does BLoC/Cubit work?

```dart
// BLoC — Event → State (streams)
// pubspec.yaml: flutter_bloc: ^8.1.0

// Cubit — simpler version of BLoC (function → state)
class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);

  void increment() => emit(state + 1);
  void decrement() => emit(state - 1);
  void reset() => emit(0);
}

// Provide Cubit
BlocProvider(
  create: (_) => CounterCubit(),
  child: const CounterScreen(),
);

// Consume Cubit
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return BlocBuilder<CounterCubit, int>(
      builder: (context, count) {
        return Text('$count');
      },
    );
  }
}

// BLoC — Event-driven (more structured)
abstract class CounterEvent {}
class Increment extends CounterEvent {}
class Decrement extends CounterEvent {}

class CounterBloc extends Bloc<CounterEvent, int> {
  CounterBloc() : super(0) {
    on<Increment>((event, emit) => emit(state + 1));
    on<Decrement>((event, emit) => emit(state - 1));
  }
}

// Usage
context.read<CounterBloc>().add(Increment());
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

## Q3: How do you structure BLoC for a feature?

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
}

// 4. UI
BlocProvider(
  create: (_) => AuthBloc(authRepository),
  child: BlocBuilder<AuthBloc, AuthState>(
    builder: (context, state) {
      return switch (state) {
        AuthInitial() => const LoginForm(),
        AuthLoading() => const CircularProgressIndicator(),
        Authenticated(user) => HomeScreen(user: user),
        Unauthenticated() => const LoginForm(),
        AuthError(message) => ErrorWidget(message: message),
      };
    },
  ),
)
```

---

## Q4: What is `Selector` and `Consumer` in Provider?

```dart
// Consumer — rebuilds entire builder when model changes
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Text('Total: ${cart.total}');  // Rebuilds on any change
  },
)

// Selector — rebuilds only when selected value changes
Selector<CartModel, int>(
  selector: (context, cart) => cart.itemCount,  // Select specific value
  builder: (context, itemCount, child) {
    return Text('Items: $itemCount');  // Only rebuilds when itemCount changes
  },
)

// Multi-provider
MultiProvider(
  providers: [
    ChangeNotifierProvider(create: (_) => AuthModel()),
    ChangeNotifierProvider(create: (_) => CartModel()),
    ChangeNotifierProvider(create: (_) => ThemeModel()),
  ],
  child: const MyApp(),
)

// ProxyProvider — depends on another provider
ProxyProvider<AuthModel, ApiClient>(
  update: (context, auth, previous) => ApiClient(token: auth.token),
)
```

---

## Q5: How do you handle async state with Riverpod?

```dart
// FutureProvider — handles loading/data/error automatically
final userProvider = FutureProvider<User>((ref) async {
  return api.fetchUser();
});

// Consume with when()
class UserScreen extends ConsumerWidget {
  const UserScreen({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider);

    return userAsync.when(
      data: (user) => Text('Hello, ${user.name}'),
      loading: () => const CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}

// AsyncNotifier — more control over async state
class UserNotifier extends AsyncNotifier<User> {
  @override
  Future<User> build() async {
    return api.fetchUser();
  }

  Future<void> refresh() async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() => api.fetchUser());
  }
}

final userNotifierProvider = AsyncNotifierProvider<UserNotifier, User>(UserNotifier.new);

// StreamProvider — real-time data
final messagesProvider = StreamProvider<List<Message>>((ref) {
  return firestore.collection('messages').snapshots().map(
    (snapshot) => snapshot.docs.map((d) => Message.fromDoc(d)).toList(),
  );
});
```

---

## Q6: How do you test state management?

```dart
// Testing Riverpod
void main() {
  test('counter starts at 0', () {
    final container = ProviderContainer();
    addTearDown(container.dispose);

    expect(container.read(counterProvider), 0);

    container.read(counterProvider.notifier).state++;
    expect(container.read(counterProvider), 1);
  });

  test('todo notifier adds todo', () {
    final container = ProviderContainer();
    final notifier = container.read(todoProvider.notifier);

    notifier.add(Todo(id: '1', title: 'Test'));
    expect(container.read(todoProvider), hasLength(1));
  });
}

// Testing BLoC
void main() {
  late AuthBloc bloc;

  setUp(() {
    bloc = AuthBloc(MockAuthRepository());
  });

  blocTest<AuthBloc, AuthState>(
    'emits [loading, authenticated] on successful login',
    build: () => bloc,
    act: (bloc) => bloc.add(LoginRequested('email', 'pass')),
    wait: const Duration(milliseconds: 500),
    expect: () => [isA<AuthLoading>(), isA<Authenticated>()],
  );
}
```

---

## Q7: How do you choose the right state management solution?

```
App Size         → Recommended
─────────────────────────────────
Small/Prototype  → setState + Provider
Medium           → Riverpod or Provider
Large/Team       → BLoC or Riverpod
Enterprise       → BLoC (strict architecture)
```

| Criteria | Provider | Riverpod | BLoC | GetX |
|----------|----------|----------|------|------|
| Compile-safe | ❌ | ✅ | ✅ | ❌ |
| Testability | Good | Excellent | Excellent | Medium |
| Boilerplate | Low | Low | High | Very Low |
| Learning curve | Low | Medium | High | Low |
| DevTools | Basic | Good | Excellent | Basic |
| Community | Large | Growing | Large | Medium |

> **Decision:** Riverpod for new projects (compile-safe, testable, flexible). BLoC for large teams needing strict event-driven architecture. Provider for simple apps.

---

## 🔗 Related Topics
- [State Management](../beginner/StateManagement.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
- [State Management Scenarios](../scenario_based/StateManagementScenarios.md)
