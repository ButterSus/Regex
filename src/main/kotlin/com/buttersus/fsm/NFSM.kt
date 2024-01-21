@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.fsm

// This NFSM doesn't include any optimizations
class NFSM(
    `ℚ₀`: Iterable<State>,              // ℚ is the set of all states
    `Σ₀`: Iterable<Char?>,              // Σ is the alphabet
    `Δ₀`: Iterable<NullableTransition>, // Δ ∣ δ ∈ Δ: ℚ × Σ → ℚ is the transition function
    `𝚚₀₀`: State,                       // 𝚚₀ ∈ ℚ₀ is the initial state
    `𝔽₀`: Iterable<State>               // 𝔽 ⊆ ℚ₀ is the set of final states
) : FSM {
    // Note, that input data is immutable

    internal val `ℚ` = `ℚ₀`.toMutableSet()
    internal val `Σ` = `Σ₀`.toSet() + null
    internal val `Δ` = mutableSetOf<NullableTransition>()
    internal val `𝚚₀` = `𝚚₀₀`
    internal val `𝔽` = `𝔽₀`.filter { `𝚚` -> `𝚚` in `ℚ` }.toMutableSet()

    companion object {
        // Convert a DFSM to a NFSM (reverse all transitions)
        fun reverse(`𝕄`: DFSM) =
            NFSM(`𝕄`.`ℚ`, `𝕄`.`Σ`, `𝕄`.`Δ`.map { `δ` -> `δ`.reverse() }.toSet(), `𝕄`.`𝚚₀`, `𝕄`.`𝔽`)

        // Convert a regex to a NFSM
        fun from(`𝚛`: String): NFSM {
            val `ℚ` = mutableSetOf<State>()
            val `Σ` = mutableSetOf<Char?>()
            val `Δ` = mutableSetOf<NullableTransition>()
            val `𝚚₀`: State = 0
            val `𝔽` = mutableSetOf<State>()

            return NFSM(`ℚ`, `Σ`, `Δ`, `𝚚₀`, `𝔽`)
        }
    }

    init {
        // Check if initial state is valid
        if (`𝚚₀` !in `ℚ`) throw IllegalArgumentException("Initial state is not in ℚ")

        // Add all valid transitions
        for (`δ₁` in `Δ₀`) if (
            `δ₁`.`𝚚` in `ℚ` && `δ₁`.`𝚊` in `Σ` && `δ₁`.`𝚚′` in `ℚ` // If `δ₁` is valid
        ) `Δ`.add(`δ₁`)

        // Remove unnecessary transitions and final states
        `Δ`.retainAll { `δ` -> `δ`.`𝚚` in `ℚ` && `δ`.`𝚚′` in `ℚ` }
        `𝔽`.retainAll(`ℚ`)
    }

    override operator fun contains(`𝜔`: String) = accepts(`𝚚₀`, `𝜔`)

    private fun accepts(`𝚚₁`: State, `𝜔`: CharSequence): Boolean {
        val `ε-SS` = mutableSetOf<State>()
        var `𝚚ₛ` = setOf(`𝚚₁`)
        while (`𝚚ₛ`.isNotEmpty()) {
            `ε-SS` += `𝚚ₛ`
            `𝚚ₛ` = `𝚚ₛ`.flatMap { `𝚚₂` ->
                `Δ`.filter { `δ` -> `δ`.`𝚚` == `𝚚₂` && `δ`.`𝚊` == null }.map { `δ` -> `δ`.`𝚚′` }
            }.toSet() - `ε-SS`
        }
        if (`𝜔`.isEmpty()) return `ε-SS`.any { `𝚚` -> `𝚚` in `𝔽` }
        // Recursively check if `𝜔` is accepted by `ε-SS`
        val `δs` = `Δ`.filter { `δ` -> `δ`.`𝚚` in `ε-SS` && `δ`.`𝚊` == `𝜔`.first() }
        return `δs`.any { `δ` -> accepts(`δ`.`𝚚′`, `𝜔`.drop(1)) }
    }

    override fun toString() = """
        𝕄 = ⟨ℚ, Σ, Δ, 𝚚₀, 𝔽⟩: 
            ℚ = ${`ℚ`.joinToString(",", "{", "}") { `𝚚` -> `𝚚`.toGreek() }}
            Σ = ${`Σ`.joinToString(prefix = "{", postfix = "}")}
            Δ = ${`Δ`.joinToString("%", "{", "}") { `δ` -> `δ`.toString() }}
            𝚚₀ = ${`𝚚₀`.toGreek()}
            𝔽 = ${`𝔽`.joinToString(",", "{", "}") { `𝚚` -> `𝚚`.toGreek() }}
    """.trimIndent().replace("%", ",\n" + " ".repeat(9))
}