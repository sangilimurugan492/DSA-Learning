package array.two_pointer.intersection_two_array_ii

/**
 * https://leetcode.com/problems/intersection-of-two-arrays-ii/
 *
 * Given two integer arrays nums1 and nums2, return an array of their intersection.
 * Each element in the result must appear as many times as it shows in both arrays.
 *
 * Example: nums1 = [1,2,2,1], nums2 = [2,2] → [2,2]
 *
 * Three approaches:
 * 1. Brute Force: For each element in nums2, scan nums1 for a match
 * 2. HashMap: Count frequencies of nums1, match against nums2
 * 3. Sort + Two Pointer: Both sorted, advance matching pointers
 */
fun main() {
    println("Brute Force:")
    intersectBF(intArrayOf(1, 2, 2, 1), intArrayOf(2, 2)).forEach { print(it) }  // 22
    println()
    println("HashMap:")
    intersect(intArrayOf(1, 2, 2, 1), intArrayOf(2, 2)).forEach { print(it) }    // 22
    println()
    println("Sort + Two Pointer:")
    intersectSort(intArrayOf(1, 2, 2, 1), intArrayOf(2, 2)).forEach { print(it) } // 22
    println()
}

/**
 * Brute Force: For each element in nums2, scan nums1 for an unused match.
 * Use a 'used' array to avoid reusing the same element.
 *
 * Step-by-step:
 * 1. Create a 'used' boolean array for nums1 (same size).
 * 2. For each element in nums2:
 *    a. Scan nums1 for a matching element that hasn't been used.
 *    b. If found → mark as used, add to result.
 * 3. Return result.
 *
 * Walkthrough: nums1 = [1,2,2,1], nums2 = [2,2]
 *
 *   nums2[0]=2: scan nums1 → index0(1)✗, index1(2)✓ used[1]=true, result=[2]
 *   nums2[1]=2: scan nums1 → index0(1)✗, index1 used, index2(2)✓ used[2]=true, result=[2,2]
 *
 * Result: [2,2] ✅
 *
 * Time Complexity:  O(N × M) — for each element in nums2, scan nums1
 * Space Complexity: O(N)     — used array
 */
fun intersectBF(nums1: IntArray, nums2: IntArray): IntArray {
    val used = BooleanArray(nums1.size)
    val result = mutableListOf<Int>()

    for (num in nums2) {
        for (i in nums1.indices) {
            if (!used[i] && nums1[i] == num) {
                used[i] = true
                result.add(num)
                break
            }
        }
    }

    return result.toIntArray()
}

/**
 * HashMap (Optimal 1): Count nums1 frequencies, then match against nums2.
 *
 * Step-by-step:
 * 1. Build a frequency map from nums1.
 * 2. For each element in nums2:
 *    a. If it exists in the map with count > 0 → add to result, decrement count.
 *
 * Time Complexity:  O(N + M)
 * Space Complexity: O(N)
 */
fun intersect(nums1: IntArray, nums2: IntArray): IntArray {
    val freqMap = mutableMapOf<Int, Int>()

    // Count frequencies of nums1
    for (n in nums1) {
        freqMap[n] = freqMap.getOrDefault(n, 0) + 1
    }

    // Collect intersection from nums2
    val result = IntArray(nums2.size)
    var k = 0
    for (num in nums2) {
        val count = freqMap.getOrDefault(num, 0)
        if (count > 0) {
            result[k++] = num
            freqMap[num] = count - 1
        }
    }

    return result.copyOf(k)
}

/**
 * Sort + Two Pointer (Optimal 2): Sort both arrays, then use two pointers.
 *
 * Step-by-step:
 * 1. Sort both arrays.
 * 2. Set p1 = 0, p2 = 0.
 * 3. While p1 < nums1.size and p2 < nums2.size:
 *    a. If nums1[p1] == nums2[p2] → match! Add to result, advance both.
 *    b. If nums1[p1] < nums2[p2] → p1++ (need bigger in nums1).
 *    c. If nums1[p1] > nums2[p2] → p2++ (need bigger in nums2).
 *
 * Time Complexity:  O(N log N + M log M) — dominated by sorting
 * Space Complexity: O(1) — ignoring output
 */
fun intersectSort(nums1: IntArray, nums2: IntArray): IntArray {
    nums1.sort()
    nums2.sort()

    val result = mutableListOf<Int>()
    var p1 = 0
    var p2 = 0

    while (p1 < nums1.size && p2 < nums2.size) {
        when {
            nums1[p1] == nums2[p2] -> {
                result.add(nums1[p1])
                p1++
                p2++
            }
            nums1[p1] < nums2[p2] -> p1++
            else -> p2++
        }
    }

    return result.toIntArray()
}
