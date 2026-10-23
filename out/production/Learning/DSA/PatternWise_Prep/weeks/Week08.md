# 📅 Week 8 — Graph + Simulation + Matrix + String Parsing + Final Revision

> **Dates:** Dec 4 (Fri) – Dec 10 (Thu)
> **Pattern Focus:** Graph (BFS/DFS, topological sort, union-find), Simulation, Matrix, String Parsing + FULL SYLLABUS REVISION
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/graph/`, `DSA/simulation/`, `DSA/array/matrix/`, `DSA/array/string_parsing/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week08_Practice.md](../practice/Week08_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Fri | Dec 4 | #1–#8 (Simulation + easy graph) | 2 hrs |
| Sat | Dec 5 | #9–#13 (Graph BFS/DFS + shortest path) | 2 hrs |
| Sun | Dec 6 | #14–#20 (Matrix + string parsing) | 2 hrs |
| Mon | Dec 7 | #21–#23 (Hard) + revision plan | 2 hrs |
| Wed | Dec 9 | FULL REVISION: weakest problems across all 8 weeks | 2 hrs |
| Thu | Dec 10 | 🧪 FINAL MOCK TEST (Full Syllabus) + Review | 3 hrs |

---

## 🟢 FRIDAY, Dec 4 — Simulation + Easy Graph

### 1. Add Binary — 🟢
**LeetCode #67** | Solution: `DSA/simulation/add_binary/AddBinary.kt`

**Problem:** Add two binary strings.

<details><summary>💡 Hint 1</summary>
Elementary addition from the right with carry.
</details>

<details><summary>💡 Hint 2</summary>
sum = bitA + bitB + carry; result digit = sum % 2; carry = sum / 2.
</details>

<details><summary>💡 Hint 3</summary>
1. i = a.length-1, j = b.length-1, carry = 0, sb = StringBuilder
2. While i >= 0 || j >= 0 || carry > 0: total = ...; sb.append(total % 2); carry = total / 2
3. Return sb.reverse()
</details>

```kotlin
fun addBinary(a: String, b: String): String {
    // TODO: Right-to-left with carry
    return ""
}
```

---

### 2. Add Strings — 🟢
**LeetCode #415** | Solution: `DSA/simulation/add_string/AddString.kt`

**Problem:** Add two non-negative integer strings without conversion.

<details><summary>💡 Hint 1</summary>
Same as Add Binary but base 10.
</details>

<details><summary>💡 Hint 2</summary>
digit = sum % 10; carry = sum / 10.
</details>

<details><summary>💡 Hint 3</summary>
Identical skeleton to Add Binary with base 10.
</details>

```kotlin
fun addStrings(num1: String, num2: String): String {
    // TODO: Base-10 addition
    return ""
}
```

---

### 3. Add Digits (Digital Root) — 🟢
**LeetCode #258** | Solution: `DSA/simulation/add_digits/AddDigits.kt`

**Problem:** Repeatedly sum digits until one digit remains.

<details><summary>💡 Hint 1</summary>
Digital root formula: 0 if n == 0, else 1 + (n - 1) % 9.
</details>

<details><summary>💡 Hint 2</summary>
Or simulate the digit-summing loop.
</details>

<details><summary>💡 Hint 3</summary>
return if (num == 0) 0 else 1 + (num - 1) % 9.
</details>

```kotlin
fun addDigits(num: Int): Int {
    // TODO: Digital root
    return 0
}
```

---

### 4. Fizz Buzz — 🟢
**LeetCode #412** | Solution: `DSA/simulation/fizz_buzz/FizzBuzz.kt`

**Problem:** 1..n: "FizzBuzz" (÷15), "Fizz" (÷3), "Buzz" (÷5), or the number.

<details><summary>💡 Hint 1</summary>
Check divisibility in order: 15 → 3 → 5 → number.
</details>

<details><summary>💡 Hint 2</summary>
Straightforward conditionals.
</details>

<details><summary>💡 Hint 3</summary>
when { i % 15 == 0 -> "FizzBuzz"; i % 3 == 0 -> "Fizz"; i % 5 == 0 -> "Buzz"; else -> "$i" }
</details>

```kotlin
fun fizzBuzz(n: Int): List<String> {
    // TODO: Divisibility checks
    return listOf()
}
```

---

### 5. Number of Islands — 🟡
**LeetCode #200** | Solution: `DSA/graph/bfs_dfs/NumberOfIslands.kt`

**Problem:** Count islands (connected groups of '1's, 4-directional).

<details><summary>💡 Hint 1</summary>
Grid = implicit graph. DFS/BFS flood-fill each island.
</details>

<details><summary>💡 Hint 2</summary>
For each '1' cell: count++, flood-fill (mark all connected '1's as visited '0').
</details>

<details><summary>💡 Hint 3</summary>
1. For each cell == '1': islands++; dfs(r, c)
2. dfs: out of bounds or '0' → return; mark '0'; recurse 4 dirs
</details>

```kotlin
fun numIslands(grid: Array<CharArray>): Int {
    // TODO: Flood fill each island
    return 0
}
```

---

### 6. Clone Graph — 🟡
**LeetCode #133** | Solution: `DSA/graph/bfs_dfs/CloneGraph.kt`

**Problem:** Deep copy an undirected graph (nodes with neighbors list).

<details><summary>💡 Hint 1</summary>
DFS + HashMap original → clone.
</details>

<details><summary>💡 Hint 2</summary>
If node already cloned → return the clone. Else create clone, recurse neighbors, add cloned neighbors.
</details>

<details><summary>💡 Hint 3</summary>
1. map = HashMap<Node, Node>()
2. clone(node): map[node] ?: Node(node.val).also { c -> map[node] = c; c.neighbors = node.neighbors.map { clone(it) } }
</details>

```kotlin
fun cloneGraph(node: Node?): Node? {
    // TODO: Map original to clone, DFS
    return null
}
```

---

### 7. Pacific Atlantic Water Flow — 🟡
**LeetCode #417** | Solution: `DSA/graph/bfs_dfs/PacificAtlanticWaterFlow.kt`

**Problem:** Cells from which water can flow to BOTH Pacific and Atlantic (flow to <= neighbors).

<details><summary>💡 Hint 1</summary>
Reverse thinking: DFS FROM the oceans inward.
</details>

<details><summary>💡 Hint 2</summary>
Two boolean grids: reachable from Pacific edge, reachable from Atlantic edge. Answer = cells in both.
</details>

<details><summary>💡 Hint 3</summary>
1. pacific DFS from top row + left column; atlantic DFS from bottom row + right column
2. Flow inward: neighbor height >= current height
3. Collect cells where both grids are true
</details>

```kotlin
fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
    // TODO: Two ocean DFS, intersect
    return listOf()
}
```

---

### 8. Rotting Oranges — 🟡
**LeetCode #994** | Solution: `DSA/graph/bfs_dfs/RottingOranges.kt`

**Problem:** Every minute, rotten oranges rot adjacent fresh ones. Min minutes to rot all (or -1).

<details><summary>💡 Hint 1</summary>
Multi-source BFS — start from ALL rotten oranges simultaneously.
</details>

<details><summary>💡 Hint 2</summary>
Level-by-level BFS = minutes. Count fresh oranges; decrement as they rot. If any remain at the end → -1.
</details>

<details><summary>💡 Hint 3</summary>
1. Queue all initially rotten cells; fresh = count of fresh
2. BFS level by level (minutes++ per level); each fresh neighbor → rot, fresh--
3. Return if (fresh == 0) minutes else -1
</details>

```kotlin
fun orangesRotting(grid: Array<IntArray>): Int {
    // TODO: Multi-source BFS
    return 0
}
```

---

## 🟡 SATURDAY, Dec 5 — Graph Algorithms

### 9. Dijkstra's Algorithm — 🟡
**Solution:** `DSA/graph/shortest_path/Dijkstra.kt`

**Problem:** Shortest paths from a source in a weighted graph (non-negative weights).

<details><summary>💡 Hint 1</summary>
Greedy + min-heap: always process the closest unvisited node.
</details>

<details><summary>💡 Hint 2</summary>
dist[] = ∞ except source = 0. Pop (d, u); if d > dist[u] skip; relax all edges.
</details>

<details><summary>💡 Hint 3</summary>
1. heap = PriorityQueue of (dist, node), offer (0, src)
2. While heap not empty: (d, u) = poll; if d > dist[u] continue
3. For (v, w) in adj[u]: if dist[u] + w < dist[v] → update, offer
</details>

```kotlin
fun dijkstra(graph: Array<MutableList<Pair<Int, Int>>>, src: Int): IntArray {
    // TODO: Min-heap + edge relaxation
    return intArrayOf()
}
```

---

### 10. Course Schedule — 🟡
**LeetCode #207** | Solution: `DSA/graph/topological_sort/CourseSchedule.kt`

**Problem:** Can all courses be finished given prerequisites (cycle detection)?

<details><summary>💡 Hint 1</summary>
Topological sort (Kahn's BFS) or DFS cycle detection.
</details>

<details><summary>💡 Hint 2</summary>
Kahn's: in-degree array + queue. Process nodes with in-degree 0, decrement neighbors. All processed → no cycle.
</details>

<details><summary>💡 Hint 3</summary>
1. Build adjacency + in-degrees
2. Queue all in-degree-0 nodes; count processed
3. Return count == numCourses
</details>

```kotlin
fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
    // TODO: Kahn's algorithm
    return false
}
```

---

### 11. Course Schedule II — 🟡
**LeetCode #210** | Solution: `DSA/graph/topological_sort/CourseScheduleII.kt`

**Problem:** Return a valid course order (or empty if impossible).

<details><summary>💡 Hint 1</summary>
Same as Course Schedule, but record the order.
</details>

<details><summary>💡 Hint 2</summary>
Append each processed node to the result list.
</details>

<details><summary>💡 Hint 3</summary>
Kahn's BFS; if result.size < numCourses → return empty.
</details>

```kotlin
fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
    // TODO: Kahn's + record order
    return intArrayOf()
}
```

---

### 12. Number of Provinces — 🟡
**LeetCode #547** | Solution: `DSA/graph/union_find/NumberOfProvinces.kt`

**Problem:** Count connected groups of cities (isConnected matrix).

<details><summary>💡 Hint 1</summary>
Union-Find or DFS on the adjacency matrix.
</details>

<details><summary>💡 Hint 2</summary>
Union-Find: union(i, j) for each connection; count distinct roots at the end.
</details>

<details><summary>💡 Hint 3</summary>
1. parent = IntArray(n) { it }; find with path compression; union by rank
2. For each isConnected[i][j] == 1 → union(i, j)
3. Count i where find(i) == i
</details>

```kotlin
fun findCircleNum(isConnected: Array<IntArray>): Int {
    // TODO: Union-Find, count roots
    return 0
}
```

---

### 13. Graph Valid Tree — 🟡
**LeetCode #261** | Solution: `DSA/graph/union_find/GraphValidTree.kt`

**Problem:** Do the edges form a valid tree (connected, n-1 edges, no cycle)?

<details><summary>💡 Hint 1</summary>
A tree has exactly n-1 edges and no cycles.
</details>

<details><summary>💡 Hint 2</summary>
Union-Find: if union finds the same root → cycle → false. At the end, one component.
</details>

<details><summary>💡 Hint 3</summary>
1. If edges.size != n - 1 → false
2. Union all edges; same-root union → false
3. Return true
</details>

```kotlin
fun validTree(n: Int, edges: Array<IntArray>): Boolean {
    // TODO: Edge count + cycle check
    return false
}
```

---

## 🟡 SUNDAY, Dec 6 — Matrix + String Parsing

### 14. Set Matrix Zeroes — 🟡
**LeetCode #73** | Solution: `DSA/array/matrix/SetMatrixZeroes.kt`

**Problem:** If a cell is 0, set its entire row and column to 0 (in-place, O(1) extra).

<details><summary>💡 Hint 1</summary>
Use the first row and column as markers.
</details>

<details><summary>💡 Hint 2</summary>
First pass: mark first row/col cells if a 0 exists in that row/col. Track separately whether first row/col themselves need zeroing.
</details>

<details><summary>💡 Hint 3</summary>
1. firstRowZero, firstColZero flags
2. For inner cells: 0 → matrix[i][0] = 0, matrix[0][j] = 0
3. Second pass: zero inner cells based on markers; finally handle first row/col
</details>

```kotlin
fun setZeroes(matrix: Array<IntArray>) {
    // TODO: First row/col as markers
}
```

---

### 15. Spiral Matrix — 🟡
**LeetCode #54** | Solution: `DSA/array/matrix/SpiralMatrix.kt`

**Problem:** Return elements in spiral order.

<details><summary>💡 Hint 1</summary>
Four boundaries: top, bottom, left, right. Shrink inward.
</details>

<details><summary>💡 Hint 2</summary>
Traverse top row → right column → bottom row → left column, adjusting boundaries after each.
</details>

<details><summary>💡 Hint 3</summary>
while (top <= bottom && left <= right): 4 loops with boundary checks between them.
</details>

```kotlin
fun spiralOrder(matrix: Array<IntArray>): List<Int> {
    // TODO: Boundary shrinking
    return listOf()
}
```

---

### 16. Rotate Image — 🟡
**LeetCode #48** | Solution: `DSA/array/matrix/RotateImage.kt`

**Problem:** Rotate the matrix 90° clockwise in-place.

<details><summary>💡 Hint 1</summary>
Transpose, then reverse each row.
</details>

<details><summary>💡 Hint 2</summary>
Transpose: swap matrix[i][j] with matrix[j][i] for j > i. Then each row reversed.
</details>

<details><summary>💡 Hint 3</summary>
Two clean steps: transpose + row reversal.
</details>

```kotlin
fun rotate(matrix: Array<IntArray>) {
    // TODO: Transpose + reverse rows
}
```

---

### 17. Game of Life — 🟡
**LeetCode #289** | Solution: `DSA/array/matrix/GameOfLife.kt`

**Problem:** Apply Conway's Game of Life rules simultaneously (in-place).

<details><summary>💡 Hint 1</summary>
Encode both states: 2 = was dead now alive, 3 = was alive now dead.
</details>

<details><summary>💡 Hint 2</summary>
Count live neighbors (treat 1 and 3 as alive). Apply rules writing 2/3. Second pass: decode.
</details>

<details><summary>💡 Hint 3</summary>
1. For each cell: count neighbors with value 1 or 3
2. Live cell: neighbors < 2 or > 3 → 3; stays 1
3. Dead cell: exactly 3 live neighbors → 2
4. Decode: 1, 2 → 1; 0, 3 → 0
</details>

```kotlin
fun gameOfLife(board: Array<IntArray>) {
    // TODO: Two-state encoding
}
```

---

### 18. String to Integer (atoi) — 🟡
**LeetCode #8** | Solution: `DSA/array/string_parsing/StringToInteger.kt`

**Problem:** Parse a string to int: skip whitespace, optional sign, digits; clamp to Int range.

<details><summary>💡 Hint 1</summary>
State machine: whitespace → sign → digits → stop at non-digit.
</details>

<details><summary>💡 Hint 2</summary>
Accumulate with overflow check: if result > (MAX - digit) / 10 → clamp.
</details>

<details><summary>💡 Hint 3</summary>
1. Skip leading spaces; read optional +/-
2. For each digit: check overflow before result = result * 10 + digit
3. Clamp to Int.MIN_VALUE / MAX_VALUE
</details>

```kotlin
fun myAtoi(s: String): Int {
    // TODO: Parse with clamping
    return 0
}
```

---

### 19. Integer to Roman — 🟡
**LeetCode #12** | Solution: `DSA/array/string_parsing/IntegerToRoman.kt`

**Problem:** Convert an integer (1..3999) to Roman numerals.

<details><summary>💡 Hint 1</summary>
Greedy: value-symbol pairs in descending order (including 900, 400, 90, 40, 9, 4).
</details>

<details><summary>💡 Hint 2</summary>
While num >= value → append symbol, num -= value.
</details>

<details><summary>💡 Hint 3</summary>
Pairs: 1000→M, 900→CM, 500→D, 400→CD, 100→C, 90→XC, 50→L, 40→XL, 10→X, 9→IX, 5→V, 4→IV, 1→I.
</details>

```kotlin
fun intToRoman(num: Int): String {
    // TODO: Greedy descending pairs
    return ""
}
```

---

### 20. ZigZag Conversion — 🟡
**LeetCode #6** | Solution: `DSA/array/string_parsing/ZigZagConversionString.kt`

**Problem:** Write the string in zigzag rows, read row by row.

<details><summary>💡 Hint 1</summary>
Simulation: one StringBuilder per row, track direction.
</details>

<details><summary>💡 Hint 2</summary>
row += dir; at row 0 or numRows-1 → dir = -dir. Append each char to rows[row].
</details>

<details><summary>💡 Hint 3</summary>
1. rows = List(numRows) { StringBuilder() }; row = 0; dir = 1 (or -1 start)
2. For each char: rows[row].append(c); row += dir; flip dir at boundaries
3. Join rows
</details>

```kotlin
fun convert(s: String, numRows: Int): String {
    // TODO: Row builders + direction flip
    return ""
}
```

---

## 🟡 MONDAY, Dec 7 — Hard Problems

### 21. Spiral Matrix II — 🟡
**LeetCode #59** | Solution: `DSA/simulation/spiral_matrix_ii/SpiralMatrixII.kt`

**Problem:** Fill an n×n matrix with 1..n² in spiral order.

<details><summary>💡 Hint 1</summary>
Same boundary-shrinking as Spiral Matrix, but writing instead of reading.
</details>

<details><summary>💡 Hint 2</summary>
Counter increments as you fill; boundaries shrink after each side.
</details>

<details><summary>💡 Hint 3</summary>
top/bottom/left/right boundaries; four loops per cycle.
</details>

```kotlin
fun generateMatrix(n: Int): Array<IntArray> {
    // TODO: Boundary fill
    return arrayOf()
}
```

---

### 22. Word Ladder — 🔴
**LeetCode #127** | Solution: `DSA/graph/shortest_path/WordLadder.kt`

**Problem:** Transform beginWord to endWord one letter at a time; each intermediate must be in the list. Min steps (BFS).

<details><summary>💡 Hint 1</summary>
BFS where neighbors = words differing by one letter.
</details>

<details><summary>💡 Hint 2</summary>
Generate neighbors by trying a-z at each position; check against a word set. Level = transformation count.
</details>

<details><summary>💡 Hint 3</summary>
1. wordSet = wordList.toSet(); queue with (word, level)
2. For each position, for c in 'a'..'z': newWord; if in set → remove (visited), enqueue level+1
3. endWord found → level + 1
</details>

```kotlin
fun ladderLength(beginWord: String, endWord: String, wordList: List<String>): Int {
    // TODO: BFS with one-letter mutations
    return 0
}
```

---

### 23. Text Justification — 🔴
**LeetCode #68** | Solution: `DSA/simulation/text_justification/TextJustification.kt`

**Problem:** Wrap text to maxWidth with full justification (last line left-aligned).

<details><summary>💡 Hint 1</summary>
Greedily pack words per line; distribute spaces evenly (extra spaces go leftmost).
</details>

<details><summary>💡 Hint 2</summary>
Line = words + minimum 1 space each. Extra spaces = maxWidth - length; distribute round-robin to gaps.
</details>

<details><summary>💡 Hint 3</summary>
1. Collect words while (lineLength + word.length + 1 <= maxWidth)
2. If last line or single word → left-justify with single spaces, pad right
3. Else: gaps = wordCount - 1; extra = (maxWidth - wordsLen) / gaps; leftmost gaps get +1
</details>

```kotlin
fun fullJustify(words: Array<String>, maxWidth: Int): List<String> {
    // TODO: Pack lines, distribute spaces
    return listOf()
}
```

---

## 🟢 WEDNESDAY, Dec 9 — FULL REVISION (All 8 Weeks)

Pick your 6–8 weakest problems across the whole plan (from [Progress_Tracker.md](../Progress_Tracker.md) and [Mock_Test_Log.md](../Mock_Test_Log.md)) and re-solve them from scratch. Suggested focus areas:

| Week | Pattern | Re-solve candidates |
|------|---------|---------------------|
| 1 | Two Pointer | Trapping Rain Water, Three Sum |
| 2 | Sliding Window | Minimum Window Substring, Subarray Sum Equals K |
| 3 | HashMap + Stack | Largest Rectangle in Histogram, Longest Consecutive Sequence |
| 4 | Linked List | LRU Cache, Reverse in K-Group, Sort List |
| 5 | Tree + Heap + Trie | Validate BST, Find Median from Data Stream, Word Search II |
| 6 | Greedy + BS + Bit | Median of Two Sorted Arrays, Candy, Search in Rotated Array |
| 7 | Backtracking + DP | Edit Distance, Coin Change, N-Queens |
| 8 | Graph + Simulation | Number of Islands, Course Schedule, Word Ladder |

---

## 🧪 THURSDAY, Dec 10 — FINAL MOCK TEST (Full Syllabus, 3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Two Sum | HashMap | 🟢 | 15 min |
| 2 | Merge Intervals | Greedy | 🟡 | 20 min |
| 3 | Longest Substring Without Repeating | Sliding Window | 🟡 | 20 min |
| 4 | LRU Cache | Design + DLL | 🟡 | 25 min |
| 5 | Number of Islands | Graph BFS/DFS | 🟡 | 20 min |
| 6 | Coin Change | 1D DP | 🟡 | 20 min |

**Part 2 — Final Review (1 hr):**
1. Score yourself (max 12)
2. Log in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Celebrate 🎉 — review your 8-week journey in [Progress_Tracker.md](../Progress_Tracker.md)
4. List remaining weak patterns for future practice

---

## ✅ Week 8 Completion Checklist

- [ ] Mon: 8 simulation + graph problems
- [ ] Tue: 5 graph algorithm problems
- [ ] Wed: 7 matrix + string parsing problems
- [ ] Thu: 3 hard problems
- [ ] Sat: Full revision of weakest problems
- [ ] Sun: FINAL mock test completed & logged
- [ ] Progress Tracker fully updated
- [ ] 🎉 8-week pattern-wise prep COMPLETE!
