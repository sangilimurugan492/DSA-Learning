# Concurrency

## Q1: How do you create threads in Java?

```java
// 1. Extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}
new MyThread().start();

// 2. Implementing Runnable (preferred — can extend other class)
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Task running");
    }
}
new Thread(new MyTask()).start();

// 3. Anonymous Runnable
new Thread(new Runnable() {
    @Override
    public void run() { System.out.println("Anonymous"); }
}).start();

// 4. Lambda (Java 8+ — Runnable is functional interface)
new Thread(() -> System.out.println("Lambda thread")).start();

// 5. ExecutorService (preferred for production)
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> System.out.println("Pool task"));
executor.shutdown();
```

| Method | Pros | Cons |
|--------|------|------|
| extends Thread | Simple | Can't extend other class |
| implements Runnable | Flexible, can extend | Manual thread management |
| Lambda | Concise | Same as Runnable |
| ExecutorService | Pool management, reuse | Must shutdown |

---

## Q2: What is the difference between `start()` and `run()`?

```java
Thread t = new Thread(() -> System.out.println("Hello"));

t.start();  // ✅ Creates new thread, then calls run()
t.run();    // ❌ Runs in current thread — no new thread created!

// start() lifecycle:
// 1. New thread created
// 2. Thread enters RUNNABLE state
// 3. JVM calls run() on the new thread

// ⚠️ Can't call start() twice on same thread
Thread t2 = new Thread(() -> {});
t2.start();
// t2.start();  // ❌ IllegalThreadStateException
```

---

## Q3: What is `synchronized` and `volatile`?

```java
// synchronized — mutual exclusion (lock)
class Counter {
    private int count = 0;

    // Synchronized method — one thread at a time
    public synchronized void increment() {
        count++;
    }

    // Synchronized block — finer control
    public void increment2() {
        synchronized (this) {
            count++;
        }
    }

    // Static synchronized — class-level lock
    public static synchronized void staticMethod() {
        // Locks on Class object, not instance
    }
}

// volatile — visibility guarantee (not atomicity)
class Flag {
    private volatile boolean running = true;

    public void stop() { running = false; }  // Visible to all threads
    public boolean isRunning() { return running; }
}
```

| `synchronized` | `volatile` |
|----------------|-----------|
| Atomicity + visibility | Visibility only |
| Mutual exclusion (lock) | No lock |
| Can cause blocking | Never blocks |
| On methods or blocks | On fields only |
| Heavy (monitor overhead) | Light (memory barrier) |

```java
// ⚠️ volatile doesn't make count++ atomic
class BadCounter {
    private volatile int count = 0;
    public void increment() {
        count++;  // ❌ Read-modify-write — not atomic even with volatile
    }
}

// ✅ Use AtomicInteger for atomic operations
class GoodCounter {
    private AtomicInteger count = new AtomicInteger(0);
    public void increment() {
        count.incrementAndGet();  // ✅ Atomic
    }
}
```

---

## Q4: What are thread states?

```
NEW → RUNNABLE → (running/scheduled by OS)
                    ↓
              BLOCKED (waiting for monitor lock)
                    ↓
              RUNNABLE

              RUNNABLE → WAITING (wait(), join())
                            ↓ (notify/notifyAll)
                        RUNNABLE

              RUNNABLE → TIMED_WAITING (sleep(t), wait(t), join(t))
                            ↓ (timeout/notify)
                        RUNNABLE

              RUNNABLE → TERMINATED (run() completes)
```

```java
Thread t = new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

System.out.println(t.getState());  // NEW
t.start();
System.out.println(t.getState());  // RUNNABLE
Thread.sleep(100);
System.out.println(t.getState());  // TIMED_WAITING
Thread.sleep(2000);
System.out.println(t.getState());  // TERMINATED
```

---

## Q5: What is `wait()`, `notify()`, `notifyAll()`?

```java
// Producer-Consumer with wait/notify
class SharedBuffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    public SharedBuffer(int capacity) { this.capacity = capacity; }

    // Producer
    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();  // Release lock, wait for consumer
        }
        queue.add(item);
        notifyAll();  // Wake up consumers
    }

    // Consumer
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // Release lock, wait for producer
        }
        int item = queue.poll();
        notifyAll();  // Wake up producers
        return item;
    }
}
```

### Rules
- Must be called inside `synchronized` block (holding the monitor lock)
- `wait()` releases the lock and waits; `notify()` wakes one waiting thread
- Always use `while` loop (not `if`) to check condition — prevents spurious wakeup
- `notifyAll()` is safer than `notify()` — wakes all, they re-check condition

---

## Q6: What are common concurrency issues?

```java
// 1. Race condition — multiple threads modify shared state
class BadCounter {
    private int count = 0;
    public void increment() { count++; }  // ❌ Not thread-safe
}

// Fix: synchronized or AtomicInteger
class GoodCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
}

// 2. Deadlock — circular lock dependency
class Deadlock {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            synchronized (lock2) { }  // Thread A: holds lock1, wants lock2
        }
    }
    public void method2() {
        synchronized (lock2) {
            synchronized (lock1) { }  // Thread B: holds lock2, wants lock1
        }
    }
}
// Fix: Always acquire locks in the same order

// 3. Livelock — threads respond to each other, no progress
// Fix: Add random backoff

// 4. Starvation — low-priority thread never gets CPU
// Fix: Use fair locks (ReentrantLock(true))
```

---

## Q7: What is `ThreadLocal`?

```java
// ThreadLocal — per-thread variable, no sharing
public class UserService {
    // Each thread gets its own SimpleDateFormat (not thread-safe)
    private static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public String formatDate(Date date) {
        return dateFormat.get().format(date);  // Thread's own instance
    }
}

// Use cases:
// - SimpleDateFormat (not thread-safe)
// - Database connections (per-thread)
// - User session context (per-request thread)
// - Random (per-thread for less contention)

// ⚠️ Memory leak risk in thread pools
// ThreadLocal values persist as long as the thread lives
// In thread pools, threads are reused → values accumulate
// Fix: Call remove() when done
public void process() {
    try {
        context.set(userContext);
        doWork();
    } finally {
        context.remove();  // ✅ Clean up
    }
}
```

---

## 🔗 Related Topics
- [Exception Handling](ExceptionHandling.md)
- [Collections](Collections.md)
- [Concurrency Advanced](../advanced/ConcurrencyAdvanced.md)
- [Concurrency Scenarios](../scenario_based/ConcurrencyScenarios.md)
