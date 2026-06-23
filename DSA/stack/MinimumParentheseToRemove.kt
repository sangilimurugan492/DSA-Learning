package stack

fun main() {
    minRemoveToMakeValid("lee(t(c)o)de)") // "lee(t(co)de)" , "lee(t(c)ode)"
    minRemoveToMakeValid("a)b(c)d") // ab(c)d"
    minRemoveToMakeValid("))((") // ""
}

fun minRemoveToMakeValid(s: String): String {

    return ""
}