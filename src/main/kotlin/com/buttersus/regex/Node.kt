@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.regex

import kotlin.reflect.full.memberProperties

sealed class Node {
    val properties: Map<String, Node> by lazy {
        this::class.memberProperties
            .filter { it.returnType.classifier == Node::class }
            .associate { it.name to it.getter.call(this) as Node }
    }

    fun isNodeEmpty(): Boolean = this is Empty // Cannot use name `isEmpty` because of `Collection.isEmpty()`
    fun isNodeNotEmpty(): Boolean = !isNodeEmpty() // Cannot use name `isNotEmpty` because of `Collection.isNotEmpty()`

    // Basic nodes
    /**
     * A node that represents success node.
     * It is used as non-failure of the `𝚏` production.
     *
     * @sample `𝚏` = { `𝚙₁`() ?: Empty }
     */
    data object Empty : Node() {
        override fun toString(): String = "Empty"
    }

    abstract class Collection : Node(), kotlin.collections.Collection<Node> {
        abstract override fun toString(): String

        /**
         * Shortcut to get only needed nodes from the group.
         * _(returns only 5 components at most)_
         *
         * Usage:
         * ```
         * val (𝚗₁, 𝚗₃) = Group(𝚗₁, 𝚗₂, 𝚗₃).select(1, 3)
         * ```
         *
         * @param 𝚒s Indices of needed nodes _(starts from 1)_
         * @return List of needed nodes
         */
        abstract fun select(vararg `𝚒s`: Int): List<Node>
    }

    /**
     * An immutable list of nodes, which used to unpack values from the group production.
     * @see Catalog
     */
    open class Group(vararg `𝚗s`: Node) : Collection(), List<Node> by `𝚗s`.toList() {
        override fun toString(): String = "Group(${joinToString(", ")})"
        override fun select(vararg `𝚒s`: Int): List<Node> =
            this.filterIndexed { `𝚒`, _ -> `𝚒` + 1 in `𝚒s` }
    }

    /**
     * A mutable list of nodes, which used to save values from repetitions.
     * - It's supposed to be flexible, so we forbid to use `select` method.
     *
     * @see Group
     */
    class Catalog(vararg `𝚗s`: Node) : Collection(), MutableList<Node> by `𝚗s`.toMutableList() {
        override fun toString(): String = "Catalog(${joinToString(", ")})"
        override fun select(vararg `𝚒s`: Int): Nothing =
            throw UnsupportedOperationException("Catalog cannot be selected")
    }

    /**
     * A classic wrapper for a token.
     * It's the solution to add token fields in properties.
     */
    class Wrapper(val `𝚝`: Token) : Node() {
        override fun toString(): String = "Wrapper($`𝚝`)"
    }

    // Custom nodes
}
