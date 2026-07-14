package array.sliding_window.grumpy_book_store_owner

/**
 * There is a bookstore owner that has a store open for n minutes. You are given an integer array customers of length n where customers[i] is the number of the customers that enter the store at the start of the ith minute and all those customers leave after the end of that minute.
 *
 * During certain minutes, the bookstore owner is grumpy. You are given a binary array grumpy where grumpy[i] is 1 if the bookstore owner is grumpy during the ith minute, and is 0 otherwise.
 *
 * When the bookstore owner is grumpy, the customers entering during that minute are not satisfied. Otherwise, they are satisfied.
 *
 * The bookstore owner knows a secret technique to remain not grumpy for minutes consecutive minutes, but this technique can only be used once.
 *
 * Return the maximum number of customers that can be satisfied throughout the day.
 *
 *
 *
 * Example 1:
 *
 * Input: customers = [1,0,1,2,1,1,7,5], grumpy = [0,1,0,1,0,1,0,1], minutes = 3
 *
 * Output: 16
 *
 * Explanation:
 *
 * The bookstore owner keeps themselves not grumpy for the last 3 minutes.
 *
 * The maximum number of customers that can be satisfied = 1 + 1 + 1 + 1 + 7 + 5 = 16.
 *
 * Example 2:
 *
 * Input: customers = [1], grumpy = [0], minutes = 1
 *
 * Output: 1
 */
fun main() {
    println(maxSatisfiedBrute(intArrayOf(1,0,1,2,1,1,7,5), intArrayOf(0,1,0,1,0,1,0,1), 3))
    println(maxSatisfied(intArrayOf(1,0,1,2,1,1,7,5), intArrayOf(0,1,0,1,0,1,0,1), 3))
}

fun maxSatisfiedBrute(customers: IntArray, grumpy: IntArray, minutes: Int) : Int {
    var maxTotal = 0
    val n = customers.size

    for (start in 0..n - minutes) {
        var currentSatisfied = 0
        for (i in 0 until n) {
            // If inside the "technique" window OR owner isn't grumpy
            if (i in start until (start + minutes) || grumpy[i] == 0) {
                currentSatisfied += customers[i]
            }
        }
        maxTotal = maxOf(maxTotal, currentSatisfied)
    }
    return maxTotal
}

fun maxSatisfied(customers: IntArray, grumpy: IntArray, minutes: Int): Int {
    var baseSatisfied = 0
    var windowExtra = 0
    var maxExtra = 0

    // 1. Calculate base satisfied and the first window's extra gain
    for (i in customers.indices) {
        if (grumpy[i] == 0) {
            baseSatisfied += customers[i]
        }

        // If within the first 'minutes', calculate potential extra gain
        if (i < minutes) {
            if (grumpy[i] == 1) {
                windowExtra += customers[i]
            }
        }
    }

    maxExtra = windowExtra

    // 2. Slide the window from 'minutes' to the end
    for (i in minutes until customers.size) {
        // Add new element entering the window
        if (grumpy[i] == 1) {
            windowExtra += customers[i]
        }
        // Remove element exiting the window
        if (grumpy[i - minutes] == 1) {
            windowExtra -= customers[i - minutes]
        }

        maxExtra = maxOf(maxExtra, windowExtra)
    }

    return baseSatisfied + maxExtra
}