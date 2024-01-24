@file:Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")

package com.buttersus.regex

/**
 * This parser has a few ideas:
 * - Memoization for all productions
 * - Left recursion support
 * - PEG parsing
 * - !Each production, which returns null, does not consume the token
 *
 * @constructor Creates a parser with the given logging
 */
class RegexParser {
    // Attributes
    internal lateinit var `𝕋`: Iterator<Token>
    internal val `𝕋′`: ArrayList<Token> = arrayListOf()
    internal var `𝚒`: Index = 0

    operator fun invoke(`𝕋`: Iterator<Token>): RegexParser {
        this.`𝕋` = `𝕋`
        this.`𝕋′`.clear()
        this.`𝕄`.clear()
        return this
    }

    // Token methods
    private fun mark(): Index = `𝚒`
    private fun reset(`𝚒`: Index): Index {
        this.`𝚒` = `𝚒`
        return `𝚒`
    }

    private fun peek(): Token? {
        while (`𝕋′`.size <= `𝚒`) if (`𝕋`.hasNext()) `𝕋′`.add(`𝕋`.next()) else return null
        return `𝕋′`[`𝚒`]
    }

    private fun next(): Token? = peek()?.also { `𝚒`++ }
    private fun Index.toMark(): Index = this.also { reset(it) }

    // Memoization methods
    private val `𝕄`: MutableMap<Index, MutableMap<String, Pair<Node?, Index>>> =
        mutableMapOf() // memoization table

    /**
     * Memoization method for `𝚏` productions,
     * which greatly improves the time complexity: `O(n²)` -> `≈O(n)`
     *
     * Usage:
     * ```
     * // method of Parser: ↓
     * fun parseNode(): Node? = `𝚖`{…}
     * ```
     *
     * @param 𝚕 whether the grammar is left-recursive or not
     * @param 𝚏 the production to memoize (also is the key, which used to retrieve the memoized production)
     * @return the memoized production
     *
     * @see 𝕄
     */
    private fun `𝚖`(`𝚔`: String, `𝚕`: Boolean = false, `𝚏`: () -> Node?): Node? {
        val `𝚒₀` = mark()
        val `𝚖` = `𝕄`.getOrPut(`𝚒`) { mutableMapOf() }
        `𝚖`[`𝚔`]?.run { this.second.toMark(); return this.first }
        if (!`𝚕`) return `𝚏`().also { `𝚖`[`𝚔`] = it to mark() }
        var `𝚗`: Node? = null
        var `𝚒`: Index = `𝚒₀`
        `𝚖`[`𝚔`] = null to `𝚒`
        while (true) {
            reset(`𝚒₀`)
            val `𝚗′` = `𝚏`()
            if (mark() <= `𝚒`) break
            `𝚗` = `𝚗′`
            `𝚒` = mark()
            `𝚖`[`𝚔`] = `𝚗` to `𝚒`
        }
        return `𝚗`.also { `𝚒`.toMark() }
    }

    // Parser methods
    /**
     * Parse method: Match `𝚝` type token
     * - If token type is not matched, then returns `null`
     * - Otherwise returns the token and consumes it
     *
     * Usage:
     * ```
     * val `𝚗` = `≈`(Type.LETTER) ?: return null
     * ```
     *
     * @param 𝚝 the token type to match
     * @return the matched token
     */
    private fun `≈`(`𝚝`: Type): Node? {
        val `𝚝′` = peek()?.`𝚃` ?: return null
        return if (`𝚝′` == `𝚝`) next()?.wrap() ?: return null else null
    }

    /**
     * Parse method: Match `𝚟` value token
     * - If token value is not matched, then returns `null`
     * - Otherwise returns the token and consumes it
     *
     * Usage:
     * ```
     * val `𝚗` = `≡`("a") ?: return null
     * ```
     *
     * @param 𝚟 the token value to match
     * @return the matched token
     */
    private fun `≡`(`𝚟`: String): Node? {
        val `𝚟′` = peek()?.`𝚟` ?: return null
        return if (`𝚟′` == `𝚟`) next()?.wrap() ?: return null else null
    }

    /**
     * Syntax operator: Positive lookahead `?=α`
     * - Expects successful production
     * - In any case does not consume the token
     *
     * Usage:
     * ```
     * val `𝚗` = `≟`{…} ?: return null
     * ```
     * @param 𝚏 the production to lookahead
     * @return the lookahead production
     */
    private fun `≟`(`𝚏`: () -> Node?): Node? {
        val `𝚒` = mark()
        val `𝚗` = `𝚏`()
        return `𝚗`?.also { `𝚒`.toMark() }
    }

