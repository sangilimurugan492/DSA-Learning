# Retrofit & Networking

## 📖 Explanation

Retrofit is a type-safe HTTP client for Android. It turns HTTP APIs into Kotlin/Java interfaces with annotations.

### Key Components
| Component       | Description                                          |
|-----------------|------------------------------------------------------|
| `Retrofit`       | Builder — base URL, converter, client               |
| `@GET`, `@POST`  | HTTP method annotations                              |
| `@Path`         | URL path parameter substitution                      |
| `@Query`        | Query string parameter                               |
| `@Body`         | Request body (POST/PUT)                              |
| `@Header`       | Dynamic header                                       |
| `@FormUrlEncoded` | Form-encoded POST                                   |
| `@Multipart`    | Multipart request (file upload)                      |

### API Interface
```kotlin
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): User

    @POST("users")
    suspend fun createUser(@Body user: User): User

    @GET("users")
    suspend fun searchUsers(@Query("q") query: String): List<User>
}
```

### Retrofit Setup
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(MoshiConverterFactory.create())
    .client(okHttpClient)
    .build()

val api = retrofit.create(ApiService::class.java)
```

### OkHttp Interceptor
Add logging, auth headers, retry logic.

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val authInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .build()
    chain.proceed(request)
}
```

### Error Handling
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int) : Result<Nothing>()
}
```

### Converter Factories
| Converter       | Format  |
|-----------------|---------|
| `Gson`           | JSON    |
| `Moshi`          | JSON (Kotlin-first) |
| `Kotlinx Serialization` | JSON (Kotlin multiplatform) |
| `Scalars`       | Plain text |

---

## 🧪 Code Example

```kotlin
package com.example.app

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

// --- Data Models ---
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val avatarUrl: String?
)

data class CreateUserRequest(
    val name: String,
    val email: String
)

// --- API Interface ---
interface ApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<User>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): User

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): User

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: CreateUserRequest
    ): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @GET("users")
    suspend fun searchUsers(@Query("q") query: String): List<User>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @Multipart
    @POST("users/{id}/avatar")
    suspend fun uploadAvatar(
        @Path("id") userId: Long,
        @Part avatar: MultipartBody.Part
    ): User
}

data class AuthResponse(val token: String, val expiresIn: Long)

// --- Network Module (DI setup) ---
object NetworkModule {

    private const val BASE_URL = "https://api.example.com/"

