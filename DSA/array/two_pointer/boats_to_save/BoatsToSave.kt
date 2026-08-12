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
 * Brute Force: Sort, then greedily pair each person with the heaviest
 * possible partner using a simple scan.
 *
 * For each unboarded person (lightest first), scan from the heaviest
 * unboarded person downward to find a partner that fits within limit.
 * If no partner found, they go alone.
 *
 * Time Complexity:  O(N²) — for each person, scan remaining unboarded people
 * Space Complexity: O(N)  — boarded array
 */
fun numRescueBoatsBF(people: IntArray, limit: Int): Int {
    people.sort()
    val boarded = BooleanArray(people.size)
    var boats = 0

    for (i in people.indices) {
        if (boarded[i]) continue // Already boarded

        // Try to pair person i with the heaviest unboarded person
        var partner = -1
        for (j in people.size - 1 downTo i + 1) {
            if (!boarded[j] && people[i] + people[j] <= limit) {
                partner = j
                break
            }
        }

        // Board person i (and their partner if found)
        boarded[i] = true
        if (partner != -1) {
            boarded[partner] = true
        }
        boats++
    }

    return boats
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
