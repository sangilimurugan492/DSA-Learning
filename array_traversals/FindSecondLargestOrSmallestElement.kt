package array_traversals

fun main() {
    val array = arrayOf(11,4,24,56,12,75,67,1,5,87,15,65)
    secondLargestOrSmallestElementOP(array)
}

/**
 * Optimal Way O(N) - Time Complexity
 * O(N) - Space Complexity
 */
fun secondLargestOrSmallestElementOP(value: Array<Int>) {
    var firstLargest : Int
    var secondLargest : Int

    var firstSmallest : Int
    var secondSmallest : Int

    if(value[0] > value[1]) {
        firstLargest = value[0]
        secondLargest = value[1]
        firstSmallest = value[1]
        secondSmallest = value[0]
    } else {
        firstLargest = value[1]
        secondLargest = value[0]
        firstSmallest = value[0]
        secondSmallest = value[1]
    }

    for(i in 2..<value.size) {
        if (value[i] > firstLargest) {
            val temp = firstLargest
            firstLargest = value[i]
            secondLargest = temp
        } else if (value[i] > secondLargest){
            secondLargest = value[i]
        }

        if (value[i] < firstSmallest) {
            val temp = firstSmallest
            firstSmallest = value[i]
            secondSmallest = temp
        } else if (value[i] < secondSmallest){
            secondSmallest = value[i]
        }
    }

    println("Second Largest :: $secondLargest")
    println("Second Smallest :: $secondSmallest")
}