# Extension Functions

## 📖 Explanation

Extension functions allow you to add new functions to existing classes **without modifying their source code** or inheriting from them. This is one of Kotlin's most powerful features.

### Syntax
```kotlin
fun String.isPalindrome(): Boolean {
    return this == this.reversed()
}
```
- `String` is the **receiver type**.
- `this` refers to the receiver object.

### Extension Properties
You can also add properties (computed, not backed by a field).
```kotlin
val String.firstChar: Char
    get() = this[0]
```

### Nullable Receiver
Extensions can be defined on nullable types.
```kotlin
fun String?.orDefault(default: String): String = this ?: default
```

### Extensions on Companion Objects
```kotlin
fun Person.Companion.factory() = Person("Default")
```

### Resolution
Extensions are resolved **statically** at compile time — they don't modify the class. They are essentially syntactic sugar for static utility functions.

### Member vs Extension
If a class has a member function and an extension function with the same signature, the **member always wins**.

---

## 🧪 Code Example

```kotlin
fun main() {
    // Extension function on String
    println("racecar".isPalindrome())   // true
    println("hello".isPalindrome())     // false

    // Extension property
    println("Kotlin".firstChar)          // K

    // Extension on Int
    println(5.squared())                 // 25
    println(10.isEven())                 // true

    // Extension on nullable receiver
    val name: String? = null
    println(name.orDefault("Unknown"))   // Unknown

    // Extension on List
    val numbers = listOf(1, 2, 3, 4, 5)
    println("Second max: ${numbers.secondMax()}")  // 4

    // Extension on StringBuilder
    val sb = StringBuilder()
    sb.appendLine("Hello")
    sb.appendLine("World")
    println(sb.allLines().joinToString(" | "))

    // Extension on generic type
    println(42.printType())              // Int
    println("Hi".printType())           // String
}

// --- String extensions ---
fun String.isPalindrome(): Boolean = this == this.reversed()

val String.firstChar: Char
    get() = this[0]

// --- Int extensions ---
fun Int.squared(): Int = this * this
fun Int.isEven(): Boolean = this % 2 == 0

// --- Nullable extension ---
fun String?.orDefault(default: String): String = this ?: default

// --- List extension ---
fun List<Int>.secondMax(): Int? {
    if (size < 2) return null
    return sortedDescending()[1]
}

// --- StringBuilder extension ---
fun StringBuilder.allLines(): List<String> =
    this.toString().trim().split("\n")

// --- Generic extension ---
fun <T> T.printType(): String = this!!::class.simpleName ?: "Unknown"
```

### Output
```
true
false
K
25
true
Unknown
Second max: 4
Hello | World
Int
String
```

---

## ❓ Interview Questions

1. **What is an extension function and how does it work under the hood?**
   - It adds a function to an existing class without modifying it. Under the hood, it's compiled to a static function that takes the receiver as the first parameter.

2. **Can extension functions override member functions?**
   - No. If a class has a member with the same signature, the member always wins. Extensions are resolved statically.

3. **What are extension properties and can they store state?**
   - Extension properties are computed properties with no backing field. They must have a getter (and setter for `var`). They cannot store state.

4. **Can extension functions be defined on nullable types?**
   - Yes. Use `fun String?.someFunc()` and handle null with `this` inside the function.

5. **Are extension functions polymorphic?**
   - No. They are resolved statically at compile time based on the declared type, not the runtime type.

---

## 🔗 Related Topics
- [OOP](OOP.md)
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
