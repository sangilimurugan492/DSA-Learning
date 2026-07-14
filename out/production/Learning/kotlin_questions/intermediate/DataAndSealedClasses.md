# Data Classes & Sealed Classes

## 📖 Explanation

### Data Classes
`data class` automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`, and `componentN()` functions.

```kotlin
data class User(val name: String, val age: Int)
```

**Requirements:**
- Must have at least one property in primary constructor.
- All constructor properties must be `val` or `var`.
- Cannot be `abstract`, `open`, `sealed`, or `inner`.

### Destructuring
Data classes support destructuring via `componentN()` functions.

```kotlin
val (name, age) = user
```

### `copy()`
Creates a copy with some properties changed.

```kotlin
val older = user.copy(age = 31)
```

### Sealed Classes
Sealed classes restrict class hierarchies — all subclasses must be in the same file (or same module in newer Kotlin). Perfect for representing restricted state.

```kotlin
sealed class Result
class Success(val data: String) : Result()
class Error(val message: String) : Result()
```

When used in a `when` expression, the compiler ensures all cases are covered — no `else` needed.

### Sealed Interfaces
Kotlin also supports `sealed interface` with the same restrictions.

---

## 🧪 Code Example

```kotlin
fun main() {
    // Data class
    val user1 = User("Alice", 30)
    val user2 = User("Alice", 30)
    val user3 = User("Bob", 25)

    // Auto-generated equals
    println("user1 == user2: ${user1 == user2}")  // true
    println("user1 == user3: ${user1 == user3}")  // false

    // Auto-generated toString
    println("toString: $user1")

    // Auto-generated copy
    val olderAlice = user1.copy(age = 31)
    println("Copied: $olderAlice")

    // Destructuring
    val (name, age) = user1
    println("Destructured: name=$name, age=$age")

    // Sealed class with when
    val results = listOf(
        Success("Data loaded"),
        Error("Network failure"),
        Loading
    )

    for (result in results) {
        val message = when (result) {
            is Success -> "✅ ${result.data}"
            is Error -> "❌ ${result.message}"
            is Loading -> "⏳ Loading..."
        }
        println(message)
    }

    // Sealed class for UI state
    val state: UiState = Loading
    handleState(state)
    handleState(Success("Content ready"))
    handleState(Error("Timeout"))
}

// --- Data class ---
data class User(val name: String, val age: Int)

// --- Sealed class ---
sealed class Result
class Success(val data: String) : Result()
class Error(val message: String) : Result()
object Loading : Result()

// --- Sealed class for UI state ---
sealed class UiState
object Loading : UiState()
data class Success(val data: String) : UiState()
data class Error(val message: String) : UiState()

fun handleState(state: UiState) {
    when (state) {
        is Loading -> println("State: Loading...")
        is Success -> println("State: Success - ${state.data}")
        is Error -> println("State: Error - ${state.message}")
    }
}
```

### Output
```
user1 == user2: true
user1 == user3: false
toString: User(name=Alice, age=30)
Copied: User(name=Alice, age=31)
Destructured: name=Alice, age=30
✅ Data loaded
❌ Network failure
⏳ Loading...
State: Loading...
State: Success - Content ready
State: Error - Timeout
```

---

## ❓ Interview Questions

1. **What does `data class` auto-generate?**
   - `equals()`, `hashCode()`, `toString()`, `copy()`, and `componentN()` functions for destructuring.

2. **What are the restrictions of a data class?**
   - Must have at least one constructor property, all must be `val`/`var`, cannot be `abstract`/`open`/`sealed`/`inner`.

3. **What is a sealed class and why use it?**
   - A sealed class restricts subclasses to the same file/module. It enables exhaustive `when` expressions — the compiler ensures all cases are handled.

4. **What is the difference between `sealed class` and `abstract class`?**
   - Sealed classes know all subclasses at compile time (same file/module). Abstract classes allow unlimited subclasses anywhere. Sealed enables exhaustive `when`.

5. **What is destructuring and how does it work with data classes?**
   - Destructuring splits an object into its component properties: `val (name, age) = user`. Data classes auto-generate `component1()`, `component2()`, etc.

---

## 🔗 Related Topics
- [OOP](OOP.md)
- [Collections](Collections.md)
