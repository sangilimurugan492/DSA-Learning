# Data Binding & View Binding

## 📖 Explanation

Android provides two binding libraries to replace `findViewById()` and connect UI to data.

### View Binding
Type-safe view reference generation. No more `findViewById` or null casts.

```kotlin
// Enable in build.gradle
buildFeatures {
    viewBinding = true
}
```

```kotlin
// Generates a binding class (ActivityMainBinding)
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.titleText.text = "Hello!"
    binding.loginButton.setOnClickListener { /* ... */ }
}
```

### Data Binding
Binds UI directly to data sources in XML. Supports expressions, two-way binding, and binding adapters.

```kotlin
// Enable in build.gradle
buildFeatures {
    dataBinding = true
}
```

```xml
<!-- Wrap layout with <layout> tag -->
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="user"
            type="com.example.app.User" />
    </data>

    <TextView
        android:text="@{user.name}"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</layout>
```

### View Binding vs Data Binding
| Feature           | View Binding         | Data Binding               |
|-------------------|----------------------|----------------------------|
| Purpose           | Replace findViewById | Bind data to XML            |
| XML changes        | None                 | Wrap with `<layout>`        |
| Expressions        | No                   | Yes (`@{user.name}`)        |
| Two-way binding    | No                   | Yes (`@={user.name}`)       |
| Build speed        | Faster               | Slower (annotation process) |
| Recommended        | Simple view access   | Complex data-driven UI     |

### BindingAdapter
Custom binding logic for XML attributes.

```kotlin
@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    Glide.with(this).load(url).into(this)
}
```

```xml
<ImageView
    app:imageUrl="@{user.avatarUrl}" />
```

### Two-Way Binding
```xml
<EditText
    android:text="@={user.name}" />
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.ActivityMainBinding
import com.example.app.databinding.ItemUserBinding

// --- Data Model ---
data class User(
    val name: String,
    val email: String,
    val avatarUrl: String
)

// --- View Binding Example ---
class ViewBindingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Direct view access — no findViewById!
        binding.titleText.text = "Welcome"
        binding.emailInput.setText("user@example.com")

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            // Handle login
        }
    }
}

// --- Data Binding Example ---
class DataBindingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Bind data to XML
        val user = User("Alice", "alice@example.com", "https://example.com/avatar.jpg")
        binding.user = user
        binding.lifecycleOwner = this

        // Two-way binding — changes in EditText update user.name
        binding.saveButton.setOnClickListener {
            println("Saved: ${binding.user?.name}")
        }
    }
}

// --- BindingAdapter for loading images ---
@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) return
    // Glide.with(this).load(url).into(this)
    // Or use Coil
}

// --- BindingAdapter for visibility ---
@BindingAdapter("visibleIf")
fun View.setVisibleIf(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

// --- RecyclerView with Data Binding in adapter ---
class UserAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.user = users[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = users.size

    class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)
}
```

```xml
<!-- activity_main.xml (Data Binding) -->
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="user"
            type="com.example.app.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- One-way binding -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.email}"
            android:textSize="14sp" />

        <!-- Two-way binding -->
        <EditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@={user.name}"
            android:hint="Name" />

        <!-- Custom BindingAdapter -->
        <ImageView
            android:layout_width="80dp"
            android:layout_height="80dp"
            app:imageUrl="@{user.avatarUrl}" />

        <!-- Visibility binding -->
        <ProgressBar
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:visibleIf="@{user.name.empty}" />

        <Button
            android:id="@+id/saveButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Save" />
    </LinearLayout>
</layout>
```

```xml
<!-- item_user.xml (Data Binding in RecyclerView) -->
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="user"
            type="com.example.app.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{user.name}"
            android:textStyle="bold" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{user.email}"
            android:textColor="#666" />
    </LinearLayout>
</layout>
```

---

## ❓ Interview Questions

1. **What is the difference between View Binding and Data Binding?**
   - View Binding generates type-safe view references (replaces `findViewById`). Data Binding binds data directly in XML with expressions, supports two-way binding and BindingAdapters. View Binding is simpler and faster; Data Binding is more powerful.

2. **Why should you use View Binding instead of `findViewById`?**
   - Type-safe (no casts), null-safe (views exist at binding time), faster compilation than Data Binding, and no risk of wrong IDs.

3. **What is `@BindingAdapter` and when do you use it?**
   - It defines custom XML attributes that map to a function. Used for loading images, formatting text, setting visibility, etc. E.g., `app:imageUrl="@{url}"` calls a function to load the image.

4. **What is two-way data binding and how do you implement it?**
   - `@={}` syntax — changes in the UI update the data model and vice versa. E.g., `android:text="@={user.name}"` — typing in EditText updates `user.name`.

5. **How does Data Binding work with RecyclerView?**
   - In `onCreateViewHolder`, inflate the binding class. In `onBindViewHolder`, set the data variable and call `executePendingBindings()` to update the UI immediately during scroll.

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [UI Layouts & Views](../beginner/UILayouts.md)
