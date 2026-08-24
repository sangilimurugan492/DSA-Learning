# BLoC

## 📖 Explanation

BLoC (Business Logic Component) is an event-driven state management pattern: **Events → BLoC → States**. It uses streams to transform user actions (events) into UI states. BLoC enforces strict separation between UI and business logic.

### BLoC Flow
```
User Action → Event → BLoC (process) → State → UI Rebuild

    ┌──────────┐     ┌──────────┐     ┌──────────┐
    │   Event   │ ──→ │   BLoC   │ ──→ │   State   │
    │ (intent)  │     │ (logic)  │     │  (result) │
    └──────────┘     └──────────┘     └──────────┘
                         ↓
                    UI rebuilds
```

### BLoC vs Cubit
| Feature | BLoC | Cubit |
|---------|------|-------|
| Trigger | Event class | Function call |
| Boilerplate | More (events + states) | Less (states only) |
| Traceability | Event → State mapping | Function → State |
| Best for | Complex flows | Simpler state |

### Key Widgets
| Widget | Purpose |
|--------|---------|
| `BlocProvider` | Provides BLoC to widget tree |
| `BlocBuilder` | Rebuilds UI on state change |
| `BlocListener` | Side effects (navigation, snackbar) |
| `BlocConsumer` | Builder + Listener combined |
| `BlocSelector` | Rebuild on specific state |
| `MultiBlocProvider` | Provide multiple BLoCs |

### BLoC Best Practices
- Events = user intent (what happened)
- States = UI representation (what to show)
- BLoC = business logic (what to do)
- Never build UI in BLoC — BLoC is pure Dart
- Always close BLoC in `dispose()` (or use `BlocProvider`)
- Use `Equatable` for events and states (value equality)

---

## 🧪 Code Example

```dart
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

// ── Cubit (simpler — function → state) ──
class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);  // Initial state

  void increment() => emit(state + 1);
  void decrement() => emit(state - 1);
  void reset() => emit(0);
}

// Usage
BlocProvider(
  create: (_) => CounterCubit(),
  child: BlocBuilder<CounterCubit, int>(
    builder: (context, count) => Text('$count'),
  ),
)

// ── BLoC (event-driven — event → state) ──

// Events
abstract class CartEvent extends Equatable {
  const CartEvent();
  @override List<Object> get props => [];
}

class AddItem extends CartEvent {
  final Item item;
  const AddItem(this.item);
  @override List<Object> get props => [item];
}

class RemoveItem extends CartEvent {
  final Item item;
  const RemoveItem(this.item);
  @override List<Object> get props => [item];
}

class ClearCart extends CartEvent {}

// States
abstract class CartState extends Equatable {
  const CartState();
  @override List<Object> get props => [];
}

class CartInitial extends CartState {}

class CartLoading extends CartState {}

class CartLoaded extends CartState {
  final List<Item> items;
  const CartLoaded(this.items);

  double get totalPrice =>
      items.fold(0.0, (sum, item) => sum + item.price);

  @override List<Object> get props => [items];
}

class CartError extends CartState {
  final String message;
  const CartError(this.message);
  @override List<Object> get props => [message];
}

// BLoC
class CartBloc extends Bloc<CartEvent, CartState> {
  final UserRepository repository;

  CartBloc(this.repository) : super(CartInitial()) {
    on<AddItem>(_onAddItem);
    on<RemoveItem>(_onRemoveItem);
    on<ClearCart>(_onClearCart);
  }

  Future<void> _onAddItem(AddItem event, Emitter<CartState> emit) async {
    final current = state is CartLoaded
        ? (state as CartLoaded).items
        : <Item>[];
    emit(CartLoaded([...current, event.item]));
  }

  Future<void> _onRemoveItem(RemoveItem event, Emitter<CartState> emit) async {
    if (state is CartLoaded) {
      final items = (state as CartLoaded).items
          .where((i) => i != event.item)
          .toList();
      emit(CartLoaded(items));
    }
  }

  Future<void> _onClearCart(ClearCart event, Emitter<CartState> emit) async {
    emit(const CartLoaded([]));
  }
}

// ── UI: BlocProvider + BlocBuilder ──
class CartScreen extends StatelessWidget {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => CartBloc(getIt<UserRepository>()),
      child: BlocBuilder<CartBloc, CartState>(
        builder: (context, state) {
          if (state is CartLoading) {
            return const CircularProgressIndicator();
          }
          if (state is CartError) {
            return Text('Error: ${state.message}');
          }
          if (state is CartLoaded) {
            return ListView.builder(
              itemCount: state.items.length,
              itemBuilder: (_, i) => ListTile(
                title: Text(state.items[i].name),
                trailing: IconButton(
                  icon: const Icon(Icons.remove),
                  onPressed: () =>
                    context.read<CartBloc>().add(RemoveItem(state.items[i])),
                ),
              ),
            );
          }
          return const Text('Cart empty');
        },
      ),
    );
  }
}

// ── BlocListener (side effects) ──
BlocListener<CartBloc, CartState>(
  listener: (context, state) {
    if (state is CartLoaded && state.items.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Cart cleared!')),
      );
    }
    if (state is CartError) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(state.message)),
      );
    }
  },
  child: BlocBuilder<CartBloc, CartState>(...),
)

// ── BlocConsumer (builder + listener) ──
BlocConsumer<CartBloc, CartState>(
  listener: (context, state) { /* side effects */ },
  builder: (context, state) { /* UI */ },
)

// ── MultiBlocProvider ──
MultiBlocProvider(
  providers: [
    BlocProvider(create: (_) => CartBloc()),
    BlocProvider(create: (_) => AuthBloc()),
    BlocProvider(create: (_) => ThemeBloc()),
  ],
  child: const MyApp(),
)
```

