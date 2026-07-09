# DataStore

## Q1: What is DataStore and why use it?

DataStore is Jetpack's data storage solution that replaces SharedPreferences with a modern, coroutine-based API.

### DataStore vs SharedPreferences
| Feature | SharedPreferences | DataStore |
|---------|------------------|----------|
| Async API | ❌ (sync on disk) | ✅ (coroutines/Flow) |
| Type safety | ❌ | ✅ (Proto) |
| Error handling | ❌ | ✅ |
| Main thread safe | ❌ | ✅ |
| Migration | — | ✅ (from SharedPreferences) |
| Backpressure | ❌ | ✅ |

### Setup
```gradle
dependencies {
    // Preferences DataStore
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
    // Proto DataStore
    implementation 'androidx.datastore:datastore:1.0.0'
}
```

### Two types
| Type | Description |
|------|-------------|
| Preferences DataStore | Key-value pairs, no type safety |
| Proto DataStore | Custom types, type-safe, schema |

---

## Q2: How do you use Preferences DataStore?

```kotlin
// Create DataStore extension
private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val context: Context) {

    // Define keys
    companion object {
        val THEME_KEY = booleanPreferencesKey("dark_theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
    }

    // Read data
    val themeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[THEME_KEY] ?: false }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[LANGUAGE_KEY] ?: "en" }

    // Write data
    suspend fun setTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}
```

---

## Q3: How do you use Preferences DataStore in ViewModel?

```kotlin
class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<Boolean> = repository.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val language: StateFlow<String> = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            repository.setTheme(isDark)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }
}

// In Activity/Fragment
class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.theme.collect { isDark ->
                    // Update UI
                }
            }
        }
    }
}
```

---

## Q4: How do you use Proto DataStore?

```kotlin
// 1. Define proto schema (settings.proto)
syntax = "proto3";
message UserSettings {
    bool dark_theme = 1;
    string language = 2;
    int32 font_size = 3;
}

// 2. Create Serializer
object SettingsSerializer : Serializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings(
        darkTheme = false,
        language = "en",
        fontSize = 14
    )

    override suspend fun readFrom(input: InputStream): UserSettings {
        return try {
            UserSettings.ADAPTER.decode(input)
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        UserSettings.ADAPTER.encode(output, t)
    }
}

// 3. Create DataStore
private val Context.settingsDataStore: DataStore<UserSettings>
    by dataStore("settings.pb", SettingsSerializer)

// 4. Repository
class SettingsRepository(private val context: Context) {
    val settings: Flow<UserSettings> = context.settingsDataStore.data

    suspend fun updateTheme(isDark: Boolean) {
        context.settingsDataStore.updateData { current ->
            current.copy(darkTheme = isDark)
        }
    }

    suspend fun updateLanguage(language: String) {
        context.settingsDataStore.updateData { current ->
            current.copy(language = language)
        }
    }
}
```

### Preferences vs Proto
| Feature | Preferences | Proto |
|---------|-----------|-------|
| Type safety | ❌ | ✅ |
| Schema | No | Yes (.proto) |
| Migration | Easy | Harder |
| Setup | Simple | More setup |
| Best for | Simple key-value | Complex objects |

---

## Q5: How do you migrate from SharedPreferences?

```kotlin
// SharedPreferences migration
private val Context.dataStore by preferencesDataStore(
    name = "settings",
    produceMigrations = { listOf(
        SharedPreferencesMigration(
            context = it,
            sharedPreferencesName = "old_prefs",
            keysToMigrate = listOf("dark_theme", "language")
        )
    )}
)

// Auto migration
class SettingsRepository(context: Context) {
    private val dataStore = context.dataStore

    val themeFlow = dataStore.data
        .map { it[booleanPreferencesKey("dark_theme")] ?: false }

    suspend fun setTheme(isDark: Boolean) {
        dataStore.edit { it[booleanPreferencesKey("dark_theme")] = isDark }
    }
}
```

### Migration types
| Type | Description |
|------|-------------|
| `SharedPreferencesMigration` | Auto-migrate from SharedPreferences |
| Manual migration | Custom logic for complex migrations |

---

## Q6: How do you handle errors in DataStore?

