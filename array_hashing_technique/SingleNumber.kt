package array_hashing_technique

/**
 * https://leetcode.com/problems/single-number/description/
 *
 * This XOR trick is a very common pattern in coding interviews when dealing with pairs of numbers!
 *
 * Given a non-empty array of integers nums, every element appears twice except for one. Find that single one.
 *
 * You must implement a solution with a linear runtime complexity and use only constant extra space.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [2,2,1]
 *
 * Output: 1
 *
 * Example 2:
 *
 * Input: nums = [4,1,2,1,2]
 *
 * Output: 4
 */
fun main() {
   println( singleNumberBF(intArrayOf(2,2,1)))
   println( singleNumberOP(intArrayOf(2,2,1)))
   println( singleNumberOP1(intArrayOf(2,2,1)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun singleNumberBF(nums: IntArray): Int {
    for (i in nums.indices) {
        var count = 0
        for (element in nums) {
            if (nums[i] == element) {
                count++
            }
        }
        if (count == 1) {
            return nums[i]
        }
    }
    return nums[0]
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun singleNumberOP(nums: IntArray): Int {
    val set = mutableSetOf<Int>()
    for (num in nums) {
        if (set.contains(num)) {
            set.remove(num)
        } else {
            set.add(num)
        }
    }
    return set.iterator().next()
}

fun singleNumberOP1(nums: IntArray): Int {
    var result = 0
    for (num in nums) {
        result = result xor num
    }
    return result
}

