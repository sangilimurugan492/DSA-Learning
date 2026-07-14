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
   - Types are non-nullable by default. Nullable types must be explicitly declared with `?`. The compiler prevents unsafe access to nullable types.

2. **What is the difference between `?.` and `!!`?**
   - `?.` is a safe call — returns null if the object is null. `!!` is a not-null assertion — throws NPE if null.

3. **What is the Elvis operator and when do you use it?**
   - `?:` provides a default/fallback value when the left side is null. E.g., `val len = str?.length ?: 0`.

4. **What does `?.let { }` do?**
   - It executes the lambda block only if the value is non-null, with the value as `it` inside the block.

5. **Can you still get a `NullPointerException` in Kotlin?**
   - Yes, in rare cases: using `!!` on null, uninitialized lateinit var, Java interop, or unsafe casts.

---

## 🔗 Related Topics
- [Functions](Functions.md)
- [String Templates & Operations](StringTemplates.md)
