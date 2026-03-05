package array_traversals

import java.lang.Character.isDigit

/**
 * https://leetcode.com/problems/string-to-integer-atoi/description/
 */
fun main() {
    println(myAtoi("-91283472332"))
}

fun myAtoi(s: String): Int {
    val resultBuilder = StringBuilder()
    val charArray = s.trim().toCharArray()
    for(i in charArray.indices) {
        if(isDigit(charArray[i]) || ('-' == charArray[i] && i == 0)) {
            resultBuilder.append(charArray[i])
        } else {
            break
        }
    }
    var str = resultBuilder.toString()
    str = if (str.startsWith("0") && str.length > 1)
        str.substring(1, str.length)
    else
        str

    if(str == "")
        return 0
    else return Integer.parseInt(str)
}

