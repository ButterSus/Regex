package com.buttersus.fsm

// State is an integer
typealias State = Int

// Prints state as a subscript: 𝚚₀, 𝚚₁, …
fun State.toGreek(): String = "𝚚" + this.toString().map(
    mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉"
    )::get
).joinToString()

// Syntax sugar: 𝜔 ∈ 𝕄
infix fun String.`∈`(`𝕄`: FSM) = this in `𝕄`

@Suppress("unused")
val ε = null // ε ∈ Σ is also allowed
