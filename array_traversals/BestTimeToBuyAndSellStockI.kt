package array_traversals


/**
 * You are given an array prices where prices[i] is the price of a given stock on the ith day.
 *
 * You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
 *
 * Return the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.
 *
 *
 *
 * Example 1:
 *
 * Input: prices = [7,1,5,3,6,4]
 * Output: 5
 * Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.
 * Note that buying on day 2 and selling on day 1 is not allowed because you must buy before you sell.
 *
 * Example 2:
 *
 * Input: prices = [7,6,4,3,1]
 * Output: 0
 * Explanation: In this case, no transactions are done and the max profit = 0.
 */
fun main() {
    println("Brute Force Approach ::\n ${bestTimeToBuyAndSellStockIBF(intArrayOf(7,1,5,3,6,4))}")
    println("Optimal Approach ::\n ${bestTimeToBuyAndSellStockIOP1(intArrayOf(7,1,5,3,6,4))}")
}


/**
 * Time Complexity O(N^2)
 * Space ComplexityO O(N)
 * Result in leetcode - Time Limit Exceeded
 */
fun bestTimeToBuyAndSellStockIBF(nums : IntArray) : Int {

    var maxProfit = 0
    var currentProfit: Int
    for(i in nums.indices) {
        for (j in i+1 until nums.size) {
            currentProfit = nums[j] - nums[i]
            if (maxProfit < currentProfit) {
                maxProfit = currentProfit
            }
        }
    }

    return maxProfit
}

/**
 * Time Complexity O(N)
 * Space ComplexityO O(N)
 * LeetCode Result - 3 MS
 */
fun bestTimeToBuyAndSellStockIOP1(nums : IntArray) : Int {

    var maxProfit =0
    var currentProfit: Int
    var minValue = nums[0]

    for (i in 1 until nums.size) {
        currentProfit = nums[i] - minValue
        if (minValue > nums[i])
            minValue = nums[i]
        if(currentProfit > maxProfit)
            maxProfit = currentProfit
    }

    return maxProfit
}
