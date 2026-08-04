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
   - RecyclerView enforces the ViewHolder pattern (mandatory), supports multiple layout types, custom animations, DiffUtil for efficient updates, and flexible layout managers (linear, grid, staggered). ListView had several limitations: (1) ViewHolder pattern was optional — developers had to implement it manually, leading to poor scroll performance. (2) Only vertical scrolling was supported. (3) No built-in support for animations. (4) `notifyDataSetChanged()` was the only way to update data — no granular updates. (5) No built-in item decorations. RecyclerView solves all of these: mandatory ViewHolder, pluggable LayoutManager (linear, grid, staggered), ItemAnimator for add/remove/move animations, DiffUtil for efficient list diffing, ItemDecoration for dividers/spacing, and multiple view types in a single list.

2. **What is the ViewHolder pattern and why is it important?**
   - ViewHolder caches view references so `findViewById()` is called only once per view (in `onCreateViewHolder`), not on every `onBindViewHolder`. This dramatically improves scroll performance. `findViewById()` is expensive — it traverses the view hierarchy each time. Without ViewHolder, scrolling a list of 1000 items with 5 views each would call `findViewById()` 5000 times. With ViewHolder, it's called only ~10 times (the number of visible + recycled views). In RecyclerView, the ViewHolder is mandatory — `onCreateViewHolder` creates and returns a ViewHolder, and `onBindViewHolder` binds data to it. The RecyclerView pool reuses ViewHolders as they scroll off-screen, passing them to `onBindViewHolder` with new data.

3. **What is `DiffUtil` and how does it works?**
   - DiffUtil compares two lists using `areItemsTheSame` (same ID — determines if it's the same item) and `areContentsTheSame` (same data — determines if the item's content changed). It dispatches only the minimal set of insert/remove/move/change operations, avoiding full `notifyDataSetChanged()`. This means if you add one item to a list of 100, only one `notifyItemInserted` is called instead of rebinding all 100 items. DiffUtil uses Eugene W. Myers' difference algorithm (O(N) for most cases). For large lists, use `DiffUtil.calculateDiff()` on a background thread. `ListAdapter` wraps DiffUtil — you just provide a `DiffUtil.ItemCallback` and call `submitList()`. Always implement `areItemsTheSame` with a unique ID, not position. For move detection, override `areContentsTheSame` to return false when content changes.

4. **What is the difference between `ListAdapter` and `RecyclerView.Adapter`?**
   - `ListAdapter` is a built-in subclass that handles DiffUtil automatically. You call `submitList(newList)` instead of manually managing the list and calling `notifyItemXxx()`. `ListAdapter` runs DiffUtil on a background thread and dispatches updates on the main thread. It also handles list updates asynchronously — `submitList` is non-blocking. With `RecyclerView.Adapter`, you manage the list yourself: maintain a `mutableList`, call `notifyItemInserted(position)` / `notifyItemRemoved(position)` / `notifyItemChanged(position)` manually. This is error-prone. Use `ListAdapter` when you have a list that changes frequently. Use `RecyclerView.Adapter` when you need fine-grained control over notifications or have a static list. Note: `submitList` checks reference equality — if you pass the same list instance, it won't diff. Always create a new list: `adapter.submitList(currentList + newItem)`.

5. **How do you handle multiple view types in a RecyclerView?**
   - Override `getItemViewType(position)` to return a type integer based on the data at that position. In `onCreateViewHolder`, inflate different layouts based on `viewType`. Use sealed classes or enums to model item types. Example:
     ```kotlin
     sealed class ListItem {
         data class HeaderItem(val title: String) : ListItem()
         data class ContentItem(val text: String) : ListItem()
     }
     override fun getItemViewType(position: Int) = when (getItem(position)) {
         is HeaderItem -> TYPE_HEADER
         is ContentItem -> TYPE_CONTENT
     }
     ```
     In `onCreateViewHolder`, check `viewType` and inflate the appropriate layout. In `onBindViewHolder`, cast the ViewHolder to the specific type and bind. This pattern is common for feeds with mixed content (ads, posts, headers). Use `ConcatAdapter` (RecyclerView 1.2+) to combine multiple adapters without mixing view types in one adapter.

