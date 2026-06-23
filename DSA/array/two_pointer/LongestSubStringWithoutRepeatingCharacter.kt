package array.two_pointer

fun main() {
//    println(longestSubStringWithoutRepeatingCharacter("pwwkew"))
    println(longestSubStringWithoutRepeatingCharacterOP("shekhawat")) // shekhawat

}
fun longestSubStringWithoutRepeatingCharacter(s: String): Int {

    var map: MutableMap<Char, Boolean>
    var longCount = 0
    var currentCount = 0
    for (left in s.indices) {
        map = mutableMapOf()
        currentCount =0
        for (right in left until  s.length) {
            if(!map.containsKey(s[right])) {
                map[s[right]] = true
                currentCount++
                longCount = Math.max(longCount, currentCount)
            } else {
                break
            }
        }
    }

    return longCount
}

fun longestSubStringWithoutRepeatingCharacterOP(s: String): Int {

    var str = StringBuilder()
    var currentPos = 0
    var lengthC = 0
    var cCount = 0

    while (currentPos < s.length) {
        if(!str.contains(s[currentPos]))
        {
            str.append(s[currentPos])
            cCount++
            lengthC = Math.max(cCount, lengthC)
        }
        else{
            val pos = str.indexOf(s[currentPos])
            str.append(s[currentPos])
            str = StringBuilder(str.substring(pos + 1, str.length))
            cCount = str.length
        }
        currentPos++
    }

    return lengthC
}