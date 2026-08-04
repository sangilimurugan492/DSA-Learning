# Permissions & Runtime Permissions

## 📖 Explanation

Android permissions protect user privacy and system resources. Starting from Android 6.0 (API 23), **dangerous permissions** must be requested at runtime, not just declared in the manifest.

### Permission Categories
| Category         | Description                                    | Example                          |
|-----------------|------------------------------------------------|----------------------------------|
| Normal           | Auto-granted at install (no prompt)           | `INTERNET`, `VIBRATE`           |
| Dangerous        | Require runtime user consent                  | `CAMERA`, `LOCATION`, `CONTACTS`|
| Signature        | Granted only if signed with same key           | `ACCESS_COARSE_LOCATION` (system)|
| Special          | Require settings screen navigation            | `SYSTEM_ALERT_WINDOW`, `WRITE_SETTINGS` |

### Dangerous Permission Groups
| Group             | Permissions                                    |
|-------------------|------------------------------------------------|
| `CALENDAR`        | `READ_CALENDAR`, `WRITE_CALENDAR`             |
| `CAMERA`          | `CAMERA`                                       |
| `CONTACTS`        | `READ_CONTACTS`, `WRITE_CONTACTS`             |
| `LOCATION`        | `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` |
| `MICROPHONE`      | `RECORD_AUDIO`                                 |
| `PHONE`           | `READ_PHONE_STATE`, `CALL_PHONE`              |
| `STORAGE`         | `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` |

### Runtime Permission Flow
1. Check if permission is already granted (`checkSelfPermission`).
2. If not, request it (`requestPermissions` / `ActivityResultContracts`).
3. Handle the result (granted or denied).
4. Show rationale if the user denied previously (`shouldShowRequestPermissionRationale`).

### Manifest Declaration
All permissions (normal and dangerous) must be declared in the manifest.

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

### Modern Approach: `ActivityResultContracts`
Use `RequestPermission` contract for a clean, lifecycle-safe API.

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    // Register permission request launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            statusText.text = "Camera permission granted!"
            openCamera()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                statusText.text = "Permission denied. Please grant camera access in settings."
            } else {
                statusText.text = "Permission permanently denied. Enable in app settings."
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        val cameraButton = findViewById<Button>(R.id.btnCamera)

        cameraButton.setOnClickListener {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            statusText.text = "Camera permission already granted"
            openCamera()
        } else {
            // Launch permission request
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera() {
        Toast.makeText(this, "Opening camera...", Toast.LENGTH_SHORT).show()
        // Actual camera intent would go here
    }
}
```

### Requesting Multiple Permissions
```kotlin
private val multiPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val allGranted = permissions.all { it.value }
    if (allGranted) {
        Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
    } else {
        val denied = permissions.filter { !it.value }.keys
        Toast.makeText(this, "Denied: $denied", Toast.LENGTH_SHORT).show()
    }
}

// Usage
multiPermissionLauncher.launch(
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
)
```

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- For Android 13+ (API 33), use granular media permissions -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

```xml
<!-- activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp">

    <TextView
        android:id="@+id/statusText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="No permission requested yet"
        android:textSize="16sp"
        android:layout_marginBottom="24dp" />

    <Button
        android:id="@+id/btnCamera"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Request Camera Permission" />
</LinearLayout>
```

---

## ❓ Interview Questions

1. **What is the difference between normal and dangerous permissions?**
   - **Normal permissions** are auto-granted at install time — they don't access sensitive user data (e.g., `INTERNET`, `VIBRATE`, `SET_WALLPAPER`). The system grants them without user interaction. **Dangerous permissions** require explicit user consent at runtime (Android 6.0+) because they access sensitive data like contacts, location, camera, or microphone (e.g., `CAMERA`, `ACCESS_FINE_LOCATION`, `READ_CONTACTS`). Dangerous permissions are grouped into permission groups — granting one permission in a group may auto-grant others in the same group (though this behavior is not guaranteed and shouldn't be relied upon). Always check `ContextCompat.checkSelfPermission()` before using a dangerous permission, and request it if not granted.

2. **How do you request runtime permissions in modern Android?**
   - Use `registerForActivityResult(ActivityResultContracts.RequestPermission())` — it's lifecycle-safe and avoids the deprecated `onRequestPermissionsResult` callback. The modern flow: (1) Register a launcher at field initialization or in `onCreate`: `val launcher = registerForActivityResult(RequestPermission()) { granted -> ... }`. (2) Check if permission is already granted with `ContextCompat.checkSelfPermission(context, permission) == GRANTED`. (3) If not granted, call `launcher.launch(Manifest.permission.CAMERA)`. (4) Handle the result in the callback — `granted` is a boolean. For multiple permissions, use `RequestMultiplePermissions()` which returns a `Map<String, Boolean>`. This API is type-safe, lifecycle-aware (callback only fires when Activity is at least STARTED), and avoids memory leaks from the old `onRequestPermissionsResult` approach.

3. **What is `shouldShowRequestPermissionRationale` and when do you use it?**
   - `shouldShowRequestPermissionRationale(permission)` returns `true` if the user denied the permission previously but didn't select "Don't ask again". Use it to show an educational UI explaining why you need the permission before re-requesting. The flow: (1) First request — system shows dialog, `shouldShowRequestPermissionRationale` returns `false` (never asked before). (2) User denies — `shouldShowRequestPermissionRationale` returns `true` (show rationale, then re-request). (3) User denies with "Don't ask again" — `shouldShowRequestPermissionRationale` returns `false` and the system won't show the dialog again. In this case, guide the user to app settings: `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", packageName, null))`. Always show a rationale before the first denial to improve grant rates.

