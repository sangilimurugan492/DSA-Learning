package array.greedy.shortest_job_first

/**
 * Shortest Job First (SJF) CPU Scheduling — GeeksforGeeks
 * https://www.geeksforgeeks.org/problems/shortest-job-first/1
 *
 * Problem:
 * -------
 * Given N processes with their burst times, schedule them using Shortest Job First (SJF)
 * (non-preemptive). All processes arrive at time 0. Return the average waiting time.
 *
 * Example:  bt = [4,3,7,1,2] → 4
 *           Sorted by burst: [1,2,3,4,7]
 *           Waiting times:   [0,1,3,6,10] → avg = 20/5 = 4
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic OS scheduling greedy)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — repeatedly find the shortest remaining job
 * 2. Sort: O(N log N) — sort by burst time, compute cumulative waiting time
 */

fun main() {
    val bt = intArrayOf(4, 3, 7, 1, 2)

    println("=== Method 1: Brute Force ===")
    println("avgWaitingTime(${bt.toList()}) = ${sjfBruteForce(bt)}")

    println("\n=== Method 2: Sort ===")
    println("avgWaitingTime(${bt.toList()}) = ${sjfSort(bt)}")

    println("\n=== Step-by-step trace ===")
    sjfTrace(bt)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Repeatedly find the shortest unprocessed job and schedule it.
 *
 * Core Idea:
 *   - Maintain a "completed" array to track which jobs are done.
 *   - In each iteration, scan all jobs to find the shortest unprocessed one.
 *   - Add its burst time to the running total (which becomes the waiting time
 *     for the next job).
 *
 * Time Complexity:  O(N²) — N iterations, each scanning N jobs.
 * Space Complexity: O(N) — completed array.
 */
fun sjfBruteForce(bt: IntArray): Int {
    val n = bt.size
    val completed = BooleanArray(n)
    var totalTime = 0
    var totalWaiting = 0

    for (done in 0 until n) {
        // Find the shortest unprocessed job
        var shortestIdx = -1
        var shortestBurst = Int.MAX_VALUE
        for (i in 0 until n) {
            if (!completed[i] && bt[i] < shortestBurst) {
                shortestBurst = bt[i]
                shortestIdx = i
            }
        }
        // This job's waiting time = totalTime so far
        totalWaiting += totalTime
        totalTime += bt[shortestIdx]
        completed[shortestIdx] = true
    }

    return totalWaiting / n
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: SORT — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT — Sort by burst time, then compute cumulative waiting time.
 *
 * Core Idea:
 *   - Since all processes arrive at time 0, simply sort by burst time.
 *   - The shortest job runs first, then the next shortest, etc.
 *   - Waiting time for job i = sum of burst times of all previously scheduled jobs.
 *   - totalWaiting = Σ (sum of all previous burst times)
 *
 * Key Insight:
 *   - SJF minimizes average waiting time because shorter jobs don't get stuck
 *     waiting behind longer ones.
 *   - By sorting, we process jobs in increasing burst order — the greedy choice
 *     is "always pick the shortest available job next."
 *
 * Trace for bt = [4,3,7,1,2]:
 *   Sorted: [1,2,3,4,7]
 *   Job 1 (burst=1): waiting = 0,  total = 1
 *   Job 2 (burst=2): waiting = 1,  total = 3
 *   Job 3 (burst=3): waiting = 3,  total = 6
 *   Job 4 (burst=4): waiting = 6,  total = 10
 *   Job 5 (burst=7): waiting = 10, total = 17
 *   Total waiting = 0+1+3+6+10 = 20, avg = 20/5 = 4 ✅
 *
 * Time Complexity:  O(N log N) — sorting dominates.
 * Space Complexity: O(N) — sorted copy (or O(1) if in-place sort is acceptable).
 */
fun sjfSort(bt: IntArray): Int {
    val sorted = bt.sorted()
    val n = sorted.size
    var totalWaiting = 0
    var totalTime = 0

    for (i in 0 until n) {
        // Current job waits for all previous jobs to finish
        totalWaiting += totalTime
        totalTime += sorted[i]
    }

    return totalWaiting / n
}

/**
 * Step-by-step trace with detailed output.
 */
fun sjfTrace(bt: IntArray) {
    val sorted = bt.sorted()
    val n = sorted.size
    var totalTime = 0
    var totalWaiting = 0

    println("Original burst times: ${bt.toList()}")
    println("Sorted burst times:   $sorted")
    println("─────────────────────────────────────────────────")
    println("Job # | Burst | Waiting Time | Total Time After")
    println("──────┼───────┼──────────────┼─────────────────")

    for (i in 0 until n) {
        val waiting = totalTime
        totalWaiting += waiting
        totalTime += sorted[i]
        println("  ${i + 1}   |   ${sorted[i]}   |      $waiting       |       $totalTime")
    }

    println("─────────────────────────────────────────────────")
    println("Total waiting time: $totalWaiting")
    println("Average waiting time: ${totalWaiting / n}")
}
