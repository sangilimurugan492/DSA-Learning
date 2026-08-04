# Generics

## 📖 Explanation

Kotlin generics allow writing type-safe, reusable code. They are similar to Java generics but with more powerful variance features.

### Generic Functions & Classes
```kotlin
fun <T> identity(value: T): T = value

class Box<T>(val item: T)
```

### Variance
Variance controls how subtyping between generic types works.

#### Invariant (default)
`Box<Int>` is NOT a subtype of `Box<Number>`. No substitution allowed.

#### Covariant (`out`)
`Producer<out T>` — can only **produce** (return) `T`, not consume it. `Producer<Int>` IS a subtype of `Producer<Number>`.

```kotlin
class Producer<out T> {
    fun produce(): T  // ✅ T is only in 'out' position
}
```

#### Contravariant (`in`)
`Consumer<in T>` — can only **consume** (accept) `T`, not produce it. `Consumer<Number>` IS a subtype of `Consumer<Int>`.

```kotlin
class Consumer<in T> {
    fun consume(item: T)  // ✅ T is only in 'in' position
}
```

### Declaration-site vs Use-site Variance
- **Declaration-site**: `out`/`in` on the class definition (Kotlin's preferred approach).
- **Use-site**: `out`/`in` at the usage point (Java's wildcards `? extends`/`? super`).

```kotlin
// Use-site
fun copy(from: Array<out Number>, to: Array<in Number>) { ... }
```

### `where` Clauses
Constrain generic type parameters with multiple upper bounds.

```kotlin
fun <T> process(item: T) where T : Comparable<T>, T : CharSequence {
    // T must be both Comparable and CharSequence
}
```

### `reified` Type Parameters
With `inline` functions, type parameters can be accessed at runtime.

```kotlin
inline fun <reified T> typeOf() = T::class.java
```

### Star Projection (`*`)
When you don't care about the type parameter.

```kotlin
fun printAll(list: List<*>) { list.forEach { println(it) } }
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // Generic function
    println(identity(42))
    println(identity("Hello"))

    // Generic class
    val intBox = Box(10)
    val strBox = Box("Kotlin")
    println("Int box: ${intBox.item}, String box: ${strBox.item}")

    // Covariance (out)
    val intProducer: Producer<Int> = Producer(42)
    val numProducer: Producer<Number> = intProducer  // ✅ Covariant
    println("Produced: ${numProducer.produce()}")

    // Contravariance (in)
    val numConsumer: Consumer<Number> = Consumer()
    val intConsumer: Consumer<Int> = numConsumer  // ✅ Contravariant
    intConsumer.consume(5)

    // reified
    println("Type of 42: ${typeOf<Int>()}")
    println("Type of 'Hi': ${typeOf<String>()}")

    // where clause
    println("Max of 'Banana' and 'Apple': ${maxOf("Banana", "Apple")}")

    // Star projection
    val mixed: List<*> = listOf(1, "two", 3.0)
    printAll(mixed)

    // Generic extension with constraints
    val numbers = listOf(3, 1, 4, 1, 5, 9, 2, 6)
    println("Sorted: ${numbers.sortedAscending()}")
    println("Max: ${numbers.findMax()}")
}

// --- Generic function ---
fun <T> identity(value: T): T = value

// --- Generic class ---
class Box<T>(val item: T)

// --- Covariant ---
class Producer<out T>(private val value: T) {
    fun produce(): T = value
}

// --- Contravariant ---
class Consumer<in T> {
    fun consume(item: T) = println("Consumed: $item")
}

// --- reified ---
inline fun <reified T> typeOf() = T::class.simpleName

// --- where clause ---
fun <T> maxOf(a: T, b: T): T where T : Comparable<T> {
    return if (a >= b) a else b
}

// --- Star projection ---
fun printAll(list: List<*>) {
    list.forEach { println("  Item: $it") }
}

// --- Generic extension with constraint ---
fun <T : Comparable<T>> List<T>.sortedAscending(): List<T> = sorted()

fun <T : Comparable<T>> List<T>.findMax(): T? = maxOrNull()
```

### Output
```
42
Hello
Int box: 10, String box: Kotlin
Produced: 42
Consumed: 5
Type of 42: Int
Type of 'Hi': String
Max of 'Banana' and 'Apple': Banana
  Item: 1
  Item: two
  Item: 3.0
Sorted: [1, 1, 2, 3, 4, 5, 6, 9]
Max: 9
```

---

## ❓ Interview Questions

1. **What is the difference between `in` and `out` variance in Kotlin?**
   - `out` (covariant): type can only be produced (returned), not consumed. `Producer<Int>` is a subtype of `Producer<Number>`. `in` (contravariant): type can only be consumed (accepted), not produced. `Consumer<Number>` is a subtype of `Consumer<Int>`.

2. **What is declaration-site vs use-site variance?**
   - Declaration-site: variance is declared on the class itself (`class Box<out T>`). Use-site: variance is specified at the point of use (`Box<out Number>`). Kotlin prefers declaration-site.

3. **What is `reified` and why does it require `inline`?**
   - `reified` makes a type parameter accessible at runtime. It requires `inline` because the compiler copies the function body and replaces `T` with the actual type at each call site.

4. **What is star projection (`*`)?**
   - It represents "unknown type" — like Java's `?`. Used when you don't need to know the type parameter, only that it exists.

5. **What is the difference between `T : Any` and `T : Any?`?**
   - `T : Any` constrains `T` to be non-nullable. `T : Any?` (default) allows nullable types. This is how Kotlin enforces null safety in generics.

6. **What is the difference between `in`, `out`, and `invariant` in Kotlin generics?**
    - **Invariant (default)** — `class Box<T>` — `Box<Int>` is NOT a subtype of `Box<Number>`. No relationship between subtypes. Use when the type is both produced (returned) and consumed (accepted as parameter). **Covariant (`out`)** — `class Producer<out T>` — `Producer<Int>` IS a subtype of `Producer<Number>`. The type can only be **produced** (returned), never consumed. Use `out` when the type only appears in return positions. Example: `class List<out T>` — you can only read from a `List`, not add. **Contravariant (`in`)** — `class Consumer<in T>` — `Consumer<Number>` IS a subtype of `Consumer<Int>`. The type can only be **consumed** (accepted as parameter), never produced. Use `in` when the type only appears in parameter positions. Example: `interface Comparator<in T> { fun compare(a: T, b: T): Int }` — only consumes `T`. Mnemonic: **PECS** (Producer Extends, Consumer Super) — in Kotlin: Producer `out`, Consumer `in`. Declaration-site variance is preferred — specify `in`/`out` on the class definition, not at each use site.

7. **What is type erasure and how does it affect Kotlin generics?**
    - Type erasure means generic type parameters are removed at runtime. `List<Int>` and `List<String>` both become `List` at runtime — you can't check `if (list is List<Int>)` at runtime. This is inherited from the JVM. Effects: (1) Can't do `is T` checks at runtime (unless `reified`). (2) Can't create arrays of generic types: `arrayOf<T>()`. (3) Can't check generic types in `when`: `when (x) { is List<Int> -> }` doesn't work. (4) Two generic types with different type arguments share the same `Class`: `List<Int>::class == List<String>::class`. Workarounds: (1) `reified` type parameters with `inline` functions — makes `T` available at runtime. (2) Pass `Class<T>` explicitly: `fun <T> parse(json: String, clazz: Class<T>): T`. (3) Use `inline fun <reified T> genericType() = object : TypeToken<T>() {}.type` for Gson. (4) `KClass<T>` for runtime type info. Type erasure is a JVM limitation — Kotlin/Native and Kotlin/JS have different erasure behavior.

8. **What are upper bounds and multiple constraints in Kotlin generics?**
    - Upper bound (`T : Bound`) constrains `T` to be a subtype of `Bound`: `fun <T : Number> sum(list: List<T>): Double = list.sumOf { it.toDouble() }`. Only `Number` subtypes (Int, Double, etc.) are allowed. Multiple upper bounds use `where` clause: `fun <T> process(item: T) where T : Comparable<T>, T : Serializable { }` — `T` must be both `Comparable<T>` AND `Serializable`. Default upper bound is `Any?` (nullable). Use `T : Any` to enforce non-null. You can also constrain to multiple interfaces: `fun <T> save(item: T) where T : Entity, T : Serializable { }`. Upper bounds enable calling methods of the bound: `fun <T : CharSequence> printLength(s: T) = println(s.length)` — can call `.length` because `CharSequence` has it. Use upper bounds to restrict types and enable method calls on the type parameter. Without bounds, `T` has no methods except `Any`'s methods.

9. **How do you create a generic reified function to filter a list by type?**
    - `inline fun <reified T> List<Any>.filterByType(): List<T> = filter { it is T } as List<T>`. Usage: `val mixed: List<Any> = listOf(1, "two", 3, "four"); val strings = mixed.filterByType<String>()` → `["two", "four"]`. The `reified` keyword makes `T` available at runtime so `is T` works. This is how Kotlin's `filterIsInstance<T>()` is implemented in the standard library. Other reified patterns: (1) `inline fun <reified T> Any.cast(): T = this as T`. (2) `inline fun <reified T : Activity> Context.startActivity() = startActivity(Intent(this, T::class.java))`. (3) `inline fun <reified T : Parcelable> Intent.extra(key: String): T? = getParcelableExtra(key)`. (4) `inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)`. Reified eliminates the need to pass `Class<T>` — the compiler substitutes the actual type at each call site.

10. **What is star projection (`*`) and when do you use it?**
    - Star projection (`*`) represents an "unknown type" — similar to Java's `?`. Use when you don't care about the specific type parameter, only that it exists. `List<*>` is a list of "something" — you can read from it but the type is `Any?`. You can't add to it (contravariant position). Example: `fun printSize(list: List<*>) = println(list.size)` — works for `List<Int>`, `List<String>`, etc. Star projection rules: (1) `Producer<out T>` → `Producer<*>` is equivalent to `Producer<out Any?>` — can read, type is `Any?`. (2) `Consumer<in T>` → `Consumer<*>` is equivalent to `Consumer<in Nothing>` — can't consume. (3) `Invariant<T>` → `Invariant<*>` — can read as `Any?`, can't write. Use star projection when: (1) The type parameter doesn't matter (only size, structure). (2) Raw types from Java. (3) Type erasure prevents specific checks. Prefer specific type parameters when possible — star projection loses type information.

11. **How does Kotlin generics interoperate with Java generics?**
    - Kotlin and Java generics both use type erasure on the JVM, so interoperability is generally smooth. Key differences: (1) **Declaration-site variance** — Kotlin uses `in`/`out` on the class; Java uses wildcards (`? extends`/`? super`) at use sites. Kotlin's `class Producer<out T>` maps to Java's `Producer<? extends T>` at use sites. (2) **No raw types** — Java allows raw types (`List`), Kotlin doesn't — you must specify the type parameter or use `*`. (3) **`@JvmSuppressWildcards` and `@JvmWildcard`** — control how Kotlin generates Java wildcards. By default, Kotlin generates wildcards for `in`/`out` parameters. Use `@JvmSuppressWildcards` to suppress. (4) **Reified** — not callable from Java (inline is a Kotlin-only feature). Java callers must pass `Class<T>`. (5) **Nullability** — Java generics don't have nullability. `MutableList<String>` from Kotlin is `List<String>` in Java — no platform type warnings. (6) **Arrays** — Java arrays are covariant and reified; Kotlin arrays are invariant. Use `Array<out T>` for covariance.

12. **What are the best practices for using generics in Kotlin?**
    - (1) **Use declaration-site variance** — mark `out` for producers, `in` for consumers. This simplifies use sites — no need for wildcards everywhere. (2) **Use `T : Any` for non-nullable** — prevents nullable type arguments. (3) **Use `reified` for type-safe APIs** — eliminates `Class<T>` parameters. (4) **Prefer interfaces over concrete types** for generic bounds: `T : Comparable<T>` not `T : Int`. (5) **Don't over-genericize** — if a function works with `String`, don't make it `<T : CharSequence>`. (6) **Use `where` for multiple bounds** — cleaner than nested generics. (7) **Avoid raw types** — always specify type parameters or use `*`. (8) **Use `typealias` for complex generic types** — `typealias StringMap = Map<String, String>`. (9) **Be careful with variance** — wrong variance causes compile errors or runtime issues. (10) **Test with edge cases** — null types, subtypes, mixed lists. (11) **Document generic constraints** — explain why `T : Bound` is required. (12) **Consider inline + reified** for performance-sensitive generic code — avoids boxing and enables type checks.

---

## 🔗 Related Topics
- [OOP](../intermediate/OOP.md)
- [Delegated Properties](DelegatedProperties.md)
