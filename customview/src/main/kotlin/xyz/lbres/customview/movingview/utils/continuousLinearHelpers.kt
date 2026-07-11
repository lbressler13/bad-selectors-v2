package xyz.lbres.customview.movingview.utils

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position

// TODO does this need to be a separate file? maybe best for testing

private val downAngles = (10..170).toSet()
private val upAngles = (190..350).toSet()
private val leftAngles = (100..260).toSet()
private val rightAngles = (280..360).toSet() + (0..80).toSet()
private val allAngles = (0..360).toSet()

/**
 * TODO update
 * Get bounds for allowed motion given an initial position and set of dimensions
 *
 * @param position [Position]: initial position
 * @param dimensions [Dimensions]: dimensions of allowed positions
 * @return [Bounds]: information about allowed allowed movement to prevent position from exceeding dimensions
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

/**
 * TODO
 */
internal fun validPosition(position: Position<Double>, dimensions: Dimensions<Int>): Boolean {
    val allowedAngles = getAllowedAngles(position, dimensions)
    val hasLeftAngles = (allowedAngles intersect leftAngles).isNotEmpty()
    val hasRightAngles = (allowedAngles intersect rightAngles).isNotEmpty()
    val hasUpAngles = (allowedAngles intersect upAngles).isNotEmpty()
    val hasDownAngles = (allowedAngles intersect downAngles).isNotEmpty()

    val validLeft = !hasLeftAngles || position.x <= dimensions.width.toDouble()
    val validRight = !hasRightAngles || position.x >= 0.0
    val validUp = !hasUpAngles || position.y >= 0.0
    val validDown = !hasDownAngles || position.y <= dimensions.height.toDouble()

    return validLeft && validRight && validUp && validDown
}
