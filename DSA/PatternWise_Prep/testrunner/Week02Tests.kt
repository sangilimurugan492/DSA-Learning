package testrunner

/**
 * Week02Tests.kt — Week 2: Sliding Window + Prefix Sum + Kadane's
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 */

class Week02Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 2 — Sliding Window + Prefix Sum ━━━━━━━")
        maxAverageSubarray(); runningSum(); maxSubArray(); totalFruit(); longestSubarray1s()
        grumpyOwner(); maxProductSubarray(); maxSubarraySumCircular(); productExceptSelf()
        subarraySumK(); contiguousArray(); longestSubstringNoRepeat(); characterReplacement()
        minSubArrayLen(); findAnagrams(); minWindow()
        // Complexity validation (Part B):
        subarraySumKBig(); longestSubstringBig()
    }

    fun maxAverageSubarray() = TestRunner.check("W2 #1 Max Average Subarray", block = { findMaxAverage(intArrayOf(1, 12, -5, -6, 50, 3), 4) }, verify = { it == 12.75 })
    fun runningSum() = TestRunner.check("W2 #2 Running Sum", block = { runningSum(intArrayOf(1, 2, 3, 4)).toList() }, verify = { it == listOf(1, 3, 6, 10) })
    fun maxSubArray() = TestRunner.check("W2 #3 Max Subarray (Kadane)", block = { maxSubArray(intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)) }, verify = { it == 6 })
    fun totalFruit() = TestRunner.check("W2 #4 Fruits into Baskets", block = { totalFruit(intArrayOf(1, 2, 1)) }, verify = { it == 3 })
    fun longestSubarray1s() = TestRunner.check("W2 #5 Longest Subarray After Delete", block = { longestSubarray(intArrayOf(1, 1, 0, 1)) }, verify = { it == 3 })
    fun grumpyOwner() = TestRunner.check("W2 #6 Grumpy Bookstore Owner", block = { maxSatisfied(intArrayOf(1, 0, 1, 2, 1, 1, 7, 5), intArrayOf(0, 1, 0, 1, 0, 1, 0, 1), 3) }, verify = { it == 16 })
    fun maxProductSubarray() = TestRunner.check("W2 #7 Max Product Subarray", block = { maxProduct(intArrayOf(2, 3, -2, 4)) }, verify = { it == 6 })
    fun maxSubarraySumCircular() = TestRunner.check("W2 #8 Max Sum Circular Subarray", block = { maxSubarraySumCircular(intArrayOf(5, -3, 5)) }, verify = { it == 10 })
    fun productExceptSelf() = TestRunner.check("W2 #9 Product Except Self", block = { productExceptSelf(intArrayOf(1, 2, 3, 4)).toList() }, verify = { it == listOf(24, 12, 8, 6) })
    fun subarraySumK() = TestRunner.check("W2 #10 Subarray Sum = K", block = { subarraySum(intArrayOf(1, 1, 1), 2) }, verify = { it == 2 })
    fun contiguousArray() = TestRunner.check("W2 #11 Contiguous Array", block = { findMaxLength(intArrayOf(0, 1)) }, verify = { it == 2 })
    fun longestSubstringNoRepeat() = TestRunner.check("W2 #12 Longest Substring No Repeat", block = { lengthOfLongestSubstring("abcabcbb") }, verify = { it == 3 })
    fun characterReplacement() = TestRunner.check("W2 #13 Character Replacement", block = { characterReplacement("AABABBA", 1) }, verify = { it == 4 })
    fun minSubArrayLen() = TestRunner.check("W2 #14 Min Size Subarray Sum", block = { minSubArrayLen(7, intArrayOf(2, 3, 1, 2, 4, 3)) }, verify = { it == 2 })
    fun findAnagrams() = TestRunner.check("W2 #15 Find Anagrams", block = { findAnagrams("cbaebabacd", "abc").sorted() }, verify = { it == listOf(0, 6) })
    fun minWindow() = TestRunner.check("W2 #16 Minimum Window Substring", block = { minWindow("ADOBECODEBANC", "ABC") }, verify = { it == "BANC" })

    // Part B — complexity validation
    fun subarraySumKBig() = TestRunner.check("W2-B Subarray Sum K, N=60k zeros, k=0", budgetMs = 300, block = { subarraySum(IntArray(60_000), 0) }, verify = { it == 1_800_030_000 })
    fun longestSubstringBig() = TestRunner.check("W2-B Longest Substring 'abc'×70k", budgetMs = 300, block = { lengthOfLongestSubstring("abc".repeat(70_000)) }, verify = { it == 3 })
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 2 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

fun findMaxAverage(nums: IntArray, k: Int): Double {
    // TODO: fixed-window running sum
    return 0.0
}

fun runningSum(nums: IntArray): IntArray {
    // TODO: prefix sum in place
    return intArrayOf()
}

fun maxSubArray(nums: IntArray): Int {
    // TODO: Kadane's running max
    return 0
}

fun totalFruit(fruits: IntArray): Int {
    // TODO: sliding window with 2-type map
    return 0
}

fun longestSubarray(nums: IntArray): Int {
    // TODO: sliding window, one zero skippable
    return 0
}

fun maxSatisfied(customers: IntArray, grumpy: IntArray, minutes: Int): Int {
    // TODO: base sum + best grumpy window
    return 0
}

fun maxProduct(nums: IntArray): Int {
    // TODO: track max and min products
    return 0
}

fun maxSubarraySumCircular(nums: IntArray): Int {
    // TODO: Kadane + total − min-Kadane trick
    return 0
}

fun productExceptSelf(nums: IntArray): IntArray {
    // TODO: prefix × suffix products
    return intArrayOf()
}

fun subarraySum(nums: IntArray, k: Int): Int {
    // TODO: hashmap prefix-sum counts
    return 0
}

fun findMaxLength(nums: IntArray): Int {
    // TODO: hashmap on count(1) − count(0)
    return 0
}

fun lengthOfLongestSubstring(s: String): Int {
    // TODO: sliding window, last-seen index
    return 0
}

fun characterReplacement(s: String, k: Int): Int {
    // TODO: window sized by max-frequency letter
    return 0
}

fun minSubArrayLen(target: Int, nums: IntArray): Int {
    // TODO: shrink window while sum ≥ target
    return 0
}

fun findAnagrams(s: String, p: String): List<Int> {
    // TODO: fixed-window char counts
    return listOf()
}

fun minWindow(s: String, t: String): String {
    // TODO: two pointers + need/have counts
    return ""
}
