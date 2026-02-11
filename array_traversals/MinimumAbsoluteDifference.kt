package array_traversals

import kotlin.math.abs

/**
 * https://leetcode.com/problems/minimum-absolute-difference/description/
 * Given an array of distinct integers arr, find all pairs of elements with the minimum absolute difference of any two elements.
 *
 * Return a list of pairs in ascending order(with respect to pairs), each pair [a, b] follows
 *
 * a, b are from arr
 * a < b
 * b - a equals to the minimum absolute difference of any two elements in arr
 *
 * Input: arr = [4,2,1,3]
 * Output: [[1,2],[2,3],[3,4]]
 * Explanation: The minimum absolute difference is 1. List all pairs with difference equal to 1 in ascending order.
 */

fun main() {
    maximumAbsoluteDifferenceBF(intArrayOf(4,2,1,3))
    maximumAbsoluteDifferenceOP(intArrayOf(4,2,1,3))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(2N)
 *
 */
fun maximumAbsoluteDifferenceBF(nums : IntArray) {

    val resultList : MutableList<MutableList<Int>> = mutableListOf()
    var minDiff : Int = abs(nums[0]- nums[1])
    var crDiff : Int
    for(i in nums.indices) { // Finding All Possible Pairs
        for(j in i+1 until nums.size) {
            resultList.add(mutableListOf(nums[i], nums[j]))
            crDiff = abs(nums[i] - nums[j])
            if (crDiff < minDiff) {
                minDiff = crDiff
            }
        }
    }
     var count = 0
    val index = arrayOfNulls<Int>(resultList.size)
    resultList.forEachIndexed { indx, it ->   // check whether which having minimum diff
        crDiff = abs(it[0] - it[1])
        if (crDiff > minDiff) {
            index[count] = indx
            count++
        }
    }
    index.forEachIndexed { id , it ->  // Remove unwanted data
        if (it != null) {
            resultList.removeAt(it - id)
        }
    }

    resultList.sortWith(Comparator { a, b ->  // Sort the Element
        if (a[0] != b[0]) return@Comparator a[0] - b[0]
        a[1] - b[1]
    })
    resultList.forEach {
        println("${it[0]}, ${it[1]}")
    }
}

/**
 * Time Complexity O(N logN)  because of sorting the array first
 * Space Complexity O(N) - Not using any extra space to store the data,
 * but we are returning result as per the problem statement
 */
fun maximumAbsoluteDifferenceOP(nums : IntArray) {
    nums.sort()
    val resultSet : MutableList<MutableList<Int>> = mutableListOf()
    var minDiff : Int = Int.MAX_VALUE
    val n = nums.size
    for (i in 1 until  n) {
        minDiff = minDiff.coerceAtMost((nums[i] - nums[i - 1]))
    }

    for(i in 1 until  n) {
        if (nums[i] - nums[i - 1] == minDiff) {
            resultSet.add(mutableListOf(nums[i-1], nums[i]))
        }
    }

    resultSet.forEach {
        println("${it[0]}, ${it[1]}")
    }
}