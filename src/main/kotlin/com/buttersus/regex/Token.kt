package com.buttersus.regex

data class Token(
    val `𝚃`: Type,
    val `𝚟`: String,
    val `𝚙₁`: Position,
    val `𝚙₂`: Position,
) {
    // Constructor shortcut
    constructor(`𝕃`: RegexLexer, `𝚃`: Type, `𝚟`: String): this(`𝚃`, `𝚟`, `𝕃`.`𝚙`, `𝕃`.`𝚙` + `𝚟`.length - 1)

    // Methods
    override fun toString() = "$`𝚃`($`𝚟`) @ $`𝚙₁`..$`𝚙₂`"
}
