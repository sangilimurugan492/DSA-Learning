package array.binary_search.koko_eating_bananas

/**
 * Koko Eating Bananas — LeetCode #875
 * https://leetcode.com/problems/koko-eating-bananas/
 *
 * Problem:
 * -------
 * Koko loves to eat bananas. There are n piles of bananas, the i-th pile has piles[i] bananas.
 * The guards have gone away and will come back in h hours. Koko can decide her per-hour eating
 * speed k. Each hour, she chooses some pile and eats k bananas from it. If the pile has less than
 * k bananas, she eats all of them and does not eat any more bananas during that hour.
 * Return the minimum integer k such that she can eat all bananas within h hours.
 *
 * Example:  piles=[3,6,7,11], h=8 → 4
 *           piles=[30,11,23,4,20], h=5 → 30
 *           piles=[30,11,23,4,20], h=6 → 23
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Binary search on answer — asked at Google, Amazon, Meta)
 *
 * Two approaches:
 * 1. Linear Scan: O(max(piles) * n) — try every speed from 1 to max(piles)
 * 2. Binary Search: O(n * log(max(piles))) — binary search on the answer space [1, max(piles)]
 */

fun main() {
    // ─────────────────────────────────────────────────────────────
    // HUGE 10-ELEMENT ARRAY for step-by-step walkthrough
    // piles = [3, 6, 7, 11, 12, 15, 20, 25, 30, 35]
    // h = 10 hours (one pile per hour → answer = max(piles) = 35)
    // h = 15 hours → answer = 12
    // ─────────────────────────────────────────────────────────────
    val hugePiles = intArrayOf(3, 6, 7, 11, 12, 15, 20, 25, 30, 35)

    println("=== Huge 10-Element Array Walkthrough ===")
    println("Piles: ${hugePiles.toList()}")
    println("Max pile: ${hugePiles.max()}\n")

    println("=== Method 1: Linear Scan ===")
    println("minEatingSpeed(hugePiles, h=15) = ${minEatingSpeedLinear(hugePiles, 15)}")

    println("\n=== Method 2: Binary Search (step-by-step) ===")
    println("minEatingSpeed(hugePiles, h=15) = ${minEatingSpeedVerbose(hugePiles, 15)}")

    // ── Additional small test cases ──
    println("\n=== Additional Test Cases ===")
    println("minEatingSpeed([3,6,7,11], h=8) = ${minEatingSpeed(intArrayOf(3, 6, 7, 11), 8)}")
    println("minEatingSpeed([30,11,23,4,20], h=5) = ${minEatingSpeed(intArrayOf(30, 11, 23, 4, 20), 5)}")
    println("minEatingSpeed([30,11,23,4,20], h=6) = ${minEatingSpeed(intArrayOf(30, 11, 23, 4, 20), 6)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: LINEAR SCAN — O(max(piles) * n)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * LINEAR SCAN — Try every speed from 1 to max(piles), return first that works.
 *
 * Core Idea:
 *   - The minimum speed is at least 1, at most max(piles) (eat largest pile in 1 hour).
 *   - Try each speed k from 1 upward. The first k where total hours <= h is the answer.
 *
 * Time Complexity:  O(max(piles) * n) — for each speed, scan all piles.
 * Space Complexity: O(1) — constant variables.
 */
fun minEatingSpeedLinear(piles: IntArray, h: Int): Int {
    val maxPile = piles.max()

    for (k in 1..maxPile) {
        if (canFinish(piles, k, h)) return k
    }
    return maxPile
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: BINARY SEARCH ON ANSWER — O(n * log(max(piles)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH ON ANSWER — Binary search on the speed range [1, max(piles)].
 *
 * Core Idea:
 *   - The answer lies in [1, max(piles)].
 *   - If speed k works (can finish in h hours), try smaller k (left half).
 *   - If speed k doesn't work, try larger k (right half).
 *   - This is "binary search on answer space" — a key pattern.
 *
 * Key Insight:
 *   - The speed-to-hours relationship is MONOTONIC: higher speed → fewer hours.
 *   - If speed k works, all speeds > k also work. We want the MINIMUM working speed.
 *   - So we binary search for the boundary: smallest k that works.
 *
 * Trace for piles=[3,6,7,11,12,15,20,25,30,35], h=15:
 *   left=1, right=35, mid=18 → hours=14 ≤ 15 → works → right=18
 *   left=1, right=18, mid=9  → hours=23 > 15 → no → left=10
 *   left=10, right=18, mid=14 → hours=17 > 15 → no → left=15
 *   left=15, right=18, mid=16 → hours=15 ≤ 15 → works → right=16
 *   left=15, right=16, mid=15 → hours=15 ≤ 15 → works → right=15
 *   left=15, right=15 → return 15 ✅
 *
 * Verification: speed=15 → hours=1+1+1+1+1+1+2+2+2+3=15 ≤ 15. Works!
 *               speed=14 → hours=1+1+1+1+1+2+2+2+3+3=17 > 15. Doesn't work!
 * So the minimum speed is 15.

 *
 * Time Complexity:  O(n * log(max(piles))) — binary search with O(n) check per step.
 * Space Complexity: O(1) — constant variables.
 */
fun minEatingSpeed(piles: IntArray, h: Int): Int {
    var left = 1
    var right = piles.max()

    while (left < right) {
        val mid = left + (right - left) / 2

        if (canFinish(piles, mid, h)) {
            // mid works → try smaller speed
            right = mid
        } else {
            // mid doesn't work → need faster speed
            left = mid + 1
        }
    }

    return left
}

/**
 * Helper: Can Koko finish all piles within h hours at speed k?
 * Hours for a pile = ceil(pile / k) = (pile + k - 1) / k
 */
fun canFinish(piles: IntArray, k: Int, h: Int): Boolean {
    var hours = 0L
    for (pile in piles) {
        hours += (pile + k - 1) / k  // ceiling division
    }
    return hours <= h
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2b: BINARY SEARCH WITH VERBOSE STEP-BY-STEP OUTPUT — O(n * log(max(piles)))
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BINARY SEARCH (VERBOSE) — Same logic as minEatingSpeed but prints every step.
 *
 * ── Step-by-step explanation for the 10-element array ──
 *   piles = [3, 6, 7, 11, 12, 15, 20, 25, 30, 35], h=15
 *   Search range: [1, 35]
 *
 * STEP 1: left=1, right=35, mid=18
 *         hours = ceil(3/18)+ceil(6/18)+...+ceil(35/18) = 1+1+1+1+1+1+2+2+2+2 = 14
 *         14 ≤ 15 → works! → try smaller → right=18
 *
 * STEP 2: left=1, right=18, mid=9
 *         hours = 1+1+1+2+2+2+3+3+4+4 = 23
 *         23 > 15 → doesn't work → need faster → left=10
 *
 * STEP 3: left=10, right=18, mid=14
 *         hours = 1+1+1+1+1+2+2+2+3+3 = 17
 *         17 > 15 → doesn't work → need faster → left=15
 *
 * STEP 4: left=15, right=18, mid=16
 *         hours = 1+1+1+1+1+1+2+2+2+3 = 15
 *         15 ≤ 15 → works! → try smaller → right=16
 *
 * STEP 5: left=15, right=16, mid=15
 *         hours = 1+1+1+1+1+1+2+2+2+3 = 15
 *         15 ≤ 15 → works! → try smaller → right=15
 *
 * STEP 6: left=15, right=15 → left == right → return 15 ✅
 *
 * Only 5 iterations — that's O(log(max(piles))) in action!
 *
 * Time Complexity:  O(n * log(max(piles))) — binary search with O(n) check per step.
 * Space Complexity: O(1) — constant variables.
 */
fun minEatingSpeedVerbose(piles: IntArray, h: Int): Int {
    var left = 1
    var right = piles.max()
    var step = 1

    println("  Piles: ${piles.toList()}")
    println("  h (hours available): $h")
    println("  Search range: [1, $right]")
    println("  ──────────────────────────────────────────────")

    while (left < right) {
        val mid = left + (right - left) / 2

        // Calculate hours needed at speed mid
        var hours = 0L
        val perPile = StringBuilder()
        for (pile in piles) {
            val hrs = (pile + mid - 1) / mid
            hours += hrs
            if (perPile.isNotEmpty()) perPile.append("+")
            perPile.append("ceil($pile/$mid)=$hrs")
        }

        println("  STEP $step: left=$left, right=$right, mid=$mid (speed)")
        println("         hours = $perPile = $hours")

        if (hours <= h) {
            println("         $hours ≤ $h → works! → try smaller speed → right = $mid")
            right = mid
        } else {
            println("         $hours > $h → too slow! → need faster → left = ${mid + 1}")
            left = mid + 1
        }
        println()
        step++
    }

    println("  ──────────────────────────────────────────────")
    println("  left == right == $left → loop ends")
    println("  ✅ Minimum eating speed = $left bananas/hour")
    return left
}