```kotlin
class SettingsRepository(context: Context) {

    private val dataStore = context.dataStore

    val settings: Flow<UserSettings> = dataStore.data
        .catch { exception ->
            // Handle different error types
            when (exception) {
                is IOException -> {
                    // Data corruption — emit default
                    emit(UserSettings())
                }
                else -> {
                    // Other errors — rethrow
                    throw exception
                }
            }
        }

    // Safe write with error handling
    suspend fun updateSettings(update: (UserSettings) -> UserSettings): Result<Unit> {
        return try {
            dataStore.updateData { current ->
                update(current)
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Common errors
| Error | Cause | Solution |
|-------|-------|---------|
| `IOException` | Data corruption | Catch and emit default |
| `CorruptionException` | Proto parse error | Catch and reset |
| `ConcurrentModificationException` | Multiple writes | Use `updateData()` |

---

## Q7: How do you store lists and objects?

```kotlin
// Store list of strings
class BookmarksRepository(context: Context) {
    private val BOOKMARKS_KEY = stringSetPreferencesKey("bookmarks")

    val bookmarks: Flow<Set<String>> = context.dataStore.data
        .map { it[BOOKMARKS_KEY] ?: emptySet() }

    suspend fun addBookmark(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BOOKMARKS_KEY] ?: emptySet()
            prefs[BOOKMARKS_KEY] = current + id
        }
    }

    suspend fun removeBookmark(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BOOKMARKS_KEY] ?: emptySet()
            prefs[BOOKMARKS_KEY] = current - id
        }
    }
}

// Store custom object as JSON
class UserRepository(context: Context) {

    private val USER_KEY = stringPreferencesKey("current_user")
    private val gson = Gson()

    val currentUser: Flow<User?> = context.dataStore.data
        .map { prefs ->
            prefs[USER_KEY]?.let { gson.fromJson(it, User::class.java) }
        }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_KEY] = gson.toJson(user)
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_KEY)
        }
    }
}
```

---

## Q8: How do you use DataStore with Hilt?

```kotlin
// Hilt module
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile("settings.preferences_pb") }
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository = SettingsRepository(dataStore)
}

// Repository using injected DataStore
class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    val themeFlow: Flow<Boolean> = dataStore.data
        .map { it[THEME_KEY] ?: false }

    suspend fun setTheme(isDark: Boolean) {
        dataStore.edit { it[THEME_KEY] = isDark }
    }
}
```

---

## Q9: How do you test DataStore?

```kotlin
class SettingsRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        val tempFile = File.createTempFile("test", ".preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { tempFile }
        )
        repository = SettingsRepository(dataStore)
    }

    @Test
    fun `default theme is false`() = runTest {
        val theme = repository.themeFlow.first()
        assertFalse(theme)
    }

    @Test
    fun `set theme updates flow`() = runTest {
        repository.setTheme(true)

        val theme = repository.themeFlow.first()
        assertTrue(theme)
    }

    @Test
    fun `theme flow emits updates`() = runTest {
        val themes = mutableListOf<Boolean>()
        val job = launch(UnconfinedTestDispatcher()) {
            repository.themeFlow.collect { themes.add(it) }
        }

        repository.setTheme(true)
        repository.setTheme(false)

        job.cancel()

        assertEquals(listOf(false, true, false), themes)
    }
}
```

---

## Q10: What are DataStore best practices?

### Do's
- ✅ Use `Flow` for reading (reactive)
- ✅ Use `edit {}` or `updateData {}` for writing
- ✅ Use `catch {}` for error handling
- ✅ Use Proto DataStore for type safety
- ✅ Inject DataStore with Hilt
- ✅ Use `stateIn()` in ViewModel

### Don'ts
- ❌ Don't read on main thread (use Flow)
- ❌ Don't create multiple DataStore instances for same file
- ❌ Don't store large data (use Room instead)
- ❌ Don't use SharedPreferences for new code
- ❌ Don't forget to handle `IOException`

### When to use what
| Storage | Use case |
|---------|----------|
| DataStore | Simple settings, preferences |
| Room | Complex data, queries, relations |
| File | Large files, media |
| EncryptedSharedPreferences | Sensitive data (tokens) |

---

## 🔗 Related Topics
- [Room Database](RoomDatabase.md)
- [ViewModel & LiveData](ViewModelLiveData.md)
- [Dependency Injection](../advanced/DependencyInjection.md)
