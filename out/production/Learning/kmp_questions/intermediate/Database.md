# KMP Database — Interview Questions

## 🔴 Q1: How do you implement a database in KMP?
**Answer:** Use **SQLDelight** — the standard multiplatform SQL library:

```kotlin
// build.gradle.kts
val commonMain by getting {
    dependencies {
        implementation("app.cash.sqldelight:runtime:2.0.1")
    }
}
val androidMain by getting {
    dependencies { implementation("app.cash.sqldelight:android-driver:2.0.1") }
}
val iosMain by getting {
    dependencies { implementation("app.cash.sqldelight:native-driver:2.0.1") }
}
```

```kotlin
// commonMain — Database interface
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain
class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
}

// iosMain
class IosDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver =
        NativeSqliteDriver(AppDatabase.Schema, "app.db")
}
```

---

## 🔴 Q2: What is SQLDelight?
**Answer:** SQLDelight generates type-safe Kotlin APIs from SQL statements. You write `.sq` files, and it generates Kotlin classes:

```sql
-- User.sq
CREATE TABLE User (
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    created_at INTEGER AS int
);

selectById:
SELECT * FROM User WHERE id = ?;

selectAll:
SELECT * FROM User;

insert:
INSERT INTO User(id, name, email, created_at) VALUES (?, ?, ?, ?);

deleteById:
DELETE FROM User WHERE id = ?;
```

Generated Kotlin:
```kotlin
// Auto-generated
class UserQueries {
    fun selectById(id: String): User?
    fun selectAll(): List<User>
    fun insert(id: String, name: String, email: String, createdAt: Long)
    fun deleteById(id: String)
}
```

---

## 🔴 Q3: How do you use SQLDelight with coroutines?
**Answer:**

```kotlin
// commonMain
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.map

class UserRepository(private val database: AppDatabase) {
    fun getUserFlow(id: String): Flow<User?> {
        return database.userQueries
            .selectById(id)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }
    
    fun getAllUsersFlow(): Flow<List<User>> {
        return database.userQueries
            .selectAll()
            .asFlow()
            .map { it.executeAsList() }
    }
    
    fun saveUser(user: User) {
        database.userQueries.insert(
            id = user.id,
            name = user.name,
            email = user.email,
            createdAt = user.createdAt
        )
    }
}
```

---

## 🟡 Q4: How do you handle database migrations in SQLDelight?
**Answer:**

```kotlin
// commonMain
val driver = AndroidSqliteDriver(
    schema = AppDatabase.Schema,
    context = context,
    name = "app.db",
    callback = object : SqlDriver.Callback {
        override fun onUpgrade(db: Database, oldVersion: Int, newVersion: Int) {
            db.execSQL("ALTER TABLE User ADD COLUMN avatar_url TEXT")
        }
    }
)
```

Or use `.sqm` migration files:
```
// 1.sqm (migration from v1 to v2)
ALTER TABLE User ADD COLUMN avatar_url TEXT;
```

---

## 🟡 Q5: How do you handle transactions in SQLDelight?
**Answer:**

```kotlin
// commonMain
class UserRepository(private val db: AppDatabase) {
    fun transferUsers(fromId: String, toId: String) {
        db.transaction {
            // All queries in this block are atomic
            val fromUser = userQueries.selectById(fromId).executeAsOne()
            val toUser = userQueries.selectById(toId).executeAsOne()
            
            userQueries.update(toId, fromUser.name)
            userQueries.update(fromId, toUser.name)
        }
    }
    
    // With result
    fun createUserAndReturn(user: User): User = db.withTransaction {
        userQueries.insert(user.id, user.name, user.email)
        userQueries.selectById(user.id).executeAsOne()
    }
}
```

---

## 🟡 Q6: What is Room Multiplatform?
**Answer:** Room now supports KMP (experimental). You can define entities and DAOs in `commonMain`:

```kotlin
// commonMain
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>
    
    @Insert
    suspend fun insert(user: User)
}

@Entity
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)
```

```kotlin
// androidMain
val db = Room.databaseBuilder<AppDatabase>(context, "app.db")

// iosMain
val db = Room.databaseBuilder<AppDatabase>(name = "app.db")
```

