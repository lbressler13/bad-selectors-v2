package xyz.lbres.badselectorsv2.phone.shuffledigits

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.listOfNulls
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShuffledDigitsViewModelTest {
    private val mockShuffled = listOf(
        listOf(7, 7, 6, 5, 1, 3, 5, 2, 6, 8),
        listOf(4, 2, 1, 9, 8, 8, 6, 4, 6, 5),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(3, 0, 1, 2, 3, 5, 8, 8, 3, 5),
        listOf(6, 9, 8, 7, 3, 1, 6, 5, 0, 0),
        listOf(9, 2, 4, 5, 7, 1, 3, 0, 8, 6),
        listOf(1, 4, 7, 2, 5, 8, 3, 6, 9, 0),
        listOf(9, 1, 8, 2, 7, 3, 6, 4, 0, 5),
        listOf(0, 5, 9, 7, 4, 1, 6, 8, 3, 2),
        listOf(0, 5, 1, 3, 9, 2, 6, 8, 4, 7),
    )

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = ShuffledDigitsViewModel()
        assertEquals(listOfNulls(10), vm.digits)
    }

    @Test
    fun testSetAtIndex() {
        withMockIntRange(mockShuffled) {
            val vm = ShuffledDigitsViewModel()
            val setDigitsOrder = listOf(6, 9, 1, 4, 0, 3, 2, 8, 7, 5)
            val setDigits: Array<Int?> = arrayOfNulls(numDigits)

            repeat(numDigits) { iter -> // TODO change order there too
                setDigitsOrder.forEach { digit ->
                    vm.setDigitAt(digit, iter)
                    setDigits[digit] = mockShuffled[digit][iter]
                    assertEquals(setDigits.toList(), vm.digits)
                }
            }
        }
    }

    @Test
    fun testResetData() {
        val extraShuffled = mockShuffled.reversed()
            .map { it.subList(8, 10) + it.subList(3, 8) + it.subList(0, 3) }

        withMockIntRange(mockShuffled + extraShuffled) {
            // partial number
            val vm = ShuffledDigitsViewModel()
            vm.setDigitAt(2, 5)
            vm.setDigitAt(6, 0)
            vm.resetData()
            assertEquals(listOfNulls(10), vm.digits)

            // full number
            repeat(numDigits) {
                vm.setDigitAt(it, it)
            }
            // validate that orders changed after reset
            val expected = digitsRange.map { extraShuffled[it][it] }
            assertEquals(expected, vm.digits)

            vm.resetData()
            assertEquals(listOfNulls(10), vm.digits)
        }
    }

    private fun withMockIntRange(mockValues: List<IntList>, block: () -> Unit) {
        mockkStatic(IntRange::seededShuffled) {
            with(mockk<IntRange>()) {
                every { IntRange(0, 9).seededShuffled() } returnsMany mockValues
                block()
            }
        }
    }
}
