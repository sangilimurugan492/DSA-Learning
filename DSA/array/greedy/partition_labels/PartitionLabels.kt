package array.greedy.partition_labels

/**
 * Partition Labels — LeetCode #763
 * https://leetcode.com/problems/partition-labels/
 *
 * Problem:
 * -------
 * Partition a string into as many parts as possible so each letter appears in at most one part.
 * Return a list of partition sizes.
 *
 * Example:  "ababcbacadefegdehijhklij" → [9,7,8]
 *           "eccbbbbdec" → [10]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Two approaches:
 * 1. Brute Force: O(N²) — for each partition, expand until all chars are contained
 * 2. Greedy with Last Index: O(N) — record last occurrence, extend partition to max last
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println("partitionLabels(\"ababcbacadefegdehijhklij\") = ${partitionLabelsBrute("ababcbacadefegdehijhklij")}")

    println("\n=== Method 2: Greedy with Last Index ===")
    println("partitionLabels(\"ababcbacadefegdehijhklij\") = ${partitionLabels("ababcbacadefegdehijhklij")}")
    println("partitionLabels(\"eccbbbbdec\") = ${partitionLabels("eccbbbbdec")}")
    println("partitionLabels(\"abc\") = ${partitionLabels("abc")}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each partition, keep expanding until all characters in it
 * don't appear later in the string.
 *
 * Time Complexity:  O(N²) — for each char, scan rest of string.
 * Space Complexity: O(1).
 */
fun partitionLabelsBrute(s: String): List<Int> {
    val result = mutableListOf<Int>()
    var start = 0

    while (start < s.length) {
        var end = start
        var i = start
        while (i <= end) {
            val lastOccur = s.lastIndexOf(s[i])
            end = maxOf(end, lastOccur)
            i++
        }
        result.add(end - start + 1)
        start = end + 1
    }
    return result
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: GREEDY WITH LAST INDEX — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * GREEDY — First pass: record last occurrence of each character.
 * Second pass: extend partition end to max last occurrence. Close when i == end.
 *
 * Core Idea:
 *   - A partition must contain all occurrences of every character in it.
 *   - So the partition end = max(last occurrence of any char seen so far).
 *   - When current index reaches end → close the partition.
 *
 * Time Complexity:  O(N) — two passes.
 * Space Complexity: O(1) — only 26 letters.
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
