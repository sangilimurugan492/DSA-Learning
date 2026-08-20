# Koko Eating Bananas — Detailed Explanation

> **LeetCode #875** | [Problem Link](https://leetcode.com/problems/koko-eating-bananas/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Binary search on answer — asked at Google, Amazon, Meta)  
> **Topic:** Binary Search on Answer Space

---

## 📋 Problem Statement

Koko loves to eat bananas. There are `n` piles of bananas. Koko can decide her per-hour eating speed `k`. Each hour, she picks a pile and eats `k` bananas from it (or all if fewer than `k`). Return the **minimum integer `k`** such that she can eat all bananas within `h` hours.

### Examples

| piles | h | Output | Explanation |
|-------|---|--------|-------------|
| `[3,6,7,11]` | 8 | 4 | At speed 4: 1+2+2+3=8 hours |
| `[30,11,23,4,20]` | 5 | 30 | Must eat 1 pile/hour → speed = max |
| `[30,11,23,4,20]` | 6 | 23 | At speed 23: 2+1+1+1+1=6 hours |

---

## 🧩 Method 1: Linear Scan — O(max(piles) × n)

### Core Idea

Try every speed from 1 to `max(piles)`. The first speed that finishes all piles within `h` hours is the answer.

### Step-by-Step

1. For each speed `k` from 1 to `max(piles)`:
   - Calculate total hours = `Σ ceil(pile / k)` for all piles.
   - If total hours ≤ `h` → return `k`.
2. Return `max(piles)` (worst case).

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(max(piles) × n) | For each speed, scan all piles |
| **Space** | O(1) | Constant variables |

---

## 🧩 Method 2: Binary Search on Answer — O(n × log(max(piles)))

### Core Idea

The answer lies in `[1, max(piles)]`. Binary search this range: if speed `k` works, try smaller; if not, try larger.

### Key Insight

> The speed-to-hours relationship is **monotonic**: higher speed → fewer hours. If speed `k` works, all speeds > `k` also work. We want the **minimum** working speed. This is the "binary search on answer space" pattern — we're not searching an array, we're searching the **range of possible answers**.

### Step-by-Step

1. Set `left = 1`, `right = max(piles)`.
2. While `left < right`:
   - `mid = left + (right - left) / 2`.
   - Calculate hours needed at speed `mid`.
   - If hours ≤ `h` → `mid` works → `right = mid` (try smaller).
   - Else → `mid` too slow → `left = mid + 1` (try faster).
3. Return `left`.

---

## 🔍 Huge 10-Element Array Walkthrough

### Setup

- **Piles:** `[3, 6, 7, 11, 12, 15, 20, 25, 30, 35]` (10 elements)
- **h = 15** hours
- **Search range:** `[1, 35]`
- **Expected answer:** `15` (minimum speed to finish in 15 hours)

### Step-by-Step Binary Search

#### STEP 1: `left=1, right=35, mid=18`

- Hours = `ceil(3/18)+ceil(6/18)+...+ceil(35/18)` = `1+1+1+1+1+1+2+2+2+2` = **14**
- `14 ≤ 15` → works! → try smaller → `right = 18`

#### STEP 2: `left=1, right=18, mid=9`

- Hours = `1+1+1+2+2+2+3+3+4+4` = **23**
- `23 > 15` → too slow → need faster → `left = 10`

#### STEP 3: `left=10, right=18, mid=14`

- Hours = `1+1+1+1+1+2+2+2+3+3` = **17**
- `17 > 15` → too slow → need faster → `left = 15`

#### STEP 4: `left=15, right=18, mid=16`

- Hours = `1+1+1+1+1+1+2+2+2+3` = **15**
- `15 ≤ 15` → works! → try smaller → `right = 16`

#### STEP 5: `left=15, right=16, mid=15`

- Hours = `1+1+1+1+1+1+2+2+2+3` = **15**
- `15 ≤ 15` → works! → try smaller → `right = 15`

#### STEP 6: `left=15, right=15` → loop ends → return **15** ✅

### Verification

| Speed | Hours | ≤ 15? |
|:-----:|:-----:|:-----:|
| 14 | 1+1+1+1+1+2+2+2+3+3 = 17 | ❌ |
| 15 | 1+1+1+1+1+1+2+2+2+3 = 15 | ✅ |

### Summary Table

| Step | left | right | mid (speed) | hours | ≤ h? | Action |
|:----:|:----:|:-----:|:-----------:|:-----:|:----:|:------:|
| 1 | 1 | 35 | 18 | 14 | ✅ | right = 18 |
| 2 | 1 | 18 | 9 | 23 | ❌ | left = 10 |
| 3 | 10 | 18 | 14 | 17 | ❌ | left = 15 |
| 4 | 15 | 18 | 16 | 15 | ✅ | right = 16 |
| 5 | 15 | 16 | 15 | 15 | ✅ | right = 15 |
| 6 | 15 | 15 | — | — | left == right | return **15** ✅ |

> **Key observation:** Only **5 iterations** to search a range of 35 possible speeds. That's O(log(max(piles))) — binary search on the answer space!

### Code

```kotlin
fun minEatingSpeed(piles: IntArray, h: Int): Int {
    var left = 1
    var right = piles.max()
    while (left < right) {
        val mid = left + (right - left) / 2
        if (canFinish(piles, mid, h)) right = mid
        else left = mid + 1
    }
    return left
}

fun canFinish(piles: IntArray, k: Int, h: Int): Boolean {
    var hours = 0L
    for (pile in piles) hours += (pile + k - 1) / k  // ceil division
    return hours <= h
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(n × log(max(piles))) | Binary search with O(n) check per step |
| **Space** | O(1) | Constant variables |

---

## 📊 Comparison Table

| Aspect | Linear Scan | Binary Search |
|--------|-------------|---------------|
| **Time** | O(max(piles) × n) | O(n × log(max(piles))) |
| **Space** | O(1) | O(1) |
| **Efficient for large piles?** | ❌ | ✅ |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Binary search on answer space:** When the answer is in a range [min, max] and the feasibility function is monotonic, binary search the answer.
2. **Monotonicity is key:** Higher speed → fewer hours. This monotonic relationship is what makes binary search possible.
3. **Ceiling division:** `ceil(a/b) = (a + b - 1) / b` — avoids floating point.
4. **Pattern:** This is the "binary search on answer" pattern — extends to Split Array Largest Sum, Capacity To Ship, Min Days for Bouquets, etc.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Koko Eating Bananas | [#875](https://leetcode.com/problems/koko-eating-bananas/) | Medium |
| Capacity To Ship Packages | [#810](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/) | Medium |
| Split Array Largest Sum | [#410](https://leetcode.com/problems/split-array-largest-sum/) | Hard |
| Min Days for Bouquets | [#1482](https://leetcode.com/problems/minimum-number-of-days-to-make-m-bouquets/) | Medium |
