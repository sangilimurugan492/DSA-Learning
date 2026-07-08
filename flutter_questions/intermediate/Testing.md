# Testing

## Q1: What are the types of tests in Flutter?

```
Test Pyramid (bottom to top):

    Integration Tests (few)     — Full app flow on device/emulator
         ▲
    Widget Tests (some)          — Test single widget + interactions
         ▲
    Unit Tests (many)             — Test pure logic, functions, classes
```

| Test Type | Tests | Speed | Dependencies | Use Case |
|-----------|-------|-------|-------------|----------|
| Unit | Logic | Fast (<10ms) | None | Models, services, utils |
| Widget | UI | Medium (<1s) | Flutter framework | Single widget behavior |
| Integration | E2E | Slow (seconds) | Device/emulator | User flows |

```dart
// File naming convention:
// test/unit_test.dart          → Unit tests
// test/widget/widget_test.dart  → Widget tests
// integration_test/app_test.dart → Integration tests

// Run tests
// flutter test                          → All unit + widget tests
// flutter test test/widget/             → Specific directory
// flutter test --name "counter"          → By name
// flutter test integration_test/         → Integration tests
```

---

## Q2: How do you write unit tests?

```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:my_app/models/user.dart';
import 'package:my_app/services/calculator.dart';

void main() {
  group('Calculator', () {
    late Calculator calculator;

    setUp(() {
      calculator = Calculator();
    });

    tearDown(() {
      // Cleanup after each test
    });

    test('add returns sum of two numbers', () {
      expect(calculator.add(2, 3), 5);
    });

    test('divide throws on zero', () {
      expect(() => calculator.divide(10, 0), throwsA(isA<ArgumentError>()));
    });

    test('list contains item', () {
      final list = [1, 2, 3];
      expect(list, contains(2));
    });

    test('string starts with', () {
      expect('Hello World', startsWith('Hello'));
    });
  });

  group('User model', () {
    test('fromJson creates correct user', () {
      final user = User.fromJson({
        'id': 1,
        'name': 'Alice',
        'email': 'alice@test.com',
      });

      expect(user.id, 1);
      expect(user.name, 'Alice');
      expect(user.email, 'alice@test.com');
    });

    test('toJson returns correct map', () {
      final user = User(id: 1, name: 'Alice', email: 'alice@test.com');
      final json = user.toJson();

      expect(json['id'], 1);
      expect(json['name'], 'Alice');
    });
  });
}
```

### Common Matchers
```dart
expect(value, equals(5));           // Exact equality
expect(value, isTrue);              // Boolean true
expect(value, isNull);              // Null
expect(value, isA<int>());          // Type check
expect(list, contains(3));          // Contains
expect(list, hasLength(3));         // Length
expect(string, startsWith('Hi'));   // String prefix
expect(string, contains('world'));  // String contains
expect(() => fn(), throwsException);// Throws
expect(value, greaterThan(10));     // Comparison
expect(value, lessThanOrEqualTo(5));
expect(value, inInclusiveRange(1, 10));
```

---

## Q3: How do you mock dependencies?

```dart
// pubspec.yaml: mockito: ^5.4.0, build_runner

// 1. Create mock
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

@GenerateMocks([ApiClient])
import 'user_service_test.mocks.dart';

class ApiClient {
  Future<User> fetchUser(int id) async => /* ... */;
}

class UserService {
  final ApiClient api;
  UserService(this.api);

  Future<String> getUserName(int id) async {
    final user = await api.fetchUser(id);
    return user.name;
  }
}

// 2. Test with mock
void main() {
  late MockApiClient mockApi;
  late UserService service;

  setUp(() {
    mockApi = MockApiClient();
    service = UserService(mockApi);
  });

  test('getUserName returns user name', () async {
    // Arrange — stub the mock
    when(mockApi.fetchUser(1)).thenAnswer(
      (_) async => User(id: 1, name: 'Alice'),
    );

    // Act
    final name = await service.getUserName(1);

    // Assert
    expect(name, 'Alice');
    verify(mockApi.fetchUser(1)).called(1);  // Verify called once
  });

  test('getUserName throws when API fails', () async {
    when(mockApi.fetchUser(1)).thenThrow(Exception('Network error'));

    expect(() => service.getUserName(1), throwsException);
  });
}

// 3. Generate mocks: dart run build_runner build
```

---

## Q4: How do you write widget tests?

