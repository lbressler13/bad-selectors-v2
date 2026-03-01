package xyz.lbres.customview.ext.typedarray

import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFloatOrThrow
import androidx.core.content.res.getIntOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import xyz.lbres.kotlinutils.general.tryOrDefault

/**
 * Retrieve the integer value for the attribute at [index] or return `null` if it is not defined.
 *
 * @param index [StyleableRes]: attribute to retrieve
 * @return [Int]?: the integer value of at [index] if it could be retrieved successfully, otherwise `null`
 */
internal fun TypedArray.getIntOrNull(@StyleableRes index: Int): Int? = getValueOrNull { getIntOrThrow(index) }

/**
 * Retrieve the dimension pixel size value for the attribute at [index] or return `null` if it is not defined.
 *
 * @param index [StyleableRes]: attribute to retrieve
 * @return [Int]?: the dimension pixel size value of at [index] if it could be retrieved successfully, otherwise `null`
 */
internal fun TypedArray.getDimensionPixelSizeOrNull(@StyleableRes index: Int): Int? {
    return getValueOrNull { getDimensionPixelSizeOrThrow(index) }
}

/**
 * Retrieve the float value for the attribute at [index] or return `null` if it is not defined.
 *
 * @param index [StyleableRes]: attribute to retrieve
 * @return [Float]?: the float value of at [index] if it could be retrieved successfully, otherwise `null`
 */
internal fun TypedArray.getFloatOrNull(@StyleableRes index: Int): Float? = getValueOrNull { getFloatOrThrow(index) }

/**
 * Retrieve an int angle value in degrees using the attribute at [index], and convert it to an equivalent angle in radians.
 * Returns `null` if the attribute is not defined.
 *
 * @param index [StyleableRes]: attribute to retrieve. Expected to contain an angle in degrees
 * @return [Double]?: the angle at [index] parsed into radians, or `null` if the attribute could not be retrieved
 */
internal fun TypedArray.getRadiansOrNull(@StyleableRes index: Int): Double? {
    val degrees = getIntOrNull(index)
    if (degrees == null) {
        return degrees
    }

    return Math.toRadians(degrees.toDouble())
}

/**
 * Retrieve the resource ID value for the attribute at [index] or return `null` if it is not defined.
 *
 * @param index [StyleableRes]: attribute to retrieve
 * @return [Int]?: the value of the resource ID at [index] if it could be retrieved successfully, otherwise `null`
 */
fun TypedArray.getResourceIdOrNull(@StyleableRes index: Int): Int? = getValueOrNull { getResourceIdOrThrow(index) }

/**
 * Get a value using a provided function, and return `null` if there is an error in the provided function
 *
 * @param getValue: function to retrieve value
 * @return T?: result of [getValue], or `null` if [getValue] throws an [IllegalArgumentException]
 */
private fun <T> getValueOrNull(getValue: () -> T): T? {
    return tryOrDefault(null, listOf(IllegalArgumentException::class)) {
        getValue()
    }
}
