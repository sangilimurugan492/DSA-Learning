package dp.subsequence

/**
 * https://leetcode.com/problems/partition-equal-subset-sum/
 *
 * Given a non-empty array nums, determine if the array can be partitioned
 * into two subsets such that the sum of elements in both subsets is equal.
 *
 * Example 1: nums = [1,5,11,5] → Output: true (subsets [1,5,5] and [11])
 * Example 2: nums = [1,2,3,5] → Output: false
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (0/1 Knapsack variant — subset sum pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * First, observe: if total sum is ODD → impossible (can't split evenly)
 * If total sum is EVEN → we need to find a subset that sums to total/2.
 *
 * WHY? If one subset sums to total/2, the other MUST also sum to total/2.
 * So the problem reduces to: "Is there a subset that sums to target = total/2?"
 *
 * This is the classic SUBSET SUM problem (0/1 Knapsack variant):
 *   - 0/1 Knapsack: each item can be used AT MOST once (unlike Coin Change)
 *   - We ask: can we reach exactly `target` using some subset of numbers?
 *
 * Recurrence: dp[i][t] = can we make sum `t` using first `i` elements?
 *   - If we DON'T use nums[i]: dp[i][t] = dp[i-1][t]
 *   - If we DO use nums[i]:    dp[i][t] = dp[i-1][t - nums[i]]
 *   - dp[i][t] = dp[i-1][t] || dp[i-1][t - nums[i]]
 *
 * Base case: dp[...][0] = true (empty subset sums to 0)
 *
 * KEY DIFFERENCE from Coin Change:
 *   Coin Change: UNBOUNDED (use each coin unlimited times)
 *   Subset Sum:  0/1 (use each number AT MOST once)
 *   This is why we iterate i from 1..n (process each item once)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Partition Equal Subset Sum ===")
    println("Brute Force [1,5,11,5]: ${canPartitionBruteForce(intArrayOf(1, 5, 11, 5))}")
    println("Memoization [1,5,11,5]:  ${canPartitionMemo(intArrayOf(1, 5, 11, 5))}")
    println("Tabulation  [1,5,11,5]:  ${canPartitionTabulation(intArrayOf(1, 5, 11, 5))}")
    println("Optimal     [1,5,11,5]:  ${canPartitionOptimal(intArrayOf(1, 5, 11, 5))}")
    println("---")
    println("Optimal [1,2,3,5]:       ${canPartitionOptimal(intArrayOf(1, 2, 3, 5))}")
    println("Optimal [1,2,5]:         ${canPartitionOptimal(intArrayOf(1, 2, 5))}")
}

/**
 * BRUTE FORCE — Recursion (try all subsets)
 * Time Complexity: O(2^N) — each element: include or exclude
 * Space Complexity: O(N) — recursion depth
 *
 * For each element, try including it (if it doesn't exceed target) or excluding it.
 *
 * Recursion tree for [1,5,11,5], target=11:
 *                    f(0, 11)
 *                  /          \
 *          include 1         skip 1
 *          f(1, 10)         f(1, 11)
 *         /       \        /        \
 *   include 5    skip 5  include 5   skip 5
 *   f(2, 5)     f(2,10)  f(2, 6)    f(2,11)
 *    |           ...      ...        ...
 *   include 11: 5-11<0 skip
 *   skip 11: f(3,5)
 *     include 5: f(4,0) → TRUE! ✅
 *
 * Exponential! But with memoization, each (i, target) is computed once.
 */
fun canPartitionBruteForce(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2
    return subsetSumRec(nums, 0, target)
}

private fun subsetSumRec(nums: IntArray, idx: Int, remaining: Int): Boolean {
    if (remaining == 0) return true
    if (idx == nums.size || remaining < 0) return false
    // Try including nums[idx] or skipping it
    return subsetSumRec(nums, idx + 1, remaining - nums[idx]) ||
            subsetSumRec(nums, idx + 1, remaining)
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N × target) — each (i, remaining) computed once
 * Space Complexity: O(N × target) — memo + recursion stack
 *
 * Cache result for each (idx, remaining) pair.
 *
 * Trace for [1,5,11,5], target=11:
 * f(0, 11):
 *   include 1: f(1, 10)
 *     include 5: f(2, 5)
 *       include 11: 5-11<0 → false
 *       skip 11: f(3, 5)
 *         include 5: f(4, 0) → TRUE! ✅ (cache and return)
 *   → early return, no need to explore skip paths
 *
 * With memoization, we never recompute the same (idx, remaining) state.
 */
fun canPartitionMemo(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2
    val memo = Array(nums.size) { IntArray(target + 1) { -1 } }
    return subsetSumMemo(nums, 0, target, memo)
}

