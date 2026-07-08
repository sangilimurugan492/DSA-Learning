# Debugging Scenarios

## Scenario 1: OutOfMemoryError — Heap Space

### Problem
The application crashes with `java.lang.OutOfMemoryError: Java heap space` after running for several hours.

```java
// ❌ Bad — unbounded cache causes OOM
class DataCache {
    private static final Map<String, byte[]> cache = new HashMap<>();

    public static void put(String key, byte[] data) {
        cache.put(key, data);  // Never removed — grows forever
    }

    public static byte[] get(String key) {
        return cache.get(key);
    }
}
// After hours: cache has millions of entries → OOM
```

### Solution: Bounded cache + weak references

```java
// ✅ Good 1: LinkedHashMap with LRU eviction
class BoundedCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxEntries;

    public BoundedCache(int maxEntries) {
        super(maxEntries, 0.75f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}

// ✅ Good 2: WeakHashMap — entries GC'd when key is no longer referenced
Map<WeakReference<Connection>, ConnectionState> connState = new WeakHashMap<>();

// ✅ Good 3: Caffeine cache (production-grade)
// LoadingCache<String, byte[]> cache = Caffeine.newBuilder()
//     .maximumSize(10_000)
//     .expireAfterWrite(10, TimeUnit.MINUTES)
//     .build(key -> loadData(key));
```

### Debugging Steps
```bash
# 1. Enable heap dump on OOM
java -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/tmp/heapdump.hprof \
     MyApp

# 2. Analyze heap dump with Eclipse MAT or VisualVM
#    - Find largest objects by retained size
#    - Look for collections that grow unbounded
#    - Check for duplicate objects

# 3. Monitor memory in real-time
jconsole <pid>  # or VisualVM

# 4. Check GC logs
java -Xlog:gc*=info:file=gc.log:time,uptime,level,tags MyApp
```

### Key Takeaway
- Never use unbounded `HashMap` as a cache — it grows until OOM
- Use bounded cache (LRU eviction) or `WeakHashMap` (GC-managed)
- Enable `-XX:+HeapDumpOnOutOfMemoryError` in production
- Analyze heap dumps with Eclipse MAT — find largest retained objects
- Consider Caffeine or Guava Cache for production caching

---

## Scenario 2: StackOverflowError — Unbounded Recursion

### Problem
The application crashes with `StackOverflowError` when processing deeply nested data.

```java
// ❌ Bad — unbounded recursion
class TreeProcessor {
    public int sumDepth(TreeNode node) {
        if (node == null) return 0;
        return node.value + sumDepth(node.left) + sumDepth(node.right);
        // Deep tree (100k+ nodes) → StackOverflowError
        // Each recursive call adds a stack frame (~512KB default stack)
    }
}
```

### Solution: Iterative approach or tail recursion

```java
// ✅ Good 1: Iterative with explicit stack
public int sumDepth(TreeNode root) {
    if (root == null) return 0;
    int sum = 0;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        sum += node.value;
        if (node.left != null) stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return sum;  // No stack overflow — heap-based stack
}

// ✅ Good 2: Increase stack size (temporary fix)
// java -Xss4m MyApp  — 4MB stack per thread

// ✅ Good 3: Limit recursion depth
public int sumDepth(TreeNode node, int depth) {
    if (node == null) return 0;
    if (depth > 1000) throw new IllegalStateException("Tree too deep");
    return node.value + sumDepth(node.left, depth + 1) + sumDepth(node.right, depth + 1);
}
```

### Debugging Steps
```
# StackOverflowError stack trace shows the recursive method repeated
# Look at the repeating frames to identify the recursion
# Check: missing base case? Base case never reached? Data too deep?

# Thread dump
jstack <pid> | grep -A 50 "main"
```

### Key Takeaway
- Each recursive call consumes a stack frame — default stack ~512KB
- Deep recursion → `StackOverflowError`
- Convert to iterative with explicit `Deque`/`Stack` (heap-based, no limit)
- `-Xss` increases stack size — temporary, not a real fix
- Always have a proper base case and depth limit for recursion

