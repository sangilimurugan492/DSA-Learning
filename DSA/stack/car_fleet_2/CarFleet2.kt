package stack.car_fleet_2

/**
 * Car Fleet II — LeetCode #1776
 * https://leetcode.com/problems/car-fleet-ii/
 *
 * Problem:
 * -------
 * N cars on a one-lane road, each with position[i] and speed[i].
 * Cars cannot pass; a faster car catches a slower car ahead and they
 * merge into a fleet (same position & speed). Return an array where
 * answer[i] = time car i collides with the car in front, or -1 if never.
 *
 * Example:  cars = [[1,2],[2,4],[4,1],[7,4]] → [3.0, 0.6667, -1.0, -1.0]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard, monotonic stack)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each car, scan all cars ahead
 * 2. Monotonic Stack: O(N) — process right-to-left, pop fleets that merge before catch-up
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    val result1 = getCollisionTimesBruteForce(arrayOf(intArrayOf(1, 2), intArrayOf(2, 4), intArrayOf(4, 1), intArrayOf(7, 4)))
    println("getCollisionTimes([[1,2],[2,4],[4,1],[7,4]]) = ${result1.toList()}")

    println("\n=== Method 2: Monotonic Stack ===")
    val result2 = getCollisionTimesStack(arrayOf(intArrayOf(1, 2), intArrayOf(2, 4), intArrayOf(4, 1), intArrayOf(7, 4)))
    println("getCollisionTimes([[1,2],[2,4],[4,1],[7,4]]) = ${result2.toList()}")

    println("getCollisionTimes([[3,4],[5,4],[6,3],[9,1]]) = ${getCollisionTimesStack(arrayOf(intArrayOf(3, 4), intArrayOf(5, 4), intArrayOf(6, 3), intArrayOf(9, 1))).toList()}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Process right-to-left. For each car i, scan all cars j ahead.
 * If car i is faster, compute collision time. If car j hasn't merged before
 * that time (res[j] == -1 or collisionTime ≤ res[j]), that's the answer for i.
 *
 * Time Complexity:  O(N²).
 * Space Complexity: O(N).
 */
fun getCollisionTimesBruteForce(cars: Array<IntArray>): DoubleArray {
    val n = cars.size
    val res = DoubleArray(n) { -1.0 }

    for (i in n - 1 downTo 0) {
        for (j in i + 1 until n) {
            // Can't catch if same speed or slower
            if (cars[i][1] <= cars[j][1]) continue

            val collisionTime = (cars[j][0] - cars[i][0]).toDouble() / (cars[i][1] - cars[j][1])

            // If car j never collides, or we catch j before j merges with its front
            if (res[j] < 0 || collisionTime <= res[j]) {
                res[i] = collisionTime
                break
            }
        }
    }
    return res
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MONOTONIC STACK — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MONOTONIC STACK — Process right-to-left (front to back). Stack stores
 * [position, speed, collisionTime] for cars ahead. For each car:
 *   1. If current speed ≤ top speed → can never catch → pop (check next fleet).
 *   2. Compute collision time with top. If top never merges (time=-1) or
 *      we catch top before top merges → record collision, stop.
 *   3. If top merges before we catch it → pop (top is now part of a fleet,
 *      its speed changed) → check next car on stack.
 *
 * Core Idea:
 *   - A car ahead that merges into a fleet before we catch it is no longer
 *     at its original speed — we must look past it.
 *   - Stack maintains only "relevant" cars ahead that could be caught.
 *
 * Key Insight:
 *   - Collision time = (posAhead - posBehind) / (speedBehind - speedAhead).
 *   - If collisionTime ≤ top's collisionTime → we catch top before it merges.
 *   - If collisionTime > top's collisionTime → top merges first → pop & retry.
 *
 * Time Complexity:  O(N) — each car pushed/popped at most once.
 * Space Complexity: O(N) — stack.
 */
fun getCollisionTimesStack(cars: Array<IntArray>): DoubleArray {
    val n = cars.size
    val res = DoubleArray(n) { -1.0 }
    // Stack entries: [position, speed, collisionTime]  (collisionTime = -1 if never)
    val stack = ArrayDeque<IntArray>()

    for (i in n - 1 downTo 0) {
        val pos = cars[i][0]
        val spd = cars[i][1]

        while (stack.isNotEmpty()) {
            val top = stack.last()
            val topPos = top[0]
            val topSpd = top[1]
            val topTime = top[2]

            // Can't catch top (same speed or slower) → pop, check next fleet
            if (spd <= topSpd) {
                stack.removeLast()
                continue
            }

            // Compute collision time with top
            val collisionTime = (topPos - pos).toDouble() / (spd - topSpd)

            // If top never merges, or we catch top before it merges with its front
            if (topTime < 0 || collisionTime <= topTime) {
                res[i] = collisionTime
                break
            }

            // Top merges before we catch it → pop, check next car on stack
            stack.removeLast()
        }

        // Push current car with its collision time (or -1 if none)
        stack.addLast(intArrayOf(pos, spd, (if (res[i] > 0) res[i] else -1.0).toInt()))
    }

    return res
}
