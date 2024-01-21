@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.fsm

import java.util.*

// This DFSM includes the following optimizations:
// - Remove unreachable states
// - Remove dead states
// - Remove non-distinguishable states
class DFSM(
    `ℚ₀`: Iterable<State>,      // ℚ is the set of all states
    `Σ₀`: Iterable<Char>,       // Σ is the alphabet
    `Δ₀`: Iterable<Transition>, // Δ ∣ δ ∈ Δ: ℚ × Σ → ℚ is the transition function
    `𝚚₀₀`: State,               // 𝚚₀ ∈ ℚ₀ is the initial state
    `𝔽₀`: Iterable<State>,      // 𝔽 ⊆ ℚ₀ is the set of final states
) : FSM {
    // Note, that input data is immutable

    internal val `ℚ` = `ℚ₀`.toMutableSet()
    internal val `Σ` = `Σ₀`.toSet()
    internal val `Δ` = mutableSetOf<Transition>()
    internal var `𝚚₀` = `𝚚₀₀`
    internal val `𝔽` = `𝔽₀`.filter { `𝚚` -> `𝚚` in `ℚ` }.toMutableSet()

    companion object {
        // Convert a NFSM to a DFSM using the powerset construction
        fun from(`𝕄`: NFSM): DFSM {
            fun `ε-SS`(`𝚚₀`: Set<State>): MutableSet<State> {
                val `𝚚ₙ` = `𝚚₀`.toMutableSet()
                for (`𝚚` in `𝚚ₙ`) {
                    val `ε-SS` = mutableSetOf<State>()
                    var `𝚚ₛ` = setOf(`𝚚`)
                    while (`𝚚ₛ`.isNotEmpty()) {
                        `ε-SS` += `𝚚ₛ`
                        `𝚚ₛ` = `𝚚ₛ`.flatMap { `𝚚₂` ->
                            `𝕄`.`Δ`.filter { `δ` -> `δ`.`𝚚` == `𝚚₂` && `δ`.`𝚊` == null }.map { `δ` -> `δ`.`𝚚′` }
                        }.toSet() - `ε-SS`
                    }
                    `𝚚ₙ` += `ε-SS`
                }
                return `𝚚ₙ`
            }

            val `ℚₘ` = mutableSetOf<Set<State>>()
            val `Σₘ` = `𝕄`.`Σ`
            val `Δₘ` = mutableSetOf<SetTransition>()
            val `𝚚₀ₘ` = `ε-SS`(setOf(`𝕄`.`𝚚₀`))
            val `𝔽ₘ` = mutableSetOf<Set<State>>()

            val `ℚᵣ` = mutableSetOf<Set<State>>()
            val `ℚₙ`: Queue<MutableSet<State>> = LinkedList(listOf(`𝚚₀ₘ`))
            while (`ℚₙ`.isNotEmpty()) {
                // Get the next state `𝚚ₙ`
                val `𝚚ₙ` = `ℚₙ`.remove()

                `ℚᵣ` += `𝚚ₙ`
                `ℚₘ` += `𝚚ₙ`

                // If `𝚚ₙ` is a final state, add it to `𝔽ₘ`
                if (`𝚚ₙ`.any { `𝚚` -> `𝚚` in `𝕄`.`𝔽` }) `𝔽ₘ` += `𝚚ₙ`

                // For each character in `Σₘ`, find the all possible next states
                val `Σₙ` = `𝕄`.`Δ`.filter { `δ` -> `δ`.`𝚚` in `𝚚ₙ` }.map { `δ` -> `δ`.`𝚊` }.toSet()
                for (`𝚊` in `Σₙ`) {
                    if (`𝚊` == null) continue
                    val `𝚚ₙ′` = `ε-SS`(
                        `𝕄`.`Δ`
                            .filter { `δ` -> `δ`.`𝚚` in `𝚚ₙ` && `δ`.`𝚊` == `𝚊` }
                            .sortedBy { `δ` -> `δ`.`𝚚′` }
                            .map { `δ` -> `δ`.`𝚚′` }.toSet()
                    )
                    `Δₘ` += SetTransition(`𝚚ₙ`, `𝚊`, `𝚚ₙ′`)
                    if (`𝚚ₙ′` !in `ℚᵣ`) `ℚₙ`.add(`𝚚ₙ′`)
                }
            }

            // Transform set of states -> single state
            // 1. Create a map from set of states -> single state
            val `ℚₜ`: Map<Set<State>, State> = `ℚₘ`.mapIndexed { `𝚚ₙ`, `𝚚` -> `𝚚` to `𝚚ₙ` }.toMap()

            // 2. Replace all states in `ℚₘ`, `Δₘ` `𝚚₀ₘ`, `𝔽ₘ` with their corresponding single state
            val `ℚₘ′` = `ℚₘ`.map { `𝚚` -> `ℚₜ`[`𝚚`]!! }.toSet()
            val `Σₘ′`: Set<Char> = `Σₘ`.filterNotNull().toSet()
            val `Δₘ′` = `Δₘ`.map { `δ` -> Transition(`ℚₜ`[`δ`.`𝚚`]!!, `δ`.`𝚊`, `ℚₜ`[`δ`.`𝚚′`]!!) }.toSet()
            val `𝚚₀ₘ′` = `ℚₜ`[`𝚚₀ₘ`]!!
            val `𝔽ₘ′` = `𝔽ₘ`.map { `𝚚` -> `ℚₜ`[`𝚚`]!! }.toSet()

            // 3. Return the new DFSM
            return DFSM(`ℚₘ′`, `Σₘ′`, `Δₘ′`, `𝚚₀ₘ′`, `𝔽ₘ′`)
        }

        // Convert a regex to a DFSM
        fun from(`𝚛`: String): DFSM = from(NFSM.from(`𝚛`))
    }

    init {
        // Check if initial state is valid
        if (`𝚚₀` !in `ℚ`) throw IllegalArgumentException("Initial state is not in ℚ")

        // Add all valid transitions
        for (`δ₁` in `Δ₀`) if (
            `δ₁`.`𝚚` in `ℚ` && `δ₁`.`𝚊` in `Σ` && `δ₁`.`𝚚′` in `ℚ` && // If `δ₁` is valid
            `Δ`.none { `δ₂` -> `δ₂`.`𝚚` == `δ₁`.`𝚚` && `δ₂`.`𝚊` == `δ₁`.`𝚊` } // and `Δ` does not contain `δ₁`
        ) `Δ`.add(`δ₁`)

        // Unreachable states (https://en.wikipedia.org/wiki/DFA_minimization#Unreachable_states)
        run {
            val `ℚᵣ` = mutableSetOf(`𝚚₀`)
            var `ℚₙ` = setOf(`𝚚₀`)
            while (`ℚₙ`.isNotEmpty()) {
                val `ℚₙ′` = mutableSetOf<State>()
                for ((`𝚚`, `𝚊`) in `ℚₙ`.flatMap { `𝚚` -> `Σ`.map { `𝚊` -> `𝚚` to `𝚊` } }) {
                    val `δ` = `Δ`.firstOrNull { `δ₂` -> `δ₂`.`𝚚` == `𝚚` && `δ₂`.`𝚊` == `𝚊` } ?: continue
                    `ℚₙ′`.add(`δ`.`𝚚′`)
                }
                `ℚₙ` = `ℚₙ′` - `ℚᵣ`
                `ℚᵣ` += `ℚₙ`
            }
            `ℚ`.retainAll(`ℚᵣ`)
        }

        // Dead states (https://en.wikipedia.org/wiki/DFA_minimization#Dead_states)
        run {
            val cache = mutableMapOf<State, Boolean>()
            fun isDead(`𝚚`: State): Boolean = cache.getOrPut(`𝚚`) {
                (`𝚚` !in `𝔽`) && `Δ`.none { `δ` -> (`δ`.`𝚚` == `𝚚`) && (`δ`.`𝚚′` != `𝚚`) && !isDead(`δ`.`𝚚′`) }
            }
            `ℚ`.removeIf(::isDead)
        }

        // Non-distinguishable states (https://en.wikipedia.org/wiki/DFA_minimization#Indistinguishable_states)
        run {
            // 1. Hopcroft's algorithm to calculate `ℙ` - partitions of `ℚ`
            val `ℙ` = mutableSetOf(`𝔽`, `ℚ` - `𝔽`)
            val `𝕊`: Queue<Pair<Set<State>, Char>> = `Σ`.flatMap { `𝚊` ->
                listOf(`𝔽` to `𝚊`, `ℚ` - `𝔽` to `𝚊`)
            }.toCollection(LinkedList())
            while (`𝕊`.isNotEmpty()) {
                val (`ℂ`, `𝚊`) = `𝕊`.remove()
                for (`ℝ` in `ℙ`) {
                    val (`ℝ₁`, `ℝ₂`) = `ℝ`
                        .partition { `𝚚` -> `Δ`.any { `δ` -> `δ`.`𝚚` == `𝚚` && `δ`.`𝚊` == `𝚊` && `δ`.`𝚚′` in `ℂ` } }
                        .let { (`ℝ₁`, `ℝ₂`) -> `ℝ₁`.toSet() to `ℝ₂`.toSet() }
                    if (`ℝ₁`.isEmpty() || `ℝ₂`.isEmpty()) continue
                    `Σ`.forEach { `𝚌` -> `𝕊`.run { add(`ℝ₁` to `𝚌`); add(`ℝ₂` to `𝚌`) } }
                }
            }

            // 2. Replace all non-distinguishable states with a single state
            val `ℚᵣ×ℚ`: Map<State, State> = `ℙ`.flatMap { `ℝ` -> `ℝ`.map { `𝚚` -> `𝚚` to `ℝ`.first() } }.toMap()
            `ℚ`.retainAll(`ℙ`.map { `ℝ` -> `ℝ`.first() }.toSet())
            `Δ`.forEach { `δ` -> `δ`.run { `𝚚` = `ℚᵣ×ℚ`[`𝚚`]!!; `𝚚′` = `ℚᵣ×ℚ`[`𝚚′`]!! } }
            `𝚚₀` = `ℚᵣ×ℚ`[`𝚚₀`]!!
            `𝔽`.retainAll(`ℚ`)

            // 3. Remove duplicate transitions
            val `Δ′` = mutableSetOf<Transition>()
            for (`δ₁` in `Δ`) {
                val `δ₂` = `Δ′`.firstOrNull { `δ` -> `δ`.`𝚚` == `δ₁`.`𝚚` && `δ`.`𝚊` == `δ₁`.`𝚊` }
                if (`δ₂` == null) `Δ′`.add(`δ₁`) else `δ₂`.`𝚚′` = `δ₁`.`𝚚′`
            }
            `Δ`.run { clear(); addAll(`Δ′`) }
        }

        // After removing unnecessary states, remove unnecessary transitions and final states
        `Δ`.retainAll { `δ` -> `δ`.`𝚚` in `ℚ` && `δ`.`𝚚′` in `ℚ` }
        `𝔽`.retainAll(`ℚ`)
    }

    override operator fun contains(`𝜔`: String): Boolean {
        var `𝚚` = `𝚚₀`
        // For each character in `𝜔`, find the next state
        for (`𝚊` in `𝜔`)
            `𝚚` = `Δ`.firstOrNull { `δ₂` -> `δ₂`.`𝚚` == `𝚚` && `δ₂`.`𝚊` == `𝚊` }?.`𝚚′` ?: return false
        return `𝚚` in `𝔽`
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
