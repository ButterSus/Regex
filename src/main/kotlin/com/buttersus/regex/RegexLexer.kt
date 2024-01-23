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
            if (Regex("""[*+]\??|\^|\$['`&N]?|\\(?:[tnrfNbBdDsSwWQUL\\]|cX|N{3})""").matchAt(`𝚙`)
                    ?.also { yield(newToken(Type.CHARACTER, it.value)) } != null
            ) continue
            Regex("""\\.""").matchAt(`𝚙`)
                ?.also { throw Exception("Invalid escape sequence") }
            yield(newToken(Type.CHARACTER, `𝚙`.`𝚊`.toString()))
        }; yield(newToken(Type.EOF, ""))
    }
}
