package array.linear_scan.leaders_in_array

/**
 * https://www.geeksforgeeks.org/dsa/leaders-in-an-array/
 *
 * Given an array arr[] of positive integers, an element is a "leader" if it is greater
 * than or equal to all the elements to its right. The rightmost element is always a leader.
 *
 * Return all leaders in the array in their original order (left to right).
 *
 * Constraints:
 *   1 <= arr.size <= 10^5
 *   1 <= arr[i] <= 10^6
 *
 * Example 1:
 *   Input:  arr = [16, 17, 4, 3, 5, 2]
 *   Output: [17, 5, 2]
 *   Explanation:
 *     17 > all elements to its right [4, 3, 5, 2] → leader
 *     4  < 5 (to its right)              → not a leader
 *     3  < 5 (to its right)              → not a leader
 *     5  > all elements to its right [2] → leader
 *     2  is the rightmost element         → leader (always)
 *
 * Example 2:
 *   Input:  arr = [1, 2, 3, 4, 5, 2]
 *   Output: [5, 2]
 *   Explanation:
 *     5 > all elements to its right [2] → leader
 *     2 is the rightmost element         → leader
 */
fun main() {
    println("Brute Force: ${findLeadersInAnArrayBF(intArrayOf(16, 17, 4, 3, 5, 2))}")  // [17, 5, 2]
    println("Optimal:     ${findLeadersInAnArrayOP(intArrayOf(16, 17, 4, 3, 5, 2))}")  // [17, 5, 2]
    println("Optimal:     ${findLeadersInAnArrayOP(intArrayOf(1, 2, 3, 4, 5, 2))}")    // [5, 2]
}

/**
 * Brute Force — For each element, check if any element to its right is larger.
 *
 * For each element arr[i], scan all elements to its right (arr[i+1..n-1]).
 * If we find any element >= arr[i], then arr[i] is NOT a leader.
 * If we reach the end without finding one, arr[i] IS a leader.
 *
 * Time Complexity:  O(N²) — for each of N elements, scan up to N elements to the right
 * Space Complexity: O(1)   — excluding the result array (no extra data structures)
 */
fun findLeadersInAnArrayBF(nums: IntArray): ArrayList<Int> {
    val result = ArrayList<Int>()
    val n = nums.size

    for (i in 0 until n) {
        var j = i + 1
        // Scan all elements to the right of i.
        while (j < n) {
            // If any element to the right is larger, arr[i] is not a leader.
            if (nums[i] < nums[j]) break
            j++
        }
        // If j reached the end, no larger element was found → arr[i] is a leader.
        if (j == n) result.add(nums[i])
    }

    return result
}

/**
 * Optimal — Right-to-Left Scan with Running Maximum
 *
 * Key insight: An element is a leader if it is >= all elements to its right.
 * This is equivalent to saying it is >= the maximum of all elements to its right.
 * So we can scan from right to left, keeping track of the running maximum.
 *
 * Steps:
 * 1. The rightmost element is always a leader. Add it to result and set maxRight = arr[n-1].
 * 2. Scan from right to left (i = n-2 down to 0):
 *    a. If arr[i] >= maxRight, it's a leader → add to result, update maxRight = arr[i].
 *    b. Otherwise, skip.
 * 3. Since we traverse right-to-left, leaders are collected in reverse order.
 *    Reverse the result list to restore left-to-right order.
 *
 * Trace for arr = [16, 17, 4, 3, 5, 2]:
 *
 *   Start: maxRight = 2, result = [2]
 *
 *   i=4 (5): 5 >= 2 → leader! result = [2, 5], maxRight = 5
 *   i=3 (3): 3 < 5  → not a leader
 *   i=2 (4): 4 < 5  → not a leader
 *   i=1 (17): 17 >= 5 → leader! result = [2, 5, 17], maxRight = 17
 *   i=0 (16): 16 < 17 → not a leader
 *
 *   Reverse: result = [17, 5, 2] ✅
 *
 * Time Complexity:  O(N) — single right-to-left pass + O(N) reverse
 * Space Complexity: O(1) — excluding the result array (only a running max variable)
 */
fun findLeadersInAnArrayOP(nums: IntArray): ArrayList<Int> {
    val result = ArrayList<Int>()
    val n = nums.size

    // The rightmost element is always a leader.
    var maxRight = nums[n - 1]
    result.add(maxRight)

    // Traverse from right to left, tracking the maximum seen so far.
    for (i in n - 2 downTo 0) {
        if (nums[i] >= maxRight) {
            maxRight = nums[i]
            result.add(maxRight)
        }
    }

    // Reverse to restore original left-to-right order.
    result.reverse()

    return result
}
