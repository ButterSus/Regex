@file:Suppress("MemberVisibilityCanBePrivate")

package com.buttersus.regex

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf

sealed class Node {
    val properties: Map<String, Node> by lazy {
        this::class.declaredMemberProperties
            .filter { (it.returnType.classifier as KClass<*>).isSubclassOf(Node::class) }
            .associate { it.name to it.getter.call(this) as Node }
    }
    open val parameters: Map<String, String> = emptyMap()

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

    /**
     * Any node that represents a list of nodes.
     * - Indexing starts from 1.
     */
    abstract class Collection : Node(), List<Node> {
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
        abstract fun select(vararg `𝚒s`: Int): Collection
        operator fun component1(): Node = this[0]
        operator fun component2(): Node = this[1]
        operator fun component3(): Node = this[2]
        operator fun component4(): Node = this[3]
        operator fun component5(): Node = this[4]
    }

    /**
     * An immutable list of nodes, which used to unpack values from the group production.
     * - Indexing starts from 1.
     *
     * @see Catalog
     */
    class Group(vararg `𝚗s`: Node) : Collection(), List<Node> by `𝚗s`.toList() {
        override fun toString(): String = "Group(${joinToString(", ")})"
        override fun select(vararg `𝚒s`: Int): Group =
            this.filterIndexed { `𝚒`, _ -> `𝚒` + 1 in `𝚒s` }.toGroup()

        fun item(index: Int): Node = this[index - 1]
    }

    /**
     * A mutable list of nodes, which used to save values from repetitions.
     * - It's supposed to be flexible, so we forbid to use `select` method.
     * - Indexing starts from 1.
     *
     * @see Group
     */
    class Catalog(vararg `𝚗s`: Node) : Collection(), MutableList<Node> by `𝚗s`.toMutableList() {
        override fun toString(): String = "Catalog(${joinToString(", ")})"
        override fun select(vararg `𝚒s`: Int): Nothing =
            throw UnsupportedOperationException("Catalog cannot be selected")

        fun item(index: Int): Node = this[index - 1]
    }

    /**
     * A classic wrapper for a token.
     * It's the solution to add token fields in properties.
     */
    class Wrapper(val `𝚝`: Token) : Node() {
        override fun toString(): String = "Wrapper($`𝚝`)"
    }

    // Custom nodes
    data class Set(
        val isPositive: Boolean,
        val items: Catalog,
    ) : Node() {
        override val parameters: Map<String, String> = mapOf(
            "sign" to if (isPositive) "+" else "-",
        )

        override fun toString(): String = "Set(${if (isPositive) "+" else "-"}$items)"
    }

    data class Range(
        val from: Node,
        val to: Node,
    ) : Node() {
        override fun toString(): String = "Range($from, $to)"
    }
}
