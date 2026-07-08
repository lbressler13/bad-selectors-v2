package xyz.lbres.badselectorsv2.testutils

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled

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
 * Run a test with mocked IntRange.seededRandom and IntRange.seededShuffled
 *
 * @param randomMocks Map<IntRange, List<Int>>: map of ranges to the results of invoking seededRandom on the range.
 * Defaults to empty map.
 * @param shuffledMocks Map<IntRange, List<List<Int>>>: map of ranges to the results of invoking seededShuffled on the range.
 * Defaults to empty map.
 * @param test: test block to execute
 */
fun withMockedIntRange(
    randomMocks: Map<IntRange, List<Int>> = emptyMap(),
    shuffledMocks: Map<IntRange, List<List<Int>>> = emptyMap(),
    test: () -> Unit,
) {
    mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
    with(mockk<IntRange>()) {
        randomMocks.forEach { (range, mocks) ->
            every { IntRange(range.first, range.last).seededRandom() } returnsMany mocks
        }
        shuffledMocks.forEach { (range, mocks) ->
            every { IntRange(range.first, range.last).seededShuffled() } returnsMany mocks
        }

        test()
    }
    unmockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
}
