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
    mock: Boolean = true,
    test: () -> Unit,
) {
    if (mock) {
        mockkStatic(::random)
    }
    every { random.nextDouble(0.0, parentWidth) } returnsMany mockPositions.map { it.x }
    every { random.nextDouble(0.0, parentHeight) } returnsMany mockPositions.map { it.y }
    test()
    if (mock) {
        unmockkStatic(::random)
    }
}

/**
 * Run test with mocked angle values, including initial call on IntRange and later calls on set
 *
 * @param mockDegrees List<Int>: list of angles, in degrees, to use for mocks
 * @param test: test block to execute
 */
fun withMockedDegrees(mockDegrees: List<Int>, mock: Boolean = true, test: () -> Unit) {
    if (mock) {
        mockkStatic(IntRange::seededRandom, Set<Int>::seededRandom)
    }
    if (mockDegrees.size == 1) {
        every { any<Set<Int>>().seededRandom() } returns mockDegrees[0]
    } else {
        every { any<Set<Int>>().seededRandom() } returnsMany mockDegrees.subList(1, mockDegrees.size)
    }

    with(mockk<IntRange>()) {
        every { IntRange(0, 360).seededRandom() } returns mockDegrees[0]
        test()
    }

    if (mock) {
        unmockkStatic(IntRange::seededRandom, Set<Int>::seededRandom)
    }
}
