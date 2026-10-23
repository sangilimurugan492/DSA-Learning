# 📅 Week 12 — Full Revision + Company-Style Mixed Sets

> **Dates:** Jan 4 (Mon) – Jan 10 (Sun)
> **Focus:** Spaced-repetition revision of ALL 12 weeks + company-style timed sets + 🧪 FINAL Grand Mock
> **Method:** For each problem: re-solve FROM SCRATCH (no hints). If solved < 2 min (Easy) / < 10 min (Medium), mark 🏆. Otherwise add to the weak list and re-solve again Saturday.

---

> 🧪 **Practice & Test Cases:** [Week12_Practice.md](../practice/Week12_Practice.md) — timing sheet + company-set scoring.

## 🗓️ Day Plan

| Day | Date | Set | Time |
|-----|------|-----|------|
| Mon | Jan 4 | Meta-style set (arrays, strings, graphs) | 2 hrs |
| Tue | Jan 5 | Google-style set (DP, graphs, design) | 2 hrs |
| Wed | Jan 6 | Amazon-style set (graphs, heaps, oo design) | 2 hrs |
| Thu | Jan 7 | Weak-pattern re-drill (from Mock log) | 2 hrs |
| Fri | Jan 8 | 🛑 REST DAY | — |
| Sat | Jan 9 | Speed round: 8 Easy/Medium in 90 min + weak-list re-solve | 2 hrs |
| Sun | Jan 10 | 🧪 **FINAL GRAND MOCK (Mock Test 12)** + full review | 3 hrs |

---

## 🟦 MONDAY, Jan 4 — Meta-Style Set (practical arrays/strings/graphs)

| # | Problem | Source | Difficulty | Target |
|---|---------|--------|-----------|--------|
| 1 | Valid Palindrome | W1 #2 | 🟢 | < 5 min |
| 2 | Three Sum | W1 #8 | 🟡 | < 15 min |
| 3 | Minimum Window Substring | W2 #18 | 🔴 | < 30 min |
| 4 | Merge Intervals | W9 #16 | 🟡 | < 15 min |
| 5 | Number of Islands | W8 #5 | 🟡 | < 15 min |
| 6 | Rotting Oranges | W8 #8 | 🟡 | < 15 min |

```kotlin
fun validPalindromeRe(s: String): Boolean {
    // TODO: Re-solve from scratch
    return false
}
```

```kotlin
fun threeSumRe(nums: IntArray): List<List<Int>> {
    // TODO: Re-solve from scratch
    return listOf()
}
```

```kotlin
fun mergeIntervalsRe(intervals: Array<IntArray>): Array<IntArray> {
    // TODO: Re-solve from scratch
    return arrayOf()
}
```

---

## 🟨 TUESDAY, Jan 5 — Google-Style Set (DP + optimization)

| # | Problem | Source | Difficulty | Target |
|---|---------|--------|-----------|--------|
| 1 | Longest Substring Without Repeating | W2 #14 | 🟡 | < 15 min |
| 2 | Coin Change | W7 | 🟡 | < 20 min |
| 3 | Longest Common Subsequence | W10 #2 | 🟡 | < 20 min |
| 4 | Edit Distance | W10 #1 | 🔴 | < 30 min |
| 5 | Course Schedule | W8 #10 | 🟡 | < 15 min |
| 6 | Trapping Rain Water | W1 #17 | 🔴 | < 25 min |

```kotlin
fun coinChangeRe(coins: IntArray, amount: Int): Int {
    // TODO: Re-solve from scratch
    return 0
}
```

```kotlin
fun lcsRe(text1: String, text2: String): Int {
    // TODO: Re-solve from scratch
    return 0
}
```

---

## 🟧 WEDNESDAY, Jan 6 — Amazon-Style Set (graphs/heaps/design)

