package xyz.lbres.customview.testutils

import android.view.View
import xyz.lbres.customview.data.Position
import kotlin.test.assertEquals

/**
 * Check that the position of a view matches the given position
 */
internal fun checkViewPosition(view: View, position: Position<Double>) {
    assertEquals(position.x.toInt(), view.left)
    assertEquals(position.y.toInt(), view.top)
}

/**
 * Check that the position history matches the list of positions up to the given index
 */
internal fun checkPositionHistory(positions: List<Position<Double>>, history: List<Position<Int>>) {
    val expectedHistory = positions.map { Position(it.x.toInt(), it.y.toInt()) }
    assertEquals(expectedHistory, history)
}