4. **What changed with storage permissions in Android 13 (API 33)?**
   - `READ_EXTERNAL_STORAGE` is deprecated. Replaced by granular media permissions: `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO`. Users can grant access to specific media types independently — e.g., grant images but not video. This gives users more control over their data. For accessing media files, use `MediaStore` with these granular permissions. For accessing non-media files (documents, PDFs), use the Storage Access Framework (SAF) with `ACTION_OPEN_DOCUMENT` — no permission needed. For app-specific files, use `getExternalFilesDir()` — no permission needed. The old `WRITE_EXTERNAL_STORAGE` permission no longer has any effect on Android 10+. On Android 11+, `MANAGE_EXTERNAL_STORAGE` is available for file managers but requires special Play Store approval.

5. **What happens if you declare a permission in the manifest but don't request it at runtime?**
   - For normal permissions, it's auto-granted at install — no runtime request needed. For dangerous permissions, the system will NOT prompt the user automatically. If your code tries to use the protected feature without the permission being granted, the app will either crash with a `SecurityException` (e.g., opening camera without `CAMERA` permission) or silently fail (e.g., location returns null). You must explicitly check and request dangerous permissions at runtime before using the feature. Best practice: create a permission helper that checks, requests, and handles all permission states (granted, denied, permanently denied) in a reusable way.

6. **What are permission groups and how do they work?**
   - Permission groups categorize related dangerous permissions. For example, the `CALENDAR` group contains `READ_CALENDAR` and `WRITE_CALENDAR`. The `LOCATION` group contains `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`. When a user grants one permission in a group, the system may auto-grant other permissions in the same group without showing another dialog. However, this behavior is not guaranteed — on Android 12+, the system asks for each permission separately. Never rely on group behavior — always request each permission you need. The group concept is mainly for UI purposes (showing a group-level rationale to the user).

7. **What is the difference between `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`?**
   - `ACCESS_FINE_LOCATION` provides precise location (GPS-based, ~3 meters accuracy). `ACCESS_COARSE_LOCATION` provides approximate location (WiFi/cell-based, ~city-level accuracy). On Android 12+, users can grant only coarse location even if you request fine. You should request both: `permissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)`. Handle the case where only coarse is granted — degrade gracefully (show approximate location on map). On Android 12+, the system shows a single dialog with options for "Precise" or "Approximate". Use `LocationManager` or `FusedLocationProviderClient` to get location. For background location (Android 10+), you must first get foreground location, then request `ACCESS_BACKGROUND_LOCATION` separately.

8. **What is `POST_NOTIFICATIONS` permission in Android 13?**
   - Starting from Android 13 (API 33), apps must request `POST_NOTIFICATIONS` permission before posting notifications. Before this, notifications were enabled by default. The flow: (1) Declare `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` in manifest. (2) Request at runtime with `RequestPermission()` contract. (3) If granted, notifications work normally. (4) If denied, notifications are silently dropped. Best practice: request this permission after the user has had a chance to understand the app's value — don't request on first launch. Targeting API 33+ without this permission means notifications won't show. For apps targeting < 33, the permission is auto-granted on install but users can revoke it from settings.

