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

6. **What are extension functions on nullable receivers and how do they work?**
   - Kotlin allows defining extension functions on nullable types: `fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank()`. Inside the function, `this` is nullable — you must handle null. This is how `isNullOrEmpty()` and `isNullOrBlank()` are implemented in the Kotlin standard library. The benefit is that you can call the extension directly on a nullable variable without `?.`: `val name: String? = null; if (name.isNullOrBlank()) { }` — no `?.` needed because the extension accepts nullable. Without nullable extensions, you'd need: `if (name == null || name.isBlank()) { }`. This pattern is used for null-safe utilities. Another example: `fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()` — returns the list or an empty list if null. Nullable extensions are powerful for fluent null handling.

7. **What is the difference between extension functions and member functions?**
   - **Member functions**: (1) Defined inside the class. (2) Can access private members. (3) Polymorphic — resolved at runtime (virtual dispatch). (4) Can be overridden in subclasses. (5) Require modifying the class source code. **Extension functions**: (1) Defined outside the class. (2) Cannot access private members — only public API. (3) Static — resolved at compile time based on declared type. (4) Cannot be overridden. (5) Can be added to any class (including third-party classes like String, List). If a class has both a member function and an extension function with the same signature, the **member always wins**. Extensions are syntactic sugar — they compile to static functions. Use members for core class behavior, extensions for utility functions and third-party class enhancements. Extensions don't break encapsulation — they can only use the public API.

8. **How do extension properties work and what are their limitations?**
   - Extension properties add a property syntax to existing classes: `val String.isVowel: Boolean get() = this.lowercase() in "aeiou"`. Usage: `"hello".isVowel` → false. Limitations: (1) **No backing field** — extension properties cannot store state. They must be computed from other values. (2) **No initialization** — no `= value` initializer. (3) **Must have a getter** — `val` requires a getter, `var` requires both getter and setter. (4) **Setter must use existing APIs** — `var StringBuilder.lastChar: Char get() = get(length - 1); set(value) { setCharAt(length - 1, value) }`. (5) **Cannot be `open` or `override`** — same static resolution as extension functions. Use extension properties for computed values (like `isVowel`, `halfOpenRange`) that logically belong to the type but don't need stored state. For stored state, use a map or a wrapper class.

9. **What is scope resolution for extension functions and how do you resolve conflicts?**
   - Extension functions are resolved by **import scope**. If two libraries define the same extension function (same receiver + same signature), there's a conflict. Resolution: (1) **Import specific extension**: `import com.lib1.specialFunc` — explicitly import the one you want. (2) **Fully qualify**: `com.lib1.specialFunc(receiver)` — call without import. (3) **Alias imports**: `import com.lib1.specialFunc as lib1Special` — rename to avoid conflict. (4) **Member wins over extension** — if the class has a member with the same signature, the member is used. Extension resolution order: (1) Member functions in the class and its superclasses. (2) Extension functions in the same file. (3) Extension functions in the same package. (4) Explicitly imported extensions. (5) Star-imported extensions. Always import extensions explicitly (not star imports) to avoid unexpected conflicts and improve IDE navigation.

10. **How do you create extension functions for generic types?**
    - Extension functions support type parameters: `fun <T> List<T>.secondOrNull(): T? = if (size >= 2) get(1) else null`. Usage: `listOf(1, 2, 3).secondOrNull()` → 2. You can add constraints: `fun <T : Comparable<T>> List<T>.sortedRange(): List<T> = sorted()` — only works for lists of Comparable elements. You can also use reified type parameters with inline extension functions: `inline fun <reified T> List<Any>.filterByType(): List<T> = filter { it is T }` — `filterByType<String>()` returns only String elements. Generic extensions are used throughout the Kotlin standard library: `List<T>.map`, `List<T>.filter`, `Iterable<T>.joinToString`. You can also extend generic types with specific type arguments: `fun List<String>.toCommaSeparated() = joinToString(", ")` — only available on `List<String>`.

11. **What are extension functions with lambdas with receivers and how are they used in DSLs?**
    - Extension functions that accept a lambda with receiver are the building blocks of Kotlin DSLs: `fun <T> buildList(block: MutableList<T>.() -> Unit): List<T> = mutableListOf<T>().apply(block)`. The `block` is `MutableList<T>.() -> Unit` — inside the block, `this` is a `MutableList<T>`. This pattern allows creating DSL-like APIs: `fun html(block: Html.() -> Unit): Html = Html().apply(block)`. The `apply` function calls `block(this)` so `this` is the Html object. Inside the block, you can call any Html method directly: `html { head { }; body { p("text") } }`. This is how Kotlin HTML builders, Gradle build scripts, and testing frameworks work. The receiver lambda gives the block access to the object's API without qualification, creating a natural DSL syntax. `@DslMarker` prevents scope ambiguity in nested DSLs.

12. **Can extension functions cause memory leaks or performance issues?**
    - Extension functions are compiled to **static functions** — they don't add any runtime overhead or memory cost. The receiver is passed as the first parameter: `fun String.myExtension()` compiles to `static void myExtension(String receiver)`. No object allocation, no virtual dispatch, no memory leak. However: (1) **Extension lambdas** — if you store an extension function as a lambda (`val ext = String::myExtension`), the function reference is an object. (2) **Inline extensions** — if not `inline`, passing extension functions as parameters creates function objects. (3) **Extension on inner classes** — capturing the outer class instance may cause leaks if the extension is stored long-term. (4) **Performance** — extensions are as fast as regular static functions. No performance concern. (5) **APK size** — adding many extensions increases code size marginally. The key point: extension functions are zero-cost abstractions at runtime — they're just static functions with syntactic sugar.

---

## 🔗 Related Topics
- [OOP](OOP.md)
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
