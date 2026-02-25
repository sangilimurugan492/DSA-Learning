package two_pointer_technique

/**
 * https://leetcode.com/problems/merge-sorted-array/description/
 * You are given two integer arrays nums1 and nums2, sorted in non-decreasing order, and two integers m and n, representing the number of elements in nums1 and nums2 respectively.
 *
 * Merge nums1 and nums2 into a single array sorted in non-decreasing order.
 *
 * The final sorted array should not be returned by the function, but instead be stored inside the array nums1. To accommodate this, nums1 has a length of m + n, where the first m elements denote the elements that should be merged, and the last n elements are set to 0 and should be ignored. nums2 has a length of n.
 *
 *
 *
 * Example 1:
 *
 * Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
 * Output: [1,2,2,3,5,6]
 * Explanation: The arrays we are merging are [1,2,3] and [2,5,6].
 * The result of the merge is [1,2,2,3,5,6] with the underlined elements coming from nums1.
 * Example 2:
 *
 * Input: nums1 = [1], m = 1, nums2 = [], n = 0
 * Output: [1]
 * Explanation: The arrays we are merging are [1] and [].
 * The result of the merge is [1].
 */
fun main() {
    mergeSortedArrayBF(intArrayOf(1,2,3,0,0,0), 3, intArrayOf(2,5,6), 3)
    mergeSortedArrayOP(intArrayOf(1,2,3,0,0,0), 3, intArrayOf(2,5,6), 3)
}

/**
 * Time Complexity $O((m + n) log(m + n))$
 * Space Complexity O(1)
 */
fun mergeSortedArrayBF(nums1: IntArray, m: Int, nums2: IntArray, n: Int) : Unit {
    for (i in 0 until n) {
        nums1[m + i] = nums2[i]
    }
    nums1.sort()
}

/**
 * Time Complexity O(m+n)
 * Space Complexity O(1)
 */
fun mergeSortedArrayOP(nums1: IntArray, m: Int, nums2: IntArray, n: Int) : Unit {
    var p1 = m - 1
    var p2 = n - 1
    var pMerge = m + n - 1

    while (p2 >= 0) {
        if (p1 >= 0 && nums1[p1] > nums2[p2]) {
            nums1[pMerge] = nums1[p1]
            p1--
        } else {
            nums1[pMerge] = nums2[p2]
            p2--
        }
        pMerge--
    }
}