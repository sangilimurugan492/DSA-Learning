package dp.subsequence

/**
 * https://leetcode.com/problems/longest-increasing-subsequence/
 *
 * Given an integer array nums, return the length of the longest
 * strictly increasing subsequence.
 *
 * Example 1: nums = [10,9,2,5,3,7,101,18] → Output: 4 ([2,3,7,101] or [2,5,7,101])
 * Example 2: nums = [0,1,0,3,2,3] → Output: 4 ([0,1,2,3])
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 15 most asked — patience sorting is a must-know)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * A subsequence maintains relative order but can skip elements.
 * We want the LONGEST one where each element > previous.
 *
 * KEY INSIGHT for DP approach:
 * "For each element, what's the longest increasing subsequence ENDING here?"
 *   dp[i] = length of LIS ending at index i
 *   dp[i] = 1 + max(dp[j]) for all j < i where nums[j] < nums[i]
 *
 * WHY look at all j < i? Because nums[i] can be appended AFTER any
 * smaller element that comes before it. We take the MAXIMUM such subsequence.
 *
 * WHY "ending at i"? Because to extend the subsequence, future elements
 * only care about the LAST element (it must be smaller than them).
 * The internal elements don't matter for future decisions.
 *
 * This is DIFFERENT from LCS:
 *   LCS: compare two strings → 2D DP
 *   LIS: one array, increasing order → 1D DP (or binary search)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Longest Increasing Subsequence ===")
    println("Brute Force [10,9,2,5,3,7,101,18]: ${lisBruteForce(intArrayOf(10, 9, 2, 5, 3, 7, 101, 18))}")
    println("DP Tabulation [10,9,2,5,3,7,101,18]: ${lisDP(intArrayOf(10, 9, 2, 5, 3, 7, 101, 18))}")
    println("Binary Search [10,9,2,5,3,7,101,18]: ${lisBinarySearch(intArrayOf(10, 9, 2, 5, 3, 7, 101, 18))}")
    println("---")
    println("Binary Search [0,1,0,3,2,3]: ${lisBinarySearch(intArrayOf(0, 1, 0, 3, 2, 3))}")
    println("Binary Search [7,7,7,7]:      ${lisBinarySearch(intArrayOf(7, 7, 7, 7))}")
}

/**
 * BRUTE FORCE — Generate all subsequences, check each
 * Time Complexity: O(2^N × N) — 2^N subsequences, O(N) to check each
 * Space Complexity: O(N) — current subsequence
 *
 * For each element, either include it (if > last) or skip it.
 * This is exponential — impractical for N > 25.
 *
 * Recursion tree for [2,5,3,7]:
 *                    (0, -∞)
 *                  /          \
 *           include 2        skip 2
 *            (1, 2)          (1, -∞)
 *          /       \        /       \
 *     include 5   skip 5  include 5  skip 5
 *     (2, 5)    (2, 2)   (2, 5)    (2, -∞)
 *    ...         ...      ...        ...
 *
 * Exponential blowup! Same subproblems recomputed.
 */
fun lisBruteForce(nums: IntArray): Int {
    return lisRec(nums, 0, Int.MIN_VALUE)
}

private fun lisRec(nums: IntArray, idx: Int, prev: Int): Int {
    if (idx == nums.size) return 0
    val skip = lisRec(nums, idx + 1, prev)
    val take = if (nums[idx] > prev) {
        1 + lisRec(nums, idx + 1, nums[idx])
    } else {
        0
    }
    return maxOf(skip, take)
}

