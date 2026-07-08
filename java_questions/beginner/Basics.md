# Java Basics

## Q1: What is JVM, JDK, and JRE?

| Component | Full Form | Description |
|-----------|-----------|-------------|
| **JVM** | Java Virtual Machine | Executes bytecode. Platform-dependent (different JVM for Windows, Linux, Mac). Makes Java "write once, run anywhere." |
| **JRE** | Java Runtime Environment | Contains JVM + core libraries. Needed to **run** Java applications. |
| **JDK** | Java Development Kit | Contains JRE + compiler (`javac`) + tools. Needed to **develop** Java applications. |

```
JDK = JRE + Development Tools (javac, jdb, javadoc)
JRE = JVM + Core Libraries (rt.jar, etc.)
JVM = Runtime engine that executes bytecode
```

### Key Points
- JVM is an **abstract specification** — implementations exist for each platform
- Java source (`.java`) → `javac` → bytecode (`.class`) → JVM → machine code
- Bytecode is platform-independent; JVM is platform-dependent

---

## Q2: Why is Java platform-independent?

Java source code is compiled into **bytecode** (`.class` file), not machine code. This bytecode runs on any platform that has a JVM implementation.

```java
// On Windows:
javac Hello.java → Hello.class (bytecode)

// Same Hello.class runs on:
// Windows JVM → Windows machine code
// Linux JVM   → Linux machine code
// Mac JVM     → Mac machine code
```

> **Note:** Java is platform-independent at the **source/bytecode** level. The JVM itself is platform-dependent.

---

## Q3: What are the primitive data types in Java?

| Type | Size | Range | Default | Example |
|------|------|-------|---------|---------|
| `byte` | 1 byte | -128 to 127 | 0 | `byte b = 100;` |
| `short` | 2 bytes | -32,768 to 32,767 | 0 | `short s = 1000;` |
| `int` | 4 bytes | -2³¹ to 2³¹-1 | 0 | `int i = 100000;` |
| `long` | 8 bytes | -2⁶³ to 2⁶³-1 | 0L | `long l = 100000L;` |
| `float` | 4 bytes | ~±3.4e38 | 0.0f | `float f = 3.14f;` |
| `double` | 8 bytes | ~±1.7e308 | 0.0d | `double d = 3.14159;` |
| `char` | 2 bytes | 0 to 65,535 (Unicode) | `'\u0000'` | `char c = 'A';` |
| `boolean` | 1 bit* | true / false | false | `boolean b = true;` |

> *JVM spec doesn't define exact size for `boolean`; typically 1 byte in arrays, 4 bytes standalone.

---

## Q4: What is the difference between `==` and `.equals()`?

```java
String a = new String("hello");
String b = new String("hello");

System.out.println(a == b);        // false — different objects in memory
System.out.println(a.equals(b));  // true  — same content
```

| `==` | `.equals()` |
|------|------------|
| Compares **references** (memory address) | Compares **content** (if overridden) |
| Default behavior for all objects | Default (Object class) also compares references |
| Cannot be overridden | Can be overridden (e.g., String, Integer) |

```java
// String pool example
String s1 = "hello";        // String pool
String s2 = "hello";        // Reuses pool reference
String s3 = new String("hello"); // New object in heap

System.out.println(s1 == s2);        // true  — same pool reference
System.out.println(s1 == s3);       // false — different objects
System.out.println(s1.equals(s3));  // true  — same content
```

---

## Q5: What is the `main` method signature and why?

```java
public static void main(String[] args)
```

| Keyword | Reason |
|---------|--------|
| `public` | JVM needs to access it from outside the class |
| `static` | JVM calls it without creating an instance |
| `void` | Returns nothing to the JVM |
| `main` | Convention — JVM looks for this name |
| `String[] args` | Command-line arguments |

```java
// Valid alternative signatures (Java 21+):
// public static void main()  // No args — valid from Java 21

// Running with args:
// java MyApp arg1 arg2
// args[0] = "arg1", args[1] = "arg2"
```

---

## Q6: What is the difference between `final`, `finally`, and `finalize`?

```java
// final — keyword for constants/immutability
final int MAX = 100;          // Variable: cannot reassign
final class Animal { }        // Class: cannot extend
final void run() { }          // Method: cannot override

// finally — block in try-catch
try {
    riskyOperation();
} catch (Exception e) {
    handle(e);
} finally {
    cleanup();  // Always executes (even if return/throw in try)
}

// finalize — method called by GC before destroying object
@Override
protected void finalize() throws Throwable {
    closeResources();  // Deprecated since Java 9 — use AutoCloseable instead
    super.finalize();
}
```

| `final` | `finally` | `finalize` |
|---------|-----------|------------|
| Modifier keyword | Block in exception handling | Method in Object class |
| Prevents reassignment/override/inheritance | Always executes after try-catch | Called by GC before reclamation |
| Compile-time concept | Runtime concept | Deprecated since Java 9 |

---

## Q7: What is type casting in Java?

```java
// Widening (implicit) — no data loss
int i = 100;
long l = i;        // int → long (automatic)
double d = l;      // long → double (automatic)

// Narrowing (explicit) — may lose data
double d = 3.99;
int i = (int) d;   // i = 3 — fractional part lost

// Object casting
Object obj = "Hello";
String str = (String) obj;  // Downcasting — requires explicit cast

// instanceof check before casting
if (obj instanceof String) {
    String s = (String) obj;  // Safe cast
}
```

---

## Q8: What are wrapper classes and autoboxing/unboxing?

```java
// Primitive → Wrapper (Boxing)
int primitive = 42;
Integer wrapper = Integer.valueOf(primitive);  // Manual boxing
Integer auto = primitive;                      // Autoboxing (Java 5+)

// Wrapper → Primitive (Unboxing)
Integer wrapper = Integer.valueOf(42);
int primitive = wrapper.intValue();  // Manual unboxing
int auto = wrapper;                  // Auto-unboxing

// ⚠️ NPE risk with autoboxing
Integer nullValue = null;
int x = nullValue;  // NullPointerException!

// Wrapper class cache: -128 to 127
Integer a = 127;
Integer b = 127;
System.out.println(a == b);  // true — cached

Integer c = 128;
Integer d = 128;
System.out.println(c == d);  // false — not cached, different objects
```

---

## 🔗 Related Topics
- [Variables and Data Types](VariablesAndDataTypes.md)
- [Control Flow](ControlFlow.md)
- [OOP Basics](OOPBasics.md)
