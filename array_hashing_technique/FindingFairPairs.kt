package array_hashing_technique

/**
* Input: nums = [0,1,7,4,4,5], lower = 3, upper = 6
* Output: 6
* Explanation: There are 6 fair pairs: (0,3), (0,4), (0,5), (1,3), (1,4), and (1,5).
 */


fun main() {
    findingFairPairsBF(intArrayOf(0,1,7,4,4,5), 3, 6)
    findingFairPairsOP(intArrayOf(0,1,7,4,4,5), 3, 6)
}

/* Brute Force Approach
* Time Complexity O(N^2)
* Space Complexity O(N)
*/

fun findingFairPairsBF(intArray: IntArray, lower: Int, upper: Int) {
    var count = 0
    for(i in intArray.indices) {
        for(j in i+1 until  intArray.size) {
            val temp = intArray[i] + intArray[j]
            if (temp in lower..upper) {
                count++
            }
        }
    }
    println(count)
}

/**
 * 0,1,7,4,4,5
 */
fun findingFairPairsOP(intArray: IntArray, lower: Int, upper: Int) {
    var count = 0
    val numMap = mutableMapOf<Int, Int>()
    for (i in intArray.indices) {
//        if (numMap.containsKey(intArray[i] + lower))
//            count += numMap[intArray[i] + lower]!!
//
//        if (numMap.containsKey(intArray[i] + upper))
//            count += numMap[intArray[i] + upper]!!

        numMap[intArray[i]] = numMap.getOrDefault(intArray[i], 0) + 1
    }

    for (i in intArray.indices) {
        val temp = intArray[i] + lower
        if (temp in lower..upper) {
            count++
        }
    }

    println(count)
}