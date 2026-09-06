package xyz.lbres.badselectorsv2.date.randomdots

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.date.utils.DateComponent
import xyz.lbres.testutils.mockLog
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RandomDotsViewModelTest {
    private val mockDate = LocalDate.of(2025, 1, 1)
    private val maxDots = 100

    @BeforeTest
    fun setupTest() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns mockDate
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = RandomDotsViewModel()
        checkInitialState(vm)
    }

    @Test
    fun testGetAndUpdateDotPosition() {
        // combined test for getDotPosition and updateDotPosition
        // TODO
    }

    @Test
    fun testHideDot() {
        val vm = RandomDotsViewModel()

        fun hideAndRemove(expected: MutableSet<Int>, index: Int) {
            vm.hideDot(index)
            expected.remove(index)
            assertEquals(expected, vm.visibleIndices)
        }

        // valid
        var expected = setTo(12).toMutableSet()
        listOf(0, 4, 9, 2, 5, 1, 8, 6, 3, 10, 7, 11).forEach { hideAndRemove(expected, it) }

        // duplicate
        vm.resetDots()
        expected = setTo(12).toMutableSet()
        hideAndRemove(expected, 6)
        hideAndRemove(expected, 4)
        hideAndRemove(expected, 4)
        hideAndRemove(expected, 6)

        // out of bounds
        vm.hideDot(-6)
        vm.hideDot(12)
        assertEquals(expected, vm.visibleIndices)

        // other date components
        // day
        selectNumber(vm, 0)
        expected.addAll(setTo(31))
        listOf(9, 24, 29, 16, 2, 36).forEach { hideAndRemove(expected, it) }

        // first half year
        selectNumber(vm, 2)
        expected.clear()
        expected.addAll(setTo(21))
        listOf(9, 16, -7, 20, 2).forEach { hideAndRemove(expected, it) }

        // second half year
        selectNumber(vm, 17)
        expected.addAll(setTo(100))
        listOf(9, 16, 90, 103).forEach { hideAndRemove(expected, it) }
    }

    @Test
    fun testUseSelectedNumber() {
        val vm = RandomDotsViewModel()

        // TODO
        // check visible dots, date, and date component

        // null

        // month

        // day
        // check correct number of days per

        // first half year

        // second half year
    }

    @Test
    fun testResetDots() {
        val vm = RandomDotsViewModel()

        fun hideAndReset(dotsToHide: List<Int>, expectedSize: Int) {
            dotsToHide.forEach { vm.hideDot(it) }
            vm.resetDots()
            assertEquals(setTo(expectedSize), vm.visibleIndices)
        }

        // month
        hideAndReset(listOf(0, 3, 5, 9, 11), 12)

        // day
        selectNumber(vm, 0) // jan
        hideAndReset(listOf(4, 5, 6, 19, 22, 26), 31)

        // first half year
        selectNumber(vm, 19)
        hideAndReset(listOf(2, 4, 9, 14, 18), 21)

        // second half year
        selectNumber(vm, 17)
        hideAndReset(listOf(2, 4, 9, 14, 18), 100)

        // second half w/ limited dots
        vm.resetData()
        selectNumber(vm, 5) // month
        selectNumber(vm, 6) // day
        selectNumber(vm, 20) // first half

        hideAndReset(listOf(2, 4, 9, 14, 18), 26)
    }

    @Test
    fun testResetData() {
        val vm = RandomDotsViewModel()

        // no date
        listOf(10, 3, 7, 8).forEach { vm.hideDot(it) }
        vm.resetData()
        checkInitialState(vm)

        // partial date
        listOf(4, 7).forEach { selectNumber(vm, it) }
        listOf(5, 20).forEach { vm.hideDot(it) }
        vm.resetData()
        checkInitialState(vm)

        // partial year
        listOf(4, 7, 19).forEach { selectNumber(vm, it) }
        listOf(5, 20).forEach { vm.hideDot(it) }
        vm.resetData()
        checkInitialState(vm)

        // complete date
        repeat(4) { selectNumber(vm, 10) }
        vm.resetData()
        checkInitialState(vm)
    }

    private fun setTo(max: Int) = (0 until max).toSet()

    private fun selectNumber(vm: RandomDotsViewModel, number: Int) {
        vm.selectedNumber = number
        vm.useSelectedNumber()
    }

    private fun checkInitialState(vm: RandomDotsViewModel) {
        assertNull(vm.month)
        assertNull(vm.day)
        assertNull(vm.year)
        assertNull(vm.firstHalfYear)
        assertNull(vm.secondHalfYear)

        assertNull(vm.selectedNumber)
        assertEquals(DateComponent.MONTH, vm.dateComponent)
        assertEquals(setTo(12), vm.visibleIndices)
        repeat(maxDots) { assertNull(vm.getDotPosition(it)) }
    }
}
