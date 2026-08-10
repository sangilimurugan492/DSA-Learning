package array.two_pointer_inplace.remove_element

/**
 * https://leetcode.com/problems/remove-element/
 *
 * Given an integer array nums and an integer val, remove all occurrences of val in nums
 * in-place. The order of the elements may be changed. Then return the number of elements
 * in nums which are not equal to val.
 *
 * The first k elements of nums should hold the elements not equal to val. The remaining
 * elements are beyond the first k and are ignored.
 *
 * Constraints:
 *   0 <= nums.length <= 100
 *   0 <= nums[i] <= 50
 *   0 <= val <= 100
 *
 * Example 1:
 *   Input:  nums = [3, 2, 2, 3], val = 3
 *   Output: 2, nums = [2, 2, _, _]
 *   Explanation: Your function should return k = 2, with the first two elements being 2.
 *
 * Example 2:
 *   Input:  nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2
 *   Output: 5, nums = [0, 1, 4, 0, 3, _, _, _]
 */
fun main() {
    val arr1 = intArrayOf(3, 2, 2, 3)
    println("BF: ${removeElementBF(arr1, 3)}, nums=${arr1.toList()}") // BF: 2, nums=[2, 2, 2, 3]

    val arr2 = intArrayOf(3, 2, 2, 3)
    println("OP: ${removeElementOP(arr2, 3)}, nums=${arr2.toList()}") // OP: 2, nums=[2, 2, 2, 3]

    val arr3 = intArrayOf(0, 1, 2, 2, 3, 0, 4, 2)
    println("OP: ${removeElementOP(arr3, 2)}, nums=${arr3.toList()}") // OP: 5
}

/**
 * Brute Force — Shift on Every Match
 *
 * When we find nums[i] == val, shift all elements to the right one position left.
 * This is O(N) per removal, giving O(N²) overall.
 *
 * Time Complexity:  O(N²) — each removal shifts up to N elements
 * Space Complexity: O(1)  — in-place
 */
fun removeElementBF(nums: IntArray, `val`: Int): Int {
    var size = nums.size
    var i = 0
    while (i < size) {
        if (nums[i] == `val`) {
            // Shift everything to the left
            for (j in i until size - 1) {
                nums[j] = nums[j + 1]
            }
            size-- // Reduce logical size
            // Do not increment i; check the new element at this position
        } else {
            i++
        }
    }
    return size
}

/**
 * Optimal — Two Pointers (Write Pointer)
 *
 * Key insight: We don't need to shift elements on every removal. Instead, use a "write
 * pointer" (`k`) that tracks where the next non-val element should go. Iterate through
 * the array with a "read pointer" (`i`). When nums[i] != val, copy it to nums[k] and
 * increment k.
 *
 * Trace for nums = [3, 2, 2, 3], val = 3:
 *
 *   k=0
 *   i=0: nums[0]=3 == val → skip
 *   i=1: nums[1]=2 != val → nums[0]=2, k=1
 *   i=2: nums[2]=2 != val → nums[1]=2, k=2
 *   i=3: nums[3]=3 == val → skip
 *
 *   Result: k=2, nums = [2, 2, 2, 3] (first 2 elements are valid) ✅
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1) — in-place
 */
fun removeElementOP(nums: IntArray, `val`: Int): Int {
    var k = 0 // write pointer — number of elements not equal to val
    for (i in nums.indices) {
        if (nums[i] != `val`) {
            nums[k] = nums[i]
            k++
        }
    }
    return k // New length of the "valid" portion of the array
}
