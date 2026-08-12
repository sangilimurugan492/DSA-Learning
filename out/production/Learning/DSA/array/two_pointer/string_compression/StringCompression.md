# String Compression — Detailed Explanation

> **LeetCode #443** | [Problem Link](https://leetcode.com/problems/string-compression/)  
> **Topic:** Two Pointers  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array of characters `chars`, compress it in-place: for each group of consecutive repeating characters, write the character followed by the count (if > 1). Return the new length.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `["a","a","b","b","c","c","c"]` | `6` | `"a2b2c3"` |
| `["a"]` | `1` | `"a"` (single char, no count) |
| `["a","b","b","b",...12 b's]` | `4` | `"ab12"` (multi-digit count) |

---

## 🧩 Method 1: Optimal (Two Pointer, In-Place)

### Core Idea

Use two pointers:
- `index` — scans the array (read pointer)
- `indexToWrite` — writes compressed output (write pointer)

For each group of consecutive characters:
1. Write the character at `indexToWrite`.
2. If count > 1, write each digit of the count.
3. Advance `index` to the next group.

### Walkthrough: `["a","a","b","b","c","c","c"]`

```
index=0: char='a', count=2 → write 'a', '2' → indexToWrite=2
index=2: char='b', count=2 → write 'b', '2' → indexToWrite=4
index=4: char='c', count=3 → write 'c', '3' → indexToWrite=6

Result: 6, chars = ["a","2","b","2","c","3",...] ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — in-place |

---

## 🧩 Method 2: StringBuilder (Not In-Place)

### Core Idea

Use two pointers to scan consecutive groups, build a compressed string with StringBuilder, then copy back to `chars`. Simpler but uses O(N) extra space.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) — StringBuilder |

---

## 🔑 Key Takeaways

1. **Read/write pointer pattern:** `index` reads groups, `indexToWrite` writes compressed output.
2. **Multi-digit counts:** When count ≥ 10, write each digit as a separate character.
3. **Single chars:** If count == 1, write only the character (no count).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| String Compression | [#443](https://leetcode.com/problems/string-compression/) | Medium |
| Decompress Run-Length Encoded List | [#1313](https://leetcode.com/problems/decompress-run-length-encoded-list/) | Easy |
| Count and Say | [#38](https://leetcode.com/problems/count-and-say/) | Medium |
