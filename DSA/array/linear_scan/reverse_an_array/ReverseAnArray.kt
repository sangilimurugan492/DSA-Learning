package array.linear_scan.reverse_an_array

/**
 * Reverse an array of integers in-place.
 *
 * Given an array, reverse the order of its elements.
 *
 * Example 1:
 *   Input:  arr = [1, 4, 3, 2, 6, 5, 7]
 *   Output: [7, 5, 6, 2, 3, 4, 1]
 *
 * Example 2:
 *   Input:  arr = [1, 2, 3, 4, 5]
 *   Output: [5, 4, 3, 2, 1]
 */
fun main() {
    val arr1 = arrayOf(1, 4, 3, 2, 6, 5, 7)
    reverseArrayBF(arr1)
    println(arr1.toList()) // [7, 5, 6, 2, 3, 4, 1]

    val arr2 = arrayOf(1, 4, 3, 2, 6, 5, 7)
    reverseArrayOP(arr2)
    println(arr2.toList()) // [7, 5, 6, 2, 3, 4, 1]
}

/**
 * Brute Force — Extra Array
 *
 * Create a new array and copy elements in reverse order, then copy back.
 *
 * Time Complexity:  O(N) — single pass to copy
 * Space Complexity: O(N) — extra array of size N
 */
fun reverseArrayBF(array: Array<Int>) {
    val n = array.size
    val temp = arrayOfNulls<Int>(n)
    for (i in 0 until n) {
        temp[i] = array[n - 1 - i]
    }
    // Copy back into original array
    for (i in 0 until n) {
        array[i] = temp[i]!!
    }
}

/**
 * Optimal — Two Pointers (In-Place Swap)
 *
 * Use two pointers: left (start) and right (end). Swap array[left] and array[right],
 * then move both pointers inward until they meet in the middle.
 *
 * Trace for arr = [1, 4, 3, 2, 6, 5, 7]:
 *
 *   left=0, right=6: swap 1 and 7 → [7, 4, 3, 2, 6, 5, 1]
 *   left=1, right=5: swap 4 and 5 → [7, 5, 3, 2, 6, 4, 1]
 *   left=2, right=4: swap 3 and 6 → [7, 5, 6, 2, 3, 4, 1]
 *   left=3, right=3: left >= right → stop
 *
 *   Result = [7, 5, 6, 2, 3, 4, 1] ✅
 *
 * Time Complexity:  O(N) — N/2 swaps, each O(1)
 * Space Complexity: O(1) — in-place, only a temp variable
 */
fun reverseArrayOP(array: Array<Int>) {
    var left = 0
    var right = array.size - 1

    while (left < right) {
        // Swap array[left] and array[right]
        val temp = array[left]
        array[left] = array[right]
        array[right] = temp

        left++
        right--
    }
}
