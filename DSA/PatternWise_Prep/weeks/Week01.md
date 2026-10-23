# 📅 Week 1 — Two Pointer + Two Pointer In-Place

> **Dates:** Oct 13 (Tue) – Oct 25 (Sun)
> **Pattern Focus:** Two Pointer (opposite ends / different speeds) + In-Place modification
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solution in `DSA/array/two_pointer/` and `DSA/array/two_pointer_inplace/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week01_Practice.md](../practice/Week01_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Tue | Oct 13 | #1–#5 (Warm-up: Easy Two Pointer) | 2 hrs |
| Wed | Oct 14 | #6–#10 (Easy → Medium) | 2 hrs |
| Thu | Oct 15 | #11–#17 (Medium + Trapping Rain Water) | 2 hrs |
| Sat | Oct 24 | #18–#25 (Two Pointer In-Place) | 2 hrs |
| Sun | Oct 25 | 🧪 Mock Test 1 + Review | 3 hrs |

---

## 🟢 TUESDAY, Oct 13 — Easy Two Pointer Warm-up

### 1. Two Sum (Sorted) — 🟡
**LeetCode #167** | Solution (check later): `DSA/array/two_pointer/TwoSumLevelTwoWithSortedArray.kt`

**Problem:** Given a **sorted** array and a target, return the indices (1-based) of the two numbers that add up to the target. Exactly one solution exists.

<details><summary>💡 Hint 1 (click to reveal)</summary>
Two Pointer — opposite ends (left = 0, right = n-1).
</details>

<details><summary>💡 Hint 2</summary>
If nums[left] + nums[right] > target → the right value is too big, move right--. If < target → left++. If equal → found.
</details>

<details><summary>💡 Hint 3 (steps)</summary>
1. left = 0, right = n - 1
2. While left < right: sum = nums[left] + nums[right]
3. sum == target → return [left+1, right+1]; sum > target → right--; sum < target → left++
</details>

```kotlin
fun twoSum(numbers: IntArray, target: Int): IntArray {
    // TODO: Two pointers from both ends
    return intArrayOf()
}
```

---

### 2. Valid Palindrome — 🟢
**LeetCode #125** | Solution: `DSA/array/two_pointer/ValidPalindrom.kt`

**Problem:** Given a string, return true if it is a palindrome considering only alphanumeric characters and ignoring cases.

<details><summary>💡 Hint 1</summary>
Two Pointer — opposite ends on the string.
</details>

<details><summary>💡 Hint 2</summary>
Skip non-alphanumeric chars on both sides. Compare lowercase forms. If mismatch → false.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0, right = s.length - 1
2. While left < right: skip non-alnum (left++ / right--)
3. If s[left].lowercaseChar() != s[right].lowercaseChar() → false, else left++, right--
</details>

```kotlin
fun isPalindrome(s: String): Boolean {
    // TODO: Skip non-alnum, compare lowercase from both ends
    return false
}
```

---

### 3. Is Subsequence — 🟢
**LeetCode #392** | Solution: `DSA/array/two_pointer/IsSubSequence.kt`

**Problem:** Given strings `s` and `t`, return true if `s` is a subsequence of `t` (chars of `s` appear in order in `t`, not necessarily contiguous).

<details><summary>💡 Hint 1</summary>
Two Pointer — one pointer per string, both moving forward.
</details>

<details><summary>💡 Hint 2</summary>
Pointer `i` on s, pointer `j` on t. When chars match → advance i. Always advance j. s is a subsequence if i reaches s.length.
</details>

<details><summary>💡 Hint 3</summary>
1. i = 0, j = 0
2. While i < s.length && j < t.length: if s[i] == t[j] → i++; j++
3. Return i == s.length
</details>

```kotlin
fun isSubsequence(s: String, t: String): Boolean {
    // TODO: Two forward pointers, advance i on match
    return false
}
```

---

### 4. Move Zeros to End — 🟢
**LeetCode #283** | Solution: `DSA/array/two_pointer/MoveZerosToEnd.kt`

**Problem:** Move all 0s in the array to the end while keeping the relative order of non-zero elements. Do it **in-place**.

<details><summary>💡 Hint 1</summary>
Two Pointer — slow/fast (write pointer / read pointer).
</details>

<details><summary>💡 Hint 2</summary>
Slow pointer = position to write the next non-zero. Fast pointer scans. When fast finds non-zero → swap with slow position.
</details>

<details><summary>💡 Hint 3</summary>
1. slow = 0
2. For fast in 0 until n: if nums[fast] != 0 → swap(nums[slow], nums[fast]), slow++
</details>

```kotlin
fun moveZeroes(nums: IntArray) {
    // TODO: Slow/fast pointers, swap non-zeros to front
}
```

---

### 5. Merge Sorted Array — 🟢
**LeetCode #88** | Solution: `DSA/array/two_pointer/MergeSortedArray.kt`

**Problem:** Merge `nums2` into `nums1` (which has extra space at the end). Both sorted. Merge in-place into nums1.

<details><summary>💡 Hint 1</summary>
Two Pointer — but fill from the **BACK** to avoid overwriting.
</details>

<details><summary>💡 Hint 2</summary>
Pointers: i = last element of nums1's valid part, j = last of nums2, k = last index of nums1. Place the LARGER of nums1[i], nums2[j] at k.
</details>

<details><summary>💡 Hint 3</summary>
1. i = m - 1, j = n - 1, k = m + n - 1
2. While j >= 0: if i >= 0 && nums1[i] > nums2[j] → nums1[k--] = nums1[i--], else nums1[k--] = nums2[j--]
</details>

```kotlin
fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int) {
    // TODO: Fill from back with 3 pointers
}
```

---

## 🟡 WEDNESDAY, Oct 14 — Easy → Medium

### 6. Remove Element in Array — 🟢
**LeetCode #27** | Solution: `DSA/array/two_pointer/RemoveElementInAnArray.kt`

**Problem:** Remove all occurrences of `val` in-place. Return the new length (elements beyond it don't matter).

<details><summary>💡 Hint 1</summary>
Two Pointer — slow/fast (write pointer).
</details>

<details><summary>💡 Hint 2</summary>
Write pointer only advances when the current element != val.
</details>

<details><summary>💡 Hint 3</summary>
1. write = 0
2. For each num: if num != val → nums[write++] = num
3. Return write
</details>

```kotlin
fun removeElement(nums: IntArray, `val`: Int): Int {
    // TODO: Copy non-val elements forward
    return 0
}
```

---

### 7. Intersection of Two Arrays II — 🟢
**LeetCode #350** | Solution: `DSA/array/two_pointer/IntersectionTwoArrayII.kt`

**Problem:** Return the intersection of two arrays including duplicates, as many times as they appear in both.

<details><summary>💡 Hint 1</summary>
Sort both arrays, then Two Pointer (opposite direction style — both forward).
</details>

<details><summary>💡 Hint 2</summary>
After sorting: advance the pointer with the smaller value. On match → add to result, advance both.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort nums1, nums2. i = 0, j = 0
2. nums1[i] < nums2[j] → i++; > → j++; == → add to result, i++, j++
</details>

```kotlin
fun intersect(nums1: IntArray, nums2: IntArray): IntArray {
    // TODO: Sort + two forward pointers
    return intArrayOf()
}
```

---

### 8. Three Sum — 🟡
**LeetCode #15** | Solution: `DSA/array/two_pointer/ThreeSum.kt`

**Problem:** Find all unique triplets in the array that sum to zero. No duplicate triplets.

<details><summary>💡 Hint 1</summary>
Sort + fix one element + Two Sum (sorted) with two pointers.
</details>

<details><summary>💡 Hint 2</summary>
For each i: find pairs (j, k) with j = i+1, k = n-1 such that nums[i] + nums[j] + nums[k] == 0. Skip duplicates for i, and after finding a match, skip duplicate j and k values.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort array
2. For i in 0 until n-2: skip if nums[i] == nums[i-1]; if nums[i] > 0 → break
3. j = i+1, k = n-1. sum < 0 → j++; sum > 0 → k--; sum == 0 → record, skip dups on both
</details>

```kotlin
fun threeSum(nums: IntArray): List<List<Int>> {
    // TODO: Sort, fix i, two-pointer j/k, skip duplicates
    return listOf()
}
```

---

### 9. Three Sum Closest — 🟡
**LeetCode #16** | Solution: `DSA/array/two_pointer/ThreeSumClosest.kt`

**Problem:** Find three numbers whose sum is closest to `target`. Return that sum.

<details><summary>💡 Hint 1</summary>
Same skeleton as 3Sum: sort + fix i + two pointers.
</details>

<details><summary>💡 Hint 2</summary>
Track the best (minimum |sum - target|) seen. Move pointers based on whether sum < target or > target.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. best = first triplet sum
2. For each i: j = i+1, k = n-1
3. sum == target → return target. |sum - target| < |best - target| → best = sum
4. sum < target → j++ else k--
</details>

```kotlin
fun threeSumClosest(nums: IntArray, target: Int): Int {
    // TODO: Sort, track closest sum, move pointers by comparison
    return 0
}
```

---

### 10. Four Sum — 🟡
**LeetCode #18** | Solution: `DSA/array/two_pointer/FourSum.kt`

**Problem:** Find all unique quadruplets that sum to `target`.

<details><summary>💡 Hint 1</summary>
Sort + two nested fixed loops (i, j) + two pointers (k, l).
</details>

<details><summary>💡 Hint 2</summary>
Fix i and j, then two-pointer between j+1 and n-1. Skip duplicates at every level. Use Long to avoid overflow.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. For i (skip dups), for j > i (skip dups)
2. k = j+1, l = n-1. sum < target → k++; > → l--; == → record, skip dups
</details>

```kotlin
fun fourSum(nums: IntArray, target: Int): List<List<Int>> {
    // TODO: Sort, two fixed loops + two pointers, skip duplicates
    return listOf()
}
```

---

## 🟡 THURSDAY, Oct 15 — Medium Two Pointer

### 11. Container With Most Water — 🟡
**LeetCode #11** | Solution: `DSA/array/two_pointer/ContainerWithMostWater.kt`

**Problem:** Given heights of vertical lines, pick two lines that form a container holding the most water.

<details><summary>💡 Hint 1</summary>
Two Pointer — opposite ends, always move the SHORTER line inward.
</details>

<details>💡 Hint 2</summary>
Area = min(height[left], height[right]) × (right - left). Moving the shorter pointer is the only way to possibly increase area.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0, right = n-1, maxArea = 0
2. While left < right: area = min(h[l], h[r]) * (r - l); maxArea = max(maxArea, area)
3. If h[left] < h[right] → left++ else right--
</details>

```kotlin
fun maxArea(height: IntArray): Int {
    // TODO: Opposite pointers, move shorter side
    return 0
}
```

---

### 12. Sort Colors (Dutch National Flag) — 🟡
**LeetCode #75** | Solution: `DSA/array/two_pointer/SortColorsDutchNationalFlag.kt`

**Problem:** Sort an array containing only 0s, 1s, 2s in-place, in one pass.

<details><summary>💡 Hint 1</summary>
Three Pointers — low, mid, high (Dutch National Flag).
</details>

<details><summary>💡 Hint 2</summary>
Invariant: [0..low-1] = 0s, [low..mid-1] = 1s, [high+1..end] = 2s. Process the element at mid.
</details>

<details><summary>💡 Hint 3</summary>
1. low = 0, mid = 0, high = n-1
2. nums[mid] == 0 → swap(low, mid), low++, mid++
3. nums[mid] == 1 → mid++
4. nums[mid] == 2 → swap(mid, high), high-- (don't advance mid!)
</details>

```kotlin
fun sortColors(nums: IntArray) {
    // TODO: Dutch flag: low/mid/high pointers
}
```

---

### 13. Backspace String Compare — 🟡
**LeetCode #844** | Solution: `DSA/array/two_pointer/BackspaceStringCompare.kt`

**Problem:** Given two strings containing '#' (backspace), return if they are equal after typing into empty text editors. O(1) space.

<details><summary>💡 Hint 1</summary>
Two Pointer — traverse both strings from the END backwards.
</details>

<details><summary>💡 Hint 2</summary>
Write a helper `nextValidIndex(s, i)` that skips backspaces: count '#' and move left accordingly. Compare valid chars.
</details>

<details><summary>💡 Hint 3</summary>
1. i = s.length-1, j = t.length-1
2. Loop: move i/j past backspaced chars (skip = 1 + number of # skipped)
3. If both < 0 → true. If one < 0 or chars differ → false
</details>

```kotlin
fun backspaceCompare(s: String, t: String): Boolean {
    // TODO: Scan from end, skip backspaced chars
    return false
}
```

---

### 14. Two Sum Less Than K — 🟡
**LeetCode #1099** | Solution: `DSA/array/two_pointer/TwoSumLessThanK.kt`

**Problem:** Return the largest sum of any pair that is strictly less than K, or -1.

<details><summary>💡 Hint 1</summary>
Sort + Two Pointer (opposite ends).
</details>

<details><summary>💡 Hint 2</summary>
If sum >= K → too big, right--. If sum < K → it's a candidate; record max and left++ to try bigger sums.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. left = 0, right = n-1, best = -1
2. sum < K → best = max(best, sum), left++; else right--
</details>

```kotlin
fun twoSumLessThanK(nums: IntArray, k: Int): Int {
    // TODO: Sort, track best sum under k
    return -1
}
```

---

### 15. K-Sum Pairs — 🟡
**LeetCode #1679** | Solution: `DSA/array/two_pointer/KSumPairs.kt`

**Problem:** Given nums and k, repeatedly pick pairs summing to k and remove them. Return max number of operations.

<details><summary>💡 Hint 1</summary>
Sort + Two Pointer (opposite ends).
</details>

<details><summary>💡 Hint 2</summary>
sum == k → count++, move both. sum < k → left++. sum > k → right--.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. left = 0, right = n-1, ops = 0
2. left < right: sum == k → ops++, left++, right--; < → left++; > → right--
</details>

```kotlin
fun maxOperations(nums: IntArray, k: Int): Int {
    // TODO: Sort, count pairs summing to k
    return 0
}
```

---

### 16. Boats to Save People — 🟡
**LeetCode #881** | Solution: `DSA/array/two_pointer/BoatsToSave.kt`

**Problem:** Each boat carries at most 2 people with weight limit `limit`. Return minimum boats.

<details><summary>💡 Hint 1</summary>
Sort + Two Pointer (opposite ends) — pair heaviest with lightest if possible.
</details>

<details><summary>💡 Hint 2</summary>
If lightest + heaviest <= limit → both go in one boat (left++). Heaviest always departs either way (right--).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. left = 0, right = n-1, boats = 0
2. While left <= right: boats++
3. If people[left] + people[right] <= limit → left++
4. right--
</details>

```kotlin
fun numRescueBoats(people: IntArray, limit: Int): Int {
    // TODO: Pair lightest with heaviest
    return 0
}
```

---

### 17. Trapping Rain Water — 🔴
**LeetCode #42** | Solution: `DSA/array/two_pointer/TrappingRainWaterI.kt`

**Problem:** Given elevation map heights, compute how much water can be trapped after rain.

<details><summary>💡 Hint 1</summary>
Two Pointer (opposite ends) + track leftMax and rightMax.
</details>

<details><summary>💡 Hint 2</summary>
Water at position i = min(leftMax, rightMax) - height[i]. Process the side with the SMALLER max — that side's max is guaranteed to be the limiting one.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0, right = n-1, leftMax = 0, rightMax = 0, water = 0
2. If height[left] < height[right]: leftMax = max(leftMax, height[left]); water += leftMax - height[left]; left++
3. Else: rightMax = max(rightMax, height[right]); water += rightMax - height[right]; right--
</details>

```kotlin
fun trap(height: IntArray): Int {
    // TODO: Two pointers, track leftMax/rightMax, add water on smaller side
    return 0
}
```

---

## 🟢 SATURDAY, Oct 24 — Two Pointer In-Place + Revision

### 18. Remove Duplicates from Sorted Array — 🟢
**LeetCode #26** | Solution: `DSA/array/two_pointer_inplace/RemoveDuplicateFromSortedArray.kt`

**Problem:** Remove duplicates in-place from a sorted array. Return new length. Each element appears once.

<details><summary>💡 Hint 1</summary>
Two Pointer — slow/fast (write/read).
</details>

<details><summary>💡 Hint 2</summary>
Slow points to the last unique element. When fast finds a different value → advance slow and copy.
</details>

<details><summary>💡 Hint 3</summary>
1. slow = 0
2. For fast in 1 until n: if nums[fast] != nums[slow] → nums[++slow] = nums[fast]
3. Return slow + 1
</details>

```kotlin
fun removeDuplicates(nums: IntArray): Int {
    // TODO: Slow/fast, copy unique values forward
    return 0
}
```

---

### 19. Remove Duplicates II (Keep Two) — 🟡
**LeetCode #80** | Solution: `DSA/array/two_pointer_inplace/RemoveDuplicateseTWO.kt`

**Problem:** Allow at most 2 duplicates in-place. Return new length.

<details><summary>💡 Hint 1</summary>
Same slow/fast pattern, but compare with the element TWO positions behind slow.
</details>

<details><summary>💡 Hint 2</summary>
If nums[fast] != nums[slow-1] (where slow counts valid length) → keep it. This allows up to 2 copies.
</details>

<details><summary>💡 Hint 3</summary>
1. If n <= 2 → return n
2. slow = 2 (first two always valid)
3. For fast in 2 until n: if nums[fast] != nums[slow-2] → nums[slow++] = nums[fast]
</details>

```kotlin
fun removeDuplicates(nums: IntArray): Int {
    // TODO: Compare with slow-2, allow two duplicates
    return 0
}
```

---

### 20. Replace with Greatest on Right — 🟡
**LeetCode #1299** | Solution: `DSA/array/two_pointer_inplace/ReplaceGratestElementOnRight.kt`

**Problem:** Replace every element with the greatest element among elements to its right. Last element becomes -1.

<details><summary>💡 Hint 1</summary>
Traverse from RIGHT to LEFT, tracking running max.
</details>

<details><summary>💡 Hint 2</summary>
Save the old value before overwriting; new value = running max so far; update running max with old value.
</details>

<details><summary>💡 Hint 3</summary>
1. maxSoFar = -1
2. For i from n-1 down to 0: temp = arr[i]; arr[i] = maxSoFar; maxSoFar = max(maxSoFar, temp)
</details>

```kotlin
fun replaceElements(arr: IntArray): IntArray {
    // TODO: Right-to-left scan with running max
    return arr
}
```

---

### 21. Sorted Squares — 🟡
**LeetCode #977** | Solution: `DSA/array/two_pointer_inplace/SortedSquares.kt`

**Problem:** Sorted array (may contain negatives). Return squares sorted.

<details><summary>💡 Hint 1</summary>
Largest squares come from either END of the array. Fill result from the BACK.
</details>

<details><summary>💡 Hint 2</summary>
Two Pointer (opposite ends). Compare |nums[left]| and |nums[right]|; larger square goes at the current back position.
</details>

<details><summary>💡 Hint 3</summary>
1. result = IntArray(n), pos = n-1, left = 0, right = n-1
2. While left <= right: if abs(nums[left]) > abs(nums[right]) → result[pos--] = nums[left]², left++ else result[pos--] = nums[right]², right--
</details>

```kotlin
fun sortedSquares(nums: IntArray): IntArray {
    // TODO: Fill result from back using absolute comparison
    return intArrayOf()
}
```

---

### 22. Rotate Array — 🟡
**LeetCode #189** | Solution: `DSA/array/two_pointer_inplace/RotateArray.kt`

**Problem:** Rotate array right by k steps in-place.

<details><summary>💡 Hint 1</summary>
Reversal trick: reverse whole array, then reverse first k, then reverse rest.
</details>

<details><summary>💡 Hint 2</summary>
k = k % n. Reverse(0, n-1) → Reverse(0, k-1) → Reverse(k, n-1).
</details>

<details><summary>💡 Hint 3</summary>
1. Write helper reverse(arr, i, j) swapping ends inward
2. Apply the three reversals in order
</details>

```kotlin
fun rotate(nums: IntArray, k: Int) {
    // TODO: Three reversals
}
```

---

### 23. Next Permutation — 🟡
**LeetCode #31** | Solution: `DSA/array/two_pointer_inplace/NextPermutation.kt`

**Problem:** Rearrange to the next lexicographically greater permutation. If none, sort ascending.

<details><summary>💡 Hint 1</summary>
Scan from the right: find first index `i` where nums[i] < nums[i+1] (the "dip").
</details>

<details><summary>💡 Hint 2</summary>
If dip found: find rightmost element > nums[i], swap them. Then reverse the suffix after i.
</details>

<details><summary>💡 Hint 3</summary>
1. i = n-2; while i >= 0 && nums[i] >= nums[i+1] → i--
2. If i >= 0: j = n-1; while nums[j] <= nums[i] → j--; swap(i, j)
3. Reverse from i+1 to n-1
</details>

```kotlin
fun nextPermutation(nums: IntArray) {
    // TODO: Find dip, swap with next bigger, reverse suffix
}
```

---

### 24. Sort Colors — 🟡
**LeetCode #75** | Solution: `DSA/array/two_pointer_inplace/SortColors.kt`

**Problem:** (Same as #12 — re-solve from scratch today as revision. If you solved #12 without hints, this should be 🧠 again.)

<details><summary>💡 Hint 1</summary>
Dutch National Flag — low/mid/high.
</details>

<details><summary>💡 Hint 2</summary>
0s before low, 1s between low and mid, 2s after high.
</details>

<details><summary>💡 Hint 3</summary>
mid==0 → swap(low,mid), both++. mid==1 → mid++. mid==2 → swap(mid,high), high-- only.
</details>

```kotlin
fun sortColors(nums: IntArray) {
    // TODO: Re-solve from scratch
}
```

---

### 25. Remove Element — 🟢
**LeetCode #27** | Solution: `DSA/array/two_pointer_inplace/RemoveElement.kt`

**Problem:** (Same as #6 — re-solve from scratch as revision.)

<details><summary>💡 Hint 1</summary>
Write pointer pattern.
</details>

<details><summary>💡 Hint 2</summary>
Copy forward only elements != val.
</details>

<details><summary>💡 Hint 3</summary>
write = 0; for each num != val → nums[write++] = num; return write.
</details>

```kotlin
fun removeElement(nums: IntArray, `val`: Int): Int {
    // TODO: Re-solve from scratch
    return 0
}
```

---

## 🧪 SUNDAY, Oct 25 — Mock Test 1 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Difficulty | Time Limit |
|---|---------|-----------|-----------|
| 1 | Valid Palindrome | 🟢 | 15 min |
| 2 | Three Sum | 🟡 | 30 min |
| 3 | Container With Most Water | 🟡 | 25 min |
| 4 | Sort Colors | 🟡 | 20 min |
| 5 | Trapping Rain Water | 🔴 | 30 min |

**Part 2 — Review (1 hr):**
1. Score yourself: 🧠 = 2 pts, 💡 = 1 pt, 📖 = 0 pts (max 10)
2. Log results in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Re-solve any failed problems from scratch
4. Update [Progress_Tracker.md](../Progress_Tracker.md) statuses
5. Add weak patterns to the re-practice queue

---

## ✅ Week 1 Completion Checklist

- [x] Tue: 5 easy two-pointer problems solved
- [x] Wed: 5 problems (3Sum family) solved
- [x] Thu: 7 problems incl. Trapping Rain Water
- [ ] Sat: 8 in-place problems solved
- [ ] Sun: Mock Test 1 completed & logged
- [ ] Progress Tracker updated
- [ ] Weak patterns identified for Week 2 revision
