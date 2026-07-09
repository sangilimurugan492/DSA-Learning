# Serialization in Kotlin

## Q1: What is serialization and why use it?

Serialization converts objects to a format (JSON, Proto) for storage or transmission. Deserialization reverses it.

### Use cases
| Scenario | Format |
|----------|--------|
| API communication | JSON |
| Local storage | JSON / Proto |
| DataStore | Proto |
| Inter-process communication | Parcelable / Proto |

### Kotlin serialization libraries
| Library | Description |
|---------|-------------|
| **kotlinx.serialization** | Official, multiplatform |
| **Gson** | Google, reflection-based |
| **Moshi** | Square, code-gen |
| **Jackson** | Feature-rich, heavy |

---

## Q2: How do you set up kotlinx.serialization?

```gradle
// build.gradle (project)
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.22'
}

// build.gradle (app)
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization'
}

dependencies {
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2'
}
```

### Basic usage
```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int = 0  // Default value
)

// Serialize
val user = User("1", "Alice", "alice@test.com", 30)
val json = Json.encodeToString(user)
// {"id":"1","name":"Alice","email":"alice@test.com","age":30}

// Deserialize
val parsed = Json.decodeFromString<User>(json)
```

---

## Q3: How do you customize field names?

```kotlin
@Serializable
data class User(
    @SerialName("user_id")
    val id: String,

    @SerialName("full_name")
    val name: String,

    @SerialName("email_address")
    val email: String,

    @SerialName("is_active")
    val isActive: Boolean = true
)

// JSON: {"user_id":"1","full_name":"Alice","email_address":"alice@test.com","is_active":true}
```

### Annotation summary
| Annotation | Purpose |
|-----------|---------|
| `@SerialName("name")` | Custom JSON field name |
| `@SerialName("name") @DefaultValue("x")` | Default value |
| `@Transient` | Exclude from serialization |
| `@Required` | Field must be present in JSON |
| `@Optional` | Field can be missing (deprecated) |

---

## Q4: How do you handle nullable and optional fields?

```kotlin
@Serializable
data class User(
    val id: String,              // Required, non-null
    val name: String,            // Required, non-null
    val email: String? = null,   // Optional, nullable
    val phone: String? = null,   // Optional, nullable
    val age: Int = 0,            // Optional, non-null with default
    @Transient val temp: String = ""  // Not serialized
)

// All of these work:
Json.decodeFromString<User>("""{"id":"1","name":"Alice"}""")
Json.decodeFromString<User>("""{"id":"1","name":"Alice","email":"a@b.com"}""")
Json.decodeFromString<User>("""{"id":"1","name":"Alice","email":null}""")
```

### Field types
| Declaration | JSON missing | JSON null | Behavior |
|-------------|-------------|-----------|---------|
| `val x: String` | ❌ Error | ❌ Error | Required |
| `val x: String = "d"` | ✅ "d" | ❌ Error | Optional with default |
| `val x: String? = null` | ✅ null | ✅ null | Optional nullable |
| `@Transient val x` | — | — | Not serialized |

---

## Q5: How do you configure the Json parser?

```kotlin
val json = Json {
    ignoreUnknownKeys = true       // Ignore unknown fields in JSON
    encodeDefaults = true          // Encode fields with default values
    explicitNulls = false          // Don't encode null fields
    prettyPrint = true             // Format with indentation
    coerceInputValues = true       // Coerce invalid values to defaults
    isLenient = true               // Allow unquoted keys, etc.
}

// Usage
val user = json.decodeFromString<User>(jsonString)
val jsonString = json.encodeToString(user)
```

### Configuration options
| Option | Default | Description |
|--------|---------|-------------|
| `ignoreUnknownKeys` | false | Skip unknown JSON fields |
| `encodeDefaults` | false | Include default values in output |
| `explicitNulls` | true | Include null fields in output |
| `prettyPrint` | false | Indented output |
| `coerceInputValues` | false | Coerce bad values to defaults |
| `isLenient` | false | Allow non-standard JSON |

### Recommended config for API
```kotlin
val apiJson = Json {
    ignoreUnknownKeys = true   // API adds fields
    explicitNulls = false      // Smaller payloads
    coerceInputValues = true   // Bad data → defaults
}
```

---

