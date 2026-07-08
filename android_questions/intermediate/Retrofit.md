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

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [Room Database](RoomDatabase.md)
