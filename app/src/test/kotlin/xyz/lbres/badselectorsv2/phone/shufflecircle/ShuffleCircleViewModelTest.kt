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
import xyz.lbres.kotlinutils.list.listOfNulls
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
        assertEquals(-1, vm.generatedDigit)
        digitsRange.forEach { vm.getGeneratedAtIndex(it) in digitsRange }
    }

    @Test
    fun testGetAndSetDigit() {
        setupGeneratorMocks(listOf(shuffledDigits), listOf(shuffledDigits))
        val vm = ShuffleCircleViewModel()

        assertEquals(listOfNulls<Int?>(numDigits), vm.digits)

        digitsRange.forEach { index ->
            val digit = shuffledDigits[index]
            vm.setCurrentDigit(digit)

            repeat(numDigits) {
                if (it <= index) {
                    assertEquals(shuffledDigits[it], vm.digits[it])
                } else {
                    assertNull(vm.digits[it])
                }
            }
            vm.incrementCurrentIndex()
        }
    }

    @Test
    fun testGetGeneratedAtIndex() {
        setupGeneratorMocks(listOf(shuffledDigits))
        val vm = ShuffleCircleViewModel()
        repeat(10) {
            val result = vm.getGeneratedAtIndex(it)
            assertEquals(shuffledDigits[it], result)
            assertEquals(shuffledDigits[it], vm.generatedDigit)
        }

        var result = vm.getGeneratedAtIndex(7)
        assertEquals(shuffledDigits[7], result)
        result = vm.getGeneratedAtIndex(7)
        assertEquals(shuffledDigits[7], result)
    }

    @Test
    fun testGetGeneratedAtIndexOob() {
        val vm = ShuffleCircleViewModel()
        assertFailsWith<IndexOutOfBoundsException> { vm.getGeneratedAtIndex(10) }
        assertFailsWith<IndexOutOfBoundsException> { vm.getGeneratedAtIndex(-1) }
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
        )
        setupGeneratorMocks(listOf(shuffledDigits), digitsValues)

        val vm = ShuffleCircleViewModel()
        repeat(10) {
            vm.incrementCurrentIndex()
            assertEquals(it + 1, vm.currentIndex)
            checkDigits(vm, digitsValues[it])
        }
    }

    @Test
    fun testGetGeneratedAtIndexRussianRoulette() {
        mockkStatic("xyz.lbres.kotlinutils.random.ext.RandomExtKt")
        mockkStatic(::createRandom, IntRange::seededRandom, IntRange::seededShuffled)

        val nextBoolValues = listOf(true, true, true, false, true, true, false, false, true)
        every { createRandom() } returns mockk {
            every { nextBoolean(any<Float>()) } returnsMany nextBoolValues
        }

        setupGeneratorMocks(listOf(shuffledDigits), listOf(shuffledDigits))
        val vm = ShuffleCircleViewModel()
        vm.russianRoulette = true

        // first index, true is ignored
        var result = vm.getGeneratedAtIndex(4)
        assertEquals(shuffledDigits[4], result)
        result = vm.getGeneratedAtIndex(1)
        assertEquals(shuffledDigits[1], result)

        vm.incrementCurrentIndex()

        // initial, true is ignored
        result = vm.getGeneratedAtIndex(4)
        assertEquals(shuffledDigits[4], result)

        // non-null digit, false
        result = vm.getGeneratedAtIndex(6)
        assertEquals(shuffledDigits[6], result)

        // null digit, true
        result = vm.getGeneratedAtIndex(6)
        assertNull(result)

        // previous digit is null, true is ignored
        result = vm.getGeneratedAtIndex(1)
        assertEquals(shuffledDigits[1], result)

        // new digit, false
        result = vm.getGeneratedAtIndex(7)
        assertEquals(shuffledDigits[7], result)

        // new digit, false
        result = vm.getGeneratedAtIndex(0)
        assertEquals(shuffledDigits[0], result)

        // null digit, true
        result = vm.getGeneratedAtIndex(2)
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
        setupGeneratorMocks(digitsValues)

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
        setupGeneratorMocks(digitsValues, forceDigitsValues)

        val vm = ShuffleCircleViewModel()
        checkDigits(vm, digitsValues[0])

        // change digits and current index
        vm.setCurrentDigit(5)
        vm.incrementCurrentIndex()
        vm.setCurrentDigit(2)
        vm.getGeneratedAtIndex(0)

        // check state before reset
        assertEquals(5, vm.digits[0])
        assertEquals(2, vm.digits[1])
        assertEquals(1, vm.currentIndex)
        assertEquals(0, vm.generatedDigit)

        // reset
        vm.resetData()
        assertEquals(0, vm.currentIndex)
        assertEquals(vm.generatedDigit, -1)
        checkDigits(vm, digitsValues[1])
    }

    // mock generation in number generator
    private fun setupGeneratorMocks(mockValues: List<IntList>, forceMockValues: List<IntList> = emptyList()) {
        val paramMatcher = EqMatcher(1..3)
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(false) } returnsMany mockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).generateNumber(true) } returnsMany forceMockValues
        every { constructedWith<PhoneNumberGenerator>(paramMatcher).reset() } answers { callOriginal() }
    }

    private fun checkDigits(vm: ShuffleCircleViewModel, expected: List<Int>) {
        val actual = List(numDigits) { vm.getGeneratedAtIndex(it) }
        assertEquals(expected, actual)
    }
}
