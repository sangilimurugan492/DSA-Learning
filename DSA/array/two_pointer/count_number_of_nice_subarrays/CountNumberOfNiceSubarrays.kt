package array.two_pointer.count_number_of_nice_subarrays

/**
 * https://leetcode.com/problems/count-number-of-nice-subarrays/
 *
 * Given an array of integers nums and an integer k. A continuous subarray is called nice
 * if there are exactly k odd numbers in it. Return the number of nice subarrays.
 *
 * Example 1:
 *
 * Input: nums = [1,1,2,1,1], k = 3
 * Output: 2
 * Explanation: [1,1,2,1] and [1,2,1,1]
 *
 * Example 2:
 *
 * Input: nums = [2,4,6], k = 1
 * Output: 0
 *
 * Example 3:
 *
 * Input: nums = [2,2,2,1,2,2,1,2,2,2], k = 2
 * Output: 16
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta — atMost pattern)
 *
 * Key Insight: exactly(K) = atMost(K) - atMost(K-1)
 * This is the SAME pattern as SubArrayWithKDifferentInteger.
 * For atMost(k): sliding window, shrink when odd count > k.
 * Number of subarrays ending at right = right - left + 1.
 */
fun main() {
    println("Brute Force:")
    println(numberOfSubarraysBF(intArrayOf(1, 1, 2, 1, 1), 3))           // 2
    println(numberOfSubarraysBF(intArrayOf(2, 4, 6), 1))                  // 0
    println(numberOfSubarraysBF(intArrayOf(2, 2, 2, 1, 2, 2, 1, 2, 2, 2), 2)) // 16
    println("Optimal (atMost pattern):")
    println(numberOfSubarrays(intArrayOf(1, 1, 2, 1, 1), 3))             // 2
    println(numberOfSubarrays(intArrayOf(2, 4, 6), 1))                    // 0
    println(numberOfSubarrays(intArrayOf(2, 2, 2, 1, 2, 2, 1, 2, 2, 2), 2)) // 16
}

/**
 * Brute Force: For each starting index, expand right and count odd numbers.
 * If odd count == k, increment result. If odd count > k, stop expanding.
 *
 * Walkthrough: nums = [1,1,2,1,1], k = 3
 *
 *   i=0: expand right → odds: 1,2,2,3 → [1,1,2,1] count++ → odds: 4 > 3 stop
 *   i=1: expand right → odds: 1,1,2,3 → [1,2,1,1] count++ → stop
 *   i=2: expand right → odds: 0,1,2 → never reaches 3
 *   i=3: expand right → odds: 1,2 → never reaches 3
 *   i=4: expand right → odds: 1 → never reaches 3
 *
 * Result: 2 ✅
 *
 * Time Complexity:  O(N²) — nested loops for each starting index
 * Space Complexity: O(1)  — only a counter variable
 */
fun numberOfSubarraysBF(nums: IntArray, k: Int): Int {
    var result = 0

    for (i in nums.indices) {
        var oddCount = 0
        for (j in i until nums.size) {
            if (nums[j] % 2 == 1) oddCount++
            if (oddCount == k) {
                result++
            } else if (oddCount > k) {
                break // No point expanding further — odds only increase
            }
        }
    }

    return result
}

/**
 * Optimal (atMost pattern): exactly(k) = atMost(k) - atMost(k-1)
 *
 * atMost(k): Count subarrays with at most k odd numbers.
 * For each right, if odd count > k, shrink from left.
 * Number of valid subarrays ending at right = right - left + 1.
 *
 * Time Complexity:  O(N) — two passes of atMost, each single pass
 * Space Complexity: O(1) — only variables
 */
fun numberOfSubarrays(nums: IntArray, k: Int): Int {
    return atMost(nums, k) - atMost(nums, k - 1)
}


fun atMost(nums: IntArray, k: Int): Int {
    if (k < 0) return 0
    var left = 0
    var odds = 0
    var count = 0

    for (right in nums.indices) {
        if (nums[right] % 2 == 1) odds++

        while (odds > k) {
            if (nums[left] % 2 == 1) odds--
            left++
        }

        count += right - left + 1
    }

    return count
}
