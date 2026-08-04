# Intents & Intent Filters

## 📖 Explanation

An Intent is a messaging object used to request an action from another app component. Intents are the primary mechanism for navigation and communication between components.

### Types of Intents
| Type       | Description                                          |
|------------|------------------------------------------------------|
| Explicit   | Specifies the exact component class to start         |
| Implicit   | Declares an action; system finds a matching component |

### Explicit Intent
```kotlin
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("key", "value")
startActivity(intent)
```

### Implicit Intent
```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
startActivity(intent)
```

### Common Actions
| Action                    | Description                          |
|--------------------------|--------------------------------------|
| `ACTION_VIEW`             | View data (URL, contact, map)       |
| `ACTION_SEND`             | Share data (text, image)            |
| `ACTION_DIAL`             | Open dialer with number             |
| `ACTION_PICK`              | Pick an item (contact, image)      |
| `ACTION_MAIN`             | Start as main entry point           |

### Intent Extras
Pass data between components using key-value pairs.

```kotlin
// Sender
intent.putExtra("name", "Alice")
intent.putExtra("age", 30)

// Receiver
val name = intent.getStringExtra("name")
val age = intent.getIntExtra("age", 0)
```

### Intent Filters
Declared in `AndroidManifest.xml` to specify which implicit intents a component can handle.

```xml
<activity android:name=".ShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

### `startActivityForResult` (Modern: `registerForActivityResult`)
Get a result back from the started activity.

```kotlin
val launcher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val data = result.data?.getStringExtra("result")
    }
}
launcher.launch(intent)
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Explicit intent — navigate to SecondActivity
        findViewById<Button>(R.id.btnExplicit).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra("message", "Hello from MainActivity!")
                putExtra("count", 42)
            }
            startActivity(intent)
        }

        // Implicit intent — open URL
        findViewById<Button>(R.id.btnOpenUrl).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kotlinlang.org"))
            startActivity(intent)
        }

        // Implicit intent — share text
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out this awesome app!")
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        // Implicit intent — dial a number
        findViewById<Button>(R.id.btnDial).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234567890"))
            startActivity(intent)
        }

        // Get result back from another activity
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra("result")
                findViewById<TextView>(R.id.resultText).text = "Result: $data"
            }
        }

        findViewById<Button>(R.id.btnForResult).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}

// SecondActivity — receives and returns data
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Receive data from explicit intent
        val message = intent.getStringExtra("message") ?: "No message"
        val count = intent.getIntExtra("count", 0)
        findViewById<TextView>(R.id.receivedText).text = "$message (count: $count)"

        // Return result
        findViewById<Button>(R.id.btnReturn).setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("result", "Data from SecondActivity")
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
```

```xml
<!-- AndroidManifest.xml — Intent filter for receiving SEND -->
<activity android:name=".ShareActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

---

## ❓ Interview Questions

1. **What is the difference between explicit and implicit intents?**
   - **Explicit intents** specify the exact component class to start — `Intent(this, SecondActivity::class.java)`. The system directly launches that component without any resolution. Used for internal app navigation (e.g., navigating from MainActivity to SettingsActivity). **Implicit intents** declare a general action to perform (e.g., `ACTION_VIEW`, `ACTION_SEND`) and optionally a data URI/mime type. The system searches all apps' intent filters to find components that can handle the action. If multiple apps match, the user sees a chooser dialog. If no app matches, the app crashes with `ActivityNotFoundException` — always use `resolveActivity()` or `try/catch` to handle this. Use explicit intents for internal navigation, implicit intents for cross-app actions (sharing, opening URLs, dialing).

2. **What is an intent filter and where is it declared?**
   - An intent filter specifies which implicit intents a component (Activity, Service, Broadcast Receiver) can handle. Declared in `AndroidManifest.xml` inside the component tag with `<intent-filter>`. It contains three elements: `<action>` (the action string like `android.intent.action.SEND`), `<category>` (category like `DEFAULT` or `LAUNCHER`), and `<data>` (mime type, URI scheme, host). The system matches implicit intents against all registered filters. An activity with `android:exported="true"` and an intent filter can be started by other apps. For the launcher activity, use `ACTION_MAIN` + `category_LAUNCHER`. You can have multiple intent filters for one component (logical OR — any match triggers it).

