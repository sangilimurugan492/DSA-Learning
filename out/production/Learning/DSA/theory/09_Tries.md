# Data Structure Theory: Tries (Prefix Trees)

> **In-depth theory, diagrams, and implementation details for understanding tries at a fundamental level.**

---

## 1. What is a Trie?

A trie (pronounced "try") is a **tree-like data structure** that stores strings character by character. Each node represents a **single character**, and a path from the root to a node spells out a **prefix**. Tries excel at **prefix-based operations** that are impossible with hash tables.

```
Trie storing: "cat", "car", "card", "can", "dog"

            ROOT
           /    \
          c      d
         / \      \
        a          o
       /|\          \
      t  r  n        g*  ← isEnd = true ("dog")
      |  |  |
      *  d  *             ← * = isEnd = true ("cat", "can")
         |
         *

Words: cat*, car, card*, can*, dog*
(* = end of complete word)

Key insight: Shared prefixes share nodes!
"cat", "car", "card", "can" all share the "ca" prefix.
```

### Key Properties:
- **Character-based**: Each edge represents a single character
- **Prefix sharing**: Common prefixes share nodes (memory efficient)
- **O(M) operations**: M = length of word (independent of number of words stored)
- **Prefix operations**: Can check if any word starts with a given prefix
- **No collisions**: Unlike hash tables, no hash function needed

---

## 2. Trie vs Hash Table

| Operation | Trie | Hash Table |
|-----------|------|------------|
| Insert word | O(M) | O(M) avg |
| Search word | O(M) | O(M) avg |
| Prefix search ("starts with?") | **O(M)** | **❌ Impossible** |
| Autocomplete | **Natural** | **❌ Impossible** |
| Memory (shared prefixes) | **Less** (shared) | More (each word separate) |
| Memory (unique words) | More (node per char) | Less |
| Ordered traversal | **Sorted order** | Unordered |

> **The #1 advantage of tries over hash tables is PREFIX operations.** A hash table can tell you if "cat" exists, but it CANNOT tell you if any word starts with "ca".

---

## 3. Trie Node Structure

```
TrieNode:
┌─────────────────────────────┐
│ children: Map<Char, Node>   │  ← One child per possible next character
│ isEndOfWord: Boolean        │  ← True if this node completes a word
└─────────────────────────────┘

For lowercase English letters (a-z):
children can be an Array of size 26 instead of a Map.

Example: After inserting "cat"

    ROOT
     |
     c (children: {a → node})
     |
     a (children: {t → node})
     |
     t (children: {}, isEndOfWord: true)
```

```kotlin
class TrieNode {
    val children = HashMap<Char, TrieNode>()  // or Array<TrieNode?>(26)
    var isEndOfWord = false
}

class Trie {
    private val root = TrieNode()

    fun insert(word: String) {
        var current = root
        for (char in word) {
            current = current.children.getOrPut(char) { TrieNode() }
        }
        current.isEndOfWord = true
    }

    fun search(word: String): Boolean {
        var current = root
        for (char in word) {
            current = current.children[char] ?: return false
        }
        return current.isEndOfWord  // Must be end of complete word
    }

    fun startsWith(prefix: String): Boolean {
        var current = root
        for (char in prefix) {
            current = current.children[char] ?: return false
        }
        return true  // Prefix exists (don't need isEndOfWord)
    }
}
```

---

## 4. Operations and Time Complexity

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| **insert(word)** | **O(M)** | O(M) | M = word length |
| **search(word)** | **O(M)** | O(1) | Exact word match |
| **startsWith(prefix)** | **O(M)** | O(1) | Prefix check |
| **autocomplete(prefix)** | **O(M + N)** | O(1) | M = prefix, N = words with prefix |
| **delete(word)** | **O(M)** | O(1) | Unmark isEnd, optionally prune |

> **O(M) regardless of how many words are stored!** This is because we traverse exactly M characters. A hash table is also O(M) for a single word, but can't do prefix operations at all.

### Visual: Insert "cat", "car", "can"

```
Insert "cat":
ROOT → c → a → t(end)

Insert "car":
ROOT → c → a → t(end)
                → r(end)

Insert "can":
ROOT → c → a → t(end)
                → r(end)
                → n(end)

All three share "ca" prefix → only 2 nodes for shared prefix
```

### Visual: Search vs startsWith

```
Trie contains: "cat", "car" (NOT "ca")

search("cat"):  ROOT→c→a→t(end=true) → FOUND ✓
search("ca"):   ROOT→c→a(end=false)  → NOT FOUND (not end of word)
search("car"):  ROOT→c→a→r(end=true) → FOUND ✓

startsWith("ca"): ROOT→c→a → EXISTS ✓ (even though "ca" is not a word)
startsWith("d"):  ROOT→d? → NOT FOUND ✗
```

---

## 5. Autocomplete with Trie

