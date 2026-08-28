package array.binary_search.split_array_largest_sum

/**
 * Split Array Largest Sum — LeetCode #410
 * https://leetcode.com/problems/split-array-largest-sum/
 *
 * Problem:
 * -------
 * Given an array of non-negative integers and an integer m, split the array into m
 * non-empty contiguous subarrays. Minimize the largest sum among these m subarrays.
 * Return the minimized largest sum.
 *
 * Example:  nums=[7,2,5,10,8], m=2 → 18  (split: [7,2,5] + [10,8] → max(14,18)=18)
 *           nums=[1,2,3,4,5], m=2 → 9   (split: [1,2,3] + [4,5] → max(6,9)=9)
 *           nums=[1,4,4], m=3 → 4      (split: [1]+[4]+[4] → max(1,4,4)=4)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — Binary search on answer — asked at Google, Meta)
 *
 * Two approaches:
 * 1. Brute Force DP: O(n^2 * m) — DP over all split points
 * 2. Binary Search: O(n * log(sum(nums))) — binary search on the answer space [max(nums), sum(nums)]
 */

fun main() {
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAY for step-by-step walkthrough
    // nums = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    // m = 3 (split into 3 contiguous subarrays)
    // sum = 55, max = 10
    // Search range: [10, 55]
    // Answer: 21 (split: [1,2,3,4,5] + [6,7,8] + [9,10] → max(15,21,19)=21)
    // ─────────────────────────────────────────────────────────────
    val hugeArray = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("Array: ${hugeArray.toList()}")
    println("m = 3 (split into 3 subarrays)")
    println("sum = ${hugeArray.sum()}, max = ${hugeArray.max()}")
    println("Search range: [${hugeArray.max()}, ${hugeArray.sum()}]\n")

    println("=== Method 1: Brute Force ===")
    println("splitArray(hugeArray, m=3) = ${splitArrayBruteForce(hugeArray, 3)}")

    println("\n=== Method 2: Binary Search (step-by-step) ===")
    println("splitArray(hugeArray, m=3) = ${splitArrayVerbose(hugeArray, 3)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
    println("splitArray([7,2,5,10,8], m=2) = ${splitArray(intArrayOf(7, 2, 5, 10, 8), 2)}")
    println("splitArray([1,2,3,4,5], m=2) = ${splitArray(intArrayOf(1, 2, 3, 4, 5), 2)}")
    println("splitArray([1,4,4], m=3) = ${splitArray(intArrayOf(1, 4, 4), 3)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(n^2 * m)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Try all possible split points using DP.
 *
 * Core Idea:
 *   - Use dynamic programming: dp[i][j] = min largest sum for first i elements split into j parts.
 *   - For each split point k < i, dp[i][j] = min(max(dp[k][j-1], sum(k..i-1))).
 *
 * Time Complexity:  O(n^2 * m) — for each (i, j), try all split points k.
 * Space Complexity: O(n * m) — DP table.
 */
fun splitArrayBruteForce(nums: IntArray, m: Int): Int {
    val n = nums.size
    val prefix = IntArray(n + 1)
    for (i in 0 until n) prefix[i + 1] = prefix[i] + nums[i]

    // dp[i][j] = min largest sum for first i elements split into j parts
    val dp = Array(n + 1) { IntArray(m + 1) { Int.MAX_VALUE } }
    dp[0][0] = 0

    for (i in 1..n) {
        for (j in 1..minOf(i, m)) {
            for (k in 0 until i) {
                if (dp[k][j - 1] != Int.MAX_VALUE) {
                    val segmentSum = prefix[i] - prefix[k]
                    dp[i][j] = minOf(dp[i][j], maxOf(dp[k][j - 1], segmentSum))
                }
            }
        }
    }

    return dp[n][m]
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH ON ANSWER — O(n * log(sum(nums)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH ON ANSWER — Binary search on the range [max(nums), sum(nums)].
 *
 * Core Idea:
 *   - The answer is at least max(nums) (largest single element must be in some subarray).
 *   - The answer is at most sum(nums) (all elements in one subarray).
 *   - For a candidate maxSum, greedily check if we can split into ≤ m subarrays
 *     where each subarray sum ≤ maxSum.
 *   - If we can split into ≤ m subarrays → maxSum works → try smaller.
 *   - If we need > m subarrays → maxSum too small → try larger.
 *
 * Key Insight:
 *   - The relationship is monotonic: larger maxSum → fewer subarrays needed.
 *   - If maxSum works (≤ m subarrays), all larger values also work.
 *   - We want the MINIMUM working maxSum.
 *
 * Trace for nums=[1,2,3,4,5,6,7,8,9,10], m=3:
 *   left=10, right=55, mid=32 → 2 subarrays ≤ 3 → works → right=32
 *   left=10, right=32, mid=21 → 3 subarrays ≤ 3 → works → right=21
 *   left=10, right=21, mid=15 → 5 subarrays > 3 → too small → left=16
 *   left=16, right=21, mid=18 → 4 subarrays > 3 → too small → left=19
 *   left=19, right=21, mid=20 → 4 subarrays > 3 → too small → left=21
 *   left=21, right=21 → return 21 ✅
 *
 * Verification: maxSum=21 → [1,2,3,4,5,6]=21, [7,8]=15, [9,10]=19 → 3 subarrays, max=21. Works!
 *               maxSum=20 → need 4 subarrays > 3. Doesn't work!
 * So the answer is 21.
 *
 * Time Complexity:  O(n * log(sum(nums))) — binary search with O(n) greedy check per step.
 * Space Complexity: O(1) — constant variables.
 */
fun splitArray(nums: IntArray, m: Int): Int {
    var left = nums.max()
    var right = nums.sum()

    while (left < right) {
        val mid = left + (right - left) / 2

        if (canSplit(nums, mid, m)) {
            // mid works → try smaller maxSum
            right = mid
        } else {
            // mid too small → need larger maxSum
            left = mid + 1
        }
    }

    return left
}

/**
 * Helper: Can we split nums into ≤ m subarrays where each subarray sum ≤ maxSum?
 * Greedily add elements to current subarray until adding next would exceed maxSum.
 */
fun canSplit(nums: IntArray, maxSum: Int, m: Int): Boolean {
    var subarrays = 1
    var currentSum = 0

    for (num in nums) {
        if (currentSum + num > maxSum) {
            subarrays++
            currentSum = num
        } else {
            currentSum += num
        }
    }

    return subarrays <= m
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2b: BINARY SEARCH WITH VERBOSE STEP-BY-STEP OUTPUT — O(n * log(sum(nums)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH (VERBOSE) — Same logic as splitArray but prints every step.
 *
 * ── Step-by-step explanation for the 10-element array ──
 *   nums = [1,2,3,4,5,6,7,8,9,10], m=3
 *   Search range: [10, 55]
 *
 * STEP 1: left=10, right=55, mid=32
 *         Greedy split at maxSum=32: [1,2,3,4,5,6,7]=28, [8,9,10]=27 → 2 subarrays ≤ 3 → works → right=32
 * STEP 2: left=10, right=32, mid=21
 *         Greedy split at maxSum=21: [1,2,3,4,5,6]=21, [7,8]=15, [9,10]=19 → 3 subarrays ≤ 3 → works → right=21
 * STEP 3: left=10, right=21, mid=15
 *         Greedy split at maxSum=15: [1,2,3,4,5]=15, [6,7]=13, [8]=8, [9]=9, [10]=10 → 5 subarrays > 3 → left=16
 * STEP 4: left=16, right=21, mid=18
 *         Greedy split at maxSum=18: [1,2,3,4,5]=15, [6,7]=13, [8,9]=17, [10]=10 → 4 subarrays > 3 → left=19
 * STEP 5: left=19, right=21, mid=20
 *         Greedy split at maxSum=20: [1,2,3,4,5]=15, [6,7]=13, [8,9]=17, [10]=10 → 4 subarrays > 3 → left=21
 * STEP 6: left=21, right=21 → return 21 ✅
 *
 * Only 5 iterations — that's O(log(sum(nums))) in action!
 *
 * Time Complexity:  O(n * log(sum(nums))) — binary search with O(n) greedy check per step.
 * Space Complexity: O(1) — constant variables.
 */
fun splitArrayVerbose(nums: IntArray, m: Int): Int {
    var left = nums.max()
    var right = nums.sum()
    var step = 1

    println("  Array: ${nums.toList()}")
    println("  m (subarrays): $m")
    println("  Search range: [$left, $right]")
    println("  ──────────────────────────────────────────────")

    while (left < right) {
        val mid = left + (right - left) / 2

        // Greedy split at maxSum = mid
        var subarrays = 1
        var currentSum = 0
        val splits = StringBuilder()
        var currentSub = StringBuilder()

        for (num in nums) {
            if (currentSum + num > mid) {
                splits.append("[$currentSub]=$currentSum, ")
                currentSub = StringBuilder()
                currentSum = num
                subarrays++
            } else {
                currentSum += num
            }
            if (currentSub.isNotEmpty()) currentSub.append(",")
            currentSub.append(num)
        }
        splits.append("[$currentSub]=$currentSum")

        println("  STEP $step: left=$left, right=$right, mid=$mid (maxSum)")
        println("         Greedy split: $splits")
        println("         Subarrays needed: $subarrays (m=$m)")

        if (subarrays <= m) {
            println("         $subarrays ≤ $m → works! → try smaller → right = $mid")
            right = mid
        } else {
            println("         $subarrays > $m → too many! → need larger maxSum → left = ${mid + 1}")
            left = mid + 1
        }
        println()
        step++
    }

    println("  ──────────────────────────────────────────────")
    println("  left == right == $left → loop ends")
    println("  ✅ Minimum largest sum = $left")
    return left
}