    /**
     * Group syntax operator: Positive lookahead `?={α₁, α₂, …}`
     * - Expects successful group of productions
     * - In any case does not consume the token
     *
     * Usage:
     * ```
     * val (`𝚗₁`, `𝚗₃`) = `{?=}`({…}₁, {…}₂, …)?.select(1, 3) ?: return null
     * ```
     *
     * @param 𝚏s the group of productions to lookahead
     * @return the lookahead group of productions
     */
    private fun `{≟}`(vararg `𝚏s`: () -> Node?): Node.Group? {
        val `𝚒` = mark()
        val `𝚗𝚜` = `𝚏s`.map { `𝚏` -> `𝚏`() ?: return null.also { `𝚒`.toMark() } }
        return Node.Group(*`𝚗𝚜`.toTypedArray()).also { `𝚒`.toMark() }
    }

    /**
     * Syntax operator: Negative lookahead `?!α`
     * - Expects failed production
     * - In any case does not consume the token
     *
     * Usage:
     * ```
     * `≠`{…} ?: return null
     * ```
     *
     * @param 𝚏 the production to lookahead
     * @return `Node.Empty` if negative lookahead is successful, otherwise returns `null`
     */
    private fun `≠`(`𝚏`: () -> Node?): Node.Empty? {
        val `𝚒` = mark()
        val `𝚗` = `𝚏`()
        return if (`𝚗` == null) Node.Empty else null.also { `𝚒`.toMark() }
    }

    /**
     * Group syntax operator: Negative lookahead `?!{α₁, α₂, …}`
     * - Expects failed group of productions
     * _(at least one production must be failed)_
     * - In any case does not consume the token
     *
     * Usage:
     * ```
     * `{≠}`({…}₁, {…}₂, …) ?: return null
     * ```
     *
     * @param 𝚏s the group of productions to lookahead
     * @return `Node.Empty` if negative lookahead is successful, otherwise returns `null`
     */
    private fun `{≠}`(vararg `𝚏s`: () -> Node?): Node.Empty? {
        val `𝚒` = mark()
        `𝚏s`.forEach { `𝚏` -> `𝚏`() ?: return Node.Empty.also { `𝚒`.toMark() } }
        return null.also { `𝚒`.toMark() }
    }

    /**
     * Syntax operator: Optional `α?`
     * - If production is not successful, then returns `Empty`
     * - Does not consume the token if production is not successful
     *
     * Usage:
     * ```
     * val `𝚗` = `∅`{…}
     * ```
     *
     * @param 𝚏 the production to make optional
     * @return the optional production
     */
    private fun `∅`(`𝚏`: () -> Node?): Node = `𝚏`() ?: Node.Empty

    /**
     * Group syntax operator: Optional `{α₁, α₂, …}?`
     * - If group of productions is not successful, then returns alternatives,
     * which are passed with pairs.
     * - Does not consume the token if group of productions is not successful
     *
     * Usage:
     * ```
     * val (`𝚗₁`, `𝚗₃`) = `{∅}`({…}₁ to `𝚗₁′`, {…}₂ to `𝚗₂′`, …).select(1, 3)
     * ```
     *
     * @param 𝚏s the group of productions with alternatives
     * @return the optional group of productions
     */
    fun `{∅}₁`(vararg `𝚏s`: Pair<() -> Node?, Node>): Node.Group {
        val `𝚒` = mark()
        val `𝚗𝚜` = `𝚏s`.map { (`𝚏`, _) ->
            `𝚏`() ?: return Node.Group(*`𝚏s`.map { (_, `𝚗`) -> `𝚗` }.toTypedArray())
                .also { `𝚒`.toMark() }
        }
        return Node.Group(*`𝚗𝚜`.toTypedArray())
    }

    /**
     * Group syntax operator: Optional `{α₁, α₂, …}?`
     * - If group of productions is not successful, then returns
     * result of alternative functions.
     * - Does not consume the token if group of productions is not successful
     *
     * Usage:
     * ```
     * val `𝚗` = `{∅}`({…}₁ to {…}₁′, {…}₂ to {…}₂′, …)
     * ```
     *
     * Warning: Functions shouldn't change the state of the parser. _(its index)_
     *
     * @param 𝚏s the group of productions with alternative functions
     * @return the optional group of productions
     */
    fun `{∅}₂`(vararg `𝚏s`: Pair<() -> Node?, () -> Node>): Node.Group {
        val `𝚒` = mark()
        val `𝚗𝚜` = `𝚏s`.map { (`𝚏`, _) ->
            `𝚏`() ?: return Node.Group(*`𝚏s`.map { (_, `𝚗`) -> `𝚗`() }.toTypedArray())
                .also { `𝚒`.toMark() }
        }
        return Node.Group(*`𝚗𝚜`.toTypedArray())
    }

