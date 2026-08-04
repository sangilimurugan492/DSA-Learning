package array.two_pointer.four_sum_ii

/**
 * https://leetcode.com/problems/4sum-ii/
 * Given four integer arrays nums1, nums2, nums3, and nums4, all of length n,
 * return the number of tuples (i, j, k, l) such that:
 *
 * 0 <= i, j, k, l < n
 * nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
 *
 *
 * Example 1:
 *
 * Input: nums1 = [1,2], nums2 = [-2,-1], nums3 = [-1,2], nums4 = [0,2]
 * Output: 2
 *
 * Example 2:
 *
 * Input: nums1 = [0], nums2 = [0], nums3 = [0], nums4 = [0]
 * Output: 1
 */
fun main() {
    println(fourSumCountBF(
        intArrayOf(1, 2),
        intArrayOf(-2, -1),
        intArrayOf(-1, 2),
        intArrayOf(0, 2)
    )) // Expected: 2

    println()

    println(fourSumCountOP(
        intArrayOf(1, 2),
        intArrayOf(-2, -1),
        intArrayOf(-1, 2),
        intArrayOf(0, 2)
    )) // Expected: 2
}

/**
 * Brute Force Approach
 *
 * Iterate through all possible combinations of indices (i, j, k, l) across the
 * four arrays and count how many sum to 0.
 *
 * Time Complexity: O(N⁴) — four nested loops over arrays of size N
 * Space Complexity: O(1) — no extra data structures used
 */
fun fourSumCountBF(
    nums1: IntArray,
    nums2: IntArray,
    nums3: IntArray,
    nums4: IntArray
): Int {
    val n = nums1.size
    var count = 0

    for (i in 0 until n) {
        for (j in 0 until n) {
            for (k in 0 until n) {
                for (l in 0 until n) {
                    if (nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0) {
                        count++
                    }
                }
            }
        }
    }

    return count
}

/**
 * Optimal Approach (HashMap)
 *
 * 1. Precompute all pairwise sums of nums1[i] + nums2[j] and store their
 *    frequencies in a HashMap.
 * 2. For each pair (nums3[k] + nums4[l]), check if the negation of that sum
 *    exists in the HashMap. If it does, add its frequency to the count.
 *
 * Time Complexity: O(N²) — two nested loops for nums1+nums2, two nested loops for nums3+nums4
 * Space Complexity: O(N²) — HashMap stores up to N² pairwise sums
 */
fun fourSumCountOP(
    nums1: IntArray,
    nums2: IntArray,
    nums3: IntArray,
    nums4: IntArray
): Int {
    val n = nums1.size
    val sumMap = HashMap<Int, Int>()

    // Step 1: Build a frequency map of all nums1[i] + nums2[j] sums
    for (i in 0 until n) {
        for (j in 0 until n) {
            val sum = nums1[i] + nums2[j]
            sumMap[sum] = sumMap.getOrDefault(sum, 0) + 1
        }
    }

    // Step 2: For each nums3[k] + nums4[l], look for the complement (0 - sum)
    var count = 0
    for (k in 0 until n) {
        for (l in 0 until n) {
            val sum = nums3[k] + nums4[l]
            val complement = -sum
            count += sumMap.getOrDefault(complement, 0)
        }
    }

    return count
}
