# Shortest Job First (SJF) CPU Scheduling — Detailed Explanation

> **GeeksforGeeks** | [Problem Link](https://www.geeksforgeeks.org/problems/shortest-job-first/1)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Classic OS scheduling greedy)  
> **Topic:** Greedy, CPU Scheduling

---

## 📋 Problem Statement

Given N processes with their burst times (execution times), schedule them using the **Shortest Job First (SJF)** non-preemptive scheduling algorithm. All processes arrive at time 0. Return the **average waiting time** (integer division).

### What is SJF?

- **Non-preemptive SJF:** Once a process starts, it runs to completion. The CPU picks the process with the **shortest burst time** from the ready queue.
- **Goal:** Minimize the **average waiting time** across all processes.

### Example

| bt | Sorted | Waiting Times | Total Waiting | Average |
|----|--------|---------------|---------------|---------|
| `[4,3,7,1,2]` | `[1,2,3,4,7]` | `[0,1,3,6,10]` | 20 | 20/5 = **4** |

---

## 🧠 Concept Explanation

### Why SJF Minimizes Average Waiting Time

Consider two jobs: A (burst=10) and B (burst=1).

| Schedule | A waits | B waits | Average |
|----------|---------|---------|---------|
| A then B | 0 | 10 | 5.0 |
| B then A | 1 | 0 | 0.5 |

Running the **shorter job first** means fewer jobs pile up behind it. A long job at the front blocks everyone — a short job at the front finishes quickly and lets others proceed.

**Generalizing:** Sorting all jobs by burst time (ascending) and running them in that order gives the **optimal** (minimum) average waiting time. This is the greedy choice: *"always pick the shortest available job next."*

### Key Terms

| Term | Definition |
|------|-----------|
| **Burst Time** | CPU time required by the process |
| **Waiting Time** | Time the process spends waiting in the ready queue before it starts |
| **Turnaround Time** | Waiting Time + Burst Time (total time from arrival to completion) |
| **Completion Time** | The time at which the process finishes |

### Formula

For jobs sorted by burst time `[b₁, b₂, ..., bₙ]`:

```
Waiting time of job i = Σ (burst times of all jobs before i)
                     = b₁ + b₂ + ... + bᵢ₋₁

Average waiting time = (Σ all waiting times) / N
```

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Maintain a "completed" array. In each iteration, scan all jobs to find the shortest unprocessed one. Schedule it, add its burst time to the running total.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | N iterations × N scans |
| **Space** | O(N) | Completed array |

---

## 🧩 Method 2: Sort — O(N log N)

### Core Idea

Since all processes arrive at time 0, simply **sort by burst time**. Then compute the cumulative waiting time in a single pass.

### Key Insight

> Sorting by burst time directly gives the SJF schedule. The waiting time for each job is the sum of all previous burst times. A single pass computes the total.

### Dry Run — `bt = [4,3,7,1,2]`

**Step 1:** Sort → `[1, 2, 3, 4, 7]`

| Job # | Burst | Waiting Time | Total Time After |
|:-----:|:-----:|:------------:|:----------------:|
| 1 | 1 | 0 | 1 |
| 2 | 2 | 1 | 3 |
| 3 | 3 | 3 | 6 |
| 4 | 4 | 6 | 10 |
| 5 | 7 | 10 | 17 |

**Total waiting time** = 0 + 1 + 3 + 6 + 10 = **20**  
**Average** = 20 / 5 = **4** ✅

### Code

```kotlin
fun sjfSort(bt: IntArray): Int {
    val sorted = bt.sorted()
    val n = sorted.size
    var totalWaiting = 0
    var totalTime = 0

    for (i in 0 until n) {
        totalWaiting += totalTime
        totalTime += sorted[i]
    }

    return totalWaiting / n
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sorting dominates |
| **Space** | O(N) | Sorted copy |

---

## 📊 Comparison Table

| Aspect | Brute Force | Sort |
|--------|-------------|------|
| **Time** | O(N²) | O(N log N) |
| **Space** | O(N) | O(N) |
| **Handles arrival times?** | ✅ (easily extended) | ❌ (only arrival=0) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Greedy choice:** Always pick the shortest available job next — this minimizes average waiting time.
2. **Why it works:** Shorter jobs finishing first means fewer jobs accumulate waiting time behind them.
3. **Arrival time = 0 assumption:** When all jobs arrive at the same time, sorting suffices. If jobs have different arrival times, a min-heap is needed to always pick the shortest available job at each time step.
4. **Non-preemptive vs Preemptive:** This solution is non-preemptive (jobs run to completion). Preemptive SJF (a.k.a. SRTF) interrupts a running job if a shorter one arrives.
5. **Pattern:** Greedy scheduling — extends to Priority Scheduling, Round Robin, Task Scheduler (LeetCode #621).

---

## 📚 Related Problems

| Problem | Platform | Difficulty |
|---------|----------|------------|
| Shortest Job First | [GeeksforGeeks](https://www.geeksforgeeks.org/problems/shortest-job-first/1) | Medium |
| Task Scheduler | [LeetCode #621](https://leetcode.com/problems/task-scheduler/) | Medium |
| Single-Threaded CPU | [LeetCode #1834](https://leetcode.com/problems/single-threaded-cpu/) | Medium |
| Process Tasks Using Servers | [LeetCode #1882](https://leetcode.com/problems/process-tasks-using-servers/) | Medium |
