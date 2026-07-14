package array.voting_floyd.majority_of_elements

/**
 * https://leetcode.com/problems/majority-element/
 * Given array nums of size n, return the majority element (appears > n/2 times).
 * Example: [3,2,3] → 3 | [2,2,1,1,1,2,2] → 2
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(majorityElementBruteForce(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
    println("---")
    println(majorityElementSort(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
    println("---")
    println(majorityElementHashMap(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
    println("---")
    println(majorityElementVoting(intArrayOf(2, 2, 1, 1, 1, 2, 2)))
}

/** BRUTE FORCE: O(N²) — count frequency for each element */
fun majorityElementBruteForce(nums: IntArray): Int {
    for (num in nums) {
        var count = 0
        for (n in nums) if (n == num) count++
        if (count > nums.size / 2) return num
    }
    return -1
}

/** BETTER: O(N log N) Sort — majority element is always at middle */
fun majorityElementSort(nums: IntArray): Int {
    nums.sort()
    return nums[nums.size / 2]
}

/** BETTER: O(N) HashMap */
fun majorityElementHashMap(nums: IntArray): Int {
    val count = hashMapOf<Int, Int>()
    for (num in nums) {
        count[num] = count.getOrDefault(num, 0) + 1
        if (count[num]!! > nums.size / 2) return num
    }
    return -1
}

/** OPTIMAL: O(N) O(1) — Boyer-Moore Voting Algorithm */
fun majorityElementVoting(nums: IntArray): Int {
    var candidate = nums[0]
    var count = 1
    for (i in 1 until nums.size) {
        if (count == 0) { candidate = nums[i]; count = 1 }
        else if (nums[i] == candidate) count++
        else count--
    }
    return candidate
}
