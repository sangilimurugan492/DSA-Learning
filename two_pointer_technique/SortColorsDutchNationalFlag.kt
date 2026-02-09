package two_pointer_technique

/**
 * https://leetcode.com/problems/sort-colors/description/
 */
fun main() {
//    println("Brute Force")
//    sortColorsDutchFlagBF(intArrayOf(2,0,2,1,1,0))

    println("\nOptimal")
    sortColorsDutchFlagOP(intArrayOf(2,0,1))
}


/**
 * Brute Force Approach
 * Dutch National Flag or Sort Colors
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 * Input: nums = [1,0,2,1,2,0]
 * Output: [0,0,1,1,2,2]
 *
 */
fun sortColorsDutchFlagBF(array : IntArray) {

    var temp :Int
    for(i in array.indices) {
        for (j in  i+1 until array.size) {
           if (array[i] == 2) {
               temp = array[j]
               array[j] = array[i]
               array[i] = temp
           } else if (array[j] == 0) {
               temp = array[i]
               array[i] = array[j]
               array[j] = temp
           }
        }
    }

    array.forEach {
        print("$it ")
    }
}

/**
 * Optimal Approach
 * Dutch National Flag or Sort Colors
 * Time Complexity O(N / 2) => O(N)
 * Space Complexity O(N)
 * Input: nums = [1,0,2,1,2,0]
 * Output: [0,0,1,1,2,2]
 *
 */
fun sortColorsDutchFlagOP(array : IntArray) {

    var lfCounter = 0
    var rtCounter = array.size - 1
    var i = 0
    var temp : Int
    while (i <= rtCounter) {
        if (array[i] == 0) {
            temp = array[lfCounter]
            array[lfCounter] = array[rtCounter]
            array[rtCounter] = temp
            lfCounter++
            i++
        } else if (array[i] == 2) {
            temp = array[rtCounter]
            array[rtCounter] = array[i]
            array[i] = temp
            rtCounter--
        } else {
            i++
        }
    }
    array.forEach {
        print("$it ")
    }
}