```dart
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:my_app/widgets/counter_widget.dart';

void main() {
  testWidgets('Counter increments on tap', (tester) async {
    // 1. Pump the widget
    await tester.pumpWidget(const MaterialApp(home: CounterWidget()));

    // 2. Verify initial state
    expect(find.text('Count: 0'), findsOneWidget);

    // 3. Tap the button
    await tester.tap(find.byType(ElevatedButton));
    await tester.pump();  // Rebuild after state change

    // 4. Verify updated state
    expect(find.text('Count: 1'), findsOneWidget);
  });

  testWidgets('Form validates empty email', (tester) async {
    await tester.pumpWidget(MaterialApp(home: LoginForm()));

    // Tap submit without entering email
    await tester.tap(find.text('Submit'));
    await tester.pump();

    // Validation error should appear
    expect(find.text('Email is required'), findsOneWidget);
  });

  testWidgets('List displays items', (tester) async {
    await tester.pumpWidget(MaterialApp(
      home: ItemList(items: ['Apple', 'Banana', 'Cherry']),
    ));

    // Find all list items
    expect(find.byType(ListTile), findsNWidgets(3));
    expect(find.text('Apple'), findsOneWidget);
    expect(find.text('Banana'), findsOneWidget);
  });

  testWidgets('Navigation works', (tester) async {
    await tester.pumpWidget(MaterialApp(
      home: HomeScreen(),
      routes: {'/detail': (_) => const DetailScreen()},
    ));

    await tester.tap(find.text('Go to Detail'));
    await tester.pumpAndSettle();  // Wait for animation

    expect(find.byType(DetailScreen), findsOneWidget);
  });
}
```

### Common Finders
```dart
find.text('Hello')              // By text
find.byType(ElevatedButton)    // By widget type
find.byIcon(Icons.add)         // By icon
find.byKey(ValueKey('email'))  // By key
find.byType(TextField)         // By widget type
find.descendant(
  of: find.byType(Card),
  matching: find.text('Title'),
)                               // Find within parent
find.ancestor(
  of: find.text('Title'),
  matching: find.byType(Card),
)                               // Find parent of child
```

---

## Q5: How do you test with Provider/Riverpod?

```dart
// Testing with Provider
testWidgets('Counter displays from provider', (tester) async {
  await tester.pumpWidget(
    ChangeNotifierProvider(
      create: (_) => CounterModel()..increment(),
      child: const MaterialApp(home: CounterScreen()),
    ),
  );

  expect(find.text('1'), findsOneWidget);
});

// Testing with Riverpod
testWidgets('Counter from riverpod', (tester) async {
  await tester.pumpWidget(
    ProviderScope(
      overrides: [
        counterProvider.overrideWith((ref) => CounterModel()..increment()),
      ],
      child: const MaterialApp(home: CounterScreen()),
    ),
  );

  expect(find.text('1'), findsOneWidget);
});

// Testing with BLoC
testWidgets('Counter from bloc', (tester) async {
  await tester.pumpWidget(
    BlocProvider(
      create: (_) => CounterCubit()..increment(),
      child: const MaterialApp(home: CounterScreen()),
    ),
  );

  expect(find.text('1'), findsOneWidget);
});
```

---

## Q6: How do you write integration tests?

```dart
// integration_test/app_test.dart
// pubspec.yaml: integration_test: ^0.0.1

import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:my_app/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('Login flow end to end', (tester) async {
    // 1. Start the app
    app.main();
    await tester.pumpAndSettle();

    // 2. Enter credentials
    await tester.enterText(find.byKey(const Key('email_field')), 'test@test.com');
    await tester.enterText(find.byKey(const Key('password_field')), 'password123');

    // 3. Tap login
    await tester.tap(find.byKey(const Key('login_button')));
    await tester.pumpAndSettle();

    // 4. Verify home screen
    expect(find.text('Welcome, Test User'), findsOneWidget);
  });

  testWidgets('Add item to cart', (tester) async {
    app.main();
    await tester.pumpAndSettle();

    // Navigate to products
    await tester.tap(find.text('Products'));
    await tester.pumpAndSettle();

    // Add first product to cart
    await tester.tap(find.byIcon(Icons.add_shopping_cart).first);
    await tester.pumpAndSettle();

    // Go to cart
    await tester.tap(find.byIcon(Icons.shopping_cart));
    await tester.pumpAndSettle();

    // Verify item in cart
    expect(find.byType(ListTile), findsNWidgets(1));
  });
}

// Run: flutter test integration_test/
```

---

## Q7: What is golden test (screenshot testing)?

```dart
// Golden test — compares widget rendering to a reference image
testWidgets('MyWidget looks correct', (tester) async {
  await tester.pumpWidget(
    const MaterialApp(home: Scaffold(body: MyWidget())),
  );

  await expectLater(
    find.byType(MyWidget),
    matchesGoldenFile('goldens/my_widget.png'),
  );
});

// Generate golden images (first run)
// flutter test --update-goldens

// Golden test with multiple states
testWidgets('Button states', (tester) async {
  await tester.pumpWidget(MaterialApp(
    home: Scaffold(
      body: Column(
        children: [
          PrimaryButton(label: 'Normal', onPressed: () {}),
          const PrimaryButton(label: 'Loading', onPressed: null, isLoading: true),
        ],
      ),
    ),
  ));

  await expectLater(
    find.byType(Column),
    matchesGoldenFile('goldens/button_states.png'),
  );
});
```

---

## 🔗 Related Topics
- [State Management Advanced](StateManagementAdvanced.md)
- [Custom Widgets](CustomWidgets.md)
- [CI/CD](../advanced/CICD.md)
