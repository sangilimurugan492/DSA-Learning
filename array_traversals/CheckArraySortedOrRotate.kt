package array_traversals

fun main() {

    val array = arrayOf(3,4,5,1,2)
    //Brute Force Solution
    checkArraySortedOrRotateBF(array)

    //Optimal Approach
    checkIfSortedRotateOp(array)
}


/**
 * Check array Sorted or Rotate
 * https://leetcode.com/problems/check-if-array-is-sorted-and-rotated/description/
 * B[i] == A[(i+x) % A.length]
 * Brute Force
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 */

fun checkArraySortedOrRotateBF(array : Array<Int>) {
    val n = array.size
    val bArray = array.clone()
    bArray.sort() // O(N Log N) its Lower then O(N^2)
    println(bArray[n-1])
    for (k in 0 until n) {
        var isMatch = true
        for(i in 0 until n) {
            if (array[i] != bArray[(i+k) % n]) {
                isMatch = false
                break
            }
        }
        if (isMatch) {
            println("Is Sorted Array")
        }
    }
}

/**
 * Check array Sorted or Rotate
 * B[i] == A[(i+x) % A.length]
 * Optimal
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun checkIfSortedRotateOp(array : Array<Int>) {
    val size = array.size
    var count = 0
    for (i in 0 until size) {
        if (array[i] > array[(i + 1) % size]) {
            count ++
        }
        if (count > 1) {
            println("Is Not Sorted Array")
        }
    }
    println("Is Sorted Array")
}
