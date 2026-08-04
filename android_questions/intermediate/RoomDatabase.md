# Room Database

## 📖 Explanation

Room is a Jetpack persistence library that provides an abstraction layer over SQLite. It's the recommended way to store structured data locally.

### Key Components
| Component       | Description                                          |
|-----------------|------------------------------------------------------|
| `@Entity`        | Represents a table — class is a row                  |
| `@Dao`           | Data Access Object — SQL queries as Kotlin functions|
| `@Database`      | Database holder — connects entities and DAOs         |
| `@PrimaryKey`    | Marks a column as primary key                        |
| `@ColumnInfo`    | Customizes column name                               |
| `@Embedded`      | Embeds another object in the table                   |
| `@Relation`      | Defines relationships between entities               |

### Entity
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_name") val name: String,
    val email: String,
    val age: Int
)
```

### DAO (Data Access Object)
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long)
}
```

### Database
```kotlin
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

### Type Converters
For storing complex types (Date, enums, lists).

```kotlin
class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}
```

### Migration
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
    }
}
```

### Reactive Queries
- `Flow<List<T>>` — Emits on data changes.
- `LiveData<T>` — Lifecycle-aware.
- `suspend fun` — Coroutine-friendly.

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.content.Context
import androidx.room.*
import androidx.room.Database
import kotlinx.coroutines.flow.Flow

// --- Entity ---
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_name") val name: String,
    val email: String,
    val age: Int,
    val createdAt: Long = System.currentTimeMillis()
)

// --- DAO ---
@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY user_name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE age >= :minAge ORDER BY age DESC")
    fun getUsersOlderThan(minAge: Int): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Insert
    suspend fun insertAll(users: List<User>): List<Long>

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

// --- Database ---
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository ---
class UserRepository(private val dao: UserDao) {

    fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()

    fun getUsersOlderThan(minAge: Int): Flow<List<User>> = dao.getUsersOlderThan(minAge)

    suspend fun addUser(name: String, email: String, age: Int): Long {
        return dao.insert(User(name = name, email = email, age = age))
    }

    suspend fun updateUser(user: User) = dao.update(user)

    suspend fun deleteUser(user: User) = dao.delete(user)

    suspend fun count(): Int = dao.count()
}

