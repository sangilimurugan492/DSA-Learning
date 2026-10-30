# 📅 Week 9 — Advanced Graphs: Dijkstra + Union-Find + Topological Sort + Intervals

> **Dates:** Dec 14 (Mon) – Dec 20 (Sun)
> **Pattern Focus:** Weighted shortest path (Dijkstra/Bellman-Ford), Disjoint Set (Union-Find), Topological Sort, MST, Interval merging
> **Reminder:** Try each problem 15–35 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/graph/` and `DSA/array/greedy/` (intervals) only AFTER solving.

> 🧪 **Practice & Test Cases:** [Week09_Practice.md](../practice/Week09_Practice.md) — sample cases + complexity validation (use [TestRunner.kt](../testrunner/TestRunner.kt)).

## 🗓️ Day Plan

| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Dec 14 | #1–#5 (Dijkstra / Shortest Path) | 2 hrs |
| Tue | Dec 15 | #6–#10 (Union-Find) | 2 hrs |
| Wed | Dec 16 | #11–#15 (Topo Sort / MST) | 2 hrs |
| Thu | Dec 17 | #16–#20 (Intervals) | 2 hrs |
| Fri | Dec 18 | 🛑 REST DAY | — |
| Sat | Dec 19 | Revision: re-solve 2 weakest + mixed practice | 2 hrs |
| Sun | Dec 20 | 🧪 Mock Test 9 + Review | 3 hrs |

---

## 🟡 MONDAY, Dec 14 — Dijkstra / Shortest Path

### 1. Network Delay Time — 🟡
**LeetCode #743** | Solution (check later): `DSA/graph/shortest_path/`

**Problem:** Signal spreads from node `k` through a weighted directed graph. Time for ALL nodes to receive it, or -1.

<details><summary>💡 Hint 1 (click to reveal)</summary>
Single-source shortest path → Dijkstra with a min-heap.
</details>

<details><summary>💡 Hint 2</summary>
Pop smallest distance, skip stale entries, relax neighbors. Answer = max distance.
</details>

<details><summary>💡 Hint 3 (steps)</summary>
1. Adjacency list; dist[] = ∞ except dist[k]=0; heap of (dist, node)
2. Pop, skip if stale, relax. 3. max(dist) or -1 if unreachable
</details>

```kotlin
fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
    // TODO: Dijkstra with PriorityQueue
    return 0
}
```

---

### 2. Cheapest Flights Within K Stops — 🟡
**LeetCode #787** | Solution: `DSA/graph/shortest_path/`

**Problem:** Cheapest price from `src` to `dst` using at most `k` stops.

<details><summary>💡 Hint 1</summary>
Dijkstra alone fails — stop count constrains cost. Use Bellman-Ford.
</details>

<details><summary>💡 Hint 2</summary>
Relax ALL edges k+1 rounds, each round from a frozen copy of previous distances.
</details>

<details><summary>💡 Hint 3</summary>
1. dist[src]=0. Repeat k+1: temp=dist.copyOf(); for (u,v,w): temp[v]=min(temp[v], dist[u]+w); dist=temp
2. Return dist[dst] or -1
</details>

```kotlin
fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
    // TODO: Bellman-Ford, k+1 rounds
    return 0
}
```

---

### 3. Path With Minimum Effort — 🟡
**LeetCode #1631** | Solution: `DSA/graph/shortest_path/`

**Problem:** Top-left → bottom-right minimizing the MAX |height diff| along the path.

<details><summary>💡 Hint 1</summary>
Dijkstra where path cost = max edge weight so far.
</details>

<details><summary>💡 Hint 2</summary>
First pop of destination = answer. Edge weight = |h[a]−h[b]|.
</details>

<details><summary>💡 Hint 3</summary>
1. Min-heap (effort, r, c). 2. Relax 4 dirs: new = max(effort, |diff|); update if smaller. 3. Return on reaching (n−1,n−1)
</details>

```kotlin
fun minimumEffortPath(heights: Array<IntArray>): Int {
    // TODO: Dijkstra with max-diff cost
    return 0
}
```

---

### 4. Path With Maximum Probability — 🟡
**LeetCode #1514** | Solution: `DSA/graph/shortest_path/`

**Problem:** Maximize product of edge probabilities from `start` to `end`.

<details><summary>💡 Hint 1</summary>
Dijkstra variant: MAXIMIZE instead of minimize → max-heap on probability.
</details>

<details><summary>💡 Hint 2</summary>
Relaxation: candidate = prob[u] × w; update if greater.
</details>

<details><summary>💡 Hint 3</summary>
1. prob[start]=1.0; max-heap. 2. Pop; if node==end return prob. 3. Relax with candidate > prob[v]
</details>

```kotlin
fun maxProbability(n: Int, edges: Array<IntArray>, succProb: DoubleArray, start: Int, end: Int): Double {
    // TODO: Max-heap Dijkstra
    return 0.0
}
```


---

### 5. Swim in Rising Water — 🔴
**LeetCode #778** | Solution: `DSA/graph/shortest_path/`

**Problem:** Can enter a cell when time ≥ its elevation. Least time to reach bottom-right.

<details><summary>💡 Hint 1</summary>
Dijkstra where path cost = MAX elevation on the path.
</details>

<details><summary>💡 Hint 2</summary>
Always expand lowest-elevation reachable cell; first pop of destination = answer.
</details>

<details><summary>💡 Hint 3</summary>
1. Min-heap (height, r, c) from (grid[0][0],0,0). 2. ans = max(ans, h); push unvisited neighbors. 3. Return ans when (n−1,n−1) popped
</details>

```kotlin
fun swimInWater(grid: Array<IntArray>): Int {
    // TODO: Dijkstra with path-max cost
    return 0
}
```

---

## 🟡 TUESDAY, Dec 15 — Union-Find (Disjoint Set)

### 6. Redundant Connection — 🟡
**LeetCode #684** | Solution: `DSA/graph/union_find/`

**Problem:** A tree got one extra edge added. Return that edge.

<details><summary>💡 Hint 1</summary>
Union-Find — first edge whose endpoints are ALREADY connected is the answer.
</details>

<details><summary>💡 Hint 2</summary>
find with path compression + union by rank; cycle check = find(u) == find(v).
</details>

<details><summary>💡 Hint 3</summary>
1. parent[i]=i. 2. For each edge: same root → return edge; else union. 
</details>

```kotlin
fun findRedundantConnection(edges: Array<IntArray>): IntArray {
    // TODO: Union-Find cycle detection
    return intArrayOf()
}
```

---

### 7. Redundant Connection II — 🔴
**LeetCode #685** | Solution: `DSA/graph/union_find/`

**Problem:** Same, but rooted DIRECTED tree — one extra directed edge.

<details><summary>💡 Hint 1</summary>
Three cases: node with 2 parents, cycle, or both.
</details>

<details><summary>💡 Hint 2</summary>
If a 2-parent node exists, try skipping the 2nd edge; validate the rest with DSU.
</details>

<details><summary>💡 Hint 3</summary>
1. Find in-degree-2 node → candidates e1, e2. 2. Union edges minus candidate: no cycle & connected → that candidate; cycle without 2-parent → that edge
</details>

```kotlin
fun findRedundantDirectedConnection(edges: Array<IntArray>): IntArray {
    // TODO: 2-parent + cycle case analysis
    return intArrayOf()
}
```

---

### 8. Number of Operations to Make Network Connected — 🟡
**LeetCode #1319** | Solution: `DSA/graph/union_find/`

**Problem:** Min cable moves to connect all n computers, or -1.

<details><summary>💡 Hint 1</summary>
Answer = components − 1, using redundant (cycle) cables as spare.
</details>

<details><summary>💡 Hint 2</summary>
Count cycle-forming edges while unioning; count components.
</details>

<details><summary>💡 Hint 3</summary>
1. Union all; count redundant. 2. comps = distinct roots. 3. redundant ≥ comps−1 → comps−1 else −1
</details>

```kotlin
fun makeConnected(n: Int, connections: Array<IntArray>): Int {
    // TODO: components − 1 with spare edges
    return 0
}
```


---

### 9. Most Stones Removed with Same Row or Column — 🟡
**LeetCode #947** | Solution: `DSA/graph/union_find/`

**Problem:** Max stones removable if sharing a row/column with another stone.

<details><summary>💡 Hint 1</summary>
Each component can shrink to 1 stone → answer = n − components.
</details>

<details><summary>💡 Hint 2</summary>
Union stone's row-key with col-key (offset columns to one index space).
</details>

<details><summary>💡 Hint 3</summary>
1. row r → r, col c → c + 10001. 2. Union per stone. 3. removed = n − distinct roots
</details>

```kotlin
fun removeStones(stones: Array<IntArray>): Int {
    // TODO: union row/col keys
    return 0
}
```

---

### 10. Accounts Merge — 🟡
**LeetCode #721** | Solution: `DSA/graph/union_find/`

**Problem:** Merge accounts sharing any email.

<details><summary>💡 Hint 1</summary>
Union all emails within an account; components = people.
</details>

<details><summary>💡 Hint 2</summary>
email → id map; group emails by root; sort; prepend name.
</details>

<details><summary>💡 Hint 3</summary>
1. Union consecutive email ids per account. 2. root → set of emails. 3. Sort each group, attach name
</details>

```kotlin
fun accountsMerge(accounts: List<List<String>>): List<List<String>> {
    // TODO: union emails, group by root
    return listOf()
}
```

---

## 🟡 WEDNESDAY, Dec 16 — Topological Sort / MST

### 11. Regions Cut By Slashes — 🔴
**LeetCode #959** | Solution: `DSA/graph/union_find/`

**Problem:** n×n grid of '/', '\\', ' ' → number of regions formed.

<details><summary>💡 Hint 1</summary>
Split each cell into 4 triangles: 0=top, 1=right, 2=bottom, 3=left. Union-Find.
</details>

<details><summary>💡 Hint 2</summary>
'/' merges (top+left) and (right+bottom); '\\' merges (top+right) and (bottom+left); ' ' merges all 4. Neighbors: right-triangle ↔ next cell's left; bottom ↔ below's top.
</details>

<details><summary>💡 Hint 3</summary>
1. DSU size 4n². 2. Apply per-cell merges. 3. Cross-cell merges. 4. Count distinct roots
</details>

```kotlin
fun regionsBySlashes(grid: Array<String>): Int {
    // TODO: DSU over 4 triangles per cell
    return 0
}
```

---

### 12. Min Cost to Connect All Points — 🟡
**LeetCode #1584** | Solution: `DSA/graph/`

**Problem:** MST cost over all points with Manhattan-distance edges.

<details><summary>💡 Hint 1</summary>
Complete graph → Prim's O(n²) without heap is simplest.
</details>

<details><summary>💡 Hint 2</summary>
Keep minDist[i] = distance from point i to the tree; each round add closest outside point, relax.
</details>

<details><summary>💡 Hint 3</summary>
1. minDist[i] = |xi−x0|+|yi−y0|. 2. Repeat n−1 picks: take min unvisited, add cost, relax others
</details>

```kotlin
fun minCostConnectPoints(points: Array<IntArray>): Int {
    // TODO: Prim's O(n^2)
    return 0
}
```


---

### 13. Course Schedule IV — 🟡
**LeetCode #1462** | Solution: `DSA/graph/topological_sort/`

**Problem:** For each query [u, v]: is v reachable from u via prerequisites?

<details><summary>💡 Hint 1</summary>
Many queries → precompute transitive closure (Floyd-Warshall boolean).
</details>

<details><summary>💡 Hint 2</summary>
closure[i][j] |= closure[i][k] && closure[k][j] over all k.
</details>

<details><summary>💡 Hint 3</summary>
1. closure[pre][course] = true per edge. 2. Triple loop (n ≤ 100, fine). 3. Answer queries O(1)
</details>

```kotlin
fun checkIfPrerequisite(numCourses: Int, prerequisites: Array<IntArray>, queries: Array<IntArray>): List<Boolean> {
    // TODO: Boolean Floyd-Warshall closure
    return listOf()
}
```

---

### 14. Alien Dictionary — 🔴
**LeetCode #269** | Solution: `DSA/graph/topological_sort/`

**Problem:** Given a sorted alien word list, recover the character order.

<details><summary>💡 Hint 1</summary>
First differing chars of adjacent words give edge c1 → c2.
</details>

<details><summary>💡 Hint 2</summary>
Kahn's topo sort. Cycle or invalid prefix ("abc" before "ab") → "".
</details>

<details><summary>💡 Hint 3</summary>
1. Build graph + in-degree over unique chars. 2. BFS in-degree-0. 3. result < unique count → ""
</details>

```kotlin
fun alienOrder(words: List<String>): String {
    // TODO: Edge extraction + Kahn's
    return ""
}
```

---

### 15. Minimum Height Trees — 🟡
**LeetCode #310** | Solution: `DSA/graph/topological_sort/`

**Problem:** Nodes whose rooted tree has minimum height (the 1–2 centroids).

<details><summary>💡 Hint 1</summary>
Peel leaves level by level — like topo sort on an undirected graph.
</details>

<details><summary>💡 Hint 2</summary>
Remove degree-1 nodes each round; the last ≤ 2 surviving nodes are the answer.
</details>

<details><summary>💡 Hint 3</summary>
1. degrees + adjacency. 2. Queue leaves; strip; decrement neighbor degrees. 3. Return last layer
</details>

```kotlin
fun findMinHeightTrees(n: Int, edges: Array<IntArray>): List<Int> {
    // TODO: Leaf peeling
    return listOf()
}
```

---

## 🟡 THURSDAY, Dec 17 — Intervals

### 16. Merge Intervals — 🟡
**LeetCode #56** | Solution: `DSA/array/greedy/`

**Problem:** Merge all overlapping intervals.

<details><summary>💡 Hint 1</summary>
Sort by start; merge greedily.
</details>

<details><summary>💡 Hint 2</summary>
cur.start ≤ last.end → last.end = max(last.end, cur.end).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. 2. Extend-or-append. 3. Back to array
</details>

```kotlin
fun merge(intervals: Array<IntArray>): Array<IntArray> {
    // TODO: Sort by start, extend last
    return arrayOf()
}
```


---

### 17. Insert Interval — 🟡
**LeetCode #57** | Solution: `DSA/array/greedy/`

**Problem:** Insert a new interval into sorted non-overlapping intervals, merging as needed.

<details><summary>💡 Hint 1</summary>
Three phases: before → merged overlap → after.
</details>

<details><summary>💡 Hint 2</summary>
Overlap while new.start ≤ cur.end → new.start=min, new.end=max.
</details>

<details><summary>💡 Hint 3</summary>
1. Add intervals ending before new.start. 2. Merge overlapping into new. 3. Add new + the rest
</details>

```kotlin
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    // TODO: before / merge / after
    return arrayOf()
}
```

---

### 18. Non-overlapping Intervals — 🟡
**LeetCode #435** | Solution: `DSA/array/greedy/`

**Problem:** Minimum intervals to remove so all are non-overlapping.

<details><summary>💡 Hint 1</summary>
Greedy: keep intervals with the EARLIEST end (activity selection).
</details>

<details><summary>💡 Hint 2</summary>
Sort by end; removals = count of intervals starting before the last kept end.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort by end. prev=first. 2. cur.start >= prev.end → keep; else removals++. 3. keep−1... return removals
</details>

```kotlin
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    // TODO: Sort by end, greedy keep
    return 0
}
```

---

### 19. Meeting Rooms II — 🟡
**LeetCode #253** | Solution: `DSA/heap/`

**Problem:** Minimum number of conference rooms for a meeting list.

<details><summary>💡 Hint 1</summary>
Min-heap of end times; heap size = rooms in use.
</details>

<details><summary>💡 Hint 2</summary>
Sort by start; if earliest end ≤ start → reuse (pop); push current end.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort by start. 2. For each: reuse-or-push. 3. Final heap size = answer
</details>

```kotlin
fun minMeetingRooms(intervals: Array<IntArray>): Int {
    // TODO: Min-heap of end times
    return 0
}
```

---

### 20. Interval List Intersections — 🟡
**LeetCode #986** | Solution: `DSA/array/two_pointer/`

**Problem:** Intersections of two sorted closed-interval lists.

<details><summary>💡 Hint 1</summary>
Two pointers; intersection = [max(starts), min(ends)] if valid.
</details>

<details><summary>💡 Hint 2</summary>
Advance the pointer whose interval ENDS first.
</details>

<details><summary>💡 Hint 3</summary>
1. i=j=0. 2. lo=max, hi=min; lo≤hi → add. 3. a.end<b.end → i++ else j++
</details>

```kotlin
fun intervalIntersection(firstList: Array<IntArray>, secondList: Array<IntArray>): Array<IntArray> {
    // TODO: Two pointers over sorted lists
    return arrayOf()
}
```

---

## 🧪 SUNDAY, Dec 20 — Mock Test 9 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Difficulty | Time Limit |
|---|---------|-----------|-----------|
| 1 | Merge Intervals | 🟡 | 15 min |
| 2 | Network Delay Time | 🟡 | 25 min |
| 3 | Redundant Connection | 🟡 | 20 min |
| 4 | Course Schedule IV | 🟡 | 20 min |
| 5 | Alien Dictionary | 🔴 | 30 min |

**Part 2 — Review (1 hr):**
1. Score yourself: 🧠 = 2 pts, 💡 = 1 pt, 📖 = 0 pts (max 10)
2. Log results in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Re-solve failed problems from scratch
4. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## ✅ Week 9 Completion Checklist

- [ ] Mon: 5 shortest-path problems solved
- [ ] Tue: 5 Union-Find problems solved
- [ ] Wed: 5 topo-sort/MST problems solved
- [ ] Thu: 5 interval problems solved
- [ ] Sat: 2 weakest re-solved + mixed practice
- [ ] Sun: Mock Test 9 completed & logged
- [ ] Progress Tracker updated
