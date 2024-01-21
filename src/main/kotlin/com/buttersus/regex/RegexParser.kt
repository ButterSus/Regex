@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.regex

/**
 * This parser has a few ideas:
 * - Memoization for all productions
 * - Left recursion support
 * - PEG parsing
 * - !Each production, which returns null, does not consume the token
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

    private fun peek(): Token {
        while (`𝕋′`.size <= `𝚒`) `𝕋′`.add(`𝕋`.next())
        return `𝕋′`[`𝚒`]
    }

    private fun next(): Token = peek().also { `𝚒`++ }
    private fun Index.toMark(): Index = this.also { reset(it) }

    // Memoization methods
    private val `𝕄`: MutableMap<Index, MutableMap<() -> Node?, Pair<Node?, Index>>> =
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
     * @param 𝚏 the production to memoize (also is the key, which used to retrieve the memoized production)
     * @param leftRecursive whether the grammar is left-recursive or not
     * @return the memoized production
     *
     * @see 𝕄
     */
    private fun `𝚖`(`𝚏`: () -> Node?, leftRecursive: Boolean = false): Node? {
        var `𝚒` = mark()
        val `𝚖` = `𝕄`.getOrPut(`𝚒`) { mutableMapOf() }
        `𝚖`[`𝚏`]?.run { this.second.toMark(); return this.first }
        if (!leftRecursive) return `𝚏`().also { `𝚖`[`𝚏`] = it to mark() }
        var `𝚗`: Node? = null
        while (true) {
            `𝚖`[`𝚏`] = `𝚗` to `𝚒`.toMark()
            val `𝚗′` = `𝚏`()
            if (mark() <= `𝚒`) break
            `𝚗` = `𝚗′`
            `𝚒` = mark()
            `𝚖`[`𝚏`] = `𝚗` to `𝚒`
        }
        return `𝚗`.also { `𝚒`.toMark() }
    }

    // Parser methods
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
     * @return `Unit` if negative lookahead is successful, otherwise returns `null`
     */
    private fun `≠`(`𝚏`: () -> Node?): Unit? {
        val `𝚒` = mark()
        val `𝚗` = `𝚏`()
        return if (`𝚗` == null) Unit else null.also { `𝚒`.toMark() }
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
     * @return `Unit` if negative lookahead is successful, otherwise returns `null`
     */
    private fun `{≠}`(vararg `𝚏s`: () -> Node?): Unit? {
        val `𝚒` = mark()
        `𝚏s`.forEach { `𝚏` -> `𝚏`() ?: return Unit.also { `𝚒`.toMark() } }
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
    fun `{∅}`(vararg `𝚏s`: Pair<() -> Node?, Node>): Node.Group {
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
    fun `{∅}`(vararg `𝚏s`: Pair<() -> Node?, () -> Node>): Node.Group {
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
     * val `𝚗` = `⊞`{…} ?: return null
     * ```
     *
     * @param 𝚏 the production to repeat
     * @param 𝚜 the separator production
     * @return the repeated production or `null` if it is not successful
     */
    private fun `⊞`(`𝚏`: () -> Node?, `𝚜`: () -> Node?): Node.Catalog? {
        val `ℕ` = Node.Catalog(`𝚏`() ?: return null)
        while (true) {
            val `𝚒` = mark()
            `𝚜`() ?: return `ℕ`
            val `𝚗` = `𝚏`() ?: return `ℕ`.also { `𝚒`.toMark() }
            `ℕ`.add(`𝚗`)
        }
    }

    /**
     * Repetitive syntax operator: Separated zero or more `α:β*`
     *
     * Usage:
     * ```
     * val `𝚗` = `⧆`{…} ?: return null
     * ```
     *
     * @param 𝚏 the production to repeat
     * @param 𝚜 the separator production
     * @return the repeated production or `null` if it is not successful
     */
    private fun `⧆`(`𝚏`: () -> Node?, `𝚜`: () -> Node?): Node.Catalog {
        val `ℕ` = Node.Catalog()
        var `𝚒` = mark()
        while (true) {
            val `𝚗` = `𝚏`() ?: return `ℕ`.also { `𝚒`.toMark() }
            `ℕ`.add(`𝚗`)
            `𝚒` = mark()
            `𝚜`() ?: return `ℕ`
        }
    }
}