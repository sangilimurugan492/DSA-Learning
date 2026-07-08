# Exception Handling

## Q1: What is the exception hierarchy in Java?

```
Throwable
├── Error                    — JVM-level, shouldn't catch
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
│
└── Exception
    ├── RuntimeException (Unchecked)  — Compiler doesn't force handling
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ClassCastException
    │   ├── IllegalArgumentException
    │   ├── ArithmeticException
    │   └── ConcurrentModificationException
    │
    └── Other Exceptions (Checked)    — Compiler forces try-catch or throws
        ├── IOException
        ├── SQLException
        ├── ClassNotFoundException
        └── FileNotFoundException
```

| Checked | Unchecked |
|---------|-----------|
| Compiler enforces try-catch or `throws` | Compiler doesn't enforce |
| Subclass of `Exception` (not RuntimeException) | Subclass of `RuntimeException` |
| Recoverable (IO, SQL, network) | Programming errors (NPE, array bounds) |
| `IOException`, `SQLException` | `NullPointerException`, `ArithmeticException` |

---

## Q2: How does try-catch-finally work?

```java
public String readFile(String path) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(new FileReader(path));
        return reader.readLine();
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + e.getMessage());
        return null;
    } catch (IOException e) {
        System.err.println("IO error: " + e.getMessage());
        return null;
    } finally {
        // Always runs — even if return or throw in try/catch
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // Ignore close error
            }
        }
    }
}
```

### Multi-catch (Java 7+)
```java
try {
    riskyOperation();
} catch (IOException | SQLException e) {
    // Handle both the same way
    log.error(e);
}
```

### Finally Execution Rules
```java
// finally ALWAYS executes except:
// 1. System.exit() called
// 2. JVM crashes
// 3. Thread killed
// 4. Infinite loop in try

// ⚠️ finally can override return
int example() {
    try {
        return 1;      // Evaluated, but...
    } finally {
        return 2;      // This is returned! (bad practice)
    }
}
// Returns 2
```

---

## Q3: What is try-with-resources (Java 7+)?

```java
// ✅ Good — auto-close resources (implements AutoCloseable)
public String readFile(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        return reader.readLine();
    }  // reader.close() called automatically — even on exception
    // No finally needed!
}

// Multiple resources — closed in reverse order
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write(fis.readAllBytes());
}  // fos.close() first, then fis.close()

// Custom AutoCloseable
public class DatabaseConnection implements AutoCloseable {
    @Override
    public void close() throws Exception {
        connection.close();  // Called automatically
    }
}

try (DatabaseConnection conn = new DatabaseConnection()) {
    conn.query("SELECT * FROM users");
}  // conn.close() auto-called

// Java 9+ — effectively final variables can be used
BufferedReader reader = new BufferedReader(new FileReader(path));
try (reader) {  // No need to declare inside
    return reader.readLine();
}
```

---

## Q4: How do you create custom exceptions?

```java
// Custom checked exception
public class InvalidUserException extends Exception {
    private final String username;

    public InvalidUserException(String message, String username) {
        super(message);
        this.username = username;
    }

    public InvalidUserException(String message, String username, Throwable cause) {
        super(message, cause);
        this.username = username;
    }

    public String getUsername() { return username; }
}

// Custom unchecked exception
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage
public User login(String username, String password) throws InvalidUserException {
    if (username == null || username.isEmpty()) {
        throw new InvalidUserException("Username required", username);
    }
    // ...
}

// Runtime — no throws declaration needed
public void loadConfig() {
    if (config == null) {
        throw new ConfigurationException("Config not loaded");
    }
}
```

### When to use checked vs unchecked
| Checked | Unchecked |
|---------|-----------|
| Caller can recover (retry, fallback) | Programming error (NPE, illegal arg) |
| External failure (IO, network, DB) | Business logic violation |
| Forces caller to handle | Optional to handle |
| `IOException`, `SQLException` | `IllegalArgumentException`, `NPE` |

---

## Q5: What is the difference between throw and throws?

```java
// throw — actually throw an exception
public void withdraw(double amount) {
    if (amount > balance) {
        throw new IllegalArgumentException("Insufficient funds");
    }
    balance -= amount;
}

// throws — declare that method may throw
public String readFile(String path) throws IOException {
    // Caller must handle or declare
    return Files.readString(Path.of(path));
}

// throw = action (throw an exception object)
// throws = declaration (method signature)
```

```java
// Re-throwing with wrapping
public void processFile(String path) throws ProcessingException {
    try {
        readFile(path);
    } catch (IOException e) {
        throw new ProcessingException("Failed to process: " + path, e);  // Wrap
    }
}
```

---

## Q6: What are common exception anti-patterns?

```java
// ❌ Anti-pattern 1: Swallowing exceptions
try {
    riskyOperation();
} catch (Exception e) {
    // Empty — silently ignored!
}

// ❌ Anti-pattern 2: Catching too broadly
try {
    doEverything();
} catch (Exception e) {  // Catches NPE, OOM, everything
    e.printStackTrace();
}

// ❌ Anti-pattern 3: Catching and ignoring
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // Don't ignore — restore interrupt status!
    // Thread.currentThread().interrupt();  // ✅ Do this
}

// ❌ Anti-pattern 4: Using exceptions for control flow
try {
    Integer.parseInt(input);
    isNumeric = true;
} catch (NumberFormatException e) {
    isNumeric = false;
}
// ✅ Better: use if/else or regex check

// ❌ Anti-pattern 5: Returning from finally
try {
    return computeValue();
} finally {
    return defaultValue;  // Overrides try return, swallows exceptions
}

// ✅ Good patterns:
// 1. Catch specific exceptions
// 2. Log or rethrow — don't swallow
// 3. Use try-with-resources
// 4. Restore interrupt status in InterruptedException
// 5. Wrap low-level exceptions in domain exceptions
```

---

## Q7: What is exception chaining?

```java
public class ServiceException extends Exception {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

public User getUser(int id) throws ServiceException {
    try {
        return database.query(id);
    } catch (SQLException e) {
        // Chain — preserve original cause
        throw new ServiceException("Failed to get user " + id, e);
    }
}

// Accessing the cause
try {
    getUser(42);
} catch (ServiceException e) {
    log.error(e.getMessage());      // "Failed to get user 42"
    log.error("Caused by:", e.getCause());  // SQLException details
}
```

---

## 🔗 Related Topics
- [Basics](../beginner/Basics.md)
- [OOP Concepts](OOPConcepts.md)
- [Concurrency](Concurrency.md)
