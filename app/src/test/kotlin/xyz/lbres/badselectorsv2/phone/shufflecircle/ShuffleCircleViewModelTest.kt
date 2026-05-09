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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ShuffleCircleViewModelTest {
    private val shuffledDigits = listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9)

    @BeforeTest
    fun setupTest() {
        mockkConstructor(PhoneNumberGenerator::class)
    }

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
    fun testGetAndSetDigit() {
        val paramMatcher = EqMatcher(1..3)
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(false) } returns shuffledDigits
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(true) } returns shuffledDigits
        val vm = ShuffleCircleViewModel()

        repeat(numDigits) { assertNull(vm.getDigitAt(it)) }

        digitsRange.forEach { index ->
            val digit = shuffledDigits[index]
            vm.setCurrentDigit(digit)

            repeat(numDigits) {
                if (it <= index) {
                    assertEquals(shuffledDigits[it], vm.getDigitAt(it))
                } else {
                    assertNull(vm.getDigitAt(it))
                }
            }
            vm.incrementCurrentIndex()
        }
    }

    @Test
    fun testGetAtIndex() {
        setUpdateMocks(listOf(shuffledDigits))
        val vm = ShuffleCircleViewModel()
        repeat(10) {
            val result = vm.getDigitAtIndex(it)
            assertEquals(shuffledDigits[it], result)
            assertEquals(shuffledDigits[it], vm.currentDigit)
        }

        var result = vm.getDigitAtIndex(7)
        assertEquals(shuffledDigits[7], result)
        result = vm.getDigitAtIndex(7)
        assertEquals(shuffledDigits[7], result)
    }

    @Test
    fun testGetAtIndexOob() {
        val vm = ShuffleCircleViewModel()
        assertFailsWith<IndexOutOfBoundsException> { vm.getDigitAtIndex(10) }
        assertFailsWith<IndexOutOfBoundsException> { vm.getDigitAtIndex(-1) }
    }

    @Test
    fun testIncrementCurrentIndex() {
        val digitsValues = listOf(
            (0..9).toList(),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
            listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
            (0..9).toList().reversed(),
            listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
            listOf(2, 4, 6, 8, 0, 9, 7, 5, 3, 1),
            listOf(0, 3, 6, 9, 2, 4, 8, 6, 5, 1),
            listOf(9, 8, 7, 4, 1, 3, 0, 6, 5, 2),
            listOf(5, 4, 3, 2, 1, 0, 6, 7, 8, 9),
            listOf(0, 5, 1, 6, 2, 4, 9, 8, 3, 7),
            (0..9).toList().reversed(),
            (0..9).toList().reversed(),
            (0..9).toList().reversed(),
        )
        setUpdateMocks(listOf(shuffledDigits), digitsValues)

        val vm = ShuffleCircleViewModel()
        repeat(10) {
            vm.incrementCurrentIndex()
            assertEquals(it + 1, vm.currentIndex)
            checkDigits(vm, digitsValues[it])
        }
    }

    @Test
    fun testGetAtIndexNullable() {
        mockkStatic("xyz.lbres.kotlinutils.random.ext.RandomExtKt")
        mockkStatic(::createRandom, IntRange::seededRandom, IntRange::seededShuffled)

        val nextBoolValues = listOf(true, false, true, true, false, false, true)
        every { createRandom() } returns mockk {
            every { nextBoolean(any<Float>()) } returnsMany nextBoolValues
        }

        setUpdateMocks(listOf(shuffledDigits))
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

    @Test
    fun testUpdateDigits() {
        val digitsValues = listOf(
            shuffledDigits,
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
            listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
            listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
        )
        setUpdateMocks(digitsValues)

        val vm = ShuffleCircleViewModel()
        val updateDigitsAndCheck = { idx: Int ->
            vm.updateDigits()
            checkDigits(vm, digitsValues[idx])
        }

        checkDigits(vm, digitsValues[0])
        updateDigitsAndCheck(1)
        updateDigitsAndCheck(2)
        updateDigitsAndCheck(3)
        updateDigitsAndCheck(4)
    }

    @Test
    fun testResetData() {
        val digitsValues = listOf(
            shuffledDigits,
            listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        )
        val forceDigitsValues = listOf((0..9).toList())
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

    private fun setUpdateMocks(
        mockValues: List<IntList>,
        forceMockValues: List<IntList> = emptyList(),
    ) {
        val paramMatcher = EqMatcher(1..3)
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(false) } returnsMany mockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(true) } returnsMany forceMockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).reset() } answers { callOriginal() }
    }

    private fun checkDigits(vm: ShuffleCircleViewModel, expected: List<Int>) {
        val actual = List(numDigits) { vm.getDigitAtIndex(it) }
        assertEquals(expected, actual)
    }
}
