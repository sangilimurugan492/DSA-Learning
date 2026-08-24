# Platform Channels

## 📖 Explanation

Platform channels enable communication between Dart and native platform code (Android/Kotlin, iOS/Swift). They bridge Flutter to platform-specific APIs that aren't available through Dart packages.

### How Platform Channels Work
```
Flutter (Dart)
    ↕ MethodChannel (JSON messages)
Android (Kotlin/Java)  /  iOS (Swift/Obj-C)
```

### Channel Types
| Channel Type | Direction | Use Case |
|-------------|-----------|----------|
| `MethodChannel` | Dart → Native → Dart | One-time calls (get battery) |
| `EventChannel` | Native → Dart (stream) | Continuous events (sensor, location) |
| `BasicMessageChannel` | Bidirectional | Custom message format |

### Supported Types
| Dart | Android (Kotlin) | iOS (Swift) |
|------|-------------------|-------------|
| `null` | `null` | `nil` |
| `bool` | `Boolean` | `NSNumber(bool)` |
| `int` | `Int` / `Long` | `NSNumber(int)` |
| `double` | `Double` | `NSNumber(double)` |
| `String` | `String` | `String` |
| `List` | `List` | `Array` |
| `Map` | `HashMap` | `Dictionary` |

### Pigeon vs Manual Channels
| Manual Channels | Pigeon |
|----------------|--------|
| String method names | Type-safe API |
| No compile-time checks | Compile-time safety |
| Manual serialization | Auto-generated |
| Error-prone | Less boilerplate |

### FFI vs Platform Channels
| FFI | Platform Channels |
|-----|-------------------|
| Direct C/C++ call | Through platform SDK |
| Synchronous | Asynchronous |
| No platform code needed | Need Kotlin/Swift |
| Ultra-fast | Message serialization overhead |
| C/Rust libraries | Platform APIs (camera, sensors) |

> **Use FFI** for: math libraries, crypto, image processing, existing C/C++ code
> **Use Platform Channels** for: platform APIs (camera, Bluetooth, notifications)

---

## 🧪 Code Example

```dart
// ── MethodChannel — get battery level ──
import 'package:flutter/services.dart';

class BatteryService {
  static const _channel = MethodChannel('com.example.app/battery');

  static Future<int> getBatteryLevel() async {
    final level = await _channel.invokeMethod<int>('getBatteryLevel');
    return level ?? -1;
  }
}

// Usage
final level = await BatteryService.getBatteryLevel();
```

```kotlin
// Android (MainActivity.kt)
class MainActivity: FlutterActivity() {
  private val CHANNEL = "com.example.app/battery"

  override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)
    MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
      .setMethodCallHandler { call, result ->
        when (call.method) {
          "getBatteryLevel" -> {
            val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
            val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if (level != -1) result.success(level)
            else result.error("UNAVAILABLE", "Battery not available", null)
          }
          else -> result.notImplemented()
        }
      }
  }
}
```

```dart
// ── EventChannel — sensor stream ──
class SensorService {
  static const _channel = EventChannel('com.example.app/sensor');

  static Stream<double> get accelerometer {
    return _channel.receiveBroadcastStream()
      .map((event) => event as double);
  }
}

SensorService.accelerometer.listen((value) {
  print('Accelerometer: $value');
});
```

```dart
// ── Pigeon — type-safe platform channels ──
// pigeons/api.dart
import 'package:pigeon/pigeon.dart';

class UserRequest {
  final int id;
  UserRequest(this.id);
}

class UserResponse {
  final int id;
  final String name;
  final String email;
  UserResponse(this.id, this.name, this.email);
}

@HostApi()
abstract class UserApi {
  UserResponse getUser(UserRequest request);
  void deleteUser(int id);
}

// Generate: dart run pigeon --input pigeons/api.dart
// Generates type-safe code in Dart, Kotlin, Swift

// ── FFI — call C library ──
import 'dart:ffi';

final dylib = Platform.isAndroid
    ? DynamicLibrary.open('libnative.so')
    : DynamicLibrary.process();

typedef NativeAdd = Int32 Function(Int32 a, Int32 b);
typedef DartAdd = int Function(int a, int b);

final addPointer = dylib.lookupFunction<NativeAdd, DartAdd>('add');
final result = addPointer(3, 4);  // 7

// ── Error handling ──
try {
  final result = await channel.invokeMethod('riskyOperation');
} on PlatformException catch (e) {
  print('Code: ${e.code}');      // "PERMISSION_DENIED"
  print('Message: ${e.message}'); // "Camera permission not granted"
} on MissingPluginException {
  print('Method not implemented on this platform');
}

// Platform check
if (Platform.isAndroid) {
  // Android-specific
} else if (Platform.isIOS) {
  // iOS-specific
} else if (kIsWeb) {
  // Web — no platform channels
}
```

### Output
```
A Flutter app using platform channels:
- MethodChannel for one-time native calls (battery level)
- EventChannel for continuous native streams (sensors)
- Pigeon for type-safe platform channel code generation
- FFI for direct C/C++ library calls
- Platform-specific error handling with PlatformException
```

---

## ❓ Interview Questions

1. **What are platform channels?**
   - Platform channels enable communication between Dart and native platform code (Android/Kotlin, iOS/Swift). They use JSON message serialization over a channel. `MethodChannel` for request/response (Dart calls native method, gets result back). `EventChannel` for continuous streams from native to Dart (sensors, location). `BasicMessageChannel` for bidirectional custom messages. Use when you need platform APIs not available in Dart packages (battery, Bluetooth, notifications). Data must be serializable types (int, double, String, bool, List, Map, null) — no custom objects. For complex data, convert to Map first.

