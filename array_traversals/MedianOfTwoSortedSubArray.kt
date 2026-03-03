package array_traversals

import java.util.*

/**
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 */

fun main() {
    println(findMedianSortedArraysBF(intArrayOf(1,2), intArrayOf(3, 4)))
    println(findMedianSortedArrays(intArrayOf(1,2), intArrayOf(3,4)))
}

fun findMedianSortedArraysBF(nums1: IntArray, nums2: IntArray): Double {
    val result = IntArray(nums1.size + nums2.size)
    for(i in nums1.indices) {
        result[i] = nums1[i]
    }

    for(i in nums2.indices) {
        result[i + nums1.size] = nums2[i]
    }

    Arrays.sort(result) // O((m+n) * log(m+n))
    if (result.size%2 == 1) {
        println(result[((result.size / 2))])
    } else {
       val mid1 = result[result.size/2]
       val mid2 = result[result.size/2 - 1]
        println((mid1 + mid2).toDouble()/2)
    }

    println()
    return 0.0
}

fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    val m = nums1.size
    val n = nums2.size
    val result = IntArray(m + n)
    var i = 0
    var nums1Count = 0
    var nums2Count = 0
    while (i in result.indices ) {
        if (m > nums1Count && n > nums2Count) {
            if (nums1[nums1Count] < nums2[nums2Count]) {
                result[i] = nums1[nums1Count]
                nums1Count++;
            } else {
                result[i] = nums2[nums2Count]
                nums2Count++;
            }
        } else if (m > nums1Count) {
            result[i] = nums1[nums1Count]
            nums1Count++;
        } else {
            result[i] = nums2[nums2Count]
            nums2Count++;
        }
        i++
    }

    if (result.size%2 == 1) {
        return result[((result.size / 2))].toDouble()
    } else {
        val mid1 = result[result.size/2]
        val mid2 = result[(result.size/2) - 1]
        return (mid1 + mid2).toDouble()/2
    }
}