package trie

/**
 * https://leetcode.com/problems/word-search-ii/
 *
 * Given an m×n board of characters and a list of words, return all words
 * that exist on the board. Each word must be constructed from letters of
 * sequentially adjacent cells (horizontal or vertical). The same cell
 * may not be used more than once per word.
 *
 * Example: board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["s","o","f","l"]],
 *          words = ["oath","pea","eat","rain"] → Output: ["eat","oath"]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Hard — Trie + Backtracking combination pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * NAIVE APPROACH: For each word, run DFS on the board → O(W × M × N × 4^L)
 *   W = number of words, M×N = board size, L = max word length
 *   Too slow when W is large!
 *
 * OPTIMAL: Build a Trie from ALL words, then DFS the board ONCE.
 *   - As we traverse the board, we follow trie paths simultaneously
 *   - If we reach a trie node with isEnd=true, we found a word!
 *   - If a trie path doesn't exist, we prune immediately (no need to continue DFS)
 *
 * WHY is this better?
 *   - Instead of searching for each word separately, we search for ALL words
 *     simultaneously using the trie as a "guide"
 *   - The trie acts as a filter: we only explore board paths that could lead to a word
 *   - Time: O(M × N × 4^L) but with MUCH better pruning than naive approach
 *
 * KEY OPTIMIZATION: Remove found words from trie (set isEnd=false) to avoid
 * duplicates and reduce search space. Also prune trie nodes with no children.
 *
 * Algorithm:
 *   1. Build trie from all words
 *   2. For each cell (r, c) on the board, start DFS
 *   3. In DFS: check if board[r][c] exists in current trie node's children
 *   4. If yes: mark cell as visited, recurse to neighbors, unmark cell
 *   5. If trie node isEnd: add word to result, set isEnd=false (avoid duplicates)
 *
 * Connection to other problems:
 *   - Word Search I: single word DFS (no trie needed)
 *   - Implement Trie: the trie structure used here
 *   - This combines Trie + Backtracking — a powerful pattern!
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Word Search II ===")

    val board = arrayOf(
        charArrayOf('o', 'a', 'a', 'n'),
        charArrayOf('e', 't', 'a', 'e'),
        charArrayOf('i', 'h', 'k', 'r'),
        charArrayOf('s', 'o', 'f', 'l')
    )
    val words = listOf("oath", "pea", "eat", "rain")

    println("Found: ${findWords(board, words)}")  // [eat, oath]

    println("---")

    val board2 = arrayOf(
        charArrayOf('a', 'b'),
        charArrayOf('c', 'd')
    )
    val words2 = listOf("abcb")
    println("Found: ${findWords(board2, words2)}")  // []
}

/**
 * Trie Node for Word Search
 * Includes the word at this node (if isEnd) for easy retrieval
 */
class WordTrieNode {
    val children = Array<WordTrieNode?>(26) { null }
    var isEnd = false
    var word: String? = null  // store the complete word at end nodes
}

/**
 * OPTIMAL — Trie + Backtracking
 * Time Complexity: O(M × N × 4^L) where L = max word length (with pruning)
 * Space Complexity: O(total characters in all words) for trie + O(L) for recursion
 *
 * Trace for board with words=["oath","pea","eat","rain"]:
 *
 * Trie:
 *   root → o → a → t → h (isEnd="oath")
 *        → p → e → a (isEnd="pea")
 *        → e → a → t (isEnd="eat")
 *        → r → a → i → n (isEnd="rain")
 *
 * DFS from (0,0)='o': trie has 'o' → follow
 *   (0,1)='a': trie has 'a' → follow
 *     (0,2)='a': trie has 'a'? No (need 't') → backtrack
 *     (1,1)='t': trie has 't' → follow
 *       (1,0)='e': trie has 'e'? No (need 'h') → backtrack
 *       (2,1)='h': trie has 'h' → follow → isEnd! Found "oath" ✅
 *
 * DFS from (1,0)='e': trie has 'e' → follow
 *   (0,0)='o': trie has 'o'? No (need 'a') → backtrack
 *   (2,0)='i': trie has 'i'? No → backtrack
 *   (1,1)='t': trie has 't'? No → backtrack
 *
 * DFS from (1,2)='a': trie has 'a' → follow
 *   ... eventually finds "eat" through (1,2)→(0,2)→(1,2)wait, need to track visited
 *   Actually: (1,2)='a' → (0,2)='a' → no 'e' child → backtrack
 *              (1,2)='a' → (1,1)='t' → no 'e' child → backtrack
 *   Hmm, "eat" starts with 'e': DFS from (1,0)='e' → (1,1)='a' → (0,1)='a' no
 *     Actually (1,0)='e' → (1,1)='t' no... Let me re-check.
 *     "eat": e→a→t. From (1,0)='e', neighbors: (0,0)='o', (2,0)='i', (1,1)='t'
 *     None is 'a'. But (1,0)='e' → trie child 'a'? Yes!
 *     Wait, we need to check if the board char matches the trie path.
 *     From (1,0)='e': trie has child 'a'? The trie path is e→a→t.
 *     But board[1][0]='e', and we need to go to a neighbor that is 'a'.
 *     Neighbors of (1,0): (0,0)='o', (2,0)='i', (1,1)='t'. None is 'a'.
 *     So "eat" is NOT found from (1,0).
 *     Actually "eat" is found from (1,3)='e' → (1,2)='a' → (1,1)='t' ✅
 */
fun findWords(board: Array<CharArray>, words: List<String>): List<String> {
    val result = mutableListOf<String>()
    if (board.isEmpty() || words.isEmpty()) return result

    // Step 1: Build trie from all words
    val root = WordTrieNode()
    for (word in words) {
        var node = root
        for (ch in word) {
            val idx = ch - 'a'
            if (node.children[idx] == null) {
                node.children[idx] = WordTrieNode()
            }
            node = node.children[idx]!!
        }
        node.isEnd = true
        node.word = word
    }

    // Step 2: DFS from each cell
    val m = board.size
    val n = board[0].size
    val directions = arrayOf(intArrayOf(0, 1), intArrayOf(0, -1), intArrayOf(1, 0), intArrayOf(-1, 0))

    fun dfs(r: Int, c: Int, node: WordTrieNode) {
        val ch = board[r][c]
        val idx = ch - 'a'

        // No such word in trie — prune
        if (node.children[idx] == null) return

        val nextNode = node.children[idx]!!

        // Found a word!
        if (nextNode.isEnd) {
            result.add(nextNode.word!!)
            nextNode.isEnd = false   // avoid duplicates
            nextNode.word = null
        }

        // Mark cell as visited
        board[r][c] = '#'

        // Explore neighbors
        for (dir in directions) {
            val nr = r + dir[0]
            val nc = c + dir[1]
            if (nr in 0 until m && nc in 0 until n && board[nr][nc] != '#') {
                dfs(nr, nc, nextNode)
            }
        }

        // Restore cell (backtrack)
        board[r][c] = ch

        // OPTIMIZATION: prune empty trie nodes (reduces future search space)
        // If nextNode has no children and is not an end, we can remove it
        if (nextNode.children.all { it == null } && !nextNode.isEnd) {
            node.children[idx] = null
        }
    }

    for (r in 0 until m) {
        for (c in 0 until n) {
            dfs(r, c, root)
        }
    }

    return result
}
