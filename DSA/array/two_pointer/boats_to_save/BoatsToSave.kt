package array.two_pointer.boats_to_save

/**
 * https://leetcode.com/problems/boats-to-save-people/description/
 * You are given an array people where people[i] is the weight of the ith person, and an infinite number of boats
 * where each boat can carry a maximum weight of limit.
 * Each boat carries at most two people at the same time, provided the sum of the weight of those people is at most limit.
 *
 * Return the minimum number of boats to carry every given person.
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
    println(numRescueBoatsBF(intArrayOf(1, 2), 3))         // 1
    println(numRescueBoatsBF(intArrayOf(3, 2, 2, 1), 3))   // 3
    println(numRescueBoatsBF(intArrayOf(3, 5, 3, 4), 5))   // 4
    println("---")
    println(numRescueBoats(intArrayOf(1, 2), 3))           // 1
    println(numRescueBoats(intArrayOf(3, 2, 2, 1), 3))     // 3
    println(numRescueBoats(intArrayOf(3, 5, 3, 4), 5))     // 4
}

/**
 * Brute force approach: Try all possible pairings using recursion.
 *
 * For each unboarded person, we have two choices:
 * 1. Send them alone in a boat.
 * 2. Pair them with every other unboarded person (if combined weight <= limit).
 *
 * We explore all possibilities and return the minimum boat count.
 *
 * Time Complexity:  O(n * 2^n) — exponential, each person can be paired or go alone
 * Space Complexity: O(n)      — recursion stack + visited array
 */
fun numRescueBoatsBF(people: IntArray, limit: Int): Int {
    val boarded = BooleanArray(people.size)
    return numRescueBoatsBFHelper(people, limit, boarded, people.size)
}

private fun numRescueBoatsBFHelper(
    people: IntArray,
    limit: Int,
    boarded: BooleanArray,
    remaining: Int
): Int {
    // Base case: everyone has been boarded
    if (remaining == 0) return 0

    // Find the first unboarded person
    var firstUnboarded = 0
    while (firstUnboarded < people.size && boarded[firstUnboarded]) firstUnboarded++

    var minBoats = Int.MAX_VALUE

    // Option 1: Send this person alone
    boarded[firstUnboarded] = true
    minBoats = 1 + numRescueBoatsBFHelper(people, limit, boarded, remaining - 1)
    boarded[firstUnboarded] = false

    // Option 2: Pair this person with every other unboarded person
    for (j in firstUnboarded + 1 until people.size) {
        if (!boarded[j] && people[firstUnboarded] + people[j] <= limit) {
            boarded[firstUnboarded] = true
            boarded[j] = true
            val boats = 1 + numRescueBoatsBFHelper(people, limit, boarded, remaining - 2)
            if (boats < minBoats) minBoats = boats
            boarded[firstUnboarded] = false
            boarded[j] = false
        }
    }

    return minBoats
}

/**
 * Two-pointer greedy approach:
 * 1. Sort the array so we can pair the lightest remaining person with the heaviest.
 * 2. Use two pointers: i (lightest) and j (heaviest).
 * 3. If people[i] + people[j] <= limit, they share a boat (move both pointers).
 *    Otherwise, the heaviest person goes alone (move j only).
 * 4. Each iteration represents one boat.
 *
 * Time Complexity:  O(n log n) — dominated by sorting
 * Space Complexity: O(log n)   — sorting overhead (in-place sort)
 */
fun numRescueBoats(people: IntArray, limit: Int): Int {
    people.sort()
    var i = 0
    var j = people.size - 1
    var boats = 0

    while (i <= j) {
        // If the lightest and heaviest can fit together, pair them
        if (people[i] + people[j] <= limit) {
            i++
        }
        // The heaviest person always takes a boat (either paired with 'i' or alone)
        j--
        boats++
    }

    return boats
}