// --- ViewModel ---
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val allUsers: Flow<List<User>> = repository.getAllUsers()

    fun addUser(name: String, email: String, age: Int) {
        viewModelScope.launch {
            repository.addUser(name, email, age)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
}

// --- Activity ---
class UserActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val database = AppDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(repository) as T
                }
            }
        )[UserViewModel::class.java]

        adapter = UserAdapter()
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@UserActivity)
            adapter = this@UserActivity.adapter
        }

        // Collect Flow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allUsers.collect { users ->
                    adapter.submitList(users)
                }
            }
        }

        // Add user
        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            viewModel.addUser("Alice", "alice@example.com", 30)
        }
    }
}
```

```groovy
// build.gradle dependencies
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
```

---

## ❓ Interview Questions

1. **What is Room and why use it over raw SQLite?**
   - Room provides compile-time SQL verification, type-safe queries, reactive support (Flow/LiveData), and coroutine integration. Raw SQLite uses string queries with no validation and requires manual mapping.

2. **What is the difference between `@Insert(onConflict = REPLACE)` and `IGNORE`?**
   - `REPLACE` deletes the existing row and inserts the new one when there's a primary key conflict. `IGNORE` keeps the existing row and discards the new one.

3. **How do you handle database migrations in Room?**
   - Create `Migration` objects with `migrate()` that execute `ALTER TABLE` SQL. Pass them to `Room.databaseBuilder().addMigrations(migration1to2)`. Use `fallbackToDestructiveMigration()` for development only.

4. **What is the difference between returning `Flow` and `suspend fun` from a DAO?**
   - `Flow` is reactive — emits whenever the table changes. `suspend fun` is one-shot — returns the result once. Use `Flow` for live UI updates, `suspend` for one-time queries.

5. **What are Type Converters in Room?**
   - They convert custom types (Date, enum, list) to/from types Room can store (Long, String). Annotate with `@TypeConverter` and register with `@TypeConverters` on the database.

6. **What is the difference between `fallbackToDestructiveMigration` and `addMigrations`?**
   - `fallbackToDestructiveMigration()` drops the entire database and recreates it when the version changes. All data is **lost** — use only in development. `addMigrations(MIGRATION_1_2, MIGRATION_2_3)` applies specific SQL `ALTER TABLE` statements to transform the schema without data loss. In production, always use explicit migrations. A `Migration` object specifies `startVersion` and `endVersion` and overrides `migrate(database: SupportSQLiteDatabase)` to execute SQL. You can chain multiple migrations. If no migration is found and destructive migration is not enabled, Room throws `IllegalStateException`. Best practice: test migrations with `MigrationTestHelper` in instrumented tests.

7. **How do you implement one-to-many and many-to-many relationships in Room?**
   - **One-to-many**: Use `@Relation` in a POJO that combines the parent entity with a list of child entities. Example: `data class UserWithPosts(@Embedded val user: User, @Relation(parentColumn = "id", entityColumn = "userId") val posts: List<Post>)`. Query: `@Transaction @Query("SELECT * FROM users") fun getUsersWithPosts(): Flow<List<UserWithPosts>>`. The `@Transaction` annotation is required because Room runs two queries. **Many-to-many**: Create a junction (associative) table with `@Junction`. Example: `data class PlaylistWithSongs(@Embedded val playlist: Playlist, @Relation(parentColumn = "id", entityColumn = "id", associateBy = Junction(PlaylistSongCrossRef::class, parentColumn = "playlistId", entityColumn = "songId")) val songs: List<Song>)`. The `PlaylistSongCrossRef` is an entity with composite primary key `(playlistId, songId)`.

8. **What is `@Embedded` and when would you use it?**
   - `@Embedded` flattens an object's fields into the parent entity's table — no separate table is created. Example: `@Entity data class User(@Embedded val address: Address)` — `Address` fields (`street`, `city`, `zip`) become columns in the `users` table. Use when the nested object is always loaded with the parent and has no independent identity. For `@Embedded` with name conflicts, use `prefix`: `@Embedded(prefix = "home_") val homeAddress: Address`. Unlike `@Relation` (which creates separate tables and requires `@Transaction`), `@Embedded` is a single-table operation — faster and simpler. Don't use `@Embedded` for lists — use `@Relation` instead.

9. **How do you use Room with coroutines and Flow for reactive queries?**
   - Room supports three reactive return types: (1) `Flow<List<T>>` — emits whenever the table changes (insert/update/delete triggers a new emission). Use for live UI updates. (2) `suspend fun` — one-shot coroutine call, returns once. Use for single fetches. (3) `LiveData<T>` — lifecycle-aware, notifies active observers. Flow is preferred for new Kotlin projects. Example: `@Query("SELECT * FROM users") fun getAllUsers(): Flow<List<User>>`. The Flow is cold — it starts emitting when collected. Use `repeatOnLifecycle(STARTED) { flow.collect { ... } }` in the UI to collect lifecycle-safely. Room handles thread scheduling automatically — no need for `withContext(Dispatchers.IO)`. For one-shot + reactive combo, use `flatMapLatest` to switch queries.

10. **What is the difference between `@Query`, `@RawQuery`, and `@Insert`/`@Update`/`@Delete`?**
    - `@Query` — executes a SQL query with compile-time validation. Room verifies column names, table names, and return types. Supports `:parameter` substitution. Most commonly used annotation. `@RawQuery` — executes a raw SQL string at runtime with no compile-time validation. Use when the query is dynamic (e.g., dynamic WHERE clauses). Must use `SupportSQLiteQuery` and pass arguments separately. Less safe — typos cause runtime crashes. `@Insert`/`@Update`/`@Delete` — convenience annotations that generate SQL automatically. `@Insert` supports `onConflict` strategy (REPLACE, IGNORE, ABORT). `@Update` matches by primary key. `@Delete` also matches by primary key. These don't require writing SQL — simpler and less error-prone for basic CRUD. Prefer `@Query` for complex queries with JOINs, WHERE clauses, or aggregations.

11. **How do you test Room database in instrumented tests?**
    - Use `Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()` — creates a database in memory (destroyed after test, no persistence). Test DAO methods directly with `runTest`. Benefits: (1) Tests real SQLite — catches SQL errors. (2) Fast — in-memory is faster than disk. (3) Isolated — each test gets a fresh database. For migration testing, use `MigrationTestHelper` — it creates the old schema, applies the migration, and validates the result. For coroutines, use `runTest`. Always close the database in `@After`. Example: `@After fun teardown() { database.close() }`. Test edge cases: empty tables, large datasets, concurrent access, and conflict strategies. Use `assertNotNull` and `assertEquals` to verify query results.

12. **What are Room's best practices for performance?**
    - (1) Use `@Transaction` on queries with `@Relation` or multiple queries — ensures atomicity and consistency. (2) Use `Flow` for reactive queries — only emit when data changes. (3) Add indexes on frequently queried columns: `@Entity(indices = [Index("email", unique = true)])`. (4) Use `@Query` with specific columns instead of `SELECT *` to reduce memory. (5) Use `LIMIT` for pagination instead of loading all rows. (6) Use `@Insert(onConflict = REPLACE)` for upsert operations. (7) Batch operations: `@Insert fun insertAll(users: List<User>)` instead of individual inserts. (8) Use `suspend` or `Flow` — Room handles threading. (9) Avoid `fallbackToDestructiveMigration` in production. (10) Pre-populate with `Room.databaseBuilder().addCallback()` or `createFromAsset()`. (11) Use `@Embedded` for nested objects instead of JOINs when possible.

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [ViewModel & LiveData](ViewModelLiveData.md)
