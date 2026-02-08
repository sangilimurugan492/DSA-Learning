package two_pointer_technique

/**
 * https://leetcode.com/problems/move-zeroes/description/
 */
fun main() {
    println("Move Zeros to to End Using Brute Force Approach")
    moveZerosToEndBF(intArrayOf(0,1,0,3,12))

    println("\nMove Zeros to to End Using Optimal Approach")
    moveZerosToEndOP(intArrayOf(0,1,0,3,12))
}

/**
 * Input: nums = [0,1,0,3,12]
 * Output: [1,3,12,0,0]
 * Time Complexity
 * Space Complexity O(N)
 */
fun moveZerosToEndBF(nums: IntArray) {

    for(i in nums.indices) {
        for (j in i+1  until nums.size) {
            if(nums[i] == 0) {
                val temp = nums[i]
                nums[i] = nums[j]
                nums[j] = temp
            }
        }
    }

    nums.forEach {
        print(it)
    }

}

/**
 * Input: nums = [0,1,0,3,12]
 * Output: [1,3,12,0,0]
 * Time Complexity
 * Space Complexity O(N)
 */
fun moveZerosToEndOP(nums: IntArray) {
    var k = 0
    for (i in nums.indices) {
        if (nums[i] != 0) {
            val temp = nums[k]
            nums[k] = nums[i]
            nums[i] = temp
            k++
        }
    }

    nums.forEach {
        print(it)
    }

}


