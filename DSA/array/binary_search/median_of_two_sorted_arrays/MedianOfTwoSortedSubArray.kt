package array.binary_search.median_of_two_sorted_arrays

/**
 * Median of Two Sorted Arrays — LeetCode #4
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 *
 * Problem:
 * -------
 * Given two sorted arrays nums1 and nums2, find the median in O(log(m+n)) time.
 *
 * Example:  nums1=[1,2], nums2=[3,4] → 2.5  (merged=[1,2,3,4], median=(2+3)/2)
 *           nums1=[1,3], nums2=[2] → 2.0  (merged=[1,2,3], median=2)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — Asked at Google, Meta, Amazon)
 *
 * Two approaches:
 * 1. Merge + Sort: O((m+n) log(m+n)) — combine, sort, find middle
 * 2. Binary Search Partition: O(log(min(m,n))) — partition both arrays so left halves = right halves
 */

fun main() {
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAYS for step-by-step walkthrough
    // nums1 = [1, 3, 5, 7, 9]   (5 elements — odd positions)
    // nums2 = [2, 4, 6, 8, 10]  (5 elements — even positions)
    // Merged = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]  (10 elements, even)
    // Median = (5 + 6) / 2 = 5.5
    // ─────────────────────────────────────────────────────────────
    val hugeNums1 = intArrayOf(1, 3, 5, 7, 9)
    val hugeNums2 = intArrayOf(2, 4, 6, 8, 10)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("nums1: ${hugeNums1.toList()}")
    println("nums2: ${hugeNums2.toList()}")
    println("Merged: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]")
    println("Expected median: 5.5  (average of 5 and 6)\n")

    println("=== Method 1: Merge + Sort ===")
    println("median(hugeNums1, hugeNums2) = ${findMedianSortedArraysBF(hugeNums1, hugeNums2)}")

    println("\n=== Method 2: Binary Search Partition (step-by-step) ===")
    println("median(hugeNums1, hugeNums2) = ${findMedianSortedArraysVerbose(hugeNums1, hugeNums2)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
    println("median([1,2],[3,4]) = ${findMedianSortedArrays(intArrayOf(1, 2), intArrayOf(3, 4))}")
    println("median([1,3],[2]) = ${findMedianSortedArrays(intArrayOf(1, 3), intArrayOf(2))}")
}


// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: MERGE + SORT — O((m+n) log(m+n))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MERGE + SORT — Combine both arrays, sort, find median.
 *
 * Core Idea:
 *   - Merge nums1 and nums2 into one array.
 *   - Sort the combined array.
 *   - If length is odd → middle element. If even → average of two middle elements.
 *
 * Time Complexity:  O((m+n) log(m+n)) — sorting dominates.
 * Space Complexity: O(m+n) — merged array.
 */
fun findMedianSortedArraysBF(nums1: IntArray, nums2: IntArray): Double {
    val merged = IntArray(nums1.size + nums2.size)
    for (i in nums1.indices) merged[i] = nums1[i]
    for (i in nums2.indices) merged[i + nums1.size] = nums2[i]
    merged.sort()

    val n = merged.size
    return if (n % 2 == 1) {
        merged[n / 2].toDouble()
    } else {
        (merged[n / 2] + merged[n / 2 - 1]) / 2.0
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH PARTITION — O(log(min(m,n)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH PARTITION — Binary search on the smaller array to find correct partition.
 *
 * Core Idea:
 *   - Partition nums1 and nums2 so that left halves combined = right halves combined.
 *   - All elements in left partition ≤ all elements in right partition.
 *   - Binary search on the smaller array to find the correct partition point.
 *
 * Key Insight:
 *   - If we partition both arrays at the right point:
 *     max(left side) ≤ min(right side)
 *   - For even total: median = (max(left) + min(right)) / 2
 *   - For odd total: median = max(left)
 *
 * Partition:
 *   - partitionX = elements from nums1 in left half
 *   - partitionY = (m + n + 1) / 2 - partitionX = elements from nums2 in left half
 *
 * Valid partition condition:
 *   maxLeftX <= minRightY && maxLeftY <= minRightX
 *
 * Time Complexity:  O(log(min(m,n))) — binary search on smaller array.
 * Space Complexity: O(1) — constant variables.
 */
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    // Ensure nums1 is the smaller array for efficiency
    val (a, b) = if (nums1.size <= nums2.size) nums1 to nums2 else nums2 to nums1
    val m = a.size
    val n = b.size

    var low = 0
    var high = m

    while (low <= high) {
        val partitionX = (low + high) / 2
        val partitionY = (m + n + 1) / 2 - partitionX

        val maxLeftX = if (partitionX == 0) Int.MIN_VALUE else a[partitionX - 1]
        val minRightX = if (partitionX == m) Int.MAX_VALUE else a[partitionX]

        val maxLeftY = if (partitionY == 0) Int.MIN_VALUE else b[partitionY - 1]
        val minRightY = if (partitionY == n) Int.MAX_VALUE else b[partitionY]

        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            // Found correct partition
            return if ((m + n) % 2 == 0) {
                (maxOf(maxLeftX, maxLeftY) + minOf(minRightX, minRightY)) / 2.0
            } else {
                maxOf(maxLeftX, maxLeftY).toDouble()
            }
        } else if (maxLeftX > minRightY) {
            // Too many elements from nums1 in left half → move left
            high = partitionX - 1
        } else {
            // Too few elements from nums1 in left half → move right
            low = partitionX + 1
        }
    }

    throw IllegalArgumentException("Input arrays are not sorted")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2b: BINARY SEARCH PARTITION WITH VERBOSE STEP-BY-STEP OUTPUT — O(log(min(m,n)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH PARTITION (VERBOSE) — Same logic as findMedianSortedArrays but prints every step.
 *
 * This function is identical to findMedianSortedArrays() but adds println statements at each
 * iteration so you can see exactly how the binary search partition narrows down to the correct cut.
 *
 * ── Step-by-step explanation for the 10-element arrays ──
 *   nums1 (a) = [1, 3, 5, 7, 9]   (m=5)
 *   nums2 (b) = [2, 4, 6, 8, 10]  (n=5)
 *   Total = 10 (even) → median = (max(left) + min(right)) / 2
 *   Left half should have (m+n+1)/2 = 5 elements
 *
 * STEP 1:  low=0, high=5
 *          partitionX = (0+5)/2 = 2 → a's left = [1,3], right = [5,7,9]
 *          partitionY = 5 - 2 = 3   → b's left = [2,4,6], right = [8,10]
 *          maxLeftX=3, minRightX=5, maxLeftY=6, minRightY=8
 *          Check: 3<=8 ✅ but 6<=5 ❌ → maxLeftY > minRightX
 *          → Too few from a in left half → low = partitionX + 1 = 3
 *
 * STEP 2:  low=3, high=5
 *          partitionX = (3+5)/2 = 4 → a's left = [1,3,5,7], right = [9]
 *          partitionY = 5 - 4 = 1   → b's left = [2], right = [4,6,8,10]
 *          maxLeftX=7, minRightX=9, maxLeftY=2, minRightY=4
 *          Check: 7<=4 ❌ → maxLeftX > minRightY
 *          → Too many from a in left half → high = partitionX - 1 = 3
 *
 * STEP 3:  low=3, high=3
 *          partitionX = (3+3)/2 = 3 → a's left = [1,3,5], right = [7,9]
 *          partitionY = 5 - 3 = 2   → b's left = [2,4], right = [6,8,10]
 *          maxLeftX=5, minRightX=7, maxLeftY=4, minRightY=6
 *          Check: 5<=6 ✅ && 4<=7 ✅ → VALID PARTITION!
 *          Total is even → median = (max(5,4) + min(7,6)) / 2 = (5+6)/2 = 5.5 ✅
 *
 * Only 3 iterations for two 5-element arrays — that's O(log(min(m,n))) in action!
 *
 * Time Complexity:  O(log(min(m,n))) — binary search on smaller array.
 * Space Complexity: O(1) — constant variables.
 */
fun findMedianSortedArraysVerbose(nums1: IntArray, nums2: IntArray): Double {
    // Ensure nums1 is the smaller array for efficiency
    val (a, b) = if (nums1.size <= nums2.size) nums1 to nums2 else nums2 to nums1
    val m = a.size
    val n = b.size

    var low = 0
    var high = m
    var step = 1

    println("  a (smaller): ${a.toList()}  (m=$m)")
    println("  b (larger):  ${b.toList()}  (n=$n)")
    println("  Total elements: ${m + n} (${if ((m + n) % 2 == 0) "even" else "odd"})")
    println("  Left half size: ${(m + n + 1) / 2}")
    println("  ──────────────────────────────────────────────")

    while (low <= high) {
        val partitionX = (low + high) / 2
        val partitionY = (m + n + 1) / 2 - partitionX

        val maxLeftX = if (partitionX == 0) Int.MIN_VALUE else a[partitionX - 1]
        val minRightX = if (partitionX == m) Int.MAX_VALUE else a[partitionX]

        val maxLeftY = if (partitionY == 0) Int.MIN_VALUE else b[partitionY - 1]
        val minRightY = if (partitionY == n) Int.MAX_VALUE else b[partitionY]

        println("  STEP $step: low=$low, high=$high")
        println("         partitionX=$partitionX → a's left=${a.take(partitionX)}, right=${a.drop(partitionX)}")
        println("         partitionY=$partitionY → b's left=${b.take(partitionY)}, right=${b.drop(partitionY)}")
        println("         maxLeftX=$maxLeftX, minRightX=$minRightX, maxLeftY=$maxLeftY, minRightY=$minRightY")

        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            // Found correct partition
            println("         ✅ VALID PARTITION! $maxLeftX<=$minRightY && $maxLeftY<=$minRightX")
            val median = if ((m + n) % 2 == 0) {
                val leftMax = maxOf(maxLeftX, maxLeftY)
                val rightMin = minOf(minRightX, minRightY)
                println("         Even total → median = (max($maxLeftX,$maxLeftY) + min($minRightX,$minRightY)) / 2")
                println("         = ($leftMax + $rightMin) / 2 = ${(leftMax + rightMin) / 2.0}")
                (leftMax + rightMin) / 2.0
            } else {
                val leftMax = maxOf(maxLeftX, maxLeftY)
                println("         Odd total → median = max($maxLeftX,$maxLeftY) = $leftMax")
                leftMax.toDouble()
            }
            println("  ──────────────────────────────────────────────")
            return median
        } else if (maxLeftX > minRightY) {
            // Too many elements from nums1 in left half → move left
            println("         ❌ $maxLeftX > $minRightY → too many from a → high = $partitionX - 1 = ${partitionX - 1}")
            high = partitionX - 1
        } else {
            // Too few elements from nums1 in left half → move right
            println("         ❌ $maxLeftY > $minRightX → too few from a → low = $partitionX + 1 = ${partitionX + 1}")
            low = partitionX + 1
        }
        println()
        step++
    }

    throw IllegalArgumentException("Input arrays are not sorted")
}

