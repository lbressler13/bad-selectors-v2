package xyz.lbres.badselectorsv2.phone.shufflecircle

import io.mockk.EqMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.random.ext.nextBoolean
import kotlin.collections.listOf
import kotlin.test.AfterTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ShuffleCircleViewModelTest {
    private val shuffledDigits = listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9)
    private val fullNumberRepeats = 1..3

    // @BeforeTest
    // fun setupTest() {
    // mockkConstructor(PhoneNumberGenerator::class)
    // }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = ShuffleCircleViewModel()
        assertEquals(-1, vm.currentDigit)
        digitsRange.forEach { vm.getDigitAtIndex(it) in digitsRange }
    }

    @Test
    @Ignore
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
    fun testIncrementCurrentIndex() {
        // TODO
    }

    @Test
    @Ignore
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
    @Ignore
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
    fun testResetData() {
        // mock shuffle digits
        val digitsValues = listOf(
            shuffledDigits,
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        )
        val forceDigitsValues = listOf(
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        )
        setUpdateMocks(digitsValues, forceDigitsValues)

        // initialize vm
        val vm = ShuffleCircleViewModel()
        checkDigits(vm, digitsValues[0])

        // set index 0 = 5
        vm.setCurrentDigit(5)

        // increment counter & force update
        vm.incrementCurrentIndex()

        // set index 1 = 2
        vm.setCurrentDigit(2)

        // validate saved values
        assertEquals(5, vm.getDigitAt(0))
        assertEquals(2, vm.getDigitAt(1))

        // validate current index
        assertEquals(1, vm.currentIndex)

        // validate generated number
        checkDigits(vm, forceDigitsValues[0])

        // validate that the must recently returned value is 0
        vm.getDigitAtIndex(0)
        assertEquals(0, vm.currentDigit)

        vm.resetData()
        assertEquals(0, vm.currentIndex)
        assertEquals(vm.currentDigit, -1)
        checkDigits(vm, digitsValues[1])
    }

    private fun setUpdateMocks(mockValues: List<IntList>, forceMockValues: List<IntList> = emptyList()) {
        val paramMatcher = EqMatcher(fullNumberRepeats)
        mockkConstructor(PhoneNumberGenerator::class)
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(false) } returnsMany mockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(true) } returnsMany forceMockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).reset() } answers { callOriginal() }
    }

    private fun checkDigits(vm: ShuffleCircleViewModel, expected: List<Int>) {
        val actual = List(numDigits) { vm.getDigitAtIndex(it) }
        assertEquals(expected, actual)
    }
}
