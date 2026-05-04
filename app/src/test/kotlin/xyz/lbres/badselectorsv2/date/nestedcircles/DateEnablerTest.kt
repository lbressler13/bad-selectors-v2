package xyz.lbres.badselectorsv2.date.nestedcircles

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
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
    private val startYear = mockDate.year - numYears + 1
    private val maxChanges = mockDate.year / numYears

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
        checkDate(enabler)

        assertEquals(0, enabler.minYear)
        assertEquals(numYears, enabler.numYears)
        assertEquals(mockDate.year, enabler.maxYear)
        assertEquals(startYear..mockDate.year, enabler.availableYears)

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
        enabler.month = 6
        checkDate(enabler, month = 6)

        enabler.day = 12
        enabler.setYearAt(0)
        enabler.month = 8
        checkDate(enabler, 8, 12, startYear)
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
        enabler.day = 6
        checkDate(enabler, day = 6)

        enabler.month = 2
        enabler.setYearAt(0)
        enabler.day = 8
        checkDate(enabler, 2, 8, startYear)
    }

    @Test
    fun testSetYear() {
        var enabler = DateEnabler()

        val checkAllYears = { startYear: Int ->
            repeat(numYears) {
                enabler.setYearAt(it)
                assertEquals(startYear + it, enabler.year)
            }
        }

        // initial years
        checkAllYears(startYear)

        // decrement a few times
        repeat(6) { enabler.decrementAvailableYears() }
        var offset = 6
        checkAllYears(startYear - numYears * offset)

        // increment a few times
        repeat(4) { enabler.incrementAvailableYears() }
        offset -= 4
        checkAllYears(startYear - numYears * offset)

        // check out of order
        (0 until numYears).shuffled().forEach {
            enabler.setYearAt(it)
            val expectedYear = startYear - numYears * offset + it
            assertEquals(expectedYear, enabler.year)
        }

        // repeat value
        enabler = DateEnabler()
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)
        enabler.decrementAvailableYears()
        enabler.setYearAt(3)
        assertEquals(startYear - numYears + 3, enabler.year)

        // null
        enabler.setYearAt(null)
        assertNull(enabler.year)

        // doesn't change other components
        enabler = DateEnabler()
        enabler.setYearAt(6)
        checkDate(enabler, year = startYear + 6)

        enabler.month = 2
        enabler.day = 8
        enabler.setYearAt(58)
        checkDate(enabler, 2, 8, startYear + 58)
    }

    @Test
    fun testIncrementYear() {
        var enabler = DateEnabler()

        // unabled to increment at start
        enabler.incrementAvailableYears()
        checkEnabledYears(enabler, startYear)

        // decrement to zero
        repeat(maxChanges) { enabler.decrementAvailableYears() }

        // increment
        repeat(maxChanges - 1) {
            enabler.incrementAvailableYears()
            checkEnabledYears(enabler, (it + 1) * numYears)
        }

        // to start
        enabler.incrementAvailableYears()
        checkEnabledYears(enabler, startYear)

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.decrementAvailableYears() // decrement because year is already at maximum
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
            checkEnabledYears(enabler, startYear - numYears * (it + 1))
        }

        // to zero
        enabler.decrementAvailableYears()
        checkEnabledYears(enabler, 0)

        // unable to decrement at zero
        enabler.decrementAvailableYears()
        checkEnabledYears(enabler, 0)

        // decrement after increment
        enabler.incrementAvailableYears()
        enabler.incrementAvailableYears()
        enabler.decrementAvailableYears()
        checkEnabledYears(enabler, numYears)

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.decrementAvailableYears()
        checkEnabledYears(enabler, startYear - numYears)
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

    // check the current date set in the enabler
    private fun checkDate(enabler: DateEnabler, month: Int? = null, day: Int? = null, year: Int? = null) {
        assertEquals(month, enabler.month)
        assertEquals(day, enabler.day)
        assertEquals(year, enabler.year)
    }

    // check the enabled years range
    private fun checkEnabledYears(enabler: DateEnabler, startYear: Int) {
        val expectedYears = (startYear until startYear + numYears).toList()
        assertEquals(expectedYears.sorted(), enabler.availableYears.sorted())
    }
}
