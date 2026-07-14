package array.greedy.candy

/**
 * Candy — LeetCode #135
 * https://leetcode.com/problems/candy/
 *
 * Problem:
 * -------
 * There are n children standing in a line. Each child is assigned a rating value.
 * You are giving candies to these children subjected to:
 * - Each child must have at least one candy.
 * - Children with a higher rating get more candies than their neighbors.
 * Return the minimum number of candies you need to have.
 *
 * Example:  ratings = [1,0,2]  →  5  ([2,1,2] → 2+1+2 = 5)
 *           ratings = [1,2,2]  →  4  ([1,2,1] → 1+2+1 = 4)
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Amazon, Google, Meta — hard greedy)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — repeatedly fix until stable
 * 2. Two-Pass Greedy: O(N) — left→right, then right→left
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("candy([1,0,2]) = ${candyBruteForce(intArrayOf(1, 0, 2))}")
    println("candy([1,2,2]) = ${candyBruteForce(intArrayOf(1, 2, 2))}")

    println("\n=== Method 2: Two-Pass Greedy ===")
    println("candy([1,0,2]) = ${candy(intArrayOf(1, 0, 2))}")
    println("candy([1,2,2]) = ${candy(intArrayOf(1, 2, 2))}")

    println("\n=== Step-by-step trace ===")
    candyTrace(intArrayOf(1, 0, 2))
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Start with 1 candy each. Repeatedly scan and fix violations
 * until no more changes needed.
 *
 * Core Idea:
 *   - Give each child 1 candy.
 *   - Repeat until stable: for each child, if rating > neighbor, ensure candy > neighbor's candy.
 *
 * Time Complexity:  O(N²) — may need N passes in worst case.
 * Space Complexity: O(N) — candies array.
 */
fun candyBruteForce(ratings: IntArray): Int {
    val n = ratings.size
    val candies = IntArray(n) { 1 }
    var changed = true

    while (changed) {
        changed = false
        for (i in 0 until n) {
            // Check left neighbor.
            if (i > 0 && ratings[i] > ratings[i - 1] && candies[i] <= candies[i - 1]) {
                candies[i] = candies[i - 1] + 1
                changed = true
            }
            // Check right neighbor.
            if (i < n - 1 && ratings[i] > ratings[i + 1] && candies[i] <= candies[i + 1]) {
                candies[i] = candies[i + 1] + 1
                changed = true
            }
        }
    }
    return candies.sum()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: TWO-PASS GREEDY — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * TWO-PASS GREEDY — Left→Right ensures right neighbor rule, Right→Left ensures left neighbor rule.
 *
 * Core Idea:
 *   - Pass 1 (Left→Right): If ratings[i] > ratings[i-1], candies[i] = candies[i-1] + 1.
 *   - Pass 2 (Right→Left): If ratings[i] > ratings[i+1], candies[i] = max(candies[i], candies[i+1] + 1).
 *   - Take max of both passes to satisfy both neighbor constraints.
 *
 * Key Insight:
 *   - One pass can only satisfy one neighbor constraint at a time.
 *   - Left→Right handles "higher than left neighbor". Right→Left handles "higher than right neighbor".
 *   - Taking max ensures both constraints are satisfied simultaneously.
 *
 * Time Complexity:  O(N) — two linear passes.
 * Space Complexity: O(N) — candies array.
 */
fun candy(ratings: IntArray): Int {
    val n = ratings.size
    val candies = IntArray(n) { 1 }

    // Pass 1: Left to Right — ensure higher rating than LEFT neighbor gets more.
    for (i in 1 until n) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1
        }
    }

    // Pass 2: Right to Left — ensure higher rating than RIGHT neighbor gets more.
    for (i in n - 2 downTo 0) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = maxOf(candies[i], candies[i + 1] + 1)
        }
    }

    return candies.sum()
}

/**
 * Two-pass greedy with step-by-step trace.
 */
fun candyTrace(ratings: IntArray) {
    val n = ratings.size
    val candies = IntArray(n) { 1 }
    println("Input: ${ratings.toList()}")
    println("Initial: ${candies.toList()}")

    // Pass 1: Left to Right
    for (i in 1 until n) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1
        }
    }
    println("After L→R: ${candies.toList()}")

    // Pass 2: Right to Left
    for (i in n - 2 downTo 0) {
        if (ratings[i] > ratings[i + 1]) {
            val old = candies[i]
            candies[i] = maxOf(candies[i], candies[i + 1] + 1)
            println("  i=$i: rating ${ratings[i]} > ${ratings[i+1]} → max($old, ${candies[i+1]}+1) = ${candies[i]}")
        }
    }
    println("After R→L: ${candies.toList()}")
    println("Total: ${candies.sum()}")
}
