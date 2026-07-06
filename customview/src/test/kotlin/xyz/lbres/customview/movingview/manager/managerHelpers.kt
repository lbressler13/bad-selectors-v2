package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Position
import kotlin.test.assertEquals

/**
 * Check that the position in the movement manager matches the given position
 */
internal fun checkPosition(manager: BaseMovementManager, position: Position<Double>) {
    assertEquals(position.x, manager.x)
    assertEquals(position.y, manager.y)
}

internal fun checkPosition(manager: BaseMovementManager, x: Double, y: Double) = checkPosition(manager, Position(x, y))
