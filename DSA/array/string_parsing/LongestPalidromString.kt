package array.string_parsing


fun main() {
    println(longestPalindrome("abccccdd"))
//    println(checkWeatherStringIsPalindrome("abababss"))
}


/**
 * LongestPalindrome
 */

fun findPalindromeStringOP(s : String) : Int {

    return 0
}

fun longestPalindrome(s: String): Int {
    val counts = IntArray(128) // To store ASCII character counts
    for (c in s.toCharArray()) {
        counts[c.code]++
    }

    var length = 0
    var hasOdd = false

    for (count in counts) {
        length += (count / 2) * 2
        if (count % 2 == 1) {
            hasOdd = true
        }
    }

    return if (hasOdd) length + 1 else length
}

/**
 * Time Complexity O(N/2)
 * Space Complexity O(N)
 */
fun checkWeatherStringIsPalindrome(s : String) : Boolean {
    val str = s.lowercase()
    var left = 0
    var right = str.length - 1
    var mid = str.length / 2
    while (left <= mid) {
        if (str[left] != str[right]) {
            return false
        }
        left++
        right--
    }
    return true
}