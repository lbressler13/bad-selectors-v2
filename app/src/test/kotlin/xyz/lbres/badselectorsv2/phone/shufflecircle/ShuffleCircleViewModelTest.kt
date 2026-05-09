package xyz.lbres.badselectorsv2.phone.shufflecircle

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.random.ext.nextBoolean
import kotlin.collections.listOf
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ShuffleCircleViewModelTest {
    private val shuffledDigits = listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9)

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = ShuffleCircleViewModel()
        assertEquals(-1, vm.currentDigit)
    }

    @Test
    fun testGetAtIndex() {
        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returns shuffledDigits

            val vm = ShuffleCircleViewModel()
            repeat(10) {
                val result = vm.getDigitAtIndex(it)
                assertEquals(shuffledDigits[it], result)
                assertEquals(shuffledDigits[it], vm.currentDigit)
            }

            // duplicate digit
            var result = vm.getDigitAtIndex(7)
            assertEquals(shuffledDigits[7], result)
            result = vm.getDigitAtIndex(7)
            assertEquals(shuffledDigits[7], result)
        }
    }

    @Test
    fun testGetAtIndexOob() {
        val vm = ShuffleCircleViewModel()
        assertFailsWith<IndexOutOfBoundsException> { vm.getDigitAtIndex(10) }
        assertFailsWith<IndexOutOfBoundsException> { vm.getDigitAtIndex(-1) }
    }

    @Test
    fun testGetAtIndexNullable() {
        mockkStatic("xyz.lbres.kotlinutils.random.ext.RandomExtKt")
        mockkStatic(::createRandom, IntRange::seededRandom, IntRange::seededShuffled)

        val nextBoolValues = listOf(true, false, true, true, false, false, true)
        every { createRandom() } returns mockk {
            every { nextBoolean(any<Float>()) } returnsMany nextBoolValues
        }

        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returns shuffledDigits
            every { IntRange(0, 2).seededRandom() } returns 0

            val vm = ShuffleCircleViewModel()
            // initial, true is ignored
            var result = vm.getDigitAtIndex(4, true)
            assertEquals(shuffledDigits[4], result)

            // non-null digit, false
            result = vm.getDigitAtIndex(6, true)
            assertEquals(shuffledDigits[6], result)

            // null digit, true
            result = vm.getDigitAtIndex(6, true)
            assertNull(result)

            // previous digit is null, true is ignored
            result = vm.getDigitAtIndex(1, true)
            assertEquals(shuffledDigits[1], result)

            // new digit, false
            result = vm.getDigitAtIndex(7, true)
            assertEquals(shuffledDigits[7], result)

            // new digit, false
            result = vm.getDigitAtIndex(0, true)
            assertEquals(shuffledDigits[0], result)

            // null digit, true
            result = vm.getDigitAtIndex(2, true)
            assertNull(result)
        }
    }

    @Test
    fun testUpdateDigits() {
        // mock shuffle digits
        val digitsValues = listOf(
            shuffledDigits,
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
            listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
            listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
        )
        // mock get next shuffle
        val shuffleValues = listOf(0, 2, 1, 1)

        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returnsMany digitsValues
            every { IntRange(0, 2).seededRandom() } returnsMany shuffleValues

            val vm = ShuffleCircleViewModel()
            val updateDigitsAndCheck = { idx: Int ->
                vm.updateDigits()
                checkDigits(vm, digitsValues[idx])
            }

            // init
            checkDigits(vm, digitsValues[0])

            // 0
            updateDigitsAndCheck(1)

            // 2
            updateDigitsAndCheck(1)
            updateDigitsAndCheck(1)
            updateDigitsAndCheck(2)

            // 1
            updateDigitsAndCheck(2)
            updateDigitsAndCheck(3)

            // 1
            updateDigitsAndCheck(3)
            updateDigitsAndCheck(4)
        }
    }

    @Test
    fun testReset() {
        // mock shuffle digits
        val digitsValues = listOf(
            shuffledDigits,
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        )
        // mock get next shuffle
        val shuffleValues = listOf(0, 2, 0)

        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returnsMany digitsValues
            every { IntRange(0, 2).seededRandom() } returnsMany shuffleValues

            val vm = ShuffleCircleViewModel()
            vm.getDigitAtIndex(0)
            assertEquals(shuffledDigits[0], vm.currentDigit)

            vm.reset()
            assertEquals(-1, vm.currentDigit)

            // reset when updateDigits count isn't 0
            vm.updateDigits()
            checkDigits(vm, digitsValues[1])
            vm.reset()

            checkDigits(vm, digitsValues[2])
        }
    }

    private fun checkDigits(vm: ShuffleCircleViewModel, expected: List<Int>) {
        repeat(10) { assertEquals(expected[it], vm.getDigitAtIndex(it)) }
    }
}
