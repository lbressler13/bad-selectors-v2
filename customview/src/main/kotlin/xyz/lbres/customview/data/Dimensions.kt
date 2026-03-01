package xyz.lbres.customview.data

import xyz.lbres.kotlinutils.general.simpleIf

/**
 * Rectangular dimensions
 *
 * @param width [T]
 * @param height [T]
 */
internal data class Dimensions<T>(val width: T, val height: T) where T : Comparable<T>, T : Number {
    val min: T = simpleIf(width < height, width, height)
    val max: T = simpleIf(width > height, width, height)
}
