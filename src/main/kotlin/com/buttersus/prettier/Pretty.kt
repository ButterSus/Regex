@file:Suppress("unused")

package com.buttersus.prettier

import java.util.Stack


sealed class Pretty {
    /**
     * Adds the given `other` Pretty object to this object.
     *
     * @param other The Pretty object to be added.
     * @return The result of adding `other` to this object.
     */
    protected abstract fun concat(other: Pretty): Pretty
    private lateinit var _palette: Palette
    protected var palette: Palette
        get() = if (this::_palette.isInitialized) _palette else Palette()
        set(value) {
            _palette = value
        }


    /**
     * Adds the other object to the current object.
     * If the other object is null, this method returns the current object.
     * The palette of the other object is set to the same palette as the current object.
     *
     * @param other The object to be added.
     * @return The updated object.
     */
    open operator fun plus(other: Pretty?): Pretty {
        if (other == null) return this
        if (other::_palette.isInitialized) other.palette.replace(palette)
        else other.palette = palette
        return concat(other)
    }

    /**
     * Combines the current Pretty object with the result of executing the provided code block.
     *
     * @param code The code block that returns a Pretty object.
     * @return A new Pretty object that is the concatenation of the current Pretty object and the result of executing the code block.
     */
    open fun with(code: () -> Pretty): Pretty {
        return this + code()
    }

    /**
     * Concatenates the specified `pretty` object with the current `Pretty` object.
     *
     * @param pretty The `Pretty` object to be concatenated with the current `Pretty` object.
     * @return A new `Pretty` object containing the concatenated result.
     */
    open fun with(pretty: Pretty?): Pretty {
        return this + pretty
    }

    abstract override fun toString(): String

    /**
     * Represents a class that derives from Pretty and implements a stack of Pretty objects.
     *
     * @constructor Creates an instance of DerivesStack.
     */
    abstract class DerivesStack : Pretty() {
        protected val stack = Stack<Pretty>()
        override fun concat(other: Pretty): Pretty {
            stack.add(other)
            return this
        }
    }

    /**
     * A class representing a Builder.
     *
     * @param palette The palette to use for building Pretty objects.
     * @constructor Creates a Builder with the given palette.
     *
     * @property stack The stack of Pretty objects.
     */
    class Builder(
        palette: Palette = Palette()
    ) : DerivesStack() {
        init {
            this.palette = palette
        }

        @Deprecated(
            "Use with method instead.",
            ReplaceWith("this.with(other)")
        )
        override operator fun plus(other: Pretty?) = super.plus(other)

        override fun toString(): String {
            return stack.joinToString { it.toString() }
        }
    }

    /**
     * Represents a plain text string without any formatting or styling.
     *
     * @property text The raw text string.
     */
    data class RawText(val text: String) : DerivesStack() {
        override fun concat(other: Pretty): Pretty {
            return when (other) {
                is RawText -> RawText(text + other.text)
                else -> super.concat(other)
            }
        }

        override fun toString(): String {
            return palette.color["general"].cover(text) + stack.joinToString { it.toString() }
        }
    }

    object LineBreak : DerivesStack() {
        override fun toString(): String {
            return "\n" + stack.joinToString { it.toString() }
        }
    }
}