package array.two_pointer.remove_element_in_an_array

/**
 * https://leetcode.com/problems/remove-element/description/
 *
 * Given an array nums and a value val, remove all instances of val in-place.
 * Return the new length k. The first k elements should be the non-val elements.
 *
 * Example: nums = [3,2,2,3], val = 3 → k=2, nums = [2,2,_,_]
 *
 * Key Idea: Use a "write pointer" `count` that only advances when we keep an element.
 * Same pattern as Move Zeros — overwrite unwanted elements in-place.
 */
fun main() {
    println("Brute Force Approach")
    removeElementInArrayBF(intArrayOf(3, 2, 2, 3), 3)
    println("\nOptimal Approach (Two Pointer)")
    removeElementInArrayOP(intArrayOf(3, 2, 2, 3), 3)
}

/**
 * Brute Force: For each element equal to val, swap it to the back.
 *
 * Time Complexity:  O(N²)
 * Space Complexity: O(1)
 */
fun removeElementInArrayBF(intArray: IntArray, element: Int) {
    var count = 0
    for (i in intArray.indices) {
        for (j in i + 1 until intArray.size) {
            if (element != intArray[j]) {
                val temp = intArray[i]
                intArray[i] = intArray[j]
                intArray[j] = temp
                count++
                break
            }
        }
    }
    println("After Removed Element Count :: -> $count")
    intArray.forEach { print("$it ") }
}

/**
 * Optimal (Two Pointer): Use `count` as a write pointer.
 * If the current element is NOT val, swap it to position `count` and advance `count`.
 * Elements equal to val are overwritten / pushed to the back.
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1) — in-place
 */
fun removeElementInArrayOP(intArray: IntArray, element: Int) {
    var count = 0

    for (i in intArray.indices) {
        if (element != intArray[i]) {
            val temp = intArray[count]
            intArray[count] = intArray[i]
            intArray[i] = temp
            count++
        }
    }
    println("After Removed Element Count :: -> $count")
    intArray.forEach { print("$it ") }
}