| # | Problem | Source | Difficulty | Target |
|---|---------|--------|-----------|--------|
| 1 | Top K Frequent Elements | W5 | 🟡 | < 15 min |
| 2 | Find Median from Data Stream | W5 | 🔴 | < 25 min |
| 3 | LRU Cache | W11 #11 | 🟡 | < 25 min |
| 4 | Network Delay Time | W9 #1 | 🟡 | < 20 min |
| 5 | Accounts Merge | W9 #10 | 🟡 | < 20 min |
| 6 | Clone Graph | W8 #6 | 🟡 | < 15 min |

```kotlin
fun topKFrequentRe(nums: IntArray, k: Int): IntArray {
    // TODO: Re-solve from scratch
    return intArrayOf()
}
```

```kotlin
class LRUCacheRe(capacity: Int) {
    fun get(key: Int): Int { /* TODO */ return 0 }
    fun put(key: Int, value: Int) { /* TODO */ }
}
```

---

## 🟪 THURSDAY, Jan 7 — Weak-Pattern Re-Drill

> Open [Mock_Test_Log.md](../Mock_Test_Log.md) → Weak Pattern Re-Practice Queue.
> Re-solve 2–3 problems from EACH weak pattern, from scratch, no hints.

| Weak Pattern | Problems Re-Solved | Status |
|--------------|-------------------|--------|
| _ | _ | ⬜ |
| _ | _ | ⬜ |
| _ | _ | ⬜ |

---

## 🟫 SATURDAY, Jan 9 — Speed Round + Final Consolidation

**Part 1 — Speed round (90 min, no hints at all):**

| # | Problem | Source | Difficulty | Time Limit |
|---|---------|--------|-----------|-----------|
| 1 | Two Sum (Sorted) | W1 #1 | 🟢 | 4 min |
| 2 | Move Zeroes | W1 #4 | 🟢 | 4 min |
| 3 | Subarray Sum Equals K | W2 #11 | 🟡 | 10 min |
| 4 | Valid Parentheses | W3 | 🟢 | 5 min |
| 5 | Middle of Linked List | W4 | 🟢 | 5 min |
| 6 | Invert Binary Tree | W5 | 🟢 | 5 min |
| 7 | Product of Array Except Self | W2 #10 | 🟡 | 12 min |
| 8 | Non-overlapping Intervals | W9 #18 | 🟡 | 12 min |

**Part 2 — Final consolidation (30 min):**
1. Update [Progress_Tracker.md](../Progress_Tracker.md) — every problem ⬜ → final status
2. Rewrite your one-page complexity cheatsheet from memory
3. Write your personal pattern-recognition cheat card (problem keyword → pattern)

---

## 🧪 SUNDAY, Jan 10 — FINAL GRAND MOCK (Mock Test 12, 3 hrs)

**Part 1 — Timed Test (2 hrs, no hints):**

| # | Problem | Difficulty | Time Limit |
|---|---------|-----------|-----------|
| 1 | Easy warm-up (mixed) | 🟢 | 10 min |
| 2 | Two Pointers / Sliding Window | 🟡 | 25 min |
| 3 | Graph (BFS/DFS/Union-Find) | 🟡 | 25 min |
| 4 | DP (1D/2D) | 🟡 | 30 min |
| 5 | Design / Monotonic Stack | 🔴 | 30 min |

**Part 2 — Review (1 hr):**
1. Score yourself: 🧠 = 2 pts, 💡 = 1 pt, 📖 = 0 pts (max 10)
2. Log results in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Update [Progress_Tracker.md](../Progress_Tracker.md) — final statuses
4. Write a 10-line "what I learned in 12 weeks" summary

---

## ✅ Week 12 Completion Checklist

- [ ] Mon: Meta-style set completed under target times
- [ ] Tue: Google-style set completed
- [ ] Wed: Amazon-style set completed
- [ ] Thu: All weak patterns re-drilled & cleared
- [ ] Sat: Speed round 8/8 within time limits
- [ ] Sun: FINAL Grand Mock completed & logged
- [ ] Progress Tracker fully updated
- [ ] Complexity cheatsheet + pattern-recognition card written
