# Paging 3

## Q1: What is Paging 3 and why use it?

Paging 3 is a Jetpack library for loading and displaying paged data from large data sources efficiently.

### Why Paging 3?
| Feature | Description |
|---------|-------------|
| Memory efficient | Only loads visible items + small buffer |
| Infinite scrolling | Handles endless lists seamlessly |
| Configurable | Page size, prefetch distance, placeholders |
| Reactive | Built on Flow/LiveData |
| Error handling | Built-in retry mechanism |
| List diffing | Works with RecyclerView/Compose |

### Setup
```gradle
dependencies {
    implementation 'androidx.paging:paging-runtime-ktx:3.2.1'
    implementation 'androidx.paging:paging-compose:3.2.1'  // For Compose
}
```

### Basic flow
```
PagingSource → Pager → PagingData → PagingDataAdapter / LazyColumn
```

---

## Q2: How do you implement a PagingSource?

```kotlin
class UserPagingSource(
    private val api: UserApi
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: 1  // First page is 1
            val response = api.getUsers(
                page = page,
                pageSize = params.loadSize
            )

            LoadResult.Page(
                data = response.users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.users.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
```

### PagingSource key concepts
| Method | Purpose |
|--------|---------|
| `load()` | Fetch data for a page |
| `getRefreshKey()` | Which page to load on refresh |

### Key types
| Type | Description |
|------|-------------|
| `Key` | Page identifier (Int for page numbers, String for cursors) |
| `Value` | Item type (User, Article, etc.) |

---

## Q3: How do you create a Pager and PagingData?

```kotlin
class UserRepository(private val api: UserApi) {

    fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,           // Items per page
                prefetchDistance = 10,   // Load next page when 10 items from end
                enablePlaceholders = false,
                initialLoadSize = 40     // First load fetches 2x
            ),
            pagingSourceFactory = { UserPagingSource(api) }
        ).flow
    }
}
```

### PagingConfig parameters
| Parameter | Default | Description |
|-----------|---------|-------------|
| `pageSize` | — | Items per API call |
| `prefetchDistance` | `pageSize` | When to load next page |
| `enablePlaceholders` | `false` | Show null items while loading |
| `initialLoadSize` | `pageSize * 3` | First load size |
| `maxSize` | `UNDEFINED` | Max items in memory |

---

## Q4: How do you use Paging with RecyclerView?

```kotlin
class UserAdapter : PagingDataAdapter<User, UserViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)  // Returns null for placeholder
        holder.bind(user)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(old: User, new: User) = old.id == new.id
            override fun areContentsTheSame(old: User, new: User) = old == new
        }
    }
}

// In Activity/Fragment
class UserActivity : AppCompatActivity() {
    private val adapter = UserAdapter()
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... setup RecyclerView

        lifecycleScope.launch {
            viewModel.users.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}
```

---

## Q5: How do you use Paging with Compose?

```kotlin
@Composable
fun UserListScreen(viewModel: UserViewModel) {
    val users = viewModel.users.collectAsLazyPagingItems()

    LazyColumn {
        items(users) { user ->
            user?.let { UserItem(it) }
        }

        // Handle loading state
        users.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { CircularProgressIndicator() }
                }
                loadState.append is LoadState.Loading -> {
                    item { CircularProgressIndicator() }
                }
                loadState.refresh is LoadState.Error -> {
                    item {
                        ErrorItem(
                            message = "Failed to load",
                            onRetry = { users.retry() }
                        )
                    }
                }
            }
        }
    }
}
```

---

## Q6: How do you handle load states?

```kotlin
@Composable
fun UserListScreen(viewModel: UserViewModel) {
    val users = viewModel.users.collectAsLazyPagingItems()

    // Refresh state (initial load)
    when (users.loadState.refresh) {
        is LoadState.Loading -> LoadingScreen()
        is LoadState.Error -> ErrorScreen { users.retry() }
        is LoadState.NotLoading -> {
            if (users.itemCount == 0) {
                EmptyScreen()
            } else {
                LazyColumn {
                    items(users) { user ->
                        user?.let { UserItem(it) }
                    }

                    // Append state (pagination)
                    when (users.loadState.append) {
                        is LoadState.Loading -> item { LoadingFooter() }
                        is LoadState.Error -> item {
                            ErrorFooter { users.retry() }
                        }
                    }
                }
            }
        }
    }
}
```