    /**
     * Group syntax operator: Group `{α₁, α₂, …}`
     * - If group of productions is not successful, then returns `null`
     * - Does not consume the token if group of productions is not successful
     *
     * Usage:
     * ```
     * val (`𝚗₁`, `𝚗₃`) = `{…}`({…}₁, {…}₂, …)?.select(1, 3) ?: return null
     * ```
     *
     * @param 𝚏s the group of productions
     * @return the group of productions
     */
    fun `{…}`(vararg `𝚏s`: () -> Node?): Node.Group? {
        val `𝚒` = mark()
        val `𝚗𝚜` = `𝚏s`.map { `𝚏` -> `𝚏`() ?: return null.also { `𝚒`.toMark() } }
        return Node.Group(*`𝚗𝚜`.toTypedArray())
    }

    /**
     * Repetitive syntax operator: One or more `α+`
     *
     * Usage:
     * ```
     * val `𝚗` = `⊕`{…} ?: return null
     * ```
     *
     * @param 𝚏 the production to repeat
     * @return the repeated production or `null` if it is not successful
     */
    private fun `⊕`(`𝚏`: () -> Node?): Node.Catalog? {
        val `ℕ` = Node.Catalog(`𝚏`() ?: return null)
        while (true) {
            val `𝚗` = `𝚏`() ?: return `ℕ`
            `ℕ`.add(`𝚗`)
        }
    }

    /**
     * Repetitive syntax operator: Zero or more `α*`
     *
     * Usage:
     * ```
     * val `𝚗` = `⊛`{…}
     * ```
     *
     * @param 𝚏 the production to repeat
     * @return the repeated production
     */
    private fun `⊛`(`𝚏`: () -> Node?): Node.Catalog {
        val `ℕ` = Node.Catalog()
        while (true) {
            val `𝚗` = `𝚏`() ?: return `ℕ`
            `ℕ`.add(`𝚗`)
        }
    }

    /**
     * Repetitive syntax operator: Separated one or more `α:β+`
     *
     * Usage:
     * ```
     * val `𝚗` = `⊕̂`({…}, {…}) ?: return null
     * ```
     *
     * @param 𝚏 the production to repeat
     * @param 𝚜 the separator production
     * @return the repeated production or `null` if it is not successful
     */
    private fun `⊕̂`(`𝚏`: () -> Node?, `𝚜`: () -> Node?): Node.Catalog? {
        var `𝚒`: Index
        val `ℕ` = Node.Catalog(`𝚏`() ?: return null)
        while (true) {
            `𝚒` = mark()
            `𝚜`() ?: return `ℕ`.also { `𝚒`.toMark() }
            val `𝚗` = `𝚏`() ?: return `ℕ`.also { `𝚒`.toMark() }
            `ℕ`.add(`𝚗`)
        }
    }

    /**
     * Repetitive syntax operator: Separated zero or more `α:β*`
     *
     * Usage:
     * ```
     * val `𝚗` = `⊛̂`{…} ?: return null
     * ```
     *
     * @param 𝚏 the production to repeat
     * @param 𝚜 the separator production
     * @return the repeated production or `null` if it is not successful
     */
    private fun `⊛̂`(`𝚏`: () -> Node?, `𝚜`: () -> Node?): Node.Catalog {
        var `𝚒` = mark()
        val `ℕ` = Node.Catalog()
        while (true) {
            // here we use toMark, because separator production can be consumed before
            val `𝚗` = `𝚏`() ?: return `ℕ`.also { `𝚒`.toMark() }
            `𝚒` = mark()
            `ℕ`.add(`𝚗`)
            `𝚜`() ?: return `ℕ`.also { `𝚒`.toMark() }
        }
    }

    /**
     * Syntax operator: Alternatives `α₁|α₂|…`
     * - If production αᵢ is not successful, then returns production αᵢ₊₁
     * - And so on until the last production
     *
     * Usage:
     * ```
     * val `𝚗` = `⋃`({…}₁, {…}₂, …) ?: return null
     * ```
     *
     * @param 𝚏s the group of productions
     * @return the alternative production or `null` if it is not successful
     */
    private fun `⋃`(vararg `𝚏s`: () -> Node?): Node? {
        return `𝚏s`.firstNotNullOfOrNull { `𝚏` -> `𝚏`() }
    }

