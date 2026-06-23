package stack

/**
 * https://leetcode.com/problems/next-greater-element-i/
 * Given two arrays nums1 (subset of nums2), find next greater element for each nums1[i] in nums2.
 * Example: nums1 = [4,1,2], nums2 = [1,3,4,2] → [-1,3,-1]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Monotonic Stack intro)
 */

fun main() {
    println(nextGreaterElementBruteForce(intArrayOf(4, 1, 2), intArrayOf(1, 3, 4, 2)).toList())
    println(nextGreaterElementBruteForce(intArrayOf(2, 4), intArrayOf(1, 2, 3, 4)).toList())
    println("---")
    println(nextGreaterElementStack(intArrayOf(4, 1, 2), intArrayOf(1, 3, 4, 2)).toList())
    println(nextGreaterElementStack(intArrayOf(2, 4), intArrayOf(1, 2, 3, 4)).toList())
}

/**
 * BRUTE FORCE: O(N × M) — for each element in nums1, scan nums2 for next greater
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

/**
 * OPTIMAL: O(M) Monotonic Stack — build nextGreater map for nums2, then lookup
 * Stack maintains decreasing order. When larger element found, pop and record.
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
