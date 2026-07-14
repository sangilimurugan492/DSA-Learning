# Concurrency Scenarios

## Scenario 1: Race Condition in Counter

### Problem
Multiple threads increment a shared counter. The final count is less than expected due to race conditions.

```java
// ❌ Bad — race condition on count++
class BadCounter {
    private int count = 0;

    public void increment() {
        count++;  // ❌ Not atomic: read, add, write
    }
}

// count++ is 3 steps:
// 1. Read count from memory
// 2. Add 1
// 3. Write back to memory
// Thread A reads 0, Thread B reads 0, both write 1 → lost update

BadCounter counter = new BadCounter();
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 1000; i++) {
    executor.execute(counter::increment);
}
executor.shutdown();
executor.awaitTermination(1, TimeUnit.SECONDS);
// Expected: 1000, Actual: ~973 (varies)
```

### Solution: AtomicInteger or synchronized

```java
// ✅ Solution 1: AtomicInteger (lock-free, best performance)
class AtomicCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet();  // CAS-based, atomic
    }

    public int get() { return count.get(); }
}

// ✅ Solution 2: synchronized (simple, heavier)
class SyncCounter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int get() { return count; }
}

// ✅ Solution 3: ReentrantLock (more control)
class LockCounter {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int get() {
        lock.lock();
        try { return count; }
        finally { lock.unlock(); }
    }
}

// ✅ Solution 4: LongAdder (best for high contention)
class AdderCounter {
    private final LongAdder count = new LongAdder();

    public void increment() { count.increment(); }
    public long get() { return count.sum(); }
}
```

| Solution | Throughput | Contention | Use Case |
|----------|-----------|------------|----------|
| `int++` | Fastest | ❌ Not safe | Single-thread only |
| `AtomicInteger` | High | Low CAS contention | General purpose |
| `synchronized` | Medium | High (blocking) | Simple, low contention |
| `ReentrantLock` | Medium | High (blocking) | Need tryLock, fairness |
| `LongAdder` | Highest | Very low (cells) | High contention writes |

### Key Takeaway
- `count++` is NOT atomic — it's read-modify-write
- `AtomicInteger` uses CAS (Compare-And-Swap) — lock-free, fast
- `LongAdder` splits counter across cells — best for high contention
- `synchronized` is simplest but blocks threads
- Always test with multiple threads to verify thread safety

---

## Scenario 2: Deadlock Between Two Locks

### Problem
Two threads each hold one lock and wait for the other — neither can proceed.

```java
// ❌ Bad — different lock acquisition order → deadlock
class TransferService {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void transferAtoB() {
        synchronized (lock1) {        // Thread 1: holds lock1
            synchronized (lock2) {    // Thread 1: waits for lock2 (held by Thread 2)
                doTransfer();
            }
        }
    }

    public void transferBtoA() {
        synchronized (lock2) {        // Thread 2: holds lock2
            synchronized (lock1) {    // Thread 2: waits for lock1 (held by Thread 1)
                doTransfer();
            }
        }
    }
}
// Deadlock: Thread 1 holds lock1, wants lock2
//           Thread 2 holds lock2, wants lock1
```

### Solution: Consistent lock ordering

```java
// ✅ Good — always acquire locks in the same order
class SafeTransferService {
    public void transfer(Account from, Account to, double amount) {
        // Always lock lower-ID account first — consistent ordering
        Account first = from.getId() < to.getId() ? from : to;
        Account second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}

// ✅ Better — tryLock with timeout (no deadlock, may fail)
class TimeoutTransferService {
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();

    public boolean transfer() {
        try {
            if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                        try {
                            doTransfer();
                            return true;
                        } finally { lock2.unlock(); }
                    }
                } finally { lock1.unlock(); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;  // Failed to acquire — retry or give up
    }
}
```

### Deadlock Detection
```bash
# Find deadlocked threads
jstack <pid> | grep -A 20 "Found .* deadlock"

# jconsole → Threads tab → Detect Deadlock button
```

### Key Takeaway
- Deadlock: circular wait on locks
- Fix: acquire locks in a **consistent global order**
- `tryLock(timeout)` avoids deadlock — backs off if can't acquire
- Deadlocks can be detected with `jstack` or `jconsole`
- Minimize lock scope — hold locks for the shortest time possible

---

## Scenario 3: Producer-Consumer with BlockingQueue

### Problem
A producer generates data and a consumer processes it. They run at different speeds. The producer shouldn't overwhelm the consumer, and the consumer shouldn't wait idle.

```java
// ❌ Bad — busy waiting, no coordination
class BadProducerConsumer {
    private final Queue<Task> queue = new LinkedList<>();
    private volatile boolean running = true;

    // Producer — busy-waits when queue full
    void produce() {
        while (running) {
            if (queue.size() < 100) {  // Spin check
                queue.add(generateTask());
            }
        }
    }

    // Consumer — busy-waits when queue empty
    void consume() {
        while (running) {
            if (!queue.isEmpty()) {  // Spin check
                Task task = queue.poll();
                process(task);
            }
        }
    }
}
```

### Solution: BlockingQueue

```java
// ✅ Good — BlockingQueue handles coordination
class ProducerConsumer {
    private final BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);
    private volatile boolean running = true;

    // Producer — put() blocks if queue full
    void produce() {
        try {
            while (running) {
                Task task = generateTask();
                queue.put(task);  // Blocks if queue is full (backpressure)
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Consumer — take() blocks if queue empty
    void consume() {
        try {
            while (running) {
                Task task = queue.take();  // Blocks if empty
                process(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Full setup with ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(4);
BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

// Start producers
executor.execute(() -> {
    try {
        while (running) {
            queue.put(generateTask());
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

// Start consumers
for (int i = 0; i < 3; i++) {
    executor.execute(() -> {
        try {
            while (running) {
                Task task = queue.take();
                process(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
}
```

