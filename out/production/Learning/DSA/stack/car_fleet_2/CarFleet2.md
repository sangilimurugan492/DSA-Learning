# Car Fleet II — Detailed Explanation

> **LeetCode #1776** | [Problem Link](https://leetcode.com/problems/car-fleet-ii/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard, monotonic stack)  
> **Topic:** Stack, Math, Simulation

---

## 📋 Problem Statement

N cars on a one-lane road, each with `position[i]` and `speed[i]`. Cars cannot pass; a faster car catches a slower car ahead and they merge into a fleet (same position & speed). Return an array where `answer[i]` = time car `i` collides with the car in front, or `-1` if it never collides.

### Example

`cars = [[1,2],[2,4],[4,1],[7,4]]` → `[3.0, 0.6667, -1.0, -1.0]`

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Process right-to-left. For each car `i`, scan all cars `j` ahead. If car `i` is faster, compute collision time. If car `j` hasn't merged before that time (`res[j] == -1` or `collisionTime ≤ res[j]`), that's the answer for `i`.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | For each car, scan all cars ahead |
| **Space** | O(N) | Result array |

---

## 🧩 Method 2: Monotonic Stack — O(N)

### Core Idea

Process right-to-left (front to back). Stack stores `[position, speed, collisionTime]` for cars ahead. For each car:

1. If current speed ≤ top speed → can never catch → pop (check next fleet).
2. Compute collision time with top. If top never merges (`time=-1`) or we catch top before top merges → record collision, stop.
3. If top merges before we catch it → pop (top is now part of a fleet, its speed changed) → check next car on stack.

### Key Insight

> Collision time = `(posAhead - posBehind) / (speedBehind - speedAhead)`. If `collisionTime ≤ top's collisionTime` → we catch top before it merges. If `collisionTime > top's collisionTime` → top merges first → pop & retry. A car ahead that merges into a fleet before we catch it is no longer at its original speed — we must look past it.

### Dry Run — `cars = [[1,2],[2,4],[4,1],[7,4]]`

Process right-to-left (i = 3 → 0):

| i | pos | spd | Stack (before) | Collision Time | Action | res[i] |
|:-:|:---:|:---:|:---------------|:--------------|:-------|:------:|
| 3 | 7 | 4 | [] | — | push [7,4,-1] | -1.0 |
| 2 | 4 | 1 | [[7,4,-1]] | spd(1) ≤ spd(4) → pop | push [4,1,-1] | -1.0 |
| 1 | 2 | 4 | [[4,1,-1]] | (4-2)/(4-1)=0.667, topTime=-1 → catch | push [2,4,0.667] | 0.667 |
| 0 | 1 | 2 | [[2,4,0.667]] | spd(2) ≤ spd(4) → pop; next top [4,1,-1]: (4-1)/(2-1)=3.0, topTime=-1 → catch | push [1,2,3.0] | 3.0 |

**Why car 0 pops car 1 first:** Car 0 (spd=2) is slower than car 1 (spd=4) → can never catch car 1 at its original speed. But car 1 merges with car 2 at t=0.667 (becoming speed 1). So we pop car 1 and check car 2 (now speed 1). Car 0 (spd=2) catches car 2 (spd=1): time = (4-1)/(2-1) = 3.0. Since car 2 never merges (topTime=-1), car 0 catches it at t=3.0. ✅

✅ **Result: [3.0, 0.667, -1.0, -1.0]**

### Code

```kotlin
fun getCollisionTimesStack(cars: Array<IntArray>): DoubleArray {
    val n = cars.size
    val res = DoubleArray(n) { -1.0 }
    val stack = ArrayDeque<IntArray>()  // [position, speed, collisionTime]

    for (i in n - 1 downTo 0) {
        val pos = cars[i][0]
        val spd = cars[i][1]

        while (stack.isNotEmpty()) {
            val top = stack.last()
            val topPos = top[0]
            val topSpd = top[1]
            val topTime = top[2]

            // Can't catch top (same speed or slower) → pop, check next fleet
            if (spd <= topSpd) {
                stack.removeLast()
                continue
            }

            // Compute collision time with top
            val collisionTime = (topPos - pos).toDouble() / (spd - topSpd)

            // If top never merges, or we catch top before it merges with its front
            if (topTime < 0 || collisionTime <= topTime) {
                res[i] = collisionTime
                break
            }

            // Top merges before we catch it → pop, check next car on stack
            stack.removeLast()
        }

        // Push current car with its collision time (or -1 if none)
        stack.addLast(intArrayOf(pos, spd, if (res[i] > 0) res[i] else -1.0))
    }

    return res
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each car pushed/popped at most once |
| **Space** | O(N) | Stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Collision time:** `(posAhead - posBehind) / (speedBehind - speedAhead)` — the key formula.
2. **Process right-to-left:** Cars ahead are processed first so we know their merge times.
3. **Pop condition 1:** If current speed ≤ top speed → can never catch → pop.
4. **Pop condition 2:** If top merges before we catch it (`collisionTime > topTime`) → pop (top's speed changed).
5. **Push with collision time:** Each car on the stack carries its own collision time so cars behind know when it disappears.
6. **Pattern:** Monotonic stack — same family as Car Fleet I, Daily Temperatures, Next Greater Element.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Car Fleet | [#853](https://leetcode.com/problems/car-fleet/) | Medium |
| Car Fleet II | [#1776](https://leetcode.com/problems/car-fleet-ii/) | Hard |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Next Greater Element | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
