package array.greedy.gas_station

/**
 * Gas Station — LeetCode #134
 * https://leetcode.com/problems/gas-station/
 *
 * Problem:
 * -------
 * There are n gas stations along a circular route. gas[i] is the amount of gas
 * at station i. cost[i] is the gas needed to travel from station i to i+1.
 * Return the starting station index if you can complete the circuit clockwise,
 * otherwise return -1. If a solution exists, it is guaranteed to be unique.
 *
 * Example 1:  gas=[1,2,3,4,5], cost=[3,4,5,1,2]  →  3
 * Example 2:  gas=[2,3,4], cost=[3,4,3]           →  -1
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    val gas1 = intArrayOf(1, 2, 3, 4, 5)
    val cost1 = intArrayOf(3, 4, 5, 1, 2)
    val gas2 = intArrayOf(2, 3, 4)
    val cost2 = intArrayOf(3, 4, 3)

    println("=== Method 1: Brute Force ===")
    println("canCompleteCircuit(gas=[1,2,3,4,5], cost=[3,4,5,1,2]) = ${canCompleteCircuitBruteForce(gas1, cost1)}")
    println("canCompleteCircuit(gas=[2,3,4], cost=[3,4,3]) = ${canCompleteCircuitBruteForce(gas2, cost2)}")

    println("\n=== Method 2: Greedy (Optimal) ===")
    println("canCompleteCircuit(gas=[1,2,3,4,5], cost=[3,4,5,1,2]) = ${canCompleteCircuitGreedy(gas1, cost1)}")
    println("canCompleteCircuit(gas=[2,3,4], cost=[3,4,3]) = ${canCompleteCircuitGreedy(gas2, cost2)}")

    println("\n=== Step-by-step trace (Greedy) ===")
    canCompleteCircuitTrace(gas1, cost1)
    println()
    canCompleteCircuitTrace(gas2, cost2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — Try Every Starting Station
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Try starting from each station and simulate the circuit.
 *
 * Core Idea:
 *   - For each station `start`, simulate a full circuit.
 *   - Track the tank: at each station, add gas[i] - cost[i].
 *   - If tank goes negative at any point, this start fails — try next.
 *   - If we complete the circuit, return `start`.
 *
 * Algorithm:
 *   1. For each station `start` from 0 to n-1:
 *      a. Initialize tank = 0.
 *      b. For each step i from 0 to n-1:
 *         - idx = (start + i) % n  (circular indexing)
 *         - tank += gas[idx] - cost[idx]
 *         - If tank < 0, break (can't complete from this start).
 *      c. If completed all n steps, return `start`.
 *   2. If no start works, return -1.
 *
 * Time Complexity:  O(N²) — try each of N starts, each simulating N steps.
 * Space Complexity: O(1).
 */
fun canCompleteCircuitBruteForce(gas: IntArray, cost: IntArray): Int {
    val n = gas.size

    // Try each station as the starting point.
    for (start in 0 until n) {
        var tank = 0
        var canComplete = true

        // Simulate a full circuit starting from `start`.
        for (i in 0 until n) {
            val idx = (start + i) % n  // Circular indexing
            tank += gas[idx] - cost[idx]
            if (tank < 0) {
                canComplete = false
                break
            }
        }

        // If we completed the circuit, this is the answer.
        if (canComplete) return start
    }

    return -1
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY (OPTIMAL)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — One-pass with two key insights.
 *
 * Core Idea:
 *   1. If total gas >= total cost, a solution MUST exist (guaranteed unique).
 *   2. If we can't reach station j from start i, then no station between i and j
 *      can be a valid start either (because they'd have even less gas).
 *      So we skip to j+1 as the new candidate start.
 *
 * Algorithm:
 *   1. Track totalTank (sum of all gas - cost) and currentTank (running sum).
 *   2. Track startStation (candidate starting index).
 *   3. For each station i:
 *      a. totalTank += gas[i] - cost[i]
 *      b. currentTank += gas[i] - cost[i]
 *      c. If currentTank < 0: reset startStation = i+1, currentTank = 0.
 *   4. If totalTank >= 0, return startStation. Else return -1.
 *
 * Why it works:
 *   - If currentTank goes negative at station i, all stations from the current
 *     start to i are invalid starts (they'd have even less gas).
 *   - So we skip them all and try i+1.
 *   - If total gas >= total cost, the unique solution is startStation.
 *
 * Time Complexity:  O(N) — single pass.
 * Space Complexity: O(1).
 */
fun canCompleteCircuitGreedy(gas: IntArray, cost: IntArray): Int {
    var totalTank = 0       // Total gas - cost across all stations
    var currentTank = 0    // Running tank from current start candidate
    var startStation = 0   // Candidate starting station

    for (i in gas.indices) {
        // Accumulate total surplus/deficit.
        totalTank += gas[i] - cost[i]
        // Accumulate running tank from current start.
        currentTank += gas[i] - cost[i]

        // If we can't reach the next station from current start:
        if (currentTank < 0) {
            // Reset: try starting from the next station.
            startStation = i + 1
            currentTank = 0
        }
    }

    // If total gas >= total cost, solution exists and is startStation.
    return if (totalTank >= 0) startStation else -1
}

/**
 * Greedy with step-by-step trace for learning/debugging.
 */
fun canCompleteCircuitTrace(gas: IntArray, cost: IntArray) {
    println("Input: gas=${gas.toList()}, cost=${cost.toList()}")
    var totalTank = 0
    var currentTank = 0
    var startStation = 0

    for (i in gas.indices) {
        val diff = gas[i] - cost[i]
        totalTank += diff
        currentTank += diff
        if (currentTank < 0) {
            println("  Station $i | gas=${gas[i]} cost=${cost[i]} diff=$diff | currentTank=$currentTank < 0 → RESET start to ${i + 1}")
            startStation = i + 1
            currentTank = 0
        } else {
            println("  Station $i | gas=${gas[i]} cost=${cost[i]} diff=$diff | currentTank=$currentTank | start=$startStation")
        }
    }
    val result = if (totalTank >= 0) startStation else -1
    println("  totalTank=$totalTank | Result: $result")
}
