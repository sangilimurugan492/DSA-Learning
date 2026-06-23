package stack

/**
 * https://leetcode.com/problems/car-fleet/
 * Given target position and cars' position/speed, return number of car fleets.
 * A fleet forms when a faster car catches a slower car before target.
 * Example: target=12, position=[10,8,0,5,3], speed=[2,4,1,1,3] → 3
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 hardest stack)
 */

fun main() {
    println(carFleetBruteForce(12, intArrayOf(10, 8, 0, 5, 3), intArrayOf(2, 4, 1, 1, 3)))
    println(carFleetBruteForce(10, intArrayOf(3), intArrayOf(3)))
    println("---")
    println(carFleetStack(12, intArrayOf(10, 8, 0, 5, 3), intArrayOf(2, 4, 1, 1, 3)))
    println(carFleetStack(10, intArrayOf(3), intArrayOf(3)))
}

/**
 * BRUTE FORCE: O(N²) — simulate each car's time to target, check merges
 * Sort by position. If car behind reaches target <= car ahead, they merge.
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

/**
 * OPTIMAL: O(N log N) — Sort + Monotonic Stack
 * Sort by position descending. Calculate time to target.
 * If current car's time <= top of stack, it merges (don't push).
 * If current car's time > top, it forms a new fleet (push).
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
