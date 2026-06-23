package graph.shortest_path

/**
 * https://leetcode.com/problems/word-ladder/
 * Find the length of shortest transformation sequence from beginWord to endWord.
 * Each step can change only one letter, and the new word must be in wordList.
 * FAANG Importance: ⭐⭐⭐⭐⭐ (BFS shortest path on implicit graph)
 */

fun main() {
    println(ladderLength("hit", "cog", listOf("hot", "dot", "dog", "lot", "log", "cog")))  // 5
    println(ladderLength("hit", "cog", listOf("hot", "dot", "dog", "lot", "log")))  // 0
}

/**
 * BFS: O(M²×N) time where M=word length, N=word count. O(M²×N) space
 * Treat each word as a node. Edges exist between words differing by 1 letter.
 * BFS guarantees shortest path.
 */
fun ladderLength(beginWord: String, endWord: String, wordList: List<String>): Int {
    if (endWord !in wordList) return 0

    val wordSet = wordList.toMutableSet()
    val queue = ArrayDeque<Pair<String, Int>>()
    queue.addLast(beginWord to 1)

    while (queue.isNotEmpty()) {
        val (word, level) = queue.removeFirst()

        // Try changing each position to every letter
        val chars = word.toCharArray()
        for (i in chars.indices) {
            val original = chars[i]
            for (c in 'a'..'z') {
                if (c == original) continue
                chars[i] = c
                val newWord = String(chars)
                if (newWord == endWord) return level + 1
                if (newWord in wordSet) {
                    wordSet.remove(newWord)
                    queue.addLast(newWord to level + 1)
                }
            }
            chars[i] = original
        }
    }
    return 0
}
