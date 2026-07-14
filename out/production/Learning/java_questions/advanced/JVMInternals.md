# JVM Internals

## Q1: What are the JVM memory areas?

```
JVM Memory
├── Method Area (Metaspace in Java 8+)  — Shared
│   ├── Class metadata, method bytecode
│   ├── Static variables
│   └── Runtime constant pool
│
├── Heap  — Shared
│   ├── Young Generation
│   │   ├── Eden Space
│   │   └── Survivor Space 0 (S0)
│   │   └── Survivor Space 1 (S1)
│   └── Old Generation (Tenured)
│
├── Stack (per thread)  — Thread-private
│   ├── Stack frames
│   │   ├── Local variables
│   │   ├── Operand stack
│   │   └── Frame data
│   └── One frame per method call
│
├── PC Register (per thread)  — Thread-private
│   └── Current instruction address
│
└── Native Method Stack (per thread)  — Thread-private
    └── Native method (JNI) frames
```

```java
public class MemoryExample {
    // Method area: class metadata, static vars
    private static int staticVar = 42;

    // Heap: object instance
    private String instanceVar = "hello";

    public void method(int param) {
        // Stack: local variable
        int localVar = param + 1;

        // Heap: new object
        List<String> list = new ArrayList<>();
        list.add("item");  // "item" in heap (String pool)
    }
}
```

---

## Q2: What is the class loading process?

```
Loading → Linking → Initialization
           ↓
    Verification → Preparation → Resolution
```

```java
// 1. Loading — .class file loaded into method area
//    ClassLoader reads bytecode, creates Class object in heap

// 2. Verification — bytecode verified for safety
//    - Correct format, no stack overflow, valid types

// 3. Preparation — static fields set to default values
//    static int x = 0;  (not yet 42)

// 4. Resolution — symbolic references → direct references
//    "java/lang/String" → actual memory pointer

// 5. Initialization — static initializers run
//    static int x = 42;  (now assigned)
//    static { } blocks execute
```

### ClassLoader Hierarchy
```
Bootstrap ClassLoader (C++ — loads rt.jar, java.lang.*)
    ↑ parent
Extension ClassLoader (loads ext/*.jar, Java 8)
    ↑ parent
Application ClassLoader (loads classpath, your app)
    ↑ parent
Custom ClassLoader (plugin systems, hot reload)
```

### Parent Delegation Model
```java
// When loading a class, ask parent first
// 1. App ClassLoader → asks Extension
// 2. Extension → asks Bootstrap
// 3. Bootstrap loads java.lang.String → done
// Prevents: custom java.lang.String (security)
```

---

## Q3: What is bytecode and how does JIT compilation work?

```java
// Source code
public int add(int a, int b) {
    return a + b;
}

// Bytecode (javap -c)
// 0: iload_1     // Load local var 1 (a) onto operand stack
// 1: iload_2     // Load local var 2 (b) onto operand stack
// 2: iadd        // Pop two ints, push sum
// 3: ireturn     // Return int from method
```

### JIT Compilation
```
Source (.java) → javac → Bytecode (.class) → JVM

JVM execution modes:
1. Interpreter — executes bytecode line by line (slow start, no warmup)
2. JIT Compiler — compiles hot methods to native machine code (fast after warmup)

JIT optimizations:
- Method inlining — replace method call with method body
- Escape analysis — allocate on stack if object doesn't escape
- Lock elision — remove unnecessary synchronization
- Loop unrolling — reduce loop overhead
- Dead code elimination — remove unreachable code
```

```java
// JIT inlining example
// Before:
public int calculate() {
    return add(1, 2) + add(3, 4);
}
// After inlining:
public int calculate() {
    return (1 + 2) + (3 + 4);  // = 10
}
```

---

## Q4: What is the difference between Stack and Heap overflow?

```java
// StackOverflowError — too deep recursion or infinite recursion
public void recurse() {
    recurse();  // ❌ StackOverflowError
}
// Default stack size: ~512KB-1MB per thread
// Tune: -Xss512k

// OutOfMemoryError: Heap — too many objects
List<byte[]> data = new ArrayList<>();
while (true) {
    data.add(new byte[1024 * 1024]);  // ❌ OOM: Java heap space
}
// Default heap: ~256MB (varies)
// Tune: -Xms256m -Xmx2g (min/max heap)
```

| StackOverflowError | OutOfMemoryError (Heap) |
|-------------------|------------------------|
| Stack too deep | Heap too full |
| Unbounded recursion | Too many objects / memory leak |
| `StackOverflowError` | `OutOfMemoryError` |
| Fix: limit recursion depth | Fix: fix leaks, increase heap |

---

## Q5: What are JVM flags and tuning?

```bash
# Heap sizing
-Xms512m          # Initial heap size
-Xmx2g            # Maximum heap size
-Xmn256m          # Young generation size
-Xss512k          # Thread stack size

# GC selection
-XX:+UseG1GC              # Use G1 Garbage Collector (Java 9+ default)
-XX:+UseZGC               # Use ZGC (low latency, Java 15+)
-XX:+UseParallelGC        # Throughput-focused

# GC tuning
-XX:MaxGCPauseMillis=200  # Target max GC pause
-XX:G1HeapRegionSize=16m  # G1 region size

# Monitoring
-XX:+PrintGCDetails       # Print GC details
-XX:+PrintGCDateStamps    # Add timestamps
-Xlog:gc*                 # Java 9+ unified logging

# Debugging
-XX:+HeapDumpOnOutOfMemoryError  # Dump heap on OOM
-XX:HeapDumpPath=/tmp/dump.hprof
```

---

## Q6: What is the String pool and where is it stored?

```
Java 6 and earlier:
  String Pool → PermGen (fixed size, separate from heap)
  ⚠️ Too many intern() calls → java.lang.OutOfMemoryError: PermGen

Java 7+:
  String Pool → Heap (can grow with -XX:StringTableSize)
  ✅ More flexible, GC can clean up unused strings

Java 9+:
  String internal storage: byte[] instead of char[]
  - Latin-1 (1 byte/char) for ASCII → 50% memory savings
  - UTF-16 (2 bytes/char) for non-ASCII
```

```java
// String pool size
// Default: 60013 buckets (Java 8), 65536 (Java 11+)
// Tune: -XX:StringTableSize=1000003

// Check pool size
// java -Xlog:gc+stringtable=debug -version  (Java 9+)
```

---

## 🔗 Related Topics
- [Garbage Collection](GarbageCollection.md)
- [Concurrency Advanced](ConcurrencyAdvanced.md)
- [Basics](../beginner/Basics.md)