## Q6: How do you serialize collections and nested objects?

```kotlin
@Serializable
data class Address(
    val street: String,
    val city: String,
    val zipCode: String
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val address: Address,           // Nested object
    val phoneNumbers: List<String>,  // List
    val tags: Set<String>,           // Set
    val metadata: Map<String, String>  // Map
)

// Serialize
val user = User(
    id = "1",
    name = "Alice",
    address = Address("123 Main St", "NYC", "10001"),
    phoneNumbers = listOf("555-1234", "555-5678"),
    tags = setOf("admin", "user"),
    metadata = mapOf("key" to "value")
)

val json = Json.encodeToString(user)
```

### Output
```json
{
  "id": "1",
  "name": "Alice",
  "address": { "street": "123 Main St", "city": "NYC", "zipCode": "10001" },
  "phoneNumbers": ["555-1234", "555-5678"],
  "tags": ["admin", "user"],
  "metadata": { "key": "value" }
}
```

---

## Q7: How do you use polymorphic serialization?

```kotlin
@Serializable
sealed class Message {
    @Serializable
    @SerialName("text")
    data class TextMessage(val text: String) : Message()

    @Serializable
    @SerialName("image")
    data class ImageMessage(val url: String, val caption: String?) : Message()
}

// Serialize
val messages: List<Message> = listOf(
    Message.TextMessage("Hello"),
    Message.ImageMessage("https://img.com/1.png", "Photo")
)

val json = Json.encodeToString(messages)
// [{"type":"text","text":"Hello"},{"type":"image","url":"https://img.com/1.png","caption":"Photo"}]

// Deserialize
val parsed = Json.decodeFromString<List<Message>>(json)
```

### Polymorphic with class discriminator
```kotlin
@Serializable
sealed class Result {
    @Serializable
    data class Success(val data: String) : Result()

    @Serializable
    data class Error(val message: String) : Result()
}

// JSON: {"type":"Success","data":"OK"}
// JSON: {"type":"Error","message":"Failed"}
```

---

## Q8: How do you use custom serializers?

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

// Custom serializer for Date
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}

// Usage
@Serializable
data class Event(
    val name: String,
    @Serializable(with = DateSerializer::class) val date: Date
)
```

### Custom serializer for Instant
```kotlin
object InstantSerializer : KSerializer<Instant> {
    override val descriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

@Serializable
data class Article(
    val title: String,
    @Serializable(with = InstantSerializer::class) val publishedAt: Instant
)
```

---

## Q9: How do you use kotlinx.serialization with Retrofit?

```gradle
dependencies {
    implementation 'com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
}
```

```kotlin
// Configure Retrofit with kotlinx.serialization
val json = Json { ignoreUnknownKeys = true }

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @POST("users")
    suspend fun createUser(@Body user: User): User
}

// User is @Serializable
@Serializable
data class User(val id: String, val name: String, val email: String)
```

---

## Q10: How do you handle enums in serialization?

```kotlin
@Serializable
enum class Status {
    @SerialName("pending") PENDING,
    @SerialName("active") ACTIVE,
    @SerialName("inactive") INACTIVE,
    @SerialName("deleted") DELETED
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val status: Status
)

// Serialize
val user = User("1", "Alice", Status.ACTIVE)
// {"id":"1","name":"Alice","status":"active"}

// Deserialize
val parsed = Json.decodeFromString<User>(
    """{"id":"1","name":"Alice","status":"active"}"""
)

// Handle unknown enum values
val json = Json {
    coerceInputValues = true  // Unknown enum → default
}
```

### Enum with fallback
```kotlin
@Serializable
enum class Platform {
    ANDROID, IOS, WEB, UNKNOWN;

