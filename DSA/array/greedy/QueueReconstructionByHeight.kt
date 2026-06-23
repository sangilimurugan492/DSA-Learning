package array.greedy

/**
 * https://leetcode.com/problems/queue-reconstruction-by-height/
 *
 * You are given an array of people [h, k] where h is height and k is the number of people
 * in front who have height ≥ h. Reconstruct the queue.
 *
 * Example 1:
 *
 * Input: people = [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]
 * Output: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]
 *
 * Example 2:
 *
 * Input: people = [[6,0],[5,0],[4,0],[3,2],[2,2],[1,4]]
 * Output: [[4,0],[5,0],[2,2],[3,2],[6,0],[1,4]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon — classic greedy insertion)
 *
 * Key Insight: Sort tallest first (by height descending, then k ascending).
 * Insert each person at index k — since all taller people are already placed,
 * inserting at index k guarantees exactly k taller people are in front.
 */
fun main() {
    val result1 = reconstructQueue(arrayOf(intArrayOf(7, 0), intArrayOf(4, 4), intArrayOf(7, 1), intArrayOf(5, 0), intArrayOf(6, 1), intArrayOf(5, 2)))
    result1.forEach { println(it.toList()) }

    println("---")

    val result2 = reconstructQueue(arrayOf(intArrayOf(6, 0), intArrayOf(5, 0), intArrayOf(4, 0), intArrayOf(3, 2), intArrayOf(2, 2), intArrayOf(1, 4)))
    result2.forEach { println(it.toList()) }
}

/**
 * Time Complexity O(N²) — N insertions into list, each O(N)
 * Space Complexity O(N)
 *
 * Approach: Greedy insertion
 *
 * Step 1: Sort by height DESC, then k ASC
 *   [7,0], [7,1], [6,1], [5,0], [5,2], [4,4]
 *
 * Step 2: Insert each at index k
 *   Insert [7,0] at 0 → [[7,0]]
 *   Insert [7,1] at 1 → [[7,0],[7,1]]
 *   Insert [6,1] at 1 → [[7,0],[6,1],[7,1]]
 *   Insert [5,0] at 0 → [[5,0],[7,0],[6,1],[7,1]]
 *   Insert [5,2] at 2 → [[5,0],[7,0],[5,2],[6,1],[7,1]]
 *   Insert [4,4] at 4 → [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]] ✅
 *
 * Why works: When inserting a shorter person, they don't affect k of taller people
 * already placed. The k value tells us exactly where to insert.
 */
fun reconstructQueue(people: Array<IntArray>): Array<IntArray> {
    // Sort: tallest first, then by k ascending
    people.sortWith(compareBy({ -it[0] }, { it[1] }))

    val result = mutableListOf<IntArray>()
    for (person in people) {
        result.add(person[1], person)  // Insert at index k
    }

    return result.toTypedArray()
}
