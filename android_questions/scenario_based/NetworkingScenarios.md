# Networking & Data Scenarios

## Scenario 1: No Internet — Stale Data Shown

### Problem
The app crashes when there's no internet connection. The user sees an error instead of cached data.

```kotlin
// ❌ Bad — crashes on no network, no caching
class BadViewModel(private val api: ApiService) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = api.getUsers()  // ❌ Throws IOException on no network
        }
    }
}
```

### Solution: Offline-first with Room + network fallback

```kotlin
// ✅ Good — Room as single source of truth, network refreshes cache
class UserRepository(
    private val api: ApiService,
    private val dao: UserDao
) {
    // Flow from Room — always emits cached data first
    fun getUsers(): Flow<List<User>> = dao.getAllUsers()

    suspend fun refreshUsers(): Result<Unit> {
        return try {
            val remote = api.getUsers()
            dao.replaceAll(remote)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)  // UI shows cached data + snackbar error
        }
    }
}

class UserViewModel(private val repo: UserRepository) : ViewModel() {
    val users: StateFlow<List<User>> = repo.getUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Idle)
    val refreshState: StateFlow<RefreshState> = _refreshState

    fun refresh() {
        viewModelScope.launch {
            _refreshState.value = RefreshState.Loading
            val result = repo.refreshUsers()
            _refreshState.value = if (result.isSuccess) {
                RefreshState.Success
            } else {
                RefreshState.Error("Offline — showing cached data")
            }
        }
    }
}

sealed class RefreshState {
    object Idle : RefreshState()
    object Loading : RefreshState()
    object Success : RefreshState()
    data class Error(val message: String) : RefreshState()
}
```

### Key Takeaway
- Use Room as the **single source of truth** — UI always reads from DB
- Network call refreshes the cache; failure shows stale data with a message
- `Flow` from Room auto-emits when DB changes — no manual `notify`
- `Result` wrapper handles errors gracefully without crashing

---

## Scenario 2: Retrofit Call Leaking Coroutine

### Problem
A Retrofit call is made but the response is slow. The user navigates away, and the coroutine leaks, updating a destroyed view.

```kotlin
// ❌ Bad — GlobalScope leaks, updates destroyed UI
class BadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.Main) {
            // ❌ GlobalScope never cancels — leaks + crashes
            val data = api.getData()
            findViewById<TextView>(R.id.text).text = data  // Crash if destroyed
        }
    }
}
```

### Solution: lifecycleScope + proper error handling

```kotlin
// ✅ Good — lifecycleScope cancels on destroy, proper error handling
class GoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val textView = findViewById<TextView>(R.id.text)
            try {
                val data = withContext(Dispatchers.IO) { api.getData() }
                // Check if still active — lifecycleScope auto-cancels, but be safe
                if (isActive) {
                    textView.text = data
                }
            } catch (e: CancellationException) {
                throw e  // Don't catch CancellationException
            } catch (e: IOException) {
                textView.text = "Network error"
            }
        }
    }

    // ✅ Even better — use repeatOnLifecycle for Flow
    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataFlow.collect { data ->
                    findViewById<TextView>(R.id.text).text = data
                }
            }
        }
    }
}
```

### Key Takeaway
- Never use `GlobalScope` — it outlives the Activity and causes leaks
- `lifecycleScope.launch` auto-cancels when the lifecycle is destroyed
- Always rethrow `CancellationException` — it's used for coroutine control flow
- Use `repeatOnLifecycle(STARTED)` for Flow collection to pause in background

---

## Scenario 3: Token Expiry — Silent Refresh

### Problem
The auth token expires. Every API call starts failing with 401, and the user is force-logged-out.

```kotlin
// ❌ Bad — 401 crashes or force-logs-out
class BadApiService(private val api: RetrofitApi) {
    suspend fun getUser(): User = api.getUser()  // ❌ 401 → crash
}
```

### Solution: OkHttp Authenticator for automatic token refresh

```kotlin
// ✅ Good — OkHttp Authenticator intercepts 401 and refreshes
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApi: RefreshApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop — if already retried, give up
        if (response.responseCount() >= 2) return null

        // Synchronize refresh — avoid multiple concurrent refreshes
        synchronized(this) {
            val currentToken = tokenManager.accessToken

            // If token changed, another thread already refreshed — retry with new token
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (requestToken != currentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Refresh token synchronously
            return try {
                val refreshResponse = runBlocking { refreshApi.refreshToken(tokenManager.refreshToken) }
                if (refreshResponse.isSuccessful) {
                    val newToken = refreshResponse.body()!!
                    tokenManager.saveTokens(newToken.accessToken, newToken.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newToken.accessToken}")
                        .build()
                } else {
                    // Refresh failed — log out user
                    tokenManager.clearTokens()
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun Response.responseCount(): Int {
        var count = 1
        var prev = priorResponse
        while (prev != null) {
            count++
            prev = prev.priorResponse
        }
        return count
    }
}

// Register in OkHttp client
val okHttpClient = OkHttpClient.Builder()
    .authenticator(TokenAuthenticator(tokenManager, refreshApi))
    .addInterceptor(AuthInterceptor(tokenManager))  // Adds token to every request
    .build()
```

### Key Takeaway
- `Authenticator` is OkHttp's built-in mechanism for 401 retry
- It automatically retries the failed request with the new token
- Synchronize the refresh to avoid multiple simultaneous refresh calls
- Limit retries to prevent infinite loops (check `priorResponse` count)
- If refresh fails, clear tokens and return `null` to trigger logout

