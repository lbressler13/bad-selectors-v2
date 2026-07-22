package xyz.lbres.customview.data

/**
 * A position on the xy plane
 *
 * @param x [T]: position on the x axis
 * @param y [T]: position on the y axis
 */
internal data class Position<T : Number>(val x: T, val y: T) {
    // TODO test
    fun toIntPosition(): Position<Int> = Position(x.toInt(), y.toInt())
}
