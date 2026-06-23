package array.two_pointer_inplace

/**
 * https://leetcode.com/problems/remove-element/description/
 */
fun main() {

    val resultBF = removeElementBF(intArrayOf(3,2,2,3), 3)
    val resultOP = removeElementOP(intArrayOf(3,2,2,3), 3)

    println(
        "Result BF $resultBF" + "\n"+
        "Result OP $resultOP"
    )
}

/**
 * [3,2,2,3], val = 3
 * Time Complexity O(N^2)
 * Space Complexity O(1)
 */
fun removeElementBF(nums: IntArray, `val`: Int) : Int {
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
 * [3,2,2,3], val = 3
 * Time Complexity O(N)
 * Space Complexity O(1)
 */
fun removeElementOP(nums: IntArray, `val`: Int) : Int {
    var count = 0
    for (i in nums.indices) {
        if (nums[i] != `val`) {
            nums[count] = nums[i]
            count++
        }
    }
    return count // New length of the array
}