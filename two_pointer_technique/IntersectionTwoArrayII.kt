package array_traversals.two_pointer_technique

/**
 * https://leetcode.com/problems/intersection-of-two-arrays-ii/
 */
fun main() {
    intersect(intArrayOf(1,2,2,1), intArrayOf(2,2)).forEach {
        print(it)
    }

    println()

    intersectI(intArrayOf(1,2,2,1), intArrayOf(2,2)).forEach {
        print(it)
    }
}

/**
 * Time Complexity O(N + M) N- size of Nums1 M- size of Nums 2
 * Space Complexity O(M)
 */
fun intersect(nums1: IntArray, nums2: IntArray): IntArray {

    val resultList = mutableListOf<Int>()
    val result = IntArray(nums2.size)

    for(n in nums1) {
        resultList.add(n)
    }
    var k = 0
    for (num in nums2) {
        if (resultList.remove(num)) {
            result[k++] = num
        }
    }

    return result.copyOf(k)
}



/**
 * Array Intersection 1
 * Time Complexity O(N + M) N- size of Nums1 M- size of Nums 2
 * Space Complexity O(M)
 */
fun intersectI(nums1: IntArray, nums2: IntArray): IntArray {

    val resultSet = mutableSetOf<Int>()
    val result = IntArray(nums2.size)

    for(n in nums1) {
        resultSet.add(n)
    }
    var k = 0
    for (num in nums2) {
        if (resultSet.remove(num)) {
            result[k++] = num
        }
    }

    return result.copyOf(k)
}