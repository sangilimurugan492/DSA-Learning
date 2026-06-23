package array.set_operations

/**
 * https://leetcode.com/problems/find-the-difference-of-two-arrays/description/
 *
 * Given two 0-indexed integer arrays nums1 and nums2, return a list answer of size 2 where:
 *
 * answer[0] is a list of all distinct integers in nums1 which are not present in nums2.
 * answer[1] is a list of all distinct integers in nums2 which are not present in nums1.
 * Note that the integers in the lists may be returned in any order.
 *
 * Example 1:
 *
 * Input: nums1 = [1,2,3], nums2 = [2,4,6]
 * Output: [[1,3],[4,6]]
 * Explanation:
 * For nums1, nums1[1] = 2 is present at index 0 of nums2, whereas nums1[0] = 1 and nums1[2] = 3 are not present in nums2. Therefore, answer[0] = [1,3].
 * For nums2, nums2[0] = 2 is present at index 1 of nums1, whereas nums2[1] = 4 and nums2[2] = 6 are not present in nums1. Therefore, answer[1] = [4,6].
 */
fun main() {

    findDifferenceBF(intArrayOf(1,2,3), intArrayOf(2,4,6)).forEach {
        it.forEach {it1->
            print(it1)
        }
        println()
    }

    findDifferenceOP(intArrayOf(1,2,3), intArrayOf(2,4,6)).forEach {
        it.forEach {it1->
            print(it1)
        }
        println()
    }
}

fun findDifferenceBF(nums1: IntArray, nums2: IntArray): List<List<Int>> {
    val resultSet = mutableListOf<MutableList<Int>>()
    var present: Boolean
    val list1 = mutableSetOf<Int>()
    val list2 = mutableSetOf<Int>()
    for(n1 in nums1) {
        present = false
        for(n2 in nums2) {
            if (n1 == n2) {
                present = true
                break
            }
        }
        if (!present) {
            list1.add(n1)
        }
    }

    for(n1 in nums2) {
        present = false
        for(n2 in nums1) {
            if (n1 == n2) {
                present = true
                break
            }
        }
        if (!present) {
            list2.add(n1)
        }
    }

    resultSet.add(list1.toMutableList())
    resultSet.add(list2.toMutableList())
    return resultSet
}

fun findDifferenceOP(nums1: IntArray, nums2: IntArray): List<List<Int>> {
    val set = mutableSetOf<Int>()
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()
    val list = mutableListOf<List<Int>>()
    for(num in nums2){
        set.add(num)
    }
    for(num in nums1){
        if(set.add(num)){
            list1.add(num)
        }
    }
    list.add(list1)
    set.clear()
    for(num in nums1){
        set.add(num)
    }
    for(num in nums2){
        if(set.add(num)){
            list2.add(num)
        }
    }
    list.add(list2)
    return list
}