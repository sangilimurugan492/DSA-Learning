package dp.subsequence

/**
 * https://leetcode.com/problems/target-sum/
 *
 * You are given an integer array nums and an integer target.
 * You want to build an expression out of nums by adding '+' or '-'
 * before each integer and then concatenate all the integers.
 * Return the number of different expressions that evaluate to target.
 *
 * Example 1: nums = [1,1,1,1,1], target = 3 → Output: 5
 *   (-1+1+1+1+1), (+1-1+1+1+1), (+1+1-1+1+1), (+1+1+1-1+1), (+1+1+1+1-1)
 * Example 2: nums = [1], target = 1 → Output: 1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Partition Equal Subset Sum variant — 0/1 knapsack)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * KEY INSIGHT: This is Partition Equal Subset Sum in disguise!
 *
 * We assign '+' or '-' to each number. Let's say:
 *   P = subset of numbers with '+' sign
 *   N = subset of numbers with '-' sign
 *
 * Then: sum(P) - sum(N) = target
 * And:  sum(P) + sum(N) = totalSum
 *
 * Adding both equations: 2 * sum(P) = target + totalSum
 * Therefore: sum(P) = (target + totalSum) / 2
 *
 * So the problem reduces to: "How many subsets of nums sum to (target + totalSum) / 2?"
 * This is EXACTLY the subset sum problem (0/1 knapsack)!
 *
 * WHY must (target + totalSum) be even? Because sum(P) must be an integer.
 * If (target + totalSum) is odd → no valid partition → return 0.
 * Also, if target > totalSum → impossible → return 0.
 *
 * Connection to other problems:
 *   Partition Equal Subset Sum: "Can we find a subset summing to totalSum/2?" (boolean)
 *   Target Sum: "How many subsets sum to (target+totalSum)/2?" (count)
 *   Same pattern, but COUNTING instead of just checking existence!
 *
 * Recurrence: dp[t] = number of subsets that sum to t
 *   For each num: dp[t] += dp[t - num] (for t from target down to num)
 *   REVERSE iteration (0/1 knapsack — each number used at most once)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Target Sum ===")
    println("Brute Force [1,1,1,1,1], target=3: ${findTargetSumWaysBruteForce(intArrayOf(1, 1, 1, 1, 1), 3)}")
    println("Memoization [1,1,1,1,1], target=3: ${findTargetSumWaysMemo(intArrayOf(1, 1, 1, 1, 1), 3)}")
    println("Tabulation  [1,1,1,1,1], target=3: ${findTargetSumWaysTabulation(intArrayOf(1, 1, 1, 1, 1), 3)}")
    println("---")
    println("Tabulation [1], target=1: ${findTargetSumWaysTabulation(intArrayOf(1), 1)}")
    println("Tabulation [1,2,3], target=0: ${findTargetSumWaysTabulation(intArrayOf(1, 2, 3), 0)}")
}

/**
 * BRUTE FORCE — Recursion (try '+' or '-' for each number)
 * Time Complexity: O(2^N) — each number has 2 choices
 * Space Complexity: O(N) — recursion stack
 *
 * For each number, try adding '+' or '-' and recurse.
 *
 * Recursion tree for [1,1,1,1,1], target=3:
 *                    f(0, 0)
 *                  /         \
 *          +1: f(1,1)    -1: f(1,-1)
 *          /    \          /      \
 *     f(2,2)  f(2,0)  f(2,0)  f(2,-2)
 *    /   \    /   \    /   \    /   \
 *   ...  ... ...  ... ...  ... ...  ...
 *
 * 2^5 = 32 leaves. 5 of them equal target=3.
 * Exponential! But with memoization, each (i, sum) is computed once.
 */
fun findTargetSumWaysBruteForce(nums: IntArray, target: Int): Int {
    return tsRec(nums, 0, 0, target)
}

private fun tsRec(nums: IntArray, idx: Int, currentSum: Int, target: Int): Int {
    if (idx == nums.size) return if (currentSum == target) 1 else 0
    val add = tsRec(nums, idx + 1, currentSum + nums[idx], target)
    val subtract = tsRec(nums, idx + 1, currentSum - nums[idx], target)
    return add + subtract
}