9. **How do you handle permissions for foreground services?**
   - Starting from Android 9 (API 28), foreground services require the `FOREGROUND_SERVICE` permission. Starting from Android 14 (API 34), you must declare a foreground service type: `camera`, `location`, `microphone`, `health`, `mediaPlayback`, `mediaProjection`, `phoneCall`, `connectedDevice`, `specialUse`. Declare in manifest: `<service android:name=".MyService" android:foregroundServiceType="camera" />`. Start with `startForeground(id, notification, type)`. The type must match the permissions you have — e.g., `camera` type requires `CAMERA` permission. This ensures the system knows what resources the service uses and can enforce restrictions (e.g., camera service is killed if camera permission is revoked).

10. **What is the difference between `requestPermissions` (deprecated) and `ActivityResultContracts`?**
    - `requestPermissions(activity, permissions, requestCode)` (deprecated) launched the system permission dialog and returned results via `onRequestPermissionsResult(requestCode, permissions, grantResults)`. Problems: (1) Used magic numbers for request codes — error-prone. (2) `onRequestPermissionsResult` could be called when the Activity was in a bad state. (3) Not type-safe. `ActivityResultContracts.RequestPermission()` (modern) uses a registered launcher: `registerForActivityResult(RequestPermission()) { granted -> ... }`. Benefits: (1) Type-safe — input is a String (permission), output is a Boolean. (2) Lifecycle-safe — callback only fires when Activity is at least STARTED. (3) No request codes needed. (4) Works with `RequestMultiplePermissions()` for batch requests. Always use the modern API for new code.

11. **What are special permissions and how do you request them?**
    - Special permissions are a category that requires navigating to system settings — they can't be requested with the normal permission dialog. Examples: `SYSTEM_ALERT_WINDOW` (draw over other apps), `WRITE_SETTINGS` (modify system settings), `MANAGE_EXTERNAL_STORAGE` (all files access). To request `SYSTEM_ALERT_WINDOW`: `Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))` with `startActivityForResult`. To request `MANAGE_EXTERNAL_STORAGE`: `Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)`. These require user to manually toggle a switch in settings. Always check with `Settings.canDrawOverOtherApps()` or `Environment.isExternalStorageManager()` before using. Use sparingly — Google Play has restrictions on apps using these permissions.

12. **How do you test permission flows in instrumented tests?**
    - Use `GrantPermissionRule` in Espresso tests to pre-grant permissions: `@get:Rule val permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)`. For testing denial flows, don't grant the permission and verify the app handles it gracefully. Use `UiAutomator` for testing the actual system permission dialog (Espresso can't interact with system dialogs). For testing the "permanently denied" state, use `adb shell pm revoke package permission` to revoke, then verify the app shows a settings redirect. Test all states: granted, denied, permanently denied, and "don't ask again". Use `ApplicationProvider.getApplicationContext()` to check permission state in tests. Always test both the happy path (granted) and edge cases (denied, revoked while app is running).

13. **What is `onUserLeaveHint` and how does it relate to permissions?**
    - `onUserLeaveHint()` is called when the user presses the Home button (user-initiated navigation away). It's the only callback that can distinguish between user-initiated backgrounding and system-initiated. It's related to permissions in the context of overlay permission (`SYSTEM_ALERT_WINDOW`): when an app with overlay permission starts an activity from the background, the system blocks it unless it was triggered by user action. `onUserLeaveHint` can set a flag indicating the user voluntarily left. This is used for features like picture-in-picture (PiP) — `enterPictureInPictureMode()` should only be called from user-initiated navigation.

14. **How do you handle `BLUETOOTH` permissions in Android 12+?**
    - Android 12 (API 31) introduced `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, and `BLUETOOTH_ADVERTISE` runtime permissions. The old `BLUETOOTH` and `BLUETOOTH_ADMIN` permissions are deprecated. You must request these at runtime: `requestPermissions(arrayOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT))`. For scanning, you can use `neverForLocation` attribute in the manifest if you don't derive location from Bluetooth: `<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />`. Always check with `checkSelfPermission()` before calling Bluetooth APIs — `BluetoothManager.adapter.name` requires `BLUETOOTH_CONNECT`.

15. **What is the difference between `ACCESS_BACKGROUND_LOCATION` and foreground location?**
    - `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` grant foreground location access — location is available only when the app is in the foreground or running a foreground service with `location` type. `ACCESS_BACKGROUND_LOCATION` (Android 10+) allows location access when the app is in the background (not visible). You CANNOT request background location directly — you must first request foreground location, then request background location separately. The system shows a separate dialog directing the user to settings. Google Play has strict policies: background location is only approved for apps that need it as a core feature (navigation, fitness, safety). Requesting it unnecessarily leads to Play Store rejection.

---

## 🔗 Related Topics
- [Android Basics](Basics.md)
- [Intents & Intent Filters](Intents.md)
