# RecyclerView & Adapter Patterns

## 📖 Explanation

`RecyclerView` is the most efficient way to display large scrollable lists. It **recycles** (reuses) view holders that scroll off-screen, avoiding the cost of creating new views.

### Key Components
| Component          | Description                                          |
|--------------------|------------------------------------------------------|
| `RecyclerView`     | The scrollable container                             |
| `Adapter`          | Binds data to view holders                           |
| `ViewHolder`       | Holds references to views for one item               |
| `LayoutManager`    | Determines how items are arranged (linear, grid)     |
| `ItemDecoration`   | Adds dividers, spacing                               |
| `ItemAnimator`     | Animates add/remove/change operations                |

### LayoutManagers
| Manager              | Layout                          |
|----------------------|---------------------------------|
| `LinearLayoutManager` | Vertical or horizontal list    |
| `GridLayoutManager`   | Grid                            |
| `StaggeredGridLayoutManager` | Staggered grid          |

### ViewHolder Pattern
Each item view is wrapped in a `ViewHolder` that caches view references, avoiding repeated `findViewById()` calls.

### DiffUtil
Efficiently calculates differences between two lists and dispatches only the necessary updates (insert, remove, move, change).

### Multiple View Types
Override `getItemViewType()` to display different layouts for different items in the same list.

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// --- Data model ---
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// --- ViewHolder ---
class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val nameText: TextView = view.findViewById(R.id.userName)
    private val emailText: TextView = view.findViewById(R.id.userEmail)

    fun bind(user: User) {
        nameText.text = user.name
        emailText.text = user.email
    }
}

// --- Adapter with DiffUtil (ListAdapter) ---
class UserAdapter : ListAdapter<User, UserViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(old: User, new: User): Boolean =
                old.id == new.id

            override fun areContentsTheSame(old: User, new: User): Boolean =
                old == new
        }
    }
}

// --- Activity ---
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = UserAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set initial data
        val users = listOf(
            User(1, "Alice", "alice@example.com"),
            User(2, "Bob", "bob@example.com"),
            User(3, "Charlie", "charlie@example.com"),
            User(4, "Diana", "diana@example.com"),
            User(5, "Eve", "eve@example.com")
        )
        adapter.submitList(users)

        // Update data (DiffUtil handles the diff)
        findViewById<View>(R.id.addButton).setOnClickListener {
            val newList = adapter.currentList + User(6, "Frank", "frank@example.com")
            adapter.submitList(newList)
        }
    }
}
```

```xml
<!-- item_user.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/userName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="18sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/userEmail"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="14sp"
        android:textColor="#666666" />
</LinearLayout>
```

```xml
<!-- activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <Button
        android:id="@+id/addButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Add User" />
</LinearLayout>
```

### Click Listener (Extension Pattern)
```kotlin
// Add click handling to adapter
class UserAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, UserViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.itemView.setOnClickListener { onItemClick(user) }
    }
}

// Usage
adapter = UserAdapter { user ->
    Toast.makeText(this, "Clicked: ${user.name}", Toast.LENGTH_SHORT).show()
}
```

---

## ❓ Interview Questions

1. **Why is `RecyclerView` better than `ListView`?**
   - RecyclerView enforces the ViewHolder pattern (mandatory), supports multiple layout types, custom animations, DiffUtil for efficient updates, and flexible layout managers (linear, grid, staggered).

2. **What is the ViewHolder pattern and why is it important?**
   - ViewHolder caches view references so `findViewById()` is called only once per view (in `onCreateViewHolder`), not on every `onBindViewHolder`. This dramatically improves scroll performance.

3. **What is `DiffUtil` and how does it work?**
   - DiffUtil compares two lists using `areItemsTheSame` (same ID) and `areContentsTheSame` (same data). It dispatches only the minimal set of insert/remove/move/change operations, avoiding full `notifyDataSetChanged()`.

4. **What is the difference between `ListAdapter` and `RecyclerView.Adapter`?**
   - `ListAdapter` is a built-in subclass that handles DiffUtil automatically. You call `submitList()` instead of manually managing the list and calling `notifyItemXxx()`.

5. **How do you handle multiple view types in a RecyclerView?**
   - Override `getItemViewType(position)` to return a type integer. In `onCreateViewHolder`, inflate different layouts based on `viewType`. Use sealed classes or enums to model item types.

---

## 🔗 Related Topics
- [UI Layouts & Views](UILayouts.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
