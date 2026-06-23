package array.greedy

/**
 * https://leetcode.com/problems/partition-labels/
 *
 * You are given a string s. Partition the string into as many parts as possible so that
 * each letter appears in at most one part. Return a list of integers representing the size
 * of each partition.
 *
 * Example 1:
 *
 * Input: s = "ababcbacadefegdehijhklij"
 * Output: [9,7,8]
 * Explanation: "ababcbaca" | "defegde" | "hijhklij"
 *
 * Example 2:
 *
 * Input: s = "eccbbbbdec"
 * Output: [10]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: First pass — record the last occurrence of each character.
 * Second pass — extend the partition end to the last occurrence of any character seen so far.
 * When current index reaches the partition end → close the partition.
 */
fun main() {
    println(partitionLabels("ababcbacadefegdehijhklij"))
    println(partitionLabels("eccbbbbdec"))
    println(partitionLabels("abc"))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1) — only 26 letters
 */
fun partitionLabels(s: String): List<Int> {
    // Step 1: Record last occurrence of each character
    val lastIndex = IntArray(26)
    for (i in s.indices) {
        lastIndex[s[i] - 'a'] = i
    }

    // Step 2: Greedy partition
    val result = mutableListOf<Int>()
    var start = 0
    var end = 0

    for (i in s.indices) {
        end = maxOf(end, lastIndex[s[i] - 'a'])  // Extend partition
        if (i == end) {                           // Close partition
            result.add(end - start + 1)
            start = i + 1
        }
    }

    return result
}
