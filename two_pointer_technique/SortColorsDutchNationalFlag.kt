package two_pointer_technique

/**
 * https://leetcode.com/problems/sort-colors/description/
 */
fun main() {
    sortColorsDutchFlagBF(intArrayOf(2,0,2,1,1,0))
}


/**
 * Brute Force Approach
 * Dutch National Flag or Sort Colors
 * Time Complexity O(N^2)
 * Space Complexity O(N)
 * Input: nums = [2,0,2,1,1,0]
 * Output: [0,0,1,1,2,2]
 *
 */
fun sortColorsDutchFlagBF(array : IntArray) {

    var temp :Int
    for(i in array.indices) {
        for (j in  i+1 until array.size) {
            if (array[j] == 0) {
                temp = array[i]
                array[i] = array[j]
                array[j] = temp
            } else if (array[j] == 2) {
                temp = array[i]
                array[i] = array[j]
                array[j] = temp
            } else {
                continue
            }
        }
    }

    array.forEach {
        print("$it ")
    }

}

