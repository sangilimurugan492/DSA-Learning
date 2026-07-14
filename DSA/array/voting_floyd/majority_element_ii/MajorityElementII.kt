package array.voting_floyd.majority_element_ii

/**
 * https://leetcode.com/problems/majority-element-ii/
 *
 * Given an integer array of size n, find all elements that appear more than ⌊n/3⌋ times.
 * Follow-up: Could you solve the problem in linear time and O(1) space?
 *
 * Example 1:
 *
 * Input: nums = [3,2,3]
 * Output: [3]
 *
 * Example 2:
 *
 * Input: nums = [1]
 * Output: [1]
 *
 * Example 3:
 *
 * Input: nums = [1,2]
 * Output: [1,2]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: At most 2 elements can appear more than ⌊n/3⌋ times.
 * Extend Boyer-Moore Voting to track TWO candidates instead of one.
 * Then verify both candidates actually appear > n/3 times.
 */
fun main() {
    println(majorityElementII(intArrayOf(3, 2, 3)))
    println(majorityElementII(intArrayOf(1)))
    println(majorityElementII(intArrayOf(1, 2)))
    println(majorityElementII(intArrayOf(2, 2, 1, 3)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Extended Boyer-Moore Voting Algorithm
 *
 * Why at most 2 elements? If an element appears > n/3 times, and n/3 + n/3 = 2n/3 < n,
 * so there can be at most 2 such elements.
 *
 * Steps:
 * 1. Find two candidates using Boyer-Moore (track 2 candidates + 2 counts)
 * 2. Verify both candidates appear > n/3 times
 *
 * Trace for [2,2,1,3]:
 * candidate1=2, count1=2; candidate2=3, count2=1
 * Verify: 2 appears 2 times, 2 > 4/3=1 ✅; 3 appears 1 time, 1 > 1 ❌
 * Result: [2]
 */
fun majorityElementII(nums: IntArray): List<Int> {
    var candidate1 = 0
    var candidate2 = 0
    var count1 = 0
    var count2 = 0

    // Pass 1: Find two candidates
    for (num in nums) {
        when {
            num == candidate1 -> count1++
            num == candidate2 -> count2++
            count1 == 0 -> { candidate1 = num; count1 = 1 }
            count2 == 0 -> { candidate2 = num; count2 = 1 }
            else -> { count1--; count2-- }
        }
    }

    // Pass 2: Verify candidates
    count1 = 0
    count2 = 0
    for (num in nums) {
        when (num) {
            candidate1 -> count1++
            candidate2 -> count2++
        }
    }

    val result = mutableListOf<Int>()
    val threshold = nums.size / 3
    if (count1 > threshold) result.add(candidate1)
    if (count2 > threshold && candidate2 != candidate1) result.add(candidate2)

    return result
}
