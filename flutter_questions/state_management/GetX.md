# GetX

## Q1: What is GetX?

GetX is a lightweight, all-in-one solution for state management, navigation, and dependency injection.

```dart
// pubspec.yaml: get: ^4.6.6

// GetX combines:
// 1. State management (reactive)
// 2. Route management (no context needed)
// 3. Dependency injection (Get.put, Get.find)
// 4. Utilities (translations, themes, dialogs)
```

### GetX Three Pillars
| Pillar | Description |
|--------|-------------|
| Performance | Minimal rebuilds, only what changes |
| Productivity | Less boilerplate, no context needed |
| Organization | State, routes, DI in one package |

---

## Q2: How do you manage state with GetX?

```dart
// 1. GetxController — holds reactive state
class CounterController extends GetxController {
  var count = 0.obs;  // .obs makes it reactive

  void increment() => count.value++;
  void decrement() => count.value--;
  void reset() => count.value = 0;
}

// 2. Provide controller (dependency injection)
void main() {
  Get.put(CounterController());  // Register
  runApp(const GetMaterialApp(home: HomeScreen()));
}

// 3. Consume with Obx
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final controller = Get.find<CounterController>();  // Get instance

    return Scaffold(
      body: Center(
        child: Obx(() => Text('${controller.count.value}')),  // Rebuilds on change
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: controller.increment,
        child: const Icon(Icons.add),
      ),
    );
  }
}
```

### Reactive Types
```dart
// .obs — makes any type reactive
var count = 0.obs;              // RxInt
var name = ''.obs;              // RxString
var isLoading = false.obs;      // RxBool
var items = <String>[].obs;     // RxList<String>
var user = User.empty().obs;    // Rx<User>

// Access
count.value;           // Get value
count.value = 5;       // Set value
count++;               // Shorthand for count.value++

// Listen to changes
count.listen((value) {
  print('Count changed to $value');
});

// RxList — reactive list
items.add('item');     // Auto-updates UI
items.removeAt(0);
items.value = ['new'];
```

---

## Q3: What is the difference between Obx, GetBuilder, and GetX?

```dart
// Obx — reactive, auto-detects .obs dependencies
Obx(() => Text('${controller.count.value}'))
// Rebuilds when any .obs used inside changes

// GetBuilder — manual update, more performant
class CounterController extends GetxController {
  int count = 0;  // Plain int, not .obs

  void increment() {
    count++;
    update();  // Manual notify
  }
}

GetBuilder<CounterController>(
  builder: (controller) => Text('${controller.count}'),
)
// Rebuilds only when update() is called

// GetX — combines controller injection + builder
GetX<CounterController>(
  init: CounterController(),  // Inject
  builder: (controller) => Text('${controller.count}'),
)
```

| Widget | Reactive | Performance | Use Case |
|--------|----------|-------------|----------|
| `Obx` | ✅ `.obs` | Medium | Reactive state |
| `GetBuilder` | ❌ `update()` | High | Simple state, less rebuilds |
| `GetX` | ❌ `update()` | High | Inject + build in one |

---

## Q4: How do you handle navigation with GetX?

```dart
// GetMaterialApp instead of MaterialApp
GetMaterialApp(
  initialRoute: '/',
  getPages: [
    GetPage(name: '/', page: () => const HomeScreen()),
    GetPage(name: '/detail/:id', page: () => const DetailScreen()),
    GetPage(name: '/profile', page: () => const ProfileScreen()),
  ],
)

// Navigation — no context needed
Get.toNamed('/profile');                    // Push
Get.toNamed('/detail/42');                  // Push with param
Get.back();                                 // Pop
Get.offNamed('/login');                      // Push + remove current (no back)
Get.offAllNamed('/home');                    // Clear stack, push new
Get.until((route) => Get.currentRoute == '/');  // Pop until route

// Pass arguments
Get.toNamed('/detail', arguments: {'id': 42, 'name': 'Alice'});

// Read arguments
final args = Get.arguments;
final id = args['id'];  // 42

// Read path params
final id = Get.parameters['id'];  // /detail/42 → '42'

// Bottom sheet — no context
Get.bottomSheet(
  Container(child: Text('Bottom Sheet')),
);

// Dialog — no context
Get.dialog(AlertDialog(title: Text('Hello')));

// Snackbar — no context
Get.snackbar('Title', 'Message', snackPosition: SnackPosition.BOTTOM);
```

---

## Q5: How do you use dependency injection with GetX?