6. **What is the RecyclerView RecycledViewPool and how does it work?**
   - The `RecycledViewPool` stores ViewHolders that have been scrolled off-screen, making them available for reuse. When a view scrolls off, its ViewHolder is placed in the pool. When a new view needs to appear, the pool provides a recycled ViewHolder (matching view type) instead of creating a new one. The pool has a maximum size per view type (default 5). You can increase it with `recycledViewPool.setMaxRecycledViews(viewType, size)`. For nested RecyclerViews (e.g., horizontal lists inside a vertical list), share a single pool across all inner RecyclerViews with `innerRecyclerView.setRecycledViewPool(sharedPool)` to reduce memory and improve performance. The pool is per-RecyclerView by default but can be shared.

7. **How do you handle item click events in RecyclerView?**
   - Three approaches: (1) **Lambda in adapter constructor** — pass a click listener lambda to the adapter, call it in `onBindViewHolder` with `holder.itemView.setOnClickListener { onItemClick(item) }`. Simple but the lambda is re-set on every bind. (2) **Interface on ViewHolder** — define an interface, set the listener in `onCreateViewHolder` (called once), and use `adapterPosition`/`bindingAdapterPosition` to get the clicked item. More efficient. (3) **ViewHolder with its own callback** — the ViewHolder handles its own clicks and calls back to the adapter. Always use `bindingAdapterPosition` (not `adapterPosition` which is deprecated) to get the correct position, since the data may have changed between bind and click. Avoid setting click listeners in `onBindViewHolder` for performance — set them in the ViewHolder constructor.

8. **What is `ItemDecoration` and how do you use it?**
   - `ItemDecoration` adds visual decorations to RecyclerView items — dividers, spacing, headers, or custom drawings. Override `getItemOffsets()` to add spacing/padding around items, and `onDraw()` / `onDrawOver()` to draw custom graphics. For spacing between items:
     ```kotlin
     class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
         override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
             outRect.bottom = spacing
         }
     }
     ```
     Add with `recyclerView.addItemDecoration(SpacingItemDecoration(16))`. For dividers, use `DividerItemDecoration` (built-in). For sticky headers, override `onDrawOver` to draw the header at the top. ItemDecorations are applied in order — later ones draw on top.

9. **What is `ItemAnimator` and how does it work?**
   - `ItemAnimator` animates add, remove, move, and change operations in RecyclerView. The default is `DefaultItemAnimator` which provides slide-in/fade-out animations. When DiffUtil dispatches updates (insert/remove/move/change), the ItemAnimator animates them. You can customize by extending `SimpleItemAnimator` or implementing `RecyclerView.ItemAnimator`. For example, you can add custom enter/exit animations, or disable animations entirely with `recyclerView.itemAnimator = null`. Use `recyclerView.itemAnimator?.changeDuration = 0` to disable change animations (useful for preventing flicker when updating items). `supportsChangeAnimations` can be set to false to prevent the cross-fade animation on item updates.

10. **What is `ConcatAdapter` and when do you use it?**
    - `ConcatAdapter` (RecyclerView 1.2+) lets you combine multiple adapters into a single RecyclerView without merging data. Each adapter manages its own view types and data. Use cases: (1) A feed with a header section, content section, and footer — each is a separate adapter. (2) A list with a loading state at the bottom — use a separate adapter for the loading item. (3) Combining lists from different data sources. Usage: `recyclerView.adapter = ConcatAdapter(headerAdapter, contentAdapter, footerAdapter)`. Benefits: separation of concerns (each adapter handles one section), no mixing of view types in one adapter, and adapters can be reused across screens. You can configure view type isolation with `ConcatAdapter.Config` to prevent view type conflicts.

