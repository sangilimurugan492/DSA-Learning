# Testing

## 📖 Explanation

Testing in Flutter ensures your app works correctly and prevents regressions. Flutter supports three types of tests: **Unit tests** (logic), **Widget tests** (single widget), and **Integration tests** (full app flow).

### Test Types
| Test Type | What It Tests | Speed | Dependencies |
|-----------|--------------|-------|-------------|
| Unit | Functions, classes, logic | Fast | None |
| Widget | Single widget UI | Medium | Flutter framework |
| Integration | Full app flow | Slow | Device/emulator |

### Testing Pyramid
```
        /\
       /  \        Integration (few)
      /----\
     /      \      Widget (some)
    /----------\
   /            \    Unit (many)
  /________________\
```

### Key Testing Packages
| Package | Purpose |
|---------|---------|
| `flutter_test` | Unit + widget tests (built-in) |
| `mockito` | Mock dependencies |
| `mocktail` | Mock dependencies (no codegen) |
| `integration_test` | Integration tests |
| `bloc_test` | Test BLoC state changes |
| `patrol` | Advanced integration testing |

### Arrange-Act-Assert Pattern
```dart
test('description', () {
  // Arrange — set up test data
  final calculator = Calculator();

  // Act — perform the action
  final result = calculator.add(2, 3);

  // Assert — verify the result
  expect(result, 5);
});
```

### Mocking
- `Mockito` — `class MockRepo extends Mock implements Repo {}`, `when(mock.method()).thenAnswer(...)`, `verify(mock.method()).called(1)`
- `Mocktail` — same API but no code generation, no `@GenerateMocks`
- Mock dependencies (API clients, databases) to test in isolation

### Widget Testing Helpers
| Helper | Purpose |
|--------|---------|
| `testWidgets()` | Widget test entry point |
| `tester.pumpWidget()` | Render a widget |
| `tester.pump()` | Advance one frame |
| `tester.pumpAndSettle()` | Advance until settled |
| `tester.tap()` | Simulate tap |
| `tester.enterText()` | Enter text |
| `tester.drag()` | Simulate drag |
| `expect(find.byType(...), findsOneWidget)` | Find widget |
| `find.text('Hello')` | Find by text |
| `find.byKey(Key('key'))` | Find by key |

---

## 🧪 Code Example

```dart
// ── Unit Test ──
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('Calculator', () {
    late Calculator calculator;

    setUp(() {
      calculator = Calculator();  // Fresh instance for each test
    });

    test('adds two numbers correctly', () {
      expect(calculator.add(2, 3), 5);
      expect(calculator.add(-1, 1), 0);
      expect(calculator.add(0, 0), 0);
    });

    test('divides two numbers correctly', () {
      expect(calculator.divide(10, 2), 5);
    });

    test('throws on division by zero', () {
      expect(() => calculator.divide(10, 0), throwsA(isA<ArgumentError>()));
    });

    tearDown(() {
      // Clean up if needed
    });
  });
}

// ── Unit Test with Mocking (mocktail) ──
import 'package:mocktail/mocktail.dart';

class MockUserRepository extends Mock implements UserRepository {}

void main() {
  late MockUserRepository mockRepo;
  late GetUserUseCase useCase;

  setUp(() {
    mockRepo = MockUserRepository();
    useCase = GetUserUseCase(mockRepo);
    registerFallbackValue(User(id: 0, name: '', email: ''));
  });

  test('returns user when repository succeeds', () async {
    // Arrange
    when(() => mockRepo.getUser(1)).thenAnswer((_) async =>
      const User(id: 1, name: 'Alice', email: 'alice@test.com'));

    // Act
    final result = await useCase(1);

    // Assert
    expect(result.name, 'Alice');
    verify(() => mockRepo.getUser(1)).called(1);
  });

  test('throws when repository fails', () async {
    when(() => mockRepo.getUser(1))
      .thenThrow(Exception('Network error'));

    expect(() => useCase(1), throwsException);
  });
}

// ── Widget Test ──
void main() {
  testWidgets('Counter increments on tap', (tester) async {
    // Arrange — pump the widget
    await tester.pumpWidget(const MaterialApp(home: CounterScreen()));

    // Verify initial state
    expect(find.text('Count: 0'), findsOneWidget);

    // Act — tap the button
    await tester.tap(find.byIcon(Icons.add));
    await tester.pump();  // Trigger rebuild

    // Assert
    expect(find.text('Count: 1'), findsOneWidget);
  });

  testWidgets('Form validation shows error on empty submit', (tester) async {
    await tester.pumpWidget(const MaterialApp(home: LoginForm()));

    // Tap submit without entering text
    await tester.tap(find.byType(ElevatedButton));
    await tester.pump();

    // Verify error message
    expect(find.text('Email is required'), findsOneWidget);
  });

  testWidgets('List displays items from data', (tester) async {
    final items = ['Apple', 'Banana', 'Cherry'];

    await tester.pumpWidget(MaterialApp(
      home: ItemList(items: items),
    ));

    // Verify all items are displayed
    expect(find.text('Apple'), findsOneWidget);
    expect(find.text('Banana'), findsOneWidget);
    expect(find.text('Cherry'), findsOneWidget);
    expect(find.byType(ListTile), findsNWidgets(3));
  });
}

// ── BLoC Test ──
import 'package:bloc_test/bloc_test.dart';

void main() {
  group('CartBloc', () {
    late CartBloc bloc;

    setUp(() {
      bloc = CartBloc();
    });

    tearDown(() => bloc.close());

    blocTest<CartBloc, CartState>(
      'emits [CartUpdated] when AddItem is added',
      build: () => bloc,
      act: (bloc) => bloc.add(AddItem(const Item(name: 'Apple'))),
      expect: () => [
        isA<CartUpdated>().having((s) => s.items.length, 'count', 1),
      ],
    );

    blocTest<CartBloc, CartState>(
      'emits [CartUpdated] with empty list when ClearCart',
      build: () => bloc,
      act: (bloc) => bloc.add(ClearCart()),
      expect: () => [CartUpdated([])],
    );
  });
}

// ── Integration Test ──
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('Full login flow', (tester) async {
    app.main();
    await tester.pumpAndSettle();

    // Enter credentials
    await tester.enterText(find.byKey(const Key('email_field')), 'test@test.com');
    await tester.enterText(find.byKey(const Key('password_field')), 'password');
    await tester.tap(find.byKey(const Key('login_button')));
    await tester.pumpAndSettle();

    // Verify home screen
    expect(find.text('Welcome, test@test.com'), findsOneWidget);
  });
}

// ── Run tests ──
// flutter test                          # All unit + widget tests
// flutter test test/unit/               # Specific folder
// flutter test --coverage               # With coverage report
// flutter test integration_test/        # Integration tests
// flutter test --name "Counter"         # By test name
```

