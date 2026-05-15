package xyz.lbres.badselectorsv2.phone.selectcorrect

import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.listOfNulls
import kotlin.collections.listOf
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SelectCorrectViewModelTest {
    private val mockGeneratedValues = listOf(
        listOf(7, 7, 6, 5, 1, 3, 5, 2, 6, 8),
        listOf(4, 2, 1, 9, 8, 8, 6, 4, 6, 5),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(3, 0, 1, 2, 3, 5, 8, 8, 3, 5),
        listOf(6, 9, 8, 7, 3, 1, 6, 5, 0, 0),
    )

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = SelectCorrectViewModel()
        vm.digits.forEach { assertNull(it) }
        vm.generatedNumber.forEach { assertContains(digitsRange, it) }
        assertFalse(vm.completedNumber)
    }

    @Test
    fun testUpdateNumber() {
        setupGeneratorMocks()
        val vm = SelectCorrectViewModel()
        assertEquals(mockGeneratedValues[0], vm.generatedNumber)
        for (i in 1 until mockGeneratedValues.size) {
            assertEquals(mockGeneratedValues[i], vm.updateNumber())
            assertEquals(mockGeneratedValues[i], vm.generatedNumber)
        }
    }

    @Test
    fun testSetDigitAt() {
        val vm = SelectCorrectViewModel()
        val setDigits = arrayOfNulls<Int?>(numDigits)

        val setOrder = listOf(4, 8, 1, 0, 7, 3, 9, 6, 2, 5)
        repeat(numDigits) {
            val generated = vm.generatedNumber
            // validate frozen digits
            for (digit in setOrder.subList(0, it)) {
                assertEquals(setDigits[digit], generated[digit])
            }

            val index = setOrder[it]
            vm.setDigitAt(index)
            setDigits[index] = generated[index]
            assertEquals(setDigits.toList(), vm.digits)
            assertEquals(it == numDigits - 1, vm.completedNumber)
            vm.updateNumber()
        }

        // no change when number is complete
        val generated = vm.generatedNumber
        assertEquals(generated, vm.updateNumber())
        assertTrue(vm.completedNumber)
    }

    @Test
    fun testSetDigitAtSingleSelect() {
        val vm = SelectCorrectViewModel()

        // turn on
        var generated = vm.generatedNumber
        vm.singleSelect = true
        assertEquals(generated, vm.generatedNumber)

        // set while on
        vm.setDigitAt(3)
        assertNotEquals(generated, vm.generatedNumber)
        generated = vm.generatedNumber
        vm.setDigitAt(6)
        assertNotEquals(generated, vm.generatedNumber)
        generated = vm.generatedNumber
        vm.setDigitAt(3)
        assertEquals(generated, vm.generatedNumber)

        // turn back off
        vm.singleSelect = false
        assertEquals(generated, vm.generatedNumber)
        vm.setDigitAt(0)
        vm.setDigitAt(9)
        assertEquals(generated, vm.generatedNumber)
        vm.updateNumber()
        assertNotEquals(generated, vm.generatedNumber)
    }

    @Test
    fun testResetData() {
        setupGeneratorMocks()
        val vm = SelectCorrectViewModel()

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
    }

    // mock generation in number generator
    private fun setupGeneratorMocks(values: List<IntList> = mockGeneratedValues) {
        mockkConstructor(PhoneNumberGenerator::class)
        every { constructedWith<PhoneNumberGenerator>().generateNumber(false) } returnsMany values
        every { constructedWith<PhoneNumberGenerator>().reset() } answers { callOriginal() }
    }
}
