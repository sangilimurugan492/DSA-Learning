# UI Layouts & Views

## 📖 Explanation

Android UIs are built using layouts (ViewGroups) and views (widgets). Layouts define how child views are arranged on screen.

### Common Layouts
| Layout         | Description                                          |
|----------------|------------------------------------------------------|
| `LinearLayout`  | Arranges children in a single direction (horizontal/vertical) |
| `ConstraintLayout` | Positions children using constraints (most flexible) |
| `RelativeLayout` | Positions children relative to each other or parent  |
| `FrameLayout`   | Stacks children (single child typically)            |
| `TableLayout`   | Arranges children in rows and columns                |
| `GridLayout`    | Arranges children in a grid                           |

### Common Views
| View            | Description                          |
|-----------------|--------------------------------------|
| `TextView`      | Displays text                       |
| `EditText`      | Text input field                    |
| `Button`        | Clickable button                    |
| `ImageView`     | Displays an image                   |
| `RecyclerView`  | Scrollable list (efficient)         |
| `CheckBox`      | Toggle checkbox                     |
| `RadioButton`   | Radio selection                     |
| `ProgressBar`   | Loading indicator                   |
| `Spinner`       | Dropdown selector                   |

### `match_parent` vs `wrap_content`
- `match_parent` — Fill the parent's size.
- `wrap_content` — Size to fit content.

### Common Attributes
| Attribute           | Description                          |
|---------------------|--------------------------------------|
| `android:id`         | Unique view ID                      |
| `android:layout_width` | Width of the view                  |
| `android:layout_height` | Height of the view               |
| `android:padding`    | Inner spacing                       |
| `android:layout_margin` | Outer spacing                    |
| `android:gravity`    | Alignment of content within view    |
| `android:layout_gravity` | Alignment of view within parent |
| `android:visibility` | visible, invisible, gone            |

### ConstraintLayout
The most powerful and recommended layout. Uses constraints to position views.

```xml
<Button
    android:id="@+id/button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent" />
```

### `include` & `merge`
Reuse layouts with `<include>`. Use `<merge>` to eliminate redundant view groups.

```xml
<include layout="@layout/common_header" />
```

---

## 🧪 Code Example

```xml
<!-- activity_main.xml — ConstraintLayout example -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <!-- Title -->
    <TextView
        android:id="@+id/titleText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Login"
        android:textSize="28sp"
        android:textStyle="bold"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Email input -->
    <EditText
        android:id="@+id/emailInput"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Email"
        android:inputType="textEmailAddress"
        android:layout_marginTop="24dp"
        app:layout_constraintTop_toBottomOf="@id/titleText"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Password input -->
    <EditText
        android:id="@+id/passwordInput"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Password"
        android:inputType="textPassword"
        android:layout_marginTop="12dp"
        app:layout_constraintTop_toBottomOf="@id/emailInput"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Login button -->
    <Button
        android:id="@+id/loginButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Login"
        android:layout_marginTop="24dp"
        app:layout_constraintTop_toBottomOf="@id/passwordInput"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Loading indicator (hidden by default) -->
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
// MainActivity.kt — Wiring up the views
package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false

            // Simulate login
            loginButton.postDelayed({
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            }, 2000)
        }
    }
}
```

---

## ❓ Interview Questions

1. **What is the difference between `LinearLayout` and `ConstraintLayout`?**
   - `LinearLayout` arranges children in a single direction (horizontal/vertical) — can cause nested layout hierarchies. `ConstraintLayout` uses constraints to position views flat — reduces nesting and improves performance.

2. **What is the difference between `padding` and `margin`?**
   - `padding` is space inside the view (between content and border). `margin` is space outside the view (between this view and others).

3. **What is the difference between `gravity` and `layout_gravity`?**
   - `gravity` aligns content within the view itself. `layout_gravity` aligns the view within its parent.

4. **What is the difference between `invisible` and `gone`?**
   - `invisible` — view is hidden but still takes up space. `gone` — view is hidden and takes no space.

5. **Why is `ConstraintLayout` recommended over nested `LinearLayout`?**
   - It flattens the view hierarchy, reducing layout passes and improving performance. Nested layouts cause multiple measure/layout passes. ConstraintLayout does everything in one pass.

---

## 🔗 Related Topics
- [RecyclerView & Adapter Patterns](RecyclerView.md)
- [Android Basics](Basics.md)
