package array.two_pointer

/**
 * https://leetcode.com/problems/boats-to-save-people/description/
 * You are given an array people where people[i] is the weight of the ith person, and an infinite number of boats where each boat can carry a maximum weight of limit.
 * Each boat carries at most two people at the same time, provided the sum of the weight of those people is at most limit.
 *
 * Return the minimum number of boats to carry every given person.
 *
 *
 *
 * Example 1:
 *
 * Input: people = [1,2], limit = 3
 * Output: 1
 * Explanation: 1 boat (1, 2)
 * Example 2:
 *
 * Input: people = [3,2,2,1], limit = 3
 * Output: 3
 * Explanation: 3 boats (1, 2), (2) and (3)
 * Example 3:
 *
 * Input: people = [3,5,3,4], limit = 5
 * Output: 4
 * Explanation: 4 boats (3), (3), (4), (5)
 */
fun main() {
    println(numRescueBoatsBF(intArrayOf(3, 2, 2, 1), 3))
}

fun numRescueBoatsBF(people: IntArray, limit: Int): Int {

    people.sort() // O(N log N)
    var i = 0
    var j = people.size - 1
    var boats = 0

    while (i <= j) {
        // If the lightest and heaviest can fit together
        if (people[i] + people[j] <= limit) {
            i++
        }
        // The heaviest person always takes a boat (either with 'i' or alone)
        j--
        boats++
    }

    return boats
}