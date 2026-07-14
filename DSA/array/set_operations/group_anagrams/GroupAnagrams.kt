package array.set_operations.group_anagrams

/**
 * https://leetcode.com/problems/group-anagrams/
 *
 * Given an array of strings, group the anagrams together.
 *
 * Example 1:
 * Input: strs = ["eat","tea","tan","ate","nat","bat"]
 * Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 *
 * Example 2:
 * Input: strs = [""] → Output: [[""]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked)
 */

fun main() {
    println(groupAnagramsBruteForce(arrayOf("eat", "tea", "tan", "ate", "nat", "bat")))
    println("---")
    println(groupAnagramsSortedKey(arrayOf("eat", "tea", "tan", "ate", "nat", "bat")))
    println("---")
    println(groupAnagramsCountKey(arrayOf("eat", "tea", "tan", "ate", "nat", "bat")))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N² × K) — compare each string with every other
 * Space Complexity: O(N × K) — store groups
 *
 * For each string, compare with all others by sorting and checking equality.
 * Mark visited to avoid duplicates.
 */
fun groupAnagramsBruteForce(strs: Array<String>): List<List<String>> {
    val visited = BooleanArray(strs.size)
    val result = mutableListOf<List<String>>()

    for (i in strs.indices) {
        if (visited[i]) continue
        val group = mutableListOf(strs[i])
        visited[i] = true
        val sortedI = strs[i].toCharArray().sorted().joinToString("")
        for (j in i + 1 until strs.size) {
            if (!visited[j] && sortedI == strs[j].toCharArray().sorted().joinToString("")) {
                group.add(strs[j])
                visited[j] = true
            }
        }
        result.add(group)
    }
    return result
}

/**
 * BETTER — Sorted String as Key
 * Time Complexity: O(N × K log K) — sort each string of length K
 * Space Complexity: O(N × K)
 *
 * Sort each string → use as HashMap key. All anagrams produce same sorted string.
 */
fun groupAnagramsSortedKey(strs: Array<String>): List<List<String>> {
    val map = hashMapOf<String, MutableList<String>>()
    for (str in strs) {
        val key = str.toCharArray().sorted().joinToString("")
        map.getOrPut(key) { mutableListOf() }.add(str)
    }
    return map.values.toList()
}

/**
 * OPTIMAL — Character Count as Key
 * Time Complexity: O(N × K) — count characters instead of sorting
 * Space Complexity: O(N × K)
 *
 * Use character frequency count as key (e.g., "a1b2c3" format).
 * Avoids sorting overhead — O(K) instead of O(K log K) per string.
 */
fun groupAnagramsCountKey(strs: Array<String>): List<List<String>> {
    val map = hashMapOf<String, MutableList<String>>()
    for (str in strs) {
        val count = IntArray(26)
        for (c in str) count[c - 'a']++
        val key = count.joinToString("#")  // e.g., "1#0#0#0#1#..."
        map.getOrPut(key) { mutableListOf() }.add(str)
    }
    return map.values.toList()
}
