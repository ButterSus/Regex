package com.buttersus.prettier

/**
 * The Prettier interface represents an object that can be prettified.
 *
 * Implementing classes should provide an implementation for the `prettify` function,
 * which returns a prettified version of the object as a string.
 */
interface Prettier {
    fun prettify(): String
}
