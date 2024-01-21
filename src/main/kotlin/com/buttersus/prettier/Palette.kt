@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.buttersus.prettier

/**
 * This class defines a palette object, which serves as a container for a variety of data types.
 * It uses `Map` data structures to store its values,
 * associating them with specific keys to create well-organized blocks of data.
 *
 * @property colors A `Map` where keys are associated with color values.
 * This color dictionary can be used to store and retrieve color-related data.
 * @property bools A `Map` where keys are associated with boolean values.
 * This proves useful for storing and accessing boolean-based data such as true or false state conditions.
 * @property strings A `Map` where keys are associated with string values,
 * serving as a repository for any text-form data and information.
 * @property numbers A `Map` where keys are associated with numeric values.
 * It functions as a placeholder for a wide range of numerical data.
 * @property anythings A `Map` where keys are associated with values of any data type.
 * This universal storage allows for the holding and retrieval of values regardless of their respective data types.
 */
class Palette(
    colors: Map<String, Color> = mapOf(),
    bools: Map<String, Boolean> = mapOf(),
    strings: Map<String, String> = mapOf(),
    numbers: Map<String, Number> = mapOf(),
    anythings: Map<String, Any> = mapOf()
) {
    private val colors: MutableMap<String, Color> = colors.toMutableMap()
    private val bools: MutableMap<String, Boolean> = bools.toMutableMap()
    private val strings: MutableMap<String, String> = strings.toMutableMap()
    private val numbers: MutableMap<String, Number> = numbers.toMutableMap()
    private val anythings: MutableMap<String, Any> = anythings.toMutableMap()

    /**
     * Represents a color palette. Use it to store and retrieve color values.
     */
    val color = _Color()

    @Suppress("ClassName")
    inner class _Color {
        operator fun get(key: String): Color = colors[key]!!
        operator fun set(key: String, value: Color) {
            this@Palette.colors[key] = value
        }
    }

    /**
     * Represents a boolean palette. Use it to store and retrieve boolean values.
     */
    val bool = _Bool()

    @Suppress("ClassName")
    inner class _Bool {
        operator fun get(key: String): Boolean = bools[key]!!
        operator fun set(key: String, value: Boolean) {
            this@Palette.bools[key] = value
        }
    }

    /**
     * Represents a string palette. Use it to store and retrieve string values.
     */
    val string = _String()

    @Suppress("ClassName")
    inner class _String {
        operator fun get(key: String): String = strings[key]!!
        operator fun set(key: String, value: String) {
            this@Palette.strings[key] = value
        }
    }

    /**
     * Represents a number palette. Use it to store and retrieve numeric values.
     */
    val number = _Number()

    @Suppress("ClassName")
    inner class _Number {
        operator fun get(key: String): Number = numbers[key]!!
        operator fun set(key: String, value: Number) {
            this@Palette.numbers[key] = value
        }
    }

    /**
     * Represents a universal palette. Use it to store and retrieve values of any data type.
     */
    val anything = _Anything()

    @Suppress("ClassName")
    inner class _Anything {
        operator fun get(key: String): Any = anythings[key]!!
        operator fun set(key: String, value: Any) {
            this@Palette.anythings[key] = value
        }
    }

    /**
     * Converts the current object to a map representation.
     *
     * @return A `Map` object representing the current object. The keys of the map are the field names of the object
     * and the values are the corresponding field values.
     */
    fun toMap(): Map<String, Any> = mapOf(
        "colors" to colors,
        "bools" to bools,
        "strings" to strings,
        "numbers" to numbers,
        "anythings" to anythings
    )

    /**
     * Updates this Palette instance by merging the contents of another Palette.
     *
     * @param other The other Palette instance to merge with this one.
     */
    fun apply(other: Palette) {
        colors.putAll(other.colors)
        bools.putAll(other.bools)
        strings.putAll(other.strings)
        numbers.putAll(other.numbers)
        anythings.putAll(other.anythings)
    }

    /**
     * Clears all the collections in this object.
     */
    fun clear() {
        colors.clear()
        bools.clear()
        strings.clear()
        numbers.clear()
        anythings.clear()
    }

    /**
     * Replaces the current palette with the colors from the specified palette.
     *
     * @param other The palette to replace with.
     */
    fun replace(other: Palette) {
        clear()
        apply(other)
    }

    companion object {
        val ButterSusPalette = Palette(
            colors = mapOf(
                "general" to Color.FLORAL_WHITE
            )
        )
    }
}