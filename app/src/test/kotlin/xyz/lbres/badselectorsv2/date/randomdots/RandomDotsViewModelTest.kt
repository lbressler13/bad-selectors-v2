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

        // call useSelectedNumber and perform all checks, except checking current date
        // can be used after selecting null or a valid value
        fun checkPostSelection(newDateComponent: DateComponent?, newMaxValue: Int) {
            vm.useSelectedNumber()
            assertNull(vm.selectedNumber)
            assertEquals(newDateComponent, vm.dateComponent)
            assertEquals((0 until newMaxValue).toSet(), vm.visibleIndices)
        }

        // month
        checkPostSelection(DateComponent.MONTH, 12)
        checkDate(vm)

        vm.selectedNumber = 0
        vm.selectedNumber = 4
        checkPostSelection(DateComponent.DAY, 31)
        checkDate(vm, 5)

        // day
        checkPostSelection(DateComponent.DAY, 31)
        checkDate(vm, 5)

        vm.selectedNumber = 18
        vm.selectedNumber = 12
        checkPostSelection(DateComponent.FIRST_HALF_YEAR, 21)
        checkDate(vm, 5, 13)

        // first half year
        checkPostSelection(DateComponent.FIRST_HALF_YEAR, 21)
        checkDate(vm, 5, 13)

        vm.selectedNumber = 15
        checkPostSelection(DateComponent.SECOND_HALF_YEAR, 100)
        checkDate(vm, 5, 13, 15)

        // second half year
        checkPostSelection(DateComponent.SECOND_HALF_YEAR, 100)
        checkDate(vm, 5, 13, 15)

        vm.selectedNumber = 19
        vm.selectedNumber = 13
        checkPostSelection(null, 0)
        checkDate(vm, 5, 13, 15, 13, 1513)

        // month with different num days
        vm.resetData()
        vm.selectedNumber = 3
        vm.useSelectedNumber()
        checkPostSelection(DateComponent.DAY, 30)

        vm.selectedNumber = 5
        vm.useSelectedNumber()

        // current first half
        vm.selectedNumber = 20
        vm.useSelectedNumber()
        checkPostSelection(DateComponent.SECOND_HALF_YEAR, 26)
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
        checkDate(vm)

        assertNull(vm.selectedNumber)
        assertEquals(DateComponent.MONTH, vm.dateComponent)
        assertEquals(setTo(12), vm.visibleIndices)
        repeat(maxDots) { assertNull(vm.getDotPosition(it)) }
    }

    private fun checkDate(
        vm: RandomDotsViewModel,
        month: Int? = null,
        day: Int? = null,
        firstHalfYear: Int? = null,
        secondHalfYear: Int? = null,
        year: Int? = null,
    ) {
        assertEquals(month, vm.month)
        assertEquals(day, vm.day)
        assertEquals(firstHalfYear, vm.firstHalfYear)
        assertEquals(secondHalfYear, vm.secondHalfYear)
        assertEquals(year, vm.year)
    }
}