/**
 * BETTER — DP Tabulation O(N²)
 * Time Complexity: O(N²) — for each i, check all j < i
 * Space Complexity: O(N)
 *
 * dp[i] = length of LIS ending at index i
 * dp[i] = 1 + max(dp[j]) for all j < i where nums[j] < nums[i]
 * Answer = max(dp[i]) over all i
 *
 * Trace for [10,9,2,5,3,7,101,18]:
 *
 * i=0: dp[0]=1 (base, no j < 0)
 * i=1: nums[1]=9, no j with nums[j]<9 → dp[1]=1
 * i=2: nums[2]=2, no j with nums[j]<2 → dp[2]=1
 * i=3: nums[3]=5, j=2: nums[2]=2<5, dp[2]=1 → dp[3]=1+1=2
 * i=4: nums[4]=3, j=2: nums[2]=2<3, dp[2]=1 → dp[4]=1+1=2
 * i=5: nums[5]=7, j=2: dp[2]=1, j=3: dp[3]=2, j=4: dp[4]=2
 *       max=2 → dp[5]=1+2=3
 * i=6: nums[6]=101, j=5: dp[5]=3 is max → dp[6]=1+3=4
 * i=7: nums[7]=18, j=5: dp[5]=3 is max → dp[7]=1+3=4
 *
 * dp = [1, 1, 1, 2, 2, 3, 4, 4]
 * Answer: 4 ✅ ([2,5,7,101] or [2,3,7,101] or [2,3,7,18])
 */
fun lisDP(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val dp = IntArray(nums.size) { 1 }  // each element is an LIS of length 1
    var maxLen = 1

    for (i in nums.indices) {
        for (j in 0 until i) {
            if (nums[j] < nums[i]) {
                dp[i] = maxOf(dp[i], 1 + dp[j])
            }
        }
        maxLen = maxOf(maxLen, dp[i])
    }
    return maxLen
}

/**
 * OPTIMAL — Binary Search (Patience Sorting) O(N log N)
 * Time Complexity: O(N log N)
 * Space Complexity: O(N)
 *
 * KEY INSIGHT: Maintain an array `tails` where tails[i] = smallest tail element
 * of any increasing subsequence of length i+1.
 *
 * WHY does this work?
 * - If we have a subsequence of length 3 ending with 7, and later find one
 *   ending with 5, the one ending with 5 is BETTER because 5 < 7, meaning
 *   more future elements can extend it.
 * - So we always want the SMALLEST possible tail for each length.
 *
 * For each num in nums:
 *   - If num > all tails → extend longest subsequence (append to tails)
 *   - Otherwise → binary search for first tails[i] >= num, replace it
 *     (we found a better (smaller) tail for subsequence of length i+1)
 *
 * Trace for [10,9,2,5,3,7,101,18]:
 * num=10:  tails=[10]           → extend (10 > nothing)
 * num=9:   tails=[9]            → replace 10 (9 < 10, better tail for len=1)
 * num=2:   tails=[2]            → replace 9 (2 < 9, better tail for len=1)
 * num=5:   tails=[2,5]          → extend (5 > 2, new subsequence of len=2)
 * num=3:   tails=[2,3]          → replace 5 (3 < 5, better tail for len=2)
 * num=7:   tails=[2,3,7]        → extend (7 > 3, new subsequence of len=3)
 * num=101: tails=[2,3,7,101]    → extend (101 > 7, new subsequence of len=4)
 * num=18:  tails=[2,3,7,18]     → replace 101 (18 < 101, better tail for len=4)
 *
 * Length of tails = 4 ✅
 *
 * NOTE: tails is NOT the actual LIS! It only gives the correct LENGTH.
 * The actual LIS can be reconstructed by tracking predecessors (O(N²) approach).
 */
fun lisBinarySearch(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val tails = mutableListOf<Int>()

    for (num in nums) {
        var left = 0
        var right = tails.size
        // Binary search: find first tails[i] >= num
        while (left < right) {
            val mid = left + (right - left) / 2
            if (tails[mid] < num) {
                left = mid + 1
            } else {
                right = mid
            }
        }
        if (left == tails.size) {
            tails.add(num)   // extend: num is larger than all tails
        } else {
            tails[left] = num // replace: better (smaller) tail for this length
        }
    }
    return tails.size
}
