package array_traversals

fun main() {
    println("Brute Force ")
removeDuplicateFromSortedArrayBF(arrayOf(0,0,1,1,1,2,2,3,3,4))

    println("Using the Extra Data Structure ")
    removeDuplicateFromSortedArrayDS(arrayOf(0,0,1,1,1,2,2,3,3,4))

    println("Using the Optimal Approch ")
    removeDuplicateFromSortedArrayOP(arrayOf(0,0,1,1,1,2,2,3,3,4))
}

/**
 * Using Another Array and store unique value
 * Time Complexity O(N)
 * Space Complexity O(2N) ->
 */
fun removeDuplicateFromSortedArrayBF(array: Array<Int>) {
    val size = array.size
    val result = arrayOfNulls<Int>(size)
    var current = array[0]
    result[0] = array[0]
    var count = 1

    for (i in 0 until size) {
        if (array[i] != current) {
            result[count] = array[i]
            count++
        }
        current = array[i]
    }

    var resultSize = 0
    result.forEach {
        if(it != null) {
            resultSize++
        }
    }
    println(resultSize)

}

/**
 * Using Set Data Structure nd store unique value
 * Time Complexity O(NLogN)
 * Space Complexity O(2N) ->
 */
fun removeDuplicateFromSortedArrayDS(array: Array<Int>) {
    val size = array.size
    val resultSet = mutableSetOf<Int> ()
    for (i in 0 until size) {
        resultSet.add(array[i])
    }
    println(resultSet.size)

}

/**
 * Using Optimal Approach
 * Time Complexity O(N)
 * Space Complexity O(N) ->
 */
fun removeDuplicateFromSortedArrayOP(array: Array<Int>) {
    val size = array.size
    var count = 0
    var current = 0

    if (array.isNotEmpty()) {
        count = 1
        current = array[0]
    }

    for (i in 1 until size) {
        if (current != array[i]) {
            array[count] = array[i]
            count++
        }
        current = array[i]
    }
    println(count)

}