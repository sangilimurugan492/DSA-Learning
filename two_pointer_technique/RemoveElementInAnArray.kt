package two_pointer_technique


fun main() {
    println("Brute Force Approach")
    removeElementInArrayBF(intArrayOf(3,2,2,3), 3)
    println("Optimal Approach")
    removeElementInArrayOP(intArrayOf(3,2,2,3), 3)
}

/**
 * https://leetcode.com/problems/remove-element/description/
 * Remove Element In Array
 * Input: nums = [3,2,2,3], val = 3
 * Output: 2, nums = [2,2,_,_]
 * Explanation: Your function should return k = 2, with the first two elements of nums being 2.
 * It does not matter what you leave beyond the returned k (hence they are underscores).
 * Time Complexity O(N)
 * Space Complexity O(N)
 */
fun removeElementInArrayBF(intArray: IntArray, element : Int) {
    var count = 0
    var temp: Int
    for (i in intArray.indices) {
        for (j in i + 1 until intArray.size) {
            if (element != intArray[j]) {
                temp = intArray[i]
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
 * Remove Element In Array
 * Input: nums = [3,2,2,3], val = 3
 * Output: 2, nums = [2,2,_,_]
 * Explanation: Your function should return k = 2, with the first two elements of nums being 2.
 * It does not matter what you leave beyond the returned k (hence they are underscores).
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 */
fun removeElementInArrayOP(intArray: IntArray, element : Int) {
    var count = 0
    var temp : Int

        for(i in  intArray.indices) {
            if (element != intArray[i]) {
                temp = intArray[count]
                intArray[count] = intArray[i]
                intArray[i] = temp
                count++
            }
        }
    println("After Removed Element Count :: -> $count")
    intArray.forEach { print("$it ") }
}
