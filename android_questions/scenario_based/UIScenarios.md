# UI & RecyclerView Scenarios

## Scenario 1: Janky Scrolling in RecyclerView

### Problem
A RecyclerView with a large list scrolls with visible jank. Each item loads an image from a URL and does heavy computation in `onBindViewHolder`.

```kotlin
// ❌ Bad — heavy work in onBindViewHolder
class BadAdapter(private val items: List<Item>) : RecyclerView.Adapter<BadAdapter.VH>() {
    class VH(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        // ❌ Synchronous image load on main thread
        val bitmap = loadImageFromUrl(item.imageUrl)
        holder.binding.imageView.setImageBitmap(bitmap)

        // ❌ Heavy string formatting on every bind
        holder.binding.title.text = formatComplexHtml(item.description)

        // ❌ New listener allocation every bind
        holder.binding.root.setOnClickListener {
            startActivity(Intent(context, DetailActivity::class.java))
        }
    }
}
```

### Solution: Async loading, ViewType diffing, and listener reuse

```kotlin
// ✅ Good — async image loading, DiffUtil, payload-based updates
class GoodAdapter : RecyclerView.Adapter<GoodAdapter.VH>() {
    private val items = mutableListOf<Item>()
    private val onClick: (Item) -> Unit  // Set once in constructor

    class VH(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<Item>) {
        val diffResult = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize() = items.size
                override fun getNewListSize() = newItems.size
                override fun areItemsTheSame(old: Int, new: Int) =
                    items[old].id == newItems[new].id
                override fun areContentsTheSame(old: Int, new: Int) =
                    items[old] == newItems[new]
            }
        )
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        // ✅ Async image loading with Glide/Coil
        holder.binding.imageView.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            size(200, 200)  // Resize to target
        }

        // ✅ Pre-formatted text (format in ViewModel, not in bind)
        holder.binding.title.text = item.formattedDescription

        // ✅ Reuse listener — no allocation
        holder.binding.root.setOnClickListener { onClick(item) }
    }
}
```

### Key Takeaway
- Never do synchronous I/O or heavy computation in `onBindViewHolder`
- Use `DiffUtil` or `ListAdapter` to minimize rebinds
- Load images with Glide/Coil — they handle caching, downsscaling, and async
- Set listeners once or reuse a lambda reference — avoid allocation per bind
- Pre-format text in ViewModel, not in the adapter

---

## Scenario 2: RecyclerView Duplicates on Rotation

### Problem
After rotating the screen, the RecyclerView shows duplicate items appended to the original list.

```kotlin
// ❌ Bad — keeps adding to the list without clearing
class BadViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    init { loadMore() }

    fun loadMore() {
        viewModelScope.launch {
            val data = repo.fetch()
            // ❌ Appends to existing list — doubles on rotation
            _items.value = (_items.value ?: emptyList()) + data
        }
    }
}
```

### Solution: Replace list, don't append

```kotlin
// ✅ Good — ViewModel caches data, no re-fetch on rotation
class GoodViewModel(private val repo: Repository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    init { loadMore() }

    fun loadMore() {
        viewModelScope.launch {
            val data = repo.fetch()
            // ✅ Replace entire list — idempotent on re-creation
            _items.value = data
        }
    }
}

class GoodActivity : AppCompatActivity() {
    private val viewModel: GoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    adapter.submitList(items)  // DiffUtil handles updates
                }
            }
        }

        loadMoreButton.setOnClickListener { viewModel.loadMore() }
    }
}
```

### Key Takeaway
- ViewModel survives rotation — don't re-fetch on `onCreate`
- Replace lists rather than appending unless pagination is intentional
- `submitList` with DiffUtil only animates changed items
- Use `StateFlow` — it replays the last value to new collectors

---

## Scenario 3: Multiple ViewTypes in One RecyclerView

### Problem
A feed needs to display text posts, image posts, and sponsored ads in one RecyclerView. Using `instanceof` checks in a single ViewHolder is messy.

```kotlin
// ❌ Bad — God ViewHolder with type checks
class BadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: FeedItem) {
        when (item) {
            is TextPost -> { /* show text, hide image */ }
            is ImagePost -> { /* show image, hide text */ }
            is Ad -> { /* show ad */ }
        }
        // 200 lines of conditional logic...
    }
}
```

