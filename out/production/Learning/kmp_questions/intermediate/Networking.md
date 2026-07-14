# KMP Networking — Interview Questions

## 🔴 Q1: How do you implement networking in KMP?
**Answer:** Use **Ktor Client** — the standard multiplatform HTTP client:

```kotlin
// commonMain
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreCase = true })
    }
}

suspend fun fetchUser(id: String): User {
    return httpClient.get("https://api.example.com/users/$id").body()
}
```

```kotlin
// build.gradle.kts
val commonMain by getting {
    dependencies {
        implementation("io.ktor:ktor-client-core:2.3.7")
        implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    }
}
val androidMain by getting {
    dependencies { implementation("io.ktor:ktor-client-okhttp:2.3.7") }
}
val iosMain by getting {
    dependencies { implementation("io.ktor:ktor-client-darwin:2.3.7") }
}
```

---

## 🔴 Q2: What Ktor engines are available per platform?
**Answer:**

| Platform | Engine | Dependency |
|----------|--------|------------|
| Android | OkHttp | `ktor-client-okhttp` |
| iOS | Darwin (NSURLSession) | `ktor-client-darwin` |
| JVM | Java (HttpURLConnection) | `ktor-client-java` |
| JS | JS (fetch) | `ktor-client-js` |
| Node.js | Node | `ktor-client-node` |
| CIO | Coroutine I/O | `ktor-client-cio` |

```kotlin
// commonMain
expect val httpClientEngine: HttpClientEngineFactory<*>

// androidMain
actual val httpClientEngine = OkHttp
// iosMain
actual val httpClientEngine = Darwin
```

---

## 🔴 Q3: How do you configure Ktor with serialization?
**Answer:**

```kotlin
// commonMain
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}

@Serializable
data class User(val id: String, val name: String, val email: String)

suspend fun getUser(id: String): User {
    return httpClient.get("/users/$id").body()
}

suspend fun createUser(user: User): User {
    return httpClient.post("/users") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()
}
```

---

## 🟡 Q4: How do you handle authentication in Ktor KMP?
**Answer:**

```kotlin
// commonMain
val httpClient = HttpClient {
    install(Auth) {
        bearer {
            tokenProvider = BearerTokenProvider {
                val token = tokenManager.getAccessToken()
                BearerTokens(token, tokenManager.getRefreshToken())
            }
            refreshTokens {
                val newToken = tokenManager.refreshToken()
                BearerTokens(newToken.accessToken, newToken.refreshToken)
            }
        }
    }
}

// Token manager interface (platform-specific)
interface TokenManager {
    fun getAccessToken(): String
    fun getRefreshToken(): String
    fun saveTokens(accessToken: String, refreshToken: String)
}
```

---

## 🟡 Q5: How do you add interceptors in Ktor?
**Answer:**

```kotlin
val httpClient = HttpClient {
    install(Logging) {
        level = LogLevel.HEADERS
        logger = Logger.SIMPLE
    }
    
    install(DefaultRequest) {
        header("X-Platform", "KMP")
        header("Accept", "application/json")
    }
    
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 10_000
    }
}

// Custom interceptor
install(HttpSend) {
    intercept { request ->
        request.headers.append("Authorization", "Bearer ${tokenManager.getToken()}")
        execute(request)
    }
}
```

---

## 🟡 Q6: How do you handle errors in Ktor?
**Answer:**

```kotlin
// commonMain
suspend fun <T> safeRequest(block: suspend () -> HttpResponse): Result<T> {
    return try {
        val response = block()
        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created -> {
                Result.success(response.body<T>())
            }
            HttpStatusCode.Unauthorized -> Result.failure(AuthError("Token expired"))
            HttpStatusCode.NotFound -> Result.failure(NotFoundError("Resource not found"))
            else -> Result.failure(ServerError("HTTP ${response.status.value}"))
        }
    } catch (e: UnresolvedAddressException) {
        Result.failure(NetworkError("No internet"))
    } catch (e: Exception) {
        Result.failure(UnknownError(e.message ?: "Unknown"))
    }
}

sealed class ApiError : Throwable() {
    data class Network(val msg: String) : ApiError()
    data class Auth(val msg: String) : ApiError()
    data class Server(val msg: String) : ApiError()
}
```

