package array_traversals.two_pointer_technique

/**
 * https://leetcode.com/problems/string-compression/?envType=problem-list-v2&envId=two-pointers
 * Given an array of characters chars, compress it using the following algorithm:
 *
 * Begin with an empty string s. For each group of consecutive repeating characters in chars:
 *
 * If the group's length is 1, append the character to s.
 * Otherwise, append the character followed by the group's length.
 * The compressed string s should not be returned separately, but instead, be stored in the input character array chars. Note that group lengths that are 10 or longer will be split into multiple characters in chars.
 *
 * After you are done modifying the input array, return the new length of the array.
 *
 * You must write an algorithm that uses only constant extra space.
 *
 * Note: The characters in the array beyond the returned length do not matter and should be ignored.
 *
 *
 *
 * Example 1:
 *
 * Input: chars = ["a","a","b","b","c","c","c"]
 * Output: 6
 * Explanation: The groups are "aa", "bb", and "ccc". This compresses to "a2b2c3".
 * Example 2:
 *
 * Input: chars = ["a"]
 * Output: 1
 * Explanation: The only group is "a", which remains uncompressed since it's a single character.
 * Example 3:
 *
 * Input: chars = ["a","b","b","b","b","b","b","b","b","b","b","b","b"]
 * Output: 4
 * Explanation: The groups are "a" and "bbbbbbbbbbbb". This compresses to "ab12".
 */
fun main() {
//    println(compress(charArrayOf('a','b','b','b','b','b','b','b','b','b','b','b','b', 'b')))
//    println(compress(charArrayOf('a','b','b','b','b','b','b','b','b','b','b','b','c', 'd')))
//    println(compress(charArrayOf('a','a', 'b','b','b','c','c','c','c','d','d','d','d','e', 'e')))
    println(compress(charArrayOf('a','a', 'b','b','c','c','c')))
}

/**
 * Code Sample
 */
fun compressOp1(chars: CharArray): Int {
    if (chars.size < 2) return chars.size
    var index = 0
    var indexToWrite = 0

    while (index < chars.size) {
        val current = chars[index]
        var next = index + 1
        var letterCount = 1
        while (next < chars.size && current == chars[next]) {
            next++
            letterCount++
        }

        chars[indexToWrite++] = current
        if (letterCount > 1) {
            letterCount.toString()
                .forEach { chars[indexToWrite++] = it }
        }
        index = next
    }
    return indexToWrite
}

/**
 * 1. Empty String
 * 2. Only one length
 * 3. All are same char
 * 4. Multiple Chars
 */
fun compress(chars: CharArray): Int {
    if (chars.isEmpty()){
        return 0
    } else if(chars.size == 1) {
        return 1
    } else {
        var first = 0
        var second = 1
        var count = 1
        val builder = StringBuilder()

        do {
            if (chars[first] != chars[second]) {
                builder.append(chars[first])
                if(count > 1)
                    builder.append(count)
                count = 1
            } else {
                count++
            }
            first++
            second++
        } while (second < chars.size)

        if (chars[first-1] == chars[second-1]) {
            builder.append(chars[first])
            builder.append(count)
        } else {
            builder.append(chars[first])
        }
//        println(builder.toString().toCharArray())
        for (i in 0 until  builder.toString().length) {
            chars[i] = builder.toString()[i]
        }
        return builder.toString().toCharArray().size
    }
}