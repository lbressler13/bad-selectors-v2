package xyz.lbres.customview.testutils

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom
import kotlin.random.Random

/**
 * Mock nextDouble returns with given parent width/height
 */
/**
 * Run a test with mocked Random.nextDouble with given parent width/height
 *
 * @param parentWidth [Double]
 * @param parentHeight [Double]
 * @param mockPositions List<Position<Double>>: list of positions to use for mocks
 * @param test: test block to execute
 */
internal fun withMockedNextDouble(
    parentWidth: Double,
    parentHeight: Double,
    mockPositions: List<Position<Double>>,
    test: () -> Unit,
) {
    mockkStatic(::createRandom)
    every { createRandom() } returns mockk<Random> {
        every { nextDouble(0.0, parentWidth) } returnsMany mockPositions.map { it.x }
        every { nextDouble(0.0, parentHeight) } returnsMany mockPositions.map { it.y }
    }
    test()
    unmockkStatic(::createRandom)
}
