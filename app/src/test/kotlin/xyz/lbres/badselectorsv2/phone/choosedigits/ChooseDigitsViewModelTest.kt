package xyz.lbres.badselectorsv2.phone.choosedigits

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.badselectorsv2.utils.seededShuffled
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

        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returns digitsOrder

            val vm = ChooseDigitsViewModel()
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(10)

            // set all 10 digits
            repeat(10) { digitIndex ->
                val selectIndex = selectOrder[digitIndex]
                selectValue(vm, selectIndex, digitIndex, expectedDigits)
                assertEquals(expectedDigits, vm.digits)
            }

            // update a digit
            selectValue(vm, 0, 0, expectedDigits)
            assertEquals(expectedDigits, vm.digits)

            // doesn't change repeat value
            vm.setCurrentDigit(digitsOrder[0])
            assertEquals(expectedDigits, vm.digits)

            // errors
            selectValue(vm, -1, 7)
            selectValue(vm, 10, 7)
            selectValue(vm, 5, -1)
            selectValue(vm, 5, 10)
        }
    }

    @Test
    fun testResetData() {
        val newOrder = listOf(1, 3, 9, 2, 4, 7, 0, 5, 6, 8)
        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returnsMany listOf(digitsOrder, newOrder)
            val vm = ChooseDigitsViewModel()
            repeat(10) {
                selectValue(vm, it, it)
            }

            vm.resetData()
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(10)
            repeat(10) {
                vm.currentIndex = it
                vm.setCurrentDigit(it)
                expectedDigits[it] = newOrder[it]
                assertEquals(expectedDigits, vm.digits)
            }
        }
    }

    private fun selectValue(vm: ChooseDigitsViewModel, index: Int, value: Int, expected: MutableList<Int?>? = null) {
        vm.currentIndex = index
        vm.setCurrentDigit(value)
        if (expected != null) {
            expected[index] = digitsOrder[value]
        }
    }
}
