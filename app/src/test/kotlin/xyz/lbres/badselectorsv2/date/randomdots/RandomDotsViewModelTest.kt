package xyz.lbres.badselectorsv2.date.randomdots

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.date.utils.DateComponent
import xyz.lbres.badselectorsv2.date.utils.monthRange
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
        val viewModel = RandomDotsViewModel()
        assertNull(viewModel.month)
        assertNull(viewModel.day)
        assertNull(viewModel.year)
        assertNull(viewModel.firstHalfYear)
        assertNull(viewModel.secondHalfYear)

        assertNull(viewModel.selectedNumber)
        assertEquals(DateComponent.MONTH, viewModel.dateComponent)
        assertEquals(setTo(12), viewModel.visibleIndices)
        repeat(maxDots) { assertNull(viewModel.getDotPosition(it)) }
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
        val viewModel = RandomDotsViewModel()

        // month
        listOf(0, 3, 5, 9, 11).forEach { viewModel.hideDot(it) }
        viewModel.resetDots()
        assertEquals(setTo(12), viewModel.visibleIndices)

        // day
        viewModel.selectedNumber = 0
        viewModel.useSelectedNumber()

        listOf(4, 5, 6, 19, 22, 26).forEach { viewModel.hideDot(it) }
        viewModel.resetDots()
        assertEquals(setTo(31), viewModel.visibleIndices)

        // first half year
        viewModel.selectedNumber = 19
        viewModel.useSelectedNumber()

        listOf(2, 4, 9, 14, 18).forEach { viewModel.hideDot(it) }
        viewModel.resetDots()
        assertEquals(setTo(21), viewModel.visibleIndices)

        // second half year
        viewModel.selectedNumber = 17
        viewModel.useSelectedNumber()

        listOf(2, 4, 9, 14, 18).forEach { viewModel.hideDot(it) }
        viewModel.resetDots()
        assertEquals(setTo(100), viewModel.visibleIndices)

        // second half w/ limited dots
        viewModel.resetData()
        viewModel.selectedNumber = 5
        viewModel.useSelectedNumber()
        viewModel.selectedNumber = 6
        viewModel.useSelectedNumber()
        viewModel.selectedNumber = 20
        viewModel.useSelectedNumber()

        listOf(2, 4, 9, 14, 18).forEach { viewModel.hideDot(it) }
        viewModel.resetDots()
        assertEquals(setTo(26), viewModel.visibleIndices)
    }

    @Test
    fun testResetData() {
        // TODO
    }

    private fun setTo(max: Int) = (0 until max).toSet()
}
