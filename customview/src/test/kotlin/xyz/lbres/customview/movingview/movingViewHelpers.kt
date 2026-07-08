package xyz.lbres.customview.movingview

import android.view.View
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 * Mock nextDouble returns with given parent width/height
 */
internal fun mockNextDouble(parentWidth: Double, parentHeight: Double, mockPositions: List<Position<Double>>) {
    mockkStatic(::createRandom)
    every { createRandom() } returns mockk<Random> {
        every { nextDouble(0.0, parentWidth) } returnsMany mockPositions.map { it.x }
        every { nextDouble(0.0, parentHeight) } returnsMany mockPositions.map { it.y }
    }
}

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
