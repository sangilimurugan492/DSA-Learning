package array.greedy.lemonade_change

/**
 * Lemonade Change — LeetCode #860
 * https://leetcode.com/problems/lemonade-change/
 *
 * Problem:
 * -------
 * Each lemonade costs $5. Customers pay with $5, $10, or $20 bills.
 * Return true if you can provide correct change to every customer.
 *
 * Example:  [5,5,5,10,20] → true
 *           [5,5,10,10,20] → false
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Two approaches:
 * 1. Simulation with HashMap: O(N) — track bill counts in a map
 * 2. Greedy with counters: O(N) — prefer $10+$5 over three $5s for $20
 */

fun main() {
    println("=== Method 1: Simulation with HashMap ===")
    println("lemonadeChange([5,5,5,10,20]) = ${lemonadeChangeMap(intArrayOf(5, 5, 5, 10, 20))}")
    println("lemonadeChange([5,5,10,10,20]) = ${lemonadeChangeMap(intArrayOf(5, 5, 10, 10, 20))}")

    println("\n=== Method 2: Greedy with Counters ===")
    println("lemonadeChange([5,5,5,10,20]) = ${lemonadeChange(intArrayOf(5, 5, 5, 10, 20))}")
    println("lemonadeChange([5,5,10,10,20]) = ${lemonadeChange(intArrayOf(5, 5, 10, 10, 20))}")
    println("lemonadeChange([5,5,10]) = ${lemonadeChange(intArrayOf(5, 5, 10))}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: SIMULATION WITH HASHMAP — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SIMULATION — Track bill counts in a HashMap. For each customer, give change.
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(1) — only 3 bill types.
 */
fun lemonadeChangeMap(bills: IntArray): Boolean {
    val cash = mutableMapOf(5 to 0, 10 to 0, 20 to 0)

    for (bill in bills) {
        cash[bill] = cash[bill]!! + 1
        var change = bill - 5

        // Give change using largest bills first
        for (denom in listOf(20, 10, 5)) {
            while (change >= denom && cash[denom]!! > 0) {
                change -= denom
                cash[denom] = cash[denom]!! - 1
            }
        }
        if (change > 0) return false
    }
    return true
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY WITH COUNTERS — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — Track $5 and $10 counts. For $20, prefer $10+$5 (conserve $5s).
 *
 * Core Idea:
 *   - $5: just collect.
 *   - $10: give one $5 change.
 *   - $20: prefer $10+$5 (greedy — $5s are more valuable). Fallback: three $5s.
 *
 * Key Insight:
 *   - $5 bills are needed for $10 change. So conserve them — prefer $10+$5 for $20.
 *
 * Time Complexity:  O(N).
 * Space Complexity: O(1).
 */
fun lemonadeChange(bills: IntArray): Boolean {
    var five = 0
    var ten = 0

    for (bill in bills) {
        when (bill) {
            5 -> five++
            10 -> {
                if (five == 0) return false
                five--; ten++
            }
            20 -> {
                if (ten > 0 && five > 0) {
                    ten--; five--  // Prefer $10+$5 (greedy: conserve $5s)
                } else if (five >= 3) {
                    five -= 3
                } else {
                    return false
                }
            }
        }
    }
    return true
}
