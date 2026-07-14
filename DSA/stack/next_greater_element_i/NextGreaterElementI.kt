package stack.next_greater_element_i

/**
 * Next Greater Element I — LeetCode #496
 * https://leetcode.com/problems/next-greater-element-i/
 *
 * Problem:
 * -------
 * Given nums1 (subset of nums2), find next greater element for each nums1[i] in nums2.
 *
 * Example:  nums1 = [4,1,2], nums2 = [1,3,4,2] → [-1,3,-1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Monotonic Stack intro)
 *
 * Two approaches:
 * 1. Brute Force: O(N × M) — for each element in nums1, scan nums2
 * 2. Monotonic Stack: O(M) — build nextGreater map for nums2, then lookup
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("nextGreaterElement([4,1,2], [1,3,4,2]) = ${nextGreaterElementBruteForce(intArrayOf(4, 1, 2), intArrayOf(1, 3, 4, 2)).toList()}")

    println("\n=== Method 2: Monotonic Stack ===")
    println("nextGreaterElement([4,1,2], [1,3,4,2]) = ${nextGreaterElementStack(intArrayOf(4, 1, 2), intArrayOf(1, 3, 4, 2)).toList()}")
    println("nextGreaterElement([2,4], [1,2,3,4]) = ${nextGreaterElementStack(intArrayOf(2, 4), intArrayOf(1, 2, 3, 4)).toList()}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N × M)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each element in nums1, scan nums2 for next greater.
 *
 * Time Complexity:  O(N × M).
 * Space Complexity: O(N) — result.
 */
fun nextGreaterElementBruteForce(nums1: IntArray, nums2: IntArray): IntArray {
    val result = IntArray(nums1.size) { -1 }
    for (i in nums1.indices) {
        var found = false
        for (j in nums2.indices) {
            if (nums2[j] == nums1[i]) found = true
            else if (found && nums2[j] > nums1[i]) {
                result[i] = nums2[j]
                break
            }
        }
    }
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(M)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Build nextGreater map for nums2, then lookup nums1.
 *
 * Core Idea:
 *   - Stack maintains decreasing order.
 *   - When a larger element is found, pop all smaller elements and record their next greater.
 *   - Build a map: element → next greater element.
 *   - Lookup nums1 values in the map.
 *
 * Key Insight:
 *   - Process nums2 once with a stack → O(M). Then O(1) lookup for each nums1 element.
 *
 * Time Complexity:  O(M) — process nums2 once.
 * Space Complexity: O(M) — map + stack.
 */
fun nextGreaterElementStack(nums1: IntArray, nums2: IntArray): IntArray {
    val nextGreater = hashMapOf<Int, Int>()
    val stack = ArrayDeque<Int>()

    for (num in nums2) {
        while (stack.isNotEmpty() && num > stack.last()) {
            nextGreater[stack.removeLast()] = num
        }
        stack.addLast(num)
    }
    // Remaining elements in stack have no next greater → -1 (default)

    return nums1.map { nextGreater.getOrDefault(it, -1) }.toIntArray()
}
