package array_traversals.simulation

fun main() {
    println(addStrings("4567", "6575"))
}

fun addStrings(num1: String, num2: String): String {
    val result = StringBuilder()
    var i = num1.length - 1
    var j = num2.length - 1
    var carry = 0

    while (i >= 0 || j >= 0 || carry > 0) {
        // Convert char to digit by subtracting '0'
        val d1 = if (i >= 0) num1[i--] - '0' else 0
        val d2 = if (j >= 0) num2[j--] - '0' else 0

        val sum = d1 + d2 + carry
        result.append(sum % 10)
        carry = sum / 10
    }

    // Reverse since we appended from the ones place upward
    return result.reverse().toString()
}