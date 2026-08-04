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

6. **What is the difference between `$variable` and `${expression}` in string templates?**
   - `$variable` — simple variable interpolation. Only works for single identifiers: `"Hello, $name"`. Can access properties: `"Length: ${list.size}"`. If you need to call a method or use an expression, you need `${}`: `${name.uppercase()}`. `$` followed by anything that's not a valid identifier is treated as a literal `$`: `"Cost: \$100"` (escaped) or `"Cost: $$100"`. Best practice: use `$variable` for simple variables and `${expression}` for anything more complex. Always use `${}` for: method calls, arithmetic, complex expressions, and nested template strings.

7. **How do raw strings (triple-quoted) handle string templates?**
   - Raw strings (`"""..."""`) support string templates just like regular strings: `val name = "Alice"; val raw = """Hello, $name! You are ${2024 - 1990} years old."""`. This is powerful for generating multi-line text with embedded variables (SQL queries, HTML, JSON). To include a literal `$` in a raw string, use `${'$'}` or escape with `\$`. Raw strings preserve all whitespace and newlines — use `trimIndent()` or `trimMargin()` to clean up indentation.

8. **What is `trimMargin` and how does it differ from `trimIndent`?**
   - `trimIndent()` — detects the minimum common indentation across all lines and removes it. Simple and automatic, but requires consistent indentation. `trimMargin(marginPrefix)` — removes leading whitespace up to and including the specified prefix (default `|`). More control — you mark each line with `|` and everything before it is removed. Example: `val text = """ |Line 1 |Line 2 """.trimMargin()` → `"Line 1\nLine 2"`. Use `trimIndent()` for clean, consistently-indented strings. Use `trimMargin()` when indentation is inconsistent or when you need to preserve some leading whitespace on certain lines. Custom prefix: `trimMargin(">")`.

9. **How do you escape special characters in Kotlin strings?**
   - Use backslash `\`: `\n` (newline), `\t` (tab), `\\` (backslash), `\$` (dollar sign — needed because `$` starts a template), `\'` (single quote), `\"` (double quote in regular strings), `\r` (carriage return), `\b` (backspace), `\uXXXX` (Unicode character, e.g., `\u0041` = 'A'). In raw strings (`"""..."""`), escaping is different: you don't need to escape `"` or `\n`, but you DO need to escape `$` with `${'$'}` since `$` starts a template. Raw strings can contain literal newlines without `\n`.

10. **What is the difference between `String` and `StringBuilder` in Kotlin?**
    - `String` is immutable — every modification creates a new `String` object. Operations like `+`, `replace()`, `trim()` all allocate new strings. `StringBuilder` (alias for `java.lang.StringBuilder`) is mutable — modifications happen in-place. Use `StringBuilder` for: (1) Building strings in loops — avoids creating N intermediate strings. (2) Heavy string concatenation — `StringBuilder().append("a").append("b")` is more efficient than `"a" + "b" + "c"` for many operations. (3) `buildString { }` — Kotlin's helper that creates a `StringBuilder` with a receiver lambda: `buildString { for (i in 1..1000) append("$i, ") }`. For simple concatenation, string templates (`"$a$b"`) are efficient enough.

11. **How do you format strings in Kotlin?**
    - (1) **String templates** — `"Hello, $name! You are $age years old."` — preferred for most cases. (2) **`String.format()`** — `String.format("Hello, %s! You are %d years old.", name, age)` — C-style formatting. Supports `%s`, `%d`, `%f`, `%x`, `%e`, etc. Useful for complex formatting: `String.format("%.2f", 3.14159)` → "3.14". (3) **`format()` extension** — `"Hello, %s!".format(name)` — same as `String.format()`. String templates are idiomatic Kotlin — use them for simple interpolation. Use `String.format()` for: precision control (`%.2f`), padding (`%10d`), alignment (`%-20s`), and locale-specific formatting. Note: `String.format()` uses the default locale — pass `Locale.US` for consistent formatting across devices.

12. **How do you perform multi-line string interpolation for SQL/HTML/JSON?**
     - Use raw strings with templates: `val query = """ SELECT * FROM users WHERE age > $minAge AND city = '$city' ORDER BY name """.trimIndent()`. For HTML: `val html = """ <html> <body> <h1>$title</h1> <p>$content</p> </body> </html> """.trimIndent()`. For JSON, prefer a library (Kotlinx Serialization, Moshi, Gson) — manual JSON building is error-prone. But if needed: `val json = """ {"name": "$name", "age": $age} """.trimIndent()`. Always use `trimIndent()` or `trimMargin()` to keep the source code readable. Escape `$` with `${'$'}` if needed in raw strings. For dynamic queries with multiple conditions, use `buildString` with `appendIf` patterns.

13. **How do you convert between `String` and numeric types safely in Kotlin?**
    - Use safe conversion functions: `toIntOrNull()` returns `Int?` (null if conversion fails), `toDoubleOrNull()`, `toFloatOrNull()`, `toLongOrNull()`. For base conversion: `toInt(radix)` or `toIntOrNull(radix)` — `"FF".toIntOrNull(16)` → 255. For unsafe conversion (throws `NumberFormatException`): `toInt()`, `toDouble()`. Best practice: always use `*OrNull()` variants: `val age = input.toIntOrNull() ?: 0`. For parsing with validation: `val age = input.toIntOrNull()?.takeIf { it in 0..150 } ?: -1`. The `String.toInt()` function delegates to `Integer.parseInt()` on the JVM. For `BigDecimal`: `input.toBigDecimalOrNull()`. Always handle the null case — never use `!!` on `*OrNull()` results.

14. **What is the difference between `==` and `.equals()` for strings in Kotlin?**
    - In Kotlin, `==` for strings calls `.equals()` under the hood — both check structural (content) equality. `"hello" == "hello"` → true. `===` checks referential equality — whether they're the same object in memory. On the JVM, string literals are interned, so `"hello" === "hello"` may be true, but this is an implementation detail — never rely on it. Always use `==` for string comparison. For case-insensitive comparison, use `equals(other, ignoreCase = true)` or `compareTo(other, ignoreCase = true)`. Unlike Java where `==` checks reference equality (a common bug source), Kotlin's `==` is null-safe and checks content — `a == b` compiles to `if (a === null) b === null else a.equals(b)`.

15. **What is the `buildString` function and how does it work?**
    - `buildString` is a stdlib function that creates a `StringBuilder` and applies a lambda with receiver to it: `buildString { append("Hello"); append(" "); append("World") }`. The lambda receiver is the `StringBuilder`, so `append()` is called directly without a qualifier. This is more efficient than string concatenation in loops because `StringBuilder` is mutable and avoids creating intermediate `String` objects. Example: `buildString { for (i in 1..100) { if (i > 1) append(", "); append(i) } }` → "1, 2, 3, ..., 100". Use `buildString` for: building SQL queries, constructing log messages, creating CSV/JSON strings. It's cleaner than manual `StringBuilder` creation and handles the lifecycle automatically.

---

## 🔗 Related Topics
- [Null Safety](NullSafety.md)
- [Basics & Hello World](Basics.md)
