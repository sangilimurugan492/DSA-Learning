package array.greedy

/**
 * https://leetcode.com/problems/candy/
 *
 * There are n children standing in a line. Each child is assigned a rating value given in
 * the integer array ratings. You are giving candies to these children subjected to:
 * - Each child must have at least one candy.
 * - Children with a higher rating get more candies than their neighbors.
 * Return the minimum number of candies you need to have.
 *
 * Example 1:
 *
 * Input: ratings = [1,0,2]
 * Output: 5
 * Explanation: [2,1,2] → 2+1+2 = 5
 *
 * Example 2:
 *
 * Input: ratings = [1,2,2]
 * Output: 4
 * Explanation: [1,2,1] → 1+2+1 = 4 (equal ratings don't require more candies)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Amazon, Google, Meta — hard greedy)
 *
 * Key Insight: Two-pass greedy. Left→Right ensures right neighbor rule, Right→Left ensures
 * left neighbor rule. Take max of both passes.
 */
fun main() {
    println(candy(intArrayOf(1, 0, 2)))
    println(candy(intArrayOf(1, 2, 2)))
    println(candy(intArrayOf(1, 3, 2, 2, 1)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N)
 *
 * Approach: Two-pass greedy
 *
 * Pass 1 (Left → Right): If ratings[i] > ratings[i-1], candies[i] = candies[i-1] + 1
 *   This ensures: child with higher rating than LEFT neighbor gets more candy
 *
 * Pass 2 (Right → Left): If ratings[i] > ratings[i+1], candies[i] = max(candies[i], candies[i+1] + 1)
 *   This ensures: child with higher rating than RIGHT neighbor gets more candy
 *
 * Trace for ratings = [1,0,2]:
 * Initial:    [1,1,1]
 * Left→Right: [1,1,2]  (2>0, so candies[2]=candies[1]+1=2)
 * Right→Left: [2,1,2]  (1>0, so candies[0]=max(1,1+1)=2)
 * Total: 2+1+2 = 5 ✅
 */
fun candy(ratings: IntArray): Int {
    val n = ratings.size
    val candies = IntArray(n) { 1 }

    // Pass 1: Left to Right
    for (i in 1 until n) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1
        }
    }

    // Pass 2: Right to Left
    for (i in n - 2 downTo 0) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = maxOf(candies[i], candies[i + 1] + 1)
        }
    }

    return candies.sum()
}
