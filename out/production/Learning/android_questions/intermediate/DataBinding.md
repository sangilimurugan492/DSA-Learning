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

6. **What is the difference between `@BindingAdapter` and `@InverseBindingAdapter`?**
   - `@BindingAdapter` defines how to set a value from the data model to the view (data → view). Example: `@BindingAdapter("imageUrl") fun loadImage(view: ImageView, url: String?)`. `@InverseBindingAdapter` defines how to read a value from the view back to the data model (view → data) — used in two-way binding. Example: `@InverseBindingAdapter(attribute = "android:text") fun getText(view: TextView): String = view.text.toString()`. For two-way binding, you also need a `@BindingAdapter` with an `InverseBindingListener` to notify when the view value changes. Most attributes (text, checked, progress) have built-in two-way binding. You only need custom `@InverseBindingAdapter` for custom attributes. Two-way binding: `@={}` — changes flow both directions.

7. **How do you use Data Binding with LiveData and ViewModel?**
   - (1) In XML, declare a `<variable name="viewModel" type="com.example.app.MyViewModel" />`. (2) Bind the ViewModel in the Activity: `binding.viewModel = viewModel; binding.lifecycleOwner = this`. (3) Use LiveData in XML: `android:text="@{viewModel.userName}"` — when `userName` LiveData changes, the TextView updates automatically. (4) For click events: `android:onClick="@{() -> viewModel.onLoginClicked()}"`. (5) The `lifecycleOwner` is required for LiveData to be lifecycle-aware in Data Binding — without it, LiveData won't update the UI. (6) For two-way binding with LiveData: `android:text="@={viewModel.email}"` — but LiveData must be `MutableLiveData` for this to work. In modern Android, prefer using StateFlow with `collectAsStateWithLifecycle()` in Compose instead of Data Binding.

8. **What are binding adapters for custom views and how do you create them?**
   - Binding adapters let you create custom XML attributes that map to custom logic. For custom views: (1) Define the attribute in the binding adapter: `@BindingAdapter("customAttribute") fun setCustomValue(view: MyCustomView, value: Int) { view.setValue(value) }`. (2) Use in XML: `<MyCustomView app:customAttribute="@{model.value}" />`. (3) Multiple attributes: `@BindingAdapter("attr1", "attr2") fun setMultiple(view: View, attr1: String, attr2: Int)`. (4) `requireAll = false` makes some attributes optional. (5) Old value: `@BindingAdapter(value = ["url", "placeholder"], requireAll = false) fun loadImage(view: ImageView, url: String?, placeholder: Drawable?)`. Binding adapters can be top-level functions or in an `object`. They're especially useful for Glide/Coil image loading, custom formatters, and conditional visibility.

9. **What is `BindingObject` and how do you use it for event handling?**
   - You can bind any object (not just ViewModel) to XML. For event handling, define a listener object: `class ClickHandlers { fun onLoginClick(view: View) { ... }; fun onTextChanged(s: CharSequence) { ... } }`. In XML: `<variable name="handlers" type="com.example.app.ClickHandlers" />` and `android:onClick="@{handlers::onLoginClick}"`. You can also use lambda expressions: `android:onClick="@{() -> handlers.onLoginClick()}"` or reference ViewModel methods: `android:onClick="@{() -> viewModel.login()}"`. For text watchers: `android:onTextChanged="@{handlers::onTextChanged}"`. This keeps the Activity/Fragment clean — no `setOnClickListener` boilerplate. Note: In Compose, event handling is simpler — just pass lambdas to composables.

10. **What are the performance implications of Data Binding vs View Binding?**
    - **View Binding**: (1) Faster compilation — no annotation processing. (2) No runtime overhead — generates a simple class with view references. (3) Smaller APK — less generated code. **Data Binding**: (1) Slower compilation — annotation processor generates mapping code. (2) Runtime overhead — reflection (older versions) or generated code for binding expressions. (3) Larger APK — generated binding classes for every layout. (4) `executePendingBindings()` on every RecyclerView bind adds minor overhead. **Recommendation**: Use View Binding for simple view access (most cases). Use Data Binding only when you need XML expressions, two-way binding, or BindingAdapters. In new projects, prefer Jetpack Compose which eliminates both View Binding and Data Binding — UI is declared in Kotlin with full type safety and no XML.

11. **How do you handle null safety in Data Binding expressions?**
    - Data Binding automatically handles nulls in expressions. (1) `android:text="@{user.name}"` — if `user` is null, the text is not set (no NPE). (2) `android:text="@{user.name ?? user.email}"` — null coalescing operator. (3) `android:visibility="@{user.name != null ? View.VISIBLE : View.GONE}"` — ternary operator. (4) `android:text="@{user.name.length()}"` — if `user` or `name` is null, the entire expression is skipped. (5) Import `View` class: `<import type="android.view.View" />` to use `View.VISIBLE` in expressions. (6) For lists: `android:text="@{list[0].name}"` — safe if list is empty. Data Binding uses generated null-checking code — every access is wrapped in null checks. This is safer than manual findViewById + null checks. However, complex expressions in XML are hard to debug — keep them simple.

12. **How do you migrate from Data Binding to Jetpack Compose?**
    - Gradual migration approach: (1) Start by replacing simple Data Binding layouts with Compose. (2) Use `ComposeView` in XML layouts to embed Compose in existing screens: `<androidx.compose.ui.platform.ComposeView android:id="@+id/composeView" />` then `binding.composeView.setContent { MyComposable() }`. (3) Use `AbstractComposeView` for custom views. (4) Replace `@BindingAdapter` with Compose extension functions or custom modifiers. (5) Replace two-way binding with Compose state: `var text by remember { mutableStateOf("") }`. (6) Replace LiveData observation with `observeAsState()` or `collectAsStateWithLifecycle()`. (7) Replace `BindingAdapter` for images with Coil's `AsyncImage`. (8) Eventually remove Data Binding entirely. Benefits of Compose: no XML, no annotation processing, full Kotlin type safety, easier testing, and better performance for complex UIs.

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [UI Layouts & Views](../beginner/UILayouts.md)
