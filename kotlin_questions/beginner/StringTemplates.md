# String Templates & Operations

## 📖 Explanation

Kotlin strings are powerful with built-in template expressions and rich utility functions.

### String Templates
Use `$` to embed variables and `${}` for expressions inside strings.

```kotlin
val name = "Alice"
val age = 30
println("My name is $name and I am $age years old")
println("Next year I will be ${age + 1}")
```

### Raw Strings (Triple-Quoted)
Triple-quoted strings preserve formatting and can contain multi-line text without escape sequences.

```kotlin
val text = """
    Hello,
    World!
""".trimIndent()
```

### String Comparison
In Kotlin, `==` checks structural equality (like Java's `.equals()`). `===` checks referential equality.

```kotlin
val a = "Kotlin"
val b = "Kotlin"
println(a == b)   // true (structural)
println(a === b)  // true (same reference due to string pool)
```

### Useful String Functions
| Function         | Description                              |
|------------------|------------------------------------------|
| `length`         | Length of string                         |
| `uppercase()`    | Converts to uppercase                    |
| `lowercase()`    | Converts to lowercase                    |
| `trim()`         | Removes leading/trailing whitespace      |
| `split()`        | Splits string by delimiter               |
| `replace()`      | Replaces matching substrings             |
| `substring()`    | Extracts a portion                       |
| `contains()`     | Checks if substring exists               |
| `startsWith()`   | Checks prefix                             |
| `endsWith()`     | Checks suffix                             |
| `isEmpty()`      | Checks if length is 0                    |
| `isBlank()`      | Checks if blank (only whitespace)       |

---

## 🧪 Code Example

```kotlin
fun main() {
    // String templates
    val name = "Kotlin"
    val version = 2.0
    println("Welcome to $name $version!")
    println("Length of name: ${name.length}")

    // Expression in template
    val x = 10
    val y = 20
    println("$x + $y = ${x + y}")

    // Multi-line raw string
    val json = """
        {
            "name": "Alice",
            "age": 30
        }
    """.trimIndent()
    println("JSON:\n$json")

    // String operations
    val sentence = "Hello Kotlin World"
    println("Uppercase: ${sentence.uppercase()}")
    println("Lowercase: ${sentence.lowercase()}")
    println("Words: ${sentence.split(" ")}")
    println("Contains 'Kotlin': ${sentence.contains("Kotlin")}")
    println("Starts with 'Hello': ${sentence.startsWith("Hello")}")
    println("Replace: ${sentence.replace("Kotlin", "Java")}")
    println("Substring: ${sentence.substring(6, 12)}")

    // String comparison
    val s1 = "test"
    val s2 = "test"
    println("s1 == s2: ${s1 == s2}")       // structural equality
    println("s1 === s2: ${s1 === s2}")     // referential equality

    // Build string
    val sb = buildString {
        append("Item 1\n")
        append("Item 2\n")
        append("Item 3")
    }
    println("Built string:\n$sb")
}
```

### Output
```
Welcome to Kotlin 2.0!
Length of name: 6
10 + 20 = 30
JSON:
{
    "name": "Alice",
    "age": 30
}
Uppercase: HELLO KOTLIN WORLD
Lowercase: hello kotlin world
Words: [Hello, Kotlin, World]
Contains 'Kotlin': true
Starts with 'Hello': true
Replace: Hello Java World
Substring: Kotlin
s1 == s2: true
s1 === s2: true
Built string:
Item 1
Item 2
Item 3
```

---

## ❓ Interview Questions

1. **What are string templates in Kotlin?**
   - String templates allow embedding variables (`$var`) and expressions (`${expr}`) directly inside string literals.

2. **What is the difference between `==` and `===` in Kotlin?**
   - `==` checks structural equality (calls `.equals()`). `===` checks referential equality (same object in memory).

3. **What are raw strings and how do you create them?**
   - Triple-quoted strings (`"""..."""`) that preserve formatting, support multi-line text, and don't require escape sequences.

4. **What does `trimIndent()` do?**
   - Removes common leading whitespace from all lines of a raw string, making it easy to format multi-line strings.

5. **How does `buildString` work?**
   - It creates a string using a lambda with `StringBuilder` scope, allowing efficient string construction with `append()` calls.

---

## 🔗 Related Topics
- [Null Safety](NullSafety.md)
- [Basics & Hello World](Basics.md)
