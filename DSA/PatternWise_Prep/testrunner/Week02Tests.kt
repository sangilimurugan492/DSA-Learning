package testrunner

/**
 * Week02Tests.kt — Week 2: Sliding Window + Prefix Sum + Kadane's
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 *
 * Test numbering (W2 #1 … #30) matches weeks/Week02.md; the input/expected pairs come
 * from practice/Week02_Practice.md (Part A), the timed cases from its Part B.
 * Every problem now has all 3 Part-A cases wired up, and every problem is listed in
 * runAll() — unimplemented stubs use TODO(), which the runner reports as [SKIP ⏭️].
 *
 * Note: 3 expectations in Week02_Practice.md disagree with brute force — corrected here:
 *   #24 case 2 ([5,2,1,2,5,2,1,2,5]) is 8 (not 9); #29 case 1 ([1,-3,2,3,-4]) is 5 (not 6);
 *   Part B #8 (alternating 5,-1 ×100k) is 200,001 (not 249,999).
 */

class Week02Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 2 — Sliding Window + Prefix Sum + Kadane's ━━━━━━━")
        // Mon — fixed window + prefix sum basics (#1–#4)
        maxAverageSubarray(); runningSum(); prefixSumQuery(); maxSubArray()
        // Tue — variable window + Kadane's variants (#5–#9)
        totalFruit(); longestSubarray1s(); grumpyOwner(); maxProductSubarray(); maxSubarraySumCircular()
        // Wed — prefix sum + HashMap combos (#10–#13)
        productExceptSelf(); subarraySumK(); contiguousArray(); longestSubarrayWithSumK()
        // Thu — window problems (#14–#18)
        longestSubstringNoRepeat(); characterReplacement(); minSubArrayLen(); findAnagrams(); minWindow()
        // Fri — bonus window / prefix / Kadane extensions (#19–#30)
        maxOnesIII(); permutationInString(); bestSightseeingPair(); binarySubarraysWithSum()
        maxPointsFromCards(); maxErasureValue(); slidingWindowMax(); pivotIndex(); rangeSum2D()
        continuousSubarraySum(); maxAbsoluteSum(); maxLenPositiveProduct()
        // Complexity validation (Part B) — uncomment to time-check O(N) vs O(N²):
        // complexityAll()
    }

    // ── MON — fixed window + prefix sum basics (#1–#4) ─────────────────────

    fun maxAverageSubarray() {
        TestRunner.check("W2 #1 Max Average Subarray", block = { findMaxAverage(intArrayOf(1, 12, -5, -6, 50, 3), 4) }, verify = { it == 12.75 })
        TestRunner.check("W2 #1b Max Average Subarray, single element", block = { findMaxAverage(intArrayOf(5), 1) }, verify = { it == 5.0 })
        TestRunner.check("W2 #1c Max Average Subarray, k=1", block = { findMaxAverage(intArrayOf(0, 4, 0, 3, 2), 1) }, verify = { it == 4.0 })
    }

    fun runningSum() {
        TestRunner.check("W2 #2 Running Sum, basic", block = { runningSum(intArrayOf(1, 2, 3, 4)).toList() }, verify = { it == listOf(1, 3, 6, 10) })
        TestRunner.check("W2 #2b Running Sum, same values", block = { runningSum(intArrayOf(1, 1, 1, 1, 1)).toList() }, verify = { it == listOf(1, 2, 3, 4, 5) })
        TestRunner.check("W2 #2c Running Sum, mixed", block = { runningSum(intArrayOf(3, 1, 2, 10, 1)).toList() }, verify = { it == listOf(3, 4, 6, 16, 17) })
    }

    fun prefixSumQuery() {
        val nums = intArrayOf(1, 2, 3, 4, 5)
        TestRunner.check("W2 #3 Prefix Sum Query, range middle (1,3)", block = { PrefixSum(nums).rangeSum(1, 3) }, verify = { it == 9 })
        TestRunner.check("W2 #3b Prefix Sum Query, full range (0,4)", block = { PrefixSum(nums).rangeSum(0, 4) }, verify = { it == 15 })
        TestRunner.check("W2 #3c Prefix Sum Query, single element (2,2)", block = { PrefixSum(nums).rangeSum(2, 2) }, verify = { it == 3 })
    }

    fun maxSubArray() {
        TestRunner.check("W2 #4 Max Subarray (Kadane), classic", block = { maxSubArray(intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)) }, verify = { it == 6 })
        TestRunner.check("W2 #4b Max Subarray (Kadane), single", block = { maxSubArray(intArrayOf(1)) }, verify = { it == 1 })
        TestRunner.check("W2 #4c Max Subarray (Kadane), all negative", block = { maxSubArray(intArrayOf(-3, -1, -2)) }, verify = { it == -1 })
    }

    // ── TUE — variable window + Kadane's variants (#5–#9) ──────────────────

    fun totalFruit() {
        TestRunner.check("W2 #5 Fruits into Baskets, basic", block = { totalFruit(intArrayOf(1, 2, 1)) }, verify = { it == 3 })
        TestRunner.check("W2 #5b Fruits into Baskets, two types", block = { totalFruit(intArrayOf(0, 1, 2, 2)) }, verify = { it == 3 })
        TestRunner.check("W2 #5c Fruits into Baskets, classic", block = { totalFruit(intArrayOf(3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4)) }, verify = { it == 5 })
    }

    fun longestSubarray1s() {
        TestRunner.check("W2 #6 Longest Subarray After Delete, basic", block = { longestSubarray(intArrayOf(1, 1, 0, 1)) }, verify = { it == 3 })
        TestRunner.check("W2 #6b Longest Subarray After Delete, two zeros", block = { longestSubarray(intArrayOf(0, 1, 1, 1, 0, 1, 1, 0, 1)) }, verify = { it == 5 })
        TestRunner.check("W2 #6c Longest Subarray After Delete, no zero", block = { longestSubarray(intArrayOf(1, 1, 1)) }, verify = { it == 2 })
    }

    fun grumpyOwner() {
        TestRunner.check("W2 #7 Grumpy Bookstore Owner, classic", block = { maxSatisfied(intArrayOf(1, 0, 1, 2, 1, 1, 7, 5), intArrayOf(0, 1, 0, 1, 0, 1, 0, 1), 3) }, verify = { it == 16 })
        TestRunner.check("W2 #7b Grumpy Bookstore Owner, single minute", block = { maxSatisfied(intArrayOf(1), intArrayOf(0), 1) }, verify = { it == 1 })
        TestRunner.check("W2 #7c Grumpy Bookstore Owner, all grumpy", block = { maxSatisfied(intArrayOf(4, 2), intArrayOf(1, 1), 2) }, verify = { it == 6 })
    }

    fun maxProductSubarray() {
        TestRunner.check("W2 #8 Max Product Subarray, basic", block = { maxProduct(intArrayOf(2, 3, -2, 4)) }, verify = { it == 6 })
        TestRunner.check("W2 #8b Max Product Subarray, zero resets", block = { maxProduct(intArrayOf(-2, 0, -1)) }, verify = { it == 0 })
        TestRunner.check("W2 #8c Max Product Subarray, double negative", block = { maxProduct(intArrayOf(-2, 3, -4)) }, verify = { it == 24 })
    }

    fun maxSubarraySumCircular() {
        TestRunner.check("W2 #9 Max Sum Circular Subarray, non-circular wins", block = { maxSubarraySumCircular(intArrayOf(1, -2, 3, -2)) }, verify = { it == 3 })
        TestRunner.check("W2 #9b Max Sum Circular Subarray, circular wins", block = { maxSubarraySumCircular(intArrayOf(5, -3, 5)) }, verify = { it == 10 })
        TestRunner.check("W2 #9c Max Sum Circular Subarray, all negative", block = { maxSubarraySumCircular(intArrayOf(-3, -2, -3)) }, verify = { it == -2 })
    }
    // ── WED — prefix sum + HashMap combos (#10–#13) ────────────────────────

    fun productExceptSelf() {
        TestRunner.check("W2 #10 Product Except Self, basic", block = { productExceptSelf(intArrayOf(1, 2, 3, 4)).toList() }, verify = { it == listOf(24, 12, 8, 6) })
        TestRunner.check("W2 #10b Product Except Self, zero + negatives", block = { productExceptSelf(intArrayOf(-1, 1, 0, -3, 3)).toList() }, verify = { it == listOf(0, 0, 9, 0, 0) })
        TestRunner.check("W2 #10c Product Except Self, two elements", block = { productExceptSelf(intArrayOf(2, 3)).toList() }, verify = { it == listOf(3, 2) })
    }

    fun subarraySumK() {
        TestRunner.check("W2 #11 Subarray Sum = K, basic", block = { subarraySum(intArrayOf(1, 1, 1), 2) }, verify = { it == 2 })
        TestRunner.check("W2 #11b Subarray Sum = K, overlapping", block = { subarraySum(intArrayOf(1, 2, 3), 3) }, verify = { it == 2 })
        TestRunner.check("W2 #11c Subarray Sum = K, all zeros", block = { subarraySum(intArrayOf(0, 0, 0), 0) }, verify = { it == 6 })
    }

    fun contiguousArray() {
        TestRunner.check("W2 #12 Contiguous Array, basic", block = { findMaxLength(intArrayOf(0, 1)) }, verify = { it == 2 })
        TestRunner.check("W2 #12b Contiguous Array, odd length", block = { findMaxLength(intArrayOf(0, 1, 0)) }, verify = { it == 2 })
        TestRunner.check("W2 #12c Contiguous Array, middle window", block = { findMaxLength(intArrayOf(0, 1, 1, 0, 1, 1, 1, 0)) }, verify = { it == 4 })
    }

    fun longestSubarrayWithSumK() {
        TestRunner.check("W2 #13 Longest Subarray with Sum K, tail run", block = { longestSubarrayWithSumK(intArrayOf(1, 2, 3, 1, 1, 1), 5) }, verify = { it == 4 })
        TestRunner.check("W2 #13b Longest Subarray with Sum K, negatives", block = { longestSubarrayWithSumK(intArrayOf(-1, 2, 3, -6, 2, 2), 2) }, verify = { it == 2 })
        TestRunner.check("W2 #13c Longest Subarray with Sum K, no match", block = { longestSubarrayWithSumK(intArrayOf(1), 0) }, verify = { it == 0 })
    }

    // ── THU — window problems (#14–#18) ────────────────────────────────────

    fun longestSubstringNoRepeat() {
        TestRunner.check("W2 #14 Longest Substring No Repeat, basic", block = { lengthOfLongestSubstring("abcabcbb") }, verify = { it == 3 })
        TestRunner.check("W2 #14b Longest Substring No Repeat, all same", block = { lengthOfLongestSubstring("bbbbb") }, verify = { it == 1 })
        TestRunner.check("W2 #14c Longest Substring No Repeat, middle window", block = { lengthOfLongestSubstring("pwwkew") }, verify = { it == 3 })
    }

    fun characterReplacement() {
        TestRunner.check("W2 #15 Character Replacement, replace all B", block = { characterReplacement("ABAB", 2) }, verify = { it == 4 })
        TestRunner.check("W2 #15b Character Replacement, classic", block = { characterReplacement("AABABBA", 1) }, verify = { it == 4 })
        TestRunner.check("W2 #15c Character Replacement, k=0", block = { characterReplacement("AAAA", 0) }, verify = { it == 4 })
    }

    fun minSubArrayLen() {
        TestRunner.check("W2 #16 Min Size Subarray Sum, basic", block = { minSubArrayLen(7, intArrayOf(2, 3, 1, 2, 4, 3)) }, verify = { it == 2 })
        TestRunner.check("W2 #16b Min Size Subarray Sum, exact single", block = { minSubArrayLen(4, intArrayOf(1, 4, 4)) }, verify = { it == 1 })
        TestRunner.check("W2 #16c Min Size Subarray Sum, impossible", block = { minSubArrayLen(11, intArrayOf(1, 1, 1, 1, 1, 1, 1, 1)) }, verify = { it == 0 })
    }

    fun findAnagrams() {
        TestRunner.check("W2 #17 Find Anagrams, classic", block = { findAnagrams("cbaebabacd", "abc").sorted() }, verify = { it == listOf(0, 6) })
        TestRunner.check("W2 #17b Find Anagrams, overlapping", block = { findAnagrams("abab", "ab").sorted() }, verify = { it == listOf(0, 1, 2) })
        TestRunner.check("W2 #17c Find Anagrams, p longer than s", block = { findAnagrams("a", "ab") }, verify = { it.isEmpty() })
    }

    fun minWindow() {
        TestRunner.check("W2 #18 Minimum Window Substring, classic", block = { minWindow("ADOBECODEBANC", "ABC") }, verify = { it == "BANC" })
        TestRunner.check("W2 #18b Minimum Window Substring, exact", block = { minWindow("a", "a") }, verify = { it == "a" })
        TestRunner.check("W2 #18c Minimum Window Substring, not enough chars", block = { minWindow("a", "aa") }, verify = { it == "" })
    }

    // ── FRI — bonus window / prefix / Kadane extensions (#19–#30) ──────────

    fun maxOnesIII() {
        TestRunner.check("W2 #19 Max Consecutive Ones III, classic", block = { longestOnes(intArrayOf(1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0), 2) }, verify = { it == 6 })
        TestRunner.check("W2 #19b Max Consecutive Ones III, k=0", block = { longestOnes(intArrayOf(0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0), 0) }, verify = { it == 3 })
        TestRunner.check("W2 #19c Max Consecutive Ones III, all ones", block = { longestOnes(intArrayOf(1, 1, 1, 1), 0) }, verify = { it == 4 })
    }

    fun permutationInString() {
        TestRunner.check("W2 #20 Permutation in String, classic", block = { checkInclusion("ab", "eidbaooo") }, verify = { it })
        TestRunner.check("W2 #20b Permutation in String, no permutation", block = { checkInclusion("ab", "eidboaoo") }, verify = { !it })
        TestRunner.check("W2 #20c Permutation in String, duplicate chars", block = { checkInclusion("adc", "dcda") }, verify = { it })
    }

    fun bestSightseeingPair() {
        TestRunner.check("W2 #21 Best Sightseeing Pair, classic", block = { maxScoreSightseeingPair(intArrayOf(8, 1, 5, 2, 6)) }, verify = { it == 11 })
        TestRunner.check("W2 #21b Best Sightseeing Pair, small", block = { maxScoreSightseeingPair(intArrayOf(1, 2, 2)) }, verify = { it == 3 })
        TestRunner.check("W2 #21c Best Sightseeing Pair, uniform", block = { maxScoreSightseeingPair(intArrayOf(2, 2, 2)) }, verify = { it == 3 })
    }

    fun binarySubarraysWithSum() {
        TestRunner.check("W2 #22 Binary Subarrays With Sum, classic", block = { numSubarraysWithSum(intArrayOf(1, 0, 1, 0, 1), 2) }, verify = { it == 4 })
        TestRunner.check("W2 #22b Binary Subarrays With Sum, all zeros", block = { numSubarraysWithSum(intArrayOf(0, 0, 0, 0, 0), 0) }, verify = { it == 15 })
        TestRunner.check("W2 #22c Binary Subarrays With Sum, unreachable", block = { numSubarraysWithSum(intArrayOf(0, 0, 0), 1) }, verify = { it == 0 })
    }

    fun maxPointsFromCards() {
        TestRunner.check("W2 #23 Max Points from Cards, take left", block = { maxScore(intArrayOf(1, 2, 3, 4, 5, 6, 1), 3) }, verify = { it == 12 })
        TestRunner.check("W2 #23b Max Points from Cards, uniform", block = { maxScore(intArrayOf(2, 2, 2), 2) }, verify = { it == 4 })
        TestRunner.check("W2 #23c Max Points from Cards, mostly right end", block = { maxScore(intArrayOf(1, 79, 80, 1, 1, 1, 200, 1), 3) }, verify = { it == 202 })
    }

    fun maxErasureValue() {
        TestRunner.check("W2 #24 Maximum Erasure Value, skip first 4", block = { maximumErasureValue(intArrayOf(4, 2, 4, 5, 6)) }, verify = { it == 17 })
        TestRunner.check("W2 #24b Maximum Erasure Value, window 5-2-1", block = { maximumErasureValue(intArrayOf(5, 2, 1, 2, 5, 2, 1, 2, 5)) }, verify = { it == 8 })
        TestRunner.check("W2 #24c Maximum Erasure Value, all duplicates", block = { maximumErasureValue(intArrayOf(1, 1, 1, 1, 1)) }, verify = { it == 1 })
    }

    fun slidingWindowMax() {
        TestRunner.check("W2 #25 Sliding Window Maximum, classic", block = { maxSlidingWindow(intArrayOf(1, 3, -1, -3, 5, 3, 6, 7), 3).toList() }, verify = { it == listOf(3, 3, 5, 5, 6, 7) })
        TestRunner.check("W2 #25b Sliding Window Maximum, single", block = { maxSlidingWindow(intArrayOf(1), 1).toList() }, verify = { it == listOf(1) })
        TestRunner.check("W2 #25c Sliding Window Maximum, all equal", block = { maxSlidingWindow(intArrayOf(9, 9, 9), 2).toList() }, verify = { it == listOf(9, 9) })
    }

    fun pivotIndex() {
        TestRunner.check("W2 #26 Find Pivot Index, classic", block = { pivotIndex(intArrayOf(1, 7, 3, 6, 5, 6)) }, verify = { it == 3 })
        TestRunner.check("W2 #26b Find Pivot Index, no pivot", block = { pivotIndex(intArrayOf(1, 2, 3)) }, verify = { it == -1 })
        TestRunner.check("W2 #26c Find Pivot Index, pivot at start", block = { pivotIndex(intArrayOf(2, 1, -1)) }, verify = { it == 0 })
    }

    fun rangeSum2D() {
        val m = arrayOf(intArrayOf(3, 0, 1, 4, 2), intArrayOf(5, 6, 3, 2, 1), intArrayOf(1, 2, 0, 1, 5), intArrayOf(4, 1, 0, 1, 7), intArrayOf(1, 0, 3, 0, 5))
        TestRunner.check("W2 #27 Range Sum 2D, (2,1)-(4,3)", block = { NumMatrix(m).sumRegion(2, 1, 4, 3) }, verify = { it == 8 })
        TestRunner.check("W2 #27b Range Sum 2D, interior block (1,1)-(2,2)", block = { NumMatrix(m).sumRegion(1, 1, 2, 2) }, verify = { it == 11 })
        TestRunner.check("W2 #27c Range Sum 2D, right edge (1,2)-(2,4)", block = { NumMatrix(m).sumRegion(1, 2, 2, 4) }, verify = { it == 12 })
    }

    fun continuousSubarraySum() {
        TestRunner.check("W2 #28 Continuous Subarray Sum, 2+4=6", block = { checkSubarraySum(intArrayOf(23, 2, 4, 6, 7), 6) }, verify = { it })
        TestRunner.check("W2 #28b Continuous Subarray Sum, whole-run multiple", block = { checkSubarraySum(intArrayOf(23, 2, 6, 4, 7), 6) }, verify = { it })
        TestRunner.check("W2 #28c Continuous Subarray Sum, no length-2 multiple", block = { checkSubarraySum(intArrayOf(23, 2, 6, 4, 7), 13) }, verify = { !it })
    }

    fun maxAbsoluteSum() {
        TestRunner.check("W2 #29 Max Absolute Subarray Sum, positive wins", block = { maxAbsoluteSum(intArrayOf(1, -3, 2, 3, -4)) }, verify = { it == 5 })
        TestRunner.check("W2 #29b Max Absolute Subarray Sum, negative wins", block = { maxAbsoluteSum(intArrayOf(2, -5, 1, -4, 3, -2)) }, verify = { it == 8 })
        TestRunner.check("W2 #29c Max Absolute Subarray Sum, all negative", block = { maxAbsoluteSum(intArrayOf(-1, -2, -3, -4)) }, verify = { it == 10 })
    }

    fun maxLenPositiveProduct() {
        TestRunner.check("W2 #30 Max Length Positive Product, even negatives", block = { getMaxLen(intArrayOf(1, -2, -3, 4)) }, verify = { it == 4 })
        TestRunner.check("W2 #30b Max Length Positive Product, zero resets", block = { getMaxLen(intArrayOf(0, 1, -2, -3, -4)) }, verify = { it == 3 })
        TestRunner.check("W2 #30c Max Length Positive Product, odd negatives", block = { getMaxLen(intArrayOf(-1, -2, -3, 0, 1)) }, verify = { it == 2 })
    }

    // Part B — complexity validation (Week02_Practice.md cases 1–13).
    // Uncomment complexityAll() in runAll() to time-check O(N) solutions against their budgets.
    fun complexityAll() {
        maxSubArrayBig(); subarraySumOnesBig(); subarraySumKBig(); longestSubstringBig(); longestSubstring26Big()
        productExceptSelfBig(); minSubArrayLenBig(); findAnagramsBig(); maxProductBig(); maxSubarraySumCircularBig()
        contiguousArrayBig(); characterReplacementBig(); slidingWindowMaxBig(); binarySubarraysBig(); continuousSubarraySumBig()
    }

    fun maxSubArrayBig() = TestRunner.check("W2-B1 Max Subarray, N=1M periodic (best per cycle = 6)", budgetMs = 200, block = { maxSubArray(IntArray(1_000_000) { (it % 7) - 3 }) }, verify = { it == 6 })
    fun subarraySumOnesBig() = TestRunner.check("W2-B2 Subarray Sum K, N=100k ones, k=100", budgetMs = 200, block = { subarraySum(IntArray(100_000) { 1 }, 100) }, verify = { it == 99_901 })
    fun subarraySumKBig() = TestRunner.check("W2-B2b Subarray Sum K, N=60k zeros, k=0", budgetMs = 300, block = { subarraySum(IntArray(60_000), 0) }, verify = { it == 1_800_030_000 })
    fun longestSubstringBig() = TestRunner.check("W2-B Longest Substring 'abc'×70k", budgetMs = 300, block = { lengthOfLongestSubstring("abc".repeat(70_000)) }, verify = { it == 3 })
    fun longestSubstring26Big() = TestRunner.check("W2-B3 Longest Substring, 26-cycle x20k", budgetMs = 300, block = { lengthOfLongestSubstring("abcdefghijklmnopqrstuvwxyz".repeat(20_000)) }, verify = { it == 26 })
    fun productExceptSelfBig() = TestRunner.check("W2-B4 Product Except Self, N=60 twos (structure only: 2^59 overflows Int)", budgetMs = 50, block = { productExceptSelf(IntArray(60) { 2 }) }, verify = { it.toHashSet().size == 1 })
    fun minSubArrayLenBig() = TestRunner.check("W2-B5 Min Size Subarray Sum, N=500k ones, target=500k", budgetMs = 200, block = { minSubArrayLen(500_000, IntArray(500_000) { 1 }) }, verify = { it == 500_000 })
    fun findAnagramsBig() = TestRunner.check("W2-B6 Find Anagrams, 500k 'ab', p='ab'", budgetMs = 500, block = { findAnagrams("ab".repeat(250_000), "ab").size }, verify = { it == 499_999 })
    fun maxProductBig() = TestRunner.check("W2-B7 Max Product Subarray, N=60 alternating 2/-1", budgetMs = 50, block = { maxProduct(IntArray(60) { if (it % 2 == 0) 2 else -1 }) }, verify = { it == 1_073_741_824 })
    fun maxSubarraySumCircularBig() = TestRunner.check("W2-B8 Max Sum Circular, N=100k [5,-1] (practice doc says 249999, brute force = 200001)", budgetMs = 200, block = { maxSubarraySumCircular(IntArray(100_000) { if (it % 2 == 0) 5 else -1 }) }, verify = { it == 200_001 })
    fun contiguousArrayBig() = TestRunner.check("W2-B9 Contiguous Array, N=200k alternating", budgetMs = 200, block = { findMaxLength(IntArray(200_000) { it % 2 }) }, verify = { it == 200_000 })
    fun characterReplacementBig() = TestRunner.check("W2-B10 Character Replacement, 500k 'AB', k=250k", budgetMs = 300, block = { characterReplacement("AB".repeat(250_000), 250_000) }, verify = { it == 500_000 })
    fun slidingWindowMaxBig() = TestRunner.check("W2-B11 Sliding Window Max, N=200k mod 1000, k=1000", budgetMs = 300, block = { maxSlidingWindow(IntArray(200_000) { it % 1000 }, 1000) }, verify = { r -> r.size == 199_001 && r.all { v -> v == 999 } })
    fun binarySubarraysBig() = TestRunner.check("W2-B12 Binary Subarrays With Sum, N=100k ones, goal=100", budgetMs = 200, block = { numSubarraysWithSum(IntArray(100_000) { 1 }, 100) }, verify = { it == 99_901 })
    fun continuousSubarraySumBig() = TestRunner.check("W2-B13 Continuous Subarray Sum, N=200k ones, k=1000", budgetMs = 200, block = { checkSubarraySum(IntArray(200_000) { 1 }, 1000) }, verify = { it })
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 2 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

