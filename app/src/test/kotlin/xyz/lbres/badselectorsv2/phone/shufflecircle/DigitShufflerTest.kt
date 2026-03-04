package xyz.lbres.badselectorsv2.phone.shufflecircle

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.badselectorsv2.utils.seededRandomHelper
import xyz.lbres.badselectorsv2.utils.seededShuffledHelper
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.random.ext.nextBoolean
import kotlin.collections.listOf
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DigitShufflerTest {
    private val shuffledDigits = listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9)

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val shuffler = DigitShuffler()
        assertEquals(-1, shuffler.digit)
    }

    @Test
    fun testGetAtIndex() {
        // mockkStatic(IntRange::seededShuffled) // digits list
        // mockkStatic(IntRange::seededRandom) // next shuffle

        mockkStatic(::seededShuffledHelper)
        every { seededShuffledHelper(0..9) } returns shuffledDigits

        val shuffler = DigitShuffler()
        repeat(10) {
            val result = shuffler.getAtIndex(it)
            assertEquals(shuffledDigits[it], result)
            assertEquals(shuffledDigits[it], shuffler.digit)
        }

        // duplicate digit
        var result = shuffler.getAtIndex(7)
        assertEquals(shuffledDigits[7], result)
        result = shuffler.getAtIndex(7)
        assertEquals(shuffledDigits[7], result)
    }

    @Test
    fun testGetAtIndexOob() {
        val shuffler = DigitShuffler()
        assertFailsWith<IndexOutOfBoundsException> { shuffler.getAtIndex(10) }
        assertFailsWith<IndexOutOfBoundsException> { shuffler.getAtIndex(-1) }
    }

    @Test
    fun testGetAtIndexNullable() {
        mockkStatic(::createRandom)
        mockkStatic(::seededShuffledHelper)
        mockkStatic(::seededRandomHelper)

        val nextBoolValues = listOf(true, false, true, true, false, false, true)
        val nextFloatValues = nextBoolValues.map { simpleIf(it, 1f, 0f) }
        // TODO fix issue with nextBoolean
        every { createRandom() } returns mockk {
            // every { nextBoolean(any()) } returns false // returnsMany nextBoolValues
            every { nextFloat() } returnsMany nextFloatValues
        }

        every { seededShuffledHelper(0..9) } returns shuffledDigits
        every { seededRandomHelper(0..2) } returns 0

        val shuffler = DigitShuffler()
        // initial, true is ignored
        var result = shuffler.getAtIndex(4, true)
        assertEquals(shuffledDigits[4], result)

        // non-null digit, false
        result = shuffler.getAtIndex(6, true)
        assertEquals(shuffledDigits[6], result)

        // null digit, true
        result = shuffler.getAtIndex(6, true)
        assertNull(result)

        // previous digit is null, true is ignored
        result = shuffler.getAtIndex(1, true)
        assertEquals(shuffledDigits[1], result)

        // new digit, false
        result = shuffler.getAtIndex(7, true)
        assertEquals(shuffledDigits[7], result)

        // new digit, false
        result = shuffler.getAtIndex(0, true)
        assertEquals(shuffledDigits[0], result)

        // null digit, true
        result = shuffler.getAtIndex(2, true)
        assertNull(result)
    }

    @Test
    fun testUpdate() {
        mockkStatic(::seededShuffledHelper)
        mockkStatic(::seededRandomHelper)

        // mock shuffle digits
        val digitsValues = listOf(
            shuffledDigits,
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
            listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
            listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
        )
        every { seededShuffledHelper(0..9) } returnsMany digitsValues

        // mock get next shuffle
        val shuffleValues = listOf(0, 2, 1, 1)
        every { seededRandomHelper(0..2) } returnsMany shuffleValues

        val shuffler = DigitShuffler()
        val updateAndCheck = { idx: Int ->
            shuffler.update()
            checkDigits(shuffler, digitsValues[idx])
        }

        // init
        checkDigits(shuffler, digitsValues[0])

        // 0
        updateAndCheck(1)

        // 2
        updateAndCheck(1)
        updateAndCheck(1)
        updateAndCheck(2)

        // 1
        updateAndCheck(2)
        updateAndCheck(3)

        // 1
        updateAndCheck(3)
        updateAndCheck(4)
    }

    @Test
    fun testReset() {
        mockkStatic(::seededShuffledHelper)
        mockkStatic(::seededRandomHelper)

        // mock shuffle digits
        val digitsValues = listOf(
            shuffledDigits,
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        )
        every { seededShuffledHelper(0..9) } returnsMany digitsValues

        // mock get next shuffle
        val shuffleValues = listOf(0, 2, 0)
        every { seededRandomHelper(0..2) } returnsMany shuffleValues

        val shuffler = DigitShuffler()
        shuffler.getAtIndex(0)
        assertEquals(shuffledDigits[0], shuffler.digit)

        shuffler.reset()
        assertEquals(-1, shuffler.digit)

        // reset when update count isn't 0
        shuffler.update()
        checkDigits(shuffler, digitsValues[1])
        shuffler.reset()

        checkDigits(shuffler, digitsValues[2])
    }

    private fun checkDigits(shuffler: DigitShuffler, expected: List<Int>) {
        repeat(10) { assertEquals(expected[it], shuffler.getAtIndex(it))}
    }
}

