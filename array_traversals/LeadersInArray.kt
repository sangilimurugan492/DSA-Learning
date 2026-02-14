package array_traversals

import java.util.*


/**
 * https://www.geeksforgeeks.org/dsa/leaders-in-an-array/
 * Input: arr[] = [16, 17, 4, 3, 5, 2]
 * Output: [17 5 2]
 * Explanation: 17 is greater than all the elements to its right i.e., [4, 3, 5, 2], therefore 17 is a leader. 5 is greater than all the elements to its right i.e., [2], therefore 5 is a leader. 2 has no element to its right, therefore 2 is a leader.
 *
 * Input: arr[] = [1, 2, 3, 4, 5, 2]
 * Output: [5 2]
 * Explanation: 5 is greater than all the elements to its right i.e., [2], therefore 5 is a leader. 2 has no element to its right, therefore 2 is a leader.
 */
fun main() {
    findLeadersInAnArrayBF(intArrayOf(16, 17, 4, 3, 5, 2)).forEach{
        println(it)
    }

    findLeadersInAnArrayOP(intArrayOf(16, 17, 4, 3, 5, 2)).forEach{
        println(it)
    }
}


/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun findLeadersInAnArrayBF(nums : IntArray) : ArrayList<Int> {
    val result = ArrayList<Int>()
    val n: Int = nums.size

    for (i in 0 until n) {
        // Check elements to the right
        var j = i + 1
        while (j < n) {
            // If a larger element is found
            if (nums[i] < nums[j]) break
            j++
        }


        // If no larger element was found
        if (j == n) result.add(nums[i])
    }

    return result
}


/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun findLeadersInAnArrayOP(nums : IntArray) : ArrayList<Int>{

    val result = ArrayList<Int>()
    val n: Int = nums.size


    // Start with the rightmost element
    var maxRight: Int = nums[n - 1]


    // Rightmost element is always a leader
    result.add(maxRight)


    // Traverse the array from right to left
    for (i in n - 2 downTo 0) {
        if (nums.get(i) >= maxRight) {
            maxRight = nums[i]
            result.add(maxRight)
        }
    }


    // Reverse the result list to maintain
    // original order
    result.reverse()

    return result
}
