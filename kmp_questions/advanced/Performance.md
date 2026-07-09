# KMP Performance — Interview Questions

## 🔴 Q1: What are common KMP performance issues?
**Answer:**

| Issue | Cause | Solution |
|-------|-------|---------|
| Slow iOS build | Kotlin/Native compilation | Use incremental compilation, cache |
| Large binary size | Skia, stdlib included | Strip unused code, R8/ProGuard |
| Memory leaks | Uncancelled coroutines | Cancel scopes in `deinit` |
| Slow startup | Framework initialization | Lazy initialization |
| JNI overhead | Obj-C interop calls | Batch calls, minimize boundary crossings |

---

## 🔴 Q2: How do you optimize Kotlin/Native build times?
**Answer:**

```properties
# gradle.properties
kotlin.native.cacheKind=static
kotlin.native.cacheKind.iosX64=static
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx4g

# Enable incremental compilation
kotlin.incremental=true
kotlin.native.incrementalCompilation=true
```

```kotlin
// build.gradle.kts
kotlin {
    iosX64 {
        binaries.all {
            freeCompilerArgs += "-Xruntime-logs=info"
        }
    }
}
```

---

## 🟡 Q3: How do you reduce KMP binary size on iOS?
**Answer:**

```kotlin
// build.gradle.kts
kotlin {
    iosX64 {
        binaries {
            framework {
                isStatic = true  // Static framework (smaller)
                freeCompilerArgs += "-Xstrip-stage=FULL"
            }
        }
    }
}
```

Other strategies:
- Remove unused targets
- Use `@OptIn` instead of including experimental APIs
- Minimize dependencies in `iosMain`
- Use tree-shaking (R8 for Android, LLVM strips for iOS)

---

## 🟡 Q4: How do you optimize coroutine performance in KMP?
**Answer:**

```kotlin
// Use appropriate dispatchers
suspend fun processData() {
    // CPU-intensive → Default
    withContext(Dispatchers.Default) {
        heavyComputation()
    }
    
    // I/O → IO
    withContext(Dispatchers.IO) {
        api.fetchData()
    }
    
    // UI → Main
    withContext(Dispatchers.Main) {
        updateUI()
    }
}

// Avoid unnecessary context switching
suspend fun batchProcess(items: List<Item>): List<Result> {
    return withContext(Dispatchers.Default) {
        items.map { process(it) }  // Single context switch
    }
}
```

---

## 🟡 Q5: How do you profile KMP code?
**Answer:**

```kotlin
// Android — use Android Profiler
// iOS — use Instruments

// Manual timing
inline fun <T> measureTime(block: () -> T): T {
    val start = currentTimeMillis()
    val result = block()
    val end = currentTimeMillis()
    println("Execution time: ${end - start}ms")
    return result
}

// Usage
val data = measureTime { api.fetchLargeData() }
```

---

## 🟡 Q6: How do you handle memory management on iOS?
**Answer:**

```kotlin
// commonMain
class DataManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    fun start() {
        scope.launch { observeData() }
    }
    
    // MUST be called from Swift deinit
    fun destroy() {
        scope.cancel()
    }
}
```

```swift
// Swift
class MyManager {
    let manager = DataManager()
    
    deinit {
        manager.destroy()  // Critical!
    }
}
```

---

## 🟡 Q7: How do you optimize network calls in KMP?
**Answer:**

```kotlin
// commonMain
val httpClient = HttpClient {
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 5_000
    }
    
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    
    // Connection pooling
    engine {
        pipelining = true
    }
}

// Batch requests
suspend fun fetchAll(): AllData = coroutineScope {
    val users = async { api.getUsers() }
    val posts = async { api.getPosts() }
    AllData(users.await(), posts.await())
}
```

---

## 🟡 Q8: How do you optimize database queries in KMP?
**Answer:**

```kotlin
// commonMain
class UserRepository(private val db: AppDatabase) {
    // Use transactions for batch operations
    fun saveAll(users: List<User>) {
        db.transaction {
            users.forEach { db.userQueries.insert(it.id, it.name, it.email) }
        }
    }
    
    // Use indexes
    // In .sq file: CREATE INDEX idx_user_email ON User(email)
    
    // Use Flow for reactive queries (avoid polling)
    fun observeUsers(): Flow<List<User>> =
        db.userQueries.selectAll().asFlow().map { it.executeAsList() }
    
    // Pagination
    fun getUsersPage(offset: Long, limit: Long): List<User> =
        db.userQueries.selectPage(offset, limit).executeAsList()
}
```

---

## 🟡 Q9: How do you optimize Compose Multiplatform performance?
**Answer:**

```kotlin
// commonMain
@Composable
fun UserList(users: List<User>) {
    // Use key for efficient diffing
    LazyColumn {
        items(users, key = { it.id }) { user ->
            UserItem(user)
        }
    }
}

@Composable
fun UserItem(user: User) {
    // Avoid unnecessary recomposition
    val name = remember(user) { user.formattedName }
    Text(name)
}

// Use derivedStateOf for computed state
@Composable
fun SearchScreen(users: State<List<User>>, query: State<String>) {
    val filtered by remember {
        derivedStateOf { users.value.filter { it.name.contains(query.value) } }
    }
    UserList(filtered)
}
```

---

## 🟡 Q10: How do you handle lazy initialization in KMP?
**Answer:**

```kotlin
// commonMain
class AppContainer {
    val database: AppDatabase by lazy { 
        AppDatabase(driverFactory.createDriver()) 
    }
    
    val httpClient: HttpClient by lazy { 
        createHttpClient() 
    }
    
    val userRepository: UserRepository by lazy { 
        UserRepository(httpClient, database) 
    }
}
```

---

## 🟡 Q11: How do you minimize Obj-C interop overhead?
**Answer:**

```kotlin
// ❌ Bad — frequent boundary crossing
fun processItems(items: List<ObjCItem>) {
    items.forEach { item ->
        val name = item.name  // Obj-C call
        val value = item.value  // Obj-C call
        process(name, value)
    }
}

// ✅ Good — batch conversion
fun processItems(items: List<ObjCItem>) {
    val data = items.map { it.toKotlin() }  // Single batch conversion
    data.forEach { process(it.name, it.value) }  // Pure Kotlin
}
```

---

## 🟡 Q12: How do you measure and track performance?
**Answer:**

```kotlin
// commonMain
class PerformanceTracker {
    private val timings = mutableMapOf<String, Long>()
    
    inline fun <T> track(name: String, block: () -> T): T {
        val start = currentTimeMillis()
        val result = block()
        val elapsed = currentTimeMillis() - start
        timings[name] = (timings[name] ?: 0) + elapsed
        return result
    }
    
    fun report() {
        timings.forEach { (name, time) ->
            println("$name: ${time}ms")
        }
    }
}
```

---

## 📌 Key Takeaways
- Optimize build times with caching, incremental compilation
- Reduce binary size with static frameworks, tree-shaking
- Cancel coroutines in iOS `deinit` to prevent leaks
- Batch Obj-C interop calls to minimize boundary crossing
- Use `key`, `derivedStateOf`, `remember` for Compose performance
- Lazy initialization for heavy objects

---

[← Compose Multiplatform](ComposeMultiplatform.md) | [Back to README](../README.md) | [Next: CI/CD →](CICD.md)
