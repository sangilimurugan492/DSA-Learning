# Strings

## Q1: Why is String immutable in Java?

```java
String s = "hello";
s.concat(" world");  // Creates new String "hello world", s is still "hello"
s = s.concat(" world");  // s now points to new String "hello world"
```

### Reasons for Immutability
1. **String Pool** — Multiple references can safely share the same String object
2. **Thread Safety** — Immutable objects are inherently thread-safe
3. **Security** — Used as parameters (DB URLs, file paths) — can't be changed by malicious code
4. **HashCode Caching** — Computed once, cached for HashMap keys
5. **Class Loading** — Class names are Strings — must not change during loading

```java
// String pool — shared references
String a = "hello";    // Pool
String b = "hello";    // Reuses pool object
System.out.println(a == b);  // true — same object

String c = new String("hello");  // New object in heap (not pool)
System.out.println(a == c);      // false
System.out.println(a.equals(c)); // true

// intern() — moves to pool
String d = c.intern();
System.out.println(a == d);  // true — now in pool
```

---

## Q2: String vs StringBuilder vs StringBuffer

| Feature | `String` | `StringBuilder` | `StringBuffer` |
|---------|---------|----------------|----------------|
| **Mutable** | ❌ Immutable | ✅ Mutable | ✅ Mutable |
| **Thread-safe** | ✅ (immutable) | ❌ No | ✅ Yes (synchronized) |
| **Performance** | Slow for concat | Fastest | Slower (sync overhead) |
| **Since** | Java 1.0 | Java 5 | Java 1.0 |
| **Use case** | Fixed text | Single-thread building | Multi-thread building |

```java
// ❌ Bad — creates 4 String objects
String result = "";
for (int i = 0; i < 100; i++) {
    result += i;  // Creates new String each iteration
}

// ✅ Good — StringBuilder mutates in place
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 100; i++) {
    sb.append(i);  // No new object created
}
String result = sb.toString();

// ✅ Multi-threaded — StringBuffer (synchronized)
StringBuffer sb = new StringBuffer();
// Thread-safe append
```

---

## Q3: What are common String methods?

```java
String s = "Hello World";

// Length
s.length();                    // 11

// Access
s.charAt(0);                   // 'H'
s.charAt(10);                  // 'd'
// s.charAt(11);                // ❌ StringIndexOutOfBoundsException

// Substring
s.substring(6);                // "World"
s.substring(0, 5);             // "Hello" (endIndex exclusive)

// Search
s.indexOf("o");                // 4 (first occurrence)
s.lastIndexOf("o");            // 7 (last occurrence)
s.indexOf("xyz");              // -1 (not found)
s.contains("World");            // true
s.startsWith("Hello");          // true
s.endsWith("World");            // true

// Transform
s.toUpperCase();                // "HELLO WORLD"
s.toLowerCase();                // "hello world"
s.trim();                       // Remove leading/trailing whitespace
s.strip();                      // Unicode-aware trim (Java 11+)
s.replace("o", "0");           // "Hell0 W0rld"
s.replaceAll("\\s", "_");      // "Hello_World"
s.split(" ");                   // ["Hello", "World"]
s.toCharArray();                // char[] {'H','e','l','l','o',' ','W','o','r','l','d'}

// Comparison
"Hello".equals("Hello");       // true
"Hello".equalsIgnoreCase("hello"); // true
"Hello".compareTo("World");    // negative (H < W)
"Hello".compareToIgnoreCase("hello"); // 0

// Join
String.join(", ", "a", "b", "c");  // "a, b, c"
String.join("-", List.of("2024", "01", "15"));  // "2024-01-15"

// Repeat (Java 11+)
"ab".repeat(3);                 // "ababab"

// isBlank (Java 11+)
"   ".isBlank();                // true (whitespace only)
"   ".isEmpty();                // false (length > 0)
```

---

## Q4: What is the String pool (String interning)?

```java
// String literals go to the String Constant Pool
String a = "hello";     // Pool
String b = "hello";     // Reuses pool object → a == b is true

// new String() creates a heap object (not pool)
String c = new String("hello");  // Heap object
// a == c → false (different objects)

// intern() returns pool reference
String d = c.intern();   // Returns pool reference
// a == d → true

// ⚠️ new String("hello") creates TWO objects:
// 1. Pool object "hello" (if not already in pool)
// 2. Heap object (the new String)
```

### String Pool Locations
- **Java 6 and earlier:** PermGen (fixed size, can cause OOM)
- **Java 7+**: Heap (can grow with `-XX:StringTableSize`)

---

## Q5: How does `String.format()` work?

```java
// Format specifiers
String name = "Alice";
int age = 30;
double score = 95.5;

String formatted = String.format("Name: %s, Age: %d, Score: %.1f", name, age, score);
// "Name: Alice, Age: 30, Score: 95.5"

// Common format specifiers
// %s — String
// %d — integer (decimal)
// %f — floating point
// %.2f — float with 2 decimal places
// %n — platform-independent newline
// %x — hexadecimal
// %,d — integer with grouping separator (1,000,000)

// Padding and alignment
String.format("|%10d|", 42);     // "|        42|" (right-aligned, width 10)
String.format("|%-10d|", 42);    // "|42        |" (left-aligned)
String.format("|%010d|", 42);    // "|0000000042|" (zero-padded)

// Java 15+ — formatted() instance method
"Name: %s, Age: %d".formatted(name, age);
```

---

## Q6: How do you convert between String and other types?

```java
// String → int
int i = Integer.parseInt("42");
int i2 = Integer.valueOf("42");  // Returns Integer, auto-unboxed

// String → double
double d = Double.parseDouble("3.14");

// String → boolean
boolean b = Boolean.parseBoolean("true");

// int → String
String s1 = String.valueOf(42);
String s2 = Integer.toString(42);
String s3 = "" + 42;          // Simple but creates StringBuilder
String s4 = String.format("%d", 42);

// char[] → String
char[] chars = {'H', 'i'};
String s5 = new String(chars);

// String → char[]
char[] arr = "Hello".toCharArray();

// String → byte[]
byte[] bytes = "Hello".getBytes(StandardCharsets.UTF_8);

// byte[] → String
String s6 = new String(bytes, StandardCharsets.UTF_8);
```

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Variables and Data Types](VariablesAndDataTypes.md)
- [Lambda and Streams](../intermediate/LambdaAndStreams.md)
