package array.two_pointer.four_sum

/**
 * https://leetcode.com/problems/4sum/description/
 * Given an array nums of n integers, return an array of all the unique quadruplets [nums[a], nums[b], nums[c], nums[d]] such that:
 *
 * 0 <= a, b, c, d < n
 * a, b, c, and d are distinct.
 * nums[a] + nums[b] + nums[c] + nums[d] == target
 * You may return the answer in any order.
 *
 *
 *
 * Example 1:
 *
 * Input: nums = [1,0,-1,0,-2,2], target = 0
 * Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
 * Example 2:
 *
 * Input: nums = [2,2,2,2,2], target = 8
 * Output: [[2,2,2,2]]
 */
fun main() {
    fourSumBF(intArrayOf(1000000000,1000000000,1000000000,1000000000), -294967296).forEach{it ->
        it.forEach{ it1->
            print("$it1 ")
        }
        println()
    }
    println()

    fourSumOP(intArrayOf(1,0,-1,0,-2,2), 0).forEach{it ->
        it.forEach{ it1->
            print("$it1 ")
        }
        println()
    }
}

fun fourSumBF(nums: IntArray, target: Int): List<List<Int>> {
    val resultSet = hashSetOf<List<Int>>()
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            for (k in j+1 until nums.size) {
                for (l in k + 1 until nums.size) {
                    if ((nums[i].toLong() + nums[j].toLong() + nums[k].toLong() + nums[l].toLong()) == target.toLong()) {
                        val result = listOf(nums[i], nums[j], nums[k], nums[l]).sorted()
                        resultSet.add(result)
                    }
                }
            }
        }
    }
    return resultSet.toList()
}

fun fourSumOP(nums: IntArray, target: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    val n = nums.size
    if (n < 4) return result

    // 1. Sort the array
    nums.sort()

    for (i in 0 until n - 3) {
        // Skip duplicate for first number
        if (i > 0 && nums[i] == nums[i - 1]) continue

        for (j in i + 1 until n - 2) {
            // Skip duplicate for second number
            if (j > i + 1 && nums[j] == nums[j - 1]) continue

            var left = j + 1
            var right = n - 1

            while (left < right) {
                // Use Long to prevent overflow for large target values
                val sum: Long = nums[i].toLong() + nums[j].toLong() +
                        nums[left].toLong() + nums[right].toLong()

                when {
                    sum == target.toLong() -> {
                        result.add(listOf(nums[i], nums[j], nums[left], nums[right]))
                        // Skip duplicates for left and right
                        while (left < right && nums[left] == nums[left + 1]) left++
                        while (left < right && nums[right] == nums[right - 1]) right--
                        left++
                        right--
                    }
                    sum < target -> left++
                    else -> right--
                }
            }
        }
    }
    return result
}