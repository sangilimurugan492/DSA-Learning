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
    println("=== Method 1: Merge + Sort ===")
    println("median([1,2],[3,4]) = ${findMedianSortedArraysBF(intArrayOf(1, 2), intArrayOf(3, 4))}")
    println("median([1,3],[2]) = ${findMedianSortedArraysBF(intArrayOf(1, 3), intArrayOf(2))}")

    println("\n=== Method 2: Binary Search Partition ===")
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