```
Trie contains: "cat", "car", "card", "can", "dog"

User types: "ca"

Step 1: Traverse to prefix node (a after c)
        ROOT → c → a  ← We're here

Step 2: DFS/BFS from this node, collecting all words:
        a → t(end) → "cat"
        a → r(end) → "car"
        a → r → d(end) → "card"
        a → n(end) → "can"

Autocomplete suggestions: ["cat", "car", "card", "can"]

Time: O(M) to find prefix + O(K) to collect all matching words
      M = prefix length, K = total characters in matching words
```

```kotlin
fun autocomplete(prefix: String): List<String> {
    var current = root
    for (char in prefix) {
        current = current.children[char] ?: return emptyList()
    }
    val results = mutableListOf<String>()
    collectWords(current, prefix, results)
    return results
}

private fun collectWords(node: TrieNode, prefix: String, results: MutableList<String>) {
    if (node.isEndOfWord) results.add(prefix)
    for ((char, child) in node.children) {
        collectWords(child, prefix + char, results)
    }
}
```

---

## 6. Variations

### 6.1 Compressed Trie (Radix Tree / Patricia Trie)

```
Standard Trie:  ROOT→c→a→t→e→r→p→i→l→l→a→r(end)
                (10 nodes for "caterpillar")

Compressed Trie: ROOT→"caterpillar"(end)
                 (1 node! Single-edge for non-branching paths)

Compression rule: Merge chains of single-child nodes into one edge.

Saves memory but more complex to implement.
```

### 6.2 Suffix Trie

```
Stores ALL suffixes of a string (for pattern matching).

String: "banana"

Suffixes: "banana", "anana", "nana", "ana", "na", "a"

Suffix trie contains all suffixes → can search any substring in O(M).

Use: Bioinformatics (DNA pattern matching), text search.
Very memory expensive: O(N²) nodes for string of length N.
```

### 6.3 Ternary Search Tree (TST)

```
Instead of 26 children per node, each node has 3 children:
- left (char < current)
- middle (char == current, go to next char)
- right (char > current)

Saves memory (3 pointers vs 26) at cost of slower search.
Used in sed, grep for fast string matching.
```

---

## 7. Memory Analysis

### Standard Trie (Array of 26 children per node):

```
Each node: 26 pointers × 8 bytes = 208 bytes + isEnd flag
For N words of average length M: ~N × M nodes (less with shared prefixes)

Storing 1 million words avg length 8:
  Without prefix sharing: 8M nodes × 208 bytes = 1.6 GB (huge!)
  With prefix sharing: ~2-4M nodes × 208 bytes = 400-800 MB (still large)

This is why hash tables are preferred for simple word lookup.
Tries are worth it when PREFIX operations are needed.
```

### Optimized Trie (HashMap children):

```
Each node: HashMap (only stores existing children) + isEnd flag
Overhead per node: ~48 bytes (empty HashMap) + entries

Much more memory efficient for sparse character sets.
```

---

## 8. Advantages and Disadvantages

### Advantages:
- **Prefix operations**: Only DS that supports "starts with" efficiently
- **Autocomplete**: Natural fit for search suggestions
- **O(M) operations**: Independent of number of words stored
- **Sorted traversal**: Lexicographic order for free
- **No hash function**: No collision issues, no hash computation
- **Shared prefixes**: Memory efficient for similar words

### Disadvantages:
- **Memory hungry**: Each character = a node (with 26-child array)
- **Slower than hash table**: For simple lookup (hash is O(1) amortized with less memory)
- **Complex**: More complex than simple array/hash
- **Not cache-friendly**: Nodes scattered in memory (tree structure)

---

## 9. When to Use Tries

### Use Tries When:
- ✅ You need **prefix search** ("words starting with...")
- ✅ **Autocomplete** / typeahead suggestions
- ✅ **Spell checker** (dictionary lookup + suggestions)
- ✅ **IP routing** (longest prefix match)
- ✅ **Dictionary** with prefix-based operations
- ✅ **Word games** (Boggle, Scrabble — find valid words)

### Don't Use Tries When:
- ❌ You only need **exact lookup** (use hash table — less memory)
- ❌ **Memory is critical** (hash table is more compact)
- ❌ You need **range queries** on non-string data
- ❌ Storing **very few words** (overhead not worth it)

---

## 10. Real-World Applications

| Application | How Trie Is Used |
|-------------|-----------------|
| **Google Search autocomplete** | Trie of search queries; suggest completions |
| **Spell checker** | Trie dictionary; find valid words + corrections |
| **IP routing** | Longest prefix match for packet routing |
| **T9 predictive text** | Trie of dictionary words mapped to digit sequences |
| **Boggle/Scrabble** | Find all valid words on board using trie |
| **Contact search** | Phone book contact autocomplete |
| **Code editor** | Autocomplete variable/function names |
| **DNS lookup** | Trie for domain name resolution |
| **Bioinformatics** | Suffix trie for DNA sequence matching |
| **Command history** | Shell command autocomplete |

---

## Related Documents

- [← Back to Theory README](./README.md)
- [Previous: Heaps →](./08_Heaps.md)
- [Next: Disjoint Set →](./10_Disjoint_Set.md)
