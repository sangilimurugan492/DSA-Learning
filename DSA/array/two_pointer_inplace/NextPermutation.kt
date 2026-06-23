package array.two_pointer_inplace

/**
 * https://leetcode.com/problems/next-permutation/
 *
 * The next permutation of an array of integers is the next lexicographically greater permutation.
 * If such arrangement is not possible, the array must be rearranged as the lowest possible order
 * (sorted in ascending order). The replacement must be in place and use only constant extra space.
 *
 * Example 1:
 *
 * Input: nums = [1,2,3]
 * Output: [1,3,2]
 *
 * Example 2:
 *
 * Input: nums = [3,2,1]
 * Output: [1,2,3]
 *
 * Example 3:
 *
 * Input: nums = [1,1,5]
 * Output: [1,5,1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta — classic in-place problem)
 *
 * Key Insight: 3-step algorithm:
 * 1. Find the first decreasing element from the right (pivot)
 * 2. Find the smallest element larger than pivot from the right, swap them
 * 3. Reverse everything after the pivot position
 */
fun main() {
    val arr1 = intArrayOf(1, 2, 3)
    nextPermutation(arr1)
    println(arr1.toList())

    val arr2 = intArrayOf(3, 2, 1)
    nextPermutation(arr2)
    println(arr2.toList())

    val arr3 = intArrayOf(1, 1, 5)
    nextPermutation(arr3)
    println(arr3.toList())

    val arr4 = intArrayOf(1, 3, 2)
    nextPermutation(arr4)
    println(arr4.toList())
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Algorithm:
 *
 * Step 1: Find pivot — rightmost index i where nums[i] < nums[i+1]
 *   (This is the first position from the right where we can make the number bigger)
 *
 * Step 2: If pivot found, find rightmost j where nums[j] > nums[i], swap nums[i] and nums[j]
 *   (Swap with the smallest larger element)
 *
 * Step 3: Reverse nums from i+1 to end
 *   (The suffix was in decreasing order; reversing makes it the smallest possible)
 *
 * If no pivot found (entire array is decreasing), just reverse the whole array.
 *
 * Trace for [1,3,2]:
 * Step 1: i=0 (nums[0]=1 < nums[1]=3) ← rightmost decreasing from right
 * Step 2: j=2 (nums[2]=2 > nums[0]=1) ← rightmost element > pivot
 *         Swap: [2,3,1]
 * Step 3: Reverse from i+1=1: [2,1,3]
 * Result: [2,1,3] ✅ (next after 132 is 213)
 */
fun nextPermutation(nums: IntArray) {
    val n = nums.size

    // Step 1: Find the first decreasing element from the right
    var i = n - 2
    while (i >= 0 && nums[i] >= nums[i + 1]) {
        i--
    }

    // Step 2: If found, swap with the smallest element larger than it from the right
    if (i >= 0) {
        var j = n - 1
        while (nums[j] <= nums[i]) {
            j--
        }
        swap(nums, i, j)
    }

    // Step 3: Reverse the suffix after position i
    reverseP(nums, i + 1, n - 1)
}

fun swap(nums: IntArray, i: Int, j: Int) {
    val temp = nums[i]
    nums[i] = nums[j]
    nums[j] = temp
}

fun reverseP(nums: IntArray, left: Int, right: Int) {
    var l = left
    var r = right
    while (l < r) {
        swap(nums, l, r)
        l++
        r--
    }
}