    /**
     * Group syntax operator: Alternatives `{α₁, α₂, …}`
     * - If group of productions is not successful, then returns `null`
     * - Does not consume the token if group of productions is not successful
     *
     * Usage:
     * ```
     * val `𝚗` = `{⋃}`({…}₁, {…}₂, …) ?: return null
     * ```
     *
     * @param 𝚏s the group of productions
     * @return the group of productions
     */
    private fun `{⋃}`(vararg `𝚏s`: () -> Node.Group?): Node.Group? {
        val `𝚒` = mark()
        return Node.Group(*`𝚏s`.firstNotNullOfOrNull { `𝚏` ->
            `𝚏`() ?: null.also { `𝚒`.toMark() }
        }?.toTypedArray() ?: return null)
    }

    // Shortcuts
    /** `{α₁ ∣ α₂ ∣ …}?` */
    private fun `⋃∅`(vararg `𝚏s`: () -> Node?): Node? = `⋃`(*`𝚏s`, { Node.Empty })

    /** `{α₁ ∣ α₂ ∣ …}+` */
    private fun `⋃⊕`(vararg `𝚏s`: () -> Node?): Node? = `⊕` { `⋃`(*`𝚏s`) }

    /** `{α₁ ∣ α₂ ∣ …}*` */
    private fun `⋃⊛`(vararg `𝚏s`: () -> Node?): Node = `⊛` { `⋃`(*`𝚏s`) }

    // Custom productions
    fun parse(): Node? = RE()

    private fun `RE`(): Node? = `𝚖`("RE", true) {
        `⋃`(
            // cases==>
            { // basic-RE+:'|'+ => Self
                `⊕̂`(
                    { `⊕` { `basic-RE`() } },
                    { `≡`("|") }
                )
            },
        )   // <==end cases
    }

    private fun `basic-RE`(): Node? = `𝚖`("basic-RE", true) {
        `⋃`(
            // cases==>
            { // elementary-RE {'*' | '+' | '?'} => Kleene(pattern, type = $enumStringMap(KleeneType, '*': STAR, '+': PLUS, '?': QUESTION))
                val (`𝚖₁`, `𝚖₂`) = `{…}`(
                    { `elementary-RE`() },
                    {
                        `⋃`(
                            { `≡`("*") },
                            { `≡`("+") },
                            { `≡`("?") }
                        )
                    }
                )?.select(1, 2) ?: return@`⋃` null
                Node.Kleene(`𝚖₁`, Node.Kleene.KleeneType.fromString(`𝚖₂`.`𝚟`))
            },
            { // elementary-RE => Self
                `elementary-RE`()
            }
        )   // <==end cases
    }

    private fun `elementary-RE`(): Node? = `𝚖`("elementary-RE", true) {
        `⋃`(
            // cases==>
            { // {group | '.' | '$' | negative-set | positive-set | <CHARACTER>} => Self
                `⋃`(
                    { `group`() },
                    { `≡`(".") },
                    { `≡`("$") },
                    { `set`() },
                    { `≈`(Type.CHARACTER) }
                )
            }
        )   // <==end cases
    }

    private fun `group`(): Node? = `𝚖`("group", true) {
        `⋃`(
            // cases==>
            { // '(' .RE ')' => Self
                `{…}`(
                    { `≡`("(") },
                    { `RE`() },
                    { `≡`(")") }
                )?.item(2)
            },
        )   // <==end cases
    }

    private fun `set`(): Node? = `𝚖`("negative-set", true) {
        `⋃`(
            // cases==>
            { // '[' .'^'? .set-items ']' => Set(isPositive = $isEmpty(), items)
                val (`𝚖₁`, `𝚖₂`) = `{…}`(
                    { `≡`("[") },
                    { `∅` { `≡`("^") } },
                    { `set-items`() },
                    { `≡`("]") }
                )?.select(2, 3) ?: return@`⋃` null
                Node.Set(`𝚖₁` is Node.Empty, `𝚖₂`)
            },
        )   // <==end cases
    }

    private fun `set-items`(): Node? = `𝚖`("set-items", true) {
        `⋃`(
            // cases==>
            { // {range | ?!']' <CHARACTER>}+ => Self
                `⋃⊕`(
                    { `range`() },
                    {
                        `{…}`(
                            { `≠` { `≡`("]") } },
                            { `≈`(Type.CHARACTER) }
                        )?.item(2)
                    }
                )
            },
        )   // <==end cases
    }

    private fun `range`(): Node? = `𝚖`("range", true) {
        `⋃`(
            // cases==>
            { // .<CHARACTER> '-' .<CHARACTER> => Range(from, to)
                val (`𝚖₁`, `𝚖₂`) = `{…}`(
                    { `≈`(Type.CHARACTER) },
                    { `≡`("-") },
                    { `≈`(Type.CHARACTER) }
                )?.select(1, 3) ?: return@`⋃` null
                Node.Range(`𝚖₁`, `𝚖₂`)
            },
        )   // <==end cases
    }
}