# Platform Channels

## Q1: What are platform channels?

Platform channels enable communication between Dart and native platform code (Android/Kotlin, iOS/Swift).

```
Flutter (Dart)
    ↕ MethodChannel (JSON messages)
Android (Kotlin/Java)  /  iOS (Swift/Obj-C)
```

```dart
// Dart side — call native method
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
print('Battery: $level%');
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
            val level = getBatteryLevel()
            if (level != -1) {
              result.success(level)
            } else {
              result.error("UNAVAILABLE", "Battery not available", null)
            }
          }
          else -> result.notImplemented()
        }
      }
  }

  private fun getBatteryLevel(): Int {
    val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
  }
}
```

```swift
// iOS (AppDelegate.swift)
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    let controller = window?.rootViewController as! FlutterViewController
    let channel = FlutterMethodChannel(
      name: "com.example.app/battery",
      binaryMessenger: controller.binaryMessenger
    )

    channel.setMethodCallHandler { call, result in
      if call.method == "getBatteryLevel" {
        UIDevice.current.isBatteryMonitoringEnabled = true
        let level = Int(UIDevice.current.batteryLevel * 100)
        result(level)
      } else {
        result(FlutterMethodNotImplemented)
      }
    }

    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
```

---

## Q2: What is the difference between MethodChannel and EventChannel?

| Channel Type | Direction | Use Case |
|-------------|-----------|----------|
| `MethodChannel` | Dart → Native → Dart | One-time calls (get battery) |
| `EventChannel` | Native → Dart (stream) | Continuous events (sensor, location) |
| `BasicMessageChannel` | Bidirectional | Custom message format |

```dart
// MethodChannel — request/response
final result = await methodChannel.invokeMethod('methodName', args);

// EventChannel — stream of events from native
EventChannel('com.example.app/sensor')
  .receiveBroadcastStream()
  .listen((event) {
    print('Sensor value: $event');
  });
```

### EventChannel Example
```dart
// Dart side
class SensorService {
  static const _channel = EventChannel('com.example.app/sensor');

  static Stream<double> get accelerometer {
    return _channel.receiveBroadcastStream()
      .map((event) => event as double);
  }
}

// Usage
SensorService.accelerometer.listen((value) {
  print('Accelerometer: $value');
});
```

```kotlin
// Android side — EventChannel
EventChannel(flutterEngine.dartExecutor.binaryMessenger, "com.example.app/sensor")
  .setStreamHandler(object : EventChannel.StreamHandler {
    private var sensorManager: SensorManager? = null
    private var listener: SensorEventListener? = null

    override fun onListen(args: Any?, events: EventSink) {
      sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
      val sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
      listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
          events.success(event.values[0])  // Send to Dart
        }
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
      }
      sensorManager!!.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onCancel(args: Any?) {
      sensorManager?.unregisterListener(listener)
    }
  })
```

---

## Q3: How do you pass data through platform channels?

```dart
// Dart → Native with arguments
final result = await channel.invokeMethod('calculate', {
  'operation': 'add',
  'values': [1, 2, 3],
  'precision': 2,
});

// Native → Dart result
// Result must be serializable: int, double, String, bool, List, Map, null
```

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

```dart
// ❌ Can't pass custom objects directly
await channel.invokeMethod('save', User(name: 'Alice'));
// Error — User is not serializable

// ✅ Convert to Map first
await channel.invokeMethod('save', {
  'name': 'Alice',
  'age': 30,
});

// ✅ Receive complex data
final Map<String, dynamic> data = await channel.invokeMethod('getData');
final name = data['name'] as String;
final items = data['items'] as List;
```

---

## Q4: What is `Pigeon` and why use it?

```dart
// Pigeon — type-safe platform channel code generator
// pubspec.yaml: pigeon: ^22.0.0

// 1. Define interface (pigeons/api.dart)
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

@HostApi()  // Dart calls native
abstract class UserApi {
  UserResponse getUser(UserRequest request);
  void deleteUser(int id);
}

// 2. Generate: dart run pigeon --input pigeons/api.dart
// Generates: UserApi in Dart, Kotlin, Swift

// 3. Dart side — call generated API
final api = UserApi();
final user = await api.getUser(UserRequest(1));

// 4. Native side — implement generated interface
class UserApiImpl : UserApi {
  override fun getUser(request: UserRequest): UserResponse {
    return UserResponse(request.id, "Alice", "alice@test.com")
  }
}
```

### Pigeon vs Manual Channels
| Manual Channels | Pigeon |
|----------------|--------|
| String method names | Type-safe API |
| No compile-time checks | Compile-time safety |
| Manual serialization | Auto-generated |
| Error-prone | Less boilerplate |
| Simple for 1-2 methods | Better for many methods |

---

## Q5: What is FFI (Foreign Function Interface)?

```dart
// FFI — call C/C++ libraries directly (no platform channels)
// pubspec.yaml: ffi: ^2.1.0

import 'dart:ffi';
import 'dart:io';

// 1. Load shared library
final dylib = Platform.isAndroid
    ? DynamicLibrary.open('libnative.so')
    : DynamicLibrary.process();

// 2. Bind C function
typedef NativeAdd = Int32 Function(Int32 a, Int32 b);
typedef DartAdd = int Function(int a, int b);

final addPointer = dylib.lookupFunction<NativeAdd, DartAdd>('add');
final result = addPointer(3, 4);  // 7

// 3. Struct binding
final struct = structRef.ref;
struct.x = 10.0;
struct.y = 20.0;
```

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

## Q6: How do you create a Flutter plugin?

```bash
# Create plugin package
flutter create --template=plugin --platforms=android,ios my_plugin
```

```
my_plugin/
├── lib/
│   └── my_plugin.dart        ← Dart API
├── android/
│   └── src/main/kotlin/.../MyPlugin.kt  ← Android implementation
├── ios/
│   └── Classes/MyPlugin.swift           ← iOS implementation
└── pubspec.yaml
```

```dart
// lib/my_plugin.dart — public API
class MyPlugin {
  static const _channel = MethodChannel('my_plugin');

  static Future<String?> getPlatformVersion() async {
    return _channel.invokeMethod<String>('getPlatformVersion');
  }
}

// android/.../MyPlugin.kt
class MyPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(binding.binaryMessenger, "my_plugin")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
```

---

## Q7: How do you handle platform channel errors?

```dart
// Dart side — catch platform errors
try {
  final result = await channel.invokeMethod('riskyOperation');
} on PlatformException catch (e) {
  print('Code: ${e.code}');      // "PERMISSION_DENIED"
  print('Message: ${e.message}'); // "Camera permission not granted"
  print('Details: ${e.details}');  // Additional data
} on MissingPluginException {
  print('Method not implemented on this platform');
} catch (e) {
  print('Unexpected: $e');
}
```

```kotlin
// Android — return error
result.error("PERMISSION_DENIED", "Camera permission not granted", null)
result.error("INVALID_ARGS", "Expected non-null id", {"expected": "int"})
```

```swift
// iOS — return error
result(FlutterError(code: "PERMISSION_DENIED",
  message: "Camera permission not granted",
  details: nil))
```

### Platform Check
```dart
if (Platform.isAndroid) {
  // Android-specific channel call
} else if (Platform.isIOS) {
  // iOS-specific channel call
} else if (kIsWeb) {
  // Web — no platform channels
}
```

---

## 🔗 Related Topics
- [Flutter Internals](FlutterInternals.md)
- [Architecture Patterns](ArchitecturePatterns.md)
- [Performance](Performance.md)
