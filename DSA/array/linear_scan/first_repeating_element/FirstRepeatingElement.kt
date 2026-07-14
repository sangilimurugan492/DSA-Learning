package array.linear_scan.first_repeating_element

import kotlin.math.min


/**
 * https://www.geeksforgeeks.org/dsa/find-first-repeating-element-array-integers/
 *
 * Input: arr[] = {10, 5, 3, 4, 3, 5, 6}
 * Output: 5
 * Explanation: 5 is the first element that repeats
 *
 * Input: arr[] = {6, 10, 5, 4, 9, 120, 4, 6, 10}
 * Output: 6
 * Explanation: 6 is the first element that repeats
 */
fun main() {
    println(intArrayOf(10, 5, 3, 4, 3, 5, 6) [firstRepeatingElementBF(intArrayOf(10, 5, 3, 4, 3, 5, 6))])
    println(intArrayOf(10, 5, 3, 4, 3, 5, 6) [firstRepeatingElementOP(intArrayOf(10, 5, 3, 4, 3, 5, 6))])
}


/**
 * TIme Complexity O(N^2)
 * Space Complexity O(1)
 */
fun firstRepeatingElementBF(nums : IntArray) : Int{
    for (i in nums.indices) {
        for (j in i+1 until nums.size) {
            if (nums[i] == nums[j]) {
                return i
            }
        }
    }
    return -1
}

/**
 * TIme Complexity O(N)
 * Space Complexity O(N)
 */
fun firstRepeatingElementOP(nums : IntArray) : Int{
    val s = HashSet<Int>()


    // If an element is already present, return it
    // else insert it
    var minEle = Int.MAX_VALUE
    for (i in nums.size - 1 downTo 0) {
        if (s.contains(nums[i])) {
            minEle = min(minEle.toDouble(), i.toDouble()).toInt()
        }
        s.add(nums[i])
    }


    // If no element repeats
    return if (minEle == Int.MAX_VALUE) -1 else minEle
}