private fun subsetSumMemo(nums: IntArray, idx: Int, remaining: Int, memo: Array<IntArray>): Boolean {
    if (remaining == 0) return true
    if (idx == nums.size || remaining < 0) return false
    if (memo[idx][remaining] != -1) return memo[idx][remaining] == 1

    val result = subsetSumMemo(nums, idx + 1, remaining - nums[idx], memo) ||
            subsetSumMemo(nums, idx + 1, remaining, memo)
    memo[idx][remaining] = if (result) 1 else 0
    return result
}

/**
 * OPTIMAL-1 — Bottom-Up DP (2D Tabulation)
 * Time Complexity: O(N × target)
 * Space Complexity: O(N × target)
 *
 * dp[i][t] = true if we can make sum t using first i elements
 *
 * Recurrence:
 *   dp[i][t] = dp[i-1][t] || dp[i-1][t - nums[i-1]]
 *   (skip current)  (use current)
 *
 * Trace for [1,5,11,5], target=11:
 * (rows = first i elements, cols = sums 0..11)
 *
 *       0  1  2  3  4  5  6  7  8  9  10 11
 * i=0:  T  F  F  F  F  F  F  F  F  F  F  F   (empty set: only sum=0)
 * i=1:  T  T  F  F  F  F  F  F  F  F  F  F   (add 1: can make {0,1})
 * i=2:  T  T  F  F  F  T  T  F  F  F  F  F   (add 5: can make {0,1,5,6})
 * i=3:  T  T  F  F  F  T  T  F  F  F  F  T   (add 11: can make {0,1,5,6,11,12,16,17})
 * i=4:  T  T  F  F  F  T  T  F  F  F  T  T   (add 5: can make {0,1,5,6,10,11,...})
 *
 * dp[4][11] = dp[3][11] || dp[3][6] = T || T = TRUE ✅
 */
fun canPartitionTabulation(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2
    val n = nums.size
    val dp = Array(n + 1) { BooleanArray(target + 1) }

    // Base case: sum=0 is always achievable (empty subset)
    for (i in 0..n) dp[i][0] = true

    for (i in 1..n) {
        for (t in 1..target) {
            dp[i][t] = dp[i - 1][t]  // skip nums[i-1]
            if (t >= nums[i - 1]) {
                dp[i][t] = dp[i][t] || dp[i - 1][t - nums[i - 1]]  // use nums[i-1]
            }
        }
    }
    return dp[n][target]
}

/**
 * OPTIMAL-2 — Space-Optimized 1D DP
 * Time Complexity: O(N × target)
 * Space Complexity: O(target) ← only 1 array!
 *
 * Key insight: dp[i][t] only depends on dp[i-1][...]
 * We can use a single 1D array, but MUST iterate target in REVERSE!
 *
 * WHY reverse? Because dp[t - nums[i]] refers to the PREVIOUS row's value.
 * If we iterate forward, we'd overwrite dp[t - nums[i]] before using it,
 * effectively using the same element TWICE (unbounded knapsack behavior).
 * Iterating backward ensures each element is used AT MOST once (0/1 knapsack).
 *
 * Trace for [1,5,11,5], target=11:
 * dp = [T, F, F, F, F, F, F, F, F, F, F, F]
 *
 * Process 1: (reverse from 11 to 1)
 *   t=11: dp[11] = dp[11] || dp[10] = F || F = F
 *   t=10: dp[10] = dp[10] || dp[9] = F || F = F
 *   ...
 *   t=1:  dp[1] = dp[1] || dp[0] = F || T = T
 * dp = [T, T, F, F, F, F, F, F, F, F, F, F]
 *
 * Process 5: (reverse from 11 to 5)
 *   t=11: dp[11] = dp[11] || dp[6] = F || F = F
 *   t=10: dp[10] = dp[10] || dp[5] = F || F = F
 *   t=6:  dp[6] = dp[6] || dp[1] = F || T = T
 *   t=5:  dp[5] = dp[5] || dp[0] = F || T = T
 * dp = [T, T, F, F, F, T, T, F, F, F, F, F]
 *
 * Process 11: (reverse from 11 to 11)
 *   t=11: dp[11] = dp[11] || dp[0] = F || T = T ✅
 *
 * Early exit! dp[11] = true → return true
 */
fun canPartitionOptimal(nums: IntArray): Boolean {
    val sum = nums.sum()
    if (sum % 2 != 0) return false
    val target = sum / 2
    val dp = BooleanArray(target + 1)
    dp[0] = true

    for (num in nums) {
        for (t in target downTo num) {  // REVERSE iteration! (0/1 knapsack)
            dp[t] = dp[t] || dp[t - num]
        }
        if (dp[target]) return true  // early exit
    }
    return dp[target]
}