### Output
```
A Flutter app with comprehensive tests:
- Unit tests: Calculator logic with Arrange-Act-Assert
- Mocked unit tests: UserRepository mock with mocktail
- Widget tests: CounterScreen, LoginForm validation, ItemList rendering
- BLoC tests: CartBloc state transitions with bloc_test
- Integration test: Full login flow with pumpAndSettle
```

---

## ❓ Interview Questions

1. **What are the types of tests in Flutter?**
   - Three types: **Unit tests** — test individual functions, classes, and business logic in isolation. Fast, no UI. Use `test()` and `expect()`. **Widget tests** — test a single widget's rendering and interaction. Medium speed. Use `testWidgets()` and `WidgetTester` to pump, tap, and verify. **Integration tests** — test the full app flow on a device/emulator. Slow but realistic. Use `integration_test` package with `IntegrationTestWidgetsFlutterBinding`. Follow the testing pyramid: many unit tests, some widget tests, few integration tests. Run unit/widget with `flutter test`, integration with `flutter test integration_test/`.

2. **How do you write a unit test in Flutter?**
   - Use the `flutter_test` package. Structure with `test('description', () { ... })` or `group('GroupName', () { ... })`. Follow Arrange-Act-Assert: (1) Arrange — create test data and dependencies. (2) Act — call the method being tested. (3) Assert — verify the result with `expect(actual, expected)`. Use `setUp()` for common setup (fresh instance per test), `tearDown()` for cleanup. Test edge cases: null input, empty lists, boundary values, error cases (`expect(() => fn(), throwsException)`). Use `group()` to organize related tests. Run with `flutter test test/my_test.dart`.

3. **How do you mock dependencies in tests?**
   - Use `mocktail` (no code generation) or `mockito` (with code generation). With mocktail: `class MockRepo extends Mock implements UserRepository {}`, then `when(() => mockRepo.getUser(1)).thenAnswer((_) async => user)` for stubbing, `verify(() => mockRepo.getUser(1)).called(1)` for verification. Mock external dependencies (API clients, databases, SharedPreferences) so tests are fast and deterministic. Inject mocks through constructors. For Provider: override providers in tests. For Riverpod: `ProviderContainer(overrides: [...])`. For BLoC: inject mock repository into the BLoC constructor.

