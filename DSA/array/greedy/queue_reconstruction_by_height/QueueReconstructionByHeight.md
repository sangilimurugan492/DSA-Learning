# Queue Reconstruction by Height — Detailed Explanation

> **LeetCode #406** | [Problem Link](https://leetcode.com/problems/queue-reconstruction-by-height/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)  
> **Topic:** Greedy, Sorting

---

## 📋 Problem Statement

People array `[h, k]` where h=height, k=people in front with height ≥ h. Reconstruct the queue.

### Example

`[[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]` → `[[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]`

---

## 🧩 Method 1: Brute Force — O(N³)

Try all permutations, validate each. Exponential — not practical.

---

## 🧩 Method 2: Greedy Insertion — O(N²)

### Core Idea

Sort tallest first (height DESC, k ASC). Insert each person at index k.

### Key Insight

> When inserting a shorter person, they don't affect k of taller people already placed. The k value tells us exactly where to insert — there are already k taller people in the list.

### Dry Run — `[[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]`

**Step 1: Sort** by height DESC, k ASC: `[7,0], [7,1], [6,1], [5,0], [5,2], [4,4]`

**Step 2: Insert at index k:**

| Insert | k | Result |
|:------:|:-:|:------:|
| [7,0] | 0 | [[7,0]] |
| [7,1] | 1 | [[7,0],[7,1]] |
| [6,1] | 1 | [[7,0],[6,1],[7,1]] |
| [5,0] | 0 | [[5,0],[7,0],[6,1],[7,1]] |
| [5,2] | 2 | [[5,0],[7,0],[5,2],[6,1],[7,1]] |
| [4,4] | 4 | [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]] |

✅ **Result: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]**

### Code

```kotlin
fun reconstructQueue(people: Array<IntArray>): Array<IntArray> {
    people.sortWith(compareBy({ -it[0] }, { it[1] }))
    val result = mutableListOf<IntArray>()
    for (person in people) result.add(person[1], person)
    return result.toTypedArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | N insertions, each O(N) |
| **Space** | O(N) | Result list |

---

## 🔑 Key Takeaways

1. **Sort tallest first:** Taller people are placed first — shorter people don't affect their k.
2. **Insert at k:** The k value IS the insertion index — guarantees exactly k taller people in front.
3. **Why it works:** Inserting a shorter person at index k doesn't change the count of taller people in front of already-placed taller people.
4. **Pattern:** Greedy insertion — extends to Insert Interval, Meeting Rooms.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Queue Reconstruction | [#406](https://leetcode.com/problems/queue-reconstruction-by-height/) | Medium |
| Insert Interval | [#57](https://leetcode.com/problems/insert-interval/) | Medium |
| Meeting Rooms | [#252](https://leetcode.com/problems/meeting-rooms/) | Easy |
| Russian Doll Envelopes | [#354](https://leetcode.com/problems/russian-doll-envelopes/) | Hard |