fun findMaxAverage(nums: IntArray, k: Int): Double {
    /**
     * #	Input	Expected	Tests
     * 1	[1,12,-5,-6,50,3], k=4	12.75	Basic
     * 2	[5], k=1	5.0	Single
     * 3	[0,4,0,3,2], k=1	4.0	k=1
     */
    var sum = 0.0
    for (i in 0 ..< k) {
        sum += nums[i]
    }
    var maxAvg = sum / k

    for (i in k ..< nums.size) {
        sum += nums[i] - nums[i - k]
        maxAvg = maxOf(maxAvg, sum/k)
    }

    return maxAvg
}

fun runningSum(nums: IntArray): IntArray {
    /**
     * #	Input	Expected	Tests
     * 1	[1,2,3,4]	[1,3,6,10]	Basic
     * 2	[1,1,1,1,1]	[1,2,3,4,5]	Same values
     * 3	[3,1,2,10,1]	[3,4,6,16,17]	Mixed
     */
    var sum = nums[0]
    for (i in 1 ..< nums.size) {
        sum += nums[i]
        nums[i] = sum
    }
    return nums
}

fun maxSubArray(nums: IntArray): Int {
    /**
     * #	Input	Expected	Tests
     * 1	[-2,1,-3,4,-1,2,1,-5,4]	6	Classic
     * 2	[1]	1	Single
     * 3	[-3,-1,-2]	-1	All negative
     */
    var localMax = nums[0]
    var globalMax = nums[0]

    for (i in 1 ..< nums.size) {
        localMax = maxOf(nums[i], localMax + nums[i] )
        globalMax = maxOf(globalMax,  localMax)
    }
    return globalMax
}

