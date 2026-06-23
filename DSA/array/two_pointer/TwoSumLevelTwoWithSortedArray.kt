package array.two_pointer

/**
 * https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/
 */
fun main() {
    println("Brute Force Approach")
    val result = twoSumLevelTwoWithSortedArrayBF(intArrayOf(2,7,11,15), 9)
    result.forEach {
        println(it)
    }

    println("Optimal Approach")
    val resultOP = twoSumLevelTwoWithSortedArrayOP(intArrayOf(2,7,11,15), 9)
    resultOP.forEach {
        println(it)
    }

}

/**
 * Input: nums = [2,7,11,15], target = 9
 * Output: [0,1]
 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
 * Brute Force Technique Same as Two Sum Level I
 * Time Complexity : O(N^2)
 * Space Complexity O(N)
 */

fun twoSumLevelTwoWithSortedArrayBF(numbers: IntArray, target: Int) : IntArray {

    for (i in numbers.indices) {
        for (j in i+1 until numbers.size) {
            if (target == (numbers[i] + numbers[j])) {
                return intArrayOf(i, j)
            }
        }
    }
    return intArrayOf()
}

/**
 * Input: nums = [2,7,11,15], target = 9
 * Output: [0,1]
 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
 * Brute Force Technique Same as Two Sum Level I
 * Time Complexity : O(N)
 * Space Complexity O(N) we are using extra space to compute that So Its O(2N) -> Dropping Constants O(N)
 */

fun twoSumLevelTwoWithSortedArrayOP(numbers: IntArray, target: Int) : IntArray {
    var left = 0
    var right = numbers.size - 1
    var sum: Int

    while (left < right) {
        sum = numbers[left] + numbers[right]
        if (sum > target) {
            right--
        } else if(sum < target) {
            left++
        } else {
            return intArrayOf(left, right)
        }
    }

    return intArrayOf()
}
