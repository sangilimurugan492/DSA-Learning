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

---

## 🔗 Related Topics
- [OOP](../intermediate/OOP.md)
- [Delegated Properties](DelegatedProperties.md)
