package dp.subsequence.split_array_with_same_average

/**
 * Split Array With Same Average — LeetCode #805
 * https://leetcode.com/problems/split-array-with-same-average/
 *
 * Problem:
 * -------
 * Move one element of `nums` into array B (initially empty). After the move,
 * both A and B are non-empty and average(A) == average(B). Return true if possible.
 *
 * Example:  [1,2,3,4,5,6,7,8] → true   ([1,8] avg 4.5, rest avg 4.5)
 *           [3,1]              → false
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Hard — math reduction + subset-sum DP)
 *
 * Key Insight:
 * ------------
 * If A (size k) and B (size n-k) have equal averages, both averages equal total/n.
 * So the question becomes: does a subset of size k exist with sum == k * total / n?
 *
 * Prunings:
 *   1. k * total must be divisible by n.
 *   2. Only check k in 1..n/2 (complement subset covers the rest).
 *
 * Two approaches:
 * 1. Brute force bitmask: O(2^N × N)
 * 2. DP over (count → achievable sums): O(N² × S)
 */

fun main() {
    val tests = listOf(
        intArrayOf(1, 2, 3, 4, 5, 6, 7, 8),  // true
        intArrayOf(3, 1),                      // false
        intArrayOf(0)                          // false (can't make two non-empty arrays)
    )

    for (nums in tests) {
        println("nums = ${nums.toList()}")
        println("  Brute force : ${splitArraySameAverageBrute(nums)}")
        println("  DP (optimal): ${splitArraySameAverage(nums)}")
        println()
    }

    println("=== Step-by-step trace (nums = [1,2,3,4,5,6,7,8]) ===")
    splitArraySameAverageTrace(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: Brute Force — enumerate every subset via bitmask
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Brute force — check every non-empty proper subset's average against the global average.
 *
 * Core Idea:
 *   - For each mask in 1 until (1 shl n) - 1 (non-empty, not the full set),
 *     compute the subset's sum and count; if sum * n == count * total, return true.
 *
 * Key Insight:
 *   - sum(A)/k == total/n  ⇔  sum(A) * n == k * total  (avoids floating point!)
 *
 * Time Complexity:  O(2^N × N) — exponential, only OK for n ≤ ~20.
 * Space Complexity: O(1)
 */
fun splitArraySameAverageBrute(nums: IntArray): Boolean {
    val n = nums.size
    if (n < 2) return false
    val total = nums.sum()

    for (mask in 1 until (1 shl n) - 1) {
        var sum = 0
        var count = 0
        for (i in 0 until n) {
            if (mask and (1 shl i) != 0) {
                sum += nums[i]
                count++
            }
        }
        if (sum * n == count * total) return true
    }
    return false
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: DP over (count → achievable sums) — optimal
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — dp[k] = set of sums achievable using exactly k elements.
 *
 * Core Idea:
 *   - 0/1 knapsack over "counts": for each num, iterate k from high to low and
 *     extend dp[k] with (dp[k-1] + num). High-to-low prevents reusing an element.
 *   - Answer: exists k in 1..n/2 with (k * total) % n == 0 and
 *     k * total / n ∈ dp[k].
 *
 * Key Insight:
 *   - Only k ≤ n/2 needs checking: if a size-k subset works, its complement
 *     (size n-k) works too, and one of them has size ≤ n/2.
 *
 * Time Complexity:  O(N² × S) — S = distinct sums (≤ N × 10^4).
 * Space Complexity: O(N × S) — DP sets.
 */
fun splitArraySameAverage(nums: IntArray): Boolean {
    val n = nums.size
    if (n < 2) return false
    val total = nums.sum()

    // Quick pruning: if no k in 1..n/2 satisfies divisibility, answer is false.
    val validK = (1..n / 2).filter { (it.toLong() * total) % n == 0L }
    if (validK.isEmpty()) return false

    // dp[k] = set of subset sums using exactly k elements.
    val dp = Array(n / 2 + 1) { HashSet<Int>() }
    dp[0].add(0)

    for (num in nums) {
        // Iterate k from high to low so each element is used at most once.
        for (k in minOf(n / 2, dp.size - 1) downTo 1) {
            for (s in dp[k - 1]) dp[k].add(s + num)
        }
    }

    for (k in validK) {
        val target = k.toLong() * total / n
        if (target <= Int.MAX_VALUE && dp[k].contains(target.toInt())) return true
    }
    return false
}

/**
 * DP with step-by-step trace.
 */
fun splitArraySameAverageTrace(nums: IntArray) {
    val n = nums.size
    val total = nums.sum()
    println("Input: ${nums.toList()}, n=$n, total=$total")

    if (n < 2) {
        println("  Need at least 2 elements → false")
        return
    }

    for (k in 1..n / 2) {
        if ((k.toLong() * total) % n != 0L) {
            println("  k=$k: k*total ($k*$total=${k * total}) not divisible by $n → skip")
        } else {
            val target = k.toLong() * total / n
            println("  k=$k: target sum = $k*$total/$n = $target")
        }
    }

    val result = splitArraySameAverage(nums)
    println("  Result: $result")
}