3. **How do you pass data between activities?**
   - Use `putExtra(key, value)` on the Intent to add primitive types, strings, and Parcelable/Serializable objects. Retrieve with type-specific methods: `getStringExtra("key")`, `getIntExtra("key", defaultValue)`, `getParcelableExtra("key")`. For complex objects, implement `Parcelable` (preferred — faster, Android-specific) or `Serializable` (slower — uses reflection). For large data, use a shared ViewModel, database, or file instead of Intent extras (Bundle has a ~1MB limit). In modern Android, prefer `Parcelable` with the `@Parcelize` Kotlin extension which auto-generates the implementation: `@Parcelize data class User(val name: String) : Parcelable`.

4. **What is `registerForActivityResult` and why was it introduced?**
   - `registerForActivityResult` is the modern replacement for the deprecated `startActivityForResult()` + `onActivityResult()` pattern. It was introduced because the old approach had issues: (1) `onActivityResult` was called on the main thread even if the Activity was in a bad lifecycle state, causing crashes. (2) The request code-based routing was error-prone with magic numbers. (3) It wasn't type-safe. The new API uses `ActivityResultContracts` (predefined contracts like `StartActivityForResult`, `RequestPermission`, `TakePicture`, `PickVisualMedia`). You register a launcher in `onCreate` or at field initialization, then call `launcher.launch(input)`. The callback is lifecycle-safe — it's only invoked when the Activity is at least `STARTED`. It's also type-safe: the contract defines input and output types.

5. **What is `Intent.createChooser` and when do you use it?**
   - `Intent.createChooser(intent, title)` wraps an implicit intent in a chooser dialog that forces the user to select an app, even if one is set as the default. Without the chooser, if the user has set a default app for an action (e.g., always open links in Chrome), the system skips the selection dialog. `createChooser` ensures the user always sees options. It's commonly used with `ACTION_SEND` (sharing text/images) and `ACTION_VIEW` (opening URLs). The chooser also supports `Intent.EXTRA_INITIAL_INTENTS` to add additional targets and `Intent.EXTRA_EXCLUDE_COMPONENTS` to hide specific apps. Always use `createChooser` for sharing to give users full control.

6. **What are Intent flags and what are the most common ones?**
   - Intent flags control how the Activity is started and how the back stack is managed. Common flags: `FLAG_ACTIVITY_NEW_TASK` — start in a new task (required when starting from non-Activity context). `FLAG_ACTIVITY_CLEAR_TOP` — if the activity is already in the stack, clear all activities above it and reuse it (calls `onNewIntent`). `FLAG_ACTIVITY_SINGLE_TOP` — equivalent to `singleTop` launch mode. `FLAG_ACTIVITY_CLEAR_TASK` — clears the entire back stack before starting (used with `NEW_TASK`). `FLAG_ACTIVITY_NO_HISTORY` — the activity is not kept in the stack (removed after `onStop`). `FLAG_ACTIVITY_REORDER_TO_FRONT` — if the activity exists in the stack, bring it to front instead of creating a new instance. Combine flags with `or`: `intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK`.

7. **What is a Pending Intent and how is it different from a regular Intent?**
   - A `PendingIntent` wraps an Intent and grants another app (or the system) the permission to execute it on your behalf, using your app's identity and permissions. Unlike a regular Intent (executed immediately by your app), a PendingIntent is executed later by a different app. Common use cases: (1) Notifications — `NotificationCompat.Builder` requires a PendingIntent for tap action. (2) AlarmManager — schedule an intent to fire at a later time. (3) App Widgets — handle widget clicks. Create with `PendingIntent.getActivity(context, requestCode, intent, flags)`. The `requestCode` distinguishes different PendingIntents. Use `FLAG_IMMUTABLE` (required on API 23+) or `FLAG_MUTABLE` for intents that need to be updated. Always use `FLAG_IMMUTABLE` unless you specifically need to modify the intent extras.

8. **What is the difference between `startActivity` and `startActivityForResult`?**
   - `startActivity(intent)` launches a new Activity without expecting a result back — fire and forget. `startActivityForResult(intent, requestCode)` (deprecated) launches an Activity and expects a result via `onActivityResult(requestCode, resultCode, data)`. The modern equivalent is `registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> ... }`. The started Activity calls `setResult(RESULT_OK, intent)` and `finish()` to return data. `RESULT_OK` (success), `RESULT_CANCELED` (user pressed back), or `RESULT_FIRST_USER` (custom codes). Use this pattern for: picking contacts, capturing photos, selecting files, or any flow where you need data back from another Activity.

