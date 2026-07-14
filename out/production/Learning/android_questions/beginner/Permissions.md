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
   - Normal permissions are auto-granted at install (e.g., `INTERNET`). Dangerous permissions require explicit user consent at runtime (e.g., `CAMERA`, `LOCATION`) because they access sensitive data.

2. **How do you request runtime permissions in modern Android?**
   - Use `registerForActivityResult(ActivityResultContracts.RequestPermission())` — it's lifecycle-safe and avoids the deprecated `onRequestPermissionsResult` callback.

3. **What is `shouldShowRequestPermissionRationale` and when do you use it?**
   - It returns `true` if the user denied a permission before (but didn't check "Don't ask again"). Use it to show an explanation before re-requesting. If it returns `false` after a denial, the user selected "Don't ask again" — direct them to app settings.

4. **What changed with storage permissions in Android 13 (API 33)?**
   - `READ_EXTERNAL_STORAGE` is deprecated. Replaced by granular permissions: `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO`. Users can grant access to specific media types.

5. **What happens if you declare a permission in the manifest but don't request it at runtime?**
   - For normal permissions, it's auto-granted. For dangerous permissions, the app will crash or fail silently when trying to use the protected feature — you must request at runtime.

---

## 🔗 Related Topics
- [Android Basics](Basics.md)
- [Intents & Intent Filters](Intents.md)