4. **How do you write a widget test?**
   - Use `testWidgets('description', (tester) async { ... })`. Pump the widget: `await tester.pumpWidget(MaterialApp(home: MyWidget()))`. Interact: `await tester.tap(find.byType(ElevatedButton))`, `await tester.enterText(find.byKey(Key('email')), 'test@test.com')`, `await tester.drag(find.byType(ListView), Offset(0, -100))`. After interaction, call `await tester.pump()` (one frame) or `await tester.pumpAndSettle()` (until all animations finish). Verify: `expect(find.text('Hello'), findsOneWidget)`, `expect(find.byType(LoadingIndicator), findsNothing)`. Wrap in `MaterialApp` for context (theme, direction, media query).

5. **What is the difference between `pump()` and `pumpAndSettle()`?**
   - `tester.pump()` advances the widget tree by one frame (16ms). Use after a single state change or animation step. `tester.pump(Duration(seconds: 1))` advances by a specific duration. `tester.pumpAndSettle()` repeatedly pumps frames until all animations and scheduled frames are complete — no more pending work. Use after triggering an action that starts animations (tap, scroll, state change). `pumpAndSettle` can time out if there's a continuous animation (like a loading spinner) — use `pump(Duration(...))` instead. Rule: use `pump()` for immediate rebuilds, `pumpAndSettle()` when animations need to complete.

6. **How do you test BLoC?**
   - Use the `bloc_test` package. `blocTest<BlocType, StateType>('description', build: () => MyBloc(), act: (bloc) => bloc.add(MyEvent()), expect: () => [ExpectedState()])`. This builds the BLoC, dispatches events, and verifies emitted states. For async events, `wait: Duration(seconds: 1)`. Mock dependencies by injecting them into the BLoC constructor. Test all events and state transitions. Test error cases — verify error states are emitted. Use `blocTest` with `verify: (bloc) { verify(mock.method()).called(1); }` to verify side effects. Close the BLoC in `tearDown` to prevent memory leaks.

7. **How do you write an integration test?**
   - Use the `integration_test` package. Create `integration_test/app_test.dart`. Initialize with `IntegrationTestWidgetsFlutterBinding.ensureInitialized()`. Write `testWidgets('flow', (tester) async { app.main(); await tester.pumpAndSettle(); ... })`. Call `app.main()` to start the real app. Use `find.byKey()` for reliable widget finding. Simulate user actions: tap, enter text, scroll, swipe. Verify the outcome. Run with `flutter test integration_test/` (headless) or `flutter test integration_test/ -d <device>` (on device). Integration tests test the full app including navigation, real APIs (use mock server), and platform plugins.

8. **How do you measure test coverage?**
   - Run `flutter test --coverage` — generates `coverage/lcov.info`. View with `genhtml coverage/lcov.info -o coverage/html` then open `coverage/html/index.html`. Coverage shows which lines of code are executed during tests. Target 80%+ for business logic, 60%+ for widgets. Use `ignore: 3` comments to exclude lines. Don't chase 100% — focus on critical business logic, edge cases, and error paths. Upload to Codecov in CI with `codecov/codecov-action`. Use `flutter test --coverage --coverage-path=coverage/lcov.info`. Focus coverage on domain and data layers — presentation layer is better covered by widget/integration tests.

9. **How do you test async code?**
   - For `Future`-based code: `test('async test', () async { final result = await service.fetchData(); expect(result, expected); })`. For `Stream`-based code: use `expectLater(stream, emitsInOrder([event1, event2, emitsDone]))` or `stream.toList()` to collect all events. For timers/delays: use `fakeAsync` — `test('timer', fakeAsync((async) { final timer = Timer(Duration(seconds: 1), callback); async.elapse(Duration(seconds: 1)); expect(callbackCalled, isTrue); }))`. For pending timers: use `tester.pumpAndSettle()` in widget tests. Always await `Future`s in tests — don't leave them unawaited. Test both success and failure paths for async code.

10. **What are best practices for testing in Flutter?**
    - (1) Follow the testing pyramid — many unit, some widget, few integration tests. (2) Test behavior, not implementation — don't test private methods. (3) Use Arrange-Act-Assert for readability. (4) One assertion per test (or related assertions). (5) Test edge cases: null, empty, boundary, error. (6) Mock external dependencies — tests should be fast and deterministic. (7) Name tests descriptively: `'returns empty list when API returns 404'`. (8) Use `setUp`/`tearDown` for common setup/cleanup. (9) Run tests in CI on every PR. (10) Don't test Flutter framework — test your code. (11) Use `find.byKey` for stable widget finding. (12) Aim for 80% coverage on business logic.

---

## 🔗 Related Topics
- [State Management Advanced](StateManagementAdvanced.md)
- [CI/CD](../advanced/CICD.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
