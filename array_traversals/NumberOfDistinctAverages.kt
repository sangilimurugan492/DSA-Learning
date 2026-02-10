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
    findDistinctAverageBF(arrayOf(4,1,4,0,3,5))
}

/**
 *
 */
fun findDistinctAverageBF(array: Array<Int?>) {
    val resultSet = mutableSetOf<Float>()
    for (i in array.indices) {
        resultSet.add(findSmallestOrLargest(array))
    }
}

fun findSmallestOrLargest(value : Array<Int?>) : Float {
    var smallest = value[0]
    var largest = value[0]
    var smallIndex = 0
    var largeInde = 0

    for (i in 1..<value.size) {
        if (value[i] != null && smallest != null && value[i]!! < smallest) {
            smallest = value[i]
            smallIndex = i
        }

        if (value[i] != null && largest != null && value[i]!! > largest) {
            largest = value[i]
            largeInde = i
        }
    }
    value[smallIndex] = null
    value[largeInde] = null
    var total = smallest
    return ((smallest?.let { 0 } + largest?.let { 0 })/2).toFloat()
}
