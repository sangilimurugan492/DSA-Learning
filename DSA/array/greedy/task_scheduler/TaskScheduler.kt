package array.greedy.task_scheduler

/**
 * Task Scheduler — LeetCode #621
 * https://leetcode.com/problems/task-scheduler/
 *
 * Problem:
 * -------
 * Given tasks and cooldown n, return the least number of units of time to finish all tasks.
 *
 * Example:  ["A","A","A","B","B","B"], n=2 → 8  (A→B→idle→A→B→idle→A→B)
 *           ["A","A","A","B","B","B"], n=0 → 6
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Two approaches:
 * 1. Simulation: O(N × time) — simulate each time unit with a priority queue
 * 2. Formula: O(N) — max(total, (maxFreq-1)*(n+1) + countOfMaxFreq)
 */

fun main() {
    println("=== Method 1: Simulation ===")
    println("leastInterval(['A','A','A','B','B','B'], 2) = ${leastIntervalSim(charArrayOf('A', 'A', 'A', 'B', 'B', 'B'), 2)}")

    println("\n=== Method 2: Formula ===")
    println("leastInterval(['A','A','A','B','B','B'], 2) = ${leastInterval(charArrayOf('A', 'A', 'A', 'B', 'B', 'B'), 2)}")
    println("leastInterval(['A','A','A','B','B','B'], 0) = ${leastInterval(charArrayOf('A', 'A', 'A', 'B', 'B', 'B'), 0)}")
    println("leastInterval(['A','A','A','A','A','A','B','C','D','E','F','G'], 2) = ${leastInterval(charArrayOf('A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G'), 2)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: SIMULATION — O(N × time)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SIMULATION — Use a max-heap of frequencies. Each cycle: pick most frequent task,
 * if none available → idle. Decrement frequency, put back after cooldown.
 *
 * Time Complexity:  O(N × result) — simulate each time unit.
 * Space Complexity: O(26) — frequencies.
 */
fun leastIntervalSim(tasks: CharArray, n: Int): Int {
    val freq = IntArray(26)
    for (task in tasks) freq[task - 'A']++

    val maxHeap = java.util.PriorityQueue<Int>(reverseOrder())
    for (f in freq) if (f > 0) maxHeap.offer(f)

    var time = 0
    while (maxHeap.isNotEmpty()) {
        val cycle = mutableListOf<Int>()
        // Pick up to n+1 tasks in one cycle
        for (i in 0..n) {
            if (maxHeap.isNotEmpty()) {
                cycle.add(maxHeap.poll())
            }
        }
        // Put back tasks that still have remaining count
        for (f in cycle) {
            if (f > 1) maxHeap.offer(f - 1)
        }
        // If heap is empty, this was the last cycle — count actual tasks
        time += if (maxHeap.isEmpty()) cycle.size else n + 1
    }
    return time
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: FORMULA — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * FORMULA — The most frequent task determines the framework.
 *
 * Core Idea:
 *   - If max frequency is f, there are (f-1) gaps of size n between occurrences.
 *   - Fill gaps with other tasks. If not enough tasks → idle.
 *   - Formula: max(total tasks, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
 *
 * Key Insight:
 *   - The most frequent task creates a "frame": A _ _ A _ _ A (for n=2, freq=3).
 *   - Other tasks fill the gaps. If they overflow the frame, no idle needed.
 *
 * Time Complexity:  O(N) — count frequencies.
 * Space Complexity: O(1) — 26 letters.
 */
fun leastInterval(tasks: CharArray, n: Int): Int {
    val freq = IntArray(26)
    for (task in tasks) freq[task - 'A']++

    val maxFreq = freq.max()
    val countOfMaxFreq = freq.count { it == maxFreq }

    return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
}