---

## 🟡 Q7: How do you implement retry logic in Ktor?
**Answer:**

```kotlin
val httpClient = HttpClient {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        exponentialDelay()
        retryIf { request, response ->
            response.status.value in 500..599
        }
    }
}

// Manual retry
suspend fun <T> withRetry(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    block: suspend () -> T
): T {
    var lastError: Exception? = null
    repeat(maxRetries) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastError = e
            delay(delayMs * (attempt + 1))
        }
    }
    throw lastError ?: RuntimeException("Retry failed")
}
```

---

## 🟡 Q8: How do you handle file uploads/downloads in KMP?
**Answer:**

```kotlin
// commonMain — Upload
suspend fun uploadFile(file: ByteArray, fileName: String): String {
    return httpClient.post("/upload") {
        setBody(MultiPartFormDataContent(
            formData {
                append("file", file, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=$fileName")
                })
            }
        ))
    }.body()
}

// commonMain — Download
suspend fun downloadFile(url: String): ByteArray {
    return httpClient.get(url).body()
}
```

---

## 🟡 Q9: How do you implement caching for network requests?
**Answer:**

```kotlin
// commonMain
class CachedApiClient(
    private val client: HttpClient,
    private val cache: KeyValueStorage
) {
    suspend fun <T> get(url: String, ttl: Long = 300_000): T where T : Serializable {
        val cacheKey = "cache_$url"
        cache.getString(cacheKey)?.let { cached ->
            val entry = Json.decodeFromString<CacheEntry<T>>(cached)
            if (System.currentTimeMillis() - entry.timestamp < ttl) {
                return entry.data
            }
        }
        
        val response: T = client.get(url).body()
        cache.putString(cacheKey, Json.encodeToString(CacheEntry(response, System.currentTimeMillis())))
        return response
    }
}

@Serializable
data class CacheEntry<T>(val data: T, val timestamp: Long)
```

---

## 🟡 Q10: How do you handle WebSocket connections in KMP?
**Answer:**

```kotlin
// commonMain
suspend fun connectWebSocket(url: String) {
    httpClient.webSocket(url) {
        // Send
        send("Hello Server!")
        
        // Receive
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> println("Received: ${frame.readText()}")
                is Frame.Binary -> println("Received binary: ${frame.data.size} bytes")
            }
        }
    }
}
```

---

## 🟡 Q11: How do you configure SSL/TLS in KMP?
**Answer:**

```kotlin
// androidMain — OkHttp config
val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            sslSocketFactory(mySslSocketFactory, myTrustManager)
            hostnameVerifier { hostname, session -> 
                // Custom verification
                true
            }
        }
    }
}

// iosMain — Darwin config
val httpClient = HttpClient(Darwin) {
    engine {
        configureRequest {
            setURLCredential(credential, forChallenge)
        }
    }
}
```

---

## 🟡 Q12: How do you test networking in KMP?
**Answer:**

```kotlin
// commonTest
class UserApiTest {
    private val mockEngine = MockEngine { request ->
        respond(
            content = """{"id":"1","name":"John","email":"john@test.com"}""",
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }
    
    private val client = HttpClient(mockEngine) {
        install(ContentNegotiation) { json() }
    }
    
    @Test
    fun `should fetch user`() = runTest {
        val api = UserApi(client)
        val user = api.getUser("1")
        assertEquals("John", user.name)
    }
}
```

Use `ktor-client-mock` for testing.

---

## 📌 Key Takeaways
- **Ktor** is the standard KMP HTTP client
- Different engines per platform (OkHttp for Android, Darwin for iOS)
- Use `ContentNegotiation` + `kotlinx.serialization` for JSON
- `Bearer` auth with token refresh built-in
- `MockEngine` for testing network calls in `commonTest`

---

[← Architecture](Architecture.md) | [Back to README](../README.md) | [Next: Database →](Database.md)
