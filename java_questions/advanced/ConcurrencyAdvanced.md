# Concurrency Advanced

## Q1: What is ExecutorService and how do you use it?

```java
// Thread pool types
ExecutorService singleThread = Executors.newSingleThreadExecutor();
ExecutorService fixedPool = Executors.newFixedThreadPool(4);
ExecutorService cachedPool = Executors.newCachedThreadPool();
ExecutorService scheduled = Executors.newScheduledThreadPool(2);

// Recommended: ThreadPoolExecutor (explicit configuration)
ExecutorService executor = new ThreadPoolExecutor(
    2,                      // Core pool size
    4,                      // Max pool size
    60, TimeUnit.SECONDS,   // Keep-alive time
    new LinkedBlockingQueue<>(100),  // Work queue
    Thread::new,            // Thread factory
    new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy
);

// Submit tasks
Future<String> future = executor.submit(() -> {
    Thread.sleep(1000);
    return "Result";
});

// Callable (returns value)
Future<Integer> result = executor.submit(Callable task);

// Runnable (no return)
executor.execute(() -> System.out.println("Fire and forget"));

// Shutdown
executor.shutdown();        // Graceful — finish pending tasks
executor.shutdownNow();     // Forceful — interrupt running tasks
executor.awaitTermination(60, TimeUnit.SECONDS);  // Wait
```

### Rejection Policies
| Policy | Behavior |
|--------|----------|
| `AbortPolicy` (default) | Throws `RejectedExecutionException` |
| `CallerRunsPolicy` | Caller thread runs the task (backpressure) |
| `DiscardPolicy` | Silently discard |
| `DiscardOldestPolicy` | Discard oldest queued, then retry |

---

## Q2: What is CompletableFuture?

```java
// Async task with supplyAsync
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return fetchDataFromApi();  // Runs on ForkJoinPool
});

// Chain transformations
CompletableFuture<String> processed = future
    .thenApply(data -> data.toUpperCase())           // Transform
    .thenApply(upper -> upper + " processed");        // Chain

// Consume result
future.thenAccept(result -> System.out.println(result));  // No return
future.thenRun(() -> System.out.println("Done"));          // No input, no output

// Error handling
future.exceptionally(ex -> {
    System.err.println("Error: " + ex.getMessage());
    return "fallback";
});

// Handle both success and error
future.handle((result, ex) -> {
    if (ex != null) return "Error: " + ex.getMessage();
    return "Success: " + result;
});

// Combine multiple futures
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World");

// thenCombine — merge two results
CompletableFuture<String> combined = f1.thenCombine(f2, (a, b) -> a + " " + b);

// allOf — wait for all
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
all.thenRun(() -> System.out.println("Both done"));

// anyOf — first to complete
CompletableFuture<Object> any = CompletableFuture.anyOf(f1, f2);
any.thenAccept(result -> System.out.println("First: " + result));
```

### Custom Executor
```java
ExecutorService myExecutor = Executors.newFixedThreadPool(4);

CompletableFuture.supplyAsync(() -> fetchData(), myExecutor)
    .thenApplyAsync(data -> transform(data), myExecutor)
    .thenAcceptAsync(result -> save(result), myExecutor);
```

---

## Q3: What are Locks (ReentrantLock, ReadWriteLock, StampedLock)?

```java
// ReentrantLock — explicit lock (more flexible than synchronized)
class Counter {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();  // Must unlock in finally
        }
    }

    // Try with timeout
    public boolean tryIncrement() {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try { count++; return true; }
                finally { lock.unlock(); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    // Fair lock — FIFO ordering
    // new ReentrantLock(true)  // Slower but prevents starvation
}

// ReadWriteLock — multiple readers, one writer
class Cache {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Map<String, String> map = new HashMap<>();

    public String get(String key) {
        rwLock.readLock().lock();  // Multiple readers OK
        try {
            return map.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void put(String key, String value) {
        rwLock.writeLock().lock();  // Exclusive — blocks readers
        try {
            map.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

// StampedLock — optimistic reads (Java 8+)
class OptimisticCache {
    private final StampedLock lock = new StampedLock();
    private int x, y;

    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();  // No lock
        double currentX = x, currentY = y;
        if (!lock.validate(stamp)) {  // Check if changed
            stamp = lock.readLock();  // Upgrade to pessimistic read
            try {
                currentX = x; currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```

| Lock | Read | Write | Use Case |
|------|------|-------|----------|
| `synchronized` | Exclusive | Exclusive | Simple mutual exclusion |
| `ReentrantLock` | Exclusive | Exclusive | tryLock, fairness, interruptible |
| `ReadWriteLock` | Shared | Exclusive | Read-heavy, write-rare |
| `StampedLock` | Optimistic | Exclusive | Read-heavy, high throughput |

---

## Q4: What are atomic variables and CAS?

