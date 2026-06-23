package trie

/**
 * https://leetcode.com/problems/implement-trie-prefix-tree/
 *
 * A trie (pronounced "try") or prefix tree is a tree data structure used to
 * efficiently store and retrieve keys in a dataset of strings.
 * Implement the Trie class with insert, search, and startsWith methods.
 *
 * Example:
 *   Trie trie = new Trie()
 *   trie.insert("apple")
 *   trie.search("apple")   → true
 *   trie.search("app")     → false
 *   trie.startsWith("app") → true
 *   trie.insert("app")
 *   trie.search("app")     → true
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE trie problem — foundation for all trie questions)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * WHAT is a Trie?
 *   - A tree where each node represents a CHARACTER
 *   - The path from root to any node represents a PREFIX
 *   - The path from root to a marked node represents a WORD
 *
 * WHY use a Trie?
 *   - Search: O(M) where M = length of word (independent of N words stored!)
 *   - Prefix search: O(M) — impossible with HashMap!
 *   - Autocomplete: natural fit — all words with a prefix are in the subtree
 *
 * Trie structure for ["apple", "app", "apply"]:
 *            root
 *           /
 *          a
 *         /
 *        p
 *       / (isEnd=true ← "app")
 *      p
 *     / \
 *    l   (isEnd=true ← "apple")
 *   / \
 *  y   e
 *  |   |
 * (isEnd) (isEnd=true ← "apply")
 *
 * KEY OPERATIONS:
 *   insert(word):  Traverse/create nodes for each char, mark last node as isEnd
 *   search(word):  Traverse nodes, return true only if last node has isEnd=true
 *   startsWith(p): Traverse nodes, return true if path exists (don't need isEnd)
 *
 * Connection to other problems:
 *   - Word Search II: Trie + DFS backtracking
 *   - Autocomplete Systems: Trie + BFS/DFS for suggestions
 *   - HashMap: Trie trades space for O(M) prefix search (HashMap can't do prefix search)
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Implement Trie (Prefix Tree) ===")
    val trie = Trie()

    trie.insert("apple")
    println("search('apple'):   ${trie.search("apple")}")    // true
    println("search('app'):     ${trie.search("app")}")      // false
    println("startsWith('app'): ${trie.startsWith("app")}")  // true

    trie.insert("app")
    println("search('app'):     ${trie.search("app")}")      // true

    println("---")
    trie.insert("apply")
    println("search('apply'):   ${trie.search("apply")}")    // true
    println("search('appl'):    ${trie.search("appl")}")     // false
    println("startsWith('appl'): ${trie.startsWith("appl")}") // true
}

/**
 * Trie Node
 * Each node has up to 26 children (one per lowercase letter)
 * and a flag indicating if this node completes a word.
 */
class TrieNode {
    val children = Array<TrieNode?>(26) { null }
    var isEnd = false
}

/**
 * Trie (Prefix Tree) Implementation
 *
 * Time Complexity:
 *   insert(word):  O(M) where M = length of word
 *   search(word):  O(M)
 *   startsWith(p): O(M)
 *
 * Space Complexity: O(N × M) worst case where N = number of words, M = avg length
 * (In practice much less due to prefix sharing)
 */
class Trie {
    private val root = TrieNode()

    /**
     * Insert a word into the trie.
     *
     * Trace for insert("apple"):
     * root → a (create) → p (create) → p (create) → l (create) → e (create, isEnd=true)
     *
     * After insert("app"):
     * root → a → p → p (isEnd=true) → l → e (isEnd=true)
     *                  ↑ "app" is now a word
     */
    fun insert(word: String) {
        var node = root
        for (ch in word) {
            val idx = ch - 'a'
            if (node.children[idx] == null) {
                node.children[idx] = TrieNode()
            }
            node = node.children[idx]!!
        }
        node.isEnd = true
    }

    /**
     * Search for a complete word in the trie.
     * Returns true only if the word exists AND is marked as a complete word.
     *
     * Trace for search("apple"):
     * root → a → p → p → l → e (isEnd=true) → true ✅
     *
     * Trace for search("app") before insert("app"):
     * root → a → p → p (isEnd=false) → false ✅
     * (path exists but "app" wasn't inserted as a complete word)
     */
    fun search(word: String): Boolean {
        val node = findNode(word) ?: return false
        return node.isEnd
    }

    /**
     * Check if any word in the trie starts with the given prefix.
     * Returns true if the prefix path exists (regardless of isEnd).
     *
     * Trace for startsWith("app"):
     * root → a → p → p → path exists → true ✅
     *
     * Trace for startsWith("apx"):
     * root → a → p → x (null) → false ✅
     */
    fun startsWith(prefix: String): Boolean {
        return findNode(prefix) != null
    }

    /**
     * Helper: traverse the trie following the characters of the string.
     * Returns the final node if the path exists, null otherwise.
     */
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
