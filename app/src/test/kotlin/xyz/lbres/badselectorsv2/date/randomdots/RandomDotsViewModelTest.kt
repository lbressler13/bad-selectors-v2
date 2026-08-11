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

    @Test
    fun testGetDotPosition() {
        // TODO
    }

    @Test
    fun testShowDot() {
        // TODO
    }

    @Test
    fun testHideDot() {
        // TODO
    }

    @Test
    fun testUpdateDotPosition() {
        // TODO
    }

    @Test
    fun testUseSelectedNumber() {
        // TODO
    }

    @Test
    fun testFirstHalfYear() {
        // TODO
    }

    @Test
    fun testSecondHalfYear() {
        // TODO
    }

    @Test
    fun testResetDots() {
        val vm = RandomDotsViewModel()

        // month
        listOf(0, 3, 5, 9, 11).forEach { vm.hideDot(it) }
        vm.resetDots()
        assertEquals(setTo(12), vm.visibleIndices)

        // day
        selectNumber(vm, 0) // jan
        listOf(4, 5, 6, 19, 22, 26).forEach { vm.hideDot(it) }
        vm.resetDots()
        assertEquals(setTo(31), vm.visibleIndices)

        // first half year
        selectNumber(vm, 19)
        listOf(2, 4, 9, 14, 18).forEach { vm.hideDot(it) }
        vm.resetDots()
        assertEquals(setTo(21), vm.visibleIndices)

        // second half year
        selectNumber(vm, 17)
        listOf(2, 4, 9, 14, 18).forEach { vm.hideDot(it) }
        vm.resetDots()
        assertEquals(setTo(100), vm.visibleIndices)

        // second half w/ limited dots
        vm.resetData()
        selectNumber(vm, 5) // month
        selectNumber(vm, 6) // day
        selectNumber(vm, 20) // first half

        listOf(2, 4, 9, 14, 18).forEach { vm.hideDot(it) }
        vm.resetDots()
        assertEquals(setTo(26), vm.visibleIndices)
    }

    @Test
    fun testResetData() {
        // TODO
    }

    private fun setTo(max: Int) = (0 until max).toSet()

    private fun selectNumber(vm: RandomDotsViewModel, number: Int) {
        vm.selectedNumber = number
        vm.useSelectedNumber()
    }
}
