package sliding_window_technique

/**
 * https://leetcode.com/problems/fruit-into-baskets/description/
 *
 * You are visiting a farm that has a single row of fruit trees arranged from left to right. The trees are represented by an integer array fruits where fruits[i] is the type of fruit the ith tree produces.
 *
 * You want to collect as much fruit as possible. However, the owner has some strict rules that you must follow:
 *
 * You only have two baskets, and each basket can only hold a single type of fruit. There is no limit on the amount of fruit each basket can hold.
 * Starting from any tree of your choice, you must pick exactly one fruit from every tree (including the start tree) while moving to the right. The picked fruits must fit in one of your baskets.
 * Once you reach a tree with fruit that cannot fit in your baskets, you must stop.
 * Given the integer array fruits, return the maximum number of fruits you can pick.
 *
 *
 *
 * Example 1:
 *
 * Input: fruits = [1,2,1]
 * Output: 3
 * Explanation: We can pick from all 3 trees.
 * Example 2:
 *
 * Input: fruits = [0,1,2,2]
 * Output: 3
 * Explanation: We can pick from trees [1,2,2].
 * If we had started at the first tree, we would only pick from trees [0,1].
 * Example 3:
 *
 * Input: fruits = [1,2,3,2,2,3,3,2,3]
 * Output: 8
 * Explanation: We can pick from trees [2,3,2,2,3,3,2,3].
 * If we had started at the first tree, we would only pick from trees [1,2].
 */
fun main() {
println(totalFruitOP(intArrayOf(1,2,3,2,2,3,3,2,3)))
}

/**
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun totalFruitBF(fruits: IntArray): Int {
    var maxFruits = 0
    for (i in fruits.indices) {
        val basket = mutableSetOf<Int>()
        var currentCount = 0
        for (j in i until fruits.size) {
            basket.add(fruits[j])
            if (basket.size > 2) break
            currentCount++
        }
        maxFruits = maxOf(maxFruits, currentCount)
    }
    return maxFruits
}

fun totalFruitOP(fruits: IntArray): Int {
    val countMap = mutableMapOf<Int, Int>()
    var left = 0
    var maxFruits = 0

    for (right in fruits.indices) {
        // Add current fruit to the basket
        val rightFruit = fruits[right]
        countMap[rightFruit] = countMap.getOrDefault(rightFruit, 0) + 1

        // If we have more than 2 types of fruits, shrink the window
        while (countMap.size > 2) {
            val leftFruit = fruits[left]
            countMap[leftFruit] = countMap[leftFruit]!! - 1
            if (countMap[leftFruit] == 0) {
                countMap.remove(leftFruit)
            }
            left++
        }

        // Update the maximum fruits collected so far
        maxFruits = maxOf(maxFruits, right - left + 1)
    }

    return maxFruits
}

