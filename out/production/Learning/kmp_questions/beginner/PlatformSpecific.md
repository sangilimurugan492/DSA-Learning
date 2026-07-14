# KMP Platform-Specific Code — Interview Questions

## 🔴 Q1: How do you access Android-specific APIs from KMP?
**Answer:** In `androidMain`, you have full access to Android APIs:

```kotlin
// androidMain
import android.content.Context
import android.util.Log
import android.content.SharedPreferences

actual class PlatformInfo(private val context: Context) {
    actual fun getDeviceModel(): String = android.os.Build.MODEL
    actual fun getOsVersion(): String = android.os.Build.VERSION.RELEASE
}

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}
```

---

## 🔴 Q2: How do you access iOS-specific APIs from KMP?
**Answer:** In `iosMain`, use Kotlin/Native's interop with Objective-C/Swift frameworks:

```kotlin
// iosMain
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.UIKit.UIDevice
import platform.Foundation.NSUserDefaults

actual class PlatformInfo {
    actual fun getDeviceModel(): String = UIDevice.currentDevice.model
    actual fun getOsVersion(): String = UIDevice.currentDevice.systemVersion
}

actual fun log(tag: String, message: String) {
    println("[$tag] $message")
}
```

---

## 🟡 Q3: How do you call Swift code from KMP?
**Answer:** KMP can't directly call Swift — it interops with Objective-C. You need a Swift → Obj-C bridge:

```swift
// Swift code
@objc public class MySwiftClass: NSObject {
    @objc public static func doSomething() -> String {
        return "Hello from Swift"
    }
}
```

```kotlin
// iosMain
import platform.MyModule.MySwiftClass

fun callSwift() {
    val result = MySwiftClass.doSomething()
}
```

> **Note:** The Swift class must be `@objc` compatible and exposed via a framework.

---

## 🟡 Q4: How do you call KMP code from Swift?
**Answer:** KMP compiles to a framework that Swift can import:

```kotlin
// commonMain
class Greeting {
    fun hello(): String = "Hello from KMP!"
}
```

```swift
// Swift
import Shared

let greeting = Greeting()
print(greeting.hello())
```

**Kotlin → Swift naming conversions:**
- `getX()` → `x` (property)
- `setX(value)` → `x = value`
- `isX()` → `x` (Bool property)
- `MyClass` → `MyClass` (same)
- `myFunction()` → `myFunction()` (same)

---

## 🔴 Q5: How do you handle threading on iOS in KMP?
**Answer:** With the new memory model, you can use coroutines freely. But for UI updates, dispatch to main thread:

```kotlin
// commonMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ViewModel {
    suspend fun loadData(): Data {
        return withContext(Dispatchers.Default) {
            // Background work
            api.fetchData()
        }
    }
}
```

```swift
// Swift — call from main thread
Task {
    let data = await viewModel.loadData()
    await MainActor.run {
        self.updateUI(data)
    }
}
```

---

## 🟡 Q6: How do you use iOS frameworks (UIKit, Foundation, etc.) in KMP?
**Answer:** Use `platform.` prefix to access system frameworks:

```kotlin
// iosMain
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.UIKit.UIView
import platform.CoreGraphics.CGRect

fun createView(): UIView {
    return UIView(CGRect(0.0, 0.0, 100.0, 100.0))
}
```

---

## 🟡 Q7: How do you use third-party iOS libraries (CocoaPods) in KMP?
**Answer:** Use the `cocoapods` plugin:

```kotlin
kotlin {
    cocoapods {
        pod("Alamofire") {
            version = "5.6.0"
        }
        pod("SDWebImage") {
            version = "5.12.0"
        }
    }
}
```

Then in `iosMain`:
```kotlin
import platform.Alamofire.AF
import platform.Alamofire.Session

fun makeRequest(url: String) {
    AF.session.get(url)
}
```

---

## 🟡 Q8: How do you handle different screen sizes / device info?
**Answer:**

```kotlin
// commonMain
expect class DeviceInfo {
    val isTablet: Boolean
    val screenWidth: Int
    val screenHeight: Int
    val density: Float
}

// androidMain
actual class DeviceInfo {
    actual val isTablet: Boolean
        get() = (context.resources.configuration.screenLayout and 
            Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    actual val screenWidth: Int
        get() = context.resources.displayMetrics.widthPixels
    // ...
}

// iosMain
actual class DeviceInfo {
    actual val isTablet: Boolean
        get() = UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad
    actual val screenWidth: Int
        get() = UIScreen.mainScreen.bounds.size.width.toInt()
    // ...
}
```

---

## 🟡 Q9: How do you handle permissions in KMP?
**Answer:** Define a common interface, implement per platform:

```kotlin
// commonMain
interface PermissionManager {
    suspend fun requestPermission(permission: Permission): PermissionResult
    fun isGranted(permission: Permission): Boolean
}

enum class Permission { CAMERA, LOCATION, NOTIFICATIONS }
enum class PermissionResult { GRANTED, DENIED, SHOW_RATIONALE }

// androidMain — uses ActivityCompat.requestPermissions
// iosMain — uses AVCaptureDevice.requestAccess, CLLocationManager, etc.
```

---

## 🟡 Q10: How do you share platform-specific UI code?
**Answer:** Without Compose Multiplatform, UI is platform-specific. With Compose Multiplatform, you can share UI:

```kotlin
// commonMain (with Compose Multiplatform)
@Composable
fun SharedButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text)
    }
}
```

Without Compose MP, use `expect`/`actual` for UI-related logic only (view models, state), and build UI natively per platform.

---

## 📌 Key Takeaways
- `androidMain` has full Android API access, `iosMain` uses `platform.*` imports
- Swift interop requires `@objc` bridge (KMP → Obj-C → Swift)
- KMP framework is directly importable in Swift
- Use `cocoapods` plugin for third-party iOS libraries
- UI is platform-specific unless using Compose Multiplatform

---

[← Expect/Actual](ExpectActual.md) | [Back to README](../README.md) | [Next: Intermediate →](../intermediate/Architecture.md)
