package xyz.lbres.badselectorsv2.phone.typedigits

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
import kotlin.test.assertNull

class TypeDigitsViewModelTest {
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
    fun testSelectValue() {
        val selectOrder = listOf(7, 4, 1, 9, 0, 2, 3, 6, 5, 8)

        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returns digitsOrder

            val vm = TypeDigitsViewModel()
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(10)

            // set all 10 digits
            repeat(10) { digitIndex ->
                val selectIndex = selectOrder[digitIndex]
                val result = vm.selectValue(selectIndex, digitIndex)
                expectedDigits[selectIndex] = digitsOrder[digitIndex]
                assertEquals(expectedDigits[selectIndex], result)
                assertEquals(expectedDigits, vm.digits)
            }

            // update a digit
            var result = vm.selectValue(0, 0)
            expectedDigits[0] = digitsOrder[0]
            assertEquals(expectedDigits[0], result)
            assertEquals(expectedDigits, vm.digits)

            // doesn't change repeat value
            result = vm.selectValue(0, digitsOrder[0])
            assertEquals(expectedDigits[0], result)
            assertEquals(expectedDigits, vm.digits)

            // errors
            assertNull(vm.selectValue(-1, 7))
            assertNull(vm.selectValue(10, 7))
            assertNull(vm.selectValue(5, -1))
            assertNull(vm.selectValue(5, 10))
        }
    }

    @Test
    fun testResetData() {
        val newOrder = listOf(1, 3, 9, 2, 4, 7, 0, 5, 6, 8)
        mockkStatic(IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(0, 9).seededShuffled() } returnsMany listOf(digitsOrder, newOrder)
            val vm = TypeDigitsViewModel()
            repeat(10) {
                vm.selectValue(it, it)
            }

            vm.resetData()
            val expectedDigits: MutableList<Int?> = mutableListOfNulls(10)
            repeat(10) {
                val result = vm.selectValue(it, it)
                expectedDigits[it] = newOrder[it]
                assertEquals(expectedDigits[it], result)
                assertEquals(expectedDigits, vm.digits)
            }
        }
    }
}
