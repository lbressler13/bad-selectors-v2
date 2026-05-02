package xyz.lbres.badselectorsv2.date.nestedcircles

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.kotlinutils.closedrange.intrange.ext.get
import xyz.lbres.kotlinutils.list.IntList
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateEnablerTest {
    private val mockDate = LocalDate.of(2025, 1, 1)
    private val numYears = 60
    private val initialStartYear = mockDate.year - numYears + 1
    val maxChanges = mockDate.year / numYears

    @BeforeTest
    fun setupTest() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns mockDate
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val enabler = DateEnabler()
        assertNull(enabler.day)
        assertNull(enabler.month)
        assertNull(enabler.year)

        assertEquals(0, enabler.minYear)
        assertEquals(numYears, enabler.numYears)
        assertEquals(mockDate.year, enabler.maxYear)
        assertEquals(initialStartYear..mockDate.year, enabler.availableYears)

        assertEquals((0 until 31).toList(), enabler.enabledDays)
        assertEquals((0 until 12).toList(), enabler.enabledMonths)
        assertEquals((0 until numYears).toList(), enabler.enabledYears)
    }

    @Test
    fun testSetMonth() {
        var enabler = DateEnabler()

        fun disabledDays(month: Int): IntList {
            return when (month) {
                1 -> listOf(29, 30)
                3, 5, 8, 10 -> listOf(30)
                else -> emptyList()
            }
        }

        repeat(12) {
            runWithFailMessage("Error testing month $it") {
                enabler.month = it
                checkEnabledDate(enabler, disabledDays = disabledDays(it))
            }
        }

        // set back to 0
        enabler.month = 0 // jan
        checkEnabledDate(enabler, disabledDays = disabledDays(0))

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.day)
        assertNull(enabler.year)
        enabler.month = 6
        assertNull(enabler.day)
        assertNull(enabler.year)

        enabler.day = 12
        enabler.setYearAt(0)
        enabler.month = 8
        assertEquals(12, enabler.day)
        assertEquals(initialStartYear, enabler.year)
    }

    @Test
    fun testSetDay() {
        var enabler = DateEnabler()

        fun disabledMonths(day: Int): IntList {
            return when (day) {
                29 -> listOf(1) // feb
                30 -> listOf(1, 3, 5, 8, 10) // feb, apr, june, sept, nov
                else -> emptyList()
            }
        }

        repeat(31) {
            runWithFailMessage("Failure testing day $it") {
                enabler.day = it
                checkEnabledDate(enabler, disabledMonths = disabledMonths(it))
            }
        }

        // set back to 0
        enabler.day = 0
        checkEnabledDate(enabler, disabledMonths = disabledMonths(0))

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.month)
        assertNull(enabler.year)
        enabler.day = 6
        assertNull(enabler.month)
        assertNull(enabler.year)

        enabler.month = 2
        enabler.setYearAt(0)
        enabler.day = 8
        assertEquals(2, enabler.month)
        assertEquals(initialStartYear, enabler.year)
    }

    @Test
    fun testSetYear() {
        var enabler = DateEnabler()
        val year = mockDate.year
        val maxChanges = year / numYears

        fun decrementAndValidate(startYear: Int, decrementCount: Int): Int {
            var updatedStart = startYear
            repeat(decrementCount) {
                enabler.decrementAvailableYears()
                updatedStart -= numYears
            }
            validateYears(enabler, updatedStart)
            return updatedStart
        }

        fun incrementAndValidate(startYear: Int, incrementCount: Int): Int {
            var updatedStart = startYear
            repeat(incrementCount) {
                enabler.incrementAvailableYears()
                updatedStart += numYears
            }
            validateYears(enabler, updatedStart)
            return updatedStart
        }

        // initial values
        var startYear = year - numYears + 1
        validateYears(enabler, startYear)

        // decremented
        var decrementsToZero = maxChanges

        startYear = decrementAndValidate(startYear, 1)
        decrementsToZero--

        startYear = decrementAndValidate(startYear, 3)
        decrementsToZero -= 3

        // decremented to zero
        repeat(decrementsToZero) { enabler.decrementAvailableYears() }
        repeat(numYears) {
            enabler.setYearAt(it)
            assertEquals(it, enabler.year)
        }

        // incremented
        var incrementsToStart = maxChanges

        startYear = incrementAndValidate(0, 1)
        incrementsToStart--

        startYear = incrementAndValidate(startYear, 2)
        incrementsToStart -= 2

        // incremented to start
        repeat(incrementsToStart) { enabler.incrementAvailableYears() }
        startYear = year - numYears + 1
        validateYears(enabler, startYear)

        // duplicate
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)

        // null
        enabler.setYearAt(null)
        assertNull(enabler.year)

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.month)
        assertNull(enabler.day)
        enabler.setYearAt(6)
        assertNull(enabler.month)
        assertNull(enabler.day)

        enabler.month = 2
        enabler.day = 8
        enabler.setYearAt(58)
        assertEquals(2, enabler.month)
        assertEquals(8, enabler.day)
    }

    @Test
    fun testIncrementYear() {
        var enabler = DateEnabler()

        // unabled to increment at start
        enabler.incrementAvailableYears()
        validateYears(enabler, initialStartYear)

        // decrement to zero
        repeat(maxChanges) { enabler.decrementAvailableYears() }

        // increment
        repeat(maxChanges - 1) {
            enabler.incrementAvailableYears()
            validateYears(enabler, (it + 1) * 60)
        }
        // to start
        enabler.incrementAvailableYears()
        validateYears(enabler, initialStartYear)

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.decrementAvailableYears() // decrement because year already is at maximum
        enabler.decrementAvailableYears()
        enabler.incrementAvailableYears()
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    @Test
    fun testDecrementYear() {
        var enabler = DateEnabler()

        // decrement
        repeat(maxChanges - 1) {
            enabler.decrementAvailableYears()
            val startYear = initialStartYear - 60 * (it + 1)
            validateYears(enabler, startYear)
        }
        // to zero
        enabler.decrementAvailableYears()
        validateYears(enabler, 0)

        // unable to decrement at zero
        enabler.decrementAvailableYears()
        validateYears(enabler, 0)

        // decrement after increment
        enabler.incrementAvailableYears()
        enabler.incrementAvailableYears()
        enabler.decrementAvailableYears()
        validateYears(enabler, numYears)

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.decrementAvailableYears()
        validateYears(enabler, initialStartYear - numYears)
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    // validate that the correct months, days, and years are enabled
    private fun checkEnabledDate(
        enabler: DateEnabler,
        disabledMonths: IntList = emptyList(),
        disabledDays: IntList = emptyList(),
        disabledYears: IntList = emptyList(),
    ) {
        val actualMonths = (0 until 12).toList() - enabler.enabledMonths
        val actualDays = (0 until 31).toList() - enabler.enabledDays
        val actualYears = (0 until numYears).toList() - enabler.enabledYears

        assertEquals(disabledMonths.sorted(), actualMonths.sorted())
        assertEquals(disabledDays.sorted(), actualDays.sorted())
        assertEquals(disabledYears.sorted(), actualYears.sorted())
    }

    private fun validateYears(enabler: DateEnabler, startYear: Int) {
        repeat(numYears) {
            enabler.setYearAt(it)
            assertEquals(startYear + it, enabler.year)
        }
    }
}