fun totalFruit(fruits: IntArray): Int {
    val countMap = mutableMapOf<Int, Int>()
    var left = 0
    var maxLen = 0

    for (right in fruits.indices) {
        countMap[fruits[right]] = countMap.getOrDefault(fruits[right], 0) + 1

        while (countMap.size > 2) {
            val leftCount = countMap[fruits[left]]!! - 1
            if (leftCount == 0) countMap.remove(fruits[left])
            else countMap[fruits[left]] = leftCount
            left++
        }
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}

fun longestSubarray(nums: IntArray): Int {
    var left = 0
    var maxLen = 0
    var zeroCount = 0

    for (right in nums.indices) {
        if (nums[right] == 0) zeroCount++
        while (zeroCount > 1) {
            if (nums[left] == 0) zeroCount--
            left++
        }
        maxLen = maxOf(maxLen, right - left)
    }
    return maxLen
}

fun maxSatisfied(customers: IntArray, grumpy: IntArray, minutes: Int): Int =
    TODO("W2 #7 Grumpy Bookstore Owner — base sum of happy customers + best grumpy-gain window of size `minutes`")

fun maxProduct(nums: IntArray): Int {
    var maxSoFar = nums[0]
    var minSoFar = nums[0]
    var result = nums[0]
    var current : Int
    var tempMax : Int

    for (i in 1 ..< nums.size) {
        current = nums[i]
        tempMax = maxOf(current, minSoFar * current, maxSoFar * current)
        minSoFar = minOf(current, minSoFar * current, maxSoFar * current)
        maxSoFar = tempMax

        result = maxOf(result, maxSoFar)
    }
    return result
}

fun maxSubarraySumCircular(nums: IntArray): Int {
    // TODO: Kadane + total − min-Kadane trick
    /**
     * #	Input	Expected	Tests
     * 1	[1,-2,3,-2]	3	Non-circular wins
     * 2	[5,-3,5]	10	Circular wins
     * 3	[-3,-2,-3]	-2	All negative
     */

    var minSofar = nums[0]
    var maxSofar = nums[0]
    var currentMax = nums[0]
    var currentMin = nums[0]
    var total = nums[0]

    for (i in 1 ..< nums.size) {
        val num = nums[i]
        total += num

        currentMax = maxOf(num, currentMax + num)
        maxSofar = maxOf(maxSofar, currentMax)

        currentMin = minOf(num, currentMin + num)
        minSofar = minOf(minSofar, currentMin)

    }
    return if (maxSofar < 0) {
        maxSofar
    } else {
        maxOf(maxSofar, total - minSofar)
    }
}

fun productExceptSelf(nums: IntArray): IntArray {
    /**
     * #	Input	Expected	Tests
     * 1	[1,2,3,4]	[24,12,8,6]	Basic
     * 2	[-1,1,0,-3,3]	[0,0,9,0,0]	Zero + negatives
     * 3	[2,3]	[3,2]	Two elems
     */
    val n = nums.size
    val res = IntArray(n)

    res[0] = 1
    for (i in 1 ..< n) {
        res[i] = res[i - 1] * nums[i - 1]
    }

    var suffix = 1

    for (i  in n - 1 downTo 0) {
        res[i] *= suffix
        suffix *= nums[i]
    }

    return res
}

fun subarraySum(nums: IntArray, k: Int): Int {
    val prefixSumMap = hashMapOf(0 to 1)
    var count = 0
    var prefixSum = 0

    for (num in nums) {
        prefixSum += num
        count += prefixSumMap.getOrDefault(prefixSum - k, 0)
        prefixSumMap[prefixSum] = prefixSumMap.getOrDefault(prefixSum, 0) + 1
    }
    return count
}

fun findMaxLength(nums: IntArray): Int {
    /**
     * #	Input	Expected	Tests
     * 1	[0,1]	2	Basic
     * 2	[0,1,0]	2	Odd length
     * 3	[0,1,1,0,1,1,1,0]	4	Middle window
     */
    val map = hashMapOf(0 to -1)  // prefix sum → first index
    var maxLen = 0
    var prefixSum = 0

    for (i in nums.indices) {
        prefixSum += if (nums[i] == 0) -1 else 1

        if (prefixSum in map) {
            maxLen = maxOf(maxLen, i - map[prefixSum]!!)
        } else {
            map[prefixSum] = i
        }
    }

    return maxLen
}

/**
 * #	Input	Expected	Tests
 * 1	"abcabcbb"	3	Basic
 * 2	"bbbbb"	1	All same
 * 3	"pwwkew"	3	Middle window
 */
fun lengthOfLongestSubstring(s: String): Int {
    val lastSeen = hashMapOf<Char, Int>()
    var left  = 0
    var maxLen = 0

    for (right in s.indices) {
        if (s[right] in lastSeen && lastSeen[s[right]]!! >= left) {
            left = lastSeen[s[right]]!! + 1
        }
        lastSeen[s[right]] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }


//    println("W2 #14 Longest Substring Without Repeating — last-seen map, move left past the repeat")
    return maxLen
}

fun characterReplacement(s: String, k: Int): Int {
    val countArray = IntArray(26)
    var left = 0
    var maxFre = 0
    var maxLen = 0

    for (right in s.indices) {
        countArray[s[right] - 'A']++
        maxFre = maxOf(maxFre, countArray[s[right] - 'A'])

        while ((right - left + 1) - maxFre > k) {
            countArray[s[left] - 'A']--
            left++
        }

        maxLen = maxOf(maxLen, right - left + 1)

    }
    return maxLen
}

fun minSubArrayLen(target: Int, nums: IntArray): Int {
    var minLen = Int.MAX_VALUE
    var left = 0
    var sum = 0


    for (right in nums.indices) {
        sum += nums[right]
        while (sum >= target) {
            minLen = minOf(minLen, right - left + 1)
            sum -= nums[left]
            left++
        }
    }
    return if (minLen == Int.MAX_VALUE) 0 else minLen
}
fun findAnagrams(s: String, p: String): List<Int> {
    if (s.length < p.length) return emptyList()
    val res = mutableListOf<Int>()
    val sCount = IntArray(26)
    val pCount = IntArray(26)
    for (c in p) pCount[c - 'a']++

    for (i in s.indices) {
        sCount[s[i] - 'a']++
        if (i >= p.length) sCount[s[i - p.length] - 'a']--
        if (sCount.contentEquals(pCount)) res.add(i - p.length + 1)
    }
    return res
}
fun minWindow(s: String, t: String): String =
    TODO("W2 #18 Minimum Window Substring — need/have counts, shrink while the window is valid")

// ═══════════════════════════════════════════════════════════
// MON / WED — prefix sum helpers (#3, #13)
// ═══════════════════════════════════════════════════════════

/**
 * LeetCode 303 — Range Sum Query - Immutable.
 * Pattern: Prefix Sum  →  build prefix[0]=0, prefix[i+1]=prefix[i]+nums[i]; answer in O(1).
 *
 * | Input [1,2,3,4,5] | Call          | Expected |
 * |-------------------|---------------|----------|
 * | [1,2,3,4,5]       | rangeSum(1,3) | 9        |
 * | [1,2,3,4,5]       | rangeSum(0,4) | 15       |
 * | [1,2,3,4,5]       | rangeSum(2,2) | 3        |
 */
class PrefixSum(nums: IntArray) {
    // TODO: val prefix = IntArray(nums.size + 1); prefix[i+1] = prefix[i] + nums[i]
    fun rangeSum(l: Int, r: Int): Int = TODO("W2 #3 Prefix Sum Query — prefix[r+1] - prefix[l]")
}

/**
 * Longest Subarray with Sum K (no negatives → two pointers; with negatives → prefix + first-seen map).
 * Pattern: Prefix Sum + HashMap (store FIRST index of each prefix sum, never overwrite)
 *
 * | Input          | k   | Expected           |
 * |----------------|-----|--------------------|
 * | [1,2,3,1,1,1]  | 5   | 4  (index 1..4)    |
 * | [-1,2,3,-6,2,2]| 2   | 2                  |
 * | [1]            | 0   | 0                  |
 */
fun longestSubarrayWithSumK(nums: IntArray, k: Int): Int =
    TODO("W2 #13 Longest Subarray with Sum K — longestIndex[currentSum - k] = first index of that prefix")

// ═══════════════════════════════════════════════════════════
// FRI — bonus window problems (#19–#26)
// ═══════════════════════════════════════════════════════════

/**
 * LeetCode 1004 — Max Consecutive Ones III.  Pattern: Variable Window (at most k zeros).
 *
 * | Input                                   | k   | Expected |
 * |-----------------------------------------|-----|----------|
 * | [1,1,1,0,0,0,1,1,1,1,0]                 | 2   | 6        |
 * | [0,0,1,1,0,0,1,1,1,0,0,0,1,1,0,0,0,1,0,0]| 0  | 3        |
 * | [1,1,1,1]                               | 0   | 4        |
 */
fun longestOnes(nums: IntArray, k: Int): Int =
    TODO("W2 #19 Max Consecutive Ones III — shrink while zeros > k")

/**
 * LeetCode 567 — Permutation in String.  Pattern: Fixed Window + char counts (s1 anagram anywhere in s2).
 *
 * | s1   | s2         | Expected |
 * |------|------------|----------|
 * | "ab" | "eidbaooo" | true     |
 * | "ab" | "eidboaoo" | false    |
 * | "adc"| "dcda"     | true     |
 */
fun checkInclusion(s1: String, s2: String): Boolean =
    TODO("W2 #20 Permutation in String — window of s1.size, compare counts")

/**
 * LeetCode 1014 — Best Sightseeing Pair.  Pattern: Prefix Max (carry best values[i]+i while scanning).
 *
 * | Input       | Expected |
 * |-------------|----------|
 * | [8,1,5,2,6] | 11       |
 * | [1,2,2]     | 3        |
 * | [2,2,2]     | 3        |
 */
fun maxScoreSightseeingPair(values: IntArray): Int =
    TODO("W2 #21 Best Sightseeing Pair — best = max(best, bestLeft + values[i] - i)")

/**
 * LeetCode 930 — Binary Subarrays With Sum.  Pattern: Prefix Sum + HashMap (count, not max).
 *
 * | Input         | goal | Expected |
 * |---------------|------|----------|
 * | [1,0,1,0,1]   | 2    | 4        |
 * | [0,0,0,0,0]   | 0    | 15       |
 * | [0,0,0]       | 1    | 0        |
 */
fun numSubarraysWithSum(nums: IntArray, goal: Int): Int =
    TODO("W2 #22 Binary Subarrays With Sum — count += freq[currentSum - goal]")

/**
 * LeetCode 1423 — Maximum Points You Can Obtain from Cards.
 * Pattern: Fixed Window (max sum of the n-k cards left in the middle → total - min, or slide the k-card wrap window).
 *
 * | Input                 | k   | Expected |
 * |-----------------------|-----|----------|
 * | [1,2,3,4,5,6,1]       | 3   | 12       |
 * | [2,2,2]               | 2   | 4        |
 * | [1,79,80,1,1,1,200,1] | 3   | 202      |
 */
fun maxScore(cardPoints: IntArray, k: Int): Int =
    TODO("W2 #23 Max Points from Cards — slide the k-card window across the wrap point")

/**
 * LeetCode 1695 — Maximum Erasure Value.  Pattern: Variable Window + Set (unique elements, max SUM).
 *
 * | Input                     | Expected |
 * |---------------------------|----------|
 * | [4,2,4,5,6]               | 17       |
 * | [5,2,1,2,5,2,1,2,5]       | 8  (practice doc says 9 — brute force = 8) |
 * | [1,1,1,1,1]               | 1        |
 */
fun maximumErasureValue(nums: IntArray): Int =
    TODO("W2 #24 Maximum Erasure Value — shrink until the duplicate leaves the window, track running sum")

/**
 * LeetCode 239 — Sliding Window Maximum.  Pattern: Monotonic Deque (indices, decreasing values).
 *
 * | Input                   | k   | Expected               |
 * |-------------------------|-----|------------------------|
 * | [1,3,-1,-3,5,3,6,7]     | 3   | [3,3,5,5,6,7]          |
 * | [1]                     | 1   | [1]                    |
 * | [9,9,9]                 | 2   | [9,9]                  |
 */
fun maxSlidingWindow(nums: IntArray, k: Int): IntArray =
    TODO("W2 #25 Sliding Window Maximum — pop smaller tail values, drop out-of-window head index")

/**
 * LeetCode 724 — Find Pivot Index.  Pattern: Prefix Sum (total first, then left vs total-left-x).
 *
 * | Input            | Expected |
 * |------------------|----------|
 * | [1,7,3,6,5,6]    | 3        |
 * | [1,2,3]          | -1       |
 * | [2,1,-1]         | 0        |
 */
fun pivotIndex(nums: IntArray): Int =
    TODO("W2 #26 Find Pivot Index — left == total - left - x")

// ═══════════════════════════════════════════════════════════
// FRI — bonus prefix sum / Kadane's extensions (#27–#30)
// ═══════════════════════════════════════════════════════════

/**
 * LeetCode 304 — Range Sum Query 2D - Immutable.
 * Pattern: 2D Prefix Sum → p[i+1][j+1] = p[i][j+1] + p[i+1][j] - p[i][j] + matrix[i][j]
 *
 * | Matrix 5x5 (LC example) | Call              | Expected |
 * |-------------------------|-------------------|----------|
 * | see Week02.md #27       | sumRegion(2,1,4,3)| 8        |
 * | see Week02.md #27       | sumRegion(1,1,2,2)| 11       |
 * | see Week02.md #27       | sumRegion(1,2,2,4)| 12       |
 */
class NumMatrix(matrix: Array<IntArray>) {
    // TODO: build (rows+1) x (cols+1) prefix table so the top/left borders are 0
    fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int =
        TODO("W2 #27 Range Sum 2D — p[r2+1][c2+1] - p[r1][c2+1] - p[r2+1][c1] + p[r1][c1]")
}

/**
 * LeetCode 523 — Continuous Subarray Sum.
 * Pattern: Prefix Sum mod k + HashMap of FIRST index (subarray length must be >= 2).
 *
 * | Input               | k   | Expected |
 * |---------------------|-----|----------|
 * | [23,2,4,6,7]        | 6   | true     |
 * | [23,2,6,4,7]        | 6   | true     |
 * | [23,2,6,4,7]        | 13  | false    |
 */
fun checkSubarraySum(nums: IntArray, k: Int): Boolean =
    TODO("W2 #28 Continuous Subarray Sum — same (prefix mod k) seen >= 2 indices apart")

/**
 * LeetCode 1749 — Maximum Absolute Sum of Any Subarray.
 * Pattern: Kadane's twice (max subarray sum and min subarray sum), take max(|max|, |min|).
 *
 * | Input             | Expected |
 * |-------------------|----------|
 * | [1,-3,2,3,-4]     | 5  (practice doc says 6 — brute force = 5) |
 * | [2,-5,1,-4,3,-2]  | 8        |
 * | [-1,-2,-3,-4]     | 10       |
 */
fun maxAbsoluteSum(nums: IntArray): Int =
    TODO("W2 #29 Max Absolute Subarray Sum — Kadane on values and on negated values")

/**
 * LeetCode 1567 — Maximum Length of Subarray With Positive Product.
 * Pattern: Kadane-style tracking (positive/negative run lengths, reset on 0).
 *
 * | Input             | Expected |
 * |-------------------|----------|
 * | [1,-2,-3,4]       | 4        |
 * | [0,1,-2,-3,-4]    | 3        |
 * | [-1,-2,-3,0,1]    | 2        |
 */
fun getMaxLen(nums: IntArray): Int =
    TODO("W2 #30 Max Length Positive Product — track positive/negative run lengths, reset on 0")


