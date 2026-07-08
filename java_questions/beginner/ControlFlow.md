# Control Flow

## Q1: What are the types of loops in Java and when to use each?

```java
// 1. for loop — known number of iterations
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// 2. enhanced for-each — iterate collections/arrays
int[] nums = {1, 2, 3, 4, 5};
for (int n : nums) {
    System.out.println(n);
}

// 3. while loop — condition checked before, may run 0 times
int i = 0;
while (i < 10) {
    System.out.println(i);
    i++;
}

// 4. do-while — condition checked after, runs at least once
int j = 0;
do {
    System.out.println(j);
    j++;
} while (j < 10);
```

| Loop | Use Case | Min Executions |
|------|----------|----------------|
| `for` | Known count | 0 |
| `for-each` | Iterate collection/array | 0 |
| `while` | Unknown count, check first | 0 |
| `do-while` | Must execute at least once | 1 |

---

## Q2: What is the difference between `break` and `continue`?

```java
// break — exits the loop entirely
for (int i = 0; i < 10; i++) {
    if (i == 5) break;     // Stops at i=5, doesn't print 5-9
    System.out.println(i); // Prints 0,1,2,3,4
}

// continue — skips current iteration, continues loop
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // Skips even numbers
    System.out.println(i);     // Prints 1,3,5,7,9
}

// Labeled break — exits nested loop
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break outer;  // Exits both loops
        System.out.println(i + "," + j);
    }
}

// Labeled continue — skips to next iteration of outer loop
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue outer;  // Skips to next i
        System.out.println(i + "," + j);
    }
}
```

---

## Q3: How does `switch` work? What changed in Java 14+?

```java
// Traditional switch statement
switch (day) {
    case MONDAY:
    case FRIDAY:
        System.out.println("Busy");
        break;  // Without break → fall-through
    case SUNDAY:
        System.out.println("Rest");
        break;
    default:
        System.out.println("Normal");
}

// Java 14+ — switch expression (arrow, no fall-through)
String mood = switch (day) {
    case MONDAY, FRIDAY -> "Busy";
    case SUNDAY -> "Rest";
    default -> "Normal";
};

// Java 14+ — switch expression with yield (for blocks)
int result = switch (day) {
    case MONDAY -> 1;
    case SUNDAY -> 0;
    default -> {
        log(day);
        yield -1;  // Return value from block
    }
};
```

| Traditional `switch` | Switch Expression (Java 14+) |
|---------------------|----------------------------|
| Statement (no return value) | Expression (returns value) |
| Fall-through (needs `break`) | No fall-through (arrow `->`) |
| `default` optional | `default` required for exhaustiveness |
| Multiple cases need separate lines | Multiple cases: `case A, B ->` |

---

## Q4: What is the difference between `if-else` and `switch`?

```java
// if-else — range checks, boolean conditions, complex logic
if (score >= 90) {
    grade = "A";
} else if (score >= 80) {
    grade = "B";
} else {
    grade = "F";
}

// switch — exact value matching (int, String, enum, char)
switch (day) {
    case MONDAY: doWork(); break;
    case SUNDAY: rest(); break;
}
```

| `if-else` | `switch` |
|-----------|---------|
| Range and boolean conditions | Exact value matching |
| Any data type | int, char, String, enum (not float/double) |
| Evaluates conditions sequentially | Jump table (faster for many cases) |
| No fall-through | Fall-through (traditional) |
| More flexible | More readable for many discrete values |

---

## Q5: What is the ternary operator?

```java
// Syntax: condition ? valueIfTrue : valueIfFalse
int max = (a > b) ? a : b;

// Nested ternary (avoid — hard to read)
String result = (score >= 90) ? "A" :
                (score >= 80) ? "B" :
                (score >= 70) ? "C" : "F";

// With null check
String name = (user != null) ? user.getName() : "Unknown";

// Equivalent if-else
String name;
if (user != null) {
    name = user.getName();
} else {
    name = "Unknown";
}
```

> **Best Practice:** Use ternary for simple assignments. For complex logic, use `if-else` for readability.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Variables and Data Types](VariablesAndDataTypes.md)
- [Arrays](Arrays.md)
