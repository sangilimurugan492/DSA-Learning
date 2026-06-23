package array.two_pointer_inplace

/**
 * https://leetcode.com/problems/replace-elements-with-greatest-element-on-right-side/description/
 *
 * Replace Elements with Greatest Element on Right Side
 *
 * Given an array arr, replace every element in that array with the greatest element among the elements to its right, and replace the last element with -1.
 *
 * After doing so, return the array.
 *
 *
 * Example 1:
 *
 * Input: arr = [17,18,5,4,6,1]
 * Output: [18,6,6,6,1,-1]
 * Explanation:
 * - index 0 --> the greatest element to the right of index 0 is index 1 (18).
 * - index 1 --> the greatest element to the right of index 1 is index 4 (6).
 * - index 2 --> the greatest element to the right of index 2 is index 4 (6).
 * - index 3 --> the greatest element to the right of index 3 is index 4 (6).
 * - index 4 --> the greatest element to the right of index 4 is index 5 (1).
 * - index 5 --> there are no elements to the right of index 5, so we put -1.
 * Example 2:
 *
 * Input: arr = [400]
 * Output: [-1]
 * Explanation: There are no elements to the right of index 0.
 */
fun main() {
    println("Brute Force Approach")
    replaceElementsBF(intArrayOf(56903,18666,60499,57517,26961)).forEach {
        print("$it ")
    }
    println("\nOptimal Approach")
    replaceElementsOP(intArrayOf(56903,18666,60499,57517,26961)).forEach {
        print("$it ")
    }

}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 * Leet Code Time Taken - 491 MS
 */
fun replaceElementsBF(arr: IntArray): IntArray {
    val n = arr.size
    for (i in 1 until n) {
        var lar = arr[i]
        for (j in i + 1 until n) {
            if(arr[j] > lar) {
                lar = arr[j]
            }
        }
        arr[i - 1] = lar
    }
    arr[n -1] = -1

    return arr
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 * Leet Code Time Taken - 2 MS
 * Logic reverse traversal for replace max element
 */
fun replaceElementsOP(arr: IntArray): IntArray {
    var lar = -1
    var n = arr.size - 1
    while (n >= 0) {
        val item = arr[n]
        arr[n] = lar
        if (item > lar) {
            lar = item
        }
        n--
    }
    return arr
}