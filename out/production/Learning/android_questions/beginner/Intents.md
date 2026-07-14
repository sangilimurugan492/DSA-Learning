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
   - Explicit intents specify the exact component class (`Intent(this, SecondActivity::class.java)`). Implicit intents declare an action and let the system find a matching component (`ACTION_VIEW` with a URL).

2. **What is an intent filter and where is it declared?**
   - An intent filter specifies which implicit intents a component can handle. Declared in `AndroidManifest.xml` with `<intent-filter>` containing action, category, and data.

3. **How do you pass data between activities?**
   - Use `putExtra()` on the intent. Retrieve with `getXxxExtra()` methods. For complex objects, use `Parcelable` or `Serializable`.

4. **What is `registerForActivityResult` and why was it introduced?**
   - It's the modern replacement for `startActivityForResult` + `onActivityResult`. It's lifecycle-safe, type-safe, and avoids memory leaks from delayed callbacks.

5. **What is `Intent.createChooser` and when do you use it?**
   - It creates a chooser dialog for implicit intents (like sharing). It ensures the user always picks an app, even if one is set as default. Used for `ACTION_SEND` and similar.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [Android Basics](Basics.md)
