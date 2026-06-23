package array.greedy

/**
 * https://leetcode.com/problems/lemonade-change/
 *
 * At a lemonade stand, each lemonade costs $5. Customers pay with $5, $10, or $20 bills.
 * You must provide correct change. Return true if you can provide change to every customer.
 *
 * Example 1:
 *
 * Input: bills = [5,5,5,10,20]
 * Output: true
 *
 * Example 2:
 *
 * Input: bills = [5,5,10,10,20]
 * Output: false
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Key Insight: Greedy — always prefer giving a $10+$5 change over three $5s for $20 bills.
 * $5 bills are more valuable (needed for $10 change), so conserve them.
 */
fun main() {
    println(lemonadeChange(intArrayOf(5, 5, 5, 10, 20)))
    println(lemonadeChange(intArrayOf(5, 5, 10, 10, 20)))
    println(lemonadeChange(intArrayOf(5, 5, 10)))
}

fun lemonadeChange(bills: IntArray): Boolean {
    var five = 0
    var ten = 0

    for (bill in bills) {
        when (bill) {
            5 -> five++
            10 -> {
                if (five == 0) return false
                five--
                ten++
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
