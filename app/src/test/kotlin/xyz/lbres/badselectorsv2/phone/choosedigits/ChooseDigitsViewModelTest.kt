package xyz.lbres.badselectorsv2.phone.choosedigits

import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.phone.withMockedPhoneRange
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.kotlinutils.collection.list.mutableListOfNulls
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ChooseDigitsViewModelTest {
    private val digitsOrder = listOf(6, 3, 0, 1, 9, 4, 2, 8, 7, 5)

    @BeforeTest
    fun setupTest() {
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testSetCurrentDigit() {
        val selectOrder = listOf(7, 4, 1, 9, 0, 2, 3, 6, 5, 8)

        withMockedPhoneRange(shuffledMocks = listOf(digitsOrder)) {
            val vm = ChooseDigitsViewModel()
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(numDigits)

            // set all 10 digits
            repeat(numDigits) { digitIndex ->
                runWithFailMessage("Testing digit index $digitIndex") {
                    val selectIndex = selectOrder[digitIndex]
                    selectValue(vm, selectIndex, digitIndex, expectedDigits)
                    assertEquals(expectedDigits, vm.digits)
                }
            }

            // update a digit
            selectValue(vm, 0, 0, expectedDigits)
            assertEquals(expectedDigits, vm.digits)

            // doesn't change repeat value
            vm.setCurrentDigit(digitsOrder[0])
            assertEquals(expectedDigits, vm.digits)

            // errors
            val fixedDigits = expectedDigits.toList()
            selectValue(vm, -1, 7)
            assertEquals(fixedDigits, vm.digits)
            selectValue(vm, 10, 7)
            assertEquals(fixedDigits, vm.digits)
            selectValue(vm, 5, -1)
            assertEquals(fixedDigits, vm.digits)
            selectValue(vm, 5, 10)
            assertEquals(fixedDigits, vm.digits)
        }
    }

    @Test
    fun testResetData() {
        val newOrder = listOf(1, 3, 9, 2, 4, 7, 0, 5, 6, 8)
        withMockedPhoneRange(shuffledMocks = listOf(digitsOrder, newOrder)) {
            val vm = ChooseDigitsViewModel()
            repeat(numDigits) {
                selectValue(vm, it, it)
            }

            vm.resetData()

            assertEquals(-1, vm.currentIndex)
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(numDigits)
            repeat(numDigits) {
                selectValue(vm, it, it, expectedDigits, newOrder)
                assertEquals(expectedDigits, vm.digits)
            }
        }
    }

    // update current index, set value, and update expected values
    private fun selectValue(
        vm: ChooseDigitsViewModel,
        index: Int,
        value: Int,
        expected: MutableList<Int?>? = null,
        order: List<Int> = digitsOrder,
    ) {
        vm.currentIndex = index
        vm.setCurrentDigit(value)
        if (expected != null) {
            expected[index] = order[value]
        }
    }
}