| BlockingQueue | Bounded | Use Case |
|---------------|---------|----------|
| `ArrayBlockingQueue` | ✅ | Fixed buffer, backpressure |
| `LinkedBlockingQueue` | Optional | Default unbounded (Integer.MAX) |
| `SynchronousQueue` | Size 0 | Direct hand-off (no buffer) |
| `PriorityBlockingQueue` | ❌ | Priority-based ordering |

### Key Takeaway
- `BlockingQueue.put()` blocks when full — natural backpressure
- `BlockingQueue.take()` blocks when empty — no busy waiting
- `ArrayBlockingQueue(100)` — bounded, prevents OOM
- Always handle `InterruptedException` — restore interrupt status
- Use `ExecutorService` to manage producer/consumer threads

---

## Scenario 4: Thread Pool Starvation

### Problem
All threads in a pool are blocked waiting for external calls. No threads available for new tasks — the app hangs.

```java
// ❌ Bad — all threads blocked on slow API calls
ExecutorService pool = Executors.newFixedThreadPool(4);

// 4 tasks all make slow API calls → all 4 threads blocked
for (int i = 0; i < 4; i++) {
    pool.execute(() -> {
        String result = slowApiCall();  // Blocks for 30s
        // All 4 threads stuck here
    });
}

// 5th task — waits forever for a free thread
pool.execute(() -> System.out.println("I'll never run"));
```

### Solution: Separate pools + timeouts + async

```java
// ✅ Good 1: Separate pools for I/O vs CPU
ExecutorService ioPool = Executors.newFixedThreadPool(20);   // I/O-bound
ExecutorService cpuPool = Executors.newFixedThreadPool(4);   // CPU-bound

// ✅ Good 2: Add timeouts to external calls
pool.execute(() -> {
    try {
        String result = callWithTimeout(apiCall(), 5, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
        // Fallback or retry
    }
});

// ✅ Good 3: Use CompletableFuture for non-blocking
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return apiCall();  // Non-blocking — doesn't hold a thread
}, ioPool).orTimeout(5, TimeUnit.SECONDS);

// ✅ Good 4: Right-size pool for the workload
// I/O-bound: more threads (threads are mostly waiting)
// CPU-bound: threads = CPU cores
int cpuCores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuBound = Executors.newFixedThreadPool(cpuCores);
ExecutorService ioBound = Executors.newFixedThreadPool(cpuCores * 4);

// ✅ Good 5: Use virtual threads (Java 21+) for I/O
try (var vtPool = Executors.newVirtualThreadPerTaskExecutor()) {
    // Millions of cheap threads — no starvation
    for (int i = 0; i < 10000; i++) {
        vtPool.execute(() -> {
            String result = slowApiCall();  // Virtual thread parks, not OS thread
        });
    }
}
```

### Key Takeaway
- Don't share one pool for CPU and I/O tasks — different sizing needs
- I/O-bound: more threads (they wait, not compute)
- CPU-bound: threads = CPU cores (more = context switching overhead)
- Always add timeouts to external calls (network, DB)
- `CompletableFuture.orTimeout()` prevents indefinite blocking
- Virtual threads (Java 21+) solve I/O starvation — millions of cheap threads

---

## Scenario 5: Concurrent HashMap Misuse

### Problem
Using `ConcurrentHashMap` with check-then-act patterns is still not thread-safe.

```java
// ❌ Bad — check-then-act is not atomic
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread A and B both check, both put → one overwrites
if (!map.containsKey("key")) {    // Check
    map.put("key", computeValue()); // Act — race condition!
}

// Another bad pattern
Integer count = map.get("counter");
if (count == null) {
    map.put("counter", 1);
} else {
    map.put("counter", count + 1);  // ❌ Lost update
}
```

### Solution: Atomic compound operations

```java
// ✅ Good — use atomic methods
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// putIfAbsent — atomic check-and-put
map.putIfAbsent("key", computeValue());

// computeIfAbsent — atomic check-and-compute
map.computeIfAbsent("key", k -> computeValue());

// computeIfPresent — atomic update if exists
map.computeIfPresent("counter", (k, v) -> v + 1);

// compute — atomic read-modify-write
map.compute("counter", (k, v) -> (v == null) ? 1 : v + 1);

// merge — atomic merge (great for counters)
map.merge("counter", 1, Integer::sum);  // If absent → 1, if present → sum

// replace — atomic compare-and-set
map.replace("key", oldValue, newValue);  // Only replaces if current == oldValue

// Practical: thread-safe frequency counter
ConcurrentHashMap<String, LongAdder> freq = new ConcurrentHashMap<>();
void count(String word) {
    freq.computeIfAbsent(word, k -> new LongAdder()).increment();
}
```

### Key Takeaway
- `ConcurrentHashMap` makes individual operations thread-safe
- Check-then-act (`containsKey` + `put`) is NOT atomic even with CHM
- Use `computeIfAbsent`, `merge`, `compute` for atomic compound operations
- `merge(key, 1, Integer::sum)` is the best pattern for counters
- `LongAdder` is better than `AtomicInteger` for high-contention counting

---

## 🔗 Related Topics
- [Concurrency](../intermediate/Concurrency.md)
- [Concurrency Advanced](../advanced/ConcurrencyAdvanced.md)
- [Collection Scenarios](CollectionScenarios.md)
