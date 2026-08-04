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

6. **What is the `copy` function and how does it help with immutability?**
   - Data classes auto-generate a `copy()` function that creates a new instance with some properties changed: `val updated = user.copy(age = 31)`. This is the idiomatic way to modify immutable data objects — instead of mutating, you create a copy with the changes. Benefits: (1) Preserves immutability — the original object is unchanged. (2) Concise — no need to specify all properties. (3) Thread-safe — copies are independent. (4) Works with `val` properties. Example: `data class User(val name: String, val age: Int, val email: String); val user = User("Alice", 30, "a@b.com"); val renamed = user.copy(name = "Bob"); val older = user.copy(age = 31)`. The `copy` function is generated for the primary constructor properties only. Use `copy` for state updates in MVI/MVVM — `state.copy(isLoading = true)`.

7. **What is the difference between `sealed class` and `sealed interface`?**
   - **Sealed class** — an abstract class that restricts its subclasses to the same file or package (Kotlin 1.5+). Subclasses must be in the same compilation unit. Enables exhaustive `when` expressions. Cannot be instantiated directly. **Sealed interface** (Kotlin 1.5+) — same restriction but for interfaces. Allows implementing classes AND other interfaces. Key differences: (1) A class can implement multiple sealed interfaces but extend only one sealed class. (2) Sealed interfaces can have implementing objects (singletons): `sealed interface Result; object Loading : Result; data class Success<T>(val data: T) : Result`. (3) Sealed interfaces work better for modeling state machines with mixed object/class implementations. Use sealed interfaces when you need multiple inheritance or want objects as implementations. Use sealed classes when you need shared state or constructor logic. Both enable exhaustive `when` — the compiler guarantees all cases are covered.

8. **What are the limitations of data classes in Kotlin?**
   - (1) **Must have at least one constructor property**: `data class Empty()` is a compile error. (2) **All primary constructor properties must be `val` or `var`** — no bare parameters. (3) **Cannot be `abstract`, `open`, `sealed`, or `inner`** — data classes are final. (4) **Only generate `equals`/`hashCode`/`toString`/`copy`/`componentN` for primary constructor properties** — properties declared in the body are excluded. (5) **Inheritance is limited** — data classes can extend other classes but cannot be extended (final). (6) **Auto-generated `equals` compares all properties** — can be slow for large objects. (7) **`copy` is shallow** — nested mutable objects are shared, not deep-copied. (8) **No `copy` for body properties** — only constructor properties can be changed via `copy`. If you need any of these features (open, abstract, deep copy), use a regular class.

9. **How do sealed classes work with `when` expressions and why are they powerful?**
   - Sealed classes guarantee exhaustive `when` — the compiler knows ALL possible subtypes, so it can verify every case is handled. Example: `sealed class UiState { object Loading : UiState(); data class Success(val data: List<Item>) : UiState(); data class Error(val message: String) : UiState() }`. In `when(state) { is Loading -> ...; is Success -> ...; is Error -> ... }` — if you add a new state `object Empty : UiState()`, the compiler warns you about unhandled cases in ALL `when` expressions. This prevents runtime crashes from missing cases. Benefits: (1) Type-safe state management — all states are modeled. (2) Refactoring safety — adding/removing states triggers compile errors. (3) No `else` needed — if all cases are handled, `else` is redundant (and discouraged). (4) Smart casts — inside `is Success`, the compiler knows `state.data` exists. Use sealed classes for: UI states, results (Success/Error), navigation events, and finite state machines.

10. **What is the difference between `data class` and `class` in terms of `equals` and `hashCode`?**
    - **Data class**: auto-generates `equals()` and `hashCode()` based on all primary constructor properties. Two data class instances with the same property values are equal: `User("A", 30) == User("A", 30)` → true. **Regular class**: `equals()` defaults to referential equality (`===`) inherited from `Any`. Two instances with the same properties are NOT equal unless you override `equals()`: `class User(val name: String); User("A") == User("A")` → false. Data class `hashCode()` is consistent with `equals()` — equal objects have the same hash code (required by the `hashCode` contract). Data class `toString()` includes the class name and all properties: `User(name=Alice, age=30)`. Regular class `toString()` returns the class name and hash: `User@1b6d3586`. Always use data classes for value objects, DTOs, and models. Use regular classes for services, controllers, and objects with identity.

11. **How do you use data classes with JSON serialization (Moshi/Gson/Kotlinx Serialization)?**
    - Data classes work seamlessly with JSON libraries: (1) **Moshi** — `@JsonClass(generateAdapter = true) data class User(val name: String, @Json(name = "email_address") val email: String)`. Moshi's codegen generates type-safe adapters. Handles nullability correctly. (2) **Gson** — `data class User(val name: String, val email: String)`. Gson uses reflection (slower). Doesn't respect default values for missing fields. (3) **Kotlinx Serialization** — `@Serializable data class User(val name: String, val email: String = "")`. Compile-time safe, multiplatform, handles defaults. Best for new projects. Tips: (1) Use `@SerialName` (Kotlinx) or `@Json` (Moshi) for field name mapping. (2) Use `@SerialName` with default values for optional fields. (3) Use `@Nullable` for nullable fields. (4) For sealed classes, use polymorphic serialization with `@Serializable` and `@SerialName` on each subclass. (5) Always make properties `val` for immutability.

12. **What are destructuring declarations and where are they useful?**
    - Destructuring splits an object into its components: `val (name, age, email) = user`. Data classes auto-generate `component1()`, `component2()`, etc. Use cases: (1) **Multiple return values**: `data class Result(val data: String, val error: Exception?); val (data, error) = fetchResult()`. (2) **Map iteration**: `for ((key, value) in map) { }` — `Map.Entry` supports destructuring. (3) **Lambda parameters**: `map.forEach { (key, value) -> }`. (4) **Loops**: `for ((index, value) in list.withIndex()) { }`. (5) **Pattern matching with when**: `when (val (a, b) = pair) { }`. (6) **Filtering pairs**: `val (evens, odds) = numbers.partition { it % 2 == 0 }`. You can skip components with `_`: `val (name, _) = user`. Destructuring only works for the first 5 properties (component1-component5) by default in data classes — you can manually add `component6()` etc. for more. Non-data classes need to manually declare `operator fun componentN()`.

---

## 🔗 Related Topics
- [OOP](OOP.md)
- [Collections](Collections.md)
