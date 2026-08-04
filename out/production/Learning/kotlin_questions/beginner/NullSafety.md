# Null Safety

## 📖 Explanation

Null safety is one of Kotlin's signature features. It eliminates `NullPointerException` (NPE) at compile time by distinguishing nullable and non-nullable types.

### Nullable Types
By default, types are non-nullable. Add `?` to allow null.

```kotlin
var name: String = "Alice"      // Non-nullable — cannot hold null
var nickname: String? = null     // Nullable — can hold null
```

### Safe Call Operator `?.`
Returns `null` if the object is null, instead of throwing NPE.

```kotlin
val length = nickname?.length    // Returns null if nickname is null
```

### Elvis Operator `?:`
Provides a default value when the left side is null.

```kotlin
val length = nickname?.length ?: 0   // Returns 0 if nickname is null
```

### Not-Null Assertion `!!`
Forces a nullable type to non-nullable. Throws NPE if null. **Use with caution.**

```kotlin
val length = nickname!!.length   // Throws NPE if nickname is null
```

### Safe Cast `as?`
Returns null if the cast fails.

```kotlin
val num = obj as? Int   // null if obj is not an Int
```

### `let` for Null Checks
Executes a block only if the value is non-null.

```kotlin
nickname?.let {
    println("Nickname length: ${it.length}")
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // Nullable vs non-nullable
    val name: String = "Kotlin"
    var nickname: String? = null

    println("Name length: ${name.length}")

    // Safe call
    println("Nickname length (safe): ${nickname?.length}")

    // Elvis operator
    val len = nickname?.length ?: 0
    println("Nickname length (elvis): $len")

    // let for null check
    nickname?.let {
        println("Nickname is: $it")
    }

    // Assign a value and try again
    nickname = "Kot"
    println("Nickname length now: ${nickname?.length}")

    // Safe cast
    val obj: Any = "Hello"
    val num: Int? = obj as? Int
    val str: String? = obj as? String
    println("Safe cast to Int: $num")
    println("Safe cast to String: $str")

    // Chain of safe calls
    val person: Person? = Person("Alice", null)
    val city = person?.address?.city
    println("City: $city")
}

data class Person(val name: String, val address: Address?)
data class Address(val city: String)
```

### Output
```
Name length: 6
Nickname length (safe): null
Nickname length (elvis): 0
Nickname length now: 3
Safe cast to Int: null
Safe cast to String: Hello
City: null
```

---

## ❓ Interview Questions

1. **How does Kotlin ensure null safety at compile time?**
   - Kotlin's type system distinguishes nullable and non-nullable types at the language level. By default, all types are **non-nullable** — `var name: String = "Alice"` cannot hold `null`. To allow null, you must explicitly declare it as nullable with `?`: `var name: String? = null`. The compiler then **prevents unsafe access** to nullable types — you can't call `name.length` on a `String?` without null-checking first. This eliminates `NullPointerException` at compile time rather than runtime. The compiler tracks nullability through the code: after `if (name != null) { name.length }`, `name` is smart-cast to `String` (non-nullable). This is Kotlin's most famous feature — it eliminates the "billion-dollar mistake" (Tony Hoare's term for null references).

2. **What is the difference between `?.` and `!!`?**
   - `?.` (safe call operator) — safely accesses a property/method on a nullable type. If the object is null, it returns `null` instead of throwing NPE. Example: `name?.length` returns `Int?` (null if `name` is null). Can be chained: `person?.address?.city` — returns null at any point in the chain. `!!` (not-null assertion operator) — **forces** a nullable type to non-nullable, throwing `NullPointerException` if it's actually null. Example: `name!!.length` throws NPE if `name` is null. Use `?.` in almost all cases. Use `!!` only when you're 100% certain the value is non-null and the compiler can't prove it (e.g., after a Java interop call). `!!` is a code smell — prefer `?:`, `?.let {}`, or explicit null checks.

3. **What is the Elvis operator and when do you use it?**
   - The Elvis operator `?:` provides a default/fallback value when the left side is null. Named after Elvis Presley's hairstyle (the `?` looks like his hair). Example: `val len = str?.length ?: 0` — if `str` is null, `len` is 0. If `str` is non-null, `len` is `str.length`. The right side can be any expression: `val name = input ?: "default"`, `val result = cache[key] ?: fetchFromNetwork()`. You can also use it for early returns: `val data = repository.get() ?: return` or `val user = findUser() ?: throw NotFoundException()`. The Elvis operator converts a nullable type to non-nullable — after `val len = str?.length ?: 0`, `len` is `Int` (not `Int?`). This is the preferred way to handle nulls — always provide a meaningful default.