### Solution: Sealed class + multi-viewtype adapter

```kotlin
// ✅ Good — sealed class with distinct ViewHolders
sealed class FeedItem {
    abstract val id: String
    data class TextPost(override val id: String, val text: String) : FeedItem()
    data class ImagePost(override val id: String, val url: String, val caption: String) : FeedItem()
    data class Ad(override val id: String, val adContent: String) : FeedItem()
}

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_IMAGE = 2
        const val TYPE_AD = 3
    }

    private val items = mutableListOf<FeedItem>()

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is FeedItem.TextPost -> TYPE_TEXT
        is FeedItem.ImagePost -> TYPE_IMAGE
        is FeedItem.Ad -> TYPE_AD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TEXT -> TextVH(ItemTextBinding.inflate(inflater, parent, false))
            TYPE_IMAGE -> ImageVH(ItemImageBinding.inflate(inflater, parent, false))
            TYPE_AD -> AdVH(ItemAdBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FeedItem.TextPost -> (holder as TextVH).bind(item)
            is FeedItem.ImagePost -> (holder as ImageVH).bind(item)
            is FeedItem.Ad -> (holder as AdVH).bind(item)
        }
    }

    class TextVH(val b: ItemTextBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: FeedItem.TextPost) { b.textView.text = item.text }
    }
    class ImageVH(val b: ItemImageBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: FeedItem.ImagePost) {
            b.imageView.load(item.url)
            b.caption.text = item.caption
        }
    }
    class AdVH(val b: ItemAdBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: FeedItem.Ad) { b.adText.text = item.adContent }
    }
}
```

### Key Takeaway
- Use `sealed class` for type-safe, exhaustive `when` expressions
- Each view type gets its own ViewHolder — no conditional soup
- `getItemViewType` returns a constant; `onCreateViewHolder` inflates accordingly
- `ListAdapter` with `DiffUtil` works seamlessly with multi-viewtype

---

## Scenario 4: EditText Validation with Debounce

### Problem
User types in a search field. Making an API call on every keystroke causes excessive network requests and UI lag.

```kotlin
// ❌ Bad — API call on every keystroke
class BadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // ❌ Fires on every character — 10 chars = 10 API calls
                viewModel.search(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
```

### Solution: Kotlin Flow debounce

```kotlin
// ✅ Good — Flow debounce in ViewModel
class SearchViewModel(private val repo: Repository) : ViewModel() {
    private val _query = MutableStateFlow("")

    // Expose debounced + filtered results
    val results: StateFlow<List<SearchResult>> = _query
        .debounce(300)              // Wait 300ms after last keystroke
        .distinctUntilChanged()     // Skip if same as previous
        .filter { it.length >= 2 }  // Min 2 characters
        .flatMapLatest { query ->   // Cancel previous search if new query arrives
            flow {
                emit(Resource.Loading)
                try {
                    emit(Resource.Success(repo.search(query)))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Error"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading)

    fun onQueryChanged(query: String) {
        _query.value = query
    }
}

// In Activity
searchEditText.doOnTextChanged { text, _, _, _ ->
    viewModel.onQueryChanged(text.toString())
}
```

### Key Takeaway
- `debounce(300)` waits 300ms of silence before emitting — ideal for search-as-you-type
- `distinctUntilChanged` skips duplicate queries
- `flatMapLatest` cancels the previous search when a new query arrives
- `stateIn` with `WhileSubscribed` manages lifecycle automatically

---

## Scenario 5: RecyclerView Item Animation Glitch

### Problem
When updating a single item with `notifyDataSetChanged()`, the entire list flickers and all item animations replay.

```kotlin
// ❌ Bad — full rebind for a single item change
class BadActivity : AppCompatActivity() {
    fun onItemLiked(position: Int) {
        items[position].liked = !items[position].liked
        adapter.notifyDataSetChanged()  // ❌ Rebinds ALL items, kills animations
    }
}
```

### Solution: Targeted notify + payload updates

