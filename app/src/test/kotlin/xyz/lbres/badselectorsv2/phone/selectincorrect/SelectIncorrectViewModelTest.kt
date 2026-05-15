package xyz.lbres.badselectorsv2.phone.selectincorrect

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.listOfNulls
import kotlin.collections.listOf
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SelectIncorrectViewModelTest {
    private val mockGeneratedValues = listOf(
        listOf(7, 7, 6, 5, 1, 3, 5, 2, 6, 8),
        listOf(4, 2, 1, 9, 8, 8, 6, 4, 6, 5),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(3, 0, 1, 2, 3, 5, 8, 8, 3, 5),
        listOf(6, 9, 8, 7, 3, 1, 6, 5, 0, 0),
    )

    private val mockShuffledValues = listOf(
        listOf(1, 3, 4, 7, 9, 0, 2, 6, 5, 8),
        listOf(9, 8, 7, 6, 1, 2, 3, 4, 5, 0),
        listOf(2, 4, 1, 0, 8, 6, 9, 5, 3, 9),
        listOf(8, 7, 2, 3, 9, 6, 5, 0, 4, 1),
        (0..9).toList(),
    )

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = SelectIncorrectViewModel()
        vm.digits.forEach { assertNull(it) }
        vm.generatedNumber.forEach { assertContains(digitsRange, it) }
        assertFalse(vm.completedNumber)
    }

    @Test
    fun testUpdateNumber() {
        // check that generated number changed
        withMockedRange(mockShuffledValues) {
            setupGeneratorMocks()
            val vm = SelectIncorrectViewModel()
            assertEquals(mockGeneratedValues[0], vm.generatedNumber)
            for (i in 1 until mockGeneratedValues.size) {
                assertEquals(mockGeneratedValues[i], vm.updateNumber())
                assertEquals(mockGeneratedValues[i], vm.generatedNumber)
            }
        }
        unmockkAll() // TODO there's a better way to do this

        // check that the order changed by updating with same generated value
        withMockedRange(mockShuffledValues) {
            setupGeneratorMocks(listOf(digitsRange.toList())) // always returns the same thing
            val vm = SelectIncorrectViewModel()
            assertEquals(digitsRange.toList(), vm.generatedNumber)
            digitsRange.forEach { vm.setDigitAt(it) }
            assertEquals(mockShuffledValues[0], vm.digits)
            vm.updateNumber()
            digitsRange.forEach { vm.setDigitAt(it) }
            assertEquals(mockShuffledValues[1], vm.digits)
        }
    }

    @Test
    fun testSetDigitAt() {
        withMockedRange(mockShuffledValues) {
            setupGeneratorMocks()
            val vm = SelectIncorrectViewModel()
            val expectedValues = arrayOfNulls<Int?>(numDigits)
            assertEquals(expectedValues.toList(), vm.digits)

            val setDigit = { digit: Int, mockIndex: Int ->
                vm.setDigitAt(digit)
                val selectedValue = mockGeneratedValues[mockIndex][digit]
                expectedValues[digit] = mockShuffledValues[mockIndex][selectedValue]
                assertEquals(expectedValues.toList(), vm.digits)
            }

            listOf(2, 3, 7).forEach { setDigit(it, 0) }
            vm.updateNumber()
            listOf(1, 6, 9, 5).forEach { setDigit(it, 1) }
            vm.updateNumber()

            // change previously set
            listOf(8, 2, 5).forEach { setDigit(it, 2) }
            vm.updateNumber()
            listOf(0, 4).forEach { setDigit(it, 3) }
            vm.updateNumber()

            // change previously set after all digits selected
            listOf(7, 1).forEach { setDigit(it, 4) }
        }
    }

    @Test
    fun testResetData() {
        withMockedRange(mockShuffledValues) {
            setupGeneratorMocks()
            val vm = SelectIncorrectViewModel()

            // partial number
            repeat(6) { vm.setDigitAt(it) }
            vm.resetData()
            assertEquals(listOfNulls(numDigits), vm.digits)
            assertEquals(mockGeneratedValues[1], vm.generatedNumber)
            assertFalse(vm.completedNumber)

            // complete number
            repeat(6) { vm.setDigitAt(it) }
            vm.updateNumber()
            repeat(4) { vm.setDigitAt(it) }
            vm.resetData()
            assertEquals(listOfNulls(numDigits), vm.digits)
            assertEquals(mockGeneratedValues[3], vm.generatedNumber)
            assertFalse(vm.completedNumber)

            // correct digits order
            repeat(numDigits) { vm.setDigitAt(it) }
            val expected = mockGeneratedValues[3].map { mockShuffledValues[3][it] }
            assertEquals(expected, vm.digits)
        }
    }

    // mock generation in number generator
    private fun setupGeneratorMocks(values: List<IntList> = mockGeneratedValues) {
        mockkConstructor(PhoneNumberGenerator::class)
        every { constructedWith<PhoneNumberGenerator>().generateNumber(false) } returnsMany values
        every { constructedWith<PhoneNumberGenerator>().reset() } answers { callOriginal() }
    }

    private fun withMockedRange(values: List<IntList>, block: () -> Unit) {
        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returnsMany values
            block()
        }
    }
}
