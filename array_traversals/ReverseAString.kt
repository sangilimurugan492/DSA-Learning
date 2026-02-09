package array_traversals

fun main() {
    val array = charArrayOf('h','e','l','l','o')

    reverseAStringBF(array)
    println()
    reverseAStringOP(array)

//    Output: [5, 6, 2, 3, 4, 1])
}

/**
 * Reverse an Array
 * Bruteforce
 * O(N)
 * We are using Extra Space and O(N) Time Complexity
 */
fun reverseAStringBF(array: CharArray) {
    val size = array.size
    val bArray = arrayOfNulls<Char>(size = size)
    for (i in 0 until size) {
        bArray[i] = array[size - (i + 1)]
    }
    print("Brute Force Approach -> [")
    bArray.forEach { print("$it ") }
    print("]")
}


/**
 * Reverse an Array
 * Optimal
 * O(N/2)
 * We are using No Extra Space and O(N/2) Time Complexity
 */
fun reverseAStringOP(array: CharArray) {
    val size = array.size
    var temp: Char
    for (i in 0 until size/2) {
        temp = array[i]
        array[i] = array[size - (i+1)]
        array[size - (i+1)] = temp
    }
    print("Optimal Approach -> [")
    array.forEach { print("$it ") }
    print("]")
}