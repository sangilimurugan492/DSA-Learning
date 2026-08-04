package patterns.two_pointer.three_sum_closest

/**
 * https://leetcode.com/problems/3sum-closest/
 * Find the sum of three integers in nums closest to target.
 * Example: nums = [-1,2,1,-4], target = 1 → Output: 2 (-1+2+1=2)
 * FAANG Importance: ⭐⭐⭐⭐ (Two-pointer + sorting pattern)
 */

fun main() {
    println(threeSumClosest(intArrayOf(-1, 2, 1, -4), 1))  // 2
    println(threeSumClosest(intArrayOf(0, 0, 0), 1))       // 0
}

/**
 * Time Complexity O(N^3)
 * Space Complexity O(N)
 */
fun threeSumClosetBF(nums: IntArray, target : Int): Int {

    var sum = nums[0] + nums[1] + nums[2]
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            for (k in j + 1 until nums.size) {
                val currentSum = nums[i] + nums[j] + nums[k]
                // If this new sum is closer to the target, update closestSum
                if (Math.abs(target - currentSum) < Math.abs(target - sum)) {
                    sum = currentSum
                }
            }
        }
    }
    return sum
}

/**
 * Two-Pointer: O(N²) time, O(1) space
 * Sort, fix one element, use two pointers for the other two.
 * Track the closest sum to target.
 */
fun threeSumClosest(nums: IntArray, target: Int): Int {
    nums.sort()
    var closest = nums[0] + nums[1] + nums[2]

    for (i in 0 until nums.size - 2) {
        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]
            if (kotlin.math.abs(sum - target) < kotlin.math.abs(closest - target)) {
                closest = sum
            }
            when {
                sum < target -> left++
                sum > target -> right--
                else -> return sum  // Exact match
            }
        }
    }
    return closest
}
