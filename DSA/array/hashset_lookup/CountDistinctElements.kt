package array_traversals

/**
 * https://www.geeksforgeeks.org/dsa/count-distinct-elements-in-an-array/
 *
 * Input: arr[] = {10, 20, 20, 10, 30, 10}
 * Output: 3
 * Explanation: There are three distinct elements 10, 20, and 30.
 *
 *
 * Input: arr[] = {10, 20, 20, 10, 20}
 * Output: 2
 */
fun main() {
    println(countDistinctElementsBF(intArrayOf(10, 20, 20, 10, 30, 10)))
    println(countDistinctElementsOP(intArrayOf(10, 20, 20, 10, 30, 10)))
    println(countDistinctElementsOP1(intArrayOf(10, 20, 20, 10, 30, 10)))
}

/**
 * TIme Complexity O(N^2)
 * Space Complexity O(1)
 */
fun countDistinctElementsBF(array: IntArray) : Int {
    var res = 1
    for (i in 1 until array.size) {
        var j = 0
        while (j < i) {
            if (array[i] == array[j]) break
            j++
        }
        if (i == j) res++
    }
    return res
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun countDistinctElementsOP(array: IntArray) : Int {
    val resultSet = mutableSetOf<Int>()
    for (i in 0 until array.size) {
        resultSet.add(array[i])
    }
    return resultSet.size
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 * One Liner Code
 */
fun countDistinctElementsOP1(array: IntArray) : Int {
    return array.toSet().size
}



