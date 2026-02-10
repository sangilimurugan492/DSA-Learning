package array_traversals

/**
 * Input: nums = [4,1,4,0,3,5]
 * Output: 2
 * Explanation:
 * 1. Remove 0 and 5, and the average is (0 + 5) / 2 = 2.5. Now, nums = [4,1,4,3].
 * 2. Remove 1 and 4. The average is (1 + 4) / 2 = 2.5, and nums = [4,3].
 * 3. Remove 3 and 4, and the average is (3 + 4) / 2 = 3.5.
 * Since there are 2 distinct numbers among 2.5, 2.5, and 3.5, we return 2.
 */

fun main() {
//    findDistinctAverageBF(arrayOf(4,1,4,0,3,5))
    findDistinctAverageOP(intArrayOf(4,1,4,0,3,5))
}

/**
 * Without Sorting
 * Time Complexity O(N^2)
 * Space Complexity O(N) all allocated spaces are less than N
 */
fun findDistinctAverageBF(array: Array<Int?>) {
    val resultSet = mutableSetOf<Double>()
    for (i in 0..array.size/2-1) {
        resultSet.add(findSmallestOrLargest(array))
    }
    println(resultSet.size)
}

fun findSmallestOrLargest(value : Array<Int?>) : Double {
    var smallest = value[0]
    var largest = value[0]
    var smallIndex = 0
    var largeInde = 0

    for (i in value.indices) {
        if (value[i] != null) {
            if (smallest == null && largest == null) {
                smallest = value[i]
                largest = value[i]
            }
            if (value[i] != null && smallest != null && value[i]!! < smallest) {
                smallest = value[i]
                smallIndex = i
            }

            if (value[i] != null && largest != null && value[i]!! > largest) {
                largest = value[i]
                largeInde = i
            }
        }
    }
    value[smallIndex] = null
    value[largeInde] = null
    return(smallest!!.toDouble()+ largest!!.toDouble())/2.0
}


fun findDistinctAverageOP(value: IntArray) {
    value.sort()
    val resultSet = mutableSetOf<Double>()
    for (i in 0 until  value.size/2) {
        resultSet.add((value[i].toDouble() + value[value.size - (i+1)])/2)
    }
    println(resultSet.size)
}