### Output
```
A Flutter app with BLoC state management:
- Cubit for simple counter (function → state)
- CartBloc with Events (AddItem, RemoveItem, ClearCart) → States (CartLoaded, CartError)
- BlocProvider for dependency injection
- BlocBuilder for UI rebuilds, BlocListener for side effects
- Equatable for value equality in events/states
- MultiBlocProvider for multiple BLoCs
```

---

## ❓ Interview Questions

1. **What is BLoC and how does it work?**
   - BLoC (Business Logic Component) uses an event-driven pattern: Events → BLoC → States. Events are immutable classes representing user intent (`AddItem`, `RemoveItem`). States are immutable classes representing UI state (`CartLoaded`, `CartError`). The BLoC registers event handlers with `on<Event>((event, emit) { ... })` and transforms events into states by calling `emit(newState)`. The UI dispatches events: `context.read<CartBloc>().add(AddItem(item))`. The UI rebuilds with `BlocBuilder<Bloc, State>`. BLoC enforces strict separation: UI only dispatches events and renders states — all logic is in the BLoC. This makes it highly testable and traceable.

2. **What is the difference between BLoC and Cubit?**
   - **Cubit** is simpler — functions directly emit states. `class CounterCubit extends Cubit<int> { void increment() => emit(state + 1); }`. UI calls `context.read<CounterCubit>().increment()`. Less boilerplate (no event classes). Good for simple state. **BLoC** is event-driven — events trigger state changes. `class CounterBloc extends Bloc<CounterEvent, int> { on<Increment>((event, emit) => emit(state + 1)); }`. UI dispatches `context.read<CounterBloc>().add(Increment())`. More boilerplate (events + states) but better traceability — every state change is triggered by an explicit event. Use Cubit for simple state, BLoC for complex flows with many transitions.

3. **What is BlocBuilder vs BlocListener vs BlocConsumer?**
   - `BlocBuilder<Bloc, State>(builder: (context, state) => Widget)` — rebuilds the widget tree on every state change. Use for UI that depends on state. `BlocListener<Bloc, State>(listener: (context, state) { ... })` — calls a callback on state change WITHOUT rebuilding. Use for side effects: navigation, snackbars, dialogs. `BlocConsumer<Bloc, State>(listener: ..., builder: ...)` — combines both: rebuilds UI AND calls listener. Use when you need both UI rebuild and side effects. `BlocSelector<Bloc, State, T>(selector: (state) => state.field, builder: ...)` — rebuilds only when selected field changes (like Selector in Provider).

4. **Why use Equatable in BLoC?**
   - `Equatable` provides value equality (`==` and `hashCode`) without writing boilerplate. Without Equatable, Dart uses reference equality — two `CartLoaded([item])` instances are not equal even if items are identical. This causes unnecessary rebuilds because BlocBuilder thinks state changed. With Equatable: `class CartLoaded extends Equatable { final List<Item> items; @override List<Object> get props => [items]; }` — BlocBuilder compares `props` and only rebuilds if they changed. Always use Equatable for events (prevents duplicate event processing) and states (prevents unnecessary rebuilds). Include all relevant fields in `props`.

