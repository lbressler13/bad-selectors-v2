package xyz.lbres.customview.testutils

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.random
import xyz.lbres.customview.utils.seededRandom

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
    mockkStatic(::random)
    every { random.nextDouble(0.0, parentWidth) } returnsMany mockPositions.map { it.x }
    every { random.nextDouble(0.0, parentHeight) } returnsMany mockPositions.map { it.y }
    test()
    unmockkStatic(::random)
}

fun withMockedDegrees(mockDegrees: List<Int>, test: () -> Unit) {
    mockkStatic(IntRange::seededRandom, Set<Int>::seededRandom)
    val setValues = if (mockDegrees.size == 1) {
        mockDegrees
    } else {
        mockDegrees.subList(1, mockDegrees.size)
    }
    every { any<Set<Int>>().seededRandom() } returnsMany setValues

    with(mockk<IntRange>()) {
        every { IntRange(0, 360).seededRandom() } returns mockDegrees[0]
        test()
    }
    unmockkStatic(IntRange::seededRandom, Set<Int>::seededRandom)
}
