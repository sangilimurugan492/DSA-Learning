package array_traversals

/**
 * https://leetcode.com/problems/majority-element/description/
 * Given an array nums of size n, return the majority element.
 *
 * The majority element is the element that appears more than ⌊n / 2⌋ times. You may assume that the majority element always exists in the array.
 *
 * Example 1:
 *
 * Input: nums = [3,2,3]
 * Output: 3
 * Example 2:
 *
 * Input: nums = [2,2,1,1,1,2,2]
 * Output: 2
 */
fun main() {
    val resultBF = majorityOfElementsBF(intArrayOf(3,2,3))
    val resultOP = majorityOfElementsOP(intArrayOf(3,2,3))

    println(
        "Result BF $resultBF" + "\n"+
                "Result OP $resultOP"
    )
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun majorityOfElementsBF(nums : IntArray) : Int {
    var count : Int = 0
    var element : Int = 0

    for(i in nums.indices) {
        count = 0
        element = nums[i]
        for (j in nums.indices) {
            if (nums[i] == nums[j]) {
                count++
            }
        }
    }

    if (count > nums.size/2) {
        return element
    } else {
        return 0
    }
}

/**
 * TIme complexity O(N)
 * Space Complexity O(1)
 */
fun majorityOfElementsOP(nums : IntArray) : Int {
    var mc = nums[0]
    var count = 1
    for (i in 1 until nums.size) {
        if (count == 0) {
            mc = nums[i]
            count = 1
        } else if (mc == nums[i]) {
            count++
        }
        else {
            count--
        }
    }
    return mc
}