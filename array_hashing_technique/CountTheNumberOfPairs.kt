package array_hashing_technique


fun main() {
    countNumberOfPairsBF(intArrayOf(1,2,2,1), 1)
    countNumberOfPairsOP(intArrayOf(1,2,2,1), 1)
}
/**
 * Input: nums = [1,2,2,1], k = 1
 * Output: 4
 * Explanation: The pairs with an absolute difference of 1 are:
 * - [1,2,2,1]
 * - [1,2,2,1]
 * - [1,2,2,1]
 * - [1,2,2,1]
 *
 * Brute Force Approach
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 */

fun countNumberOfPairsBF(intArray: IntArray, k : Int) {
    var count = 0

    for(i in intArray.indices) {
        for(j in i+1 until  intArray.size) {
            if (k == Math.abs(intArray[i] -intArray[j])) {
                count++
            }
        }
    }
    println(count)
}

/**
 * Optimal Approach
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun countNumberOfPairsOP(intArray: IntArray, k : Int) {
    var count = 0
    val numMap = mutableMapOf<Int,Int>()
    for (i in intArray.indices) {
        if (numMap.containsKey(intArray[i] + k))
            count += numMap[intArray[i] + k]!!

        if (numMap.containsKey(intArray[i] - k))
            count += numMap[intArray[i] - k]!!

        numMap[intArray[i]] = numMap.getOrDefault(intArray[i], 0) + 1
    }

    println(count)
}