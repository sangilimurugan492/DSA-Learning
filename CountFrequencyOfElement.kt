fun main() {

    /**
     * Numbers Array
     */
    val array = arrayOf(3,5,7,2,4,7,12,10,56,78,45)
    println("Number Array Brute Force Solution")
    frequencyOfElementBF(array) // Brute Force Solution

    println("Number Array Brute Optimal Solution")
    frequencyOfElementOP(array) // Optimal Solution


    /**
     * Strings or Character Array
     */

    val stringArray = "welcometokotlinprogram"
    println("Char Array Brute Force Solution")
    frequencyOfElementStringBF(stringArray.toCharArray()) // Brute Force Solution

    println("Char Array Brute Optimal Solution")
    frequencyOfElementStringOP(stringArray.toCharArray()) // Optimal Solution
}


/**
 * Numbers Array using BruteForce Method
 * Time Complexity - O(N^2)
 * Space Complexity - O(N) - We are using the Extra Hash Map below the solution
 *
 * Edge Cases Handled
 *  1. Empty Array
 *  2. Array with One Element
 *
 */
fun frequencyOfElementBF(array: Array<Int>) {
    val n = array.size
    val frequency = mutableMapOf<Int, Int>()
    if (n == 0) {
        println(frequency)
    }
    for (i in 0 until n) {
        if (frequency.containsKey(array[i])) {
            continue
        }
        frequency[array[i]] = 1
        for (j in i+1 until n) {
            if (array[i] == array[j]) {
                val count = frequency[array[i]]?.plus(1)
                frequency[array[i]] = count ?: 1
            }
        }
    }

    println(frequency)
}

/**
 * Numbers Array using Optimal Using One Loop Method and HashMap
 * Time Complexity - O(N)
 * Space Complexity - O(2N) --> O(N) (Drop Constants) - We are using the Extra Hash Map below the solution
 *
 * Edge Cases Handled
 *  1. Empty Array
 *  2. Array with One Element
 *
 */
fun frequencyOfElementOP(array: Array<Int>) {
    val n = array.size
    val frequency = mutableMapOf<Int, Int>()
    if (n == 0) {
        println(frequency)
    }
    for (i in 0 until n) {
        if (frequency.containsKey(array[i])) {
            val count = frequency[array[i]]?.plus(1)
            frequency[array[i]] = count ?: 1

        } else {
            frequency[array[i]] = 1
        }
    }

    println(frequency)
}



/**
 * Char Array using BruteForce Method
 * Time Complexity - O(N^2)
 * Space Complexity - O(N) - We are using the Extra Hash Map below the solution
 *
 * Edge Cases Handled
 *  1. Empty Array
 *  2. Array with One Element
 *
 */
fun frequencyOfElementStringBF(array: CharArray) {
    val n = array.size
    val frequency = mutableMapOf<Char, Int>()
    if (n == 0) {
        println(frequency)
    }
    for (i in 0 until n) {
        if (frequency.containsKey(array[i])) {
            continue
        }
        frequency[array[i]] = 1
        for (j in i+1 until n) {
            if (array[i] == array[j]) {
                val count = frequency[array[i]]?.plus(1)
                frequency[array[i]] = count ?: 1
            }
        }
    }

    println(frequency)
}

/**
 * Numbers Array using Optimal Using One Loop Method and HashMap
 * Time Complexity - O(N)
 * Space Complexity - O(2N) --> O(N) (Drop Constants) - We are using the Extra Hash Map below the solution
 *
 * Edge Cases Handled
 *  1. Empty Array
 *  2. Array with One Element
 *
 */
fun frequencyOfElementStringOP(array: CharArray) {
    val n = array.size
    val frequency = mutableMapOf<Char, Int>()
    if (n == 0) {
        println(frequency)
    }
    for (i in 0 until n) {
        if (frequency.containsKey(array[i])) {
            val count = frequency[array[i]]?.plus(1)
            frequency[array[i]] = count ?: 1

        } else {
            frequency[array[i]] = 1
        }
    }

    println(frequency)
}

// Output :-
// Number Array Brute Force Solution
// {3=1, 5=1, 7=2, 2=1, 4=1, 12=1, 10=1, 56=1, 78=1, 45=1}
// Number Array Brute Optimal Solution
// {3=1, 5=1, 7=2, 2=1, 4=1, 12=1, 10=1, 56=1, 78=1, 45=1}
// Char Array Brute Force Solution
// {w=1, e=2, l=2, c=1, o=4, m=2, t=2, k=1, i=1, n=1, p=1, r=2, g=1, a=1}
// Char Array Brute Optimal Solution
// {w=1, e=2, l=2, c=1, o=4, m=2, t=2, k=1, i=1, n=1, p=1, r=2, g=1, a=1}