    companion object {
        fun fromString(value: String?): Platform =
            values().find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}
```

---

## Q11: How do you serialize to/from JSON elements?

```kotlin
import kotlinx.serialization.json.*

// Parse to JsonElement (no data class needed)
val jsonElement = Json.parseToJsonElement("""{"name":"Alice","age":30}""")

// Access fields
val name = jsonElement.jsonObject["name"]?.jsonPrimitive?.content
val age = jsonElement.jsonObject["age"]?.jsonPrimitive?.intOrNull

// Build JSON programmatically
val jsonObject = buildJsonObject {
    put("name", "Alice")
    put("age", 30)
    put("is_active", true)
    put("tags", buildJsonArray {
        add("admin")
        add("user")
    })
}

val jsonString = jsonObject.toString()
```

### JsonElement types
| Type | Description |
|------|-------------|
| `JsonObject` | Key-value map |
| `JsonArray` | List of elements |
| `JsonPrimitive` | String, number, boolean |
| `JsonNull` | Null value |

---

## Q12: How do you handle versioning and migration?

```kotlin
@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String? = null,       // Added in v2
    val phone: String? = null,       // Added in v3
    @SerialName("avatar_url") val avatarUrl: String? = null  // Added in v4
)

// Old JSON (v1): {"id":"1","name":"Alice"}
// New JSON (v4): {"id":"1","name":"Alice","email":"a@b.com","phone":"555","avatar_url":"img.png"}

// Both work with the same data class
val json = Json { ignoreUnknownKeys = true }
val user1 = json.decodeFromString<User>("""{"id":"1","name":"Alice"}""")
val user4 = json.decodeFromString<User>("""{"id":"1","name":"Alice","email":"a@b.com"}""")
```

### Migration strategies
| Strategy | Description |
|----------|-------------|
| Default values | New fields have defaults |
| Nullable fields | New fields are nullable |
| `ignoreUnknownKeys` | Removed fields don't break |
| `@SerialName` | Rename without breaking |
| Custom deserializer | Complex migration logic |

---

## Q13: How do you use kotlinx.serialization with Compose?

```kotlin
// Save UI state with serialization
@Serializable
data class ScreenState(
    val selectedTab: Int,
    val searchQuery: String,
    val filters: List<String>
)

// Save to SavedStateHandle
class MyViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val json = Json

    var state: ScreenState
        get() = savedStateHandle.get<String>("state")
            ?.let { json.decodeFromString<ScreenState>(it) }
            ?: ScreenState(0, "", emptyList())
        set(value) {
            savedStateHandle["state"] = json.encodeToString(value)
        }
}
```

---

## Q14: How do you compare kotlinx.serialization, Gson, and Moshi?

| Feature | kotlinx.serialization | Gson | Moshi |
|---------|----------------------|------|-------|
| Official | ✅ Kotlin | ❌ Google | ❌ Square |
| Reflection | ❌ Compile-time | ✅ Runtime | ❌ Code-gen |
| Multiplatform | ✅ | ❌ | ❌ |
| Speed | Fast | Slow | Fast |
| Null safety | ✅ | ❌ | ✅ |
| Sealed classes | ✅ | ❌ | ❌ |
| Setup | Plugin + annotation | Just dependency | KAPT/KSP |
| Bundle size | Small | Medium | Small |

### kotlinx.serialization advantages
- ✅ No reflection (faster, works with R8)
- ✅ Kotlin multiplatform
- ✅ Native sealed class support
- ✅ Null-safe
- ✅ Compile-time verification

### Gson pitfalls
```kotlin
// Gson creates object even with invalid JSON
Gson().fromJson("{}", User::class.java)  // User with null fields

// kotlinx.serialization fails at compile time
@Serializable
data class User(val id: String)  // Must have id in JSON
```

---

## Q15: What are serialization best practices?

### Do's
- ✅ Use `kotlinx.serialization` for new projects
- ✅ Use `@SerialName` for API field mapping
- ✅ Set `ignoreUnknownKeys = true` for API responses
- ✅ Use nullable fields for optional API data
- ✅ Use sealed classes for polymorphic data
- ✅ Use `@Transient` for non-serializable fields
- ✅ Configure one shared `Json` instance

### Don'ts
- ❌ Don't use Gson for new Kotlin projects
- ❌ Don't create multiple `Json` instances (share one)
- ❌ Don't use reflection-based libraries with R8
- ❌ Don't serialize sensitive data without encryption
- ❌ Don't forget `@Serializable` on all nested classes

### Shared Json instance
```kotlin
// Singleton
object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }
}

// Use everywhere
val user = JsonConfig.json.decodeFromString<User>(jsonString)
```

---

## 🔗 Related Topics
- [Coroutines](Coroutines.md)
- [Flows](Flows.md)
- [Reflection and Annotations](ReflectionAndAnnotations.md)
