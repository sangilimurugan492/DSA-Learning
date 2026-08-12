package array.two_pointer.string_compression

/**
 * https://leetcode.com/problems/string-compression/
 *
 * Given an array of characters chars, compress it using the following algorithm:
 *
 * Begin with an empty string s. For each group of consecutive repeating characters in chars:
 * - If the group's length is 1, append the character to s.
 * - Otherwise, append the character followed by the group's length.
 *
 * The compressed string should be stored in the input character array chars.
 * Group lengths that are 10 or longer will be split into multiple characters in chars.
 *
 * After modifying the input array, return the new length.
 *
 * Example 1:
 *   Input: chars = ["a","a","b","b","c","c","c"]
 *   Output: 6  ("a2b2c3")
 *
 * Example 2:
 *   Input: chars = ["a"]
 *   Output: 1  ("a")
 *
 * Example 3:
 *   Input: chars = ["a","b","b","b",...12 b's]
 *   Output: 4  ("ab12")
 *
 * Two approaches:
 * 1. Brute Force (StringBuilder): Build compressed string, then copy back to chars
 * 2. Optimal (In-Place Two Pointer): Read/write pointers, no extra space
 */
fun main() {
    println("Brute Force (StringBuilder):")
    println(compressBF(charArrayOf('a', 'a', 'b', 'b', 'c', 'c', 'c')))  // 6
    println(compressBF(charArrayOf('a')))                                  // 1
    println("Optimal (In-Place):")
    println(compress(charArrayOf('a', 'a', 'b', 'b', 'c', 'c', 'c')))     // 6
    println(compress(charArrayOf('a')))                                      // 1
}

/**
 * Brute Force (StringBuilder): Scan through chars, group consecutive characters,
 * build a compressed string using StringBuilder, then copy back to chars.
 *
 * Step-by-step:
 * 1. Set i = 0.
 * 2. While i < chars.size:
 *    a. Remember currentChar = chars[i].
 *    b. Count consecutive occurrences of currentChar starting at i.
 *    c. Append currentChar to StringBuilder.
 *    d. If count > 1 → append count digits to StringBuilder.
 *    e. Advance i by count (skip to next group).
 * 3. Copy StringBuilder contents back into chars.
 * 4. Return StringBuilder length.
 *
 * Walkthrough: chars = ['a','a','b','b','c','c','c']
 *
 *   i=0: char='a', count=2 → append "a2", i=2
 *   i=2: char='b', count=2 → append "b2", i=4
 *   i=4: char='c', count=3 → append "c3", i=7
 *   StringBuilder = "a2b2c3", length=6
 *
 * Result: 6 ✅
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(N) — StringBuilder
 */
fun compressBF(chars: CharArray): Int {
    if (chars.isEmpty()) return 0

    val sb = StringBuilder()
    var i = 0

    while (i < chars.size) {
        val currentChar = chars[i]
        var count = 0

        // Count consecutive occurrences
        while (i < chars.size && chars[i] == currentChar) {
            count++
            i++
        }

        // Append character
        sb.append(currentChar)
        // Append count if > 1
        if (count > 1) {
            sb.append(count)
        }
    }

    // Copy back to chars
    for (j in sb.indices) {
        chars[j] = sb[j]
    }

    return sb.length
}

/**
 * Optimal (In-Place Two Pointer): Use a read pointer to scan groups and a
 * write pointer to write compressed output directly into chars.
 *
 * Step-by-step:
 * 1. Set index = 0 (read), indexToWrite = 0 (write).
 * 2. While index < chars.size:
 *    a. Remember currentChar = chars[index].
 *    b. Count consecutive occurrences starting at index.
 *    c. Write currentChar at chars[indexToWrite], indexToWrite++.
 *    d. If count > 1 → write each digit of count at indexToWrite, advance.
 *    e. Advance index by count (skip to next group).
 * 3. Return indexToWrite (new length).
 *
 * Walkthrough: chars = ['a','a','b','b','c','c','c']
 *
 *   index=0: char='a', count=2 → write 'a','2' → indexToWrite=2, index=2
 *   index=2: char='b', count=2 → write 'b','2' → indexToWrite=4, index=4
 *   index=4: char='c', count=3 → write 'c','3' → indexToWrite=6, index=7
 *
 * Result: 6, chars = ['a','2','b','2','c','3',...] ✅
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1) — in-place
 */
fun compress(chars: CharArray): Int {
    if (chars.isEmpty()) return 0
    if (chars.size == 1) return 1

    var index = 0      // Read pointer
    var indexToWrite = 0  // Write pointer

    while (index < chars.size) {
        val currentChar = chars[index]
        var count = 0

        // Count consecutive occurrences
        while (index < chars.size && chars[index] == currentChar) {
            count++
            index++
        }

        // Write the character
        chars[indexToWrite++] = currentChar

        // Write the count digits if > 1
        if (count > 1) {
            count.toString().forEach { chars[indexToWrite++] = it }
        }
    }

    return indexToWrite
}
