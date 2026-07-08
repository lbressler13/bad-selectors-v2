package xyz.lbres.customview.testutils

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom
import kotlin.random.Random

// TODO shared testutils

/**
 * Mock the Log class. Must be unmocked at the end of each test
 */
fun mockLog() {
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 1
    every { Log.d(any(), any()) } returns 1
    every { Log.i(any(), any()) } returns 1
    every { Log.w(any(), any<String>()) } returns 1
    every { Log.w(any(), any<Throwable>()) } returns 1
    every { Log.e(any(), any()) } returns 1
}

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
