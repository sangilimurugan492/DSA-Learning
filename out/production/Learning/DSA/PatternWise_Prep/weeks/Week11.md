# 📅 Week 11 — Monotonic Stack + Design (LRU/LFU/Min Stack) + Advanced Trie/Strings

> **Dates:** Dec 28 (Mon) – Jan 3 (Sun)
> **Pattern Focus:** Monotonic stack, cache design (LinkedHashMap / doubly-linked list), O(1) data structures, Trie applications
> **Reminder:** Try each problem 15–35 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/stack/`, `DSA/linked_list/double/`, `DSA/trie/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week11_Practice.md](../practice/Week11_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan

| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Dec 28 | #1–#5 (Monotonic Stack) | 2 hrs |
| Tue | Dec 29 | #6–#10 (Monotonic + Stack Applications) | 2 hrs |
| Wed | Dec 30 | #11–#15 (Design: caches & O(1) structures) | 2 hrs |
| Thu | Dec 31 | #16–#20 (Trie + String/Stack combos) | 2 hrs |
| Fri | Jan 1 | 🛑 REST DAY | — |
| Sat | Jan 2 | Revision: re-solve 2 weakest + mixed practice | 2 hrs |
| Sun | Jan 3 | 🧪 Mock Test 11 + Review | 3 hrs |

---

## 🟡 MONDAY, Dec 28 — Monotonic Stack

### 1. Daily Temperatures — 🟡
**LeetCode #739** | Solution (check later): `DSA/stack/daily_temperatures/`

**Problem:** For each day, how many days until a warmer temperature?

<details><summary>💡 Hint 1 (click to reveal)</summary>
Monotonic decreasing stack of indices.
</details>

<details><summary>💡 Hint 2</summary>
When current temp > temp at stack top, pop and set answer = current index − popped index.
</details>

<details><summary>💡 Hint 3</summary>
1. Iterate; while stack not empty && t[i] > t[stack.top()] → j = pop, ans[j] = i − j
2. Push i
</details>

```kotlin
fun dailyTemperatures(temperatures: IntArray): IntArray {
    // TODO: Decreasing stack of indices
    return intArrayOf()
}
```

---

### 2. Next Greater Element II — 🟡
**LeetCode #503** | Solution: `DSA/stack/next_greater_element_ii/`

**Problem:** Next greater element for a circular array (-1 if none).

<details><summary>💡 Hint 1</summary>
Monotonic stack + simulate circularity by iterating 2n times with index % n.
</details>

<details><summary>💡 Hint 2</summary>
Only push during the first pass; second pass only resolves leftovers.
</details>

<details><summary>💡 Hint 3</summary>
1. For i in 0 until 2n: idx = i % n; while stack top < nums[idx] → ans[pop] = nums[idx]
2. If i < n push idx
</details>

```kotlin
fun nextGreaterElements(nums: IntArray): IntArray {
    // TODO: Two-pass circular monotonic stack
    return intArrayOf()
}
```

---

### 3. Largest Rectangle in Histogram — 🔴
**LeetCode #84** | Solution: `DSA/stack/largest_rectangle_in_histogram/`

**Problem:** Largest rectangle area under the histogram.

<details><summary>💡 Hint 1</summary>
Increasing stack of indices; a bar's rectangle extends while bars are ≥ its height.
</details>

<details><summary>💡 Hint 2</summary>
When a smaller bar arrives, pop and compute area with popped height, width = right boundary (i) − left boundary (new top) − 1.
</details>

<details><summary>💡 Hint 3</summary>
1. Append sentinel 0. For each i: while stack top > h[i] → height = h[pop]; width = stack empty ? i : i − stack.top() − 1
2. Track max
</details>

```kotlin
fun largestRectangleArea(heights: IntArray): Int {
    // TODO: Increasing stack + sentinel
    return 0
}
```

---

### 4. Sum of Subarray Minimums — 🟡
**LeetCode #907** | Solution: `DSA/stack/`

**Problem:** Sum of min(b) over every contiguous subarray b (mod 1e9+7).

<details><summary>💡 Hint 1</summary>
Contribution technique: each element is the min of (left reach × right reach) subarrays.
</details>

<details><summary>💡 Hint 2</summary>
Monotonic stack finds previous-less and next-less-or-equal boundaries; avoid double counting with asymmetry.
</details>

<details><summary>💡 Hint 3</summary>
1. For each i compute left[i] (count to previous smaller) and right[i] (count to next smaller-or-equal)
2. Contribution = arr[i] × left[i] × right[i]; sum mod 1e9+7
</details>

```kotlin
fun sumSubarrayMins(arr: IntArray): Int {
    // TODO: Contribution + monotonic stack
    return 0
}
```

---

### 5. Online Stock Span — 🟡
**LeetCode #901** | Solution: `DSA/stack/online_stock_span/`

**Problem:** Return the span (consecutive days with price ≤ today's) for each quote.

<details><summary>💡 Hint 1</summary>
Monotonic decreasing stack of (price, span) pairs.
</details>

<details><summary>💡 Hint 2</summary>
While stack top price ≤ current price, absorb its span into current span.
</details>

<details><summary>💡 Hint 3</summary>
1. span = 1. While stack not empty && top.price <= price → span += top.span, pop
2. Push (price, span), return span
</details>

```kotlin
class StockSpanner() {
    fun next(price: Int): Int {
        // TODO: Absorb spans from the stack
        return 0
    }
}
```


---

### 6. Remove K Digits — 🟡
**LeetCode #402** | Solution (check later): `DSA/stack/`

**Problem:** Remove k digits from a number string so the result is smallest.

<details><summary>💡 Hint 1 (click to reveal)</summary>
Monotonic increasing stack — pop when current digit < top.
</details>

<details><summary>💡 Hint 2</summary>
Pop while k > 0 and top > current. Strip leading zeros; empty → "0".
</details>

<details><summary>💡 Hint 3</summary>
1. For each digit: while stack not empty && k>0 && top > d → pop, k--
2. Push d. After loop pop k leftovers. Trim leading '0'
</details>

```kotlin
fun removeKdigits(num: String, k: Int): String {
    // TODO: Increasing stack
    return ""
}
```

---

### 7. 132 Pattern — 🟡
**LeetCode #456** | Solution: `DSA/stack/`

**Problem:** Does nums contain i < j < k with nums[i] < nums[k] < nums[j]?

<details><summary>💡 Hint 1</summary>
Scan from the right with a decreasing stack; track the best "3" candidate (nums[k]).
</details>

<details><summary>💡 Hint 2</summary>
When popping (found nums[k] > current), any earlier smaller number completes the 132.
</details>

<details><summary>💡 Hint 3</summary>
1. third = −∞. Iterate i from n−1 down: nums[i] < third → true
2. While stack.top() < nums[i] → third = pop. Push nums[i]
</details>

```kotlin
fun find132pattern(nums: IntArray): Boolean {
    // TODO: Right-to-left decreasing stack + third
    return false
}
```

---

### 8. Sum of Subarray Ranges — 🟡
**LeetCode #2104** | Solution: `DSA/stack/`

**Problem:** Sum of (max − min) over all subarrays.

<details><summary>💡 Hint 1</summary>
Same contribution trick as #4, twice: sum of maxes − sum of mins.
</details>

<details><summary>💡 Hint 2</summary>
Monotonic stacks give left/right reach for max (greater) and min (smaller).
</details>

<details><summary>💡 Hint 3</summary>
1. contribution(arr, isMax) with prev/next boundaries. 2. answer = sumMax − sumMin (mod 1e9+7)
</details>

```kotlin
fun subArrayRanges(nums: IntArray): Long {
    // TODO: max-contrib − min-contrib
    return 0L
}
```

---

### 9. Max Chunks To Make Sorted — 🟡
**LeetCode #769** | Solution: `DSA/stack/`

**Problem:** Permutation of 0..n−1. Max chunks so each sorted chunk concatenates to sorted array.

<details><summary>💡 Hint 1</summary>
Chunk boundary after i valid iff max(nums[0..i]) == i.
</details>

<details><summary>💡 Hint 2</summary>
Running max trick — no stack needed.
</details>

<details><summary>💡 Hint 3</summary>
1. maxSoFar = 0, chunks = 0. 2. For i: maxSoFar = max(maxSoFar, nums[i]); if maxSoFar == i → chunks++
</details>

```kotlin
fun maxChunksToSorted(arr: IntArray): Int {
    // TODO: Running max == index
    return 0
}
```

---

### 10. Remove All Adjacent Duplicates in String II — 🟡
**LeetCode #1209** | Solution: `DSA/stack/`

**Problem:** Remove k equal consecutive letters repeatedly; return final string.

<details><summary>💡 Hint 1</summary>
Stack of (char, count).
</details>

<details><summary>💡 Hint 2</summary>
Count hits k → pop. Rebuild string from stack at the end.
</details>

<details><summary>💡 Hint 3</summary>
1. Same char as top → count+1; count==k → pop. 2. Else push (c,1). 3. Expand pairs × count
</details>

```kotlin
fun removeDuplicates(s: String, k: Int): String {
    // TODO: (char,count) stack
    return ""
}
```


---

## 🟡 WEDNESDAY, Dec 30 — Design: Caches & O(1) Structures

### 11. LRU Cache — 🟡
**LeetCode #146** | Solution (check later): `DSA/linked_list/double/least_recently_used_lru/`

**Problem:** O(1) get/put with capacity eviction of least-recently-used key.

<details><summary>💡 Hint 1</summary>
HashMap + doubly-linked list (recency order).
</details>

<details><summary>💡 Hint 2</summary>
get → move node to front. put → update/move, or insert; over capacity → evict tail.
</details>

<details><summary>💡 Hint 3</summary>
1. Dummy head/tail sentinels remove edge cases. 2. Helper: removeFromList, addToFront. 3. Evict node before tail
</details>

```kotlin
class LRUCache(val capacity: Int) {
    fun get(key: Int): Int { /* TODO */ return 0 }
    fun put(key: Int, value: Int) { /* TODO */ }
}
```

---

### 12. LFU Cache — 🔴
**LeetCode #460** | Solution: `DSA/linked_list/double/least_frequently_used_lfu/`

**Problem:** O(1) get/put evicting least-frequently-used (ties → least recently used).

<details><summary>💡 Hint 1</summary>
freq → list-of-nodes buckets + node → freq map + minFreq.
</details>

<details><summary>💡 Hint 2</summary>
On get/put: move node from freq f bucket to f+1 bucket (front = MRU); if minFreq bucket empties → minFreq++.
</details>

<details><summary>💡 Hint 3</summary>
1. keyToNode, freqToList (DoublyLinkedList), keyToFreq. 2. put: if new && size==capacity → evict freqToList[minFreq].last. 3. Bump frequency, set minFreq=1 for new items
</details>

```kotlin
class LFUCache(val capacity: Int) {
    fun get(key: Int): Int { /* TODO */ return 0 }
    fun put(key: Int, value: Int) { /* TODO */ }
}
```

---

### 13. Min Stack — 🟡
**LeetCode #155** | Solution: `DSA/stack/min_stack/`

**Problem:** Stack with push/pop/top/getMin all O(1).

<details><summary>💡 Hint 1</summary>
Track the current min alongside each value in the stack.
</details>

<details><summary>💡 Hint 2</summary>
Pairs (value, minAtThisPoint); pop restores previous min automatically.
</details>

<details><summary>💡 Hint 3</summary>
1. Push (v, min(v, stack.top.min)). 2. getMin → top.min
</details>

```kotlin
class MinStack() {
    fun push(`val`: Int) { /* TODO */ }
    fun pop() { /* TODO */ }
    fun top(): Int { /* TODO */ return 0 }
    fun getMin(): Int { /* TODO */ return 0 }
}
```

---

### 14. Insert Delete GetRandom O(1) — 🟡
**LeetCode #380** | Solution: `DSA/`

**Problem:** Set with O(1) insert, delete, and getRandom (uniform).

<details><summary>💡 Hint 1</summary>
ArrayList + HashMap(value → index).
</details>

<details><summary>💡 Hint 2</summary>
Delete = swap with last element, remove last (O(1)).
</details>

<details><summary>💡 Hint 3</summary>
1. insert: append + map. 2. remove: idx = map[v]; list[idx] = last; map[last]=idx; remove last. 3. getRandom = list[random(n)]
</details>

```kotlin
class RandomizedSet() {
    fun insert(`val`: Int): Boolean { /* TODO */ return false }
    fun remove(`val`: Int): Boolean { /* TODO */ return false }
    fun getRandom(): Int { /* TODO */ return 0 }
}
```


---

### 15. Time Based Key-Value Store — 🟡
**LeetCode #981** | Solution: `DSA/`

**Problem:** set(key, value, timestamp); get(key, timestamp) → value with largest timestamp ≤ given.

<details><summary>💡 Hint 1</summary>
Per-key list of (timestamp, value) — timestamps strictly increase.
</details>

<details><summary>💡 Hint 2</summary>
Binary search on the list for rightmost timestamp ≤ target.
</details>

<details><summary>💡 Hint 3</summary>
1. map: key → ArrayList&lt;Pair&lt;Int,String&gt;&gt;. 2. get: binary search lo/hi for rightmost ≤ t; empty → ""
</details>

```kotlin
class TimeMap() {
    fun set(key: String, value: String, timestamp: Int) { /* TODO */ }
    fun get(key: String, timestamp: Int): String { /* TODO */ return "" }
}
```


---

## 🟡 THURSDAY, Dec 31 — Trie + String/Stack Combos

### 16. Design Add and Search Words Data Structure — 🟡
**LeetCode #211** | Solution (check later): `DSA/trie/`

**Problem:** addWord + search supporting '.' wildcard (any single char).

<details><summary>💡 Hint 1</summary>
Trie; DFS for the '.' branches.
</details>

<details><summary>💡 Hint 2</summary>
At '.': try ALL children recursively; normal char: follow the edge.
</details>

<details><summary>💡 Hint 3</summary>
1. Node: children[26], isEnd. 2. search(i, node): end → isEnd; '.' → any child; else exact edge
</details>

```kotlin
class WordDictionary() {
    fun addWord(word: String) { /* TODO */ }
    fun search(word: String): Boolean { /* TODO */ return false }
}
```

---

### 17. Word Search II — 🔴
**LeetCode #212** | Solution: `DSA/trie/word_search_ii/`

**Problem:** Find all dictionary words on a 2D board (adjacent cells, no reuse).

<details><summary>💡 Hint 1</summary>
Build a Trie of the words; DFS from every cell walking the Trie in parallel.
</details>

<details><summary>💡 Hint 2</summary>
Prune: if a node has no children left, remove it (speeds up hugely).
</details>

<details><summary>💡 Hint 3</summary>
1. Insert all words. 2. DFS(r,c,node): match char edge; isEnd → add word. 3. Mark visited, recurse 4 dirs, unmark. 4. Prune childless nodes after match
</details>

```kotlin
fun findWords(board: Array<CharArray>, words: List<String>): List<String> {
    // TODO: Trie-guided DFS + pruning
    return listOf()
}
```

---

### 18. Maximum XOR of Two Numbers in an Array — 🟡
**LeetCode #421** | Solution: `DSA/trie/`

**Problem:** Maximum nums[i] XOR nums[j].

<details><summary>💡 Hint 1</summary>
Bit-Trie (31 levels); greedily walk opposite bits.
</details>

<details><summary>💡 Hint 2</summary>
For each number, descend the trie choosing the OPPOSITE bit when available to maximize XOR.
</details>

<details><summary>💡 Hint 3</summary>
1. Insert all (bit b = (num >> i) & 1). 2. For each num: walk down, XOR += 1<<i when opposite child exists. 3. Track max
</details>

```kotlin
fun findMaximumXOR(nums: IntArray): Int {
    // TODO: Bit trie + greedy descent
    return 0
}
```


---

### 19. Basic Calculator — 🔴
**LeetCode #224** | Solution: `DSA/stack/`

**Problem:** Evaluate expression with digits, '+', '-', '(', ')', spaces.

<details><summary>💡 Hint 1</summary>
Stack of running results on '(' — or recursive descent.
</details>

<details><summary>💡 Hint 2</summary>
Keep result, sign, number. '(' → push (result, sign); ')' → result = pushed result + pushed sign × result.
</details>

<details><summary>💡 Hint 3</summary>
1. result=0, sign=1, num=0. 2. Digit → num=10num+d. '+' → result+=sign·num. '(' → push both. ')' → pop & combine. 3. result += sign·num at end
</details>

```kotlin
fun calculate(s: String): Int {
    // TODO: Result/sign/num + stack on parens
    return 0
}
```

---

### 20. Decode String — 🟡
**LeetCode #394** | Solution: `DSA/stack/`

**Problem:** "3[a2[c]]" → "accaccacc". Expand k[encoded] repetitions.

<details><summary>💡 Hint 1</summary>
Two stacks: counts + strings.
</details>

<details><summary>💡 Hint 2</summary>
On '[': push current string and k; on ']': pop k and prev → current = prev + current × k.
</details>

<details><summary>💡 Hint 3</summary>
1. Digit → k=10k+d. '[' → push(k), push(cur), reset. ']' → s=popStr, n=popK, cur = s + cur.repeat(n). Char → append
</details>

```kotlin
fun decodeString(s: String): String {
    // TODO: counts + strings stacks
    return ""
}
```

---

## 🧪 SUNDAY, Jan 3 — Mock Test 11 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Difficulty | Time Limit |
|---|---------|-----------|-----------|
| 1 | Daily Temperatures | 🟡 | 15 min |
| 2 | Largest Rectangle in Histogram | 🔴 | 30 min |
| 3 | LRU Cache | 🟡 | 25 min |
| 4 | Design Add and Search Words | 🟡 | 20 min |
| 5 | LFU Cache | 🔴 | 30 min |

**Part 2 — Review (1 hr):**
1. Score yourself: 🧠 = 2 pts, 💡 = 1 pt, 📖 = 0 pts (max 10)
2. Log results in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Re-solve failed problems from scratch
4. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## ✅ Week 11 Completion Checklist

- [ ] Mon: 5 monotonic-stack problems solved
- [ ] Tue: 5 stack-application problems solved
- [ ] Wed: 5 design (caches/O(1)) problems solved
- [ ] Thu: 5 trie/string problems solved
- [ ] Sat: 2 weakest re-solved + mixed practice
- [ ] Sun: Mock Test 11 completed & logged
- [ ] Progress Tracker updated
