package dp.subsequence.longest_increasing_subsequence

/**
 * Longest Increasing Subsequence (LIS) — LeetCode #300
 * https://leetcode.com/problems/longest-increasing-subsequence/
 *
 * Problem:
 * -------
 * Given an integer array, return the length of the longest strictly increasing subsequence.
 *
 * Example:  [10,9,2,5,3,7,101,18]  →  4  ([2,3,7,101] or [2,5,7,101])
 *           [0,1,0,3,2,3]  →  4  ([0,1,2,3])
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic DP + Binary Search)
 *
 * Two approaches:
 * 1. DP: O(N²) — dp[i] = LIS ending at index i
 * 2. Binary Search: O(N log N) — patience sorting
 */

fun main() {
    val nums = intArrayOf(10, 9, 2, 5, 3, 7, 101, 18)

    println("=== Method 1: DP O(N²) ===")
    println("LIS length: ${lengthOfLISDP(nums)}")

    println("\n=== Method 2: Binary Search O(N log N) ===")
    println("LIS length: ${lengthOfLISBinarySearch(nums)}")

    println("\n=== Step-by-step trace ===")
    listTrace(nums)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: DP — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * DP — dp[i] = length of LIS ending at index i.
 *
 * Core Idea:
 *   - For each i, check all j < i. If nums[j] < nums[i], dp[i] = max(dp[i], dp[j] + 1).
 *   - Answer = max(dp[0..n-1]).
 *
 * Time Complexity:  O(N²) — nested loops.
 * Space Complexity: O(N) — dp array.
 */
fun lengthOfLISDP(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val dp = IntArray(nums.size) { 1 }  // Each element is an LIS of length 1.

    for (i in nums.indices) {
        for (j in 0 until i) {
            if (nums[j] < nums[i]) {
                dp[i] = maxOf(dp[i], dp[j] + 1)
            }
        }
    }
    return dp.max()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH — Maintain a "tails" array where tails[i] = smallest tail of LIS of length i+1.
 *
 * Core Idea:
 *   - For each num, binary search its position in tails.
 *   - If num > all tails → append (extend LIS).
 *   - Else → replace the first element >= num (keep tails minimal).
 *
 * Key Insight:
 *   - We don't build the actual LIS — we maintain the smallest possible tails.
 *   - Smaller tails allow more future elements to extend the sequence.
 *
 * Time Complexity:  O(N log N) — binary search for each element.
 * Space Complexity: O(N) — tails array.
 */
fun lengthOfLISBinarySearch(nums: IntArray): Int {
    val tails = mutableListOf<Int>()

    for (num in nums) {
        var lo = 0
        var hi = tails.size
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (tails[mid] < num) lo = mid + 1
            else hi = mid
        }
        if (lo == tails.size) {
            tails.add(num)  // Extend LIS.
        } else {
            tails[lo] = num  // Replace — keep tails minimal.
        }
    }
    return tails.size
}

/**
 * Binary search with step-by-step trace.
 */
fun listTrace(nums: IntArray) {
    println("Input: ${nums.toList()}")
    val tails = mutableListOf<Int>()

    for (num in nums) {
        var lo = 0
        var hi = tails.size
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (tails[mid] < num) lo = mid + 1
            else hi = mid
        }
        if (lo == tails.size) {
            tails.add(num)
            println("  num=$num: extend → tails=$tails")
        } else {
            tails[lo] = num
            println("  num=$num: replace at $lo → tails=$tails")
        }
    }
    println("  LIS length: ${tails.size}")
}
