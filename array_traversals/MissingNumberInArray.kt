package array_traversals

/**
 * https://leetcode.com/problems/missing-number/description/
 *
 * Given an array nums containing n distinct numbers in the range [0, n], return the only number in the range that is missing from the array.
 *Example 1:
 *
 * Input: nums = [3,0,1]
 *
 * Output: 2
 *
 * Explanation:
 *
 * n = 3 since there are 3 numbers, so all numbers are in the range [0,3]. 2 is the missing number in the range since it does not appear in nums.
 *
 */

fun main() {
    println(findingMissingNumberBF(intArrayOf(3,0,1)))
    println(findingMissingNumberOP(intArrayOf(3,0,1)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 * Time Taken : 85 ms
 */
fun findingMissingNumberBF(nums : IntArray) : Int{
    val n: Int = nums.size + 1


    // Iterate from 1 to n and check
    // if the current number is present
    for (i in 1..n) {
        var found = false
        for (j in 0 until n - 1) {
            if (nums[j] == i) {
                found = true
                break
            }
        }

        // If the current number is not present
        if (!found) return i
    }
    return -1

}


/**
 * Time Complexity O(N) - Calculate sum will iterate all values
 * Space Complexity O(N)
 * Time Taken 0 Ms
 */
fun findingMissingNumberOP(nums : IntArray) : Int {
    val n: Long = (nums.size).toLong()

    // Calculate the sum of array elements
    var sum: Long = 0
    for (i in nums.indices) {
        sum += nums[i]
    }

    // Use long for expected sum to avoid overflow
    val expSum = n * (n + 1) / 2

    // Return the missing number
    return (expSum - sum).toInt()
}