    fun createApiService(tokenProvider: () -> String?): ApiService {
        // Logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Auth interceptor
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${tokenProvider() ?: ""}")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        // OkHttp client
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        // Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// --- Repository with Error Handling ---
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

class UserRepository(private val api: ApiService) {

    suspend fun getUsers(page: Int = 1): NetworkResult<List<User>> {
        return try {
            val users = api.getUsers(page)
            NetworkResult.Success(users)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Error(
                message = "HTTP ${e.code()}: ${e.message()}",
                code = e.code()
            )
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Network error")
        }
    }

    suspend fun getUserById(id: Long): NetworkResult<User> {
        return try {
            NetworkResult.Success(api.getUserById(id))
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Error")
        }
    }

    suspend fun createUser(name: String, email: String): NetworkResult<User> {
        return try {
            val request = CreateUserRequest(name, email)
            NetworkResult.Success(api.createUser(request))
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Error")
        }
    }

    suspend fun deleteUser(id: Long): NetworkResult<Unit> {
        return try {
            api.deleteUser(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Error")
        }
    }
}

// --- ViewModel ---
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkResult<List<User>>>(NetworkResult.Loading)
    val uiState: StateFlow<NetworkResult<List<User>>> = _uiState

    fun loadUsers() {
        _uiState.value = NetworkResult.Loading
        viewModelScope.launch {
            _uiState.value = repository.getUsers()
        }
    }
}
```

```groovy
// build.gradle dependencies
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
}
```

---

## ❓ Interview Questions

1. **What is Retrofit and why use it over HttpURLConnection?**
   - Retrofit is a type-safe HTTP client. It turns API endpoints into interface methods, handles JSON parsing automatically, supports coroutines, and provides interceptors. HttpURLConnection requires manual parsing and error handling.

2. **What is the difference between `@Path` and `@Query`?**
   - `@Path` substitutes a value into the URL path (`/users/{id}` → `/users/42`). `@Query` appends a query parameter (`?q=search`).

3. **How do you add authentication headers in Retrofit?**
   - Use an OkHttp `Interceptor` that adds the `Authorization` header to every request. This is cleaner than `@Header` on each method.

4. **How do you handle errors in Retrofit with coroutines?**
   - Wrap API calls in try/catch. Catch `HttpException` for HTTP errors (4xx, 5xx) and generic `Exception` for network failures. Return a sealed `Result` type for clean UI handling.

5. **What is the difference between Gson, Moshi, and Kotlinx Serialization?**
   - Gson is Java-based (reflection, slower). Moshi is Kotlin-first (better null safety, codegen). Kotlinx Serialization is multiplatform, compile-time safe, and the most modern. All work with Retrofit via converter factories.

6. **How do you implement authentication token refresh with Retrofit?**
    - Use an OkHttp `Authenticator` which automatically retries requests that return 401. The `Authenticator` intercepts the 401 response, refreshes the token (synchronized to avoid multiple simultaneous refreshes), updates the stored token, and retries the request with the new token. Example: `class TokenAuthenticator : Authenticator { override fun authenticate(route, response): Request? { val newToken = refreshTokenSync(); return response.request.newBuilder().header("Authorization", "Bearer $newToken").build() } }`. If refresh fails (refresh token expired), return `null` to give up. Use `responseCount(response)` to prevent infinite loops (max 2 retries). For concurrent requests, use a `Mutex` or `synchronized` block to ensure only one refresh happens. Store tokens securely in `EncryptedSharedPreferences`.

7. **What are OkHttp Interceptors and what are the two types?**
    - Interceptors modify, monitor, or short-circuit HTTP requests/responses. Two types: (1) **Application Interceptors** — added via `addInterceptor()`. Called once, between the app and OkHttp core. Sees the original request (before redirects/retries). Use for: adding auth headers, logging, request modification. (2) **Network Interceptors** — added via `addNetworkInterceptor()`. Called for each actual network call (including redirects/retries). Sees the raw network data (compressed, redirected). Use for: caching, monitoring network conditions, retry logic. Order: Application interceptors run first (request), then network interceptors, then the server, then network interceptors (response), then application interceptors. Use `addInterceptor` for most cases. Use `addNetworkInterceptor` when you need to see intermediate responses (redirects, retries).

8. **How do you handle file uploads and downloads with Retrofit?**
    - **Upload**: Use `@Multipart` annotation with `@Part`. For a single file: `@Multipart @POST("upload") suspend fun upload(@Part file: MultipartBody.Part): Response`. Create the part: `val filePart = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/*".toMediaType()))`. For multiple files: `@Part files: List<MultipartBody.Part>`. **Download**: Use `@Streaming` for large files to avoid loading into memory: `@Streaming @GET("download") suspend fun downloadFile(@Url url: String): ResponseBody`. Write to disk with: `inputStream.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }`. Use `@Url` for dynamic URLs. Always use `@Streaming` for files >1MB. Show progress with a custom `RequestBody` that wraps the file and reports bytes written.

9. **How do you implement caching with OkHttp and Retrofit?**
    - OkHttp has a built-in cache based on HTTP cache headers. (1) Create a `Cache` directory: `val cache = Cache(File(cacheDir, "http_cache"), 10 * 1024 * 1024) // 10MB`. (2) Add to `OkHttpClient.Builder().cache(cache)`. (3) The server must send `Cache-Control` headers. (4) For offline support, use a `ForceCacheInterceptor`: override the request to use `Cache-Control: only-if-cached` when offline. (5) For custom caching, use an interceptor that checks connectivity and serves cached responses. `maxStale` allows using cached responses even when expired: `CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build()`. Note: GET requests are cached; POST/PUT/DELETE are not. Alternative: use Room as an offline cache with `NetworkBoundResource` pattern.

10. **What is the `Result` sealed class pattern for network error handling?**
    - Create a sealed class to model all possible outcomes: `sealed class NetworkResult<out T> { data class Success<T>(val data: T) : NetworkResult<T>(); data class Error(val code: Int, val message: String) : NetworkResult<Nothing>(); object Loading : NetworkResult<Nothing>(); object NetworkError : NetworkResult<Nothing>() }`. In the Repository, wrap API calls in try/catch: catch `HttpException` for HTTP errors (4xx/5xx), `IOException` for network failures. Return `NetworkResult.Success(data)` on success. The ViewModel exposes `StateFlow<NetworkResult<T>>` and the UI handles each state: Loading → show ProgressBar, Success → show data, Error → show error message with retry, NetworkError → show offline message. This pattern centralizes error handling and makes the UI logic clean with `when` expressions. It's type-safe and exhaustive.

11. **How do you configure timeouts and retries in OkHttp?**
    - Configure in `OkHttpClient.Builder()`: `.connectTimeout(30, TimeUnit.SECONDS)` (time to establish connection), `.readTimeout(30, TimeUnit.SECONDS)` (time between bytes read), `.writeTimeout(30, TimeUnit.SECONDS)` (time between bytes written). For retries: `.retryOnConnectionFailure(true)` (default true). OkHttp automatically retries on connection failures but NOT on HTTP errors (5xx). For custom retry logic, use an interceptor with `Chain.proceed()` in a loop with `attempt` counter and exponential backoff. For network-specific retries, use `Authenticator` for 401s. Set reasonable timeouts — too short causes failures on slow networks, too long blocks the user. Default is 10 seconds. For file uploads, increase `writeTimeout`. For large downloads, increase `readTimeout` or set to 0 (no timeout).

12. **How do you test Retrofit API calls?**
    - Three approaches: (1) **MockWebServer** (recommended) — starts a local HTTP server that returns predefined responses. `val server = MockWebServer(); server.enqueue(MockResponse().setBody("{}").setResponseCode(200))`. Point Retrofit to `server.url("/")`. Tests the full HTTP stack including headers, serialization, and error handling. (2) **Mock the ApiService interface** — use Mockito to mock the interface methods. Faster but doesn't test Retrofit/OkHttp behavior. Good for ViewModel tests. (3) **Integration tests** — use a real server (staging environment). Most realistic but slowest and flaky. For MockWebServer, test: success responses, error codes (400, 401, 404, 500), malformed JSON, timeouts, and empty responses. Use `runTest` for coroutines. Verify the correct URL, headers, and request body with `server.takeRequest()`.

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [Room Database](RoomDatabase.md)
