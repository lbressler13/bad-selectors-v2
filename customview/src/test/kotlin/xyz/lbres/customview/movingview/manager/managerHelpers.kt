package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Position
import kotlin.test.assertEquals

/**
 * Check that the position in the movement manager matches the given position
 */
internal fun checkManagerPosition(manager: BaseMovementManager, position: Position<Double>) {
    assertEquals(position.x, manager.x)
    assertEquals(position.y, manager.y)
}
