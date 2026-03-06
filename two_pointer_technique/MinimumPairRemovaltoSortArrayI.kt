package array_traversals.two_pointer_technique

fun main() {
    println(minPairRemovalBF(intArrayOf(5,2,3,1)))
    println(minPairRemovalOP(intArrayOf(5,2,3,1)))
}

fun minPairRemovalBF(nums: IntArray): Int {
    val n = nums.size
    if (n <= 1) return 0

    val dp = IntArray(n) { 1 }
    var maxLNDS = 1

    for (i in 1 until n) {
        for (j in 0 until i) {
            // If non-decreasing condition met
            if (nums[j] <= nums[i]) {
                dp[i] = maxOf(dp[i], dp[j] + 1)
            }
        }
        maxLNDS = maxOf(maxLNDS, dp[i])
    }

    // Total elements minus the ones we kept
    return n - maxLNDS
}

fun minPairRemovalOP(nums: IntArray): Int {
    val tails = mutableListOf<Int>()

    for (num in nums) {
        // Find the first element strictly greater than num
        // We use binary search to keep the tails sorted
        var left = 0
        var right = tails.size
        while (left < right) {
            val mid = left + (right - left) / 2
            if (tails[mid] <= num) {
                left = mid + 1
            } else {
                right = mid
            }
        }

        if (left == tails.size) {
            tails.add(num)
        } else {
            tails[left] = num
        }
    }

    return nums.size - tails.size
}