5. **How do you test BLoC?**
   - Use `bloc_test` package: `blocTest<CartBloc, CartState>('emits [CartLoaded] when AddItem', build: () => CartBloc(mockRepo), act: (bloc) => bloc.add(AddItem(item)), expect: () => [CartLoaded([item])])`. Mock dependencies with `mocktail` — inject mock repository into BLoC constructor. Test all events and expected state sequences. Test error cases: `when(() => repo.save()).thenThrow(Exception())`, `expect: () => [CartError('message')]`. Use `verify: (bloc) { verify(() => mockRepo.save()).called(1); }` to verify side effects. Always `close()` the BLoC in `tearDown` to prevent memory leaks. Test that unexpected events don't emit states.

6. **How do you handle async operations in BLoC?**
   - In event handlers, use `async/await`: `on<FetchData>(_onFetchData); Future<void> _onFetchData(FetchData event, Emitter<CartState> emit) async { emit(CartLoading()); try { final data = await repo.fetch(); emit(CartLoaded(data)); } catch (e) { emit(CartError(e.toString())); } }`. Emit loading state first, then data or error. Use `emit` for each state transition. For debouncing: `on<Search>(_onSearch, transformer: restartable())`. For concurrent events: `transformer: concurrent()`. For sequential: `transformer: sequential()`. Always check that the BLoC is not closed before emitting: `if (isClosed) return;`.

7. **What is BlocProvider and how does it manage lifecycle?**
   - `BlocProvider(create: (_) => MyBloc())` creates and provides the BLoC to the widget tree. It automatically calls `bloc.close()` when the provider is removed from the tree (e.g., screen popped) — prevents memory leaks. Use `BlocProvider.value(value: existingBloc, child: ...)` to provide an existing BLoC without auto-close (manage lifecycle manually). Access BLoC: `context.read<MyBloc>()` (no rebuild) or `BlocBuilder<MyBloc, MyState>` (rebuild). `MultiBlocProvider` provides multiple BLoCs. BlocProvider uses `InheritedWidget` internally. Provide BLoCs at the appropriate scope — app-level BLoC at root, screen-level BLoC at the screen.

8. **How do you handle state persistence in BLoC?**
   - Use `HydratedBloc` (from `hydrated_bloc` package) for automatic state persistence. Extend `HydratedBloc<Event, State>` instead of `Bloc`. Override `fromJson` and `toJson`: `@override State fromJson(Map<String, dynamic> json) => State.fromJson(json); @override Map<String, dynamic> toJson(State state) => state.toJson();`. HydratedBloc automatically saves state to `SharedPreferences` on every emit and restores on app start. Use for: theme, locale, auth state, onboarding completion. For manual persistence: listen to BLoC state changes and save to SharedPreferences/Hive. Always persist only serializable state — not controllers or streams.

9. **What is EventTransformer in BLoC?**
   - `EventTransformer` controls how events are processed. Default: sequential (one at a time, in order). Options: `restartable()` — cancels previous event handler when new event arrives (use for search). `concurrent()` — processes events concurrently (use for independent events). `sequential()` — processes one at a time (default). `droppable()` — ignores new events while processing (use for prevent double-submit). Apply: `on<Search>(_onSearch, transformer: restartable())`. From `bloc_concurrency` package. Use `restartable` for search (cancel old query), `droppable` for submit buttons (prevent duplicates), `concurrent` for independent actions, `sequential` for dependent actions.

10. **When should you choose BLoC over other state management?**
    - Choose BLoC when: (1) App is large/complex with many state transitions. (2) Team is 5+ developers — BLoC's strict structure helps coordination. (3) You need high testability — BLoC is the most testable option. (4) You need event-driven architecture with traceability. (5) You need complex event transformation (debounce, throttle, concurrent). Don't choose BLoC for: simple apps (use Provider/Riverpod), rapid prototyping (use GetX), or when the team isn't familiar with reactive programming. BLoC has the most boilerplate (events, states, BLoC class) but provides the most structure. For medium apps, Cubit is a good middle ground — less boilerplate than BLoC, more structured than Provider.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Riverpod](Riverpod.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
