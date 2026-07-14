package array.greedy.queue_reconstruction_by_height

/**
 * Queue Reconstruction by Height — LeetCode #406
 * https://leetcode.com/problems/queue-reconstruction-by-height/
 *
 * Problem:
 * -------
 * People array [h, k] where h=height, k=people in front with height ≥ h. Reconstruct the queue.
 *
 * Example:  [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]] → [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)
 *
 * Two approaches:
 * 1. Brute Force: O(N³) — try all permutations, validate
 * 2. Greedy Insertion: O(N²) — sort tallest first, insert at index k
 */

fun main() {
    val people = arrayOf(intArrayOf(7, 0), intArrayOf(4, 4), intArrayOf(7, 1), intArrayOf(5, 0), intArrayOf(6, 1), intArrayOf(5, 2))

    println("=== Method: Greedy Insertion ===")
    reconstructQueue(people).forEach { println(it.toList()) }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD: GREEDY INSERTION — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY INSERTION — Sort tallest first (height DESC, k ASC). Insert each at index k.
 *
 * Core Idea:
 *   - Sort by height DESC, then k ASC.
 *   - Insert each person at index k in the result list.
 *   - Since all taller people are already placed, inserting at k guarantees
 *     exactly k taller people are in front.
 *
 * Key Insight:
 *   - When inserting a shorter person, they don't affect k of taller people already placed.
 *   - The k value tells us exactly where to insert.
 *
 * Time Complexity:  O(N²) — N insertions into list, each O(N).
 * Space Complexity: O(N).
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