```java
// AtomicInteger — lock-free thread-safe counter
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();   // ++count (atomic)
counter.getAndIncrement();   // count++ (atomic)
counter.compareAndSet(0, 1); // CAS: if value==0, set to 1
counter.updateAndGet(x -> x * 2);  // Atomic update with function

// AtomicReference
AtomicReference<String> ref = new AtomicReference<>("initial");
ref.compareAndSet("initial", "updated");  // CAS

// AtomicStampedReference — prevents ABA problem
AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
int[] stampHolder = new int[1];
String value = stamped.get(stampHolder);
int stamp = stampHolder[0];
stamped.compareAndSet("A", "B", stamp, stamp + 1);

// LongAdder — high-contention counter (Java 8+)
LongAdder adder = new LongAdder();
adder.increment();  // Faster than AtomicLong under contention
adder.sum();        // Combine all cells

// LongAccumulator — generalized accumulator
LongAccumulator max = new LongAccumulator(Long::max, Long.MIN_VALUE);
max.accumulate(42);
max.accumulate(99);
max.get();  // 99
```

### CAS (Compare-And-Swap) Internals
```
CAS(expected, newValue):
  if (current_value == expected) {
      current_value = newValue;
      return true;   // Success
  }
  return false;      // Retry or fail

// Hardware-level atomic instruction (x86: CMPXCHG)
// No lock needed — CPU guarantees atomicity
// Spin on failure: retry loop (busy-wait)
```

---

## Q5: What are CountDownLatch, CyclicBarrier, and Semaphore?

```java
// CountDownLatch — one-time gate, wait for N events
CountDownLatch latch = new CountDownLatch(3);

// Workers
for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        doWork();
        latch.countDown();  // Signal done
    }).start();
}

latch.await();  // Block until count reaches 0
System.out.println("All workers done");

// CyclicBarrier — reusable barrier, all threads wait at same point
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All arrived, proceeding...");
});

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        System.out.println("Waiting at barrier");
        barrier.await();  // Block until 3 threads arrive
        System.out.println("Proceeding");
    }).start();
}

// Semaphore — permit-based access control
Semaphore semaphore = new Semaphore(3);  // 3 permits

for (int i = 0; i < 10; i++) {
    new Thread(() -> {
        try {
            semaphore.acquire();  // Get permit (blocks if 0)
            accessResource();      // Only 3 at a time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();  // Return permit
        }
    }).start();
}
```

| Synchronizer | Purpose | Reusable |
|-------------|---------|----------|
| `CountDownLatch` | Wait for N events to complete | ❌ One-time |
| `CyclicBarrier` | N threads wait for each other | ✅ Reusable |
| `Semaphore` | Limit concurrent access to N | ✅ Reusable |
| `Phaser` | Advanced phased synchronization | ✅ Reusable |

---

## Q6: What is the ForkJoinPool?

```java
// ForkJoinPool — for divide-and-conquer parallel tasks
class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start, end;
    private static final int THRESHOLD = 10_000;

    SumTask(long[] array, int start, int end) {
        this.array = array; this.start = start; this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += array[i];
            return sum;
        }
        int mid = (start + end) / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        left.fork();          // Async — run in pool
        long rightResult = right.compute();  // Sync — current thread
        long leftResult = left.join();       // Wait for fork
        return leftResult + rightResult;
    }
}

long[] data = new long[1_000_000];
// ... fill data ...
ForkJoinPool pool = new ForkJoinPool();
Long sum = pool.invoke(new SumTask(data, 0, data.length));

// parallelStream uses common ForkJoinPool
long sum2 = Arrays.stream(data).parallel().sum();
```

### Work-Stealing
- Each worker thread has its own deque (double-ended queue)
- Idle threads **steal** work from the tail of busy threads' queues
- Reduces contention and improves load balancing

---

## Q7: What are virtual threads (Java 21+)?

```java
// Platform threads — OS threads (1:1 mapping, expensive)
Thread platformThread = new Thread(() -> {
    System.out.println("Platform thread: " + Thread.currentThread());
});
// Each thread: ~1MB stack, OS scheduling

// Virtual threads — lightweight (M:N mapping, cheap)
Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("Virtual thread: " + Thread.currentThread());
});
// Each thread: ~few KB, JVM scheduling, millions possible

// Virtual thread with ExecutorService
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));
            return fetchData(i);
        });
    });
}  // Auto-close waits for all tasks

// Best for I/O-bound tasks (network, DB, file)
// NOT for CPU-bound tasks (use platform threads / parallelStream)
```

| Platform Thread | Virtual Thread |
|----------------|----------------|
| 1:1 with OS thread | M:N with OS threads |
| ~1MB stack | ~few KB stack |
| Max ~thousands | Max ~millions |
| OS scheduling | JVM scheduling |
| Good for CPU-bound | Good for I/O-bound |

---

## 🔗 Related Topics
- [Concurrency](../intermediate/Concurrency.md)
- [JVM Internals](JVMInternals.md)
- [Concurrency Scenarios](../scenario_based/ConcurrencyScenarios.md)
