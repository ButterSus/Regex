package com.buttersus.regex

// Index is an integer
typealias Index = Int

// Convert a string to a source
fun String.toSource(): Source = Source(this)

// Convert a string to a position
fun String.toDeltaPosition(`𝚂`: Source): Position = Position(`𝚂`, this.length)

// returns max element index that less or equal to element
fun <T : Comparable<T>> Array<T>.bisect(element: T): Index {
    var left = 0
    var right = lastIndex
    while (left <= right) {
        val mid = (left + right) / 2
        when {
            this[mid] < element -> left = mid + 1
            this[mid] > element -> right = mid - 1
            else -> return mid
        }
    }
    return right
}

// Shortcut to avoid writing `𝚂`.`𝚙`.`𝚒`
fun Regex.matchAt(`𝚙`: Position): MatchResult? = this.matchAt(`𝚙`.`𝚂`.`𝜔`, `𝚙`.`𝚒`)
fun RegexLexer.newToken(`𝚃`: Type, `𝚟`: String): Token = Token(this, `𝚃`, `𝚟`).also {
    this.`𝚙` += `𝚟`.toDeltaPosition(this.`𝚂`)
}

fun Token.wrap(): Node.Wrapper = Node.Wrapper(this)
fun Node.Wrapper.unwrap(): Token = this.`𝚝`
fun List<Node>.toGroup(): Node.Group = Node.Group(*this.toTypedArray())
