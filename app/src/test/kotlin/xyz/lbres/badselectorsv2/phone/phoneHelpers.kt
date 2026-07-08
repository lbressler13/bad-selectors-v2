package xyz.lbres.badselectorsv2.phone

import xyz.lbres.badselectorsv2.testutils.withMockedIntRange

/**
 * Run a test with mocked IntRange.seededRandom and IntRange.seededShuffled, specifically mocking range of digits in a phone number
 *
 * @param randomMocks List<Int>: list of results of invoking seededRandom. Defaults to empty list.
 * @param randomMocks List<List<Int>>: list of results of invoking seededShuffled. Defaults to empty list.
 * @param test: test block to execute
 */
fun withMockedPhoneRange(
    randomMocks: List<Int> = emptyList(),
    shuffledMocks: List<List<Int>> = emptyList(),
    test: () -> Unit,
) {
    withMockedIntRange(mapOf(0..9 to randomMocks), mapOf(0..9 to shuffledMocks), test)
}