---

## 🟡 Q7: SQLDelight vs Room Multiplatform — which to choose?
**Answer:**

| Aspect | SQLDelight | Room KMP |
|--------|-----------|----------|
| Approach | SQL-first (write SQL, generate Kotlin) | Kotlin-first (annotations, generate SQL) |
| Learning curve | Need SQL knowledge | Easier for Room users |
| Type safety | Strong (generated from SQL) | Strong (annotations) |
| Migrations | `.sqm` files | Fallback migrations |
| Maturity | Stable | Experimental |
| Community | Large KMP community | Large Android community |

---

## 🟡 Q8: How do you test database code in KMP?
**Answer:**

```kotlin
// commonTest
class UserRepositoryTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: AppDatabase
    private lateinit var repo: UserRepository
    
    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver("jdbc:sqlite::memory:")
        AppDatabase.Schema.create(driver)
        db = AppDatabase(driver)
        repo = UserRepository(db)
    }
    
    @Test
    fun `should insert and retrieve user`() {
        val user = User("1", "John", "john@test.com")
        repo.saveUser(user)
        
        val retrieved = repo.getUser("1")
        assertEquals("John", retrieved?.name)
    }
    
    @AfterTest
    fun teardown() { driver.close() }
}
```

---

## 🟡 Q9: How do you handle encrypted databases in KMP?
**Answer:**

```kotlin
// androidMain — SQLCipher
val driver = AndroidSqliteDriver(
    schema = AppDatabase.Schema,
    context = context,
    name = "app.db",
    passphrase = "mySecretKey".toByteArray()
)

// iosMain — SQLCipher via NativeSqliteDriver
val driver = NativeSqliteDriver(
    schema = AppDatabase.Schema,
    name = "app.db",
    key = "mySecretKey"
)
```

---

## 🟡 Q10: How do you implement key-value storage in KMP?
**Answer:** Use `multiplatform-settings` library:

```kotlin
// build.gradle.kts
implementation("com.russhwolf:multiplatform-settings:1.1.1")
implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")

// commonMain
val settings = Settings()
settings.putString("user_id", "123")
settings.putBoolean("is_logged_in", true)
val userId = settings.getString("user_id", "")
val isLoggedIn = settings.getBoolean("is_logged_in", false)
```

Platform-specific implementations:
- Android: SharedPreferences
- iOS: NSUserDefaults
- JVM: java.util.prefs.Preferences

---

## 🟡 Q11: How do you handle database schema changes?
**Answer:**

```kotlin
// commonMain
object AppDatabaseSchema : SqlDriver.Schema {
    override val version: Int = 2
    
    override fun create(db: SqlDriver) {
        db.execute(null, """
            CREATE TABLE User (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT NOT NULL
            )
        """.trimIndent(), 0)
    }
    
    override fun migrate(db: SqlDriver, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execute(null, "ALTER TABLE User ADD COLUMN avatar_url TEXT", 0)
        }
    }
}
```

---

## 🟡 Q12: How do you use SQLDelight with Ktor for offline sync?
**Answer:**

```kotlin
// commonMain
class SyncRepository(
    private val api: UserApi,
    private val db: AppDatabase
) {
    suspend fun syncUsers() {
        val remoteUsers = api.getAllUsers()
        
        db.transaction {
            remoteUsers.forEach { user ->
                db.userQueries.insert(
                    id = user.id,
                    name = user.name,
                    email = user.email
                )
            }
        }
    }
    
    fun getLocalUsers(): Flow<List<User>> {
        return db.userQueries.selectAll().asFlow().map { it.executeAsList() }
    }
}
```

---

## 📌 Key Takeaways
- **SQLDelight** = SQL-first, stable, standard KMP database
- **Room KMP** = Kotlin-first, experimental, familiar to Android devs
- Use `DatabaseDriverFactory` interface for platform-specific drivers
- `asFlow()` for reactive queries
- `transaction {}` for atomic operations
- `multiplatform-settings` for key-value storage

---

[← Networking](Networking.md) | [Back to README](../README.md) | [Next: DI →](DependencyInjection.md)