4. **What does `?.let { }` do?**
   - `?.let { }` executes the lambda block **only if the value is non-null**, with the value available as `it` inside the block. Example: `name?.let { println("Length: ${it.length}") }` — the block runs only if `name` is not null, and `it` is smart-cast to `String` (non-nullable). This is useful for: (1) Executing side effects only when non-null. (2) Scoping a nullable variable to a non-null context. (3) Transforming nullable values: `val len = name?.let { it.length } ?: 0`. You can rename `it` for clarity: `name?.let { nonNullName -> println(nonNullName) }`. Avoid nesting `let` calls — use `?.` chaining or `when` instead. `let` is a scope function — it creates a new scope with the value as the context.

5. **Can you still get a `NullPointerException` in Kotlin?**
   - Yes, NPEs are still possible in these cases: (1) Using `!!` on a null value — `null!!` throws NPE. (2) **Uninitialized `lateinit var`** — accessing before initialization throws `UninitializedPropertyAccessException` (a subclass of NPE). (3) **Java interop** — Java code can pass null to Kotlin non-nullable parameters (platform types). (4) **Unsafe casts** — `null as String` throws NPE. (5) **Concurrent modification** — a var checked for null and then accessed might be set to null by another thread between check and access. (6) **External initialization** — `onCreate` in Android where views are null before `setContentView`. Kotlin reduces NPEs dramatically but doesn't eliminate them entirely — always be cautious with Java interop and `lateinit`.

6. **What are platform types in Kotlin-Java interop?**
   - When calling Java code from Kotlin, the return type is a **platform type** — Kotlin doesn't know if it's nullable or not. Example: `String` from Java is `String!` (platform type) in Kotlin — it could be null or non-null. Platform types are denoted with `!` in error messages. You can treat them as nullable or non-nullable, but if you treat as non-nullable and Java returns null, you get an NPE at runtime. Best practice: (1) Add `@Nullable`/`@NonNull` annotations to Java code. (2) Treat all Java return values as nullable in Kotlin. (3) Use `?:` or `?.` when accessing Java results. Example: `val name: String? = javaObject.getName()`. Platform types exist because Java's type system doesn't have null-safety.

7. **What is the difference between `safe call` (`?.`) chaining and multiple `if` checks?**
   - `?.` chaining is more concise and readable: `person?.address?.city?.length` — returns null if any link is null. The equivalent with `if` checks: `if (person != null && person.address != null && person.address.city != null) { person.address.city.length } else null`. The `?.` chain is: (1) More concise — one line vs many. (2) More readable — clear intent. (3) Thread-safe within the expression — each `?.` checks null at that point. (4) Returns a nullable type — the result type is `Int?`. Use `?.` chaining for property access. Use explicit `if` checks when you need to execute different logic for null vs non-null cases.

8. **What is `requireNotNull` and `checkNotNull` in Kotlin?**
   - `requireNotNull(value)` throws `IllegalArgumentException` if the value is null — used for validating function arguments. Returns the non-null value. Example: `fun process(name: String?) { val n = requireNotNull(name) { "name must not be null" } }`. `checkNotNull(value)` throws `IllegalStateException` if the value is null — used for checking internal state/invariants. Example: `val db = checkNotNull(database) { "Database not initialized" }`. Both smart-cast the value to non-nullable after the call. Use `requireNotNull` for preconditions (input validation). Use `checkNotNull` for invariants (internal state). These are cleaner than `!!` because they throw meaningful exceptions with custom messages.

9. **What is the difference between `lateinit` and nullable types for deferred initialization?**
   - `lateinit var` — the property is non-nullable but initialized later. No null checks needed after initialization. Throws `UninitializedPropertyAccessException` if accessed before init. Can't be used with primitives or `val`. Best for Android views, DI, and lifecycle-based init. `var x: String? = null` — the property is nullable. Requires null checks (`?.`, `?:`, `!!`) on every access. Can be used with any type including primitives. Best when null is a valid state. Use `lateinit` when the value will definitely be initialized before use and you want to avoid null checks. Use nullable when null is a meaningful state (e.g., "no value yet" or "not applicable"). `lateinit` is more ergonomic but less safe — it defers the error to runtime.

