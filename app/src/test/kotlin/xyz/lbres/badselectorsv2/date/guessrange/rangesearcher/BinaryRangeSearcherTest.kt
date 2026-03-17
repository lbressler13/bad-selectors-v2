package xyz.lbres.badselectorsv2.date.guessrange.rangesearcher

import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BinaryRangeSearcherTest {
    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testGetSingleValue() {
        var searcher = BinaryRangeSearcher(3..3)
        assertEquals(3, searcher.getSingleValue())

        searcher = BinaryRangeSearcher(0..1)
        assertNull(searcher.getSingleValue())

        // have range and split it
    }

    // TODO split into several
    @Test
    fun testGetRange() {

    }

    @Test
    fun testMarkLastRangeCorrect() {

    }

    @Test
    fun testMarkLastRangeIncorrect() {

    }

    @Test
    fun testRestart() {

    }
}
