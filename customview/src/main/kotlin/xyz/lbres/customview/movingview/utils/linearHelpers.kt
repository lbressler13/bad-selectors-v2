package xyz.lbres.customview.movingview.utils

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position

// separate file for testing only

// buffer of 10 degrees in each direction to prevent extremely shallow angles (boring)
private val downAngles = (10..170).toSet()
private val upAngles = (190..350).toSet()
private val leftAngles = (100..260).toSet()
private val rightAngles = (280..360).toSet() + (0..80).toSet()
private val allAngles = (0..360).toSet()

/**
 * Get allowed angles of movement given an initial position and dimensions
 *
 * @param position [Position]: initial position
 * @param dimensions [Dimensions]: dimensions of allowed positions
 * @return [Set]<Int>: allowed angles of movement
 */
internal fun getAllowedAngles(position: Position<Double>, dimensions: Dimensions<Int>): Set<Int> {
    val xAngles = when {
        position.x <= 0.0 -> rightAngles
        position.x >= dimensions.width.toDouble() -> leftAngles
        else -> allAngles
    }

    val yAngles = when {
        position.y <= 0.0 -> downAngles
        position.y >= dimensions.height.toDouble() -> upAngles
        else -> allAngles
    }

    return xAngles intersect yAngles
}