```kotlin
// ✅ Good — update only the changed item
class GoodActivity : AppCompatActivity() {
    fun onItemLiked(position: Int) {
        items[position].liked = !items[position].liked
        // ✅ Only rebinds the affected item, preserves animations
        adapter.notifyItemChanged(position, "like_toggle")
    }
}

// ✅ Even better — ListAdapter with DiffUtil handles it all
class FeedAdapter : ListAdapter<Item, FeedAdapter.VH>(DiffCallback) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    // ✅ Payload-based partial update — only update the like icon
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("like_toggle")) {
            holder.updateLikeIcon(getItem(position).liked)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
        override fun areContentsTheSame(old: Item, new: Item) = old == new
        override fun getChangePayload(old: Item, new: Item): Any? {
            return if (old.liked != new.liked) "like_toggle" else null
        }
    }
}
```

### Key Takeaway
- Never use `notifyDataSetChanged()` for a single item change
- `notifyItemChanged(position, payload)` enables partial binding
- `ListAdapter` with `DiffUtil.ItemCallback` automates diffing
- `getChangePayload` returns a key for partial updates — only update what changed
- Default item animator handles add/remove/change animations automatically

---

## Scenario 6: Nested RecyclerView Performance Issues

### Problem
A feed screen has a vertical RecyclerView with horizontal RecyclerViews inside each item (like Play Store). Scrolling is laggy, memory usage is high, and items lose their scroll position when recycled.

```kotlin
// ❌ Bad — each row creates its own RecyclerView with its own adapter and pool
class ParentAdapter(private val rows: List<Row>) : RecyclerView.Adapter<ParentAdapter.VH>() {
    class VH(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = rows[position]
        // ❌ New adapter every bind — no state preservation
        holder.binding.innerRecycler.adapter = ChildAdapter(row.items)
        // ❌ No shared pool — each inner RecyclerView has its own
        // ❌ No prefetching — inner items not prefetched
    }
}
```

### Solution: Shared RecycledViewPool, stable adapters, and prefetch

```kotlin
// ✅ Good — shared pool, stable adapter, prefetch
class ParentAdapter : ListAdapter<Row, ParentAdapter.VH>(RowDiffCallback) {
    private val sharedPool = RecyclerView.RecycledViewPool()

    class VH(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        // Pre-warm the pool for the expected view type
        sharedPool.setMaxRecycledViews(TYPE_CHILD, 20)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = getItem(position)
        // ✅ Reuse adapter — just submit new list
        if (holder.binding.innerRecycler.adapter == null) {
            holder.binding.innerRecycler.adapter = ChildAdapter()
            // ✅ Share the pool across all inner RecyclerViews
            holder.binding.innerRecycler.setRecycledViewPool(sharedPool)
            // ✅ Enable prefetch for inner items
            (holder.binding.innerRecycler.layoutManager as LinearLayoutManager).isItemPrefetchEnabled = true
            // ✅ Set prefetch count
            holder.binding.innerRecycler.layoutManager?.initialPrefetchItemCount = 4
        }
        (holder.binding.innerRecycler.adapter as ChildAdapter).submitList(row.items)

        // ✅ Save and restore scroll position
        holder.binding.innerRecycler.layoutManager?.onSaveInstanceState()?.let {
            savedScrollStates[row.id] = it
        }
        savedScrollStates[row.id]?.let {
            holder.binding.innerRecycler.layoutManager?.onRestoreInstanceState(it)
        }
    }

    companion object {
        private const val TYPE_CHILD = 1
        private val savedScrollStates = mutableMapOf<String, Parcelable>()
    }
}
```

### Key Takeaway
- Use `setRecycledViewPool()` to share a single pool across all inner RecyclerViews
- Reuse the adapter — call `submitList()` instead of creating a new adapter
- Enable `isItemPrefetchEnabled = true` and set `initialPrefetchItemCount`
- Save/restore scroll state per row ID using `onSaveInstanceState()`/`onRestoreInstanceState()`
- Use `setHasFixedSize(true)` on inner RecyclerViews if their size doesn't change
- Consider using `ConcatAdapter` for sections instead of nested RecyclerViews where possible

---

## Scenario 7: EditText Keyboard Pushing UI Up

### Problem
When the user taps an EditText at the bottom of a scrollable form, the keyboard appears and pushes the UI up, covering the submit button. The form is unusable.

