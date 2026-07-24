package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Position
import kotlin.test.assertEquals

/**
 * Check that the position in the movement manager matches the given position
 */
internal fun checkManagerPosition(manager: BaseMovementManager, position: Position<Double>) {
    assertEquals(shorten(position.x), shorten(manager.x))
    assertEquals(shorten(position.y), shorten(manager.y))
}

private fun shorten(double: Double) = (double * 100).toInt() / 100.0