---

## Scenario 3: High CPU Usage

### Problem
The application uses 100% CPU. The app is slow but doesn't crash.

```java
// ❌ Bad — busy-wait loop
class Worker implements Runnable {
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            Task task = pollQueue();
            if (task != null) {
                process(task);
            }
            // No sleep — spins at 100% CPU
        }
    }
}

// ❌ Bad — infinite loop in regex
"aaaaaaaaaaaaaaaaaaaaaa!".matches("(a+)+");
// Catastrophic backtracking — regex engine backtracks exponentially
```

### Solution: Wait/notify + regex fix

```java
// ✅ Good 1: Use BlockingQueue (waits efficiently)
class Worker implements Runnable {
    private final BlockingQueue<Task> queue;
    private volatile boolean running = true;

    public Worker(BlockingQueue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Task task = queue.take();  // Blocks — no CPU usage
                process(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

// ✅ Good 2: Fix regex (avoid catastrophic backtracking)
// Bad:  "(a+)+"  — exponential backtracking on "aaaaaaaa!"
// Good: "(a++)+" or "a+"  — possessive quantifier prevents backtracking
// Or validate input length before regex

// ✅ Good 3: Add sleep in polling loop (quick fix)
while (running) {
    Task task = pollQueue();
    if (task != null) {
        process(task);
    } else {
        Thread.sleep(100);  // Yield CPU
    }
}
```

### Debugging Steps
```bash
# 1. Find the high-CPU thread
top -H -p <pid>  # Show threads by CPU
# or: ps -L -p <pid> -o pid,tid,%cpu

# 2. Convert thread ID to hex
printf "%x\n" <tid>  # e.g., 12345 → 3039

# 3. Get thread stack trace
jstack <pid> | grep -A 30 "nid=0x3039"
# Look for: busy loops, regex, GC threads, infinite loops

# 4. Check if GC is the cause
jstat -gcutil <pid> 1000 5  # Sample GC every 1s, 5 times
# If GCT is high → GC thrashing (memory leak)
```

### Key Takeaway
- Busy-wait loops consume 100% CPU — use `BlockingQueue.take()` instead
- Regex catastrophic backtracking can peg CPU — test with long inputs
- Use `top -H` + `jstack` to find the CPU-consuming thread
- Check if GC threads are the cause (memory pressure)
- `Thread.sleep()` or `wait()` yield CPU — busy loops don't

---

## Scenario 4: Thread Leak in Thread Pool

### Problem
The application creates threads that are never terminated. Thread count keeps growing until the OS refuses to create more.

```java
// ❌ Bad — new thread per request, never shut down
class RequestHandler {
    public void handle(Request req) {
        new Thread(() -> {
            process(req);  // Thread created, never pooled
        }).start();  // Thread dies after process(), but:
        // If process() blocks → threads accumulate
    }
}

// ❌ Bad — ExecutorService never shut down
class BadService {
    public void process() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> doWork());
        // Never calls shutdown() — threads live forever
    }
}
```

### Solution: Reuse thread pool + proper shutdown

```java
// ✅ Good 1: Shared thread pool with proper lifecycle
class RequestHandler {
    private final ExecutorService executor;

    public RequestHandler() {
        this.executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder()
                .setNameFormat("request-handler-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error("Uncaught", e))
                .build()
        );
    }

    public void handle(Request req) {
        executor.submit(() -> process(req));
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

// ✅ Good 2: try-with-resources for ExecutorService (Java 19+)
try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1000; i++) {
        e.submit(() -> process(req));
    }
}  // Auto-close waits for all tasks
```

### Debugging Steps
```bash
# 1. Count threads
jstack <pid> | grep "java.lang.Thread.State" | wc -l

# 2. Find thread names (look for patterns)
jstack <pid> | grep "Thread-"
jstack <pid> | grep "pool-"

# 3. Check thread states
jstack <pid> | grep "java.lang.Thread.State" | sort | uniq -c | sort -rn
# RUNNABLE: 5, WAITING: 200, TIMED_WAITING: 50

# 4. OS-level thread count
ps -eLf | grep java | wc -l
```

