package array.sliding_window

/**
 * https://leetcode.com/problems/fruit-into-baskets/
 * Given array of fruit types, pick 2 types maximally (longest subarray with at most 2 distinct).
 * Example: [1,2,1] → 3 | [0,1,2,2] → 3 | [1,2,3,2,2] → 4
 * FAANG Importance: ⭐⭐⭐⭐
 */

fun main() {
    println(totalFruitBruteForce(intArrayOf(1, 2, 1)))
    println(totalFruitBruteForce(intArrayOf(0, 1, 2, 2)))
    println(totalFruitBruteForce(intArrayOf(1, 2, 3, 2, 2)))
    println("---")
    println(totalFruitSlidingWindow(intArrayOf(1, 2, 1)))
    println(totalFruitSlidingWindow(intArrayOf(0, 1, 2, 2)))
    println(totalFruitSlidingWindow(intArrayOf(1, 2, 3, 2, 2)))
}

/**
 * BRUTE FORCE: O(N²) — check every subarray, track distinct count
 */
fun totalFruitBruteForce(fruits: IntArray): Int {
    var maxLen = 0
    for (i in fruits.indices) {
        val types = mutableSetOf<Int>()
        for (j in i until fruits.size) {
            types.add(fruits[j])
            if (types.size > 2) break
            maxLen = maxOf(maxLen, j - i + 1)
        }
    }
    return maxLen
}

/**
 * OPTIMAL: O(N) Sliding Window — at most 2 distinct types
 */
fun totalFruitSlidingWindow(fruits: IntArray): Int {
    val count = hashMapOf<Int, Int>()
    var left = 0
    var maxLen = 0

    for (right in fruits.indices) {
        count[fruits[right]] = count.getOrDefault(fruits[right], 0) + 1

        while (count.size > 2) {
            count[fruits[left]] = count[fruits[left]]!! - 1
            if (count[fruits[left]] == 0) count.remove(fruits[left])
            left++
        }
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
