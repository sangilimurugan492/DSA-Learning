package trie.implement_trie

/**
 * Implement Trie (Prefix Tree) — LeetCode #208
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * Problem:
 * -------
 * A trie (pronounced "try") is a tree data structure used to efficiently store and
 * retrieve keys in a dataset of strings. Implement Trie with insert, search, startsWith.
 *
 * Example:
 *   trie.insert("apple")
 *   trie.search("apple")   → true
 *   trie.search("app")     → false
 *   trie.startsWith("app") → true
 *   trie.insert("app")
 *   trie.search("app")     → true
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE trie problem — foundation for all trie questions)
 *
 * Two approaches:
 * 1. HashMap-based Trie: O(M) per op — children stored in HashMap
 * 2. Array-based Trie: O(M) per op — children stored in fixed array of 26
 */

fun main() {
    println("=== Method 1: Array-based Trie ===")
    val trie = Trie()
    trie.insert("apple")
    println("search('apple'):   ${trie.search("apple")}")    // true
    println("search('app'):     ${trie.search("app")}")       // false
    println("startsWith('app'): ${trie.startsWith("app")}")   // true
    trie.insert("app")
    println("search('app'):     ${trie.search("app")}")       // true
    trie.insert("apply")
    println("search('apply'):   ${trie.search("apply")}")     // true
    println("startsWith('appl'): ${trie.startsWith("appl")}") // true

    println("\n=== Method 2: HashMap-based Trie ===")
    val trie2 = TrieHashMap()
    trie2.insert("apple")
    println("search('apple'):   ${trie2.search("apple")}")    // true
    println("search('app'):     ${trie2.search("app")}")      // false
    println("startsWith('app'): ${trie2.startsWith("app")}")  // true
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: ARRAY-BASED TRIE — O(M) per operation
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * ARRAY-BASED TRIE — Each node has an array of 26 children (one per lowercase letter).
 *
 * Core Idea:
 *   - Each node represents a character. Path from root to a node = a prefix.
 *   - isEnd flag marks if the path to this node is a complete word.
 *   - insert: traverse/create nodes for each char, mark last as isEnd.
 *   - search: traverse nodes, return true only if last node has isEnd=true.
 *   - startsWith: traverse nodes, return true if path exists.
 *
 * Time Complexity:  O(M) for all operations (M = word length).
 * Space Complexity: O(N × M) worst case (N words, avg length M). Less due to prefix sharing.
 */
class TrieNode {
    val children = Array<TrieNode?>(26) { null }
    var isEnd = false
}

class Trie {
    private val root = TrieNode()

    fun insert(word: String) {
        var node = root
        for (ch in word) {
            val idx = ch - 'a'
            if (node.children[idx] == null) node.children[idx] = TrieNode()
            node = node.children[idx]!!
        }
        node.isEnd = true
    }

    fun search(word: String): Boolean {
        val node = findNode(word) ?: return false
        return node.isEnd
    }

    fun startsWith(prefix: String): Boolean = findNode(prefix) != null

    private fun findNode(s: String): TrieNode? {
        var node = root
        for (ch in s) {
            val idx = ch - 'a'
            if (node.children[idx] == null) return null
            node = node.children[idx]!!
        }
        return node
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: HASHMAP-BASED TRIE — O(M) per operation
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * HASHMAP-BASED TRIE — Children stored in HashMap instead of fixed array.
 *
 * Advantage: Supports any character set (not just a-z). Less memory for sparse nodes.
 * Disadvantage: Slightly slower due to hash lookup vs array indexing.
 *
 * Time Complexity:  O(M) for all operations.
 * Space Complexity: O(N × M) worst case.
 */
class TrieNodeHashMap {
    val children = HashMap<Char, TrieNodeHashMap>()
    var isEnd = false
}

class TrieHashMap {
    private val root = TrieNodeHashMap()

    fun insert(word: String) {
        var node = root
        for (ch in word) {
            node = node.children.getOrPut(ch) { TrieNodeHashMap() }
        }
        node.isEnd = true
    }

    fun search(word: String): Boolean {
        val node = findNode(word) ?: return false
        return node.isEnd
    }

    fun startsWith(prefix: String): Boolean = findNode(prefix) != null

    private fun findNode(s: String): TrieNodeHashMap? {
        var node = root
        for (ch in s) {
            node = node.children[ch] ?: return null
        }
        return node
    }
}
