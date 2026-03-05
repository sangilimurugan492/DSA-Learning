package array_traversals

import java.util.*

/**
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 */

fun main() {
    println(findMedianSortedArraysBF(intArrayOf(1,2), intArrayOf(3, 4)))
    println(findMedianSortedArrays(intArrayOf(1,2), intArrayOf(3,4)))
}

/**
 * Time Complexity O((m+n) * log(m+n))
 * Space complexity O(m+n)
 */
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

/**
 * Time Complexity O(log(m+n))
 * Space complexity O(m+n)
 */
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

fun findMedianSortedArraysOp1(nums1: IntArray, nums2: IntArray): Double {

    if (nums1.size > nums2.size)
        return findMedianSortedArrays(nums2, nums1)

    val m = nums1.size
    val n = nums2.size

    var low = 0
    var high = m

    while (low <= high) {

        val partitionX = (low + high) / 2
        val partitionY = (m + n + 1) / 2 - partitionX

        val maxLeftX = if (partitionX == 0) Int.MIN_VALUE else nums1[partitionX - 1]
        val minRightX = if (partitionX == m) Int.MAX_VALUE else nums1[partitionX]

        val maxLeftY = if (partitionY == 0) Int.MIN_VALUE else nums2[partitionY - 1]
        val minRightY = if (partitionY == n) Int.MAX_VALUE else nums2[partitionY]

        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {

            return if ((m + n) % 2 == 0) {
                (maxOf(maxLeftX, maxLeftY) + minOf(minRightX, minRightY)) / 2.0
            } else {
                maxOf(maxLeftX, maxLeftY).toDouble()
            }

        } else if (maxLeftX > minRightY) {
            high = partitionX - 1
        } else {
            low = partitionX + 1
        }
    }

    throw IllegalArgumentException()
}