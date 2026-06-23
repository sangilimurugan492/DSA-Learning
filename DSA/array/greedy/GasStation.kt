package array.greedy

/**
 * https://leetcode.com/problems/gas-station/
 * Given gas[] and cost[], return starting station index to complete circuit, or -1.
 * Example: gas=[1,2,3,4,5], cost=[3,4,5,1,2] → Output: 3
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(canCompleteCircuitBruteForce(intArrayOf(1,2,3,4,5), intArrayOf(3,4,5,1,2)))
    println("---")
    println(canCompleteCircuitGreedy(intArrayOf(1,2,3,4,5), intArrayOf(3,4,5,1,2)))
}

/** BRUTE FORCE: O(N²) — try every starting station */
fun canCompleteCircuitBruteForce(gas: IntArray, cost: IntArray): Int {
    val n = gas.size
    for (start in 0 until n) {
        var tank = 0
        var canComplete = true
        for (i in 0 until n) {
            val idx = (start + i) % n
            tank += gas[idx] - cost[idx]
            if (tank < 0) { canComplete = false; break }
        }
        if (canComplete) return start
    }
    return -1
}

/** OPTIMAL: O(N) Greedy — if total >= 0, solution exists. Skip invalid starts. */
fun canCompleteCircuitGreedy(gas: IntArray, cost: IntArray): Int {
    var totalTank = 0
    var currentTank = 0
    var startStation = 0
    for (i in gas.indices) {
        totalTank += gas[i] - cost[i]
        currentTank += gas[i] - cost[i]
        if (currentTank < 0) {
            startStation = i + 1
            currentTank = 0
        }
    }
    return if (totalTank >= 0) startStation else -1
}
