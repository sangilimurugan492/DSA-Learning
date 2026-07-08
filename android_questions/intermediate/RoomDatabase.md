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

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [ViewModel & LiveData](ViewModelLiveData.md)