```xml
<!-- ❌ Bad — no keyboard handling -->
<ScrollView>
    <LinearLayout>
        <EditText ... />
        <Button android:text="Submit" />  <!-- Hidden behind keyboard -->
    </LinearLayout>
</ScrollView>
```

### Solution: `windowSoftInputMode`, `imePadding`, and `WindowInsets`

```xml
<!-- ✅ Fix 1: Manifest — adjust resize -->
<activity android:windowSoftInputMode="adjustResize">
```

```kotlin
// ✅ Fix 2: Apply window insets as padding
ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
    val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
    val systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

    view.updatePadding(
        bottom = imeInsets.bottom.coerceAtLeast(systemBarsInsets.bottom)
    )
    WindowInsetsCompat.CONSUMED
}

// ✅ Fix 3: Scroll to focused view when keyboard appears
binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener {
    val focused = binding.root.findFocus() ?: return@addOnGlobalLayoutListener
    binding.scrollView.smoothScrollTo(0, focused.bottom)
}
```

```kotlin
// ✅ Fix 4: Use IME animation callback (API 30+) for smooth transitions
ViewCompat.setWindowInsetsAnimationCallback(binding.root,
    object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
        override fun onProgress(insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>) = insets

        override fun onStart(animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.Bounds): WindowInsetsAnimationCompat.Bounds {
            // Animate UI smoothly with keyboard
            bounds
        }
    })
```

### Key Takeaway
- Use `android:windowSoftInputMode="adjustResize"` in the manifest
- Handle `WindowInsetsCompat.Type.ime()` to apply bottom padding for the keyboard
- Scroll to the focused `EditText` using `smoothScrollTo`
- Use `WindowInsetsAnimationCompat` (API 30+) for smooth keyboard transitions
- In Compose, use `imePadding()` modifier and `windowInsets` parameter on `Scaffold`

---

## Scenario 8: Dark Mode Not Applying Correctly

### Problem
The app supports dark mode but some screens show white backgrounds, hardcoded colors appear wrong, and theme switches don't apply until Activity recreation.

```kotlin
// ❌ Bad — hardcoded colors, no theme awareness
binding.container.setBackgroundColor(Color.WHITE)
binding.title.setTextColor(Color.BLACK)

// ❌ Bad — colors not in themes
<TextView android:background="#FFFFFF" android:textColor="#000000" />
```

### Solution: Theme attributes, dynamic colors, and DayNight

```xml
<!-- ✅ Fix 1: Use DayNight theme in styles.xml -->
<style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">@color/colorPrimary</item>
</style>

<!-- ✅ Fix 2: Use theme attributes, not hardcoded colors -->
<TextView
    android:background="?attr/colorSurface"
    android:textColor="?attr/colorOnSurface" />

<!-- ✅ Fix 3: values-night/colors.xml for dark-specific overrides -->
<!-- values/colors.xml: <color name="background">#FFFFFF</color> -->
<!-- values-night/colors.xml: <color name="background">#121212</color> -->
```

```kotlin
// ✅ Fix 4: Force dark mode programmatically
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

// ✅ Fix 5: Check current mode
val isDark = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
    Configuration.UI_MODE_NIGHT_YES -> true
    else -> false
}

// ✅ Fix 6: Dynamic color (Android 12+)
val dynamicContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_DayNight)
} else this
```

```kotlin
// ✅ Fix 7: In Compose — use MaterialTheme with dynamic color
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

### Key Takeaway
- Use `Theme.Material3.DayNight` as the parent theme
- Never hardcode colors — use theme attributes (`?attr/colorSurface`, `?attr/colorOnSurface`)
- Provide `values-night/` resource overrides for dark mode-specific colors
- Use `AppCompatDelegate.setDefaultNightMode()` to force/restrict dark mode
- Use `dynamicDarkColorScheme()`/`dynamicLightColorScheme()` (Android 12+) for Material You
- In Compose, use `isSystemInDarkTheme()` and `dynamicColorScheme()` for automatic theming

---

## 🔗 Related Topics
- [RecyclerView Basics](../beginner/RecyclerView.md)
- [UI Layouts](../beginner/UILayouts.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
- [Performance Optimization](../advanced/Performance.md)
