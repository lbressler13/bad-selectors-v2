package xyz.lbres.badselectorsv2.date.nestedcircles

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.kotlinutils.list.IntList
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NestedCirclesViewModelTest {
    private val mockDate = LocalDate.of(2025, 1, 1)
    private val numYears = 60
    private val startYear = mockDate.year - numYears + 1
    private val maxChanges = mockDate.year / numYears

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
        val vm = NestedCirclesViewModel()
        checkDate(vm)

        assertEquals(0, vm.minYear)
        assertEquals(numYears, vm.numYears)
        assertEquals(mockDate.year, vm.maxYear)
        assertEquals(startYear..mockDate.year, vm.availableYears)

        assertEquals((0 until 31).toList(), vm.enabledDays)
        assertEquals((0 until 12).toList(), vm.enabledMonths)
        assertEquals((0 until numYears).toList(), vm.enabledYears)
    }

    @Test
    fun testSetMonth() {
        var vm = NestedCirclesViewModel()

        fun disabledDays(month: Int): IntList {
            return when (month) {
                1 -> listOf(29, 30)
                3, 5, 8, 10 -> listOf(30)
                else -> emptyList()
            }
        }

        repeat(12) {
            runWithFailMessage("Error testing month $it") {
                vm.month = it
                checkEnabledDate(vm, disabledDays = disabledDays(it))
            }
        }

        // set back to 0
        vm.month = 0 // jan
        checkEnabledDate(vm, disabledDays = disabledDays(0))

        // doesn't change other components
        vm = NestedCirclesViewModel()
        vm.month = 6
        checkDate(vm, month = 6)

        vm.day = 12
        vm.setYearAt(0)
        vm.month = 8
        checkDate(vm, 8, 12, startYear)
    }

    @Test
    fun testSetDay() {
        var vm = NestedCirclesViewModel()

        fun disabledMonths(day: Int): IntList {
            return when (day) {
                29 -> listOf(1) // feb
                30 -> listOf(1, 3, 5, 8, 10) // feb, apr, june, sept, nov
                else -> emptyList()
            }
        }

        repeat(31) {
            runWithFailMessage("Failure testing day $it") {
                vm.day = it
                checkEnabledDate(vm, disabledMonths = disabledMonths(it))
            }
        }

        // set back to 0
        vm.day = 0
        checkEnabledDate(vm, disabledMonths = disabledMonths(0))

        // doesn't change other components
        vm = NestedCirclesViewModel()
        vm.day = 6
        checkDate(vm, day = 6)

        vm.month = 2
        vm.setYearAt(0)
        vm.day = 8
        checkDate(vm, 2, 8, startYear)
    }

    @Test
    fun testSetYear() {
        var vm = NestedCirclesViewModel()

        val checkAllYears = { startYear: Int ->
            repeat(numYears) {
                vm.setYearAt(it)
                assertEquals(startYear + it, vm.year)
            }
        }

        // initial years
        checkAllYears(startYear)

        // decrement a few times
        repeat(6) { vm.decrementAvailableYears() }
        var offset = 6
        checkAllYears(startYear - numYears * offset)

        // increment a few times
        repeat(4) { vm.incrementAvailableYears() }
        offset -= 4
        checkAllYears(startYear - numYears * offset)

        // check out of order
        (0 until numYears).shuffled().forEach {
            vm.setYearAt(it)
            val expectedYear = startYear - numYears * offset + it
            assertEquals(expectedYear, vm.year)
        }

        // repeat value
        vm = NestedCirclesViewModel()
        vm.setYearAt(3)
        assertEquals(startYear + 3, vm.year)
        vm.setYearAt(3)
        assertEquals(startYear + 3, vm.year)
        vm.decrementAvailableYears()
        vm.setYearAt(3)
        assertEquals(startYear - numYears + 3, vm.year)

        // null
        vm.setYearAt(null)
        assertNull(vm.year)

        // doesn't change other components
        vm = NestedCirclesViewModel()
        vm.setYearAt(6)
        checkDate(vm, year = startYear + 6)

        vm.month = 2
        vm.day = 8
        vm.setYearAt(58)
        checkDate(vm, 2, 8, startYear + 58)
    }

    @Test
    fun testIncrementYear() {
        var vm = NestedCirclesViewModel()

        // unabled to increment at start
        vm.incrementAvailableYears()
        checkEnabledYears(vm, startYear)

        // decrement to zero
        repeat(maxChanges) { vm.decrementAvailableYears() }

        // increment
        repeat(maxChanges - 1) {
            vm.incrementAvailableYears()
            checkEnabledYears(vm, (it + 1) * numYears)
        }

        // to start
        vm.incrementAvailableYears()
        checkEnabledYears(vm, startYear)

        // check month/day enabled
        vm = NestedCirclesViewModel()
        vm.day = 29 // 30
        vm.month = 3 // april
        checkEnabledDate(vm, disabledMonths = listOf(1), disabledDays = listOf(30))

        vm.decrementAvailableYears() // decrement because year is already at maximum
        vm.decrementAvailableYears()
        vm.incrementAvailableYears()
        checkEnabledDate(vm, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    @Test
    fun testDecrementYear() {
        var vm = NestedCirclesViewModel()

        // decrement
        repeat(maxChanges - 1) {
            vm.decrementAvailableYears()
            checkEnabledYears(vm, startYear - numYears * (it + 1))
        }

        // to zero
        vm.decrementAvailableYears()
        checkEnabledYears(vm, 0)

        // unable to decrement at zero
        vm.decrementAvailableYears()
        checkEnabledYears(vm, 0)

        // decrement after increment
        vm.incrementAvailableYears()
        vm.incrementAvailableYears()
        vm.decrementAvailableYears()
        checkEnabledYears(vm, numYears)

        // check month/day enabled
        vm = NestedCirclesViewModel()
        vm.day = 29 // 30
        vm.month = 3 // april
        checkEnabledDate(vm, disabledMonths = listOf(1), disabledDays = listOf(30))

        vm.decrementAvailableYears()
        checkEnabledYears(vm, startYear - numYears)
        checkEnabledDate(vm, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    // validate that the correct months, days, and years are enabled
    private fun checkEnabledDate(
        vm: NestedCirclesViewModel,
        disabledMonths: IntList = emptyList(),
        disabledDays: IntList = emptyList(),
        disabledYears: IntList = emptyList(),
    ) {
        val actualMonths = (0 until 12).toList() - vm.enabledMonths
        val actualDays = (0 until 31).toList() - vm.enabledDays
        val actualYears = (0 until numYears).toList() - vm.enabledYears

        assertEquals(disabledMonths.sorted(), actualMonths.sorted())
        assertEquals(disabledDays.sorted(), actualDays.sorted())
        assertEquals(disabledYears.sorted(), actualYears.sorted())
    }

    // check the current date set in the vm
    private fun checkDate(vm: NestedCirclesViewModel, month: Int? = null, day: Int? = null, year: Int? = null) {
        assertEquals(month, vm.month)
        assertEquals(day, vm.day)
        assertEquals(year, vm.year)
    }

    // check the enabled years range
    private fun checkEnabledYears(vm: NestedCirclesViewModel, startYear: Int) {
        val expectedYears = (startYear until startYear + numYears).toList()
        assertEquals(expectedYears.sorted(), vm.availableYears.sorted())
    }
}