11. **How do you optimize RecyclerView for large lists?**
    - (1) Use `ListAdapter` with DiffUtil for efficient updates. (2) Set `setHasFixedSize(true)` if the RecyclerView's size doesn't change with content. (3) Use `setHasStableIds(true)` with `getItemId()` for stable item IDs — improves animation and scroll position restoration. (4) Use `RecyclerView.RecycledViewPool` for nested lists. (5) Prefetch items with `layoutManager.isItemPrefetchEnabled = true` (default on). (6) Use `setInitialPrefetchItemCount(N)` for outer RecyclerView in nested lists. (7) Avoid heavy work in `onBindViewHolder` — use Glide/Coil for image loading. (8) Use `DiffUtil` instead of `notifyDataSetChanged()`. (9) Set `recyclerView.setRecycledViewPool(pool)` and increase pool size for multiple view types. (10) Use `itemAnimator = null` if animations aren't needed.

12. **What is `RecyclerView.LayoutParams` and how does caching work?**
    - RecyclerView caches views at three levels: (1) **Scrap** — views detached but not removed, used during layout (immediate reuse). (2) **Cache** — recently removed views, kept for quick reuse without rebinding (default size 2, increase with `setItemViewCacheSize()`). (3) **RecycledViewPool** — views that can be reused across the RecyclerView, may need rebinding. When a view scrolls off, it goes to cache → pool. When a new view is needed, it's pulled from cache (no rebind) or pool (rebind). Understanding this helps optimize: if items are frequently going off and back on screen, increase cache size. If you have many view types, increase pool size. Use `getViewForPosition()` internally — RecyclerView handles this automatically.

13. **What is `SnapHelper` and how do you use it?**
    - `SnapHelper` is a utility that snaps RecyclerView items to a specific position after scrolling — like ViewPager behavior. `LinearSnapHelper` snaps the closest item to the center (gallery-like). `PagerSnapHelper` snaps one item at a time, filling the viewport (ViewPager-like). Usage: `val snapHelper = PagerSnapHelper(); snapHelper.attachToRecyclerView(recyclerView)`. You can create custom snapping by extending `SnapHelper` and overriding `calculateDistanceToFinalSnap()` and `findSnapView()`. Use `SnapHelper` for carousels, image galleries, and onboarding screens where you want page-like behavior without using ViewPager2.

14. **How do you implement swipe-to-delete and drag-to-reorder in RecyclerView?**
    - Use `ItemTouchHelper` with a `ItemTouchHelper.SimpleCallback`. For swipe-to-delete: `ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT` directions. For drag-to-reorder: `ItemTouchHelper.UP or ItemTouchHelper.DOWN`. Override `onMove()` (reorder logic) and `onSwiped()` (delete logic). Attach: `ItemTouchHelper(callback).attachToRecyclerView(recyclerView)`. For partial swipe with custom background (like Gmail archive), override `onChildDraw()` to draw a background icon/color behind the swiping view. Use `ItemTouchHelper.ACTION_STATE_IDLE`, `ACTION_STATE_SWIPE`, and `ACTION_STATE_DRAG` for state-specific behavior.

15. **What is `RecyclerView.Recycler` and how does view recycling work internally?**
    - `RecyclerView.Recycler` manages the internal view recycling pipeline. When a view scrolls off-screen, it goes through: (1) **Scrap** — temporarily detached views during layout (immediate reuse within same layout pass). (2) **mCachedViews** — recently scrapped views kept for quick reuse without rebinding (default size 2, configurable via `setItemViewCacheSize()`). (3) **RecycledViewPool** — views available for any RecyclerView with matching view type; may need rebinding. When `getViewForPosition()` is called, it checks cache first (no rebind needed), then the pool (rebind needed), then creates a new view if none available. Understanding this pipeline helps optimize: increase cache for back-and-forth scrolling, share pool for nested lists.

---

## 🔗 Related Topics
- [UI Layouts & Views](UILayouts.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
