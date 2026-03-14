package array_traversals.two_pointer_technique

/**
 * https://leetcode.com/problems/is-subsequence/description
 * Example 1:
 *
 * Input: s = "abc", t = "ahbgdc"
 * Output: true
 * Example 2:
 *
 * Input: s = "axc", t = "ahbgdc"
 * Output: false
 *
 */
fun main() {
    println(isSubsequenceBF("leet", "leeet"))
    println(isSubsequenceOP("leet", "leeet"))
}

fun isSubsequenceBF(s: String, t: String): Boolean {

    if(s.isEmpty())
        return true
    if(t.isEmpty())
        return false

    var count = 0
    val check = s.toCharArray()
    for(ch in t.toCharArray()) {
        if (count < check.size && check[count] == ch) {
            count++
        }
    }

    return check.size == count
}

fun isSubsequenceOP(s: String, t: String): Boolean {

    if(s.isEmpty())
        return true
    if(t.isEmpty())
        return false

    var count = 0
    val sArray = s.toCharArray()
    val tArray = t.toCharArray()

    var leftT = 0
    var rightT = tArray.size - 1

    var leftS = 0
    var rightS = sArray.size - 1
    while (leftT <= rightT) {
        if (leftT == rightT && sArray[leftS] == tArray[leftT] && count < sArray.size) {
            count++
        } else {
            if (count < sArray.size && sArray[leftS] == tArray[leftT]) {
                count++
                leftS++
            }

            if (count < sArray.size && sArray[rightS] == tArray[rightT]) {
                count++
                rightS--
            }
        }

        leftT++
        rightT--
    }

    return sArray.size == count
}

fun isSubsequenceOP1(s: String, t: String): Boolean {

    fun search(i: Int, j: Int): Boolean {
        if (i == s.length) return true
        if (j == t.length) return false

        return if (s[i] == t[j]) {
            search(i + 1, j + 1)
        } else {
            search(i, j + 1)
        }

    }

    return search(0, 0)
}