### LoadState types
| State | Description |
|-------|-------------|
| `LoadState.Loading` | Data is being fetched |
| `LoadState.NotLoading` | Load completed (not necessarily success) |
| `LoadState.Error` | Load failed |
| `LoadState.NotLoading(endOfPaginationReached = true)` | No more data |

### LoadType
| Type | Description |
|------|-------------|
| `REFRESH` | Initial load or refresh |
| `APPEND` | Loading next page |
| `PREPEND` | Loading previous page (rare) |

---

## Q7: How do you handle errors and retries?

```kotlin
class UserPagingSource(
    private val api: UserApi
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: 1
            val response = api.getUsers(page, params.loadSize)

            LoadResult.Page(
                data = response.users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.users.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)  // Network error
        } catch (e: HttpException) {
            LoadResult.Error(e)  // Server error
        }
    }
}

// In UI — retry on error
adapter.addLoadStateListener { loadState ->
    when (loadState.refresh) {
        is LoadState.Error -> {
            showErrorSnackbar {
                adapter.retry()  // Retry failed load
            }
        }
    }
}

// In Compose
val users = viewModel.users.collectAsLazyPagingItems()
// users.retry() — retry last failed load
// users.refresh() — refresh from scratch
```

---

## Q8: How do you use Paging with Room?

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): PagingSource<Int, User>

    @Insert
    suspend fun insertAll(users: List<User>)
}

// Repository
class UserRepository(
    private val api: UserApi,
    private val dao: UserDao
) {
    fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { dao.getAllUsers() }  // Room as PagingSource
        ).flow
    }
}
```

### Room + Network (Boundary Callback)
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): PagingSource<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}

class UserRepository(
    private val api: UserApi,
    private val dao: UserDao
) {
    fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = UserRemoteMediator(api, dao),
            pagingSourceFactory = { dao.getAllUsers() }
        ).flow
    }
}
```

---

## Q9: How do you use RemoteMediator?

```kotlin
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val api: UserApi,
    private val dao: UserDao
) : RemoteMediator<Int, User>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, User>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.page + 1
                }
            }

            val response = api.getUsers(page, state.config.pageSize)

            if (loadType == LoadType.REFRESH) {
                dao.clearAll()
            }

            dao.insertAll(response.users.map { it.copy(page = page) })

            MediatorResult.Success(endOfPaginationReached = response.users.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
```

### RemoteMediator flow
```
User scrolls → Room PagingSource provides local data
                    ↓
            RemoteMediator fetches from network
                    ↓
            Saves to Room → Room PagingSource auto-updates
                    ↓
            UI shows new data
```

---

## Q10: How do you add headers and footers?

```kotlin
class UserAdapter : PagingDataAdapter<User, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_HEADER = 1
        private const val TYPE_FOOTER = 2
    }

    private var state: LoadState = LoadState.NotLoading(endOfPaginationReached = false)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && hasHeader) TYPE_HEADER
        else if (position < super.getItemCount() + (if (hasHeader) 1 else 0)) TYPE_USER
        else TYPE_FOOTER
    }

    // With LoadStateAdapter (simpler)
    fun withLoadStateHeaderAndFooter(
        header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        return ConcatAdapter(header, this, footer)
    }
}

// Usage
val adapter = UserAdapter()
recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
    header = LoadStateAdapter { adapter.retry() },
    footer = LoadStateAdapter { adapter.retry() }
)
```

---

## Q11: How do you transform PagingData (map, filter)?

```kotlin
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users: Flow<PagingData<User>> = repository.getUsers()
        .map { pagingData ->
            pagingData
                .filter { it.isActive }  // Filter inactive users
                .map { it.copy(name = it.name.uppercase()) }  // Transform
        }
        .cachedIn(viewModelScope)  // Cache in ViewModel scope
}

// With different type
val uiModels: Flow<PagingData<UserUiModel>> = repository.getUsers()
    .map { pagingData ->
        pagingData.map { user -> user.toUiModel() }
    }
    .cachedIn(viewModelScope)
```

### PagingData operators
| Operator | Description |
|----------|-------------|
| `map` | Transform each item |
| `filter` | Remove items |
| `flatMap` | Transform to new PagingData |
| `insertSeparators` | Add separators between items |

### Insert separators
```kotlin
val usersWithSeparators = repository.getUsers()
    .map { pagingData ->
        pagingData.insertSeparators { before, after ->
            if (before == null) return@insertSeparators null
            if (after == null) return@insertSeparators null
            if (before.name.first() != after.name.first()) {
                SeparatorItem(after.name.first().toString())
            } else {
                null
            }
        }
    }
```

