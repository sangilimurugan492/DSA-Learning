package array_traversals.simulation

fun main() {
    println(addBinary("1001", "1001"))
}
fun addBinary(a: String, b: String): String {
    val result = StringBuilder()
    var i = a.length - 1
    var j = b.length - 1
    var carry = 0


    // Loop from right to left
    while (i >= 0 || j >= 0 || carry == 1) {
        var sum = carry // Start with the carry
        if (i >= 0) sum += a[i--].code - '0'.code
        if (j >= 0) sum += b[j--].code - '0'.code

        result.append(sum % 2) // Add bit (0 or 1)
        carry = sum / 2 // Update carry
    }

    return result.reverse().toString() // Reverse at end
}