# Variables and Data Types

## Q1: What is the difference between primitive and reference types?

| Primitive Types | Reference Types |
|----------------|----------------|
| Store actual values | Store memory addresses (references) |
| 8 types: byte, short, int, long, float, double, char, boolean | String, Arrays, all custom objects |
| Stored on **stack** | Stored on **heap** (reference on stack) |
| Default values: 0, 0.0, false, '\u0000' | Default value: `null` |
| Cannot be `null` | Can be `null` |
| Fixed size | Variable size (depends on object) |

```java
int a = 10;           // Primitive — value stored directly
String s = "hello";   // Reference — address to heap object

int b = a;            // Copies value — b is independent
b = 20;               // a is still 10

int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;    // Copies reference — both point to same array
arr2[0] = 99;         // arr1[0] is also 99 now
```

---

## Q2: What are local, instance, and static variables?

```java
public class Example {
    // Instance variable — per object, on heap
    private int instanceVar = 10;

    // Static variable — per class, on method area
    private static int staticVar = 20;

    public void method() {
        // Local variable — on stack, must initialize before use
        int localVar = 30;
        System.out.println(localVar);
    }
}
```

| Type | Scope | Storage | Default | Lifetime |
|------|-------|---------|---------|----------|
| **Local** | Inside method/block | Stack | None (must init) | Method execution |
| **Instance** | Inside class, outside method | Heap (with object) | 0 / false / null | Object lifetime |
| **Static** | Inside class, `static` keyword | Method area / Metaspace | 0 / false / null | Class lifetime (until class unloaded) |

```java
// Local variable — no default, must initialize
void myMethod() {
    int x;
    // System.out.println(x);  // ❌ Compile error: variable x not initialized
    x = 5;
    System.out.println(x);  // ✅ OK
}
```

---

## Q3: What is type widening and narrowing?

```java
// Widening (implicit) — small type → large type, no data loss
// byte → short → int → long → float → double
int i = 100;
long l = i;       // int → long (implicit)
double d = l;     // long → double (implicit)

// Narrowing (explicit) — large type → small type, may lose data
double d = 9.99;
int i = (int) d;  // i = 9 — fractional part truncated

// char to int
char c = 'A';
int ascii = c;    // 65 — implicit widening

// int to char
int i = 65;
char c = (char) i; // 'A' — explicit narrowing
```

### Widening Hierarchy
```
byte → short → int → long → float → double
                ↑
               char
```

---

## Q4: What is the difference between `var` (Java 10+) and explicit types?

```java
// Java 10+ — var for local variable type inference
var name = "John";        // Inferred as String
var count = 42;           // Inferred as int
var list = new ArrayList<String>();  // Inferred as ArrayList<String>

// Equivalent to:
String name = "John";
int count = 42;
ArrayList<String> list = new ArrayList<String>();

// ⚠️ var is NOT dynamic typing — Java is still statically typed
// name = 123;  // ❌ Compile error — name is String

// ⚠️ var cannot be used for:
// var field;              // ❌ Not for fields
// var param(String x);   // ❌ Not for method params
// var x;                  // ❌ Must initialize
// var x = null;           // ❌ Cannot infer type from null
```

---

## Q5: What are default values and when do they apply?

```java
public class Defaults {
    private int intField;           // 0
    private long longField;         // 0L
    private float floatField;       // 0.0f
    private double doubleField;     // 0.0d
    private boolean booleanField;   // false
    private char charField;         // '\u0000' (null character)
    private String stringField;     // null
    private int[] arrayField;       // null

    public void method() {
        int localVar;  // ❌ No default — must initialize before use
        // System.out.println(localVar);  // Compile error
    }
}
```

| Type | Default Value |
|------|--------------|
| byte, short, int | 0 |
| long | 0L |
| float | 0.0f |
| double | 0.0d |
| char | '\u0000' |
| boolean | false |
| All references | null |

> **Important:** Default values apply only to instance and static variables — **not** local variables.

---

## Q6: What is the difference between `int` and `Integer`?

```java
int primitive = 42;          // Stack, 4 bytes, cannot be null
Integer wrapper = 42;        // Heap, ~16 bytes, can be null

// Use cases:
// int → performance-critical, simple calculations
// Integer → Collections (List<Integer>), nullable fields, generics

// ⚠️ Autoboxing NPE
Integer nullInt = null;
int x = nullInt;  // NullPointerException!

// ⚠️ Integer cache range
Integer a = 127;  // Cached
Integer b = 127;  // Same cached object
a == b;           // true

Integer c = 128;  // Not cached
Integer d = 128;  // New object
c == d;           // false — use .equals() instead
```

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Control Flow](ControlFlow.md)
- [OOP Basics](OOPBasics.md)
