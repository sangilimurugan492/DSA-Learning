package linked_list.double

/**
 * Input: head = [1,2,3,4,5,6,null,null,null,7,8,9,10,null,null,11,12]
 * Output: [1,2,3,7,8,11,12,9,10,4,5,6]
 * Explanation: The multilevel linked list in the input is shown.
 * After flattening the multilevel linked list it becomes:
 *
 */


fun main() {
    val first = DListNodeWithChild(1, null, null, null)
    val second = DListNodeWithChild(2, null, null, null)
    val third = DListNodeWithChild(3, null, null, null)
    val forth = DListNodeWithChild(4, null, null, null)
    val fifth = DListNodeWithChild(5, null, null, null)
    val sixth = DListNodeWithChild(6, null, null, null)

    first.next = second
    second.prev = first

    second.next = third
    third.prev = second

    third.next = forth
    forth.prev = third

    forth.next = fifth
    fifth.prev = forth

    fifth.next = sixth
    sixth.prev = fifth

    val seventh = DListNodeWithChild(7, null, null, null)
    val eighth = DListNodeWithChild(8, null, null, null)
    val ninth = DListNodeWithChild(9, null, null, null)
    val tenth = DListNodeWithChild(10, null, null, null)

    third.child = seventh

    seventh.next = eighth
    eighth.prev = seventh

    eighth.next = ninth
    ninth.prev = eighth

    ninth.next = tenth
    tenth.prev = ninth

    val eleventh = DListNodeWithChild(11, null, null, null)
    val twelfth = DListNodeWithChild(12, null, null, null)

    eighth.child = eleventh

    eleventh.next = twelfth
    twelfth.prev = eleventh

    var root = flatten(first)
    while (root != null) {
        print("${root.`val`} -> ")
        root = root.next
    }
}

/**
 * Definition for a Node.
 * class Node(var `val`: Int) {
 *     var prev: Node? = null
 *     var next: Node? = null
 *     var child: Node? = null
 * }
 */

fun flatten(root: DListNodeWithChild?): DListNodeWithChild? {
    if (root == null) {
        return root
    }
    var currentNode = root
//    while (currentNode != null) {
//        println(currentNode.`val`)
//        currentNode = currentNode.next
//    }

    var currentN = root
    while (currentN != null) {

        if (currentN.child != null) {
            val nextNode = currentN.next
            var childNode = currentN.child
            currentN.child = null
            currentN.next = childNode
            childNode?.prev = currentN
            while (childNode?.next != null) {
                childNode = childNode.next
            }

            childNode?.next = nextNode
            nextNode?.prev = childNode
        }
        currentN = currentN.next
    }

    return root
}

//fun mergeNodes(root: DListNodeWithChild?) : DListNodeWithChild? {
//    var cN = root
//    while (cN != null) {
//        cN.next
//    }
//}
