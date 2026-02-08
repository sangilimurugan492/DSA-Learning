package array_traversals

fun main() {

    /**
     * Same like the Remove Duplicates I But thing is it will same element twice and in place sort
     */
    removeDuplicateFromSortedArrayIIOP(arrayOf(0,0,1,1,1,1,2,3,3))
}

/**
 * Using Optimal Approach
 * Time Complexity O(N)
 * Space Complexity O(N) ->
 */
fun removeDuplicateFromSortedArrayIIOP(array: Array<Int>) {
    val size = array.size
    var totalCount = 0
    var currentCount =0
    var currentValue = 0

    if (array.isNotEmpty()) {
        totalCount = 1
        currentCount = 1
        currentValue = array[0]
    }

    for (i in 1 until size) {
        if (currentValue != array[i] || (currentValue == array[i] && currentCount < 2)) {
            array[totalCount] = array[i]
            totalCount++
            if (currentValue != array[i]) {
                currentCount = 0
            }
            currentCount++
        }
        currentValue = array[i]
    }
    println(totalCount)

}