/**
 * BETTER — Top-Down DP (Memoization)
 * Time Complexity: O(N × sumRange) — each (idx, sum) computed once
 * Space Complexity: O(N × sumRange) — memo + recursion stack
 *
 * Cache result for each (idx, currentSum) pair.
 * Note: sum can be negative, so we offset by totalSum to use as array index.
 *
 * Trace for [1,1,1,1,1], target=3:
 * f(0, 0): try +1 → f(1, 1), try -1 → f(1, -1)
 *   f(1, 1): try +1 → f(2, 2), try -1 → f(2, 0)
 *     ... (5 levels deep, 2^5 = 32 states, but many overlap)
 *   With memoization, each unique (idx, sum) computed once.
 * Total ways = 5 ✅
 */
fun findTargetSumWaysMemo(nums: IntArray, target: Int): Int {
    val totalSum = nums.sum()
    // Offset for negative sums: sum ranges from -totalSum to +totalSum
    val offset = totalSum
    val memo = Array(nums.size) { IntArray(2 * totalSum + 1) { -1 } }
    return tsMemoHelper(nums, 0, 0, target, memo, offset)
}

private fun tsMemoHelper(nums: IntArray, idx: Int, currentSum: Int, target: Int, memo: Array<IntArray>, offset: Int): Int {
    if (idx == nums.size) return if (currentSum == target) 1 else 0
    val key = currentSum + offset
    if (key < 0 || key >= memo[0].size) return 0
    if (memo[idx][key] != -1) return memo[idx][key]

    val add = tsMemoHelper(nums, idx + 1, currentSum + nums[idx], target, memo, offset)
    val subtract = tsMemoHelper(nums, idx + 1, currentSum - nums[idx], target, memo, offset)
    memo[idx][key] = add + subtract
    return memo[idx][key]
}

/**
 * OPTIMAL — Bottom-Up DP (Subset Sum Reduction)
 * Time Complexity: O(N × targetSum)
 * Space Complexity: O(targetSum)
 *
 * Reduce to: "How many subsets sum to (target + totalSum) / 2?"
 * Then use 0/1 knapsack counting.
 *
 * dp[t] = number of subsets that sum to t
 * dp[0] = 1 (empty subset)
 * For each num: iterate t from targetSum down to num:
 *   dp[t] += dp[t - num]
 *
 * WHY reverse iteration? Same as Partition Equal Subset Sum:
 * each number can be used AT MOST once (0/1 knapsack).
 *
 * Trace for [1,1,1,1,1], target=3:
 * totalSum=5, target+totalSum=8, subsetTarget=8/2=4
 * "How many subsets of [1,1,1,1,1] sum to 4?"
 *
 * dp = [1, 0, 0, 0, 0]
 *
 * Process 1: dp[4]+=dp[3]=0, dp[3]+=dp[2]=0, dp[2]+=dp[1]=0, dp[1]+=dp[0]=1
 * dp = [1, 1, 0, 0, 0]
 *
 * Process 1: dp[4]+=dp[3]=0, dp[3]+=dp[2]=0, dp[2]+=dp[1]=1, dp[1]+=dp[0]=1+1=2
 * dp = [1, 2, 1, 0, 0]
 *
 * Process 1: dp[4]+=dp[3]=0, dp[3]+=dp[2]=1, dp[2]+=dp[1]=1+2=3, dp[1]+=dp[0]=2+1=3
 * dp = [1, 3, 3, 1, 0]
 *
 * Process 1: dp[4]+=dp[3]=1, dp[3]+=dp[2]=1+3=4, dp[2]+=dp[1]=3+3=6, dp[1]+=dp[0]=3+1=4
 * dp = [1, 4, 6, 4, 1]
 *
 * Process 1: dp[4]+=dp[3]=4, dp[3]+=dp[2]=4+6=10, dp[2]+=dp[1]=6+4=10, dp[1]+=dp[0]=4+1=5
 * dp = [1, 5, 10, 10, 5]
 *
 * dp[4] = 5 ✅ (5 ways to choose 4 ones out of 5 = C(5,4) = 5)
 */
fun findTargetSumWaysTabulation(nums: IntArray, target: Int): Int {
    val totalSum = nums.sum()

    // Edge cases
    if (target > totalSum || target < -totalSum) return 0
    if ((target + totalSum) % 2 != 0) return 0

    val subsetTarget = (target + totalSum) / 2
    val dp = IntArray(subsetTarget + 1)
    dp[0] = 1

    for (num in nums) {
        for (t in subsetTarget downTo num) {  // REVERSE! (0/1 knapsack)
            dp[t] += dp[t - num]
        }
    }
    return dp[subsetTarget]
}