```dart
// Register instances
Get.put(ApiClient());                    // Singleton (immediate)
Get.lazyPut(() => Database());           // Lazy (created on first find)
Get.putAsync<Config>(() async {          // Async singleton
  return Config.load();
});

// Get instance
final api = Get.find<ApiClient>();
final db = Get.find<Database>();

// Check if registered
Get.isRegistered<ApiClient>();

// Remove instance
Get.delete<ApiClient>();
Get.deleteAll();  // Remove all

// Permanent (not removed on route change)
Get.put(AuthService(), permanent: true);

// Scoped with fenix (recreate if deleted)
Get.lazyPut(() => Controller(), fenix: true);

// Real example
void main() {
  Get.put<ApiClient>(ApiClient());
  Get.lazyPut<UserRepository>(() => UserRepository(Get.find<ApiClient>()));
  Get.lazyPut<AuthController>(() => AuthController(Get.find<UserRepository>()));

  runApp(const GetMaterialApp(home: HomeScreen()));
}

// Usage in controller
class AuthController extends GetxController {
  final UserRepository _repo;
  AuthController(this._repo);  // Injected via GetX

  var user = Rx<User?>(null);

  Future<void> login(String email, String password) async {
    user.value = await _repo.login(email, password);
  }
}
```

---

## Q6: How do you handle forms and validation with GetX?

```dart
class LoginController extends GetxController {
  final email = ''.obs;
  final password = ''.obs;
  final isLoading = false.obs;
  final error = ''.obs;

  void setEmail(String value) => email.value = value;
  void setPassword(String value) => password.value = value;

  String? validateEmail(String? value) {
    if (value == null || value.isEmpty) return 'Required';
    if (!value.contains('@')) return 'Invalid email';
    return null;
  }

  String? validatePassword(String? value) {
    if (value == null || value.isEmpty) return 'Required';
    if (value.length < 6) return 'Min 6 characters';
    return null;
  }

  Future<void> login() async {
    isLoading.value = true;
    error.value = '';
    try {
      final user = await Get.find<UserRepository>().login(email.value, password.value);
      Get.offAllNamed('/home');
    } catch (e) {
      error.value = e.toString();
    } finally {
      isLoading.value = false;
    }
  }
}

// UI
class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final controller = Get.put(LoginController());
    final formKey = GlobalKey<FormState>();

    return Form(
      key: formKey,
      child: Column(
        children: [
          TextFormField(
            onChanged: controller.setEmail,
            validator: controller.validateEmail,
          ),
          TextFormField(
            onChanged: controller.setPassword,
            validator: controller.validatePassword,
            obscureText: true,
          ),
          Obx(() => controller.isLoading.value
              ? const CircularProgressIndicator()
              : ElevatedButton(
                  onPressed: () {
                    if (formKey.currentState!.validate()) {
                      controller.login();
                    }
                  },
                  child: const Text('Login'),
                )),
          Obx(() => controller.error.value.isNotEmpty
              ? Text(controller.error.value, style: const TextStyle(color: Colors.red))
              : const SizedBox()),
        ],
      ),
    );
  }
}
```

---

## Q7: How do you handle internationalization with GetX?

```dart
// Translations
class AppTranslations extends Translations {
  @override
  Map<String, Map<String, String>> get keys => {
    'en_US': {
      'hello': 'Hello',
      'login': 'Login',
      'welcome': 'Welcome, @name',
    },
    'es_ES': {
      'hello': 'Hola',
      'login': 'Iniciar sesión',
      'welcome': 'Bienvenido, @name',
    },
    'ja_JP': {
      'hello': 'こんにちは',
      'login': 'ログイン',
      'welcome': 'ようこそ、@name',
    },
  };
}

// Setup
GetMaterialApp(
  translations: AppTranslations(),
  locale: const Locale('en', 'US'),
  fallbackLocale: const Locale('en', 'US'),
  home: const HomeScreen(),
)

// Usage
Text('hello'.tr);  // "Hello"
Text('welcome'.trParams({'name': 'Alice'}));  // "Welcome, Alice"

// Change locale
var locale = const Locale('en', 'US').obs;
Get.updateLocale(const Locale('es', 'ES'));
```

---

## Q8: What are the pros and cons of GetX?

### Pros
- ✅ Very low boilerplate
- ✅ No `BuildContext` needed for navigation/DI
- ✅ All-in-one (state, routes, DI, i18n, themes)
- ✅ Great for rapid prototyping
- ✅ Simple learning curve

### Cons
- ❌ Not compile-safe (runtime errors)
- ❌ Global state (hard to test in isolation)
- ❌ Anti-pattern: hides Flutter fundamentals
- ❌ Not recommended by Flutter team
- ❌ Harder to migrate away from
- ❌ Mixes concerns (state + navigation + DI)

```dart
// ❌ GetX hides context — beginners don't learn Flutter fundamentals
Get.toNamed('/home');  // Where does this go? No context

// ✅ Standard Flutter — explicit, learnable
Navigator.pushNamed(context, '/home');  // Clear what's happening
```

> **Recommendation:** GetX is great for quick prototypes and small apps. For production apps, prefer Riverpod or BLoC for better testability and architecture.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Riverpod](Riverpod.md)
- [Comparison](Comparison.md)
