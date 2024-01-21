package com.buttersus.fsm

// Represents a transition function: δ ∈ Δ: ℚ × Σ → ℚ
// But ε ∈ Σ is also allowed
class NullableTransition(
    var `𝚚`: State, // 𝚚 ∈ ℚ is the current state
    var `𝚊`: Char?, // 𝚊 ∈ Σ is the input character, ε ∈ Σ is also allowed (null)
    var `𝚚′`: State // 𝚚′ ∈ ℚ is the next state
) {
    // (𝚚ₙ, 𝚊) -> 𝚚ₙ₊₁
    override fun toString() = "(${`𝚚`.toGreek()}, ${`𝚊` ?: "ε"}) -> ${`𝚚′`.toGreek()}"
}