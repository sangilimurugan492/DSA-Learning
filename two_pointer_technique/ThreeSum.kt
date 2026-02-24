package two_pointer_technique

/**
 * https://leetcode.com/problems/3sum/
 * Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0.
 *
 * Notice that the solution set must not contain duplicate triplets.
 *
 * Example 1:
 *
 * Input: nums = [-1,0,1,2,-1,-4]
 * Output: [[-1,-1,2],[-1,0,1]]
 * Explanation:
 * nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0.
 * nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0.
 * nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0.
 * The distinct triplets are [-1,0,1] and [-1,-1,2].
 * Notice that the order of the output and the order of the triplets does not matter.
 * Example 2:
 *
 * Input: nums = [0,1,1]
 * Output: []
 * Explanation: The only possible triplet does not sum up to 0.
 */
fun main() {
    threeSumBF(intArrayOf(-1,0,1,2,-1,-4)).forEach {
        it.forEach{ it1->
            print("$it1 ")
        }
        println()
    }
    println()
    threeSumOP(intArrayOf(-1,0,1,2,-1,-4)).forEach {
        it.forEach{ it1->
            print("$it1 ")
        }
        println()
    }
}

/**
 * Time Complexity O(N^3)
 * Space Complexity O(N)
 */
fun threeSumBF(nums: IntArray): List<List<Int>> {
    val list = mutableSetOf<List<Int>>()

    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            for (k in j + 1 until nums.size) {
                if ((nums[i] + nums[j] + nums[k]) == 0) {
                   val triplet = arrayListOf(nums[i], nums[j], nums[k]).sorted()
                    list.add(triplet)
                }
            }
        }
    }

    return list.toList()
}

/**
 * Time Complexity O(n^2)
 * Space Complexity O(1) or O(log n)
 */
fun threeSumOP(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    nums.sort() // Sort the array first

    for (i in 0 until nums.size - 2) {
        // Skip duplicate values for the first element
        if (i > 0 && nums[i] == nums[i - 1]) continue

        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]

            when {
                sum == 0 -> {
                    result.add(listOf(nums[i], nums[left], nums[right]))
                    // Move pointers and skip duplicates
                    while (left < right && nums[left] == nums[left + 1]) left++
                    while (left < right && nums[right] == nums[right - 1]) right--
                    left++
                    right--
                }
                sum < 0 -> left++ // Sum too small, move left pointer right
                else -> right--   // Sum too large, move right pointer left
            }
        }
    }
    return result
}