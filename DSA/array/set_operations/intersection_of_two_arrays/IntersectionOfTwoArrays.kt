package array.set_operations.intersection_of_two_arrays

/**
 * https://leetcode.com/problems/intersection-of-two-arrays/description/
 *
 * Given two integer arrays nums1 and nums2, return an array of their intersection. Each element in the result must be unique and you may return the result in any order.
 *
 * Example 1:
 *
 * Input: nums1 = [1,2,2,1], nums2 = [2,2]
 * Output: [2]
 * Example 2:
 *
 * Input: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
 * Output: [9,4]
 * Explanation: [4,9] is also accepted.
 */
fun main() {
    intersectionOfTwoArrayBF(intArrayOf(1,2,2,1), intArrayOf(2,2)).forEach {
        println(it)
    }
    intersectionOfTwoArrayOP(intArrayOf(1,2,2,1), intArrayOf(2,2)).forEach {
        println(it)
    }
}

fun intersectionOfTwoArrayBF(nums1: IntArray, nums2: IntArray): IntArray {
    val resultSet = hashSetOf<Int>()

    for(n1 in nums1) {
        for (n2 in nums2) {
            if (n1 == n2) {
                resultSet.add(n1)
            }
        }
    }

    return resultSet.toIntArray()
}

fun intersectionOfTwoArrayOP(nums1: IntArray, nums2: IntArray): IntArray {
    val resultSet = hashSetOf<Int>()
    val intArray = IntArray(nums2.size)

    for(n in nums1) {
        resultSet.add(n)
    }
    var k = 0
    for (num in nums2) {
        if (resultSet.remove(num)) {
            intArray[k++] = num
        }
    }

    return intArray.copyOf(k)
}