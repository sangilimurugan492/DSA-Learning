package dp.meet_in_the_middle.partition_array_min_sum_difference

/**
 * Partition Array Into Two Arrays to Minimize Sum Difference — LeetCode #2035
 * https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/
 *
 * Problem:
 * -------
 * Given nums of length 2n, partition into arrays A and B (each length n)
 * minimizing |sum(A) - sum(B)|. Return the minimum score.
 *
 * Example:  [3,9,7,3]        → 2   (A=[3,3]=6, B=[9,7]=16... best is |10-12|=2)
 *           [-36,36]         → 72  (A=[-36], B=[36])
 *           [2,-1,0,4,-2,-9] → 0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hardest meet-in-the-middle variant)
 *
 * Key Insight:
 * ------------
 * diff = |2·sum(A) − total| → minimized when sum(A) is closest to total/2.
 * Split nums into halves of n elements. If A takes c elements from the left half,
 * it takes n−c from the right half. Enumerate (sum, count) for each half's subsets,
 * group left sums by count, sort, and binary-search the closest complement.
 *
 * Two approaches:
 * 1. Brute force recursion: O(C(2N, N))
 * 2. Meet in the middle: O(2^N × N)
 */

fun main() {
    val tests = listOf(
        intArrayOf(3, 9, 7, 3),          // 2
        intArrayOf(-36, 36),             // 72
        intArrayOf(2, -1, 0, 4, -2, -9)  // 0
    )

    for (nums in tests) {
        println("nums = ${nums.toList()}")
        println("  Brute force        : ${minimumDifferenceBrute(nums)}")
        println("  Meet in the middle : ${minimumDifference(nums)}")
        println()
    }

    println("=== Step-by-step trace (nums = [3,9,7,3]) ===")
    minimumDifferenceTrace(intArrayOf(3, 9, 7, 3))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: Brute Force — try all valid splits
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Brute force — assign each element to A or B, keeping both sizes ≤ n.
 *
 * Core Idea:
 *   - Recurse over every element with 2 choices; prune when a side exceeds n.
 *   - At the end (both sides full), record |sumA − sumB|.
 *
 * Key Insight:
 *   - C(2n, n) grows explosively (C(60,30) ≈ 10^17) — only for tiny inputs.
 *
 * Time Complexity:  O(C(2N, N)) — combinatorial.
 * Space Complexity: O(N) — recursion depth.
 */
fun minimumDifferenceBrute(nums: IntArray): Int {
    val n = nums.size / 2
    var best = Long.MAX_VALUE

    fun recurse(i: Int, countA: Int, sumA: Long) {
        if (i == nums.size) {
            val sumB = nums.sum().toLong() - sumA
            best = minOf(best, kotlin.math.abs(sumA - sumB))
            return
        }
        if (countA < n) recurse(i + 1, countA + 1, sumA + nums[i])  // nums[i] → A
        if (i - countA < n) recurse(i + 1, countA, sumA)            // nums[i] → B
    }
    recurse(0, 0, 0)
    return best.toInt()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: Meet in the Middle on (sum, count) — optimal
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Meet in the middle — enumerate (sum, count) per half, match complementary counts.
 *
 * Core Idea:
 *   - left = nums[0..n), right = nums[n..2n).
 *   - leftByCount[c] = sorted sums of left subsets with exactly c elements.
 *   - For each right subset (rSum, rCount): A takes rCount right elements, so it
 *     needs n − rCount left elements. We want lSum + rSum closest to total/2,
 *     i.e. lSum closest to (total/2 − rSum). Binary search leftByCount[n − rCount].
 *
 * Key Insight:
 *   - |sum(A) − sum(B)| = |2·sum(A) − total| → minimize by pushing sum(A) toward total/2.
 *   - Complementary counts: c (left) + (n − c) (right) = n total for A.
 *
 * Time Complexity:  O(2^N × N) — N = n (half length): enumerate, sort, binary search.
 * Space Complexity: O(2^N) — subset sums per half.
 */
fun minimumDifference(nums: IntArray): Int {
    val n = nums.size / 2
    val total = nums.sum().toLong()

    // Enumerate (sum, count) for each half.
    val leftSubsets = subsetSumCounts(nums, 0, n)
    val rightSubsets = subsetSumCounts(nums, n, 2 * n)

    // Group and sort left sums by count.
    val leftByCount = Array(n + 1) { ArrayList<Long>() }
    for ((sum, count) in leftSubsets) leftByCount[count].add(sum)
    for (c in 0..n) leftByCount[c].sort()

    var best = Long.MAX_VALUE

    for ((rSum, rCount) in rightSubsets) {
        // A takes rCount from right → needs n − rCount from left.
        val candidates = leftByCount[n - rCount]
        if (candidates.isEmpty()) continue

        // Want lSum closest to (total/2 − rSum). Use integer-safe target:
        // 2·(lSum + rSum) closest to total  ⇔  lSum closest to total/2 − rSum.
        val target = total - 2 * rSum  // = 2·(total/2 − rSum), avoids fractions

        val idx = candidates.binarySearch(target).let {
            if (it >= 0) it else -(it + 1)
        }

        if (idx < candidates.size) {
            val sumA = candidates[idx] + rSum
            best = minOf(best, kotlin.math.abs(2 * sumA - total))
        }
        if (idx > 0) {
            val sumA = candidates[idx - 1] + rSum
            best = minOf(best, kotlin.math.abs(2 * sumA - total))
        }
    }
    return best.toInt()
}

/**
 * Enumerate all 2^(to - from) subsets of nums[from, to) as (sum, count) pairs.
 * Incremental build: start with (0, 0); each element appends (sum + v, count + 1).
 */
private fun subsetSumCounts(nums: IntArray, from: Int, to: Int): List<Pair<Long, Int>> {
    val result = ArrayList<Pair<Long, Int>>(1 shl (to - from))
    result.add(0L to 0)
    for (i in from until to) {
        val v = nums[i].toLong()
        val cur = result.size
        for (j in 0 until cur) {
            val (s, c) = result[j]
            result.add((s + v) to (c + 1))
        }
    }
    return result
}

/**
 * Meet in the middle with step-by-step trace.
 */
fun minimumDifferenceTrace(nums: IntArray) {
    val n = nums.size / 2
    val total = nums.sum().toLong()
    println("Input: ${nums.toList()}, n=$n, total=$total")

    val leftSubsets = subsetSumCounts(nums, 0, n)
    val rightSubsets = subsetSumCounts(nums, n, 2 * n)
    println("  left subsets (sum,count)  = $leftSubsets")
    println("  right subsets (sum,count) = $rightSubsets")

    val leftByCount = Array(n + 1) { ArrayList<Long>() }
    for ((sum, count) in leftSubsets) leftByCount[count].add(sum)
    for (c in 0..n) leftByCount[c].sort()
    println("  leftByCount = ${leftByCount.mapIndexed { c, l -> "$c→$l" }}")

    var best = Long.MAX_VALUE
    for ((rSum, rCount) in rightSubsets) {
        val candidates = leftByCount[n - rCount]
        if (candidates.isEmpty()) continue
        val target = total - 2 * rSum
        val idx = candidates.binarySearch(target).let { if (it >= 0) it else -(it + 1) }
        var localBest = Long.MAX_VALUE
        if (idx < candidates.size) {
            val sumA = candidates[idx] + rSum
            localBest = minOf(localBest, kotlin.math.abs(2 * sumA - total))
        }
        if (idx > 0) {
            val sumA = candidates[idx - 1] + rSum
            localBest = minOf(localBest, kotlin.math.abs(2 * sumA - total))
        }
        println("  right (sum=$rSum, count=$rCount): target=$target → best diff=$localBest")
        best = minOf(best, localBest)
    }
    println("  Result: $best")
}