---

## Q12: How do you implement search with Paging?

```kotlin
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val results: Flow<PagingData<SearchResult>> = query
        .debounce(300)  // Wait for typing to stop
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            repository.search(query)
        }
        .cachedIn(viewModelScope)

    fun setQuery(q: String) {
        query.value = q
    }
}

// PagingSource for search
class SearchPagingSource(
    private val api: SearchApi,
    private val query: String
) : PagingSource<Int, SearchResult>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult> {
        return try {
            val page = params.key ?: 1
            val response = api.search(query, page, params.loadSize)
            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SearchResult>): Int? {
        return state.anchorPosition
    }
}
```

---

## Q13: How do you test Paging?

```kotlin
class UserPagingSourceTest {

    private val api: UserApi = mock()
    private lateinit var pagingSource: UserPagingSource

    @Before
    fun setUp() {
        pagingSource = UserPagingSource(api)
    }

    @Test
    fun `load first page returns correct data`() = runTest {
        whenever(api.getUsers(page = 1, pageSize = 20))
            .thenReturn(UserResponse(listOf(User("1", "Alice"))))

        val result = pagingSource.load(
            LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is LoadResult.Page)
        assertEquals(1, (result as LoadResult.Page).data.size)
        assertEquals("Alice", result.data[0].name)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `load error returns LoadResult Error`() = runTest {
        whenever(api.getUsers(any(), any()))
            .thenThrow(RuntimeException("Network error"))

        val result = pagingSource.load(
            LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is LoadResult.Error)
    }
}
```

### Testing PagingData
```kotlin
@Test
fun `repository emits paging data`() = runTest {
    val repository = UserRepository(api)
    val pagingData = repository.getUsers().first()

    // Use TestPager to test
    val pager = TestPager(
        config = PagingConfig(pageSize = 20),
        pagingSource = UserPagingSource(api)
    )

    val page = pager.refresh() as LoadResult.Page
    assertEquals(1, page.data.size)
}
```

---

## Q14: How do you handle list pagination with cursor-based API?

```kotlin
// Cursor-based pagination (instead of page numbers)
class CursorPagingSource(
    private val api: GitHubApi
) : PagingSource<String, Repo>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Repo> {
        return try {
            // Key is cursor (String), not page number (Int)
            val cursor = params.key  // null for first page
            val response = api.getRepos(cursor = cursor, pageSize = params.loadSize)

            LoadResult.Page(
                data = response.repos,
                prevKey = null,  // GitHub API doesn't support backward
                nextKey = response.nextCursor  // Cursor for next page
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Repo>): String? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id }
    }
}
```

### Page-based vs Cursor-based
| Feature | Page-based | Cursor-based |
|---------|-----------|-------------|
| Key type | Int (page number) | String (cursor/token) |
| Skip pages | ✅ Yes | ❌ No |
| Data consistency | May miss/duplicate items | Consistent |
| Use case | Simple APIs | Real-time data |
| Example | `/users?page=2` | `/users?after=abc123` |

---

## Q15: What are Paging 3 best practices?

### Do's
- ✅ Use `cachedIn(viewModelScope)` in ViewModel
- ✅ Handle all LoadStates (loading, error, empty)
- ✅ Use `PagingDataAdapter` for RecyclerView
- ✅ Use `collectAsLazyPagingItems()` for Compose
- ✅ Set appropriate `pageSize` and `prefetchDistance`
- ✅ Use RemoteMediator for network + Room
- ✅ Handle errors with retry mechanism

### Don'ts
- ❌ Don't cache PagingData in Activity (use ViewModel)
- ❌ Don't use `collect` instead of `collectLatest` (for initial load)
- ❌ Don't set `pageSize` too small (too many API calls)
- ❌ Don't set `pageSize` too large (memory issues)
- ❌ Don't forget `getRefreshKey()` implementation
- ❌ Don't block in `load()` (it's already suspend)

### Performance tips
| Tip | Impact |
|-----|--------|
| `enablePlaceholders = false` | Less memory |
| `maxSize` limit | Prevents OOM |
| `prefetchDistance` tuning | Smoother scrolling |
| `initialLoadSize = pageSize * 2` | Faster first screen |
| DiffUtil in adapter | Efficient updates |

---

## 🔗 Related Topics
- [Room Database](RoomDatabase.md)
- [Retrofit](Retrofit.md)
- [ViewModel & LiveData](ViewModelLiveData.md)
