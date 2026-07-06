package xyz.lbres.customview.movingview

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom
import kotlin.random.Random
import kotlin.test.assertEquals

internal fun mockRandom(parentWidth: Double, parentHeight: Double, mockPositions: List<Position<Double>>) {
    mockkStatic(::createRandom)
    every { createRandom() } returns mockk<Random> {
        every { nextDouble(0.0, parentWidth) } returnsMany mockPositions.map { it.x }
        every { nextDouble(0.0, parentHeight) } returnsMany mockPositions.map { it.y }
    }
}

internal fun checkPositionHistory(positions: List<Position<Double>>, history: List<Position<Int>>, index: Int) {
    val expectedHistory = positions.subList(0, index).map { Position(it.x.toInt(), it.y.toInt()) }
    assertEquals(expectedHistory, history)
}
