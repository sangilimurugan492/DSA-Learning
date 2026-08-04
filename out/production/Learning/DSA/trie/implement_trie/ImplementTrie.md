# Implement Trie (Prefix Tree) — Detailed Explanation

> **LeetCode #208** | [Problem Link](https://leetcode.com/problems/implement-trie-prefix-tree/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (THE trie problem — foundation for all trie questions)  
> **Topic:** Trie, Design

---

## 📋 Problem Statement

Implement a Trie with `insert`, `search`, and `startsWith` methods.

### Example

```
insert("apple") → search("apple")=true, search("app")=false, startsWith("app")=true
insert("app") → search("app")=true
```

---

## 🧩 Method 1: Array-based Trie — O(M) per operation

### Core Idea

Each node has an array of 26 children (one per lowercase letter). `isEnd` flag marks complete words.

### Key Insight

> A trie stores characters as paths. The path from root to any node = a prefix. The path to a node with `isEnd=true` = a complete word. Search is O(M) regardless of how many words are stored!

### Trie Structure for `["apple", "app", "apply"]`

```
root → a → p → p (isEnd ← "app")
                    → l → e (isEnd ← "apple")
                    → l → y (isEnd ← "apply")
```

### Operations

| Operation | Description | Time |
|-----------|-------------|------|
| `insert(word)` | Traverse/create nodes for each char, mark last as isEnd | O(M) |
| `search(word)` | Traverse nodes, return true only if last node has isEnd=true | O(M) |
| `startsWith(prefix)` | Traverse nodes, return true if path exists | O(M) |

### Code

```kotlin
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

    fun search(word: String): Boolean = findNode(word)?.isEnd ?: false
    fun startsWith(prefix: String): Boolean = findNode(prefix) != null
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M) | M = word length |
| **Space** | O(N × M) | N words, avg length M (less due to sharing) |

---

## 🧩 Method 2: HashMap-based Trie — O(M) per operation

### Core Idea

Children stored in `HashMap<Char, TrieNode>` instead of fixed array. Supports any character set.

### Comparison with Array-based

| Aspect | Array-based | HashMap-based |
|--------|-------------|---------------|
| Char set | a-z only | Any character |
| Memory (sparse) | Wastes 26 slots/node | Only stores existing children |
| Speed | Faster (array index) | Slightly slower (hash lookup) |

---

## 📊 Comparison Table

| Aspect | Array-based | HashMap-based |
|--------|-------------|---------------|
| **Time** | O(M) | O(M) |
| **Space** | O(N × M) | O(N × M) |
| **Char set** | a-z only | Any |
| **Interview preference** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **O(M) search:** Independent of N (number of words stored). Only depends on word length.
2. **Prefix search:** O(M) — impossible with HashMap! This is the key advantage of Trie.
3. **isEnd flag:** Distinguishes between prefix path and complete word.
4. **Prefix sharing:** Words with common prefixes share nodes → saves space.
5. **Pattern:** Trie — extends to Word Search II, Autocomplete, IP Routing.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Implement Trie | [#208](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium |
| Word Search II | [#212](https://leetcode.com/problems/word-search-ii/) | Hard |
| Design Add & Search Words | [#211](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium |
| Replace Words | [#648](https://leetcode.com/problems/replace-words/) | Medium |
