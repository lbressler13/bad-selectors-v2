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
import xyz.lbres.badselectorsv2.date.checkDate as checkStandardDate

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
        checkInitialState(RandomDotsViewModel())
    }

    @Test
    fun testGetAndUpdateDotPosition() {
        // combined test for getDotPosition and updateDotPosition
        val vm = RandomDotsViewModel()
        val dotPositions: Array<Pair<Int, Int>?> = Array(maxDots) { null }

        // get initial positions
        repeat(maxDots) { assertEquals(dotPositions[it], vm.getDotPosition(it)) }

        // set some positions
        (15..85).forEach {
            vm.updateDotPosition(it, it / 2, it * 2)
            dotPositions[it] = Pair(it / 2, it * 2)
            repeat(maxDots) { assertEquals(dotPositions[it], vm.getDotPosition(it)) }
        }

        // change existing position
        vm.updateDotPosition(19, 99, 1)
        dotPositions[19] = Pair(99, 1)

        // set remaining positions
        val remaining = (0 until 14).toList() + (86 until maxDots).toList()
        remaining.forEach {
            vm.updateDotPosition(it, it / 2, it * 2)
            dotPositions[it] = Pair(it / 2, it * 2)
            repeat(maxDots) { assertEquals(dotPositions[it], vm.getDotPosition(it)) }
        }

        // invalid index
        assertNull(vm.getDotPosition(-1))
        assertNull(vm.getDotPosition(100))
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
        listOf(6, 4, 4, 6).forEach { hideAndRemove(expected, it) }

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
            assertEquals(setTo(newMaxValue), vm.visibleIndices)
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

        // after full date
        repeat(4) { selectNumber(vm, 5) }
        vm.selectedNumber = 6
        vm.useSelectedNumber()
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

        val checkStateWithDotPositions = {
            checkInitialState(vm, checkDotPositions = false)
            repeat(maxDots) {
                val expected = if (it < 15) Pair(it, it) else null
                assertEquals(expected, vm.getDotPosition(it))
            }
        }

        // no date
        listOf(10, 3, 7, 8).forEach { vm.hideDot(it) }
        vm.resetData()
        checkInitialState(vm)

        // partial date
        listOf(4, 7).forEach { selectNumber(vm, it) }
        listOf(5, 20).forEach { vm.hideDot(it) }
        repeat(15) { vm.updateDotPosition(it, it, it) }
        vm.resetData()
        checkStateWithDotPositions()

        // partial year
        listOf(4, 7, 19).forEach { selectNumber(vm, it) }
        listOf(5, 20).forEach { vm.hideDot(it) }
        vm.resetData()
        checkStateWithDotPositions()

        // complete date
        repeat(4) { selectNumber(vm, 10) }
        vm.resetData()
        checkStateWithDotPositions()
    }

    // create a set containing values between 0 and the provided max
    private fun setTo(max: Int) = (0 until max).toSet()

    // set selectedNumber and call useSelected
    private fun selectNumber(vm: RandomDotsViewModel, number: Int) {
        vm.selectedNumber = number
        vm.useSelectedNumber()
    }

    // check that the vm matches its initial state. may skip checking dot positions
    private fun checkInitialState(vm: RandomDotsViewModel, checkDotPositions: Boolean = true) {
        checkDate(vm)

        assertNull(vm.selectedNumber)
        assertEquals(DateComponent.MONTH, vm.dateComponent)
        assertEquals(setTo(12), vm.visibleIndices)
        if (checkDotPositions) {
            repeat(maxDots) { assertNull(vm.getDotPosition(it)) }
        }
    }

    // check all components of a date
    private fun checkDate(
        vm: RandomDotsViewModel,
        month: Int? = null,
        day: Int? = null,
        firstHalfYear: Int? = null,
        secondHalfYear: Int? = null,
        year: Int? = null,
    ) {
        checkStandardDate(vm, month, day, year)
        assertEquals(firstHalfYear, vm.firstHalfYear)
        assertEquals(secondHalfYear, vm.secondHalfYear)
    }
}
