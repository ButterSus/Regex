@file:Suppress("unused")

package com.buttersus.prettier

/**
 * Represents a color with red, green, and blue components.
 *
 * @property red The red component of the color, ranging from 0 to 255.
 * @property green The green component of the color, ranging from 0 to 255.
 * @property blue The blue component of the color, ranging from 0 to 255.
 *
 * @constructor Creates a new instance of the Color class with the specified red, green, and blue components.
 * @param red The red component of the color.
 * @param green The green component of the color.
 * @param blue The blue component of the color.
 *
 * @throws IllegalArgumentException if the provided red, green, or blue value is outside the valid range of 0 to 255.
 */
data class Color(
    val red: Int,
    val green: Int,
    val blue: Int,
    val isBackground: Boolean = false
) {
    /**
     * Constructs a Color object from a hexadecimal string.
     *
     * @param hex the hexadecimal string representing the color
     */
    constructor(hex: String) : this(
        hex.substring(1, 3).toInt(16),
        hex.substring(3, 5).toInt(16),
        hex.substring(5, 7).toInt(16)
    )

    init {
        require(red in 0..255) { "Red value must be between 0 and 255" }
        require(green in 0..255) { "Green value must be between 0 and 255" }
        require(blue in 0..255) { "Blue value must be between 0 and 255" }
    }

    /**
     * Returns a hexadecimal string representation of the color.
     *
     * @return a hexadecimal string representation of the color
     */
    override fun toString(): String {
        return String.format("#%02x%02x%02x", red, green, blue)
    }

    /**
     * Generates an ASCII string representing the current RGB color values.
     *
     * @return The generated ASCII string.
     */
    fun ascii(): String {
        val r = red / 51
        val g = green / 51
        val b = blue / 51
        val color = 16 + r * 36 + g * 6 + b
        return "\u001b[48;5;${color}m \u001b[0m"
    }

    /**
     * Converts the given object to a string representation and applies the appropriate text color based on the current background color.
     *
     * @param obj the object to be converted and colored
     * @return the string representation of the object with the appropriate text color
     */
    fun cover(obj: Any): String {
        return if (isBackground) {
            "\u001b[48;2;${red};${green};${blue}m${obj}\u001b[0m"
        } else {
            "\u001b[38;2;${red};${green};${blue}m${obj}\u001b[0m"
        }
    }

    /**
     * Adds two Color objects and returns a new Color object with the summed RGB values.
     *
     * @param other The Color object to be added to the current Color.
     * @return A new Color object with the summed RGB values.
     */
    operator fun plus(other: Color): Color {
        return Color(
            red + other.red,
            green + other.green,
            blue + other.blue
        )
    }

    /**
     * Subtracts two Color objects and returns a new Color object with the subtracted RGB values.
     *
     * @param other The Color object to be subtracted from the current Color.
     * @return A new Color object with the subtracted RGB values.
     */
    operator fun minus(other: Color): Color {
        return Color(
            red - other.red,
            green - other.green,
            blue - other.blue
        )
    }

    /**
     * Returns a Color object with the same red, green, and blue values as the current instance.
     *
     * @return The Color object with the same red, green, and blue values.
     */
    operator fun unaryPlus(): Color {
        return Color(
            red,
            green,
            blue
        )
    }

    /**
     * Returns the negation of this Color object.
     *
     * @return the negated Color object with the RGB values subtracted from 255
     */
    operator fun unaryMinus(): Color {
        return Color(
            255 - red,
            255 - green,
            255 - blue
        )
    }

    /**
     * Multiplies each component of the color by the specified factor.
     *
     * @param T The number type of the factor. Must be a subclass of Number.
     * @param other The factor to multiply the color components by.
     * @return A new Color object with each component multiplied by the factor.
     */
    operator fun <T : Number> times(other: T): Color {
        val factor = other.toDouble()
        return Color(
            (red * factor).toInt(),
            (green * factor).toInt(),
            (blue * factor).toInt()
        )
    }

    /**
     * Divides each component of the color by the specified factor.
     *
     * @param T The number type of the factor. Must be a subclass of Number.
     * @param other The factor to divide the color components by.
     * @return A new Color object with each component divided by the factor.
     */
    operator fun <T : Number> div(other: T): Color {
        val factor = other.toDouble()
        return Color(
            (red / factor).toInt(),
            (green / factor).toInt(),
            (blue / factor).toInt()
        )
    }

    /**
     * Returns a new Color object with a composite color value based on the given foreground color and alpha value.
     *
     * @param foreground the foreground color to be used for composite color calculation
     * @param alpha the alpha value to be used for composite color calculation
     * @return a new Color object with the composite color value
     */
    operator fun <T : Number> get(foreground: Color, alpha: T): Color {
        val alphaFactor = alpha.toDouble()
        return Color(
            (foreground.red * alphaFactor + red * (1 - alphaFactor)).toInt(),
            (foreground.green * alphaFactor + green * (1 - alphaFactor)).toInt(),
            (foreground.blue * alphaFactor + blue * (1 - alphaFactor)).toInt()
        )
    }


    /**
     * Returns a new Color object with the red component set to the specified value.
     *
     * @param red The new red component value.
     * @return A new Color object with the red component set to the specified value.
     */
    fun withRed(red: Int): Color {
        return Color(red, green, blue)
    }

    /**
     * Returns a new Color object with the green component set to the specified value.
     *
     * @param green The new green component value.
     * @return A new Color object with the green component set to the specified value.
     */
    fun withGreen(green: Int): Color {
        return Color(red, green, blue)
    }

    /**
     * Returns a new Color object with the blue component set to the specified value.
     *
     * @param blue The new blue component value.
     * @return A new Color object with the blue component set to the specified value.
     */
    fun withBlue(blue: Int): Color {
        return Color(red, green, blue)
    }

    /**
     * Companion object for the Color class.
     *
     * This companion object contains a list of predefined colors as constants.
     * Each constant represents a specific color value in hexadecimal format.
     * These constants can be used to create Color objects without specifying the
     * color value manually.
     */
    companion object {
        val ALICE_BLUE = Color("#F0F8FF")
        val ANTIQUE_WHITE = Color("#FAEBD7")
        val AQUA = Color("#00FFFF")
        val AQUAMARINE = Color("#7FFFD4")
        val AZURE = Color("#F0FFFF")
        val BEIGE = Color("#F5F5DC")
        val BISQUE = Color("#FFE4C4")
        val BLACK = Color("#000000")
        val BLANCHED_ALMOND = Color("#FFEBCD")
        val BLUE = Color("#0000FF")
        val BLUE_VIOLET = Color("#8A2BE2")
        val BROWN = Color("#A52A2A")
        val BURLY_WOOD = Color("#DEB887")
        val CADET_BLUE = Color("#5F9EA0")
        val CHARTREUSE = Color("#7FFF00")
        val CHOCOLATE = Color("#D2691E")
        val CORAL = Color("#FF7F50")
        val CORNFLOWER_BLUE = Color("#6495ED")
        val CORNSILK = Color("#FFF8DC")
        val CRIMSON = Color("#DC143C")
        val CYAN = Color("#00FFFF")
        val DARK_BLUE = Color("#00008B")
        val DARK_CYAN = Color("#008B8B")
        val DARK_GOLDENROD = Color("#B8860B")
        val DARK_GRAY = Color("#A9A9A9")
        val DARK_GREEN = Color("#006400")
        val DARK_KHAKI = Color("#BDB76B")
        val DARK_MAGENTA = Color("#8B008B")
        val DARK_OLIVE_GREEN = Color("#556B2F")
        val DARK_ORANGE = Color("#FF8C00")
        val DARK_ORCHID = Color("#9932CC")
        val DARK_RED = Color("#8B0000")
        val DARK_SALMON = Color("#E9967A")
        val DARK_SEA_GREEN = Color("#8FBC8F")
        val DARK_SLATE_BLUE = Color("#483D8B")
        val DARK_SLATE_GRAY = Color("#2F4F4F")
        val DARK_TURQUOISE = Color("#00CED1")
        val DARK_VIOLET = Color("#9400D3")
        val DEEP_PINK = Color("#FF1493")
        val DEEP_SKY_BLUE = Color("#00BFFF")
        val DIM_GRAY = Color("#696969")
        val DODGER_BLUE = Color("#1E90FF")
        val FIREBRICK = Color("#B22222")
        val FLORAL_WHITE = Color("#FFFAF0")
        val FOREST_GREEN = Color("#228B22")
        val FUCHSIA = Color("#FF00FF")
        val GAINSBORO = Color("#DCDCDC")
        val GHOST_WHITE = Color("#F8F8FF")
        val GOLD = Color("#FFD700")
        val GOLDENROD = Color("#DAA520")
        val GRAY = Color("#808080")
        val GREEN = Color("#008000")
        val GREEN_YELLOW = Color("#ADFF2F")
        val HONEYDEW = Color("#F0FFF0")
        val HOT_PINK = Color("#FF69B4")
        val INDIAN_RED = Color("#CD5C5C")
        val INDIGO = Color("#4B0082")
        val IVORY = Color("#FFFFF0")
        val KHAKI = Color("#F0E68C")
        val LAVENDER = Color("#E6E6FA")
        val LAVENDER_BLUSH = Color("#FFF0F5")
        val LAWN_GREEN = Color("#7CFC00")
        val LEMON_CHIFFON = Color("#FFFACD")
        val LIGHT_BLUE = Color("#ADD8E6")
        val LIGHT_CORAL = Color("#F08080")
        val LIGHT_CYAN = Color("#E0FFFF")
        val LIGHT_GOLDENROD_YELLOW = Color("#FAFAD2")
        val LIGHT_GRAY = Color("#D3D3D3")
        val LIGHT_GREEN = Color("#90EE90")
        val LIGHT_PINK = Color("#FFB6C1")
        val LIGHT_SALMON = Color("#FFA07A")
        val LIGHT_SEA_GREEN = Color("#20B2AA")
        val LIGHT_SKY_BLUE = Color("#87CEFA")
        val LIGHT_SLATE_GRAY = Color("#778899")
        val LIGHT_STEEL_BLUE = Color("#B0C4DE")
        val LIGHT_YELLOW = Color("#FFFFE0")
        val LIME = Color("#00FF00")
        val LIME_GREEN = Color("#32CD32")
        val LINEN = Color("#FAF0E6")
        val MAGENTA = Color("#FF00FF")
        val MAROON = Color("#800000")
        val MEDIUM_AQUAMARINE = Color("#66CDAA")
        val MEDIUM_BLUE = Color("#0000CD")
        val MEDIUM_ORCHID = Color("#BA55D3")
        val MEDIUM_PURPLE = Color("#9370DB")
        val MEDIUM_SEA_GREEN = Color("#3CB371")
        val MEDIUM_SLATE_BLUE = Color("#7B68EE")
        val MEDIUM_SPRING_GREEN = Color("#00FA9A")
        val MEDIUM_TURQUOISE = Color("#48D1CC")
        val MEDIUM_VIOLET_RED = Color("#C71585")
        val MIDNIGHT_BLUE = Color("#191970")
        val MINT_CREAM = Color("#F5FFFA")
        val MISTY_ROSE = Color("#FFE4E1")
        val MOCCASIN = Color("#FFE4B5")
        val NAVAJO_WHITE = Color("#FFDEAD")
        val NAVY = Color("#000080")
        val OLD_LACE = Color("#FDF5E6")
        val OLIVE = Color("#808000")
        val OLIVE_DRAB = Color("#6B8E23")
        val ORANGE = Color("#FFA500")
        val ORANGE_RED = Color("#FF4500")
        val ORCHID = Color("#DA70D6")
        val PALE_GOLDENROD = Color("#EEE8AA")
        val PALE_GREEN = Color("#98FB98")
        val PALE_TURQUOISE = Color("#AFEEEE")
        val PALE_VIOLET_RED = Color("#DB7093")
        val PAPAYA_WHIP = Color("#FFEFD5")
        val PEACH_PUFF = Color("#FFDAB9")
        val PERU = Color("#CD853F")
        val PINK = Color("#FFC0CB")
        val PLUM = Color("#DDA0DD")
        val POWDER_BLUE = Color("#B0E0E6")
        val PURPLE = Color("#800080")
        val RED = Color("#FF0000")
        val ROSY_BROWN = Color("#BC8F8F")
        val ROYAL_BLUE = Color("#4169E1")
        val SADDLE_BROWN = Color("#8B4513")
        val SALMON = Color("#FA8072")
        val SANDY_BROWN = Color("#F4A460")
        val SEA_GREEN = Color("#2E8B57")
        val SEA_SHELL = Color("#FFF5EE")
        val SIENNA = Color("#A0522D")
        val SILVER = Color("#C0C0C0")
        val SKY_BLUE = Color("#87CEEB")
        val SLATE_BLUE = Color("#6A5ACD")
        val SLATE_GRAY = Color("#708090")
        val SNOW = Color("#FFFAFA")
        val SPRING_GREEN = Color("#00FF7F")
        val STEEL_BLUE = Color("#4682B4")
        val TAN = Color("#D2B48C")
        val TEAL = Color("#008080")
        val THISTLE = Color("#D8BFD8")
        val TOMATO = Color("#FF6347")
        val TURQUOISE = Color("#40E0D0")
        val VIOLET = Color("#EE82EE")
        val WHEAT = Color("#F5DEB3")
        val WHITE = Color("#FFFFFF")
        val WHITE_SMOKE = Color("#F5F5F5")
        val YELLOW = Color("#FFFF00")
        val YELLOW_GREEN = Color("#9ACD32")
    }
}