---

## Scenario 4: Pagination — Loading More Without Duplicates

### Problem
Implementing pagination with Retrofit. Each page loads but the list shows duplicates or gaps.

```kotlin
// ❌ Bad — race conditions, duplicates, no loading state
class BadViewModel : ViewModel() {
    val items = mutableListOf<Item>()
    var page = 0

    fun loadMore() {
        viewModelScope.launch {
            val newItems = api.getItems(page)  // ❌ Multiple calls overlap
            items.addAll(newItems)             // ❌ May add duplicates
            page++
        }
    }
}
```

### Solution: RemoteMediator with Paging 3

```kotlin
// ✅ Good — Paging 3 with RemoteMediator (network + DB)
@Database(entities = [ItemEntity::class, RemoteKey::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}

class ItemRemoteMediator(
    private val api: ApiService,
    private val db: AppDatabase
) : RemoteMediator<Int, ItemEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = db.remoteKeyDao().getByKey("items")
                remoteKey?.nextPage ?: 0
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = db.remoteKeyDao().getByKey("items")
                remoteKey?.nextPage ?: return MediatorResult.Success(true)
            }
        }

        return try {
            val response = api.getItems(page)
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.itemDao().clearAll()
                    db.remoteKeyDao().deleteByKey("items")
                }
                db.itemDao().insertAll(response.items)
                db.remoteKeyDao().insert(RemoteKey("items", page + 1))
            }
            MediatorResult.Success(endOfPaginationReached = response.items.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

// ViewModel — exposes PagingData
class PagingViewModel(
    private val api: ApiService,
    private val db: AppDatabase
) : ViewModel() {

    val items: Flow<PagingData<ItemEntity>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 10, enablePlaceholders = false),
        remoteMediator = ItemRemoteMediator(api, db)
    ) {
        db.itemDao().pagingSource()  // Room provides PagingSource
    }.flow.cachedIn(viewModelScope)  // Cache in viewModelScope
}

// Activity — collect and submit
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.items.collect { pagingData ->
            adapter.submitData(pagingData)
        }
    }
}

// Adapter — PagingDataAdapter
class PagingAdapter : PagingDataAdapter<ItemEntity, PagingAdapter.VH>(DIFF) {
    // ...
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ItemEntity>() {
            override fun areItemsTheSame(old: ItemEntity, new: ItemEntity) = old.id == new.id
            override fun areContentsTheSame(old: ItemEntity, new: ItemEntity) = old == new
        }
    }
}
```

### Key Takeaway
- Paging 3 `RemoteMediator` handles network + DB coordination
- `LoadType.REFRESH / PREPEND / APPEND` covers all pagination directions
- `RemoteKey` entity tracks the next page number in DB
- `cachedIn(viewModelScope)` keeps PagingData alive across config changes
- `PagingDataAdapter` auto-handles diffing, loading states, and retry

---

## Scenario 5: Concurrent API Calls with Timeout

### Problem
A dashboard needs data from 3 APIs. Calling them sequentially is slow. One slow API blocks the entire screen.

```kotlin
// ❌ Bad — sequential calls, no timeout
class BadViewModel : ViewModel() {
    suspend fun loadDashboard(): Dashboard {
        // ❌ Each takes 2s → total 6s, if one hangs → infinite wait
        val profile = api.getProfile()
        val orders = api.getOrders()
        val notifications = api.getNotifications()
        return Dashboard(profile, orders, notifications)
    }
}
```

### Solution: Parallel calls with async + timeout

```kotlin
// ✅ Good — parallel with withTimeout, partial failure handling
class DashboardViewModel(private val api: ApiService) : ViewModel() {

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            _state.value = try {
                DashboardState.Success(loadDashboardData())
            } catch (e: Exception) {
                DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }

    private suspend fun loadDashboardData(): Dashboard = coroutineScope {
        // ✅ All 3 calls run in parallel — total time = slowest API
        val profileDeferred = async {
            withTimeout(5.seconds) { api.getProfile() }
        }
        val ordersDeferred = async {
            withTimeout(5.seconds) { api.getOrders() }
        }
        val notificationsDeferred = async {
            withTimeout(5.seconds) { api.getNotifications() }
        }

        try {
            Dashboard(
                profile = profileDeferred.await(),
                orders = ordersDeferred.await(),
                notifications = notificationsDeferred.await()
            )
        } catch (e: TimeoutCancellationException) {
            // ✅ Graceful degradation — return partial data if available
            Dashboard(
                profile = profileDeferred.getCompletedOrNull(),
                orders = ordersDeferred.getCompletedOrNull(),
                notifications = notificationsDeferred.getCompletedOrNull(),
                hasError = true
            )
        }
    }
}

// Helper extension
private fun <T> Deferred<T>.getCompletedOrNull(): T? = try {
    getCompleted()
} catch (e: Exception) {
    null
}
```

### Key Takeaway
- `async` + `coroutineScope` launches parallel calls — total time = slowest call
- `withTimeout` prevents indefinite hangs from slow/dead APIs
- `awaitAll()` waits for all; individual `await()` allows partial handling
- Graceful degradation: show partial data if some calls succeed
- `coroutineScope` cancels siblings if one throws — use `supervisorScope` if you want independent failures

---

## 🔗 Related Topics
- [Retrofit & Networking](../intermediate/Retrofit.md)
- [Room Database](../intermediate/RoomDatabase.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
- [Architecture Patterns](../intermediate/ArchitecturePatterns.md)
