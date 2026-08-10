package array.voting_floyd.find_dupplicate_number

/**
 * https://leetcode.com/problems/find-the-duplicate-number/
 *
 * Given an array of integers nums containing n + 1 integers where each integer is in the
 * range [1, n] inclusive. There is only **one repeated number** in nums. Return this
 * repeated number.
 *
 * You must solve the problem **without modifying the array** and using only **constant
 * extra space**.
 *
 * Constraints:
 *   1 <= n <= 10^5
 *   nums.length == n + 1
 *   1 <= nums[i] <= n
 *   There is only one repeated number (but it could repeat more than once).
 *
 * Example 1:
 *   Input:  nums = [1, 3, 4, 2, 2]
 *   Output: 2
 *
 * Example 2:
 *   Input:  nums = [3, 1, 3, 4, 2]
 *   Output: 3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */
fun main() {
    println(findDuplicateBruteForce(intArrayOf(1, 3, 4, 2, 2))) // 2
    println(findDuplicateSort(intArrayOf(1, 3, 4, 2, 2)))      // 2
    println(findDuplicateFloyd(intArrayOf(1, 3, 4, 2, 2)))     // 2
    println(findDuplicateFloyd(intArrayOf(3, 1, 3, 4, 2)))      // 3
}

/**
 * Brute Force — Compare All Pairs
 *
 * For each element, compare it with every other element. If a match is found, return it.
 *
 * Time Complexity:  O(N²) — compare every pair
 * Space Complexity: O(1)
 */
fun findDuplicateBruteForce(nums: IntArray): Int {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] == nums[j]) return nums[i]
        }
    }
    return -1
}

/**
 * Better — Sort and Check Adjacent
 *
 * Sort the array, then check adjacent elements. If two adjacent elements are equal,
 * that's the duplicate.
 *
 * Note: This modifies the array, which violates the problem's constraint.
 *
 * Time Complexity:  O(N log N) — dominated by sorting
 * Space Complexity: O(1) or O(N) — depending on sort implementation
 */
fun findDuplicateSort(nums: IntArray): Int {
    nums.sort()
    for (i in 1 until nums.size) {
        if (nums[i] == nums[i - 1]) return nums[i]
    }
    return -1
}

/**
 * Optimal — Floyd's Cycle Detection (Tortoise and Hare)
 *
 * Key insight: Treat the array as a linked list where index i "points to" nums[i].
 * Since values are in [1, n] and there are n+1 elements, this creates a linked list
 * with a cycle. The entrance to the cycle is the duplicate number.
 *
 * Why? Because two different indices point to the same value (the duplicate), creating
 * a cycle. The entry point of the cycle is the duplicate.
 *
 * Phase 1 — Find meeting point:
 *   - slow moves one step: slow = nums[slow]
 *   - fast moves two steps: fast = nums[nums[fast]]
 *   - They meet inside the cycle.
 *
 * Phase 2 — Find cycle entrance (the duplicate):
 *   - Reset slow to nums[0], keep fast at meeting point.
 *   - Move both one step at a time until they meet — that's the cycle entrance.
 *
 * Trace for nums = [1, 3, 4, 2, 2]:
 *
 *   Treat as linked list: index → nums[index]
 *     0 → 1 → 3 → 2 → 4 → 2 → 4 → 2 → ...  (cycle between indices 2 and 4)
 *
 *   Phase 1 — Find meeting point (slow=1 step, fast=2 steps):
 *     Start: slow = nums[0] = 1, fast = nums[nums[0]] = nums[1] = 3
 *     Step 1: slow = nums[1] = 3, fast = nums[nums[3]] = nums[2] = 4
 *     Step 2: slow = nums[3] = 2, fast = nums[nums[4]] = nums[2] = 4
 *     Step 3: slow = nums[2] = 4, fast = nums[nums[4]] = nums[2] = 4 → meet at 4
 *
 *   Phase 2 — Find cycle entrance:
 *     Reset slow = nums[0] = 1, fast stays at 4
 *     Step 1: slow = nums[1] = 3, fast = nums[4] = 2
 *     Step 2: slow = nums[3] = 2, fast = nums[2] = 4
 *     Step 3: slow = nums[2] = 4, fast = nums[4] = 2
 *     Step 4: slow = nums[4] = 2, fast = nums[2] = 4
 *     ...
 *
 *   Actually, the standard algorithm resets slow to 0 (not nums[0]):
 *     slow = 0, fast = 4 (meeting point from Phase 1)
 *     Step 1: slow = nums[0] = 1, fast = nums[4] = 2
 *     Step 2: slow = nums[1] = 3, fast = nums[2] = 4
 *     Step 3: slow = nums[3] = 2, fast = nums[4] = 2 → meet at 2!
 *
 *   The meeting point is value 2 — the duplicate! ✅
 *
 *   Note: In the code below, slow is set to nums[0] (not 0). This is the common variant
 *   that also works. Both approaches find the cycle entrance.

 *
 * Time Complexity:  O(N) — both phases are O(N)
 * Space Complexity: O(1) — only two pointers
 */
fun findDuplicateFloyd(nums: IntArray): Int {
    var slow = nums[0]
    var fast = nums[0]

    // Phase 1: Find meeting point inside the cycle.
    do {
        slow = nums[slow]
        fast = nums[nums[fast]]
    } while (slow != fast)

    // Phase 2: Find cycle entrance (the duplicate).
    slow = nums[0]
    while (slow != fast) {
        slow = nums[slow]
        fast = nums[fast]
    }
    return slow
}
