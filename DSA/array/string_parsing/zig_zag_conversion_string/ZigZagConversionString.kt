package array.string_parsing.zig_zag_conversion_string


/**
 * https://leetcode.com/problems/zigzag-conversion/
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: (you may want to display this pattern in a fixed font for better legibility)
 *
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 * And then read line by line: "PAHNAPLSIIGYIR"
 *
 * Write the code that will take a string and make this conversion given a number of rows:
 *
 * string convert(string s, int numRows);
 */

fun main() {
    println(convertOP("PAYPALISHIRING", 3))
}

fun convertOP(s: String, numRows: Int): String {

    if (numRows == 1) return s

    val rows: MutableList<StringBuilder> = ArrayList()
    for (i in 0 until numRows.coerceAtMost(s.length)) {
        rows.add(StringBuilder())
    }

    var i = 0
    var goingDown = false

    for (c in s.toCharArray()) {
        rows[i].append(c)
        if (i == 0 || i == numRows - 1) goingDown = !goingDown
        i += if (goingDown) 1 else -1
    }

    val result = StringBuilder()
    for (row in rows) {
        result.append(row)
    }
    return result.toString()
}

//fun convertBF(s: String, numRows: Int): String {
//
//}