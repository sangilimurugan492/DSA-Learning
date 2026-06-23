package array.two_pointer_inplace

/**
 * https://leetcode.com/problems/rotate-array/
 *
 * Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.
 *
 * Example 1:
 *
 * Input: nums = [1,2,3,4,5,6,7], k = 3
 * Output: [5,6,7,1,2,3,4]
 * Explanation:
 * rotate 1 steps to the right: [7,1,2,3,4,5,6]
 * rotate 2 steps to the right: [6,7,1,2,3,4,5]
 * rotate 3 steps to the right: [5,6,7,1,2,3,4]
 *
 * Example 2:
 *
 * Input: nums = [-1,-100,3,99], k = 2
 * Output: [3,99,-1,-100]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Microsoft, Apple)
 *
 * Key Insight: Use array reversal trick. Rotating right by k = reverse entire array +
 * reverse first k elements + reverse remaining n-k elements.
 * Must handle k > n by doing k = k % n.
 */
fun main() {
    val arr1 = intArrayOf(1, 2, 3, 4, 5, 6, 7)
    rotateArrayExtraSpace(arr1, 3)
    println(arr1.toList())

    val arr2 = intArrayOf(1, 2, 3, 4, 5, 6, 7)
    rotateArrayReverse(arr2, 3)
    println(arr2.toList())

    val arr3 = intArrayOf(-1, -100, 3, 99)
    rotateArrayReverse(arr3, 2)
    println(arr3.toList())
}

/**
 * Time Complexity O(N)
 * Space Complexity O(N) - uses extra array
 */
fun rotateArrayExtraSpace(nums: IntArray, k: Int) {
    val n = nums.size
    if (n == 0) return
    val steps = k % n
    val temp = IntArray(n)

    for (i in nums.indices) {
        temp[(i + steps) % n] = nums[i]
    }

    for (i in nums.indices) {
        nums[i] = temp[i]
    }
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1) - in-place using reversal
 *
 * Approach: 3-step reversal
 * Original: [1, 2, 3, 4, 5, 6, 7], k = 3
 *
 * Step 1: Reverse entire array
 *   [7, 6, 5, 4, 3, 2, 1]
 *
 * Step 2: Reverse first k elements
 *   [5, 6, 7, 4, 3, 2, 1]
 *
 * Step 3: Reverse remaining n-k elements
 *   [5, 6, 7, 1, 2, 3, 4] ✅
 */
fun rotateArrayReverse(nums: IntArray, k: Int) {
    val n = nums.size
    if (n == 0) return
    val steps = k % n

    // Step 1: Reverse entire array
    reverse(nums, 0, n - 1)
    // Step 2: Reverse first k elements
    reverse(nums, 0, steps - 1)
    // Step 3: Reverse remaining elements
    reverse(nums, steps, n - 1)
}

fun reverse(arr: IntArray, left: Int, right: Int) {
    var l = left
    var r = right
    while (l < r) {
        val temp = arr[l]
        arr[l] = arr[r]
        arr[r] = temp
        l++
        r--
    }
}
