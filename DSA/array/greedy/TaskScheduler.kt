package array.greedy

/**
 * https://leetcode.com/problems/task-scheduler/
 *
 * Given a characters array tasks, representing tasks CPU needs to do, where each letter
 * represents a different task. Tasks could be done in any order. Each task is done in one unit
 * of time. For each unit of time, CPU could complete either one task or just be idle.
 *
 * However, there is a non-negative integer n that represents the cooldown period between
 * two same tasks (the same letter). Return the least number of units of time the CPU
 * will take to finish all the given tasks.
 *
 * Example 1:
 *
 * Input: tasks = ["A","A","A","B","B","B"], n = 2
 * Output: 8
 * Explanation: A -> B -> idle -> A -> B -> idle -> A -> B
 *
 * Example 2:
 *
 * Input: tasks = ["A","A","A","B","B","B"], n = 0
 * Output: 6
 *
 * Example 3:
 *
 * Input: tasks = ["A","A","A","A","A","A","B","C","D","E","F","G"], n = 2
 * Output: 16
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon — classic greedy)
 *
 * Key Insight: The most frequent task determines the framework. If max frequency is f,
 * there are (f-1) gaps of size n between occurrences. Fill gaps with other tasks.
 * Formula: max(total tasks, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
 */
fun main() {
    println(leastInterval(charArrayOf('A', 'A', 'A', 'B', 'B', 'B'), 2))
    println(leastInterval(charArrayOf('A', 'A', 'A', 'B', 'B', 'B'), 0))
    println(leastInterval(charArrayOf('A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G'), 2))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1) — fixed 26 letters
 *
 * Approach: Formula-based greedy
 *
 * Visual for ["A","A","A","B","B","B"], n=2:
 * A B _ | A B _ | A B
 * ^^^^^^^   ^^^^^^^   ^^^^
 *  slot 1    slot 2    slot 3
 *
 * maxFreq = 3 (A and B both appear 3 times)
 * numGroups = maxFreq - 1 = 2 (groups of size n+1=3)
 * emptySlots = numGroups * (n+1 - countOfMaxFreq) = 2 * (3 - 2) = 2
 * availableTasks = total - maxFreq * countOfMaxFreq = 6 - 3*2 = 0
 * idles = max(0, emptySlots - availableTasks) = max(0, 2-0) = 2
 * Result = total + idles = 6 + 2 = 8 ✅
 *
 * Simpler formula: max(total, (maxFreq-1)*(n+1) + countOfMaxFreq)
 */
fun leastInterval(tasks: CharArray, n: Int): Int {
    val freq = IntArray(26)
    for (task in tasks) {
        freq[task - 'A']++
    }

    val maxFreq = freq.max()
    val countOfMaxFreq = freq.count { it == maxFreq }

    return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + countOfMaxFreq)
}