9. **What are deep links and app links?**
   - **Deep links** are URIs that open a specific screen in your app (e.g., `myapp://product/123`). Declared via intent filters with a custom scheme. Any app can trigger them. **App links** (Android 6.0+) are HTTP/HTTPS URLs that open your app directly without a chooser dialog — verified via a `assetlinks.json` file on your website. App links use `autoVerify="true"` in the intent filter. Benefits: no disambiguation dialog, better UX, and the link works in browser or app. Use `android:autoVerify="true"` and host a `.well-known/assetlinks.json` file with your app's package name and signing certificate fingerprint. App links improve user trust and are required for certain features like instant apps.

10. **How do you handle the case where no app can handle an implicit intent?**
    - If no app can handle an implicit intent, `startActivity(intent)` throws `ActivityNotFoundException` and crashes the app. To handle this safely: (1) Check before launching: `intent.resolveActivity(packageManager)` — returns the resolving component or null. (2) Wrap in try/catch: `try { startActivity(intent) } catch (e: ActivityNotFoundException) { /* show error */ }`. (3) Check `packageManager.queryIntentActivities(intent, 0)` for a list of matching apps. Always perform this check for implicit intents, especially on older Android versions or when the intent might not have a matching app (e.g., opening a specific file type).

11. **What is the difference between `ACTION_SEND` and `ACTION_SEND_MULTIPLE`?**
    - `ACTION_SEND` shares a single piece of data (text, image, file). Set the mime type and extras: `EXTRA_TEXT` for text, `EXTRA_STREAM` (Uri) for a single file. `ACTION_SEND_MULTIPLE` shares multiple items at once — use `EXTRA_STREAM` with an `ArrayList<Uri>` for multiple files. Both require the receiving app to have an intent filter for the corresponding mime type. For sharing files, use `FileProvider` to generate a content:// URI (not file://) — Android 7.0+ crashes on file:// URIs. Grant temporary read permission: `intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)`.

12. **What is `Intent.FLAG_GRANT_READ_URI_PERMISSION` and why is it needed?**
    - This flag grants the receiving app temporary read access to the URI in the intent. It's required when sharing content:// URIs (via FileProvider) because the receiving app doesn't have permission to read your app's files by default. Without this flag, the receiving app gets a `SecurityException` when trying to access the URI. Similarly, `FLAG_GRANT_WRITE_URI_PERMISSION` grants write access. These flags are essential for secure file sharing between apps. The permission is automatically revoked when the receiving app's task finishes. Always use this flag with `ACTION_SEND` and `ACTION_VIEW` when sharing files.

13. **What is `FileProvider` and why is it needed for sharing files?**
    - `FileProvider` is a special ContentProvider that generates secure `content://` URIs for your app's files. Before Android 7.0 (API 24), you could share `file://` URIs directly, but this caused `FileUriExposedException` on newer versions. `FileProvider` solves this by: (1) Defining a content URI that grants temporary permission to the receiving app. (2) Restricting access to specific directories you define in `res/xml/file_paths.xml`. (3) Working with `FLAG_GRANT_READ_URI_PERMISSION`. Setup: declare in manifest `<provider android:name="androidx.core.content.FileProvider" android:authorities="${applicationId}.fileprovider" android:exported="false" android:grantUriPermissions="true">`. Always use `FileProvider.getUriForFile(context, authority, file)`.

14. **What is the difference between `startService` and `startForegroundService`?**
    - `startService(intent)` starts a background service that the system may kill if under memory pressure. `startForegroundService(intent)` (API 26+) starts a service that the system treats as user-visible — the service MUST call `startForeground(id, notification)` within 5 seconds, otherwise the system throws an ANR and kills the service. Use `startForegroundService` for tasks the user should know about (music playback, downloads, location tracking). Starting from Android 14 (API 34), you must also specify a foreground service type (e.g., `mediaPlayback`, `location`, `camera`).

15. **What are `Intent` extras and what is the Bundle size limit?**
    - Intent extras are key-value pairs stored in a `Bundle` attached to the Intent. Use `putExtra(key, value)` and `getXxxExtra(key, default)`. The Bundle is serialized via Binder for inter-process communication, and the Binder transaction buffer has a ~1MB limit. This means total extras must be under ~1MB. Exceeding this throws `TransactionTooLargeException`. For large data, use: (1) A shared ViewModel (same Activity scope). (2) A database or DataStore. (3) A file with FileProvider. (4) A singleton with weak references. Never pass bitmaps or large lists through Intent extras.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [Android Basics](Basics.md)