2. **What is the difference between MethodChannel and EventChannel?**
   - `MethodChannel` is request/response — Dart calls `invokeMethod('name', args)` and awaits a single result. Use for one-time calls (get battery level, take photo). `EventChannel` is a stream — native sends continuous events to Dart via `receiveBroadcastStream().listen()`. Use for continuous data (accelerometer, location updates, connectivity changes). `EventChannel` uses `StreamHandler` on native side with `onListen` (start sending) and `onCancel` (stop sending) callbacks. Both use the same JSON serialization. For bidirectional communication, use `BasicMessageChannel`.

3. **How do you pass data through platform channels?**
   - Pass data as arguments: `channel.invokeMethod('calculate', {'operation': 'add', 'values': [1, 2, 3]})`. Only serializable types are supported: null, bool, int, double, String, List, Map. Custom objects must be converted to Map first — `{'name': 'Alice', 'age': 30}`. Receive complex data as `Map<String, dynamic>`: `final data = await channel.invokeMethod('getData') as Map<String, dynamic>`. On native side, Kotlin receives `HashMap`, Swift receives `Dictionary`. For type safety and less boilerplate, use Pigeon which generates type-safe APIs.

4. **What is `Pigeon` and why use it?**
   - Pigeon is a code generator for type-safe platform channels. Define interfaces in Dart with `@HostApi()` annotation and data classes. Run `dart run pigeon --input pigeons/api.dart` to generate matching Kotlin and Swift code. Benefits: (1) Compile-time type safety — no string method names or manual casting. (2) Auto-generated serialization — no manual Map conversion. (3) Less boilerplate — no `when (call.method)` switch. (4) IDE autocomplete for native implementations. Use Pigeon when you have many platform channel methods or complex data types. For 1-2 simple methods, manual channels are fine.

5. **What is FFI (Foreign Function Interface)?**
   - FFI (`dart:ffi`) allows Dart to call C/C++ libraries directly without platform channels. Load shared library: `DynamicLibrary.open('libnative.so')`. Bind C function: `dylib.lookupFunction<NativeType, DartType>('functionName')`. Call directly — synchronous and ultra-fast. FFI is for C/C++/Rust libraries (math, crypto, image processing, game engines). Platform channels are for platform SDK APIs (camera, Bluetooth, notifications). FFI is synchronous (can block UI if the C function is slow — run in isolate). FFI doesn't need Kotlin/Swift code. FFI is faster than platform channels (no JSON serialization overhead).

6. **How do you create a Flutter plugin?**
   - Create with `flutter create --template=plugin --platforms=android,ios my_plugin`. Structure: `lib/my_plugin.dart` (Dart API), `android/.../MyPlugin.kt` (Android implementation), `ios/Classes/MyPlugin.swift` (iOS implementation). The Dart API uses `MethodChannel` to call native code. The native plugin implements `FlutterPlugin` and `MethodCallHandler`. Register in `pubspec.yaml`. Publish to pub.dev with `flutter pub publish`. Plugins can also use Pigeon for type-safe communication. For platform-specific implementations, use `Platform.isAndroid`/`Platform.isIOS` in Dart and provide fallbacks for web/desktop.

7. **How do you handle platform channel errors?**
   - On Dart side, catch `PlatformException` (has `code`, `message`, `details`) for native errors and `MissingPluginException` for unimplemented methods. On native side, return errors: Android — `result.error("PERMISSION_DENIED", "message", details)`, iOS — `result(FlutterError(code: "PERMISSION_DENIED", message: "message", details: nil))`. Always check platform: `Platform.isAndroid` / `Platform.isIOS` / `kIsWeb` (web has no platform channels). Wrap channel calls in try-catch and provide fallback behavior. Define error codes as constants for consistency. Test error paths — permission denied, method not implemented, native crash.

8. **When would you use FFI vs Platform Channels?**
   - Use **FFI** when: calling C/C++/Rust libraries (math, crypto, image processing, game engines), you need maximum performance (no serialization overhead), you have existing C code to reuse, you need synchronous calls. Use **Platform Channels** when: accessing platform-specific APIs (camera, Bluetooth, notifications, sensors), you need platform SDK features, you need async operations, you're writing a plugin for pub.dev. FFI is synchronous and fast but limited to C ABI. Platform channels are async with serialization overhead but can access any platform API. Some packages use both (FFI for computation, channels for platform integration).

9. **How do you test platform channels?**
   - Mock the `MethodChannel` in tests: `methodChannel.setMockMethodCallHandler((call) async { if (call.method == 'getBatteryLevel') return 80; })`. Test the Dart service that wraps the channel. For widget tests, use `MethodChannel.setMockMethodCallHandler` before pumping the widget. For EventChannel, mock with `setMockStreamHandler`. Integration tests on a real device/emulator test the actual native code. For Pigeon-generated APIs, the generated code includes test utilities. Always test error cases: `PlatformException`, `MissingPluginException`. Use `kIsWeb` to skip channel tests on web.

10. **What are the limitations of platform channels?**
    - (1) Data must be serializable (int, double, String, bool, List, Map, null) — no custom objects, no binary data (use `BasicMessageChannel.BinaryCodec` for binary). (2) Asynchronous — all calls go through the platform thread, adding latency. (3) No compile-time safety (manual channels) — method names are strings, typos cause runtime errors. (4) Platform-specific — need separate implementations for Android and iOS. (5) No web support — platform channels don't work on web (use JS interop instead). (6) Debugging is harder — errors cross language boundaries. Solutions: Pigeon for type safety, FFI for performance, conditional imports for web, comprehensive error handling.

---

## 🔗 Related Topics
- [Flutter Internals](FlutterInternals.md)
- [Architecture Patterns](ArchitecturePatterns.md)
- [Performance](Performance.md)
