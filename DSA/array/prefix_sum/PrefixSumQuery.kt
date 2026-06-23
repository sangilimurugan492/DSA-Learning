package array.prefix_sum

class NumArray(val nums: IntArray) {

    val prefix = IntArray(nums.size + 1)

    init {
        for (i in nums.indices) {
            prefix[i + 1] = prefix[i] + nums[i]
        }
    }

    fun sumRange(left: Int, right: Int): Int {
        return prefix[right + 1] - prefix[left]
    }

}

fun main() {
    val numsArray = NumArray(intArrayOf(-2, 0, 3, -5, 2, -1))
    println(numsArray.sumRange(0,2))
    println(numsArray.sumRange(2,5))
    println(numsArray.sumRange(0,5))
}