package array.sorting.relative_ranks

/**
 * https://leetcode.com/problems/relative-ranks/
 *
 * You are given an integer array score of size n, where score[i] is the score of the
 * ith athlete in a competition. All the scores are guaranteed to be unique.
 *
 * The athletes are placed based on their scores, where the 1st place athlete has the
 * highest score, the 2nd place athlete has the 2nd highest score, and so on. The
 * placement of each athlete determines their rank:
 *   - 1st place → "Gold Medal"
 *   - 2nd place → "Silver Medal"
 *   - 3rd place → "Bronze Medal"
 *   - 4th–nth place → the placement number as a string (e.g., "4", "5", ...)
 *
 * Return an array answer of size n where answer[i] is the rank of the ith athlete.
 *
 * Constraints:
 *   n == score.length
 *   1 <= n <= 10^4
 *   0 <= score[i] <= 10^6
 *   All values in score are unique.
 *
 * Example 1:
 *   Input:  score = [5, 4, 3, 2, 1]
 *   Output: ["Gold Medal","Silver Medal","Bronze Medal","4","5"]
 *   Explanation: The placements are [1st, 2nd, 3rd, 4th, 5th].
 *
 * Example 2:
 *   Input:  score = [10, 3, 8, 9, 4]
 *   Output: ["Gold Medal","5","Bronze Medal","Silver Medal","4"]
 *   Explanation: The placements are [1st(10), 2nd(9), 3rd(8), 4th(4), 5th(3)].
 *                score[0]=10 → 1st → "Gold Medal"
 *                score[1]=3  → 5th → "5"
 *                score[2]=8  → 3rd → "Bronze Medal"
 *                score[3]=9  → 2nd → "Silver Medal"
 *                score[4]=4  → 4th → "4"
 */
fun main() {
    println(findRelativeRanksBF(intArrayOf(5, 4, 3, 2, 1)).toList())
    // ["Gold Medal", "Silver Medal", "Bronze Medal", "4", "5"]
    println(findRelativeRanksOP(intArrayOf(10, 3, 8, 9, 4)).toList())
    // ["Gold Medal", "5", "Bronze Medal", "Silver Medal", "4"]
}

/**
 * Brute Force — Sort copy and linear search for each score
 *
 * 1. Sort a copy of score in descending order.
 * 2. For each original score, find its index (rank) in the sorted array.
 * 3. Map rank → medal string.
 *
 * Time Complexity:  O(N²)  — O(N log N) for sort + O(N) search per element
 * Space Complexity: O(N)    — for the sorted copy and result
 */
fun findRelativeRanksBF(score: IntArray): Array<String> {
    val sorted = score.sortedArrayDescending() // sorted copy

    val result = Array<String>(score.size) { "" }
    for (i in score.indices) {
        val rank = sorted.indexOf(score[i]) // O(N) linear search
        result[i] = rankToMedal(rank)
    }
    return result
}

/**
 * Optimal — HashMap + Sort
 *
 * Key insight: After sorting, we know each score's rank. We can store a
 * score → rank mapping in a HashMap for O(1) lookups, then fill the result
 * in the original order.
 *
 * Steps:
 * 1. Sort a copy of `score` in descending order.
 * 2. Build a HashMap: score → rank (0-indexed position in sorted array).
 * 3. For each original score[i], look up its rank and convert to medal string.
 *
 * Trace for score = [10, 3, 8, 9, 4]:
 *
 *   Step 1 — Sort descending:
 *     sorted = [10, 9, 8, 4, 3]
 *
 *   Step 2 — Build map (score → rank):
 *     10 → 0 (1st), 9 → 1 (2nd), 8 → 2 (3rd), 4 → 3 (4th), 3 → 4 (5th)
 *
 *   Step 3 — Fill result in original order:
 *     score[0]=10 → rank 0 → "Gold Medal"
 *     score[1]=3  → rank 4 → "5"
 *     score[2]=8  → rank 2 → "Bronze Medal"
 *     score[3]=9  → rank 1 → "Silver Medal"
 *     score[4]=4  → rank 3 → "4"
 *   Result = ["Gold Medal","5","Bronze Medal","Silver Medal","4"] ✅
 *
 * Time Complexity:  O(N log N) — dominated by sorting
 * Space Complexity: O(N)       — for sorted copy + HashMap
 */
fun findRelativeRanksOP(score: IntArray): Array<String> {
    // Step 1: Sort descending — highest score gets rank 0 (1st place).
    val sorted = score.sortedArrayDescending()

    // Step 2: Map each score to its rank (0-indexed).
    val rankMap = HashMap<Int, Int>()
    for (rank in sorted.indices) {
        rankMap[sorted[rank]] = rank
    }

    // Step 3: Build result in original order using O(1) lookups.
    val result = Array<String>(score.size) { "" }
    for (i in score.indices) {
        result[i] = rankToMedal(rankMap[score[i]]!!)
    }
    return result
}

/**
 * Converts a 0-indexed rank to its medal/placement string.
 *   0 → "Gold Medal"
 *   1 → "Silver Medal"
 *   2 → "Bronze Medal"
 *   3+ → (rank + 1) as a string, e.g. "4", "5", ...
 */
private fun rankToMedal(rank: Int): String = when (rank) {
    0 -> "Gold Medal"
    1 -> "Silver Medal"
    2 -> "Bronze Medal"
    else -> (rank + 1).toString()
}
