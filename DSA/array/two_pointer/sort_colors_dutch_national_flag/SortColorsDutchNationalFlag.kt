package array.two_pointer.sort_colors_dutch_national_flag

/**
 * https://leetcode.com/problems/sort-colors/description/
 *
 * Given an array nums with values 0 (red), 1 (white), 2 (blue),
 * sort them in-place so that same colors are adjacent (Dutch National Flag).
 *
 * Example: [2,0,1] → [0,1,2]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic 3-way partition)
 *
 * Key Idea (Dutch National Flag):
 *   - Use 3 pointers: low (boundary for 0s), mid (scanner), high (boundary for 2s)
 *   - If nums[mid] == 0 → swap with low, move both low++ and mid++
 *   - If nums[mid] == 1 → already in place, just mid++
 *   - If nums[mid] == 2 → swap with high, move high-- (don't move mid, need to check swapped value)
 */
fun main() {
    println("Brute Force")
    sortColorsDutchFlagBF(intArrayOf(2, 0, 2, 1, 1, 0))
    println("\nOptimal (Dutch National Flag)")
    sortColorsDutchFlagOP(intArrayOf(2, 0, 1))
}

/**
 * Brute Force: Bubble-sort style — swap 0s to front, 2s to back.
 *
 * Time Complexity:  O(N²)
 * Space Complexity: O(1)
 */
fun sortColorsDutchFlagBF(array: IntArray) {
    for (i in array.indices) {
        for (j in i + 1 until array.size) {
            if (array[i] > array[j]) {
                val temp = array[i]
                array[i] = array[j]
                array[j] = temp
            }
        }
    }
    array.forEach { print("$it ") }
}

/**
 * Optimal: Dutch National Flag — 3-way partition in one pass.
 *
 * Three zones:
 *   [0 ..low-1] → all 0s
 *   [low ..mid-1] → all 1s
 *   [mid ..high] → unknown
 *   [high+1. .end] → all 2s
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1)
 */
fun sortColorsDutchFlagOP(array: IntArray) {
    var low = 0
    var mid = 0
    var high = array.size - 1

    while (mid <= high) {
        when (array[mid]) {
            0 -> {
                // Swap mid with low → 0 goes to the front zone
                val temp = array[low]
                array[low] = array[mid]
                array[mid] = temp
                low++
                mid++
            }
            1 -> {
                // 1 is already in the middle zone — just advance
                mid++
            }
            2 -> {
                // Swap mid with high → 2 goes to the back zone
                val temp = array[high]
                array[high] = array[mid]
                array[mid] = temp
                high--
                // Don't move mid — the swapped-in value is unknown
            }
        }
    }
    array.forEach { print("$it ") }
}
