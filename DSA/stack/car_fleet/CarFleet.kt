package stack.car_fleet

/**
 * Car Fleet — LeetCode #853
 * https://leetcode.com/problems/car-fleet/
 *
 * Problem:
 * -------
 * Given target position and cars' position/speed, return number of car fleets.
 * A fleet forms when a faster car catches a slower car before target.
 *
 * Example:  target=12, position=[10,8,0,5,3], speed=[2,4,1,1,3] → 3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 hardest stack)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — simulate each car's time, check merges
 * 2. Monotonic Stack: O(N log N) — sort by position, track fleet times
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("carFleet(12, [10,8,0,5,3], [2,4,1,1,3]) = ${carFleetBruteForce(12, intArrayOf(10, 8, 0, 5, 3), intArrayOf(2, 4, 1, 1, 3))}")

    println("\n=== Method 2: Monotonic Stack ===")
    println("carFleet(12, [10,8,0,5,3], [2,4,1,1,3]) = ${carFleetStack(12, intArrayOf(10, 8, 0, 5, 3), intArrayOf(2, 4, 1, 1, 3))}")
    println("carFleet(10, [3], [3]) = ${carFleetStack(10, intArrayOf(3), intArrayOf(3))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Sort by position. If car behind reaches target ≤ car ahead, they merge.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N).
 */
fun carFleetBruteForce(target: Int, position: IntArray, speed: IntArray): Int {
    val n = position.size
    val cars = (0 until n).map { Pair(position[it], speed[it]) }.sortedByDescending { it.first }
    val times = cars.map { (target - it.first).toDouble() / it.second }

    var fleets = 0
    val merged = BooleanArray(n)
    for (i in 0 until n) {
        if (merged[i]) continue
        fleets++
        for (j in i + 1 until n) {
            if (times[j] <= times[i]) merged[j] = true
            else break
        }
    }
    return fleets
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Sort by position descending. Calculate time to target.
 * If current time ≤ top of stack → merges (don't push). If > top → new fleet (push).
 *
 * Core Idea:
 *   - Cars closer to target are processed first (sorted by position DESC).
 *   - If a car behind takes ≤ time as the car ahead, it catches up → merges.
 *   - If it takes more time → it can't catch up → new fleet.
 *
 * Key Insight:
 *   - Time to target = (target - position) / speed.
 *   - A car fleet is a group of cars arriving at the same time.
 *   - Stack stores arrival times. If current ≤ top → merge. If > top → new fleet.
 *
 * Time Complexity:  O(N log N) — sort.
 * Space Complexity: O(N) — stack.
 */
fun carFleetStack(target: Int, position: IntArray, speed: IntArray): Int {
    val cars = position.indices.map { Pair(position[it], speed[it]) }.sortedByDescending { it.first }
    val stack = ArrayDeque<Double>()

    for ((pos, spd) in cars) {
        val time = (target - pos).toDouble() / spd
        if (stack.isEmpty() || time > stack.last()) {
            stack.addLast(time)
        }
        // else: current car merges with fleet ahead, don't push
    }
    return stack.size
}
