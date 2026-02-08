package array_traversals

fun main() {
    val array = arrayOf(11,4,24,56,12,75,1,5,87,15,65)
    findSmallestOrLargestOP(array)
}

/**
 * Optimal Way O(N) - Time Complexity
 * O(N) - Space Complexity
 */
fun findSmallestOrLargestOP(value : Array<Int>) {
    var smallest = value[0]
    var largest = value[0]

    for (i in 1..<value.size) {
        if (value[i] < smallest) {
            smallest = value[i]
        }

        if (value[i] > largest) {
            largest = value[i]
        }
    }

    println("Largest -> $largest")
    println("Smallest -> $smallest")

}
