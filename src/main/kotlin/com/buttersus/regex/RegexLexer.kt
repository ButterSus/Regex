@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.regex

class RegexLexer {
    // Attributes
    internal lateinit var `𝚂`: Source
    internal lateinit var `𝚙`: Position

    operator fun invoke(`𝚂`: Source): RegexLexer {
        this.𝚂 = 𝚂
        this.`𝚙` = Position(𝚂)
        return this
    }

    // Methods
    fun tokenize(): Iterator<Token> = iterator {
        while (`𝚙`.isNotAtEnd()) {
            if (`𝚙`.`𝚊` in setOf('[', ']', '(', ')', '{', '}', '|', '*', '+', '?', '^', '$', '.')) {
                yield(newToken(Type.OPERATOR, `𝚙`.`𝚊`.toString()))
                continue
            } // all operators
            if (Regex("""\\.""", RegexOption.DOT_MATCHES_ALL).matchAt(`𝚙`)
                    ?.also { yield(newToken(Type.CONTROL_CHAR, it.value)) } != null
            ) continue // e.g: `\n`, `\ `, `\\`, `\[`
            yield(newToken(Type.LETTER, `𝚙`.`𝚊`.toString()))
        }; yield(newToken(Type.EOF, ""))
    }
}