10. **What is `Delegates.notNull` and how does it differ from `lateinit`?**
    - `Delegates.notNull<T>()` is a property delegate that defers initialization — similar to `lateinit` but works with any type including primitives. Example: `var count: Int by Delegates.notNull()`. Accessing before initialization throws `IllegalStateException`. Differences from `lateinit`: (1) `Delegates.notNull` works with primitive types (`Int`, `Long`) — `lateinit` doesn't. (2) `Delegates.notNull` uses property delegation (more overhead). (3) `lateinit` has `::property.isInitialized` check — `Delegates.notNull` doesn't. (4) `lateinit` is more efficient (no delegate overhead). Use `lateinit` for objects. Use `Delegates.notNull` for primitives. Use `lazy` when the value can be computed once.

11. **What is the `?.run` scope function and how does it differ from `?.let`?**
    - `?.run { }` executes the block only if non-null, with `this` as the receiver (not `it`). Example: `name?.run { println("Length: $length") }` — inside the block, `this` is `name` (non-null), so you access `length` directly. `?.let { }` uses `it` as the parameter: `name?.let { println("Length: ${it.length}") }`. Use `run` when you want to call multiple methods on the object (receiver style): `user?.run { updateName(); save(); notify() }`. Use `let` when you want to rename the parameter or when the block doesn't primarily operate on the object. Both are null-safe — the block only runs if non-null.

12. **How do you handle nullable types in collections?**
     - Collections can contain nullable elements: `List<String?>` — a list where each element may be null. Common operations: (1) `filterNotNull()` — removes null elements, returns `List<String>` (non-nullable). (2) `mapNotNull { }` — maps and filters nulls in one pass: `list.mapNotNull { it?.toIntOrNull() }`. (3) `filter { it != null }` — keeps non-null but type is still `List<String?>` (use `filterNotNull()` instead). (4) `firstOrNull()` / `lastOrNull()` — returns null if empty instead of throwing. (5) `orEmpty()` — converts null to empty collection: `nullableList.orEmpty()`. Always use `filterNotNull()` to convert `List<T?>` to `List<T>` — it's the idiomatic way to remove nulls.

13. **What is the null safety interop between Kotlin and Java?**
    - Java doesn't have null safety in its type system. When calling Java code from Kotlin, return types are **platform types** (`Type!`) — Kotlin doesn't know if they're nullable. You can assign them to either nullable or non-nullable Kotlin types, but if you choose non-nullable and Java returns null, you get an NPE at runtime. Best practices: (1) Add `@Nullable`/`@NotNull` annotations (JSR-305, AndroidX) to Java code so Kotlin respects them. (2) Treat all unannotated Java return values as nullable: `val name: String? = javaObject.getName()`. (3) Use `?:` when accessing Java results. (4) Kotlin to Java calls are safe — Kotlin non-nullable types are enforced at compile time.

14. **What is the difference between `lateinit`, `lazy`, and `Delegates.notNull`?**
    - `lateinit var` — for `var` (mutable) object types only, initialized externally before use. No null checks after init. Throws `UninitializedPropertyAccessException` if accessed before init. Check with `::prop.isInitialized`. `lazy { }` — for `val` (immutable), computed once on first access, thread-safe by default. The value is provided by a lambda. `Delegates.notNull<T>()` — for `var` of any type (including primitives), initialized externally. Throws `IllegalStateException` if accessed before init. No `isInitialized` check. Use `lateinit` for objects set in lifecycle callbacks (Android views, DI). Use `lazy` for expensive one-time computations. Use `Delegates.notNull` for primitive `var` types. Prefer `lazy` when possible — it's the safest and most self-contained.

15. **What is `Contracts` in Kotlin and how does it help with null safety?**
    - Kotlin's contract system (experimental) allows functions to declare effects that the compiler uses for smart casts. For example, `requireNotNull(value)` uses a contract to tell the compiler that after the call, `value` is non-nullable. Without contracts, the compiler wouldn't know that `requireNotNull` throws if null. Example: `fun process(name: String?) { contract { returns() implies (name != null) }; println(name.length) }`. Built-in functions using contracts: `requireNotNull`, `checkNotNull`, `assertTrue`, `assertSame`. Custom contracts use `contract { }` block. This enables smart casts after function calls that guarantee non-null. Note: contracts are still experimental (Kotlin 1.x) — the API may change. Use built-in contract functions (`requireNotNull`) which are stable.

---

## 🔗 Related Topics
- [Functions](Functions.md)
- [String Templates & Operations](StringTemplates.md)