### Key Takeaway
- Never create `new Thread()` per request — use a shared pool
- Always `shutdown()` ExecutorService when done
- Name threads for debugging: `ThreadFactoryBuilder` or custom `ThreadFactory`
- Monitor thread count with `jstack` — look for growing counts
- `CachedThreadPool` creates unbounded threads — use `FixedThreadPool` with limits

---

## Scenario 5: NullPointerException — The Billion Dollar Mistake

### Problem
A `NullPointerException` occurs deep in the call chain. The stack trace points to a line, but the actual null source is unclear.

```java
// ❌ Bad — NPE with unclear source
class OrderService {
    public String getCity(Order order) {
        // Which is null? order? address? city?
        return order.getCustomer().getAddress().getCity().toUpperCase();
        // NPE: "Cannot invoke 'toUpperCase()' because 'city' is null"
        // But which in the chain was null?
    }
}

// ❌ Bad — manual null checks (verbose, error-prone)
public String getCity(Order order) {
    if (order == null) return "Unknown";
    Customer customer = order.getCustomer();
    if (customer == null) return "Unknown";
    Address address = customer.getAddress();
    if (address == null) return "Unknown";
    String city = address.getCity();
    if (city == null) return "Unknown";
    return city.toUpperCase();
}
```

### Solution: Optional + validation + @NonNull

```java
// ✅ Good 1: Optional chaining
public String getCity(Order order) {
    return Optional.ofNullable(order)
        .map(Order::getCustomer)
        .map(Customer::getAddress)
        .map(Address::getCity)
        .map(String::toUpperCase)
        .orElse("Unknown");
    // No NPE — each step returns empty Optional if null
}

// ✅ Good 2: Validate early with clear messages
public String getCity(Order order) {
    Objects.requireNonNull(order, "Order must not be null");
    Customer customer = Objects.requireNonNull(order.getCustomer(),
        "Order customer must not be null");
    Address address = Objects.requireNonNull(customer.getAddress(),
        "Customer address must not be null");
    String city = address.getCity();
    return city != null ? city.toUpperCase() : "Unknown";
}

// ✅ Good 3: Use @NonNull annotations (compile-time checks)
public String getCity(@NonNull Order order) {
    // IDE warns if order could be null
    return order.getCustomer().getAddress().getCity().toUpperCase();
}

// ✅ Good 4: Java 14+ helpful NPE messages
// java -XX:+ShowCodeDetailsInExceptionMessages MyApp
// NPE: "Cannot invoke 'Order.getCustomer()' because 'order' is null"
// Now the message tells you WHICH was null
```

### Debugging Steps
```
# 1. Read the NPE message (Java 14+ has helpful messages)
#    "Cannot invoke 'String.toUpperCase()' because the return value of
#     'Address.getCity()' is null"

# 2. Enable helpful NPE messages
java -XX:+ShowCodeDetailsInExceptionMessages MyApp

# 3. Check the stack trace line number
#    Go to the exact line — which method call returned null?

# 4. Add logging before the chain
log.debug("order={}, customer={}, address={}",
    order, order.getCustomer(), order.getCustomer().getAddress());
```

### Key Takeaway
- Chain of method calls → NPE doesn't tell you which was null
- `Optional.ofNullable().map().map().orElse()` — null-safe chaining
- `Objects.requireNonNull(value, message)` — fail fast with clear message
- Java 14+ `-XX:+ShowCodeDetailsInExceptionMessages` — NPE tells you which was null
- Use `@NonNull`/`@Nullable` annotations for compile-time checks
- Validate at boundaries (API entry, constructor) — trust internally

---

## 🔗 Related Topics
- [JVM Internals](../advanced/JVMInternals.md)
- [Garbage Collection](../advanced/GarbageCollection.md)
- [Exception Handling](../intermediate/ExceptionHandling.md)
- [Concurrency](../intermediate/Concurrency.md)
